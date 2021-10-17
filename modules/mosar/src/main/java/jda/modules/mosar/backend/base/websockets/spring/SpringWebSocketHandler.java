package jda.modules.mosar.backend.base.websockets.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import jda.modules.mosar.backend.base.websockets.Change;
import jda.modules.mosar.backend.base.websockets.WebSocketHandler;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class SpringWebSocketHandler implements WebSocketHandler {
    private static final String prefix = "/topic/";
    private final SimpMessagingTemplate template;

    @Autowired
    public SpringWebSocketHandler(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void handleServerPush(List<String> changedTopics) {
        final Set<String> deduplicatedChangedTopics = new LinkedHashSet<>(changedTopics);
        final String defaultChangeContentFormat = "The resource {%s} has changed, please reload to see the newest changes!";
        final String defaultChangeContent = String.format(defaultChangeContentFormat,
                changedTopics.get(changedTopics.size() - 1));
        for (String changedTopic : deduplicatedChangedTopics) {
            final Change payload = new Change(
                    changedTopic, defaultChangeContent);
            template.convertAndSend(prefix.concat(changedTopic), payload);
            System.out.println("SENT TO TOPIC: " + prefix.concat(changedTopic));
        }
    }

    @Override
    public void handleServerPush(Map<String, Object> changes) {
        for (String changedTopic : changes.keySet()) {
            final Change payload = new Change(
                    changedTopic, changes.get(changedTopic));
            template.convertAndSend(prefix.concat(changedTopic), payload);
        }
    }
}
