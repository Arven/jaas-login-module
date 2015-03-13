/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.arven.auth;

/**
 *
 * @author Brian Becker
 */
public interface UserService {
    
    public abstract UserInfo loadUserByUsername(String username);
    
}
