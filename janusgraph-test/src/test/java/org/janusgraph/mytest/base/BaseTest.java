package org.janusgraph.mytest.base;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author liyangyang11
 * @date 2020/6/8
 */
public class BaseTest {
    public static JanusGraph graph = null;
    public static GraphTraversalSource g = null;

    @BeforeClass
    public static void initGraph(){
        //graph = JanusGraphFactory
        //    .open("D:\\code\\janusgraph\\janusgraph-dist\\src\\assembly\\cfilter\\conf\\janusgraph-berkeleyje-es.properties");
        graph = JanusGraphFactory
            .open("D:\\code\\janusgraph\\janusgraph-dist\\src\\assembly\\cfilter\\conf\\janusgraph-hbase-es.properties");
        g = graph.traversal();
    }

    @AfterClass
    public static void closeGraph() throws Exception {
        if (g != null){
            // 提交失败，会抛出throw new JanusGraphException("Could not commit transaction due to exception during persistence", e);
            g.tx().commit();
            g.close();
        }
        if (graph != null){
            graph.tx().commit();
            graph.close();
        }
    }
}
