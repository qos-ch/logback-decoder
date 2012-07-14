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

import java.util.HashMap;
import java.util.List;
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
  private List<LayoutPatternInfo> patternInfo;
  
  /**
   * Constructs a Decoder
   */
  protected Decoder() {
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
    patternInfo = LayoutPatternParser.parse(layoutPattern);
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
    
    if (matcher.find() && matcher.groupCount() > 0) {
      event = new LoggingEvent();
      
      int patternIndex = 0;
      Map<String, String> groupMap = matcher.namedGroups();
      for (Entry<String, String> entry : groupMap.entrySet()) {
        String pattName = entry.getKey();
        String field = entry.getValue();
        
        logger.debug("{} = {}", pattName, field);
        
        FieldCapturer<ILoggingEvent> parser = DECODER_MAP.get(pattName);
        if (parser == null) {
          logger.warn("No decoder for [{}, {}]", pattName, field);
        } else {
          parser.captureField(event, field, getConversionModifier(patternIndex, pattName));
        }
        
        patternIndex++;
      }
    }
    return event;
  }

  /**
   * Gets the conversion modifier for a pattern
   * 
   * @param patternIndex the index
   * @param fieldName the name of the pattern (it better match)
   * @return the conversion modifier or <code>null</code> if not found
   */
  private String getConversionModifier(int patternIndex, String fieldName) {
    String convPattern = null;
    LayoutPatternInfo inf = patternInfo.get(patternIndex);
    if (inf != null) {
      
      // get the value only if the field name at this index
      // matches the given name
      String infName = PatternNames.getFullName(inf.getName());
      if (infName != null && !infName.equals(fieldName)) {
        logger.debug("BUG!! Saw a field name that did not match the pattern info's name! (index={} expected={} actual={})",
            new Object[] { patternIndex, fieldName, infName });
      } else {
        convPattern = inf.getConversionModifier();
      }
    }
    return convPattern;
  }
  
  @SuppressWarnings("serial")
  private static final Map<String, FieldCapturer<ILoggingEvent>> DECODER_MAP =
    new HashMap<String, FieldCapturer<ILoggingEvent>>() {{
      put(PatternNames.DATE, new DateParser());
    }};
}
