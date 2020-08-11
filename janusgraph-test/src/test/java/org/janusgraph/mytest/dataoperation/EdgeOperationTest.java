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

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.mytest.base.BaseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

/**
 * 边操作测试
 * @author liyangyang11
 * @date 2020/6/8
 */
@Ignore
public class EdgeOperationTest extends BaseTest {

    @Test
    public void addEdgeTest(){
        Vertex sourceVertex = graph.traversal().V().has("name", "lyy_07_7").next();
        Assert.assertNotNull(sourceVertex);

        Vertex targetVertex = graph.traversal().V().has("name", "lyy_07_8").next();
        Assert.assertNotNull(targetVertex);

        Edge edge = sourceVertex.addEdge("friendDesc", targetVertex, "reason", "is your friend", "since", new Date());
//        Edge edge2 = sourceVertex.addEdge("father", targetVertex, "edge_no_index", "test_value");
        Assert.assertNotNull(edge);
//        Assert.assertNotNull(edge2);
    }

    @Test
    public void addSortKeyEdgeTest(){
        Vertex sourceVertex = graph.traversal().V().has("name", "liyangyang5").next();
        Assert.assertNotNull(sourceVertex);

        Vertex targetVertex = graph.traversal().V().has("name", "liyangyang6").next();
        Assert.assertNotNull(targetVertex);

        Edge edge = sourceVertex.addEdge("friendDesc", targetVertex, "since", new Date(), "reason", "reason_value1");
        Edge edge2 = sourceVertex.addEdge("friendDesc", targetVertex, "since", new Date(), "reason", "reason_value2");
        Assert.assertNotNull(edge);
//        Assert.assertNotNull(edge2);
    }
}
