package com.boardgame.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.boardgame.model.GameUser;
import com.boardgame.repository.GameUserRepository;



/**
 * This class is used by spring security to authenticate and authorize user
 **/
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    
    @Autowired 
    @Lazy  // Lazy initialization to avoid circular dependency
    private GameUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {   
        GameUser curruser = repository.findByUsername(username);
        UserDetails user = new org.springframework.security.core.userdetails.User(
            username, 
            curruser.getPasswordHash(), 
            AuthorityUtils.createAuthorityList(curruser.getRole())
        );
        return user;
    }
}
