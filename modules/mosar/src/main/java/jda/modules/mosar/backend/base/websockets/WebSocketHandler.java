package jda.modules.mosar.backend.base.websockets;

import java.util.List;
import java.util.Map;

/**
 * Basic abstraction to handle server push using Websockets.
 */
public interface WebSocketHandler {
    /**
     * Handle server push to topics that are changed with a default content.
     * @param changedTopics
     */
    void handleServerPush(List<String> changedTopics);

    /**
     * Handle server push to changed topics with custom payload.
     * @param changes
     */
    void handleServerPush(Map<String, Object> changes);
}
