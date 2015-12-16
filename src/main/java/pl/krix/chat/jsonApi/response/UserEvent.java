package pl.krix.chat.jsonApi.response;

import pl.krix.chat.domain.ChatUser;

/**
 * Created by krix on 12.11.15.
 */
public class UserEvent {

    private ChatUser user;

    private String event;

    public UserEvent(ChatUser user, String event) {
        this.user = user;
        this.event = event;
    }

    public ChatUser getUser() {
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
