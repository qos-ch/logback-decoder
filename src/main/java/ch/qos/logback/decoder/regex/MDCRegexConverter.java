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

import java.io.InputStream;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.decoder.PatternNames;

/**
 * Converts a MDC pattern into a regular expression
 */
public class MDCRegexConverter extends DynamicConverter<InputStream> {
  private String key = null;

  @Override
  public void start() {
    key = getFirstOption();
    if (key != null && key.indexOf(":-") > 0) {
      key = key.substring(0, key.indexOf(":-"));
    }
  }

  @Override
  public String convert(InputStream le) {
    if (key == null) {
      return "(?<" + PatternNames.MDC + ">" + RegexPatterns.MDC_REGEX + ")";
    } else {
      return "(?<" + PatternNames.MDC_PREFIX + key + ">" + RegexPatterns.Common.NON_WHITESPACE_OR_EMPTY_REGEX + ")";
    }
  }
}
