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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.decoder.regex.PatternLayoutRegexUtil;

import com.google.code.regexp.NamedMatcher;
import com.google.code.regexp.NamedPattern;

/**
 * A Decoder parses information from a log string and produces an
 * ILoggingEvent as a result.
 */
public abstract class Decoder {
  private final Logger logger;
  private NamedPattern regexPattern;
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
    regexPattern = NamedPattern.compile(regex);
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
    NamedMatcher matcher = regexPattern.matcher(inputLine);
    
    if (matcher.find()) {
      int numMatches = matcher.groupCount();
      if (numMatches > 0) {
        
        event = new LoggingEvent();
        Map<String, String> groupMap = namedGroups(regexPattern, matcher);
        for (Entry<String, String> entry : groupMap.entrySet()) {
          logger.debug("{} = {}", entry.getKey(), entry.getValue());
        }
      }
    }
    return event;
  }

  /**
   * Gets a map of the regex group names and their values from a named
   * pattern. This is a workaround for an IndexOutOfBoundsException
   * from the named-regexp library when the number of the named 
   * pattern's group names are less than the underlying group count.
   * 
   * @param p named pattern
   * @param m named matcher
   * @return the map of group names and their values
   */
  private Map<String, String> namedGroups(NamedPattern p, NamedMatcher m) {
    Map<String, String> result = new LinkedHashMap<String, String>();

    int groupCount = Math.min(m.groupCount(), p.groupNames().size());
    for (int i = 1; i <= groupCount; i++) {
        String groupName = p.groupNames().get(i-1);
        String groupValue = m.group(i);
        result.put(groupName, groupValue);
    }

    return result;
  }
}
