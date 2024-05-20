package com.lld.userms.services;

import com.lld.userms.models.Token;
import com.lld.userms.models.User;

public interface UserService {
    User signup(String name, String email, String password) throws Exception;
    Token login(String email, String password) throws Exception;
    void logout(String value) throws Exception;
    Token validate_token(String value) throws Exception;
}
