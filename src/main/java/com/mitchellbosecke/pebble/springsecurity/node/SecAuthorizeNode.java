package com.mitchellbosecke.pebble.springsecurity.node;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.NodeVisitor;
import com.mitchellbosecke.pebble.node.AbstractRenderableNode;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplateImpl;
import com.mitchellbosecke.pebble.template.ScopeChain;
import com.mitchellbosecke.pebble.utils.Pair;

public class SecAuthorizeNode extends AbstractRenderableNode {
	
	private static final Logger log = LoggerFactory.getLogger(SecAuthorizeNode.class);

    private final Pair<Expression<?>, BodyNode> ifWithCondition;
    private final BodyNode elseBody;
	private ServletContext servletContext;

    public SecAuthorizeNode(int lineNumber,
            Pair<Expression<?>, BodyNode> ifWithCondition,
            BodyNode elseBody) {
        super(lineNumber);
        this.ifWithCondition = ifWithCondition;
        this.elseBody = elseBody;
    }

    @Override
    public void render(PebbleTemplateImpl self, Writer writer, EvaluationContext context) throws PebbleException,
           IOException {
        Expression<?> exp = ifWithCondition.getLeft();
		String result = (String)exp.evaluate(self, context);

        ScopeChain scopeChain = context.getScopeChain();
    	ServletRequest request = (ServletRequest) scopeChain.get("request");
		ServletResponse response = (ServletResponse) scopeChain.get("response");

		SecurityExpressionHandler<FilterInvocation> expressionHandler = getExpressionHandler();
		ExpressionParser parser = expressionHandler.getExpressionParser();
		FilterInvocation invocation = new FilterInvocation(request, response, new javax.servlet.FilterChain(){
			public void doFilter(ServletRequest request, ServletResponse response) {
				throw new UnsupportedOperationException();
			}
		});

		SecurityContext context2 = SecurityContextHolder.getContext();
		Authentication authentication = context2.getAuthentication();
		org.springframework.expression.EvaluationContext createExpressionEvaluationContext = expressionHandler.createEvaluationContext(authentication, invocation);
		boolean satisfied = ExpressionUtils.evaluateAsBoolean(parser.parseExpression(result), createExpressionEvaluationContext);
		log.debug("satisfied: {}", satisfied);
		
		BodyNode right = ifWithCondition.getRight();
		if (satisfied) {
			right.render(self, writer, context);
		}
		else {
			elseBody.render(self, writer, context);
		}

    }

	@Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    public Pair<Expression<?>, BodyNode> getIfWithCondition() {
        return ifWithCondition;
    }
    public BodyNode getElseBody() {
        return elseBody;
    }

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public SecurityExpressionHandler<FilterInvocation>  getExpressionHandler() throws IOException {
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
    			+ "context. There must be at least one in order to support expressions in JSP 'authorize' tags.");
    }

}
