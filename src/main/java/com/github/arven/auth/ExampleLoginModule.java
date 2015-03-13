package com.github.arven.auth;

import java.util.Map;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * Login module that simply matches name and password to perform authentication.
 * If successful, set principal to name and credential to "admin".
 *
 * @author Nicolas Fränkel
 * @since 2 avr. 2009
 */
public class ExampleLoginModule implements LoginModule {
    
    private UserService svc;
 
    /** Callback handler to store between initialization and authentication. */
    private CallbackHandler handler;
 
    /** Subject to store. */
    private Subject subject;
 
    /** Login name. */
    private String login;
 
    /**
     * This implementation always return false.
     *
     * @throws javax.security.auth.login.LoginException
     * @see javax.security.auth.spi.LoginModule#abort()
     */
    @Override
    public boolean abort() throws LoginException {
        return false;
    }
 
    /**
     * This is where, should the entire authentication process succeeds,
     * principal would be set.
     *
     * @throws javax.security.auth.login.LoginException
     * @see javax.security.auth.spi.LoginModule#commit()
     */
    @Override
    public boolean commit() throws LoginException {
        try {
            UserPrincipal user = new UserPrincipal(login);
 
            subject.getPrincipals().add(user);
            
            if(!login.equalsIgnoreCase("anonymous")) {
                for(String s : svc.loadUserByUsername(login).getRoles()) {
                    subject.getPrincipals().add(new RolePrincipal(s));
                }
            } else {
                subject.getPrincipals().add(new RolePrincipal("anonymous"));
            }
 
            return true;
 
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }
 
    /**
     * This implementation ignores both state and options.
     *
     * @param aSubject
     * @param aCallbackHandler
     * @param aSharedState
     * @param aOptions
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject,
     *      javax.security.auth.callback.CallbackHandler, java.util.Map,
     *      java.util.Map)
     */
    @Override
    public void initialize(Subject aSubject, CallbackHandler aCallbackHandler, Map aSharedState, Map aOptions) {
        handler = aCallbackHandler;
        subject = aSubject;
        try {
            InitialContext context = new InitialContext();
            BeanManager bm = (BeanManager)context.lookup("java:comp/BeanManager");
            Bean<UserService> bean = (Bean<UserService>) bm.getBeans(UserService.class).iterator().next();
            System.out.println(aOptions.get("USER_SERVICE"));
            if(aOptions.containsKey("USER_SERVICE")) {
                for(Bean b : bm.getBeans(UserService.class)) {
                    if(b.getBeanClass().getCanonicalName().equals(aOptions.get("USER_SERVICE"))) {
                        bean = b;
                    }
                }
            }
            CreationalContext<UserService> ctx = bm.createCreationalContext(bean);
            svc = (UserService) bm.getReference(bean, UserService.class, ctx);
        } catch (NamingException | NullPointerException ex) {
            Logger.getLogger(ExampleLoginModule.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace(System.out);
        }
    }
 
    /**
     * This method checks whether the name and the password are the same.
     *
     * @throws javax.security.auth.login.LoginException
     * @see javax.security.auth.spi.LoginModule#login()
     */
    @Override
    public boolean login() throws LoginException {
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("login");
        callbacks[1] = new PasswordCallback("password", true);
 
        try {
            handler.handle(callbacks);
 
            String name = ((NameCallback) callbacks[0]).getName();
            String password = String.valueOf(((PasswordCallback) callbacks[1]).getPassword());
 
            System.out.println(svc);
            if (!name.equals("anonymous") && !svc.loadUserByUsername(name).checkPassword(password)) {
                throw new LoginException("Authentication failed");
            }
 
            login = name;
 
            return true;
        } catch (IOException | UnsupportedCallbackException e) {
            throw new LoginException(e.getMessage());
        }
    }
 
    /**
     * Clears subject from principal and credentials.
     *
     * @throws javax.security.auth.login.LoginException
     * @see javax.security.auth.spi.LoginModule#logout()
     */
    @Override
    public boolean logout() throws LoginException {
        try {
            UserPrincipal user = new UserPrincipal(login);
 
            subject.getPrincipals().remove(user);
            
            if(!login.equalsIgnoreCase("anonymous")) {
                for(String s : svc.loadUserByUsername(login).getRoles()) {
                    subject.getPrincipals().remove(new RolePrincipal(s));
                }
            } else {
                subject.getPrincipals().remove(new RolePrincipal("anonymous"));
            }
 
            return true;
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }
    
}