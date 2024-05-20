package com.lld.userms.dtos;

import com.lld.userms.models.Role;
import lombok.Data;

import java.util.List;

@Data
public class SignupReqDTO {
    private String name;
    private String email;
    private String password;
}
