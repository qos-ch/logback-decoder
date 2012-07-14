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
package ch.qos.logback.decoder.regex;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.util.DatePatternToRegexUtil;
import ch.qos.logback.decoder.PatternNames;

/**
 * Converts a date pattern into a regular expression
 */
public class DateRegexConverter extends DynamicConverter<String> {
  private String datePattern = null;
  
  public void start() {
    datePattern = getFirstOption();
    if (datePattern == null) {
      datePattern = CoreConstants.ISO8601_PATTERN;
    }

    if (datePattern.equals(CoreConstants.ISO8601_STR)) {
      datePattern = CoreConstants.ISO8601_PATTERN;
    }
  }

  public String convert(String s) {
    return "(?<" + PatternNames.DATE + ">" + new DatePatternToRegexUtil(datePattern).toRegex() + ")";
  }
}
