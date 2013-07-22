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
 * Tests decoding %contextName
 *
 * @author Anthony Trinh
 */
public class ContextNameDecoderTest extends DecoderTest {
  @Test
  public void decodesNumericContextName() {
    assertEquals("123", getContextName("123"));
  }

  @Test
  public void decodesQualifiedClassNameAsContextName() {
    assertEquals("com.example.foo", getContextName("com.example.foo"));
  }

  @Test
  public void noMatchWhenContextNameHasSpaces() {
    assertNoEventWhenContextNameIs("com.example.foo has spaces");
  }

  @Test
  public void noMatchWhenContextNameMissing() {
    assertNoEventWhenContextNameIs("   ");
  }

  private void assertNoEventWhenContextNameIs(String value) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  <" + value + ">: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level <%contextName>: %msg%n";
    decoder.setLayoutPattern(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNull(event);
  }

  private String getContextName(String name) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  <" + name + ">: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level <%contextName>: %msg%n";
    decoder.setLayoutPattern(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNotNull(event);
    return event.getContextName();
  }
}
