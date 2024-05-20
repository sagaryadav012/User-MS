package com.lld.userms.dtos;

import com.lld.userms.models.User;
import lombok.Data;

@Data
public class SignupRespDTO {
    private User user;
    private String message;
    private ResponseStatus responseStatus;
}
