package pl.krix.chat.jsonApi.eventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.domain.Message;
import pl.krix.chat.jsonApi.response.UserEvent;
import pl.krix.chat.jsonApi.wsConnectedUsersMap.ConnectedUsersMap;
import pl.krix.chat.service.ChatUserService;

/**
 * Created by krix on 12.11.15.
 */
public class UserDisconnected implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    private ConnectedUsersMap connectedUsersMap;

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    @Autowired
    private ChatUserService chatUserService;

    public void broadcastUserLoggedOutInfo(String name){
        ChatUser chatUser = chatUserService.getUserByName(name);
        msgTemplate.convertAndSend("/msgStream/userEvents", new UserEvent(chatUser, "disconnected"));
    }

    public void onApplicationEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        String sessionId = sessionDisconnectEvent.getSessionId();
        String name = connectedUsersMap.removeUserBySession(sessionId);
        broadcastUserLoggedOutInfo(name);
    }
}
