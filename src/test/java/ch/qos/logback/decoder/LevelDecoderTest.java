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

import java.text.ParseException;

import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Tests decoding %level
 *
 * @author Anthony Trinh
 */
public class LevelDecoderTest extends DecoderTest {

  @Test
  public void decodesLevel() throws ParseException {
    decoder.setLayoutPattern("%level %msg%n");
    final String LEVEL = "TRACE";
    ILoggingEvent event = decoder.decode(LEVEL + " Hello world!\n");
    assertNotNull(event);

    assertEquals(LEVEL, event.getLevel().toString());
  }

  @Ignore("not yet implemented")
  @Test
  public void decodesLevelShortPattern() throws ParseException {
    // FIXME: Modify LevelParser to handle the "-1" in "%.-1level".
    decoder.setLayoutPattern("%.-1level %msg%n");
    final String LEVEL = "TRACE";
    ILoggingEvent event = decoder.decode(LEVEL.charAt(0) + " Hello world!\n");
    assertNotNull(event);

    assertEquals(LEVEL, event.getLevel().toString());
  }

}
