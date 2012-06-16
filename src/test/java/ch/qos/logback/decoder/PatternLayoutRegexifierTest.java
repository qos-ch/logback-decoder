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

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Tests the {@link PatternLayoutRegexifier} class
 * 
 * @author Anthony Trinh
 */
public class PatternLayoutRegexifierTest {
  private PatternLayoutRegexifier regexifier;
  static private LoggerContext context;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    context = (LoggerContext)LoggerFactory.getILoggerFactory();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    regexifier = new PatternLayoutRegexifier();
    regexifier.setContext(context);
  }

  @After
  public void tearDown() throws Exception {
    StatusPrinter.printInCaseOfErrorsOrWarnings(context);
  }

  @Test
  public void testDatePattern() {
    final String REGEX = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}";
    
    for (String p : Arrays.asList("%d", "%date")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("2006-10-20 14:06:49,812".matches(REGEX));
    
    // TODO: How do we test different locales?
    // TODO: Need to test various date patterns (%d{HH:mm:ss.SSS})
  }

  @Test
  public void testLineOfCallerPattern() {
    final String REGEX = "\\d+|\\?";
    
    for (String p : Arrays.asList("%L", "%line")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("24".matches(REGEX));
    assertTrue("1234567890".matches(REGEX));
    assertTrue("?".matches(REGEX));
    assertFalse("123?".matches(REGEX));
    assertFalse("abc123".matches(REGEX));
    assertFalse("abc".matches(REGEX));
    assertFalse(" .!@#$%^&*()_+`".matches(REGEX));
  }
  
  @Test
  public void testFileOfCallerPattern() {
    final String REGEX = "[$_a-zA-z0-9]+\\.java";
    
    for (String p : Arrays.asList("%F", "%file")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("FooBar.java".matches(REGEX));
    assertFalse(".java".matches(REGEX));
    assertFalse("FooBar".matches(REGEX));
    assertFalse("/FooBar.java".matches(REGEX));
    assertFalse("Foobar!@#$%.java".matches(REGEX));
    assertFalse("Foobar.java!@#$%".matches(REGEX));
  }

  @Test
  public void testRelativeTimePattern() {
    final String REGEX = "\\d+";
    
    for (String p : Arrays.asList("%r", "%relative")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("00001234".matches(REGEX));
    assertTrue("1234567890".matches(REGEX));
    assertFalse("123FooBar456".matches(REGEX));
  }
  
  @Test
  public void testLevelPattern() {
    final String REGEX = "(?:OFF)|(?:WARN)|(?:ERROR)|(?:INFO)|(?:DEBUG)|(?:TRACE)|(?:ALL)";
    
    for (String p : Arrays.asList("%le", "%level", "%p")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
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
  public void testThreadPattern() {
    final String REGEX = ".+";
    
    for (String p : Arrays.asList("%t", "%thread")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("main".matches(REGEX));
    assertTrue("thread".matches(REGEX));
    assertTrue("thread-123".matches(REGEX));
  }
  
  @Test
  public void testLoggerPattern() {
    final String REGEX = ".+";
    
    for (String p : Arrays.asList("%lo", "%logger", "%c")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("MyLoggerName".matches(REGEX));
    assertTrue("a.b.c.foo".matches(REGEX));
    assertTrue("x.y.foo.MyClassName".matches(REGEX));
    
    // TODO: Need to test for different patterns based on length specifier (%c{10})
  }
  
  @Test
  public void testMessagePattern() {
    final String REGEX = "?m.+";
    
    for (String p : Arrays.asList("%msg", "%message", "%m")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    final String STACKTRACE = "mainPackage.foo.bar.TestException: Houston we have a problem\n" +
        "  at mainPackage.foo.bar.TestThrower.fire(TestThrower.java:22)\n" +
        "  at mainPackage.foo.bar.TestThrower.readyToLaunch(TestThrower.java:17)\n" +
        "  at mainPackage.ExceptionLauncher.main(ExceptionLauncher.java:38)\n";
    
    final String SAMPLEMSG = "The quick brown fox jumps over the lazy dog";
    
    assertTrue(STACKTRACE.matches(REGEX));
    assertTrue(SAMPLEMSG.matches(REGEX));
  }
  
  @Test
  public void testClassOfCallerPattern() {
    final String REGEX = "[$_a-zA-z0-9]+";
    
    for (String p : Arrays.asList("%C", "%class")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("MyLoggerName".matches(REGEX));
    
    // TODO: Need to test for different patterns based on length specifier (%C{10})
  }
  
  @Test
  public void testMethodOfCallerPattern() {
    final String REGEX = "[$_a-zA-z0-9]+";
    
    for (String p : Arrays.asList("%M", "%method")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("methodName".matches(REGEX));
  }
  
  @Test
  public void testMDCPattern() {
    final String REGEX = "(?:[^=]+=.+,)*[^=]+=.+";
    
    for (String p : Arrays.asList("%X", "%mdc")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue("key=value".matches(REGEX));
    assertTrue("k0=v0, k1=v1, k2=v3".matches(REGEX));
  }
  
  @Test
  public void testThrowableProxyPattern() {
    final String REGEX = ".+(?:Exception|Error)[^\\n]++(?:\\s+at .++)+";
    
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
  		
    for (String p : Arrays.asList("%xEx", "%xException", "%xThrowable", "%rEx", "%rootException")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      System.out.println("pattern: " + regexifier.doLayout(null));
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    assertTrue(STACKTRACE1.matches(REGEX));
    assertTrue(STACKTRACE2.matches(REGEX));
    assertTrue(STACKTRACE3.matches(REGEX));
    assertTrue(STACKTRACE4.matches(REGEX));
  }
  
  @Test
  public void testMarkerPattern() {
    final String REGEX = ".*(?: \\[[^\\]])?";
    
    regexifier.setPattern("%marker");
    regexifier.start();
    assertTrue(regexifier.isStarted());
    System.out.println("pattern: " + regexifier.doLayout(null));
    assertEquals(REGEX, regexifier.doLayout(null));
    
    assertTrue("markerName".matches(REGEX));
    assertTrue("parent1Marker [ child ]".matches(REGEX));
    assertTrue("parent2Marker [ child1, child2 ]".matches(REGEX));
  }
  
  @Test
  public void testMixedPatterns() {
    final String REGEX_DATE = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}";
    final String REGEX_FILE = "[$_a-zA-z0-9]+\\.java";
    final String REGEX_LINE = "\\d+|\\?";
    final String REGEX = REGEX_DATE + " " + REGEX_FILE + ":" + REGEX_LINE + " " + REGEX_DATE;
    
    String p = "%d %F:%L %d";
    regexifier.setPattern(p);
    regexifier.start();
    assertTrue(regexifier.isStarted());
    System.out.println("pattern: " + regexifier.doLayout(null));
    assertEquals(REGEX, regexifier.doLayout(null));
  }
}
