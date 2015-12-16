package pl.krix.chat.jsonApi.wsConnectedUsersMap;

import org.springframework.beans.factory.annotation.Autowired;
import pl.krix.chat.jsonApi.wsSessions.WsSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by krix on 12.11.15.
 */
public class ConnectedUsersMapImpl implements ConnectedUsersMap {

    private Map<String, Integer> users = new HashMap<String, Integer>();

    @Autowired
    private WsSession wsSession;

    public void addUser(String user, String sessionId) {
        wsSession.addSession(sessionId, user);
        if(users.containsKey(user)){
            users.put(user, users.get(user)+1);
        }else{
            users.put(user, 1);
        }
    }

    public void removeUser(String user) {
        if(users.containsKey(user)){
            Integer count = users.get(user);
            if(count > 1){
                users.put(user, count - 1);
            }else{
                users.remove(user);
            }
        }
    }

    public String removeUserBySession(String sessionId) {
        String user = wsSession.getUser(sessionId);
        removeUser(user);
        wsSession.removeSession(sessionId);
        return user;
    }

    public Map getUsers() {
        return users;
    }
}
