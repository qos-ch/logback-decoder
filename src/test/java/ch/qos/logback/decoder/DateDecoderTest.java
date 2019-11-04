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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
public class DateDecoderTest {

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
  public void testTimeOnlyPattern() throws Exception {
    final String TIMEZONE = "UTC";
    final String FORMAT   = "\"HH:mm:ss\"";
    final String INPUT    = "03:55:00";

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    cal.setTimeInMillis(System.currentTimeMillis());
    var year = new DecimalFormat("0000").format(cal.get(Calendar.YEAR));
    var month = new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1);  // month is 0 based, so need to add 1
    var day = new DecimalFormat("00").format(cal.get(Calendar.DAY_OF_MONTH));

    var isoFormat = year + "-" + month + "-" + day + "T03:55:00";
    var expected = ZonedDateTime.parse(isoFormat, DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC));
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT, expected);
  }

  @Test
  public void testNoYearPattern() throws Exception {
    final String TIMEZONE = "UTC";
    final String FORMAT   = "\"MMM dd HH:mm:ss\"";
    final String INPUT    = "Apr 06 16:16:50";

    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(ZoneOffset.UTC));
    cal.setTimeInMillis(System.currentTimeMillis());
    var year = new DecimalFormat("0000").format(cal.get(Calendar.YEAR));
    var expected = ZonedDateTime.parse(year + "-04-06T16:16:50", DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC));
    assertThatDateDecoded(TIMEZONE, FORMAT, INPUT, expected);
  }

  @Test
  public void testTimezone() {
    String dateString = "2018-02-28 12:00:00,000";
    Decoder decoder = new Decoder("%d");
    LocalDateTime dateTime =
        LocalDateTime.of(2018, 2, 28, 12, 0, 0, 0);

    ILoggingEvent event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.UTC).toInstant().toEpochMilli(), event.getTimeStamp());

    ZoneId tz = ZoneOffset.ofHours(8);
    decoder = new Decoder("%d", tz);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, tz).toInstant().toEpochMilli(), event.getTimeStamp());

    // verify that Daylight Saving Time is handled properly
    String summerDateString = "2018-07-08 12:00:00,000";
    LocalDateTime summerDateTime =
        LocalDateTime.of(2018, 7, 8, 12, 0, 0, 0);

    tz = ZoneId.of("America/Los_Angeles");
    decoder = new Decoder("%d", tz);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(-8)).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(summerDateString);
    assertEquals(ZonedDateTime.of(summerDateTime, ZoneOffset.ofHours(-7)).toInstant().toEpochMilli(), event.getTimeStamp());

    tz = ZoneId.of("PST", ZoneId.SHORT_IDS);
    decoder = new Decoder("%d", tz);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(-8)).toInstant().toEpochMilli(), event.getTimeStamp());
    event = decoder.decode(summerDateString);
    assertEquals(ZonedDateTime.of(summerDateTime, ZoneOffset.ofHours(-7)).toInstant().toEpochMilli(), event.getTimeStamp());

    // if timezone is specified in the pattern, then honor it.
    decoder = new Decoder("%d{\"" + CoreConstants.ISO8601_PATTERN + "\", Asia/Tokyo}");
    ZoneId jst = ZoneId.of("JST", ZoneId.SHORT_IDS);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    decoder = new Decoder("%d{\"" + CoreConstants.ISO8601_PATTERN + "\", Asia/Tokyo}", tz);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    decoder = new Decoder("%d{\"" + CoreConstants.ISO8601_PATTERN + "\", JST}");
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    decoder = new Decoder("%d{\"" + CoreConstants.ISO8601_PATTERN + "\", JST}", tz);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    decoder = new Decoder("%d{ISO8601, JST}");
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    decoder = new Decoder("%d{ISO8601, JST}", tz);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    decoder = new Decoder("%d{ISO8601,JST}");
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());
    decoder = new Decoder("%d{ISO8601,JST}", tz);
    event = decoder.decode(dateString);
    assertEquals(ZonedDateTime.of(dateTime, jst).toInstant().toEpochMilli(), event.getTimeStamp());

    // if timezone is provided in the timestamp, honor it.
    String dateStringWithTZ = "2018-02-28T12:00:00.000-0700";
    decoder = new Decoder("%d{\"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"}");
    event = decoder.decode(dateStringWithTZ);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(-7)).toInstant().toEpochMilli(), event.getTimeStamp());

    dateStringWithTZ = "2018-02-28 12:00:00.000 JST";
    decoder = new Decoder("%d{\"yyyy-MM-dd HH:mm:ss.SSS z\"}");
    event = decoder.decode(dateStringWithTZ);
    assertEquals(ZonedDateTime.of(dateTime, ZoneOffset.ofHours(9)).toInstant().toEpochMilli(), event.getTimeStamp());
  }

  private void assertThatDateDecoded(String timeZoneName, String format, String input) throws ParseException {
    assertThatDateDecoded(timeZoneName, format, input, null);
  }

  private void assertThatDateDecoded(String timeZoneName, String format, String input, ZonedDateTime expected) throws ParseException {
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

    Decoder decoder;
    if (!format.isEmpty() && !timeZoneName.isEmpty()) {
      sdf.setTimeZone(TimeZone.getTimeZone(timeZoneName));
      decoder = new Decoder("%d{" + format + ", " + timeZoneName + "} %msg%n");
    } else if (!format.isEmpty()) {
      decoder = new Decoder("%d{" + format + "} %msg%n");
    } else {
      decoder = new Decoder("%d %msg%n");
    }
    if (timeZoneName.isEmpty()) {
      sdf.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
    }

    ZonedDateTime date;
    if (expected != null) {
      date = expected;
    } else {
      date = sdf.parse(input).toInstant().atZone(ZoneOffset.UTC);
      if (date.getYear() == 1970) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        date = date.withYear(today.getYear()).withMonth(today.getMonthValue()).withDayOfMonth(today.getDayOfMonth());
      }
    }

    ILoggingEvent event = decoder.decode(input + " Hello world!\n");
    assertNotNull(event);
    assertEquals(date.toInstant().toEpochMilli(), event.getTimeStamp());
  }
}
