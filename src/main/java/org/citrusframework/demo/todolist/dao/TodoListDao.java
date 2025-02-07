/*
 * Copyright 2006-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.demo.todolist.dao;

import java.util.Set;

import org.citrusframework.demo.todolist.model.TodoEntry;

/**
 * @author Christoph Deppisch
 */
public interface TodoListDao {

    void save(TodoEntry entry);

    Set<TodoEntry> list();

    Set<TodoEntry> list(int limit);

    void delete(TodoEntry entry);

    void deleteAll();

    void update(TodoEntry entry);
}
