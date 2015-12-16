package pl.krix.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.krix.chat.dao.MessageDao;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.domain.Message;
import pl.krix.chat.service.response.ServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krix on 12.11.15.
 */

@Service
public class MessageServiceImpl implements MessageService{

    private static Integer MAXIMUM_MESSAGE_LIMIT = 100;

    @Autowired
    private MessageDao messageDao;

    public void createMessage(ChatUser author, String message) {
        Message newMessage = new Message(author, message);
        messageDao.save(newMessage);
    }

    public List<Message> getMessagesSince(Long count) {
        ArrayList<Message> messages = new ArrayList<Message>();
        messages.addAll(messageDao.findByIdGreaterThan(count, new PageRequest(0, MAXIMUM_MESSAGE_LIMIT, Sort.Direction.ASC, "id")).getContent());
        return messages;
    }

    public List<Message> getAllMessages(){
        ArrayList<Message> messages = new ArrayList<Message>();
        messages.addAll(messageDao.findAll(new PageRequest(0, MAXIMUM_MESSAGE_LIMIT, Sort.Direction.ASC, "id")).getContent());
        return messages;
    }

    public ServiceResponse deleteAllMessagesOfUser(ChatUser user) {
        ArrayList<Message> messages = messageDao.findByAuthor(user);
        if(messages == null){
            return new ServiceResponse(false, "messages not found");
        }
        messageDao.delete(messages);
        return new ServiceResponse(true, "messages deleted");
    }

}
