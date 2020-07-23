// Copyright 2020 JanusGraph Authors
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

package org.janusgraph.mytest;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.example.GraphOfTheGodsFactory;
import org.janusgraph.mytest.base.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.janusgraph.core.attribute.Geo.geoWithin;

/**
 * 图库-第一个单元测试
 * @author liyangyang11
 * @date 2020/6/5
 */
@Ignore
public class JanusGraphFirstTest extends BaseTest {

    @Test
    public void firstTest() {
        // 使用GraphOfTheGodsFactory加载“The Graph of the Gods”图，这是JanusGraph用于测试自定义的一个图
         GraphOfTheGodsFactory.load(graph); // 第一次运行时添加，之后的运行将该语句注释掉，不需要重复的load

        // 获取图遍历对象实例
        g = graph.traversal();
        // 获取属性"name"为"saturn"的节点
        Vertex saturn = g.V().has("name", "saturn").next();

        // 获取上述节点对应的所有属性的kv
        GraphTraversal<Vertex, Map<Object, Object>> vertexMapGraphTraversal = g.V(saturn).valueMap();
        List<Map<Object, Object>> saturnProMaps = vertexMapGraphTraversal.toList();
        for (Map<Object, Object> proMap : saturnProMaps) {
            proMap.forEach((key,value) -> System.out.println("====="+key + ":" + value));
        }

        // 获取上述节点的father的father的姓名，也就是grandfather的姓名
        GraphTraversal<Vertex, Object> values = g.V(saturn).in("father").in("father").values("name");
        String name = String.valueOf(values.next());
        System.out.println("=====" + "grandfather name:" + name);

        // 获取在(latitude:37.97 and long:23.72)50km内的所有节点
        GraphTraversal<Edge, Edge> place = g.E().has("place", geoWithin(Geoshape.circle(37.97, 23.72, 50)));
        GraphTraversal<Edge, Map<String, Object>> node = place.as("source")
            .inV().as("god2")
            .select("source")
            .outV().as("god1")
            .select("god1", "god2").by("name");
        List<Map<String, Object>> maps = node.toList();
        for (Map<String, Object> map : maps) {
            map.forEach((key,value) -> System.out.println("=====" + key + ":" + value));
        }

    }
}
