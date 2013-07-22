/**
 * Copyright (C) 2013, QOS.ch. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Tests decoding %method
 *
 * @author Anthony Trinh
 */
public class MethodOfCallerDecoderTest extends DecoderTest {

  @Test
  public void decodesSimpleMethodName() {
    assertEquals("fooBar123", getMethodName("fooBar123"));
  }

  @Test
  public void decodesMethodNameContainingUnderscore() {
    assertEquals("foo_bar_123", getMethodName("foo_bar_123"));
  }

  @Test
  public void decodesMethodNameThatBeginsWithUnderscore() {
    assertEquals("_fooBar123", getMethodName("_fooBar123"));
  }

  // should never happen because identifiers cannot begin with a digit
  // but Decoder should be able to parse it anyway
  @Test
  public void decodesMethodNameThatBeginsWithNumber() {
    assertEquals("0fooBar123", getMethodName("0fooBar123"));
  }

  @Test
  public void noMatchWhenMethodNameHasSpaces() {
    assertNoEventWhenMethodNameIs("fooBar123 Baz");
  }

  @Test
  public void noMatchWhenMethodNameHasDots() {
    assertNoEventWhenMethodNameIs("foo.Bar.123");
  }

  @Test
  public void noMatchWhenMethodNameMissing() {
    assertNoEventWhenMethodNameIs("   ");
  }

  private void assertNoEventWhenMethodNameIs(String value) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [" + value + "]: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%method]: %msg%n";
    decoder.setLayoutPattern(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNull(event);
  }

  private String getMethodName(String methodName) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [" + methodName + "]: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%method]: %msg%n";
    decoder.setLayoutPattern(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNotNull(event);
    return event.getMethodOfCaller();
  }
}
