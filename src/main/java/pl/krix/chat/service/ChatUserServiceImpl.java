package pl.krix.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import pl.krix.chat.dao.ChatUserDao;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.service.response.ServiceResponse;
import java.util.List;

/**
 * Created by krix on 12.11.15.
 */

@Service
public class ChatUserServiceImpl implements ChatUserService {

    @Autowired
    private ChatUserDao chatUserDao;

    private static final String ADMIN_DEFAULT_PASSWORD = "password";

    public Boolean isAdminRegistered(){
        ChatUser user = chatUserDao.findOne("admin");
        if( user == null ){
            return false;
        }
        else {
            return true;
        }
    }

    public void registerAdmin(){
        String passwordHash = BCrypt.hashpw(ADMIN_DEFAULT_PASSWORD, BCrypt.gensalt(10));
        ChatUser admin = new ChatUser("admin", passwordHash, "ROLE_ADMIN", Boolean.TRUE);
        chatUserDao.save(admin);
    }

    public void createAdminIfNotExist(){
        if(!isAdminRegistered()){
            registerAdmin();
        }
    }

    public ServiceResponse createUser(String login, String password, String role){
        if(login.length() > 0 && password.length() > 0){
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            ChatUser newUser = new ChatUser(login, passwordHash, role, Boolean.TRUE);
            chatUserDao.save(newUser);
            return new ServiceResponse(true, "user created");
        }else {
            return new ServiceResponse(false, "login and password must be nonepmpty");
        }
    }

    public void updateUser(ChatUser chatUser){
        chatUserDao.save(chatUser);
    }

    public ServiceResponse deleteUser(String login){
        if(login.equals("admin")){
            return new ServiceResponse(false, "cannot delete admin");
        }else {
            chatUserDao.delete(login);
            return new ServiceResponse(true, "user deleted");
        }
    }

    public ServiceResponse changeUserPassword(ChatUser chatUser, String oldPassword, String newPassword){
        if(newPassword.length() > 0) {
            if (BCrypt.checkpw(oldPassword, chatUser.getPasswordHash())) {
                chatUser.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt(10)));
                chatUserDao.save(chatUser);
                return new ServiceResponse(true, "password changed");
            } else {
                return new ServiceResponse(false, "password does not match");
            }
        }else {
            return new ServiceResponse(false, "password cannot be empty");
        }

    }

    public List<ChatUser> getAllUsers() {
        return chatUserDao.findAll();
    }

    public ServiceResponse modifyUser(String login, String newPassword, String newRole, Boolean enabled) {
        ChatUser user = chatUserDao.findOne(login);
        if(user != null){
            if(!login.equals("admin")){
                user.setRole(newRole);
                user.setEnabled(enabled);
            }
            if(newPassword.length() > 0){
                user.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt(10)));
            }
            chatUserDao.save(user);
            return new ServiceResponse(true, "user modified");
        }else {
            return new ServiceResponse(false, "user not found");
        }
    }


    public ChatUser getUserByName(String login) {
        return chatUserDao.findOne(login);
    }
}
