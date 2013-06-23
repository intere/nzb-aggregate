package com.intere.spring.nzb.aop.profiling;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MethodExecutionProfiling {
	
	private static final Logger LOG = Logger.getLogger(MethodExecutionProfiling.class);

	@Pointcut("execution( * com.intere.spring.nzb.builder.BinsearchUtils.*(..))")
	public void profileExecutionTime() {}
	
	@Around("profileExecutionTime()")
	public Object profile(ProceedingJoinPoint joinPoint) throws Throwable {
		
		long start = System.currentTimeMillis();
        Object output = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        
        LOG.debug("Method execution time (" + getMethodDetails(joinPoint.getSignature()) + "): " + elapsedTime + " milliseconds.");
        return output;
	}

	private String getMethodDetails(Signature signature) {
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(signature.getDeclaringType().getCanonicalName());
		builder.append(".");
		builder.append(signature.getName());
		builder.append("(...)");
		
		return builder.toString();
	}
}
