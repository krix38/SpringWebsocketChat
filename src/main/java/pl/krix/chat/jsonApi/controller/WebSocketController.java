package pl.krix.chat.jsonApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import pl.krix.chat.dao.MessageDao;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.domain.Message;
import pl.krix.chat.service.ChatUserService;

import java.security.Principal;

/**
 * Created by krix on 12.11.15.
 */

@Controller
public class WebSocketController {

    @Autowired
    MessageDao messageDao;

    @Autowired
    ChatUserService chatUserService;

    @MessageMapping("/wsMsgApi")
    @SendTo("/msgStream/messages")
    public Message broadcastMessage(Message message, Principal principal) throws Exception {
        try {
            ChatUser chatUser = chatUserService.getUserByName(principal.getName());
            message.setAuthor(chatUser);
            messageDao.save(message);
            return message;
        }catch (DataAccessException e){
            return null;
        }
    }
}
