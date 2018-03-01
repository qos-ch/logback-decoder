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
package ch.qos.logback.core.pattern.parser2;

import ch.qos.logback.core.CoreConstants;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Auxiliary pattern info for a date conversion-word (%d) -- specifically the date format.
 */
public class DatePatternInfo extends PatternInfo {
  public static final DateTimeFormatter ISO8601_FORMATTER =
      DateTimeFormatter.ofPattern(CoreConstants.ISO8601_PATTERN);

  private DateTimeFormatter dateFormat;
  private ZoneId timeZone = ZoneOffset.UTC;

  public DatePatternInfo() {
    dateFormat = ISO8601_FORMATTER;
  }

  /**
   * Gets the date format
   * @return the date format
   */
  public DateTimeFormatter getDateFormat() {
    return dateFormat;
  }

  /**
   * Sets the date format
   * @param dateFormat desired date format
   */
  public void setDateFormat(DateTimeFormatter dateFormat) {
    this.dateFormat = dateFormat;
  }

  public void setTimeZone(ZoneId timeZone) {
    this.timeZone = timeZone;
  }

  public ZoneId getTimeZone() {
    return timeZone;
  }
}
