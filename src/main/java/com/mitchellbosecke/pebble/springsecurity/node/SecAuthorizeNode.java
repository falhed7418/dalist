package com.mitchellbosecke.pebble.springsecurity.node;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.security.access.expression.ExpressionUtils;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;

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
        boolean satisfied = evaluateSecExp(self, context);
		log.debug("satisfied: {}", satisfied);
		BodyNode node = ifWithCondition.getRight();
		if (!satisfied) {
			node = elseBody;
		}
		node.render(self, writer, context);

    }

	private boolean evaluateSecExp(PebbleTemplateImpl self, EvaluationContext context)
			throws IOException, PebbleException {
		SecAuthorizeNodeDetails details = createAuthNodeSecDetails(context);

		SecurityExpressionHandler<FilterInvocation> expressionHandler = details.getExpressionHandler();
		ExpressionParser parser = expressionHandler.getExpressionParser();

		Expression<?> ifBodyNode = ifWithCondition.getLeft();
		boolean satisfied = ExpressionUtils.evaluateAsBoolean(parser.parseExpression((String)ifBodyNode.evaluate(self, context)),
				details.createSpringEvaluationContext(expressionHandler));
		return satisfied;
	}

	private SecAuthorizeNodeDetails createAuthNodeSecDetails(EvaluationContext context) {
		ScopeChain scopeChain = context.getScopeChain();
    	ServletRequest request = (ServletRequest) scopeChain.get("request");
		ServletResponse response = (ServletResponse) scopeChain.get("response");
	    SecAuthorizeNodeDetails details = new SecAuthorizeNodeDetails(request, response, servletContext);
		return details;
	}

	@Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
