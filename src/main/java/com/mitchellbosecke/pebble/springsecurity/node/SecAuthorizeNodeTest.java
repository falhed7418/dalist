package com.mitchellbosecke.pebble.springsecurity.node;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.context.WebApplicationContext;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.RootNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.node.expression.LiteralStringExpression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;
import com.mitchellbosecke.pebble.utils.Pair;

@RunWith(MockitoJUnitRunner.class)
public class SecAuthorizeNodeTest {
	
	private SecAuthorizeNode node;
	private EvaluationContext evaluationContext;
	private BodyNode elseBody;

	@Test
	public void testRenderSecAuthorizeNode() throws PebbleException, IOException {
		// ServletContext must have applicationContext
		ServletContext servletContext = mock(ServletContext.class);
		WebApplicationContext appContext = mock(WebApplicationContext.class);
		Mockito.when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(appContext);
		Mockito.when(appContext.getBeansOfType(SecurityExpressionHandler.class))
				.thenReturn(createMapSecExpressionHandler());

		//SecurityConextHolder - SecurityContext - Authentication
		SecurityContextImpl securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
		SecurityContextHolder.setContext(securityContext);

		//Pebble componentes, mocks and spies
		ScopeChain scopeChain = new ScopeChain(new HashMap<String, Object>());
		scopeChain.put("request", mock(HttpServletRequest.class));
		scopeChain.put("response", mock(HttpServletResponse.class));
		
		PebbleTemplateImpl self = new PebbleTemplateImpl(new PebbleEngine.Builder().build(),
				new RootNode(new BodyNode(1, new ArrayList<RenderableNode>())),
				"default");
		
		evaluationContext = spy(new EvaluationContext(self, false, Locale.getDefault(), null, null, null, null, scopeChain, null));

		// Prepare SecAuthorizeNode with String "isAuthenticated()"
		Expression<?> stringExpression = new LiteralStringExpression("isAuthenticated()", 1);
		BodyNode right = new BodyNode(1, new ArrayList<RenderableNode>());
		Pair<Expression<?>, BodyNode> condition = new Pair<Expression<?>, BodyNode>(stringExpression, right);
		node = new SecAuthorizeNode(1, condition, createElseBodyNode());
		node.setServletContext(servletContext);

		node.render(self, new StringWriter(), evaluationContext);
	}
	
	@Test
	public void testRenderSecAuthorizeNodeElse() throws PebbleException, IOException {
		// ServletContext must have applicationContext
		ServletContext servletContext = mock(ServletContext.class);
		WebApplicationContext appContext = mock(WebApplicationContext.class);
		Mockito.when(servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE))
				.thenReturn(appContext);
		Mockito.when(appContext.getBeansOfType(SecurityExpressionHandler.class))
				.thenReturn(createMapSecExpressionHandler());
		//SecurityConextHolder - SecurityContext - Authentication
		SecurityContextImpl securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(null, null));
		SecurityContextHolder.setContext(securityContext);
		//Pebble componentes, mocks and spies
		ScopeChain scopeChain = new ScopeChain(new HashMap<String, Object>());
		scopeChain.put("request", mock(HttpServletRequest.class));
		scopeChain.put("response", mock(HttpServletResponse.class));
		
		PebbleTemplateImpl self = new PebbleTemplateImpl(new PebbleEngine.Builder().build(),
				new RootNode(new BodyNode(1, new ArrayList<RenderableNode>())),
				"default");
		
		evaluationContext = spy(new EvaluationContext(self, false, Locale.getDefault(), null, null, null, null, scopeChain, null));
		// Prepare SecAuthorizeNode with String "isAuthenticated()"
		setupSecAuthorizeNode(servletContext, "hasRole('ROLE_ADMIN')");
		
		//Actual Code for test
		StringWriter writer = new StringWriter();
		node.render(self, writer, evaluationContext);
		Mockito.verify(elseBody).render(self, writer, evaluationContext);
	}

	private PebbleTemplateImpl setUpPebbleSpecificComponents() {
		ScopeChain scopeChain = new ScopeChain(new HashMap<String, Object>());
		scopeChain.put("request", mock(HttpServletRequest.class));
		scopeChain.put("response", mock(HttpServletResponse.class));
		
		PebbleTemplateImpl self = new PebbleTemplateImpl(new PebbleEngine.Builder().build(),
				new RootNode(new BodyNode(1, new ArrayList<RenderableNode>())),
				"default");
		
		evaluationContext = spy(new EvaluationContext(self, false, Locale.getDefault(), null, null, null, null, scopeChain, null));
		return self;
	}

	private void setupSecAuthorizeNode(ServletContext servletContext, String secExpression) {
		Expression<?> stringExpression = new LiteralStringExpression(secExpression, 1);
		BodyNode right = new BodyNode(1, new ArrayList<RenderableNode>());
		Pair<Expression<?>, BodyNode> condition = new Pair<Expression<?>, BodyNode>(stringExpression, right);
		node = new SecAuthorizeNode(1, condition, createElseBodyNode());
		node.setServletContext(servletContext);
	}

	private BodyNode createElseBodyNode() {
		elseBody = spy(new BodyNode(1, new ArrayList<RenderableNode>()));
		return elseBody;
	}

	@SuppressWarnings("rawtypes")
	private Map<String, SecurityExpressionHandler> createMapSecExpressionHandler() {
		Map<String, SecurityExpressionHandler> exphandlers = new HashMap<>();
		DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
		exphandlers.put("x", handler);
		return exphandlers;
	}

}
