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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.pattern.parser2.PatternInfo;

/**
 * A {@code LevelParser} parses a log-level field from a string and populates the
 * appropriate field in a given logging event
 */
public class LevelParser implements FieldCapturer<IStaticLoggingEvent> {

  private Logger logger() {
    return LoggerFactory.getLogger(LevelParser.class);
  }

  @Override
  public void captureField(IStaticLoggingEvent event, String fieldAsStr, PatternInfo info) {

    Level level = Level.toLevel(fieldAsStr, Level.OFF);
    if (level == Level.OFF) {
      logger().warn("Unexpected log level=\"{}\". Assuming DEBUG", fieldAsStr);
      level = Level.DEBUG;
    }

    event.setLevel(level);
  }

}
