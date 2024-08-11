package com.smartcontactmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartcontactmanager.dao.UserRepository;
import com.smartcontactmanager.entities.User;

@Service
public class Services {

@Autowired
private UserRepository userRepository;


public User addUser(User user){
    userRepository.save(user);
    return user;

}
public User getUserByEmail(String email){
    return userRepository.findByEmail(email);

}
public User getUserByEmailandPassword(String email,String password){
    return userRepository.findByEmailAndPassword(email, password);
    
}

    
}
