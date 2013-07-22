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
 * Tests decoding %thread
 *
 * @author Anthony Trinh
 */
public class ThreadNameDecoderTest extends DecoderTest {
  @Test
  public void decodesNumericThreadName() {
    assertEquals("123", getThreadName("123"));
  }

  @Test
  public void decodesQualifiedClassNameAsThreadName() {
    assertEquals("com.example.foo", getThreadName("com.example.foo"));
  }

  @Test
  public void decodesLabelWithIndexAsThreadName() {
    assertEquals("Thread-0", getThreadName("Thread-0"));
  }

  @Test
  public void decodesSentenceAsThreadName() {
    assertEquals("thread name can be any non-empty string", getThreadName("thread name can be any non-empty string"));
  }

  private String getThreadName(String name) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  <" + name + ">: foo bar message\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level <%thread{0}>: %msg%n";
    decoder.setLayoutPattern(PATT);
    ILoggingEvent event = decoder.decode(INPUT);
    assertNotNull(event);
    return event.getThreadName();
  }
}
