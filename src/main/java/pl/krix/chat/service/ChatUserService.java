package pl.krix.chat.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.service.response.ServiceResponse;

import java.util.List;

/**
 * Created by krix on 12.11.15.
 */


public interface ChatUserService {

    public Boolean isAdminRegistered();

    public void registerAdmin();

    public void createAdminIfNotExist();

    public ServiceResponse createUser(String login, String password, String role);

    public ServiceResponse deleteUser(String login);

    public void updateUser(ChatUser chatUser);

    public ServiceResponse changeUserPassword(ChatUser chatUser, String oldPassword, String newPassword);

    public List<ChatUser> getAllUsers();

    public ServiceResponse modifyUser(String login, String newPassword, String newRole, Boolean enabled);

    public ChatUser getUserByName(String login);
}
