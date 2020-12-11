> 本系列博文会不断更新和修改，大家可以收藏该文章或关注博主，便于以后的查找；
>
> 本次更新时间：2020/12/11日



## 介绍一下

博主从2018年因为业务原因开始接触图数据库，在金融公司做`金融欺诈风控`相关业务；

主要使用图库：**JanusGraph**

了解图库：**Neo4j**

----

## 沟通与交流

现如今图数据库资料较少，一般都是官网的文章和基于官网衍生出来的文章； 图库`JanusGraph`的资料更少，导致开发图数据库相关业务，基本上是`一步一坑`...

为了便于码友们的交流，博主创建了一个`图数据库社区交流微信群`，群里`现有`各大图库的码友，包含但不限于`Janus Graph`、`neo4j`、`TigerGraph`、`OrientDB`、`titan`、`Nebula Graph`、`Huge graph`...

加群请加我下述的微信，我拉你进群~

**博主微信：** `l（小写L）yy1591992570` or `18864803272`    欢迎加我好友相互沟通学习！

**博主公众号：**  `匠心Java`   正在完善中~

![20200814183421838](http://images.coderstudy.vip/二维码.png)

----

## 注意

所有博文都是博主基于日常的实践、官网和源码 总结而来，才疏学浅难免有遗漏错误之处，请大家在浏览博文时`带着‘审视’和‘怀疑’的眼光`！！！

如有`遗漏错误`之处，或者`疑问`之处，可以加我好友交流，也可以直接在对应的博客下留言！ 博主会尽快处理，感谢！！

----

## 图库文章列表

#### 已发布

1. 【原创】[解惑图数据库！你知道什么是图数据库吗？](https://liyangyang.blog.csdn.net/article/details/106384973)
2. 【原创】[源码图库-一文搞定janusgraph图数据库的本地源码编译（janusgraph source code compile）](https://liyangyang.blog.csdn.net/article/details/106674499)
3. 【原创】[图解图库JanusGraph系列-一文知晓“图数据“底层存储结构（JanusGraph data model）](https://liyangyang.blog.csdn.net/article/details/107999814)
4. 【原创】[图解Janusgraph系列-分布式id生成策略分析](https://liyangyang.blog.csdn.net/article/details/108000639)
5. 【原创】[图解Janusgraph系列-查询图数据过程源码分析](https://liyangyang.blog.csdn.net/article/details/108000561)
6. 【翻译】[JanusGraph -- 索引详解（janusgraph index）](https://liyangyang.blog.csdn.net/article/details/98513704)
7. 【翻译】[JanusGraph -- 事务详解（janusgraph transactions）](https://liyangyang.blog.csdn.net/article/details/98656078)
8. 【翻译】[JanusGraph -- 缓存（janusgraph caching）](https://liyangyang.blog.csdn.net/article/details/98656781)
9. 【翻译】[JanusGraph -- 索引参数与全文索引查询（janusgraph Index parameters and full text search）](https://liyangyang.blog.csdn.net/article/details/98657833)
10. 【翻译】[JanusGraph -- 查询谓词和数据类型（janusgraph Search predicates and data types）](https://liyangyang.blog.csdn.net/article/details/98659157)

#### 待发布

1. 【原创】图数据库-聊聊图数据库图数据底层存储几种结构
2. 【原创】图数据库-图库中超级点问题的思考（Super Node）
3. 【原创】图解JanusGraph系列-并发控制锁机制策略分析（Lock）
4. 【原创】图解JanusGraph系列-数据插入流程分析（Data Insert）
5. 【原创】图解JanusGraph系列-JanusGraph中超级点问题解决策略分析:Vertex-centric Indexes与点切割（Super Node）
6. 【原创】图解JanusGraph系列-查询优化（组件优化、gremlin语句优化）（Query Optimization）
7. 【原创】图解JanusGraph系列-一文搞定常用Gremlin语句！
8. 【原创】图解JanusGraph系列-单元测试JanusGraph（Unit Test JanusGraph）
9. 【原创】图解JanusGraph系列-如何更好的设计图Schema？（Design JanusGraph Schema）
10. 【原创】图解JanusGraph系列-关于图数据批量快速导入的一些方案和想法（Bulk Loader）

#### 待整理

1. 【原创】图解JanusGraph系列-索引详解（Index介绍、Reindex、冲突索引选择机制）
2. 【原创】图解JanusGraph系列-分区详解（分区机制、基于业务分区优化）
3. 【原创】图解JanusGraph系列-如何监控JanusGraph？（Monitoring JanusGraph）
4. 【原创】图解JanusGraph系列-缓存详解与应用（JanusGraph Caching）


----

## 源码分析

JanusGraph-0.5.2源码解析GitHub仓库：[https://github.com/YYDreamer/janusgraph](https://github.com/YYDreamer/janusgraph)   欢迎star~

**源码分析提交记录：**

1. 2020-6-5：完成初始源码编译和单元测试运行
2. 2020-7-2：添加es鉴权连接
3. 2020-7-9：官方测试图-诸神之图分析
4. 2020-7-23：JanusGraph**锁机制**源码分析
5. 2020-7-31：JanusGraph**分布式id生成逻辑**源码分析
6. 2020-8-10：JanusGraph**写（导入）数据机制分析**&&锁机制补充分析
7. 2020-8-11：补充写机制源码分析
8. 2020-8-13：查询过程源码简单分析（涉及gremlin源码文件）
9. 待续。。。

最后，共同努力学习，谢谢大家~
