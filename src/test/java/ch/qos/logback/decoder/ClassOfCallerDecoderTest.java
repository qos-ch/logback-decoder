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
 * Tests decoding %class
 *
 * @author Anthony Trinh
 */
public class ClassOfCallerDecoderTest extends DecoderTest {

  @Test
  public void decodesSimpleClassName() {
    assertEquals("FooBar", getClassName("FooBar"));
  }

  @Test
  public void decodesClassNameContainingUnderscore() {
    assertEquals("Foo_Bar", getClassName("Foo_Bar"));
  }

  @Test
  public void decodesQualifiedClassName() {
    assertEquals("com.example.FooBar", getClassName("com.example.FooBar"));
  }

  @Test
  public void decodesClassNameThatBeginsWithUnderscore() {
    assertEquals("_fooBar", getClassName("_fooBar"));
  }

  @Test
  public void decodesQualifiedClassNameThatBeginsWithUnderscore() {
    assertEquals("com.example._fooBar", getClassName("com.example._fooBar"));
  }

  // should never happen because identifiers cannot begin with a digit
  // but Decoder should be able to parse it anyway
  @Test
  public void decodesClassNameThatBeginsWithNumber() {
    assertEquals("0FooBar123", getClassName("0FooBar123"));
  }

  @Test
  public void noMatchWhenClassNameHasSpaces() {
    assertNoEventWhenClassNameIs("FooBar123 Baz");
  }

  @Test
  public void noMatchWhenClassNameMissing() {
    assertNoEventWhenClassNameIs("   ");
  }

  private void assertNoEventWhenClassNameIs(String value) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [" + value + "]: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%class]: %msg%n";
    decoder.setLayoutPattern(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNull(event);
  }

  private String getClassName(String className) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [" + className + "]: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%class]: %msg%n";
    decoder.setLayoutPattern(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNotNull(event);
    return event.getClassNameOfCaller();
  }
}
