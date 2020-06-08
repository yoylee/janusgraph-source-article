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

import org.apache.tinkerpop.gremlin.structure.T;
import org.janusgraph.core.JanusGraphVertex;
import org.janusgraph.mytest.base.BaseTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        godProperties.add("liyangyang");

        godProperties.add("age");
        godProperties.add(18);
        JanusGraphVertex godVertex = graph.addVertex(godProperties.toArray());
        Assert.assertNotNull(godVertex);
    }
}
