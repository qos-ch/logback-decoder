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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * 
 */
public class Decoder {
  private final Logger logger;
  
  /**
   * Constructs a DecoderBase
   */
  public Decoder() {
    logger = LoggerFactory.getLogger(getClass());
  }
  
  /**
   * Decodes a log line as an ILoggingEvent
   *
   * @param head the first FieldCapturer returned as a result
   * of pattern parsing
   * @param inputLine the log line to decode
   * @return the decoded ILoggingEvent
   */
  public ILoggingEvent decode(FieldCapturer<ILoggingEvent> head, String inputLine) {

    FieldCapturer<ILoggingEvent> fieldCapturer = head;

    // ---------- build the pattern string -----------------
    StringBuilder sb = new StringBuilder();

    while (fieldCapturer != null) {
      String partialRegex = fieldCapturer.getRegexPattern();
      if (fieldCapturer.isPlaceHolder()) {
        sb.append(partialRegex);
      } else {
        sb.append("(").append(partialRegex).append(")");
      }
      fieldCapturer = fieldCapturer.next();
    }
  
    // -------- do regex matching and capture fields --------
    String regex = sb.toString();
    Pattern pattern = Pattern.compile(regex);

    LoggingEvent event = new LoggingEvent();
    Matcher matcher = pattern.matcher(inputLine);

    if (matcher.find()) {

      int i = 0;
      while (fieldCapturer != null) {
        if (!fieldCapturer.isPlaceHolder()) {
          String fieldAsStr = matcher.group(i);
          i++;
          // much of the work is done here
          fieldCapturer.captureField(event, fieldAsStr);
        }
        fieldCapturer = fieldCapturer.next();
      }

    } else {
      logger.warn("Could not decode input line [" + inputLine + "]");
    }

    return event;
  }
  
}
