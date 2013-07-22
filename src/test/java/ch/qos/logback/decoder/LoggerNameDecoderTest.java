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

import org.junit.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Tests decoding %logger
 *
 * @author Anthony Trinh
 */
public class LoggerNameDecoderTest extends DecoderTest {

  @Test
  public void decodesNumericLoggerName() {
    assertEquals("123", getLoggerName("123"));
  }

  @Test
  public void decodesQualifiedClassNameAsLoggerName() {
    assertEquals("com.example.foo", getLoggerName("com.example.foo"));
  }

  @Test
  public void decodesSentenceAsLoggerName() {
    assertEquals("logger name can be any non-empty string", getLoggerName("logger name can be any non-empty string"));
  }

  private String getLoggerName(String name) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  <" + name + ">: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level <%logger{0}>: %msg%n";
    decoder.setLayoutPattern(PATT);
    ILoggingEvent event = decoder.decode(INPUT);
    assertNotNull(event);
    return event.getLoggerName();
  }
}
