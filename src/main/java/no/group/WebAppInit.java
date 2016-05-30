package no.group;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import no.group.config.RootConfig;
import no.group.config.ServletConfig;

public class WebAppInit extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { RootConfig.class };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] { ServletConfig.class };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }
    
}
