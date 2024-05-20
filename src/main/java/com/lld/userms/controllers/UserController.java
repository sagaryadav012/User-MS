package com.lld.userms.controllers;

import com.lld.userms.dtos.*;
import com.lld.userms.models.Token;
import com.lld.userms.models.User;
import com.lld.userms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupRespDTO> signup(@RequestBody SignupReqDTO signupDTO){
        SignupRespDTO respDTO = new SignupRespDTO();
        try {
            User user = this.userService.signup(signupDTO.getName(), signupDTO.getEmail(), signupDTO.getPassword());
            respDTO.setUser(user);
            respDTO.setResponseStatus(ResponseStatus.SUCCESS);
            return new ResponseEntity<>(respDTO, HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            respDTO.setMessage(e.getMessage());
            respDTO.setResponseStatus(ResponseStatus.FAILED);
            return new ResponseEntity<>(respDTO, HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResDTO> login(@RequestBody LoginReqDTO reqDTO){
        LoginResDTO resDTO = new LoginResDTO();
        try {
            Token token = this.userService.login(reqDTO.getEmail(), reqDTO.getPassword());
            resDTO.setToken(token);
            resDTO.setResponseStatus(ResponseStatus.SUCCESS);
            return new ResponseEntity<>(resDTO, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            resDTO.setMessage(e.getMessage());
            resDTO.setResponseStatus(ResponseStatus.FAILED);
            return new ResponseEntity<>(resDTO, HttpStatusCode.valueOf(400));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResDTO> logout(@RequestBody LogoutReqDTO reqDTO){
        LogoutResDTO logoutResDTO = new LogoutResDTO();
        try {
            this.userService.logout(reqDTO.getValue());
            logoutResDTO.setMessage("Logout Success");
            logoutResDTO.setResponseStatus(ResponseStatus.SUCCESS);
            return new ResponseEntity<>(logoutResDTO, HttpStatus.OK);
        } catch (Exception e) {
            logoutResDTO.setMessage(e.getMessage());
            logoutResDTO.setResponseStatus(ResponseStatus.FAILED);
            return new ResponseEntity<>(logoutResDTO, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/validate_token")
    public ResponseEntity<Token> validate_token(@RequestBody ValidateTokenReqDTO reqDTO){
        try {
            Token token = this.userService.validate_token(reqDTO.getValue());
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
