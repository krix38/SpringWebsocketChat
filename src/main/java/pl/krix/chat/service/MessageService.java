package pl.krix.chat.service;

import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.domain.Message;
import pl.krix.chat.service.response.ServiceResponse;

import java.util.List;

/**
 * Created by krix on 12.11.15.
 */
public interface MessageService {
    public void createMessage(ChatUser author, String message);
    public List<Message> getMessagesSince(Long count);
    public List<Message> getAllMessages();
    public ServiceResponse deleteAllMessagesOfUser(ChatUser user);
}
