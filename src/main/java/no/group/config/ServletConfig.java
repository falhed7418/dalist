package no.group.config;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.loader.ServletLoader;
import com.mitchellbosecke.pebble.spring4.PebbleViewResolver;
import com.mitchellbosecke.pebble.spring4.extension.SpringExtension;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.mitchellbosecke.pebble.springsecurity.SpringSecurityExtension;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages={"no.group.control"})
public class ServletConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private ServletContext servletContext;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        addResourceHandler(registry, "/css/**", "/css/");
        addResourceHandler(registry, "/js/**", "/js/");
    }

    private void addResourceHandler(ResourceHandlerRegistry registry,
                String resAntMatcher, String resourceLocation) {
        registry.addResourceHandler(resAntMatcher, resourceLocation);
    }

    @Bean
    public Loader loader() {
        return new ServletLoader(servletContext);
    }

    @Bean
    public SpringExtension extension() {
        return new SpringExtension();
    }

    @Bean
    public SpringSecurityExtension secExtension() {
        return new SpringSecurityExtension(servletContext);
    }

    @Bean
    public PebbleEngine engine() {
        PebbleEngine pengine = new PebbleEngine.Builder()
            .loader(loader())
            .extension(extension())
            .extension(secExtension())
            .cacheActive(false)
            .build();
        return pengine;
    }

    @Bean
    public PebbleViewResolver viewResolver() {
        PebbleViewResolver resolver = new PebbleViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".html");
        resolver.setPebbleEngine(engine());
        return resolver;
    }


}
