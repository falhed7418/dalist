package no.group.control;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DefaultController {

    @RequestMapping(value = "/login", method = GET)
    public String defaultMethod() {
        return "login";
    }

    @RequestMapping(value = "/", method = GET)
    public String entry() {
        return "default";
    }
    
}
