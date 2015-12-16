package pl.krix.chat.webApp.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.krix.chat.domain.ChatUser;
import pl.krix.chat.service.ChatUserService;
import pl.krix.chat.service.MessageService;
import pl.krix.chat.service.response.ServiceResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by krix on 12.11.15.
 */

@Controller
public class MainWebController implements InitializingBean {

    @Autowired
    ChatUserService chatUserService;

    @Autowired
    MessageService messageService;

    @Autowired
    AuthenticationTrustResolver authenticationTrustResolver;

    @RequestMapping("/")
    public String mainViewHandler(){
        return "mainView";
    }

    @RequestMapping("/login")
    public @ResponseBody String login(HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(authenticationTrustResolver.isAnonymous(auth)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "login failed";
        }else {
            return "login ok";
        }
    }


    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public @ResponseBody String changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            ChatUser user = chatUserService.getUserByName(auth.getName());
            ServiceResponse serviceResponse = chatUserService.changeUserPassword(user, oldPassword, newPassword);
            if(!serviceResponse.getSuccessfull()){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return serviceResponse.getMessage();
            }
        }catch (DataAccessException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return "data access failure";
        }
        return "change successfull";
    }

    @RequestMapping("/getRole")
    public @ResponseBody String getRole(HttpServletResponse response){
     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null){
            try {
                ChatUser user = chatUserService.getUserByName(auth.getName());
                if(user != null){
                    return user.getRole();
                }else {
                    return auth.getName();
                }
            }catch (DataAccessException e){
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return "data access failure";
            }
        }else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "failed to get security context";
        }

    }

    @RequestMapping("/logout")
    public @ResponseBody
    String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
            return "logout successfull";
        }else{
            return "logout failed";
        }
    }

    public void afterPropertiesSet() throws Exception {
        chatUserService.createAdminIfNotExist();
    }
}
