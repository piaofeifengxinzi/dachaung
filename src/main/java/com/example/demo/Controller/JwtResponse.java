package com.example.demo.Controller;

import java.io.Serializable;
public class JwtResponse implements Serializable {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;
    private final String type;
    public JwtResponse(String jwttoken, String type) {
        this.jwttoken = jwttoken;
        this.type = type;
    }
    public String getToken() {
        return this.jwttoken;
    }
    public String getType() {
        return this.type;
    }
}
