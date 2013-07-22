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
import ch.qos.logback.core.pattern.parser2.PatternInfo;
import ch.qos.logback.core.pattern.parser2.PatternParser;
import ch.qos.logback.decoder.regex.PatternLayoutRegexUtil;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

/**
 * A {@code Decoder} parses information from a log string and produces an
 * {@link ILoggingEvent} as a result.
 */
public abstract class Decoder {
  private final Logger logger;
  private Pattern regexPattern;
  private String layoutPattern;
  private List<PatternInfo> patternInfo;

  /**
   * Constructs a {@code Decoder}
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
    if (layoutPattern != null) {
      String regex = new PatternLayoutRegexUtil().toRegex(layoutPattern);
      regexPattern = Pattern.compile(regex);
      patternInfo = PatternParser.parse(layoutPattern);
    } else {
      regexPattern = null;
      patternInfo = null;
    }
    this.layoutPattern = layoutPattern;
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
   * Decodes a log line as an {@link ILoggingEvent}
   *
   * @param inputLine the log line to decode
   * @return the decoded {@link ILoggingEvent }or {@code null}
   * if line cannot be decoded
   */
  public ILoggingEvent decode(String inputLine) {

    IStaticLoggingEvent event = null;
    Matcher matcher = regexPattern.matcher(inputLine);

    logger.trace("regex: {}", regexPattern.toString());

    if (matcher.find() && matcher.groupCount() > 0) {
      event = new StaticLoggingEvent();

      int patternIndex = 0;
      Map<String, String> groupMap = matcher.namedGroups();
      for (Entry<String, String> entry : groupMap.entrySet()) {
        String pattName = entry.getKey();
        String field = entry.getValue();

        logger.debug("{} = {}", pattName, field);

        FieldCapturer<IStaticLoggingEvent> parser = DECODER_MAP.get(pattName);
        if (parser == null) {
          logger.warn("No decoder for [{}, {}]", pattName, field);
        } else {
          parser.captureField(event, field, getPatternInfo(patternIndex, pattName));
        }

        patternIndex++;
      }
    }
    return event;
  }

  /**
   * Gets the pattern info for a sub-pattern
   *
   * @param patternIndex the index of the sub-pattern
   * @param fieldName the name of the sub-pattern
   * @return the pattern info or {@code null} if not found
   */
  private PatternInfo getPatternInfo(int patternIndex, String fieldName) {
    PatternInfo inf = patternInfo.get(patternIndex);
    if (inf != null) {

      // get the value only if the field name at this index
      // matches the given name
      String infName = PatternNames.getFullName(inf.getName());
      if (infName != null && !infName.equals(fieldName)) {
        logger.debug(
              "BUG!! Saw a field name that did not match the pattern info's " +
              "name! (index={} expected={} actual={})",
              new Object[] { patternIndex, fieldName, infName });

        inf = null;
      }
    }
    return inf;
  }

  @SuppressWarnings("serial")
  private static final Map<String, FieldCapturer<IStaticLoggingEvent>> DECODER_MAP =
    new HashMap<String, FieldCapturer<IStaticLoggingEvent>>() {{
      put(PatternNames.CALLER_STACKTRACE, new CallerStackTraceParser());
      put(PatternNames.CLASS_OF_CALLER, new ClassOfCallerParser());
      put(PatternNames.CONTEXT_NAME, new ContextNameParser());
      put(PatternNames.DATE, new DateParser());
      put(PatternNames.LEVEL, new LevelParser());
      put(PatternNames.LINE_OF_CALLER, new LineOfCallerParser());
      put(PatternNames.LOGGER_NAME, new LoggerNameParser());
      put(PatternNames.METHOD_OF_CALLER, new MethodOfCallerParser());
      put(PatternNames.MESSAGE, new MessageParser());
      put(PatternNames.THREAD_NAME, new ThreadNameParser());
    }};
}
