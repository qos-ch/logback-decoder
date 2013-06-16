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
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;

/**
 * Tests decoding %date
 *
 * @author Anthony Trinh
 */
public class DateDecoderTest extends DecoderTest {

  @Test
  public void decodesDateISO8601Pattern() throws ParseException {
    // ISO8601 has comma for decimal point so we need to quote it for
    // the date option to prevent it from being parsed as a time zone
    final String FORMAT   = "\"" + CoreConstants.ISO8601_PATTERN + "\"";
    final String INPUT    = "2012-07-13 10:15:40,224";
    assertThatDateDecoded("", FORMAT, INPUT);
  }

  @Test
  public void decodesDateISO8601Name() throws ParseException {
    final String FORMAT   = CoreConstants.ISO8601_STR;
    final String INPUT    = "2012-07-13 10:15:40,224";
    assertThatDateDecoded("", FORMAT, INPUT);
  }

  @Test
  public void decodesDateISO8601WhenBlank() throws ParseException {
    final String INPUT    = "2012-07-13 10:15:40,224";
    assertThatDateDecoded("", "", INPUT);
  }

  @Test
  public void decodesDateWithSpecificDateFormat() throws ParseException {
    final String FORMAT   = "HH:mm:ssa";
    final String INPUT    = "10:15:40AM";
    assertThatDateDecoded("", FORMAT, INPUT);
  }

  @Test
  public void decodesDateWithSpecificDateFormatAndFullTimeZoneName() throws ParseException {
    final String TIMEZONE = "Australia/Perth";
    final String FORMAT   = "\"yyyy-MM-dd HH:mm:ss,SSSa\"";
    final String INPUT    = "2013-06-15 03:55:00,123PM";
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT);
  }

  @Test
  public void decodesDateWithSpecificDateFormatUTC() throws ParseException {
    final String TIMEZONE = "UTC";
    final String FORMAT   = "\"yyyy-MM-dd HH:mm:ss,SSSa\"";
    final String INPUT    = "2013-06-15 03:55:00,123PM";
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT);
  }

  @Test
  public void decodesDateWithSpecificDateFormatAndGeneralTimeZone() throws ParseException {
    final String TIMEZONE = "GMT-05:00";
    final String FORMAT   = "yyyy-MM-dd HH:mm:ss.SSSa";
    final String INPUT    = "2013-06-15 03:55:00.123PM";
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT);
  }

  private void assertThatDateDecoded(String timeZoneName, String format, String input) throws ParseException {
    if (format == null) format = "";
    if (timeZoneName == null) timeZoneName = "";

    // Strip quotes from format because SimpleDateFormat doesn't understand them.
    // We still need it quoted for the layout pattern set below.
    String formatClean = format;
    if (format.length() > 1 && format.startsWith("\"") && format.endsWith("\"")) {
      formatClean = format.substring(1, format.length() - 1);
    }

    // use the default ISO8601 when format is blank or "ISO8601"
    if (formatClean.isEmpty() || formatClean.equals(CoreConstants.ISO8601_STR)) {
      formatClean = CoreConstants.ISO8601_PATTERN;
    }

    SimpleDateFormat sdf = formatClean.isEmpty()
                           ? new SimpleDateFormat()
                           : new SimpleDateFormat(formatClean);

    if (!format.isEmpty() && !timeZoneName.isEmpty()) {
      sdf.setTimeZone(TimeZone.getTimeZone(timeZoneName));
      decoder.setLayoutPattern("%d{" + format + ", " + timeZoneName + "} %msg%n");
    } else if (!format.isEmpty()) {
      decoder.setLayoutPattern("%d{" + format + "} %msg%n");
    } else {
      decoder.setLayoutPattern("%d %msg%n");
    }

    ILoggingEvent event = decoder.decode(input + " Hello world!\n");
    assertNotNull(event);
    assertEquals(sdf.parse(input).getTime(), event.getTimeStamp());
  }
}
