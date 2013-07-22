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

import ch.qos.logback.classic.pattern.CallerDataConverter;
import ch.qos.logback.classic.spi.CallerData;

import java.util.regex.Pattern;

/**
 * Constant regular-expression patterns
 *
 * @author Anthony Trinh
 */
public abstract class RegexPatterns {
  abstract class Common {
    public static final String ANYTHING_REGEX = ".+?";
    public static final String NON_WHITESPACE_REGEX = "[\\S]+";
    public static final String ANYTHING_MULTILINE_REGEX = "(?s).+?";
    public static final String CSV_EQUALITIES_REGEX = "([^,\\s=]+)=([^,\\s=]+)(?:,\\s*(?:[^,\\s=]+)=(?:[^,\\s=]+))*?";

    public static final String IDENTIFIER_REGEX = "[$_a-zA-z0-9]+";
    public static final String FILENAME_REGEX = IDENTIFIER_REGEX + "\\.java";
    public static final String QUALIFIED_NAME_REGEX = IDENTIFIER_REGEX + "(\\." + IDENTIFIER_REGEX + ")*";

    public static final String INTEGER_REGEX = "-?\\d+";
    public static final String STACKTRACE_REGEX = "(?s)(.+(?:Exception|Error)[^\\n]+(?:\\s++at\\s+[^\\n]+)++)(?:\\s*\\.{3}[^\\n]++)?\\s*";
    public static final String DATE_ISO8601_REGEX = "\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2},\\d{3}";
  }

  // TODO: Move this out since it's not a regex pattern.
  // ignore last character in CallerData.CALLER_DATA_NA, which is a new-line
  static final String CALLER_DATA_NA = CallerData.CALLER_DATA_NA.substring(0, CallerData.CALLER_DATA_NA.length() - 1);

  public static final String CALLER_STACKTRACE_ELEM_REGEX =
        Pattern.quote(CallerDataConverter.DEFAULT_CALLER_LINE_PREFIX) + "\\d+\\s+at (.*)";

  public static final String CALLER_STACKTRACE_REGEX = "((?s)(?:" + CALLER_STACKTRACE_ELEM_REGEX + "))"
                      + "|(?:" + Pattern.quote(CALLER_DATA_NA) + ")";

  public static final String CLASS_OF_CALLER_REGEX = Common.QUALIFIED_NAME_REGEX + "|" + Pattern.quote(CallerData.NA);
  public static final String CONTEXT_NAME_REGEX = Common.NON_WHITESPACE_REGEX;
  public static final String FILE_OF_CALLER_REGEX = Common.FILENAME_REGEX;
  public static final String LEVEL_REGEX = "OFF|WARN|ERROR|INFO|DEBUG|TRACE|ALL";
  public static final String LINE_OF_CALLER_REGEX = Common.INTEGER_REGEX + "|\\?";
  public static final String LOGGER_NAME_REGEX = Common.ANYTHING_REGEX;
  public static final String MARKER_REGEX = Common.ANYTHING_REGEX;
  public static final String MDC_REGEX = Common.CSV_EQUALITIES_REGEX;
  public static final String METHOD_OF_CALLER_REGEX = Common.IDENTIFIER_REGEX;
  public static final String MESSAGE_REGEX = Common.ANYTHING_MULTILINE_REGEX;
  public static final String LINE_SEPARATOR_REGEX = "\\r?\\n";
  public static final String PROPERTY_REGEX = "Property_HAS_NO_KEY" + "|" + Common.ANYTHING_REGEX;
  public static final String RELATIVE_TIME_REGEX = Common.INTEGER_REGEX;
  public static final String THREAD_NAME_REGEX = Common.ANYTHING_REGEX;
  public static final String EXCEPTION_REGEX = Common.STACKTRACE_REGEX;
  public static final String ROOT_1ST_EXCEPTION_REGEX = Common.STACKTRACE_REGEX;
}
