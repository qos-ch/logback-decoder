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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;

/**
 * Tests the {@link Decoder} class
 *
 * @author Anthony Trinh
 */
public class DecoderTest {
  private DecoderTestBase decoder;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    decoder = new DecoderTestBase();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testDecodeDateISO8601() throws ParseException {
    decoder.setLayoutPattern("%d %msg%n");
    final String DATE = "2012-07-13 10:15:40,224";
    ILoggingEvent event = decoder.decode(DATE + " Hello world!\n");
    assertNotNull(event);

    Date date = new SimpleDateFormat(CoreConstants.ISO8601_PATTERN).parse(DATE);
    long timestamp = date.getTime();
    assertTrue(timestamp > 0);
    assertEquals(timestamp, event.getTimeStamp());
  }

  @Test
  public void testDecodeDateWithPattern1() throws ParseException {
    final String format = "HH:mm:ssa";
    decoder.setLayoutPattern("%d{" + format + "} %msg%n");
    final String TIME_AM = "10:15:40AM";
    ILoggingEvent event = decoder.decode(TIME_AM + " Hello world!\n");
    assertNotNull(event);

    Date date = new SimpleDateFormat(format).parse(TIME_AM);
    long timestamp = date.getTime();
    assertTrue(timestamp > 0);
    assertEquals(timestamp, event.getTimeStamp());
  }

  @Test
  public void testDecodeDateWithPattern2() throws ParseException {
    final String format = "HH:mm:ssa";
    decoder.setLayoutPattern("%d{" + format + "} %msg%n");
    final String TIME_AM = "03:55:00PM";
    ILoggingEvent event = decoder.decode(TIME_AM + " Hello world!\n");
    assertNotNull(event);

    Date date = new SimpleDateFormat(format).parse(TIME_AM);
    long timestamp = date.getTime();
    assertTrue(timestamp > 0);
    assertEquals(timestamp, event.getTimeStamp());
  }

  @Test
  public void testDecodeLevel() throws ParseException {
    decoder.setLayoutPattern("%level %msg%n");
    final String LEVEL = "TRACE";
    ILoggingEvent event = decoder.decode(LEVEL + " Hello world!\n");
    assertNotNull(event);

    assertEquals(LEVEL, event.getLevel().toString());
  }

  @Ignore("not yet implemented")
  @Test
  public void testDecodeLevelShortPattern() throws ParseException {
    // FIXME: Modify LevelParser to handle the "-1" in "%.-1level".
    decoder.setLayoutPattern("%.-1level %msg%n");
    final String LEVEL = "TRACE";
    ILoggingEvent event = decoder.decode(LEVEL.charAt(0) + " Hello world!\n");
    assertNotNull(event);

    assertEquals(LEVEL, event.getLevel().toString());
  }

  @Ignore("not yet implemented")
  @Test
  public void testDecodeLoggerName() {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [main] KdbFxFeedhandlerApp: Running com.ubs.sprint.kdb.fx.feedhandler.server.KdbFxFeedhandlerApp from directory: /sbclocal/sprint/kdb-fx-feedhandler/0.0.23/bin/.\n";
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{0}: %msg%n";
    decoder.setLayoutPattern(PATT);
    ILoggingEvent event = decoder.decode(INPUT);
    assertNotNull(event);

    // FIXME: Add logger-name parser.
    assertEquals("KdbFxFeedhandlerApp", event.getLoggerName());
  }

  private class DecoderTestBase extends Decoder {}
}

