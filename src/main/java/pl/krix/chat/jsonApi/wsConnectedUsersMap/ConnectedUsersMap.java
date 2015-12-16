package pl.krix.chat.jsonApi.wsConnectedUsersMap;

import java.util.Map;
import java.util.Set;

/**
 * Created by krix on 12.11.15.
 */
public interface ConnectedUsersMap {
    public void addUser(String user, String sessionId);
    public void removeUser(String user);
    public String removeUserBySession(String sessionId);
    public Map getUsers();
}
