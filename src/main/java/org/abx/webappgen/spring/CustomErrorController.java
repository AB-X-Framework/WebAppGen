package org.abx.webappgen.spring;

import jakarta.servlet.http.HttpServletResponse;
import org.abx.webappgen.persistence.dao.MapEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

import static org.abx.webappgen.utils.ElementUtils.mapHashCode;

@Controller
public class CustomErrorController implements ErrorController {
    @Autowired
    public MapEntryRepository repository;

    public final long favicon = mapHashCode("app.Env", "favicon.ico");
    @GetMapping("/favicon.ico")
    public void redirectFavicon(HttpServletResponse response) throws IOException {

        response.sendRedirect(repository.findByMapEntryId(favicon).mapValue);
    }


}