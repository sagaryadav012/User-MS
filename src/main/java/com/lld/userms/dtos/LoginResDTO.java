package com.lld.userms.dtos;

import com.lld.userms.models.Token;
import lombok.Data;

@Data
public class LoginResDTO {
    private Token token;
    private String message;
    private ResponseStatus responseStatus;
}
