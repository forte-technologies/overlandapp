package com.builtoverlander.trip_planner.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}