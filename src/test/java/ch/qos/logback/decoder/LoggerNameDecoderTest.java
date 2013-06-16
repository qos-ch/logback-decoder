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

import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Tests decoding %logger
 *
 * @author Anthony Trinh
 */
public class LoggerNameDecoderTest extends DecoderTest {

  @Ignore("not yet implemented")
  @Test
  public void decodesLoggerName() {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [main] KdbFxFeedhandlerApp: Running com.ubs.sprint.kdb.fx.feedhandler.server.KdbFxFeedhandlerApp from directory: /sbclocal/sprint/kdb-fx-feedhandler/0.0.23/bin/.\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{0}: %msg%n";
    decoder.setLayoutPattern(PATT);
    ILoggingEvent event = decoder.decode(INPUT);
    assertNotNull(event);

    // FIXME: Add logger-name parser.
    assertEquals("KdbFxFeedhandlerApp", event.getLoggerName());
  }
}
