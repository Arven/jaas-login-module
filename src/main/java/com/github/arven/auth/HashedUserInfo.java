/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.arven.auth;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author brian.becker
 */
public class HashedUserInfo implements UserInfo {
    
    private final String username;
    private final String hash;
    private final String messageDigest;
    private final Collection<String> roles;
    
    public HashedUserInfo(String messageDigest, String username, String hash, Collection<String> roles) {
        this.messageDigest = messageDigest;
        this.username = username;
        this.hash = hash;
        this.roles = roles;
    }
    
    @Override
    public String getUsername() {
        return this.username;
    }
    
    @Override
    public boolean checkPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(messageDigest);
            return Base64.encodeBase64String(md.digest(password.getBytes("UTF-8"))).equals(this.hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            Logger.getLogger(HashedUserInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public String getPasswordHash() {
        return this.hash;
    }    
    
    @Override
    public Collection<String> getRoles() {
        return this.roles;
    }
    
}
