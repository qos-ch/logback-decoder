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
package ch.qos.logback.core.pattern.parser2;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.decoder.ParserUtil;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.concurrent.ExecutionException;

/**
 * Auxiliary pattern info for a date conversion-word (%d) -- specifically the date format.
 */
public class DatePatternInfo extends PatternInfo {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatePatternInfo.class);

  public static final DateTimeFormatter ISO8601_FORMATTER =
      DateTimeFormatter.ofPattern(CoreConstants.ISO8601_PATTERN);

  private final DateTimeFormatter dateFormat;
  private final ZoneId defaultTimeZone;
  private final boolean noDateInPattern;
  private final boolean useCache;

  private final Cache<LocalDate, DateTimeFormatter> dateTimeFormatterCache =
      CacheBuilder.newBuilder().maximumSize(10).build();

  private final Cache<CharSequence, Long> timestampCache =
      CacheBuilder.newBuilder().maximumSize(1000).build();

  private static String extractTimeFormat(String format) {
    if (format == null) format = "";
    var index = format.indexOf(",");
    if (index >= 0) {
      format = format.substring(0, index);
    }
    return format;
  }

  public DatePatternInfo(String pattern, ZoneId defaultTimeZone) {
    DateTimeFormatter dtf = parseDateFormat(pattern);

    pattern = extractTimeFormat(pattern.toLowerCase());
    this.noDateInPattern = dtf != DatePatternInfo.ISO8601_FORMATTER && (!pattern.contains("d") ||
            (!pattern.contains("u") && !pattern.contains("y"))) && !pattern.contains(CoreConstants.ISO8601_STR.toLowerCase());
    if (dtf.getZone() == null  && !(pattern.contains("x") || pattern.contains("z"))) {
      // if TimeZone is not specified in the pattern format, use the one provided.
      dtf = dtf.withZone(defaultTimeZone);
    }

    // use cache iff the date time format doesn't contain milli/nano sec.
    this.useCache = dtf != DatePatternInfo.ISO8601_FORMATTER && !pattern.contains(CoreConstants.ISO8601_STR.toLowerCase())
        && !(pattern.contains("S") || pattern.contains("n") || pattern.contains("N") || pattern.contains("A"));

    this.dateFormat = dtf;
    this.defaultTimeZone = defaultTimeZone;
  }

  /**
   * Gets the date format
   * @return the date format
   */
  public DateTimeFormatter getDateFormat() {
    // If the date pattern only contains time, use the today's year/month/day when parsing the input string.
    if (noDateInPattern) {
      LocalDate today = LocalDate.now(defaultTimeZone);
      try {
        return dateTimeFormatterCache.get(today, () ->
          new DateTimeFormatterBuilder().append(dateFormat)
              .parseDefaulting(ChronoField.YEAR, today.getYear())
              .parseDefaulting(ChronoField.MONTH_OF_YEAR, today.getMonthValue())
              .parseDefaulting(ChronoField.DAY_OF_MONTH, today.getDayOfMonth())
              .toFormatter().withZone(defaultTimeZone)
        );
      } catch (ExecutionException e) {
        throw new IllegalArgumentException(e);
      }
    }

    return dateFormat;
  }

  public @Nullable Long getCachedTimestamp(CharSequence text) {
    return useCache ? timestampCache.getIfPresent(text) : null;
  }

  public void cacheTimestamp(CharSequence text, long timestamp) {
    if (useCache) {
      timestampCache.put(text, timestamp);
    }
  }

  private static DateTimeFormatter parseDateFormat(String option) {
    // default to ISO8601 if no conversion pattern given
    if (option == null || option.isEmpty() || option.equalsIgnoreCase(CoreConstants.ISO8601_STR)) {
      return DatePatternInfo.ISO8601_FORMATTER;
    }

    ZoneId tz = null;

    // Parse the last option in the conversion pattern as a time zone.
    // Make sure the comma is not escaped/quoted.
    int idx = option.lastIndexOf(",");
    if ((idx > -1)
        && (idx + 1 < option.length()
        && !ParserUtil.isEscaped(option, idx)
        && !ParserUtil.isQuoted(option, idx))) {

      // make sure the string isn't the millisecond pattern, which
      // can appear after a comma
      String tzStr = option.substring(idx + 1).trim();
      if (!tzStr.startsWith("SSS")) {
        option = option.substring(0, idx);
        tz = ZoneId.of(tzStr, ZoneId.SHORT_IDS);
        if (!tz.getId().equalsIgnoreCase(tzStr)) {
          LOGGER.warn("Time zone (\"{}\") defaulting to \"{}\".", tzStr, tz.getId());
        }
      }
    }

    // strip quotes from date format
    if (option.length() > 1 && option.startsWith("\"") && option.endsWith("\"")) {
      option = option.substring(1, option.length() - 1);
    }

    if (option.equalsIgnoreCase(CoreConstants.ISO8601_STR)) {
      option = CoreConstants.ISO8601_PATTERN;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(option);
    if (tz != null) {
      formatter = formatter.withZone(tz);
    }

    return formatter;
  }
}
