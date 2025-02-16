package com.smartcontactmanager.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smartcontactmanager.entities.User;
import java.util.List;
import java.util.ArrayList;
public class CustomUserDetails implements UserDetails{

    
    private User user;
    CustomUserDetails(User user){
        this.user=user;

    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      List simpleAu=new ArrayList<>();
      SimpleGrantedAuthority simpleGrantedAuthority=new SimpleGrantedAuthority(user.getRole());
      return List.of(simpleGrantedAuthority);


    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
       return user.getPassword();

    }

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
       return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;

    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
  return true;

    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
return true;

    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
       return true;

    }
    
}
