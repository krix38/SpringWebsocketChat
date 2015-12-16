package pl.krix.chat.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pl.krix.chat.domain.ChatUser;

import java.util.ArrayList;

/**
 * Created by krix on 12.11.15.
 */
@Repository
public interface ChatUserDao extends CrudRepository<ChatUser, String> {
    ChatUser findOne(String login);
    ArrayList<ChatUser> findAll();
}
