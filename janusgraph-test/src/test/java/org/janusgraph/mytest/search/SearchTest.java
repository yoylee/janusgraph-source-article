package org.janusgraph.mytest.search;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.mytest.base.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 测试查询逻辑
 * @author liyangyang11
 * @date 2020/8/11
 */
@Ignore
public class SearchTest extends BaseTest {
    @Test
    public void searchTest(){
        // 获取name为hercules节点
        GraphTraversal<Vertex, Vertex> herculesVertex = graph.traversal().V().has("name", "hercules");
        // hercules节点战斗（battled）过12次的怪兽（monster）
        GraphTraversal<Vertex, Vertex> monsterVertices = herculesVertex.outE().has(T.label, "battled").dedup().has("time", "12").inV();
        // 获取monsterVertices节点对应的主人
        GraphTraversal<Vertex, Vertex> plutoVertex = monsterVertices.inE("pet").outV().has("age", 4000);
        if (plutoVertex.hasNext()){
            Vertex next = plutoVertex.next();
            // 输出主人的姓名
            System.out.println(next.property("name"));
        }

    }
}
