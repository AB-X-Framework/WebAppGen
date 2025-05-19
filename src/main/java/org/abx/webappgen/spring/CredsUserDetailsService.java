package org.abx.webappgen.spring;


import org.abx.webappgen.persistence.dao.UserRepository;
import org.abx.webappgen.persistence.model.User;
import org.abx.util.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service("userDetailsService")
@Transactional
public class CredsUserDetailsService implements UserDetailsService {

    private HashMap<String, Collection<GrantedAuthority>> authoritiesPerRole;
    @Autowired
    private UserRepository userRepository;


    public CredsUserDetailsService() throws Exception{
        super();
        processAuthorities();
    }

    // API

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            final User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("No user found with username: " + username);
            }
            if (!user.enabled) {
                throw new UsernameNotFoundException("No username: " + username + " is disabled.");
            }

            return new org.springframework.security.core.userdetails.User(user.username,
                    user.password,
                    true, true, true, true
                    , getAuthorities(user.role));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    // UTIL

    private Collection<GrantedAuthority> getAuthorities(String role) {
        return authoritiesPerRole.get(role);
    }


    private void processAuthorities() throws Exception {
        JSONObject obj = new JSONObject(StreamUtils.readStream
                (CredsUserDetailsService.class.getClassLoader().getResourceAsStream
                ("org/abx/webappgen/Permission.json")));
        authoritiesPerRole = new HashMap<>();
        for (String role : obj.keySet()) {
            JSONArray permissions = obj.getJSONArray(role);
            authoritiesPerRole.put(role, getGrantedAuthorities(permissions));
        }

    }


    private List<GrantedAuthority> getGrantedAuthorities(final JSONArray permissions) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (int i = 0; i < permissions.length(); i++) {
            authorities.add(new SimpleGrantedAuthority(permissions.getString(i)));
        }
        return authorities;
    }

}