/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.arven.auth;

import java.util.Collection;

/**
 *
 * @author brian.becker
 */
public interface UserInfo {
    public abstract String getUsername();
    public abstract boolean checkPassword(String password);
    public abstract Collection<String> getRoles();
}
