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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
    try {
      DateTimeFormatter dtf = dpi.getDateFormat();
      ZonedDateTime date = ZonedDateTime.parse(fieldAsStr, dtf);
      event.setTimeStamp(date.toInstant().toEpochMilli());
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Failed to parse a date", e);
    }
  }
}
