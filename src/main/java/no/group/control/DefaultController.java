package no.group.control;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class DefaultController {

    private static final Logger log = LoggerFactory.getLogger(DefaultController.class);

    @RequestMapping(value = "/login", method = GET)
    public String defaultMethod() {
        return "login";
    }

    @RequestMapping(value = "/", method = GET)
    public String entry() {
        return "default";
    }

    @RequestMapping(value = "/formpost", method = POST)
    public String formpost(@ModelAttribute TheForm theForm) {
        log.info("this is theform: ", theForm.getName());
        log.info("this is theform: ", theForm.getName());
        return "form";
    }
    
}
