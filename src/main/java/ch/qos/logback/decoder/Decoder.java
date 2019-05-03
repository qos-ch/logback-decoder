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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.parser2.PatternInfo;
import ch.qos.logback.core.pattern.parser2.PatternParser;
import ch.qos.logback.decoder.regex.PatternLayoutRegexUtil;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@code Decoder} parses information from a log string and produces an
 * {@link ILoggingEvent} as a result.
 */
public class Decoder {
  private static final Pattern NAMED_GROUP = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

  private final Pattern regexPattern;
  private final List<String> namedGroups;
  private final String layoutPattern;
  private final List<PatternInfo> patternInfo;
  private final List<FieldCapturer<StaticLoggingEvent>> parsers;

  /**
   * Constructs a {@code Decoder}
   */
  public Decoder(String layoutPattern) {
    this(layoutPattern, ZoneOffset.UTC);
  }

  public Decoder(String layoutPattern, ZoneId defaultTimeZone) {
    if (layoutPattern == null) {
      throw new IllegalArgumentException("layoutPattern cannot be null");
    }

    this.layoutPattern = layoutPattern;

    // ignore the last newline %n
    if (layoutPattern.endsWith("%n")) {
      layoutPattern = layoutPattern.substring(0, layoutPattern.length() - 2);
    }

    PatternLayoutRegexUtil util = new PatternLayoutRegexUtil();
    String regex = util.toRegex(layoutPattern) + "$";
    this.regexPattern = Pattern.compile(regex);

    namedGroups = new ArrayList<>();
    Matcher matcher = NAMED_GROUP.matcher(regex);
    while (matcher.find()) {
      namedGroups.add(matcher.group(1));
    }
    patternInfo = PatternParser.parse(layoutPattern, defaultTimeZone);

    // only use patternInfo whose name matches names in namedGroups
    for (int i = 0; i < namedGroups.size(); i++) {
      if (namedGroups.get(i).startsWith(PatternNames.MDC_PREFIX)) {
        continue;
      }
      if (!Objects.equals(namedGroups.get(i), patternInfo.get(i).getName())) {
        throw new IllegalArgumentException(String.format(
            "BUG!! Saw a field name that did not match the pattern info's name! (index={} expected={} actual={})",
            i, namedGroups.get(i), patternInfo.get(i).getName()));
      }
    }

    Map<String, String> mdcKeyMap = util.getProperties();
    parsers = new ArrayList<>();
    for (String pattName: namedGroups) {
      if (pattName.startsWith(PatternNames.MDC_PREFIX)) {
        String key = pattName.substring(PatternNames.MDC_PREFIX.length());
        key = mdcKeyMap.getOrDefault(key, key);
        parsers.add(new MDCValueParser(key));
      } else {
        FieldCapturer<StaticLoggingEvent> parser = DECODER_MAP.get(pattName);
        if (parser == null) {
          throw new IllegalArgumentException("No parser found for " + pattName);
        }
        parsers.add(parser);
      }
    }
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
  public ILoggingEvent decode(CharSequence inputLine) {
    StaticLoggingEvent event = null;
    Matcher matcher = regexPattern.matcher(inputLine);

    if (matcher.matches() && matcher.groupCount() > 0) {
      event = new StaticLoggingEvent();

      int patternIndex = 0;
      for (String pattName: namedGroups) {
        Offset offset = new Offset(matcher.start(pattName), matcher.end(pattName));

        FieldCapturer<StaticLoggingEvent> parser = parsers.get(patternIndex);
        PatternInfo inf = patternInfo.get(patternIndex);
        parser.captureField(event, inputLine.subSequence(offset.start, offset.end), offset, inf);

        patternIndex++;
      }
    }

    return event;
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
      put(PatternNames.MDC, new MDCMapParser());
    }};
}
