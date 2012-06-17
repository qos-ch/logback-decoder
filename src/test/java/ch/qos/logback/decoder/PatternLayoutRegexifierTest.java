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
  public void testDatePatternToRegex() {
    final String REGEX = RegexPatterns.Common.DATE_ISO8601_REGEX;
    
    for (String p : Arrays.asList("%d", "%date")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    // TODO: How do we test different locales?
    // TODO: Need to test various date patterns (%d{HH:mm:ss.SSS})
  }

  @Test
  public void testLineOfCallerPatternToRegex() {
    final String REGEX = RegexPatterns.LINE_OF_CALLER_REGEX;
    
    for (String p : Arrays.asList("%L", "%line")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testFileOfCallerPatternToRegex() {
    final String REGEX = RegexPatterns.FILE_OF_CALLER_REGEX;
    
    for (String p : Arrays.asList("%F", "%file")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }

  @Test
  public void testRelativeTimePatternToRegex() {
    final String REGEX = RegexPatterns.RELATIVE_TIME_REGEX;
    
    for (String p : Arrays.asList("%r", "%relative")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testLevelPatternToRegex() {
    final String REGEX = RegexPatterns.LEVEL_REGEX;
    
    for (String p : Arrays.asList("%le", "%level", "%p")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testThreadPatternToRegex() {
    final String REGEX = RegexPatterns.THREAD_NAME_REGEX;
    
    for (String p : Arrays.asList("%t", "%thread")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testLoggerPatternToRegex() {
    final String REGEX = RegexPatterns.LOGGER_NAME_REGEX;
    
    for (String p : Arrays.asList("%lo", "%logger", "%c")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    // TODO: Need to test for different patterns based on length specifier (%c{10})
  }
  
  @Test
  public void testMessagePatternToRegex() {
    final String REGEX = RegexPatterns.MESSAGE_REGEX;
    
    for (String p : Arrays.asList("%msg", "%message", "%m")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testClassOfCallerPatternToRegex() {
    final String REGEX = RegexPatterns.CLASS_OF_CALLER_REGEX;
    
    for (String p : Arrays.asList("%C", "%class")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
    
    // TODO: Need to test for different patterns based on length specifier (%C{10})
  }
  
  @Test
  public void testMethodOfCallerPatternToRegex() {
    final String REGEX = RegexPatterns.METHOD_OF_CALLER_REGEX;
    
    for (String p : Arrays.asList("%M", "%method")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testMDCPatternToRegex() {
    final String REGEX = RegexPatterns.MDC_REGEX;
    
    for (String p : Arrays.asList("%X", "%mdc")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testThrowableProxyPatternToRegex() {
    final String REGEX = RegexPatterns.EXCEPTION_REGEX;

    for (String p : Arrays.asList("%xEx", "%xException", "%xThrowable", "%rEx", "%rootException")) {
      regexifier.setPattern(p);
      regexifier.start();
      assertTrue(regexifier.isStarted());
      assertEquals(REGEX, regexifier.doLayout(null));
    }
  }
  
  @Test
  public void testMarkerPatternToRegex() {
    final String REGEX = RegexPatterns.MARKER_REGEX;
    
    regexifier.setPattern("%marker");
    regexifier.start();
    assertTrue(regexifier.isStarted());
    assertEquals(REGEX, regexifier.doLayout(null));
  }
  
  @Test
  public void testCallerDataPatternToRegex() {
    final String REGEX = RegexPatterns.CALLER_STACKTRACE_REGEX;
    
    regexifier.setPattern("%caller");
    regexifier.start();
    assertTrue(regexifier.isStarted());
    assertEquals(REGEX, regexifier.doLayout(null));
  }
  
  @Test
  public void testMixedPatternsToRegex() {
    final String REGEX = RegexPatterns.Common.DATE_ISO8601_REGEX + " " + 
        RegexPatterns.FILE_OF_CALLER_REGEX + ":" + 
        RegexPatterns.LINE_OF_CALLER_REGEX + " " + 
        RegexPatterns.Common.DATE_ISO8601_REGEX;
    
    String p = "%d %F:%L %d";
    regexifier.setPattern(p);
    regexifier.start();
    assertTrue(regexifier.isStarted());
    assertEquals(REGEX, regexifier.doLayout(null));
  }
}
