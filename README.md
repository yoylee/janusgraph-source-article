## 源码分析

[![JanusGraph logo](janusgraph.png)](https://janusgraph.org/)

JanusGraph是一个图形数据库引擎，本身专注于紧凑图形序列化，丰富的图形数据建模和高效的查询。

### 提交记录
1. 2020-6-5：完成初始源码编译和单元测试运行
2. 2020-6-8：完善单元测试，单测结束时关闭图实例和事务
3. 2020-7-2：添加es鉴权连接
4. 2020-7-9：官方测试图-诸神之图分析
5. 2020-7-23：JanusGraph**锁机制**源码分析
6. 2020-7-31：JanusGraph**分布式id生成逻辑**源码分析
7. 2020-8-10：JanusGraph**写（导入）数据机制分析**&&锁机制补充分析
8. 2020-8-11：补充写机制源码分析
9. 2020-8-13：查询过程源码简单分析（涉及gremlin源码文件）
10. 待续。。。

### 相关文章

#### 源码分析相关：
[源码图库-一文搞定janusgraph图数据库的本地源码编译（janusgraph source code compile）](https://liyangyang.blog.csdn.net/article/details/106674499)

图解图库JanusGraph系列-一文知晓导入数据流程（待发布）

图解图库JanusGraph系列-简要分析查询读数据流程（待发布）

图解图库JanusGraph系列-一文知晓锁机制（本地锁+分布式锁）（待发布）

图解图库JanusGraph系列-一文知晓分布式id生成策略（待发布）

图解图库JanusGraph系列-一文知晓图库存储分区策略（待发布）

#### 存储结构相关：
[图解图库JanusGraph系列-一文知晓图数据底层存储结构](https://mp.weixin.qq.com/s?__biz=MzAwODkwMDk4OQ==&mid=2247483894&idx=1&sn=0d7b98d8d7abf86bfacf8c86b694651d&chksm=9b6699e4ac1110f2626789d78aaf617dc02b7a9cdad320c5273172a6fa3a21d8f40d63958461&token=1631136587&lang=zh_CN#rd)

#### 其他：
[解惑图数据库！你知道什么是图数据库吗？](https://mp.weixin.qq.com/s?__biz=MzAwODkwMDk4OQ==&mid=2247483830&idx=1&sn=71ad0d9e0d5868ae15011b7744c0fe8f&chksm=9b6699a4ac1110b294487a6987be5392a5093405a7a40f58d4bca697a18d64000db1aeda0a6f&token=1631136587&lang=zh_CN#rd)

图解图库JanusGraph系列-官方测试图:诸神之图分析（待发布）

## 个人博客
**CSDN**：[https://liyangyang.blog.csdn.net/](https://liyangyang.blog.csdn.net/)

**公众号**：[匠心Java]    （文章首发）

[![公众号 logo](1.png)](https://janusgraph.org/)
