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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Auxiliary pattern info for a date conversion-word (%d) -- specifically the date format.
 */
public class DatePatternInfo extends PatternInfo {
  private DateFormat dateFormat;

  public DatePatternInfo() {
    dateFormat = SimpleDateFormat.getDateInstance();
  }

  /**
   * Gets the date format
   * @return the date format
   */
  public DateFormat getDateFormat() {
    return dateFormat;
  }

  /**
   * Sets the date format
   * @param dateFormat desired date format
   */
  public void setDateFormat(DateFormat dateFormat) {
    this.dateFormat = dateFormat;
  }
}
