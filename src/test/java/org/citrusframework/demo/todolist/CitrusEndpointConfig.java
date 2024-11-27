package org.citrusframework.demo.todolist;

import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.citrusframework.config.CitrusSpringConfig;
import org.citrusframework.container.AfterSuite;
import org.citrusframework.http.client.HttpClient;
import org.citrusframework.xml.namespace.NamespaceContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.citrusframework.actions.EchoAction.Builder.echo;
import static org.citrusframework.container.SequenceAfterSuite.Builder.afterSuite;
import static org.citrusframework.http.endpoint.builder.HttpEndpoints.http;

@Configuration
@Import(CitrusSpringConfig.class)
public class CitrusEndpointConfig {

    @Bean
    public HttpClient todoClient() {
        return http().client()
                .requestUrl("http://localhost:8080")
                .build();
    }

    @Bean
    public NamespaceContextBuilder namespaceContextBuilder() {
        final NamespaceContextBuilder namespaceContextBuilder = new NamespaceContextBuilder();
        namespaceContextBuilder.setNamespaceMappings(Collections.singletonMap("xh", "http://www.w3.org/1999/xhtml"));
        return namespaceContextBuilder;
    }

    @Bean
    public ObjectMapper mapper() {
        return JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .disable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
                .enable(MapperFeature.BLOCK_UNSAFE_POLYMORPHIC_BASE_TYPES)
                .build()
                .setDefaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_EMPTY, JsonInclude.Include.NON_EMPTY));
    }

    @Bean
    public AfterSuite afterSuiteActions() {
        return afterSuite()
                .actions(
                    echo().message("TEST FINISHED!"))
                .build();
    }

}
