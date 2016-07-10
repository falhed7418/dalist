package com.mitchellbosecke.pebble.springsecurity.parser;

import com.mitchellbosecke.pebble.error.ParserException;
import com.mitchellbosecke.pebble.lexer.Token;
import com.mitchellbosecke.pebble.lexer.TokenStream;
import com.mitchellbosecke.pebble.node.BodyNode;
import com.mitchellbosecke.pebble.node.RenderableNode;
import com.mitchellbosecke.pebble.node.expression.Expression;
import com.mitchellbosecke.pebble.parser.Parser;
import com.mitchellbosecke.pebble.parser.StoppingCondition;
import com.mitchellbosecke.pebble.tokenParser.AbstractTokenParser;
import com.mitchellbosecke.pebble.utils.Pair;
import com.mitchellbosecke.pebble.springsecurity.node.SecAuthorizeNode;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

public class SecTokenParser extends AbstractTokenParser {
	
	private javax.servlet.ServletContext servletContext;

    public SecTokenParser(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
    public RenderableNode parse(Token token, Parser parser) throws ParserException {
        TokenStream stream = parser.getStream();
        int lineNumber = token.getLineNumber();
        stream.next();

        Expression<?> expression = parser.getExpressionParser().parseExpression();

        stream.expect(Token.Type.EXECUTE_END);

        BodyNode body = parser.subparse(decideIfFork);

        Pair<Expression<?>, BodyNode> pair = new Pair<Expression<?>, BodyNode>(expression, body);

        BodyNode elseBody = null;

        if ("else".equals(stream.current().getValue())) {
            stream.next();
            stream.expect(Token.Type.EXECUTE_END);
            elseBody = parser.subparse(decideIfEnd);
        }
        stream.next();
        stream.expect(Token.Type.EXECUTE_END);
        SecAuthorizeNode secAuthorizeNode = new SecAuthorizeNode(lineNumber, pair, elseBody);
        secAuthorizeNode.setServletContext(servletContext);
		return secAuthorizeNode;
    }

    private StoppingCondition decideIfFork = new StoppingCondition() {
        @Override
        public boolean evaluate(Token token) {
            return token.test(Token.Type.NAME, "else", "endsecauthorize");
        }
    };

    private StoppingCondition decideIfEnd = new StoppingCondition() {
        @Override
        public boolean evaluate(Token token) {
            return token.test(Token.Type.NAME, "endsecauthorize");
        }
    };
    
    @Override
    public String getTag() {
        return "secauthorize";
    }

}
