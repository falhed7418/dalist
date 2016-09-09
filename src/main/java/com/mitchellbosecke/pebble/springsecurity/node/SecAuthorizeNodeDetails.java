package com.mitchellbosecke.pebble.springsecurity.node;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SecAuthorizeNodeDetails  {
	
	private ServletRequest request;
	private ServletResponse response;
	private ServletContext servletContext;
	
	
	public SecAuthorizeNodeDetails(ServletRequest request, ServletResponse response, ServletContext servletContext) {
		super();
		this.request = request;
		this.response = response;
		this.servletContext = servletContext;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SecurityExpressionHandler<FilterInvocation> getExpressionHandler() throws IOException {
		ApplicationContext appContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(servletContext);
		java.util.Map<String, SecurityExpressionHandler> handlers = appContext
				.getBeansOfType(SecurityExpressionHandler.class);
		for (SecurityExpressionHandler h : handlers.values()) {
			if (FilterInvocation.class.equals(GenericTypeResolver.resolveTypeArgument(h.getClass(),
					SecurityExpressionHandler.class))) {
				return h;
			}
		}
		throw new IOException("No visible WebSecurityExpressionHandler instance could be found in the application "
				+ "context. There must be at least one in order to support expressions in secauthorize tag");
	}

	protected EvaluationContext createSpringEvaluationContext(SecurityExpressionHandler<FilterInvocation> expressionHandler) {
		FilterInvocation invocation = new FilterInvocation(request, response, new FilterChain(){
			public void doFilter(ServletRequest request, ServletResponse response) {
				throw new UnsupportedOperationException();
			}
		});
	
		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		org.springframework.expression.EvaluationContext createExpressionEvaluationContext = expressionHandler.createEvaluationContext(authentication, invocation);
		return createExpressionEvaluationContext;
	}
	
}
