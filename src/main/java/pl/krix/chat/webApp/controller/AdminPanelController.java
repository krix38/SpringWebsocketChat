package pl.krix.chat.webApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.service.ChatUserService;
import pl.krix.chat.service.MessageService;
import pl.krix.chat.service.response.ServiceResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

/**
 * Created by krix on 12.11.15.
 */

@Controller
@RequestMapping("admin/")
public class AdminPanelController {

    @Autowired
    private ChatUserService chatUserService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "addUser", method = RequestMethod.POST)
    public @ResponseBody String addUser(@RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("role") String role, HttpServletResponse response){
        try {
            ServiceResponse serviceResponse = chatUserService.createUser(userName, password, role);
            if(!serviceResponse.getSuccessfull()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return serviceResponse.getMessage();
            }
        }catch (DataAccessException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "data access failure";
        }
        return "user created";
    }

    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    public @ResponseBody String deleteUser(@RequestParam("userName") String userName, HttpServletResponse response){
        try {
            ServiceResponse serviceResponse = messageService.deleteAllMessagesOfUser(chatUserService.getUserByName(userName));
            ServiceResponse serviceResponse2 = chatUserService.deleteUser(userName);
            if(!serviceResponse.getSuccessfull()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return serviceResponse.getMessage();
            }
            if(!serviceResponse2.getSuccessfull()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return serviceResponse2.getMessage();
            }
        }catch (DataAccessException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "data access failure";
        }
        return "user deleted";
    }

    @RequestMapping(value = "modifyUser", method = RequestMethod.POST)
    public @ResponseBody String modifyUser(@RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("role") String role, @RequestParam("enabled") Boolean enabled, HttpServletResponse response){
        try {
            ServiceResponse serviceResponse = chatUserService.modifyUser(userName, password, role, enabled);
            if(!serviceResponse.getSuccessfull()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return serviceResponse.getMessage();
            }
        }catch (DataAccessException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "data access failure";
        }
        return "user modified";


    }

    @RequestMapping("getAllUsers")
    public @ResponseBody
    ArrayList<ChatUser> getAllUsers(HttpServletResponse response){
        ArrayList<ChatUser> chatUsers = null;
        try {
            chatUsers =  (ArrayList<ChatUser>)chatUserService.getAllUsers();
        }catch (DataAccessException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
        return chatUsers;
    }


}
