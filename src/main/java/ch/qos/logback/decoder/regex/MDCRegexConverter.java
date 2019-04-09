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

import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.decoder.PatternNames;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Converts a MDC pattern into a regular expression
 */
public class MDCRegexConverter extends DynamicConverter<InputStream> {
  private static final Pattern NON_ALNUM = Pattern.compile("[^a-zA-Z0-9]");

  private String key = null;

  @Override
  public void start() {
    key = getFirstOption();
    if (key != null && key.indexOf(":-") > 0) {
      key = key.substring(0, key.indexOf(":-"));
    }

    // if key contains non alnum char, use random generated key instead.
    if (key != null && NON_ALNUM.matcher(key).find()) {
      String newKey;
      do {
        newKey = "KEY" + RandomStringUtils.randomAlphanumeric(5);
      } while (getContext().getProperty(newKey) != null);
      getContext().putProperty(newKey, key);
      key = newKey;
    }
  }

  @Override
  public String convert(InputStream le) {
    if (key == null) {
      return "(?<" + PatternNames.MDC + ">" + RegexPatterns.MDC_REGEX + ")";
    } else {
      return "(?<" + PatternNames.MDC_PREFIX + key + ">" + RegexPatterns.Common.ANYTHING_OR_EMPTY_REGEX + ")";
    }
  }
}
