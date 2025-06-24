package org.abx.webappgen.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController extends RoleController {


    @RequestMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public String login(final HttpServletRequest request,
                        @RequestParam String username,
                        @RequestParam String password) throws ServletException {

        request.logout();
        JSONObject status = new JSONObject();
        try {
            request.login(username, password);
            status.put("logged", true);
        } catch (ServletException e) {
            status.put("logged", false);
            status.put("error", "Invalid credentials.");
        }
        return status.toString(2);
    }

    @RequestMapping(value = "/isLoggedIn", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public boolean isLoggedIn(final HttpServletRequest request) {
        return request.getUserPrincipal() != null && request.getUserPrincipal().getName() != null;
    }

    @RequestMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public boolean logout(final HttpServletRequest request) throws ServletException {
        request.logout();
        return true;
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(value = "/setLang", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setLang(final HttpSession session,
                        @RequestParam String lang) throws ServletException {
        updateEnv(session, "lang", lang);
    }


}
