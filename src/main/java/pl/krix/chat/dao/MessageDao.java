package pl.krix.chat.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.domain.Message;

import java.util.ArrayList;

/**
 * Created by krix on 12.11.15.
 */

@Repository
public interface MessageDao extends CrudRepository<Message, Long> {
    Page<Message> findByIdGreaterThan(Long id, Pageable pageable);
    Page<Message> findAll(Pageable pageable);
    ArrayList<Message> findByAuthor(ChatUser author);
}
