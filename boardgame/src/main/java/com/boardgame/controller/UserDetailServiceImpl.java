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
 * This class is used by spring security to authenticate and authorize users.
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    @Lazy  // Lazy initialization to avoid circular dependency
    private GameUserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {   
        GameUser curruser = repository.findByUsername(username);
        
        // Manually add the "ROLE_" prefix to the role stored in the database
        String roleWithPrefix = "ROLE_" + curruser.getRole();  // Add ROLE_ prefix to role
        
        // Create the UserDetails object with the role prefixed with ROLE_
        UserDetails user = new org.springframework.security.core.userdetails.User(
            username, 
            curruser.getPasswordHash(), 
            AuthorityUtils.createAuthorityList(roleWithPrefix)  // Use role with the "ROLE_" prefix
        );
        
        return user;
    }
    
}
