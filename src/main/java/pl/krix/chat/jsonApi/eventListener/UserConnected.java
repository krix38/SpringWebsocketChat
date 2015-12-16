package pl.krix.chat.jsonApi.eventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.domain.Message;
import pl.krix.chat.jsonApi.response.UserEvent;
import pl.krix.chat.jsonApi.wsConnectedUsersMap.ConnectedUsersMap;
import pl.krix.chat.service.ChatUserService;

/**
 * Created by krix on 12.11.15.
 */
public class UserConnected implements ApplicationListener<SessionConnectedEvent> {

    @Autowired
    private ConnectedUsersMap connectedUsersMap;

    @Autowired
    private SimpMessagingTemplate msgTemplate;

    @Autowired
    private ChatUserService chatUserService;

    public void broadcastUserLoggedInInfo(String name){
        ChatUser chatUser = chatUserService.getUserByName(name);
        msgTemplate.convertAndSend("/msgStream/userEvents", new UserEvent(chatUser, "connected"));
    }

    public void onApplicationEvent(SessionConnectedEvent sessionConnectedEvent) {
        String sessionId = (String)sessionConnectedEvent.getMessage().getHeaders().get("simpSessionId");
        String name = sessionConnectedEvent.getUser().getName();
        connectedUsersMap.addUser(name, sessionId);
        broadcastUserLoggedInInfo(name);
    }
}

