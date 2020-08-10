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

package org.janusgraph.core.schema;

/**
 * Used to control JanusGraph's consistency behavior on eventually consistent or other non-transactional backend systems.
 * The consistency behavior can be defined for individual {@link JanusGraphSchemaElement}s which then applies to all instances.
 * <p>
 * Consistency modifiers are installed on schema elements via {@link JanusGraphManagement#setConsistency(JanusGraphSchemaElement, ConsistencyModifier)}
 * and can be read using {@link JanusGraphManagement#getConsistency(JanusGraphSchemaElement)}.
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public enum ConsistencyModifier {

    /**
     * Uses the default consistency model guaranteed by the enclosing transaction against the configured
     * storage backend.
     * <p>
     * What this means exactly, depends on the configuration of the storage backend as well as the (optional) configuration
     * of the enclosing transaction.
     * 默认的一致性行为，不使用分布式锁进行控制；
     * 对配置的存储后端使用由封闭事务保证的默认一致性模型，一致性行为主要取决于存储后端的配置以及封闭事务的（可选）配置；
     * 无需显示配置即可使用
     */
    DEFAULT,

    /**
     * Locks will be explicitly acquired to guarantee consistency if the storage backend supports locks.
     * <p>
     * The exact consistency guarantees depend on the configured lock implementation.
     * <p>
     * Note, that locking may be ignored under certain transaction configurations.
     * 在存储后端支持锁的前提下，显示的获取分布式锁以保证一致性！确切的一致性保证取决于所配置的锁实现；
     * 需`management.setConsistency(element, ConsistencyModifier.LOCK);`语句进行配置
     */
    LOCK,


    /**
     * Causes JanusGraph to delete and add a new edge/property instead of overwriting an existing one, hence avoiding potential
     * concurrent write conflicts. This only applies to multi-edges and list-properties.
     * <p>
     * Note, that this potentially impacts how the data should be read.
     * 只适用于`multi-edges`和`list-properties`两种情况下使用；
     * 使JanusGraph修改数据时，采用先删除后添加新的边/属性的方式，而不是覆盖现有的边/属性，从而避免潜在的并发写入冲突；
     * 需`management.setConsistency(element, ConsistencyModifier.FORK);`进行配置
     */
    FORK

}
