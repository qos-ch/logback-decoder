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
package ch.qos.logback.decoder.regex;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Utility to convert a layout pattern into a regular expression
 * 
 * @author Anthony Trinh
 */
public class PatternLayoutRegexUtil {
  private PatternLayoutRegexHelper converter;
  
  /**
   * Constructs a PatternLayoutRegexUtil with a new converter
   * and a default context base
   */
  public PatternLayoutRegexUtil() {
    this(new ContextBase());
  }
  
  /**
   * Constructs a PatternLayoutRegexUtil with a new converter
   * and specified context
   * 
   * @param context desired context of underlying converter
   */
  public PatternLayoutRegexUtil(ContextBase context) {
    converter = new PatternLayoutRegexHelper();
    converter.setContext(context);
  }

  /** 
   * This is an escape sequence of an unlikely string, used to 
   * escape regex chars in the layout pattern. We can't use a 
   * backslash since the pattern-layout converter complains 
   * about it. This special escape sequence is converted to 
   * a backslash after we pass it through the pattern-layout 
   * converter. 
   */
  static private final String ESC_SEQ = "@&#@esc__";
  static private int ESC_SEQ_LEN = ESC_SEQ.length();
  
  /**
   * Escapes regex characters in a string. This is used to escape
   * any regex special chars in a layout pattern, which could be
   * unintentionally interpreted by the Java regex engine. 
   * 
   * @param input string to be escaped
   * @return string containing regex characters
   * special characters, escaped with a backslash
   */
  private String escapeRegexChars(String input) {
    Pattern regex = Pattern.compile("([\\[\\]?.+*$(){}])");
    Matcher matcher = regex.matcher(input);
    StringBuffer s = new StringBuffer(input);
    int numNewChars = 0;
    while (matcher.find()) {
      s.insert(matcher.start() + numNewChars, ESC_SEQ);
      numNewChars += ESC_SEQ_LEN;
    }
    return s.toString();
  }
  
  /**
   * Replaces the escape sequence in a string from {@link #escapeRegexChars(String)}
   * with backslashes.
   * 
   * @param input string containing {@link #ESC_SEQ}
   * @return updated string without {@link #ESC_SEQ}
   */
  private String slashEscapeSequence(String input) {
    return input.replace(ESC_SEQ, "\\");
  }
  
  /**
   * Converts a layout pattern to a regular expression pattern
   * 
   * @param layoutPattern the layout pattern to be evaluated
   * @return the pattern with the log-layout patterns replaced with equivalent regexes 
   */
  public String toRegex(String layoutPattern) {
    converter.setPattern(escapeRegexChars(layoutPattern));
    if (!converter.isStarted()) {
      converter.start();
      StatusPrinter.printIfErrorsOccured(converter.getContext());
    }
    return slashEscapeSequence(converter.doLayout(null));
  }
}

/**
 * Internal helper class that uses the converter logic of {@link PatternLayoutBase}
 * to perform the layout-pattern-to-regex conversion. 
 * 
 * TODO: It might actually be simpler to search and replace the layout-patterns 
 * with their regex equivalents.
 */
final class PatternLayoutRegexHelper extends PatternLayoutBase<Void> {
  
  @SuppressWarnings("serial")
  static private final Map<String, String> defaultConverterMap = new HashMap<String, String>() {{
    put("BARE", IdentityRegexConverter.class.getName());
    put("replace", ReplaceRegexConverter.class.getName());
    
    put("d", DateRegexConverter.class.getName());
    put("date", DateRegexConverter.class.getName());

    put("r", RelativeTimeRegexConverter.class.getName());
    put("relative", RelativeTimeRegexConverter.class.getName());

    put("level", LevelRegexConverter.class.getName());
    put("le", LevelRegexConverter.class.getName());
    put("p", LevelRegexConverter.class.getName());

    put("t", ThreadRegexConverter.class.getName());
    put("thread", ThreadRegexConverter.class.getName());

    put("lo", LoggerRegexConverter.class.getName());
    put("logger", LoggerRegexConverter.class.getName());
    put("c", LoggerRegexConverter.class.getName());

    put("m", MessageRegexConverter.class.getName());
    put("msg", MessageRegexConverter.class.getName());
    put("message", MessageRegexConverter.class.getName());

    put("C", ClassOfCallerRegexConverter.class.getName());
    put("class", ClassOfCallerRegexConverter.class.getName());

    put("M", MethodOfCallerRegexConverter.class.getName());
    put("method", MethodOfCallerRegexConverter.class.getName());

    put("L", LineOfCallerRegexConverter.class.getName());
    put("line", LineOfCallerRegexConverter.class.getName());
    
    put("F", FileOfCallerRegexConverter.class.getName());
    put("file", FileOfCallerRegexConverter.class.getName());

    put("X", MDCRegexConverter.class.getName());
    put("mdc", MDCRegexConverter.class.getName());

    put("ex", ThrowableProxyRegexConverter.class.getName());
    put("exception", ThrowableProxyRegexConverter.class.getName());
    put("rEx", RootCauseFirstThrowableProxyRegexConverter.class.getName());
    put("rootException", RootCauseFirstThrowableProxyRegexConverter.class.getName());
    put("throwable", ThrowableProxyRegexConverter.class.getName());

    put("xEx", ExtendedThrowableProxyRegexConverter.class.getName());
    put("xException", ExtendedThrowableProxyRegexConverter.class.getName());
    put("xThrowable", ExtendedThrowableProxyRegexConverter.class.getName());

    put("nopex", NopThrowableInformationRegexConverter.class.getName());
    put("nopexception", NopThrowableInformationRegexConverter.class.getName());

    put("cn", ContextNameRegexConverter.class.getName());
    put("contextName", ContextNameRegexConverter.class.getName());
    
    put("caller", CallerDataRegexConverter.class.getName());

    put("marker", MarkerRegexConverter.class.getName());

    put("property", PropertyRegexConverter.class.getName());
    
    put("n", LineSeparatorRegexConverter.class.getName());
  }};
  
  @Override
  public Map<String, String> getDefaultConverterMap() {
    return defaultConverterMap;
  }

  @Override
  public String doLayout(Void unused) {
    if (!isStarted()) {
      return CoreConstants.EMPTY_STRING;
    }
    return writeLoopOnConverters(unused);
  }
}
