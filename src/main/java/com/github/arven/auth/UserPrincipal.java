/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.arven.auth;

import java.security.Principal;

/**
 *
 * @author brian.becker
 */
public class UserPrincipal implements Principal {
    
    private String name;
    
    public UserPrincipal(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
}
