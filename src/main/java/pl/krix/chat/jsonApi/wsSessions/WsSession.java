package pl.krix.chat.jsonApi.wsSessions;

/**
 * Created by krix on 12.11.15.
 */
public interface WsSession {
    public void addSession(String session, String user);
    public String getUser(String session);
    public void removeSession(String session);
}
