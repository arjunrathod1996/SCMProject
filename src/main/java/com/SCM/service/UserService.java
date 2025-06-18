package com.SCM.service;


import org.springframework.stereotype.Service;

import com.SCM.entities.User;
import java.util.*;

public interface UserService {

    public User saveUser(User user);
    public Optional<User> getUserById(Long id);
    public void deleteUserById(Long id);
    public Optional<User> updateUserById(User user);
    public boolean isUserExist(Long id);
    public boolean isUserExistByEmail(String email);
    public List<User> getAllUser();
    //Optional<User> findByEmail(String email);
    

}
