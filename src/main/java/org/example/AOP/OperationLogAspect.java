package org.example.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.example.DAO.OperationLogDAO;

import java.util.Arrays;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogDAO logDAO;

    // 🎯 拦截 Service 层所有方法
    @Pointcut("execution(* org.example.Service.*.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();

        // 📌 参数转字符串
        String params = Arrays.toString(joinPoint.getArgs());

        // 📌 获取用户名（JWT / Spring Security）
        String username = "anonymous";
        try {
            username = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName();
        } catch (Exception ignored) {}

        // 📌 自动识别操作类型
        String operation = getOperationType(methodName);

        String result = "SUCCESS";
        String errorMsg = null;

        Object res;

        try {
            res = joinPoint.proceed();
        } catch (Throwable e) {
            result = "FAIL";
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long time = System.currentTimeMillis() - start;

            // 🚀 入库
            logDAO.insertLog(
                    username,
                    methodName,
                    operation,
                    params,
                    result,
                    errorMsg,
                    time
            );
        }

        return res;
    }

    // 🎯 操作类型识别（企业常用写法）
    private String getOperationType(String methodName) {
        methodName = methodName.toLowerCase();

        if (methodName.contains("add") || methodName.contains("insert")) {
            return "INSERT";
        } else if (methodName.contains("delete")) {
            return "DELETE";
        } else if (methodName.contains("update")) {
            return "UPDATE";
        } else if (methodName.contains("get") || methodName.contains("find")) {
            return "SELECT";
        }
        return "OTHER";
    }
}