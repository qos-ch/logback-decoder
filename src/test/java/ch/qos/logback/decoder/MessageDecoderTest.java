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

/**
 * Tests decoding %message
 *
 * @author Anthony Trinh
 */
public class MessageDecoderTest extends DecoderTest {
  @Test
  public void decodesNumericMessage() {
    assertEquals("123", getMessage("123"));
  }

  @Test
  public void decodesSimpleMessage() {
    assertEquals("This is just a test", getMessage("This is just a test"));
  }

  private String getMessage(String message) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO: " + message + "\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level: %msg%n";
    decoder.setLayoutPattern(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNotNull(event);
    return event.getMessage();
  }
}
