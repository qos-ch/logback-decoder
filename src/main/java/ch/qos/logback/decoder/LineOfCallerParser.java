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

import ch.qos.logback.core.pattern.parser2.PatternInfo;

/**
 * A {@code LineOfCallerParser} parses a line-of-caller field (%line) from a string
 * and populates the appropriate field in a given logging event
 */
public class LineOfCallerParser implements FieldCapturer<IStaticLoggingEvent> {

  @Override
  public void captureField(IStaticLoggingEvent event, String fieldAsStr, PatternInfo info) {
    int lineNumber = 0;
    try {
      lineNumber = Integer.valueOf(fieldAsStr);
    } catch (NumberFormatException e) {
      // ignore
    }

    event.setLineNumberOfCaller(lineNumber);
  }

}
