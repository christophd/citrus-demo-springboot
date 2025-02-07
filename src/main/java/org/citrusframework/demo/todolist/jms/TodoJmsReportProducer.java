package org.citrusframework.demo.todolist.jms;

import java.util.List;

import org.citrusframework.demo.todolist.model.TodoEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.TextMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
@ConditionalOnProperty(prefix = "todo.jms", value = "enabled")
public class TodoJmsReportProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Send entries to Jms queue.
     */
    public void sendReport(List<TodoEntry> entries) {
        try {
            String jsonPayload = new ObjectMapper().writeValueAsString(entries);
            jmsTemplate.send("jms.todo.report", session -> {
                TextMessage message = session.createTextMessage(jsonPayload);
                message.setStringProperty("_type", TodoEntry.class.getName());
                return message;
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to send Jms todo report", e);
        }
    }
}
