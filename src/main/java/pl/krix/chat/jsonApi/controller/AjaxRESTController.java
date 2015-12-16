package pl.krix.chat.jsonApi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import pl.krix.chat.domain.Message;
import pl.krix.chat.jsonApi.wsConnectedUsersMap.ConnectedUsersMap;
import pl.krix.chat.service.MessageService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by krix on 12.11.15.
 */

@RestController
public class AjaxRESTController {

    @Autowired
    MessageService messageService;

    @Autowired
    ConnectedUsersMap connectedUsersMap;

    @RequestMapping("/getMessagesSince/{id}")
    public ArrayList<Message> getMessagesSince(@PathVariable("id") Long id, HttpServletResponse response){
        try {
            return  (ArrayList<Message>) messageService.getMessagesSince(id);
        }catch (DataAccessException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }


    @RequestMapping("/getAllMessages")
    public ArrayList<Message> getAllMessages(HttpServletResponse response){
        try {
            return (ArrayList<Message>) messageService.getAllMessages();
        }catch (DataAccessException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
        @RequestMapping("/getConnectedUsers")
    public Set<String> getConnectedUsers(){
       return (Set<String>) connectedUsersMap.getUsers().keySet();
    }
}
