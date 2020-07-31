// Copyright 2017 JanusGraph Authors
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.janusgraph.graphdb.database.idassigner;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.janusgraph.core.JanusGraphException;
import org.janusgraph.diskstorage.BackendException;
import org.janusgraph.diskstorage.IDBlock;

import org.janusgraph.diskstorage.IDAuthority;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Matthias Broecheler (me@matthiasb.com)
 */

public class StandardIDPool implements IDPool {

    private static final Logger log =
            LoggerFactory.getLogger(StandardIDPool.class);


    private static final IDBlock ID_POOL_EXHAUSTION = new IDBlock() {
        @Override
        public long numIds() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getId(long index) {
            throw new UnsupportedOperationException();
        }
    };

    private static final IDBlock UNINITIALIZED_BLOCK = new IDBlock() {
        @Override
        public long numIds() {
            return 0;
        }

        @Override
        public long getId(long index) {
            throw new ArrayIndexOutOfBoundsException(0);
        }
    };

    private static final int RENEW_ID_COUNT = 100;

    private final IDAuthority idAuthority;
    private final long idUpperBound; //exclusive
    private final int partition;
    private final int idNamespace;

    private final Duration renewTimeout;
    private final double renewBufferPercentage;

    private IDBlock currentBlock;
    private long currentIndex;
    private long renewBlockIndex;
//    private long nextID;
//    private long currentMaxID;
//    private long renewBufferID;

    private volatile IDBlock nextBlock;
    private Future<IDBlock> idBlockFuture;
    private IDBlockGetter idBlockGetter;
    private final ThreadPoolExecutor exec;

    private volatile boolean closed;

    private final Queue<Future<?>> closeBlockers;

    public StandardIDPool(IDAuthority idAuthority, int partition, int idNamespace, long idUpperBound, Duration renewTimeout, double renewBufferPercentage) {
        Preconditions.checkArgument(idUpperBound > 0);
        this.idAuthority = idAuthority;
        Preconditions.checkArgument(partition>=0);
        this.partition = partition;
        Preconditions.checkArgument(idNamespace>=0);
        this.idNamespace = idNamespace;
        this.idUpperBound = idUpperBound;
        Preconditions.checkArgument(!renewTimeout.isZero(), "Renew-timeout must be positive");
        this.renewTimeout = renewTimeout;
        Preconditions.checkArgument(renewBufferPercentage>0.0 && renewBufferPercentage<=1.0,"Renew-buffer percentage must be in (0.0,1.0]");
        this.renewBufferPercentage = renewBufferPercentage;

        currentBlock = UNINITIALIZED_BLOCK;
        currentIndex = 0;
        renewBlockIndex = 0;

        nextBlock = null;

        // daemon=true would probably be fine too
        exec = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactoryBuilder()
                        .setDaemon(false)
                        .setNameFormat("JanusGraphID(" + partition + ")("+idNamespace+")[%d]")
                        .build());
        //exec.allowCoreThreadTimeOut(false);
        //exec.prestartCoreThread();
        idBlockFuture = null;

        closeBlockers = new ArrayDeque<>(4);

        closed = false;
    }

    private synchronized void waitForIDBlockGetter() throws InterruptedException {
        Stopwatch sw = Stopwatch.createStarted();
        if (null != idBlockFuture) {
            try {
                nextBlock = idBlockFuture.get(renewTimeout.toMillis(), TimeUnit.MILLISECONDS);
            } catch (ExecutionException e) {
                String msg = String.format("ID block allocation on partition(%d)-namespace(%d) failed with an exception in %s",
                        partition, idNamespace, sw.stop());
                throw new JanusGraphException(msg, e);
            } catch (TimeoutException e) {
                String msg = String.format("ID block allocation on partition(%d)-namespace(%d) timed out in %s",
                        partition, idNamespace, sw.stop());
                // Attempt to cancel the renewer
                idBlockGetter.stopRequested();
                if (idAuthority.supportsInterruption()) {
                    idBlockFuture.cancel(true);
                } else {
                    // Attempt to clean one dead element out of closeBlockers every time we append to it
                    if (!closeBlockers.isEmpty()) {
                        Future<?> f = closeBlockers.peek();
                        if (null != f && f.isDone())
                            closeBlockers.remove();
                    }
                    closeBlockers.add(idBlockFuture);
                }
                throw new JanusGraphException(msg, e);
            } catch (CancellationException e) {
                String msg = String.format("ID block allocation on partition(%d)-namespace(%d) was cancelled after %s",
                        partition, idNamespace, sw.stop());
                throw new JanusGraphException(msg, e);
            } finally {
                idBlockFuture = null;
            }
            // Allow InterruptedException to propagate up the stack
        }
    }

    private synchronized void nextBlock() throws InterruptedException {
        assert currentIndex == currentBlock.numIds();
        Preconditions.checkState(!closed,"ID Pool has been closed for partition(%s)-namespace(%s) - cannot apply for new id block",
                partition,idNamespace);

        // 在分区对应的IDPool第一次使用时，double buffer的nextBlock为空
        if (null == nextBlock && null == idBlockFuture) {
            // 异步启动 获取id block
            startIDBlockGetter();
        }

        // 也是在分区对应的IDPool第一次使用时，因为上述为异步获取，所以在执行到这一步时nextBlock可能还没拿到
        // 所以需要阻塞等待block的获取
        if (null == nextBlock) {
            waitForIDBlockGetter();
        }

        if (nextBlock == ID_POOL_EXHAUSTION)
            throw new IDPoolExhaustedException("Exhausted ID Pool for partition(" + partition+")-namespace("+idNamespace+")");

        // 将当前使用block指向next block
        currentBlock = nextBlock;
        // index清零
        currentIndex = 0;

        log.debug("ID partition({})-namespace({}) acquired block: [{}]", partition, idNamespace, currentBlock);

        assert currentBlock.numIds()>0;

        // nextBlock置空
        nextBlock = null;

        assert RENEW_ID_COUNT>0;
        // renewBlockIndex用于双buffer中，当第一个buffer block使用的百分比，到达配置的百分比则触发other buffer block的获取
        // 值current block 对应的count数量 - （值current block 对应的count数量 * 为renewBufferPercentage配置的剩余空间百分比）
        // 在使用current block的时候，当current index  ==  renewBlockIndex时，触发double buffer next block的异步获取！！！！
        renewBlockIndex = Math.max(0,currentBlock.numIds()-Math.max(RENEW_ID_COUNT, Math.round(currentBlock.numIds()*renewBufferPercentage)));
        assert renewBlockIndex<currentBlock.numIds() && renewBlockIndex>=currentIndex;
    }

    @Override
    public synchronized long nextID() {
        // currentIndex标识当前的index小于current block的最大值
        assert currentIndex <= currentBlock.numIds();

        // 此处涉及两种情况：
        // 1、分区对应的IDPool是第一次被初始化；则currentIndex = 0； currentBlock.numIds() = 0；
        // 2、分区对应的该IDPool不是第一次，但是此次的index正好使用到了current block的最后一个count
        if (currentIndex == currentBlock.numIds()) {
            try {
                // 将current block赋值为next block
                // next block置空 并计算renewBlockIndex
                nextBlock();
            } catch (InterruptedException e) {
                throw new JanusGraphException("Could not renew id block due to interruption", e);
            }
        }

        // 在使用current block的过程中，当current index  ==  renewBlockIndex时，触发double buffer next block的异步获取！！！！
        if (currentIndex == renewBlockIndex) {
            // 异步获取next block
            startIDBlockGetter();
        }

        // 生成最终的count
        long returnId = currentBlock.getId(currentIndex);
        // current index + 1
        currentIndex++;
        if (returnId >= idUpperBound) throw new IDPoolExhaustedException("Reached id upper bound of " + idUpperBound);
        log.trace("partition({})-namespace({}) Returned id: {}", partition, idNamespace, returnId);
        // 返回最终获取的分区维度的全局唯一count
        return returnId;
    }

    @Override
    public synchronized void close() {
        closed=true;
        try {
            waitForIDBlockGetter();
        } catch (InterruptedException e) {
            throw new JanusGraphException("Interrupted while waiting for id renewer thread to finish", e);
        }

        for (Future<?> closeBlocker : closeBlockers) {
            try {
                closeBlocker.get();
            } catch (InterruptedException e) {
                throw new JanusGraphException("Interrupted while waiting for runaway ID renewer task " + closeBlocker, e);
            } catch (ExecutionException e) {
                log.debug("Runaway ID renewer task completed with exception", e);
            }
        }
        exec.shutdownNow();
    }

    private synchronized void startIDBlockGetter() {
        Preconditions.checkArgument(idBlockFuture == null, idBlockFuture);
        if (closed) return; //Don't renew anymore if closed
        //Renew buffer
        log.debug("Starting id block renewal thread upon {}", currentIndex);
        // 创建一个线程对象，包含给定的权限控制类、分区、命名空间、超时时间
        idBlockGetter = new IDBlockGetter(idAuthority, partition, idNamespace, renewTimeout);
        // 提交获取double buffer的线程任务，异步执行
        idBlockFuture = exec.submit(idBlockGetter);
    }

    /**
     * 获取double buffer block的线程类
     */
    private static class IDBlockGetter implements Callable<IDBlock> {

        private final Stopwatch alive;
        private final IDAuthority idAuthority;
        private final int partition;
        private final int idNamespace;
        private final Duration renewTimeout;
        private volatile boolean stopRequested;

        public IDBlockGetter(IDAuthority idAuthority, int partition, int idNamespace, Duration renewTimeout) {
            this.idAuthority = idAuthority;
            this.partition = partition;
            this.idNamespace = idNamespace;
            this.renewTimeout = renewTimeout;
            this.alive = Stopwatch.createStarted();
        }

        private void stopRequested()
        {
            this.stopRequested = true;
        }

        @Override
        public IDBlock call() {
            Stopwatch running = Stopwatch.createStarted();

            try {
                if (stopRequested) {
                    log.debug("Aborting ID block retrieval on partition({})-namespace({}) after " +
                            "graceful shutdown was requested, exec time {}, exec+q time {}",
                            partition, idNamespace, running.stop(), alive.stop());
                    throw new JanusGraphException("ID block retrieval aborted by caller");
                }
                // 此处调用idAuthority 调用HBase进行占用获取Block
                IDBlock idBlock = idAuthority.getIDBlock(partition, idNamespace, renewTimeout);
                log.debug("Retrieved ID block from authority on partition({})-namespace({}), " +
                          "exec time {}, exec+q time {}",
                          partition, idNamespace, running.stop(), alive.stop());
                Preconditions.checkArgument(idBlock!=null && idBlock.numIds()>0);
                return idBlock;
            } catch (BackendException e) {
                throw new JanusGraphException("Could not acquire new ID block from storage", e);
            } catch (IDPoolExhaustedException e) {
                return ID_POOL_EXHAUSTION;
            }
        }
    }
}
