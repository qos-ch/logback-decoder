/**
 * Copyright (C) 2012, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.decoder;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Validates the regular expressions in {@link RegexPatterns}
 * 
 * @author Anthony Trinh
 */
public class RegexPatternsTest {

  @Test
  public void testDateRegexMatches() {
    final String REGEX = RegexPatterns.Common.DATE_ISO8601_REGEX;

    assertTrue("2006-10-20 14:06:49,812".matches(REGEX));
    
    // TODO: How do we test different locales?
    // TODO: Need to test various date patterns (%d{HH:mm:ss.SSS})
  }

  @Test
  public void testLineOfCallerRegexMatches() {
    final String REGEX = RegexPatterns.LINE_OF_CALLER_REGEX;
     
    assertTrue("24".matches(REGEX));
    assertTrue("1234567890".matches(REGEX));
    assertTrue("?".matches(REGEX));
    assertFalse("123?".matches(REGEX));
    assertFalse("abc123".matches(REGEX));
    assertFalse("abc".matches(REGEX));
    assertFalse(" .!@#$%^&*()_+`".matches(REGEX));
  }
  
  @Test
  public void testFileOfCallerRegexMatches() {
    final String REGEX = RegexPatterns.FILE_OF_CALLER_REGEX;
  
    assertTrue("FooBar.java".matches(REGEX));
    assertFalse(".java".matches(REGEX));
    assertFalse("FooBar".matches(REGEX));
    assertFalse("/FooBar.java".matches(REGEX));
    assertFalse("Foobar!@#$%.java".matches(REGEX));
    assertFalse("Foobar.java!@#$%".matches(REGEX));
  }

  @Test
  public void testRelativeTimeRegexMatches() {
    final String REGEX = RegexPatterns.RELATIVE_TIME_REGEX;

    assertTrue("00001234".matches(REGEX));
    assertTrue("1234567890".matches(REGEX));
    assertFalse("123FooBar456".matches(REGEX));
  }
  
  @Test
  public void testLevelRegexMatches() {
    final String REGEX = RegexPatterns.LEVEL_REGEX;
  
    assertTrue("OFF".matches(REGEX));
    assertTrue("WARN".matches(REGEX));
    assertTrue("ERROR".matches(REGEX));
    assertTrue("INFO".matches(REGEX));
    assertTrue("DEBUG".matches(REGEX));
    assertTrue("TRACE".matches(REGEX));
    assertTrue("ALL".matches(REGEX));
    assertFalse("Off".matches(REGEX));
    assertFalse("DebuG".matches(REGEX));
    assertFalse("INFO123".matches(REGEX));
  }
  
  @Test
  public void testThreadRegexMatches() {
    final String REGEX = RegexPatterns.THREAD_NAME_REGEX;
 
    assertTrue("main".matches(REGEX));
    assertTrue("thread".matches(REGEX));
    assertTrue("thread-123".matches(REGEX));
  }
  
  @Test
  public void testLoggerRegexMatches() {
    final String REGEX = RegexPatterns.LOGGER_NAME_REGEX;
  
    assertTrue("MyLoggerName".matches(REGEX));
    assertTrue("a.b.c.foo".matches(REGEX));
    assertTrue("x.y.foo.MyClassName".matches(REGEX));
    
    // TODO: Need to test for different patterns based on length specifier (%c{10})
  }
  
  @Test
  public void testMessageRegexMatches() {
    final String REGEX = RegexPatterns.MESSAGE_REGEX;
    
    final String MSG_WITH_STACKTRACE = "I couldn't do it, because of this exception\n" +
        "mainPackage.foo.bar.TestException: Houston we have a problem\n" +
        "  at mainPackage.foo.bar.TestThrower.fire(TestThrower.java:22)\n" +
        "  at mainPackage.foo.bar.TestThrower.readyToLaunch(TestThrower.java:17)\n" +
        "  at mainPackage.ExceptionLauncher.main(ExceptionLauncher.java:38)\n";
    
    final String SAMPLEMSG = "The quick brown fox jumps over the lazy dog";
    
    assertTrue(MSG_WITH_STACKTRACE.matches(REGEX));
    assertTrue(SAMPLEMSG.matches(REGEX));
  }
  
  @Test
  public void testClassOfCallerRegexMatches() {
    final String REGEX = RegexPatterns.CLASS_OF_CALLER_REGEX;

    assertTrue("MyLoggerName".matches(REGEX));
    
    // TODO: Need to test for different patterns based on length specifier (%C{10})
  }
  
  @Test
  public void testMethodOfCallerRegexMatches() {
    final String REGEX = RegexPatterns.METHOD_OF_CALLER_REGEX;

    assertTrue("methodName".matches(REGEX));
  }
  
  @Test
  public void testMDCRegexMatches() {
    final String REGEX = RegexPatterns.MDC_REGEX;

    assertTrue("key=value".matches(REGEX));
    assertTrue("k0=v0, k1=v1, k2=v3".matches(REGEX));
  }
  
  @Test
  public void testThrowableProxyRegexMatches() {
    final String REGEX = RegexPatterns.EXCEPTION_REGEX;
    
    final String STACKTRACE1 = "org.omg.CORBA.MARSHAL: com.ibm.ws.pmi.server.DataDescriptor; IllegalAccessException  minor code: 4942F23E\n" +    
         "\tat com.ibm.rmi.io.ValueHandlerImpl.readValue(ValueHandlerImpl.java:199)\n" +
         "\tat com.ibm.rmi.iiop.CDRInputStream.read_value(CDRInputStream.java:1429)\n" +
         "\tat com.ibm.rmi.io.ValueHandlerImpl.read_Array(ValueHandlerImpl.java:625)\n" +
         "\tat com.ibm.rmi.io.ValueHandlerImpl.readValueInternal(ValueHandlerImpl.java:273)\n" +
         "\tat com.ibm.rmi.io.ValueHandlerImpl.readValue(ValueHandlerImpl.java:189)\n" +
         "\tat com.ibm.rmi.iiop.CDRInputStream.read_value(CDRInputStream.java:1429)\n" +
         "\tat com.ibm.ejs.sm.beans._EJSRemoteStatelessPmiService_Tie._invoke(_EJSRemoteStatelessPmiService_Tie.java:613)\n" +
         "\tat com.ibm.CORBA.iiop.ExtendedServerDelegate.dispatch(ExtendedServerDelegate.java:515)\n" +
         "\tat com.ibm.CORBA.iiop.ORB.process(ORB.java:2377)\n" +
         "\tat com.ibm.CORBA.iiop.OrbWorker.run(OrbWorker.java:186)\n" +
         "\tat com.ibm.ejs.oa.pool.ThreadPool$PooledWorker.run(ThreadPool.java:104)\n" +
         "\tat com.ibm.ws.util.CachedThread.run(ThreadPool.java:137)\n";
    
    final String STACKTRACE2 = "java.lang.NullPointerException\n" + 
            "\tat com.xyz.Wombat(Wombat.java:57) ~[wombat-1.3.jar:1.3]\n" +
            "\tat com.xyz.Wombat(Wombat.java:76) ~[wombat-1.3.jar:1.3]\n" +
          "Wrapped by: org.springframework.BeanCreationException: Error creating bean with name 'wombat': \n" +
            "\tat org.springframework.AbstractBeanFactory.getBean(AbstractBeanFactory.java:248) [spring-2.0.jar:2.0]\n" +
            "\tat org.springframework.AbstractBeanFactory.getBean(AbstractBeanFactory.java:170) [spring-2.0.jar:2.0]\n" +
            "\tat org.apache.catalina.StandardContext.listenerStart(StandardContext.java:3934) [tomcat-6.0.26.jar:6.0.26]\n";
    
    final String STACKTRACE3 = "java.lang.RuntimeException: Sorry, try again later\n" +
          "  at BookController.gamma(BookController.java:26)\n" +
          "  at BookController.beta(BookController.java:20)\n" +
          "  at BookController.alpha(BookController.java:18)\n" +
          "  at BookController.main(BookController.java:32)\n" +
          "Caused by: java.lang.RuntimeException: Unable to save order\n" +
          "  at BookService.zeta(BookController.java:51)\n" +
          "  at BookService.epsilon(BookController.java:45)\n" +
          "  at BookService.delta(BookController.java:43)\n" +
          "  at BookController.gamma(BookController.java:24)\n" +
          "  ... 8 common frames omitted\n" +
          "Caused by: java.lang.RuntimeException: Database problem\n" +
          "  at BookDao.iota(BookController.java:66)\n" +
          "  at BookDao.theta(BookController.java:60)\n" +
          "  at BookDao.eta(BookController.java:58)\n" +
          "  at BookService.zeta(BookController.java:49)\n" +
          "  ... 11 common frames omitted\n" +
          "Caused by: java.lang.RuntimeException: Omega server not available\n" +
          "  at BookDao.iota(BookController.java:64)\n" +
          "  ... 14 common frames omitted";
    
    final String STACKTRACE4 = "java.lang.RuntimeException: Omega server not available\n" +
          "  at BookDao.iota(BookController.java:64)\n" +
          "Wrapped by: java.lang.RuntimeException: Database problem\n" +
          "  at BookDao.iota(BookController.java:66)\n" +
          "  at BookDao.theta(BookController.java:60)\n" +
          "  at BookDao.eta(BookController.java:58)\n" +
          "  at BookService.zeta(BookController.java:49)\n" +
          "Wrapped by: java.lang.RuntimeException: Unable to save order\n" +
          "  at BookService.zeta(BookController.java:51)\n" +
          "  at BookService.epsilon(BookController.java:45)\n" +
          "  at BookService.delta(BookController.java:43)\n" +
          "  at BookController.gamma(BookController.java:24)\n" +
          "Wrapped by: java.lang.RuntimeException: Sorry, try again later\n" +
          "  at BookController.gamma(BookController.java:26)\n" +
          "  at BookController.beta(BookController.java:20)\n" +
          "  at BookController.alpha(BookController.java:18)\n" +
          "  at BookController.main(BookController.java:32)\n";
        
    assertTrue(STACKTRACE1.matches(REGEX));
    assertTrue(STACKTRACE2.matches(REGEX));
    assertTrue(STACKTRACE3.matches(REGEX));
    assertTrue(STACKTRACE4.matches(REGEX));
  }
  
  @Test
  public void testMarkerRegexMatches() {
    final String REGEX = RegexPatterns.MARKER_REGEX;
    
    assertTrue("markerName".matches(REGEX));
    assertTrue("parent1Marker [ child ]".matches(REGEX));
    assertTrue("parent2Marker [ child1, child2 ]".matches(REGEX));
  }
  
  @Test
  public void testCallerDataRegexMatches() {
    final String CALLER_STACKTRACE = 
        "Caller+0   at mainPackage.sub.sample.Bar.sampleMethodName(Bar.java:22)\n" +
        "Caller+1   at mainPackage.sub.sample.Bar.createLoggingRequest(Bar.java:17)\n" +
        "Caller+2   at mainPackage.ConfigTester.main(ConfigTester.java:38)";

    final String REGEX = RegexPatterns.CALLER_STACKTRACE_REGEX;
    
    assertTrue(CALLER_STACKTRACE.matches(REGEX));
  }
  
}
