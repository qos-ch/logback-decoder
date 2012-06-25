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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.classic.spi.CallerData;

/**
 * Validates the regular expressions in {@link RegexPatterns}
 * 
 * @author Anthony Trinh
 */
public class RegexPatternsTest {
  private static final String CALLER_STACKTRACE1 = 
      "Caller+0   at mainPackage.sub.sample.Bar.sampleMethodName(Bar.java:22)";
  
  private static final String CALLER_STACKTRACE2 = 
      "Caller+0   at mainPackage.sub.sample.Bar.sampleMethodName(Bar.java:22)\n" +
      "Caller+1   at mainPackage.sub.sample.Bar.createLoggingRequest(Bar.java:17)\n" +
      "Caller+2   at mainPackage.ConfigTester.main(ConfigTester.java:38)";
  
  private static final String CALLER_STACKTRACE3 = 
      CallerData.CALLER_DATA_NA + "   at mainPackage.sub.sample.Bar.sampleMethodName(Bar.java:22)\n" +
      CallerData.CALLER_DATA_NA + "   at mainPackage.sub.sample.Bar.createLoggingRequest(Bar.java:17)\n" +
      CallerData.CALLER_DATA_NA + "   at mainPackage.ConfigTester.main(ConfigTester.java:38)";
  
  private static final String MSG_WITH_STACKTRACE = "I couldn't do it because of this exception\n" +
      "mainPackage.foo.bar.TestException: Houston we have a problem\n" +
      "  at mainPackage.foo.bar.TestThrower.fire(TestThrower.java:22)\n" +
      "  at mainPackage.foo.bar.TestThrower.readyToLaunch(TestThrower.java:17)\n" +
      "  at mainPackage.ExceptionLauncher.main(ExceptionLauncher.java:38)\n";
  
  private static final String SAMPLEMSG = "The quick brown fox jumps over the lazy dog";
  
  private static final String STACKTRACE1 = "org.omg.CORBA.MARSHAL: com.ibm.ws.pmi.server.DataDescriptor; IllegalAccessException  minor code: 4942F23E\n" +    
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
 
  private static final String STACKTRACE2 = "java.lang.NullPointerException\n" + 
         "\tat com.xyz.Wombat(Wombat.java:57) ~[wombat-1.3.jar:1.3]\n" +
         "\tat com.xyz.Wombat(Wombat.java:76) ~[wombat-1.3.jar:1.3]\n" +
       "Wrapped by: org.springframework.BeanCreationException: Error creating bean with name 'wombat': \n" +
         "\tat org.springframework.AbstractBeanFactory.getBean(AbstractBeanFactory.java:248) [spring-2.0.jar:2.0]\n" +
         "\tat org.springframework.AbstractBeanFactory.getBean(AbstractBeanFactory.java:170) [spring-2.0.jar:2.0]\n" +
         "\tat org.apache.catalina.StandardContext.listenerStart(StandardContext.java:3934) [tomcat-6.0.26.jar:6.0.26]\n";
 
  private static final String STACKTRACE3 = "java.lang.RuntimeException: Sorry, try again later\n" +
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
 
  private static final String STACKTRACE4 = "java.lang.RuntimeException: Omega server not available\n" +
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
     
 
  // TODO: To conform with JUnit best practices, separate each test 
  // function into its own test case, and put individual assertions 
  // into their own test functions there. The tests will be longer
  // (at least in terms of LOC), but the testability will have
  // improved!
  
  @Test
  public void testDateRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.Common.DATE_ISO8601_REGEX;

    assertTrue("2006-10-20 14:06:49,812".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testDateRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.Common.DATE_ISO8601_REGEX;
    Pattern pattern = Pattern.compile(String.format("(%1$s) <.*>: .*\\n", REGEX));
    
    Matcher m = pattern.matcher("2006-10-20 14:06:49,812 <FooBar.java:24>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("2006-10-20 14:06:49,812", m.group(1).toString());
    
    m = pattern.matcher(" <FooBar.java:24>: hello world!\n");
    assertFalse(m.find());
  }

  @Test
  public void testLineOfCallerRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.LINE_OF_CALLER_REGEX;
     
    assertTrue("24".matches(REGEX));
    assertTrue("1234567890".matches(REGEX));
    assertTrue("?".matches(REGEX));
    assertFalse("123?".matches(REGEX));
    assertFalse("abc123".matches(REGEX));
    assertFalse("abc".matches(REGEX));
    assertFalse(" .!@#$%^&*()_+`".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testLineOfCallerRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.LINE_OF_CALLER_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} .*\\.java:(%1$s) <.*>: .*\\n", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 FooBar.java:24 <TRACE>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("24", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 FooBar.java:1234567890 <TRACE>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("1234567890", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 FooBar.java:? <TRACE>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("?", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 FooBar.java:123? <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 FooBar.java:abc123 <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 FooBar.java:abc <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 FooBar.java:.!@#$%^&*()_+` <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 FooBar.java: <TRACE>: hello world!\n");
    assertFalse(m.find());
  }
  
  @Test
  public void testFileOfCallerRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.FILE_OF_CALLER_REGEX;
  
    assertTrue("FooBar.java".matches(REGEX));
    assertFalse(".java".matches(REGEX));
    assertFalse("FooBar".matches(REGEX));
    assertFalse("/FooBar.java".matches(REGEX));
    assertFalse("Foobar!@#$%.java".matches(REGEX));
    assertFalse("Foobar.java!@#$%".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testFileOfCallerRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.FILE_OF_CALLER_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} (%1$s) <.*>: .*\\n", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 FooBar.java <TRACE>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("FooBar.java", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 .java <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 FooBar <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 /FooBar.java <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 Foobar!@#$%.java <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 Foobar.java!@#$% <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <TRACE>: hello world!\n");
    assertFalse(m.find());
  }

  @Test
  public void testRelativeTimeRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.RELATIVE_TIME_REGEX;

    assertTrue("00001234".matches(REGEX));
    assertTrue("1234567890".matches(REGEX));
    assertFalse("123FooBar456".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testRelativeTimeRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.RELATIVE_TIME_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} (%1$s) <.*>: .*\\n", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 00001234 <TRACE>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("00001234", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 1234567890 <TRACE>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("1234567890", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 123FooBar456 <TRACE>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <TRACE>: hello world!\n");
    assertFalse(m.find());
  }
  
  @Test
  public void testLevelRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.LEVEL_REGEX;
    
    assertTrue("OFF".matches(REGEX));
    assertTrue("WARN".matches(REGEX));
    assertTrue("ERROR".matches(REGEX));
    assertTrue("INFO".matches(REGEX));
    assertTrue("DEBUG".matches(REGEX));
    assertTrue("TRACE".matches(REGEX));
    assertTrue("ALL".matches(REGEX));
    assertFalse("DebuG".matches(REGEX));
    assertFalse("INFO123".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testLevelRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.LEVEL_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} <(%1$s)>: .*\\n", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 <OFF>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("OFF", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <WARN>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("WARN", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <ERROR>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("ERROR", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <INFO>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("INFO", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DEBUG>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("DEBUG", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <TRACE>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("TRACE", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <ALL>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("ALL", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DebuG>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <INFO123>: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <>: hello world!\n");
    assertFalse(m.find());
  }

  @Test
  public void testThreadRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.THREAD_NAME_REGEX;
 
    assertTrue("main".matches(REGEX));
    assertTrue("thread".matches(REGEX));
    assertTrue("thread-123".matches(REGEX));
    assertTrue("any string is okay".matches(REGEX));
    assertFalse("".matches(REGEX));
  }

  @Test
  public void testThreadRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.THREAD_NAME_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} <.*> \\[(%1$s)\\]: .*\\n", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 <DEBUG> [MyThreadName]: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("MyThreadName", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DEBUG> [any string is okay]: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("any string is okay", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DEBUG> []: hello world!\n");
    assertFalse(m.find());
  }
  
  @Test
  public void testLoggerRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.LOGGER_NAME_REGEX;
  
    assertTrue("MyLoggerName".matches(REGEX));
    assertTrue("a.b.c.foo".matches(REGEX));
    assertTrue("x.y.foo.MyClassName".matches(REGEX));
    assertTrue("any string is okay".matches(REGEX));
    assertFalse("".matches(REGEX));
  }

  @Test
  public void testLoggerRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.LOGGER_NAME_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} <.*> \\[(%1$s)\\]: .*\\n", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 <DEBUG> [MyLoggerName]: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("MyLoggerName", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DEBUG> [any string is okay]: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("any string is okay", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DEBUG> []: hello world!\n");
    assertFalse(m.find());
  }

  @Test
  public void testMessageRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.MESSAGE_REGEX;
    
    assertTrue(MSG_WITH_STACKTRACE.matches(REGEX));
    assertTrue(SAMPLEMSG.matches(REGEX));
    assertFalse("".matches(REGEX));
  }

  @Test
  public void testMessageRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.MESSAGE_REGEX;
    
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} <.*>: (%1$s) !!!\\n", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 <DEBUG>: "+ MSG_WITH_STACKTRACE + " !!!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(MSG_WITH_STACKTRACE, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DEBUG>: "+ SAMPLEMSG + " !!!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(SAMPLEMSG, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <DEBUG>: !!!\n");
    assertFalse(m.find());
  }

  @Test
  public void testClassOfCallerRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.CLASS_OF_CALLER_REGEX;

    assertTrue("MyClassName".matches(REGEX));
    assertTrue("?".matches(REGEX));
    assertFalse("a.b.c.d.MyClassName".matches(REGEX));
    assertFalse("MyClassName with spaces".matches(REGEX));
    assertFalse("MyClassName***".matches(REGEX));
    assertFalse("".matches(REGEX));
  }

  @Test
  public void testClassOfCallerRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.CLASS_OF_CALLER_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} <(%1$s)>: .*", REGEX));

    Matcher m = pattern.matcher("06/20/2012 <MyClassName>: My class name is a Java identifier\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("MyClassName", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <?>: I don't know my own class name\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("?", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <>: hello world!\n");
    assertFalse(m.find());
  }
  
  @Test
  public void testMethodOfCallerRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.METHOD_OF_CALLER_REGEX;

    assertTrue("methodName".matches(REGEX));
    assertFalse("methodName with spaces".matches(REGEX));
    assertFalse("methodName***".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
    
  @Test
  public void testMethodOfCallerRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.METHOD_OF_CALLER_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} <(%1$s)>: .*", REGEX));

    Matcher m = pattern.matcher("06/20/2012 <toString>: hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("toString", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012 <>: hello world!\n");
    assertFalse(m.find());
  }
  
  @Test
  public void testMDCRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.MDC_REGEX;

    assertTrue("key=value".matches(REGEX));
    assertTrue("k0=v0, k1=v1, k2=v2".matches(REGEX));
    assertTrue("k0=v0,k1=v1,k2=v2".matches(REGEX));
    assertFalse("key = value".matches(REGEX));
    assertFalse("k0 = v0, k1 = v1, k2 = v2".matches(REGEX));
    assertFalse("k0:v0, k1:v1, k2:v2".matches(REGEX));
    assertFalse("k:v".matches(REGEX));
    assertFalse("k0 with spaces=v0 with spaces, k1 with spaces=v1 with spaces, k2=v2".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Ignore
  @Test
  public void testMDCRegexMatchesComplexInput() {
    
    /*
     * XXX: I can't figure out how to use the MDC regex in the middle of a 
     * pattern (surrounded by other regexes). The only viable solution I can
     * think of is to treat %mdc differently from the rest. The logic will be:
     * 
     * 1. If %mdc is surrounded by other patterns, continue with special logic below. Otherwise, exit (proceed as usual).
     * 2. Split the input pattern by "%mdc", yielding two patterns (one leading up to %mdc and another that follows %mdc).
     * 3. Convert the two patterns into regex. Anchor the first regex to the beginning of the line by prefixing it with "^".
     * Anchor the second regex to the end of the line by appending "$".
     * 4. Match the line with the two regexes.
     * 5. Take the string between these two matches as the input string to be matched by the regex of %mdc.
     * 6. Convert %mdc to regex.
     * 7. Match the input string from step 5 with the %mdc regex.
     * 
     * EXCEPTION: There is no string between the two matches (perhaps because one of the split regexes consumed it).
     *   5a. 
     *   6a. 
     * 
     */
    
    final String REGEX = RegexPatterns.MDC_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4} <INFO> (%1$s) : .*", REGEX));
    
    Matcher m = pattern.matcher("06/20/2012 <INFO> key=value : hello world!");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("key=value", m.group(1).toString());
    assertEquals("key", m.group(2).toString());
    assertEquals("value", m.group(3).toString());
    
    m = pattern.matcher("06/20/2012 <INFO> k0=v0, k1=v1, k2=v2 : hello world!");
    for (int i = 0; i < 3; i++) {
      assertTrue(m.find());
      assertTrue(m.groupCount() > 0);
      assertEquals("k" + i + "=v" + i, m.group(1).toString()); // k0=v0
      assertEquals("k" + i, m.group(2).toString()); // k0
      assertEquals("v" + i, m.group(3).toString()); // v0
    }
    
    m = pattern.matcher("06/20/2012 <INFO> k0=v0,k1=v1,k2=v2 : hello world!");
    for (int i = 0; i < 3; i++) {
      assertTrue(m.find());
      assertTrue(m.groupCount() > 0);
      assertEquals("k" + i + "=v" + i, m.group(1).toString()); // k0=v0
      assertEquals("k" + i, m.group(2).toString()); // k0
      assertEquals("v" + i, m.group(3).toString()); // v0
    }
    
    m = pattern.matcher("06/20/2012 <INFO> key = value: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <INFO> k0 = v0, k1 = v1, k2 = v2 : hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <INFO> k0:v0, k1:v1, k2:v2 : hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <INFO> k:v : hello world!\n");
    assertFalse(m.find());
        
    m = pattern.matcher("06/20/2012 <INFO> k0 with spaces=v0 with spaces, k1 with spaces=v1 with spaces, k2=v2: hello world!\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012 <INFO>: hello world!\n");
    assertFalse(m.find());    
  }
  
  @Test
  public void testThrowableProxyRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.EXCEPTION_REGEX;
    
    assertTrue(STACKTRACE1.matches(REGEX));
    assertTrue(STACKTRACE2.matches(REGEX));
    assertTrue(STACKTRACE3.matches(REGEX));
    assertTrue(STACKTRACE4.matches(REGEX));
    assertFalse("java.lang.NullPointerException: Houston we have a problem".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testThrowableProxyRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.EXCEPTION_REGEX;
    
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4}: EXCEPTION - (%1$s)\\n", REGEX));

    Matcher m = pattern.matcher("06/20/2012: EXCEPTION - "+ STACKTRACE1 +"\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(STACKTRACE1, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: EXCEPTION - "+ STACKTRACE2 +"\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(STACKTRACE2, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: EXCEPTION - "+ STACKTRACE3 +"\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(STACKTRACE3, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: EXCEPTION - "+ STACKTRACE4 +"\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(STACKTRACE4, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: EXCEPTION - java.lang.NullPointerException: Houston we have a problem\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012: EXCEPTION - \n");
    assertFalse(m.find());
  }
  
  @Test
  public void testMarkerRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.MARKER_REGEX;
    
    assertTrue("markerName".matches(REGEX));
    assertTrue("parent1Marker [ child ]".matches(REGEX));
    assertTrue("parent2Marker [ child1, child2 ]".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testMarkerRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.MARKER_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4}: <(%1$s)> .*\\n", REGEX));

    Matcher m = pattern.matcher("06/20/2012: <markerName> hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("markerName", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: <parent1marker [ child ]> hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("parent1marker [ child ]", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: <parent2marker [ child1, child2 ]> hello world!\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals("parent2marker [ child1, child2 ]", m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: <> hello world!\n");
    assertFalse(m.find());
  }
  
  @Test
  public void testCallerDataRegexMatchesIsolatedInput() {
    final String REGEX = RegexPatterns.CALLER_STACKTRACE_REGEX;
    
    assertTrue(CALLER_STACKTRACE1.matches(REGEX));
    assertTrue(CALLER_STACKTRACE2.matches(REGEX));
    assertTrue(CALLER_STACKTRACE3.matches(REGEX));
    assertFalse("java.lang.NullPointerException: Houston we have a problem".matches(REGEX));
    assertFalse("".matches(REGEX));
  }
  
  @Test
  public void testCallerDataRegexMatchesComplexInput() {
    final String REGEX = RegexPatterns.CALLER_STACKTRACE_REGEX;
    Pattern pattern = Pattern.compile(String.format("\\d{2}/\\d{2}/\\d{4}: <.*> .*\\n(%1$s)\\n", REGEX));

    Matcher m = pattern.matcher("06/20/2012: <TRACE> hello world!\n"+ CALLER_STACKTRACE1 +"\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(CALLER_STACKTRACE1, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: <TRACE> hello world!\n"+ CALLER_STACKTRACE2 +"\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(CALLER_STACKTRACE2, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: <TRACE> hello world!\n"+ CALLER_STACKTRACE3 +"\n");
    assertTrue(m.find());
    assertTrue(m.groupCount() > 0);
    assertEquals(CALLER_STACKTRACE3, m.group(1).toString());
    
    m = pattern.matcher("06/20/2012: <TRACE> hello world!\njava.lang.NullPointerException: Houston we have a problem\n");
    assertFalse(m.find());
    
    m = pattern.matcher("06/20/2012: <TRACE> hello world!\n");
    assertFalse(m.find());
  }
}
