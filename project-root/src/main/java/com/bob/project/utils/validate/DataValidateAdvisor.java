package com.bob.project.utils.validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.bob.project.utils.validate.constraint.Payload;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.util.ReflectionUtils;

/**
 * 数据校验切面
 *
 * @author wb-jjb318191
 * @create 2018-01-31 16:38
 */
@Aspect
@Order(1)
public class DataValidateAdvisor {

    /**
     * 定义Before AOP,代理所有标识了{@linkplain DataValidate}的方法,做数据校验工作
     *
     * @param joinpoint
     */
    @Before("@annotation(com.bob.project.utils.validate.DataValidate)")
    public void validateBefore(JoinPoint joinpoint) {
        Method method = ((MethodSignature)joinpoint.getSignature()).getMethod();
        DataValidate ann = method.getDeclaredAnnotation(DataValidate.class);
        Object validatedArg = joinpoint.getArgs()[ann.order()];
        ValidateProcessor.doValidating(validatedArg, ann.group());
    }

}
