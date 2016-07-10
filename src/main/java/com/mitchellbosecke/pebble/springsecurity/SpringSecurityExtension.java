package com.mitchellbosecke.pebble.springsecurity;

import java.util.List;

import javax.servlet.ServletContext;

import java.util.ArrayList;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.tokenParser.TokenParser;
import com.mitchellbosecke.pebble.springsecurity.parser.SecTokenParser;

public class SpringSecurityExtension extends AbstractExtension {
	
	private ServletContext servletContext;
	
	public SpringSecurityExtension(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
    
    @Override
    public List<TokenParser> getTokenParsers() {
        List<TokenParser> tokenParsers = new ArrayList<>();
        tokenParsers.add(new SecTokenParser(servletContext));
        return tokenParsers;
    }
}
