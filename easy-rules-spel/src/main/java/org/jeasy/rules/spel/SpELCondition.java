/**
 * The MIT License
 *
 *  Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.spel;

import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Facts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * This class is an implementation of {@link Condition} that uses <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions">SpEL</a> to evaluate the condition.
 *
 * Each fact is set as a variable in the {@link org.springframework.expression.EvaluationContext}.
 *
 * The facts map is set as the root object of the {@link org.springframework.expression.EvaluationContext}.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class SpELCondition implements Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpELCondition.class);

    private final ExpressionParser parser = new SpelExpressionParser();

    private String expression;
    private Expression compiledExpression;

    /**
     * Create a new {@link SpELCondition}.
     *
     * @param expression the condition written in expression language
     */
    public SpELCondition(String expression) {
        this.expression = expression;
        compiledExpression = parser.parseExpression(expression);
    }

    /**
     * Create a new {@link SpELCondition}.
     *
     * @param expression    the condition written in expression language
     * @param parserContext the SpEL parser context
     */
    public SpELCondition(String expression, ParserContext parserContext) {
        this.expression = expression;
        compiledExpression = parser.parseExpression(expression, parserContext);
    }

    @Override
    public boolean evaluate(Facts facts) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setRootObject(facts.asMap());
            context.setVariables(facts.asMap());
            return  compiledExpression.getValue(context, Boolean.class);
        } catch (Exception e) {
            LOGGER.error("Unable to evaluate expression: '" + expression + "' on facts: " + facts, e);
            return false;
        }
    }
}
