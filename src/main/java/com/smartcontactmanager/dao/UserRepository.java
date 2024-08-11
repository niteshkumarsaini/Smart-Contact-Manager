package com.smartcontactmanager.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.smartcontactmanager.entities.User;
import java.util.List;


@Component
public interface UserRepository extends CrudRepository<User,Integer>{
    public User findByEmailAndPassword(String email,String password);
    public User findByEmail(String email);
	

}
