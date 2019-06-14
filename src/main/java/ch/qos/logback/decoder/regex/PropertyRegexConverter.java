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
import java.util.regex.Pattern;

import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.decoder.PatternNames;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Converts a property pattern into a regular expression
 */
public class PropertyRegexConverter extends DynamicConverter<InputStream> {
  private static final Pattern NON_ALNUM = Pattern.compile("[^a-zA-Z0-9]");

  private String key = null;

  @Override
  public void start() {
    super.start();
    key = getFirstOption();

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
      return "(?<" + PatternNames.PROPERTY + ">" + RegexPatterns.PROPERTY_REGEX_NO_KEY + ")";
    } else {
      return "(?<" + PatternNames.PROPERTY_PREFIX + key + ">" + RegexPatterns.PROPERTY_REGEX + ")";
    }
  }
}
