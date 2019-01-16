package com.suse.salt.netapi.datatypes;

import java.io.Serializable;

import com.suse.salt.netapi.AuthModule;

/**
 * Datatype for holding all data needed for password authentication
 */
public class PasswordAuth implements Serializable {

	private static final long serialVersionUID = 3626308406445646575L;
	private String username;
    private String password;
    private AuthModule module;
    
	public PasswordAuth() {
		super();
	}

	public PasswordAuth(String username, String password, AuthModule module) {
        this.username = username;
        this.password = password;
        this.module = module;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public AuthModule getModule() {
        return module;
    }

    @Override
    public String toString() {
        return "PasswordAuth(username = " + username +
               ", password = <REDACTED>, authModule = " + module.getValue() + ")";
    }
}
