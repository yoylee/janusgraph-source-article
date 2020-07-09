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

package org.janusgraph.example;

import com.google.common.base.Preconditions;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.ConsistencyModifier;
import org.janusgraph.core.schema.JanusGraphIndex;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Example Graph factory that creates a {@link JanusGraph} based on roman mythology.
 * Used in the documentation examples and tutorials.
 *
 * @author Marko A. Rodriguez (https://markorodriguez.com)
 */
public class GraphOfTheGodsFactory {

    public static final String INDEX_NAME = "search";
    private static final String ERR_NO_INDEXING_BACKEND = 
            "The indexing backend with name \"%s\" is not defined. Specify an existing indexing backend or " +
            "use GraphOfTheGodsFactory.loadWithoutMixedIndex(graph,true) to load without the use of an " +
            "indexing backend.";

    public static JanusGraph create(final String directory) {
        JanusGraphFactory.Builder config = JanusGraphFactory.build();
        config.set("storage.backend", "berkeleyje");
        config.set("storage.directory", directory);
        config.set("index." + INDEX_NAME + ".backend", "elasticsearch");

        JanusGraph graph = config.open();
        GraphOfTheGodsFactory.load(graph);
        return graph;
    }

    public static void loadWithoutMixedIndex(final JanusGraph graph, boolean uniqueNameCompositeIndex) {
        load(graph, null, uniqueNameCompositeIndex);
    }

    public static void load(final JanusGraph graph) {
        load(graph, INDEX_NAME, true);
    }

    private static boolean mixedIndexNullOrExists(StandardJanusGraph graph, String indexName) {
        return indexName == null || graph.getIndexSerializer().containsIndex(indexName);
    }

    public static void load(final JanusGraph graph, String mixedIndexName, boolean uniqueNameCompositeIndex) {
        if (graph instanceof StandardJanusGraph) {
            Preconditions.checkState(mixedIndexNullOrExists((StandardJanusGraph)graph, mixedIndexName), 
                    ERR_NO_INDEXING_BACKEND, mixedIndexName);
        }

        //**主要包含6种节点类型：**
            //* location：位置（sky：天空，sea：海，tartarus：塔耳塔洛斯）
            //* titan：巨人（saturn：罗马神话中的农神）
            //* god：神（jupiter，neptune，pluto）
            //* demigod：半神（hercules）
            //* human：人类（alcmene）
            //* monster：怪物（nemean，hydra，cerberus）

        //**主要包含6中边类型：**
            //* father：父亲
            //* mother：母亲
            //* brother：兄弟
            //* battled：战斗
            //* lives：生活在
            //* pet：宠物

        // Create Schema
        JanusGraphManagement management = graph.openManagement();

        // ===创建name属性； String、唯一CompositeIndex、锁机制保证name的强一致性
        final PropertyKey name = management.makePropertyKey("name").dataType(String.class).make();
        JanusGraphManagement.IndexBuilder nameIndexBuilder = management.buildIndex("name", Vertex.class).addKey(name);
        if (uniqueNameCompositeIndex)
            nameIndexBuilder.unique();
        JanusGraphIndex nameIndex = nameIndexBuilder.buildCompositeIndex();
        // 此处的LOCK，在name索引上添加了LOCK标识，标识这在并发修改相同的name属性时，必须通过锁机制（本地锁+分布式锁）保证并发修改；
        management.setConsistency(nameIndex, ConsistencyModifier.LOCK);

        // ===创建age属性；Integer、mixed index
        final PropertyKey age = management.makePropertyKey("age").dataType(Integer.class).make();
        if (null != mixedIndexName)
            management.buildIndex("vertices", Vertex.class).addKey(age).buildMixedIndex(mixedIndexName);

        // ===创建time属性
        final PropertyKey time = management.makePropertyKey("time").dataType(Integer.class).make();
        // ===创建reason属性
        final PropertyKey reason = management.makePropertyKey("reason").dataType(String.class).make();
        // ===创建place属性
        final PropertyKey place = management.makePropertyKey("place").dataType(Geoshape.class).make();
        // 为reason 和 place属性创建mixed index索引edges
        if (null != mixedIndexName)
            management.buildIndex("edges", Edge.class).addKey(reason).addKey(place).buildMixedIndex(mixedIndexName);

        // 创建边类型：father， many to one
        management.makeEdgeLabel("father").multiplicity(Multiplicity.MANY2ONE).make();
        // 创建边类型：mother， many to one
        management.makeEdgeLabel("mother").multiplicity(Multiplicity.MANY2ONE).make();
        // 创建边类型：battled， 签名密匙为time:争斗次数，
        EdgeLabel battled = management.makeEdgeLabel("battled").signature(time).make();
        // 为battled边创建一个以顶点为中心的 中心索引（vertex-centric index），索引属性time； 双向索引，可以从 神->怪物 也可以 怪物->神
        // 将查询节点对应的 battled 边时，可以使用这个vertex-centric索引，索引属性为 time；
        // vertex-centric index为了解决大节点问题，一个节点存在过多的边！
        management.buildEdgeIndex(battled, "battlesByTime", Direction.BOTH, Order.desc, time);
        // 创建边类型：lives，签名密匙为reason
        management.makeEdgeLabel("lives").signature(reason).make();
        // 创建边类型：pet
        management.makeEdgeLabel("pet").make();
        // 创建边类型：brother
        management.makeEdgeLabel("brother").make();

        // 创建节点label
        management.makeVertexLabel("titan").make();
        management.makeVertexLabel("location").make();
        management.makeVertexLabel("god").make();
        management.makeVertexLabel("demigod").make();
        management.makeVertexLabel("human").make();
        management.makeVertexLabel("monster").make();

        // 提交！！ 上述schema的创建！
        management.commit();

        // ======= 开始插入数据！=======
        JanusGraphTransaction tx = graph.newTransaction();

        // 插入节点
        Vertex saturn = tx.addVertex(T.label, "titan", "name", "saturn", "age", 10000);
        Vertex sky = tx.addVertex(T.label, "location", "name", "sky");
        Vertex sea = tx.addVertex(T.label, "location", "name", "sea");
        Vertex jupiter = tx.addVertex(T.label, "god", "name", "jupiter", "age", 5000);
        Vertex neptune = tx.addVertex(T.label, "god", "name", "neptune", "age", 4500);
        Vertex hercules = tx.addVertex(T.label, "demigod", "name", "hercules", "age", 30);
        Vertex alcmene = tx.addVertex(T.label, "human", "name", "alcmene", "age", 45);
        Vertex pluto = tx.addVertex(T.label, "god", "name", "pluto", "age", 4000);
        Vertex nemean = tx.addVertex(T.label, "monster", "name", "nemean");
        Vertex hydra = tx.addVertex(T.label, "monster", "name", "hydra");
        Vertex cerberus = tx.addVertex(T.label, "monster", "name", "cerberus");
        Vertex tartarus = tx.addVertex(T.label, "location", "name", "tartarus");

        // 插入边数据
        jupiter.addEdge("father", saturn);
        jupiter.addEdge("lives", sky, "reason", "loves fresh breezes");
        jupiter.addEdge("brother", neptune);
        jupiter.addEdge("brother", pluto);

        neptune.addEdge("lives", sea).property("reason", "loves waves");
        neptune.addEdge("brother", jupiter);
        neptune.addEdge("brother", pluto);

        hercules.addEdge("father", jupiter);
        hercules.addEdge("mother", alcmene);
        hercules.addEdge("battled", nemean, "time", 1, "place", Geoshape.point(38.1f, 23.7f));
        hercules.addEdge("battled", hydra, "time", 2, "place", Geoshape.point(37.7f, 23.9f));
        hercules.addEdge("battled", cerberus, "time", 12, "place", Geoshape.point(39f, 22f));

        pluto.addEdge("brother", jupiter);
        pluto.addEdge("brother", neptune);
        pluto.addEdge("lives", tartarus, "reason", "no fear of death");
        pluto.addEdge("pet", cerberus);

        cerberus.addEdge("lives", tartarus);

        // 提交事务，持久化提交的数据到磁盘
        tx.commit();
    }

    /**
     * Calls {@link JanusGraphFactory#open(String)}, passing the JanusGraph configuration file path
     * which must be the sole element in the {@code args} array, then calls
     * {@link #load(org.janusgraph.core.JanusGraph)} on the opened graph,
     * then calls {@link org.janusgraph.core.JanusGraph#close()}
     * and returns.
     * <p>
     * This method may call {@link System#exit(int)} if it encounters an error, such as
     * failure to parse its arguments.  Only use this method when executing main from
     * a command line.  Use one of the other methods on this class ({@link #create(String)}
     * or {@link #load(org.janusgraph.core.JanusGraph)}) when calling from
     * an enclosing application.
     *
     * @param args a singleton array containing a path to a JanusGraph config properties file
     */
    public static void main(String args[]) {
        if (null == args || 1 != args.length) {
            System.err.println("Usage: GraphOfTheGodsFactory <janusgraph-config-file>");
            System.exit(1);
        }

        JanusGraph g = JanusGraphFactory.open(args[0]);
        load(g);
        g.close();
    }
}
