package org.abx.webappgen.spring;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/favicon.ico")
    public void redirectFavicon(HttpServletResponse response) throws IOException {
        response.sendRedirect("/web/favicon.ico");
    }


}