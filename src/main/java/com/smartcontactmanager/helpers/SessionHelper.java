package com.smartcontactmanager.helpers;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;


@Component
public class SessionHelper {
public void removeMessageFromSession(){
    try{
   HttpSession httpSession=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
   httpSession.removeAttribute("Message");
   System.out.println("Message removed from session. ");
    }
    catch(Exception e){
        e.printStackTrace();

    }
}
    
}
