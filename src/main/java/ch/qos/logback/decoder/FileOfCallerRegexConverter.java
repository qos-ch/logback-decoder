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

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Converts a file-of-caller pattern into a regular expression
 */
public class FileOfCallerRegexConverter extends ClassicConverter {
  
  public String convert(ILoggingEvent le) {
    // The caller must be a Java class, and Java files match the
    // class names with the .java extension as required by the 
    // Java compiler. Therefore, the pattern is the valid
    // characters for a Java identifier plus the ".java" suffix.
    return "[$_a-zA-z0-9]+\\.java";
  }
}
