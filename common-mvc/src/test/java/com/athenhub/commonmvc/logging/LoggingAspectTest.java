package com.athenhub.commonmvc.logging;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;

class LoggingAspectTest {
  @Test
  void logController_shouldCallLogManagerEntryAndExit() throws Throwable {
    LogManager logManager = mock(LogManager.class);

    ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
    MethodSignature signature = mock(MethodSignature.class);
    when(pjp.getSignature()).thenReturn(signature);
    when(signature.getDeclaringTypeName()).thenReturn("com.example.TestController");
    when(signature.getName()).thenReturn("testMethod");
    when(signature.getParameterNames()).thenReturn(new String[] {});
    when(pjp.getArgs()).thenReturn(new Object[] {});
    when(pjp.proceed()).thenReturn("result");

    LoggingAspect aspect = new LoggingAspect(logManager);
    aspect.logController(pjp);

    verify(logManager).logControllerEntry(any(), any(), any(), any());
    verify(logManager).logControllerExit(any(), any(), any(), any());
  }
}
