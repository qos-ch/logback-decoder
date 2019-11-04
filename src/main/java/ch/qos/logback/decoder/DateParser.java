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

import ch.qos.logback.core.pattern.parser2.DatePatternInfo;
import ch.qos.logback.core.pattern.parser2.PatternInfo;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

/**
 * A {@code DateParser} parses a date field from a string and populates the
 * appropriate field in a given logging event
 */
public class DateParser implements FieldCapturer<StaticLoggingEvent> {
  @Override
  public void captureField(StaticLoggingEvent event, CharSequence fieldAsStr, Offset offset, PatternInfo info) {

    if (!(info instanceof DatePatternInfo)) {
      throw new IllegalArgumentException("expected DatePatternInfo");
    }

    DatePatternInfo dpi = (DatePatternInfo)info;
    var cached = dpi.getCachedTimestamp(fieldAsStr);
    if (cached != null) {
      event.setTimeStamp(cached);
      return;
    }
    try {
      DateTimeFormatter dtf = dpi.getDateFormat();
      ZonedDateTime date = ZonedDateTime.parse(fieldAsStr, dtf);
      long ts = date.toInstant().toEpochMilli();
      event.setTimeStamp(ts);
      dpi.cacheTimestamp(fieldAsStr, ts);
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Failed to parse a date", e);
    }
  }
}
