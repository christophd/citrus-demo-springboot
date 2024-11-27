/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.citrusframework.demo.todolist;

import java.util.stream.Stream;

import org.citrusframework.TestCaseRunner;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.common.TestLoader;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.junit.jupiter.CitrusTestFactory;
import org.citrusframework.junit.jupiter.CitrusTestFactorySupport;
import org.citrusframework.junit.jupiter.spring.CitrusSpringSupport;
import org.citrusframework.message.MessageType;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.citrusframework.actions.CreateVariablesAction.Builder.createVariables;
import static org.citrusframework.dsl.XpathSupport.xpath;
import static org.citrusframework.http.actions.HttpActionBuilder.http;
import static org.citrusframework.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;

@SpringBootTest(classes = TodoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CitrusSpringSupport
@ContextConfiguration(classes = { CitrusEndpointConfig.class })
class TodoRestApiTest {

    @Autowired
    HttpClient todoClient;

    @CitrusResource
    TestCaseRunner t;

    @Test
    void shouldGetTodoList() {
        t.when(http()
                .client(todoClient)
                .send()
                .get("/todolist")
                .message()
                .accept(MediaType.TEXT_HTML_VALUE));

        t.then(http()
                .client(todoClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.XHTML)
                .validate(xpath()
                        .expression("//xh:h1", "TODO list"))
                .body("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n" +
                        "\"org/w3/xhtml/xhtml1-transitional.dtd\">" +
                        "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                        "<head>@ignore@</head>" +
                        "<body>@ignore@</body>" +
                        "</html>"));
    }

    @Test
    void shouldAddTodo() {
        t.given(createVariables()
                .variable("todoName", "citrus:concat('todo_', citrus:randomNumber(4))")
                .variable("todoDescription", "Description: ${todoName}"));

        t.when(http()
                .client(todoClient)
                .send()
                .post("/todolist")
                .message()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body("title=${todoName}&description=${todoDescription}"));

        t.then(http()
                .client(todoClient)
                .receive()
                .response(HttpStatus.OK));
    }

    @Test
    public void shouldManageTodos() {
        t.given(createVariables()
                .variable("todoId", "citrus:randomUUID()")
                .variable("todoName", "citrus:concat('todo_', citrus:randomNumber(4))")
                .variable("todoDescription", "Description: ${todoName}"));

        //Create
        t.when(http().client(todoClient)
                .send()
                .post("/api/todolist")
                .message()
                .type(MessageType.JSON)
                .contentType("application/json")
                .body("{ \"id\": \"${todoId}\", " +
                        "\"title\": \"${todoName}\", " +
                        "\"description\": \"${todoDescription}\", " +
                        "\"done\": false}"));

        t.then(http().client(todoClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.PLAINTEXT)
                .body("${todoId}"));

        //Verify existence
        t.when(http().client(todoClient)
                .send()
                .get("/api/todo/${todoId}")
                .message()
                .accept("application/json"));

        t.then(http().client(todoClient)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .validate(jsonPath()
                        .expression("$.id", "${todoId}")
                        .expression("$.title", "${todoName}")
                        .expression("$.description", "${todoDescription}")
                        .expression("$.done", false)));

        //Delete
        t.when(http().client(todoClient)
                .send()
                .delete("/api/todo/${todoId}")
                .message()
                .accept("application/json"));

        t.then(http().client(todoClient)
                .receive()
                .response(HttpStatus.OK));
    }

    @CitrusTestFactory
    public Stream<DynamicTest> shouldLoadYamlTests() {
        return CitrusTestFactorySupport.factory(TestLoader.YAML).packageScan(TodoRestApiTest.class.getPackageName());
    }

}
