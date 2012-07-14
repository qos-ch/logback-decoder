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
package ch.qos.logback.decoder.regex;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Tests the {@link PatternLayoutRegexUtil} class
 * 
 * @author Anthony Trinh
 */
public class PatternLayoutRegexUtilTest {
  private PatternLayoutRegexUtil regexifier;
  static private ContextBase context;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    context = new ContextBase();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    regexifier = new PatternLayoutRegexUtil(context);
  }

  @After
  public void tearDown() throws Exception {
    StatusPrinter.printInCaseOfErrorsOrWarnings(context);
  }

  @Test
  public void testDatePatternToRegex() {
    final String REGEX = "(" + RegexPatterns.Common.DATE_ISO8601_REGEX + ")";
    
    for (String p : Arrays.asList("%d", "%date")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
    
    // TODO: How do we test different locales?
    // TODO: Need to test various date patterns (%d{HH:mm:ss.SSS})
  }

  @Test
  public void testLineOfCallerPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.LINE_OF_CALLER_REGEX + ")";
    
    for (String p : Arrays.asList("%L", "%line")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testFileOfCallerPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.FILE_OF_CALLER_REGEX + ")";
    
    for (String p : Arrays.asList("%F", "%file")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }

  @Test
  public void testRelativeTimePatternToRegex() {
    final String REGEX = "(" + RegexPatterns.RELATIVE_TIME_REGEX + ")";
    
    for (String p : Arrays.asList("%r", "%relative")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testLevelPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.LEVEL_REGEX + ")";
    
    for (String p : Arrays.asList("%le", "%level", "%p")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testThreadPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.THREAD_NAME_REGEX + ")";
    
    for (String p : Arrays.asList("%t", "%thread")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testLoggerPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.LOGGER_NAME_REGEX + ")";
    
    for (String p : Arrays.asList("%lo", "%logger", "%c")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
    
    // TODO: Need to test for different patterns based on length specifier (%c{10})
  }
  
  @Test
  public void testMessagePatternToRegex() {
    final String REGEX = "(" + RegexPatterns.MESSAGE_REGEX + ")";
    
    for (String p : Arrays.asList("%msg", "%message", "%m")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testClassOfCallerPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.CLASS_OF_CALLER_REGEX + ")";
    
    for (String p : Arrays.asList("%C", "%class")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
    
    // TODO: Need to test for different patterns based on length specifier (%C{10})
  }
  
  @Test
  public void testMethodOfCallerPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.METHOD_OF_CALLER_REGEX + ")";
    
    for (String p : Arrays.asList("%M", "%method")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testMDCPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.MDC_REGEX + ")";
    
    for (String p : Arrays.asList("%X", "%mdc")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testThrowableProxyPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.EXCEPTION_REGEX + ")";

    for (String p : Arrays.asList("%xEx", "%xException", "%xThrowable", "%rEx", "%rootException")) {
      assertEquals(REGEX, regexifier.toRegex(p));
    }
  }
  
  @Test
  public void testMarkerPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.MARKER_REGEX + ")";
    
    assertEquals(REGEX, regexifier.toRegex("%marker"));
  }
  
  @Test
  public void testCallerDataPatternToRegex() {
    final String REGEX = "(" + RegexPatterns.CALLER_STACKTRACE_REGEX + ")";
    
    assertEquals(REGEX, regexifier.toRegex("%caller"));
  }
  
  @Test
  public void testMixedPatternsToRegex() {
    final String REGEX = "(" + RegexPatterns.Common.DATE_ISO8601_REGEX + ") " + 
        "(" + RegexPatterns.FILE_OF_CALLER_REGEX + "):" + 
        "(" + RegexPatterns.LINE_OF_CALLER_REGEX + ") " + 
        "(" + RegexPatterns.Common.DATE_ISO8601_REGEX + ")";
    
    assertEquals(REGEX, regexifier.toRegex("%d %F:%L %d"));
  }
}
