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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests decoding %date
 *
 * @author Anthony Trinh
 */
public class DateDecoderTest extends DecoderTest {

  @Test
  public void testOnlyWithTime() throws Exception {
    assertThatDateDecoded("", "HH:mm:ss", "12:34:56");
  }

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
    final String FORMAT   = "hh:mm:ssa";
    final String INPUT    = "10:15:40AM";
    assertThatDateDecoded("", FORMAT, INPUT);
  }

  @Test
  public void decodesDateWithSpecificDateFormatAndFullTimeZoneName() throws ParseException {
    final String TIMEZONE = "Australia/Perth";
    final String FORMAT   = "\"yyyy-MM-dd hh:mm:ss,SSSa\"";
    final String INPUT    = "2013-06-15 03:55:00,123PM";
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT);
  }

  @Test
  public void decodesDateWithSpecificDateFormatUTC() throws ParseException {
    final String TIMEZONE = "UTC";
    final String FORMAT   = "\"yyyy-MM-dd hh:mm:ss,SSSa\"";
    final String INPUT    = "2013-06-15 03:55:00,123PM";
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT);
  }

  @Test
  public void decodesDateWithSpecificDateFormatAndGeneralTimeZone() throws ParseException {
    final String TIMEZONE = "GMT-05:00";
    final String FORMAT   = "yyyy-MM-dd hh:mm:ss.SSSa";
    final String INPUT    = "2013-06-15 03:55:00.123PM";
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT);
  }

  @Test
  public void testTimezone() {
    String dateString = "2018-02-28 12:00:00,000";
    decoder.setLayoutPattern("%d");
    LocalDateTime dateTime =
        LocalDateTime.of(2018, 2, 28, 12, 0, 0, 0);

    ILoggingEvent event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.UTC).toInstant().toEpochMilli(), event.getTimeStamp());

    ZoneId tz = ZoneOffset.ofHours(8);
    event = decoder.decode(dateString, tz);
    assertEquals(ZonedDateTime.of(dateTime, tz).toInstant().toEpochMilli(), event.getTimeStamp());

    // verify that Daylight Saving Time is handled properly
    String summerDateString = "2018-07-08 12:00:00,000";
    LocalDateTime summerDateTime =
        LocalDateTime.of(2018, 7, 8, 12, 0, 0, 0);

    tz = ZoneId.of("America/Los_Angeles");
    event = decoder.decode(dateString, tz);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(-8)).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(summerDateString, tz);
    assertEquals(ZonedDateTime.of(summerDateTime, ZoneOffset.ofHours(-7)).toInstant().toEpochMilli(), event.getTimeStamp());

    tz = ZoneId.of("PST", ZoneId.SHORT_IDS);
    event = decoder.decode(dateString, tz);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(-8)).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(summerDateString, tz);
    assertEquals(ZonedDateTime.of(summerDateTime, ZoneOffset.ofHours(-7)).toInstant().toEpochMilli(), event.getTimeStamp());

    // if timezone is specified in the pattern, then honor it.
    decoder.setLayoutPattern("%d{\"" + CoreConstants.ISO8601_PATTERN + "\", Asia/Tokyo}");
    ZoneId jst = ZoneId.of("JST", ZoneId.SHORT_IDS);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(dateString, tz);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    decoder.setLayoutPattern("%d{\"" + CoreConstants.ISO8601_PATTERN + "\", JST}");
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(dateString, tz);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    decoder.setLayoutPattern("%d{ISO8601, JST}");
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(dateString, tz);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    decoder.setLayoutPattern("%d{ISO8601,JST}");
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(dateString, tz);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    // if timezone is provided in the timestamp, honor it.
    String dateStringWithTZ = "2018-02-28T12:00:00.000-0700";
    decoder.setLayoutPattern("%d{\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"}");
    event = decoder.decode(dateStringWithTZ);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(-7)).toInstant().toEpochMilli(), event.getTimeStamp());

    dateStringWithTZ = "2018-02-28 12:00:00.000 JST";
    decoder.setLayoutPattern("%d{\"yyyy-MM-dd HH:mm:ss.SSS z\"}");
    event = decoder.decode(dateStringWithTZ);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(9)).toInstant().toEpochMilli(), event.getTimeStamp());
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
    if (timeZoneName.isEmpty()) {
      sdf.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
    }


    ZonedDateTime date = sdf.parse(input).toInstant().atZone(ZoneOffset.UTC);
    if (date.getYear() == 1970) {
      LocalDate today = LocalDate.now(ZoneOffset.UTC);
      date = date.withYear(today.getYear()).withMonth(today.getMonthValue()).withDayOfMonth(today.getDayOfMonth());
    }

    ILoggingEvent event = decoder.decode(input + " Hello world!\n");
    assertNotNull(event);
    assertEquals(date.toInstant().toEpochMilli(), event.getTimeStamp());
  }
}
