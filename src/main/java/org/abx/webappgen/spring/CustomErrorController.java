package org.abx.webappgen.spring;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.abx.webappgen.persistence.PageModel;
import org.abx.webappgen.persistence.dao.MapEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

import static org.abx.webappgen.utils.ElementUtils.mapHashCode;

@Controller
public class CustomErrorController implements ErrorController {
    @Autowired
    public MapEntryRepository repository;

    @Autowired
    private PageModel pageModel;

    public final long favicon = mapHashCode("app.Env", "favicon.ico");
    @GetMapping("/favicon.ico")
    public void redirectFavicon(HttpServletResponse response) throws IOException {

        response.sendRedirect(repository.findByMapEntryId(favicon).mapValue);
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null) {
            int status = Integer.parseInt(statusCode.toString());
            if (status == 404) {
                return "redirect:/page/" + pageModel.getHome();
            }
        }
        return "error"; // fallback error page
    }

}