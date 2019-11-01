package com.bk.karam.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;

public class ReflectionUtils {

    private static ExpressionParser parser = new SpelExpressionParser();

    public static Method findMethod(Class<?> clazz, String name, Object... args) throws NoSuchMethodException {
        Class<?>[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }
        return clazz.getMethod(name, classes);
    }

    public static Method findMethod(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        if (null == method) {
            throw new NullPointerException("Cannot find method " + pjp.getSignature());
        }
        return method;
    }

    public static Object parserSpEL(String expressionString, ParserContext parserContext) {
        Expression expression = parser.parseExpression(expressionString, parserContext);
        return expression.getValue();
    }
}
