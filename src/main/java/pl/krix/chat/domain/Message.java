package pl.krix.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by krix on 12.11.15.
 */

@Entity
public class Message {

    private static final int MESSAGE_MAX_LENGTH = 4096;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(length=MESSAGE_MAX_LENGTH)
    private String message;

    @ManyToOne
    @JoinColumn(name="author")
    private ChatUser author;

    public Message(){};

    public Message(ChatUser author, String message) {
        this.message = message;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatUser getAuthor() {
        return author;
    }

    public void setAuthor(ChatUser author) {
        this.author = author;
    }
}
