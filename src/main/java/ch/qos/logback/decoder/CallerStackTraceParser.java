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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

import ch.qos.logback.core.pattern.parser2.PatternInfo;
import ch.qos.logback.decoder.regex.RegexPatterns;

/**
 * A {@code CallerStackTraceParser} parses a caller-stacktrace field (%caller) from a string
 * and populates the appropriate field in a given logging event
 */
public class CallerStackTraceParser implements FieldCapturer<IStaticLoggingEvent> {
  private static final Pattern PATTERN = Pattern.compile(RegexPatterns.CALLER_STACKTRACE_ELEM_REGEX);
  private static final Pattern PATTERN2 = Pattern.compile("(?<class>" + RegexPatterns.CLASS_OF_CALLER_REGEX + ")"
      + "(\\("
          + "(?<file>"+ RegexPatterns.FILE_OF_CALLER_REGEX + ")"
          + ":(?<line>"+ RegexPatterns.LINE_OF_CALLER_REGEX + ")"
      + "\\))?"
      );

  @Override
  public void captureField(IStaticLoggingEvent event, String fieldAsStr, PatternInfo info) {

    List<StackTraceElement> stackTrace = new ArrayList<StackTraceElement>();
    Matcher m = PATTERN.matcher(fieldAsStr);
    while (m.find()) {
      String line = m.group(1);

      Matcher m2 = PATTERN2.matcher(line);
      if (!m2.find()) {
        continue;
      }

      String className = StringUtils.defaultString(m2.group("class"));
      String fileName = StringUtils.defaultString(m2.group("file"));
      String lineNumberStr = StringUtils.defaultString(m2.group("line"));

      // parse line number from string
      int lineNumber = 0;
      if (lineNumberStr.length() > 0) {
        try {
          lineNumber = Integer.valueOf(lineNumberStr);
        } catch (NumberFormatException e) {
          // ignore
        }
      }

      // parse method name from classname field
      int pos = className.lastIndexOf('.');
      String methodName;
      if (pos > -1) {
        methodName = className.substring(className.lastIndexOf('.') + 1);
        className = className.substring(0, pos);
      } else {
        methodName = className;
        className = "";
      }

      stackTrace.add(new StackTraceElement(className, methodName, fileName, lineNumber));
    }

    event.setCallerStackData(stackTrace);
  }

}
