package com.smartcontactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;

@Component
public interface ContactRepository extends CrudRepository<Contact,Integer>{
    public Page<Contact>findAllByUser(User user, Pageable pageable);
    // public List<Contact>findByNameContainingAndUser(String name,User user);
    public List<Contact>findByNameContaining(String infix);


    
    
}
