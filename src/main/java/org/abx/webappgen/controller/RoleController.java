package org.abx.webappgen.controller;

import jakarta.servlet.http.HttpSession;
import org.abx.webappgen.persistence.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.stream.Collectors;

public class RoleController {
    @Autowired
    public PageModel pageModel;

    public final static String Env = "Env";

    public Set<String> getRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return roles;
    }
    public SessionEnv env(HttpSession session) {
        if (session.getAttribute(Env) == null) {
            SessionEnv env = new SessionEnv(pageModel.defaultEnv());
            session.setAttribute(Env, env);
        }
        return (SessionEnv) session.getAttribute(Env);
    }

    public void updateEnv(HttpSession session,String key, String value) {
        SessionEnv env =env(session);
        env.set(key, value);
    }
}
