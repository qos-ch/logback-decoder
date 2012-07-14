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
import ch.qos.logback.decoder.regex.PatternLayoutRegexUtil;

/**
 * A Decoder parses information from a log string and produces an
 * ILoggingEvent as a result.
 */
public abstract class Decoder {
  private final Logger logger;
  private Pattern regexPattern;
  private String layoutPattern;
  
  /**
   * Constructs a Decoder
   */
  public Decoder() {
    logger = LoggerFactory.getLogger(Decoder.class);
  }
  
  /**
   * Sets the layout pattern used for decoding
   * 
   * @param layoutPattern the desired layout pattern
   */
  public void setLayoutPattern(String layoutPattern) {
    String regex = new PatternLayoutRegexUtil().toRegex(layoutPattern);
    regexPattern = Pattern.compile(regex);
  }
  
  /**
   * Gets the layout pattern used for decoding
   * 
   * @return the layout pattern
   */
  public String getLayoutPattern() {
    return layoutPattern; 
  }
  
  /**
   * Decodes a log line as an ILoggingEvent
   * 
   * @param inputLine the log line to decode
   * @return the decoded ILoggingEvent or <code>null</code> 
   * if line cannot be decoded
   */
  public ILoggingEvent decode(String inputLine) {

    LoggingEvent event = null;
    Matcher matcher = regexPattern.matcher(inputLine);
    
    if (matcher.find()) {
      int numMatches = matcher.groupCount();
      if (numMatches > 0) {
        event = new LoggingEvent();
        for (int i = 0; i < numMatches; i++) {
          logger.debug("{}) {}", i, matcher.group(i));
        }
      }
    }
    return event;
  }

}
