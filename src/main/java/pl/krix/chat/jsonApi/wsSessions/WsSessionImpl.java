package pl.krix.chat.jsonApi.wsSessions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by krix on 12.11.15.
 */
public class WsSessionImpl implements WsSession {

    private Map<String, String> sessions = new HashMap<String, String>();

    public void addSession(String session, String user) {
        sessions.put(session, user);
    }

    public String getUser(String session) {
        return sessions.get(session);
    }

    public void removeSession(String session) {
        sessions.remove("session");
    }
}
