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

package org.janusgraph.mytest.dataoperation;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.janusgraph.core.JanusGraphVertex;
import org.janusgraph.mytest.base.BaseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 节点操作测试
 * @author liyangyang11
 * @date 2020/6/8
 */
@Ignore
public class VertexOperationTest extends BaseTest {

    @Test
    public void addVertexTest(){
        List<Object> godProperties = new ArrayList<>();
        godProperties.add(T.label);
        godProperties.add("god");

        godProperties.add("name");
        godProperties.add("lyy_07_11");

        godProperties.add("age");
        godProperties.add(18);

        JanusGraphVertex godVertex = graph.addVertex(godProperties.toArray());


//        List<Object> godPropertiesTwo = new ArrayList<>();
//        godPropertiesTwo.add(T.label);
//        godPropertiesTwo.add("god");
//
//        godPropertiesTwo.add("name");
//        godPropertiesTwo.add("lyy_07_11");
//
//        godPropertiesTwo.add("age");
//        godPropertiesTwo.add(18);

//        JanusGraphVertex godVertexTwo = graph.addVertex(godPropertiesTwo.toArray());

        assertNotNull(godVertex);
    }

    @Test
    public void updateVertexTest(){
        GraphTraversal<Vertex, Vertex> has = graph.traversal().V().has("name", "liyangyang26");

        assertTrue(has.hasNext());

        Vertex vertex = has.toList().get(0);
        Iterator<VertexProperty<Object>> properties = vertex.properties("age", "name", "type");
        while (properties.hasNext()){
            System.out.println(properties.next().value());
        }

        assertNotNull(vertex);
        vertex.property("age","12");
        vertex.property("name","liyangyang27");
        vertex.property("type","human7");

        System.out.println();
    }


    @Test
    public void deleteVertexTest(){

    }


    // 用于测试分布式锁
    @Test
    public void addVertexTest2(){
        List<Object> godProperties = new ArrayList<>();
        godProperties.add(T.label);
        godProperties.add("god");

        godProperties.add("name");
        godProperties.add("liyangyang10");

        godProperties.add("age");
        godProperties.add(18);

        JanusGraphVertex godVertex = graph.addVertex(godProperties.toArray());

        assertNotNull(godVertex);
    }
}
