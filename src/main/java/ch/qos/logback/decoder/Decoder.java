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

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.core.pattern.parser2.DatePatternInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.parser2.PatternInfo;
import ch.qos.logback.core.pattern.parser2.PatternParser;
import ch.qos.logback.decoder.regex.PatternLayoutRegexUtil;

/**
 * A {@code Decoder} parses information from a log string and produces an
 * {@link ILoggingEvent} as a result.
 */
public abstract class Decoder {
  private static final Pattern NAMED_GROUP = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

  private final Logger logger;

  private Pattern regexPattern;
  private List<String> namedGroups;
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
      String regex = new PatternLayoutRegexUtil().toRegex(layoutPattern) + "$";
      regexPattern = Pattern.compile(regex);
      namedGroups = new ArrayList<>();
      Matcher matcher = NAMED_GROUP.matcher(regex);
      while (matcher.find()) {
        namedGroups.add(matcher.group(1));
      }
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
    return decode(inputLine, ZoneOffset.UTC);
  }

  public ILoggingEvent decode(String inputLine, ZoneId timeZone) {
    StaticLoggingEvent event = null;
    Matcher matcher = regexPattern.matcher(inputLine);
    Map<String, String> mdcProperties = new HashMap<String, String>();
    Map<String, Offset> mdcPropertyOffsets = new HashMap<String, Offset>();

    logger.trace("regex: {}", regexPattern.toString());

    if (matcher.find() && matcher.groupCount() > 0) {
      event = new StaticLoggingEvent();

      int patternIndex = 0;
      for (String pattName: namedGroups) {
        String field = matcher.group(pattName);
        Offset offset = new Offset(matcher.start(pattName), matcher.end(pattName));

        logger.debug("{} = {}", pattName, field);

        if (PatternNames.MDC.equals(pattName)) {
          // value is CSV. Convert it into Map.
          int startOffset = offset.start;
          for (String kv : field.split(",")) {
            String[] keyValue = kv.split("=");
            if (keyValue.length == 2) {
              String key = keyValue[0].trim();
              mdcProperties.put(key, keyValue[1]);
              mdcPropertyOffsets.put(key,
                  new Offset(startOffset + keyValue[0].length() + 1, startOffset + kv.length()));
            } else {
              logger.warn("Cannot parse {} in {}", kv, field);
            }
            startOffset += kv.length() + 1;
          }
        } else if (pattName.startsWith(PatternNames.MDC_PREFIX)) {
          if (!field.isEmpty()) {
            String key = pattName.substring(PatternNames.MDC_PREFIX.length());
            mdcProperties.put(key, field);
            mdcPropertyOffsets.put(key, offset);
          } else {
            logger.debug("empty field for {}", pattName);
          }
        } else {
          FieldCapturer<StaticLoggingEvent> parser = DECODER_MAP.get(pattName);
          if (parser == null) {
            logger.warn("No decoder for [{}, {}]", pattName, field);
          } else {
            parser.captureField(event, field, offset, getPatternInfo(patternIndex, pattName, timeZone));
          }
        }

        patternIndex++;
      }
    }

    if (!mdcProperties.isEmpty()) {
      event.setMDCPropertyMap(mdcProperties);
      event.mdcPropertyOffsets = mdcPropertyOffsets;
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
  private PatternInfo getPatternInfo(int patternIndex, String fieldName, ZoneId timeZone) {
    PatternInfo inf = patternInfo.get(patternIndex);
    if (inf != null) {

      // get the value only if the field name at this index
      // matches the given name
      String infName = PatternNames.getFullName(inf.getName());
      if (infName != null && !infName.equals(fieldName)) {
        logger.error(
              "BUG!! Saw a field name that did not match the pattern info's " +
              "name! (index={} expected={} actual={})",
              new Object[] { patternIndex, fieldName, infName });

        inf = null;
      }

      if (inf instanceof DatePatternInfo) {
        ((DatePatternInfo) inf).setTimeZone(timeZone);
      }
    }
    return inf;
  }

  @SuppressWarnings("serial")
  private static final Map<String, FieldCapturer<StaticLoggingEvent>> DECODER_MAP =
    new HashMap<String, FieldCapturer<StaticLoggingEvent>>() {{
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
