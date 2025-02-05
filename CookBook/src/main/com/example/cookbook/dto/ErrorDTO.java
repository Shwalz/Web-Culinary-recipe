package com.example.cookbook.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorDTO {

    private String code;
    private String defaultMessage;

    public ErrorDTO(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}