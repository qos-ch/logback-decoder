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
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.pattern.PatternLayoutBase;

/**
 * Utility to convert a layout pattern into a regular expression
 * 
 * @author Anthony Trinh
 */
public class PatternLayoutRegexifier extends PatternLayoutBase<ILoggingEvent> {
  
  public static final Map<String, String> defaultConverterMap = new HashMap<String, String>();
  
  static {
    defaultConverterMap.put("BARE", IdentityRegexConverter.class.getName());
    defaultConverterMap.put("replace", ReplaceRegexConverter.class.getName());
    
    defaultConverterMap.put("d", DateRegexConverter.class.getName());
    defaultConverterMap.put("date", DateRegexConverter.class.getName());

    defaultConverterMap.put("r", RelativeTimeRegexConverter.class.getName());
    defaultConverterMap.put("relative", RelativeTimeRegexConverter.class.getName());

    defaultConverterMap.put("level", LevelRegexConverter.class.getName());
    defaultConverterMap.put("le", LevelRegexConverter.class.getName());
    defaultConverterMap.put("p", LevelRegexConverter.class.getName());

    defaultConverterMap.put("t", ThreadRegexConverter.class.getName());
    defaultConverterMap.put("thread", ThreadRegexConverter.class.getName());

    defaultConverterMap.put("lo", LoggerRegexConverter.class.getName());
    defaultConverterMap.put("logger", LoggerRegexConverter.class.getName());
    defaultConverterMap.put("c", LoggerRegexConverter.class.getName());

    defaultConverterMap.put("m", MessageRegexConverter.class.getName());
    defaultConverterMap.put("msg", MessageRegexConverter.class.getName());
    defaultConverterMap.put("message", MessageRegexConverter.class.getName());

    defaultConverterMap.put("C", ClassOfCallerRegexConverter.class.getName());
    defaultConverterMap.put("class", ClassOfCallerRegexConverter.class.getName());

    defaultConverterMap.put("M", MethodOfCallerRegexConverter.class.getName());
    defaultConverterMap.put("method", MethodOfCallerRegexConverter.class.getName());

    defaultConverterMap.put("L", LineOfCallerRegexConverter.class.getName());
    defaultConverterMap.put("line", LineOfCallerRegexConverter.class.getName());
    
    defaultConverterMap.put("F", FileOfCallerRegexConverter.class.getName());
    defaultConverterMap.put("file", FileOfCallerRegexConverter.class.getName());

    defaultConverterMap.put("X", MDCRegexConverter.class.getName());
    defaultConverterMap.put("mdc", MDCRegexConverter.class.getName());

    defaultConverterMap.put("ex", ThrowableProxyRegexConverter.class.getName());
    defaultConverterMap.put("exception", ThrowableProxyRegexConverter.class
        .getName());
    defaultConverterMap.put("rEx", RootCauseFirstThrowableProxyRegexConverter.class.getName());
    defaultConverterMap.put("rootException", RootCauseFirstThrowableProxyRegexConverter.class
        .getName());
    defaultConverterMap.put("throwable", ThrowableProxyRegexConverter.class
        .getName());

    defaultConverterMap.put("xEx", ExtendedThrowableProxyRegexConverter.class.getName());
    defaultConverterMap.put("xException", ExtendedThrowableProxyRegexConverter.class
        .getName());
    defaultConverterMap.put("xThrowable", ExtendedThrowableProxyRegexConverter.class
        .getName());

    defaultConverterMap.put("nopex", NopThrowableInformationRegexConverter.class
        .getName());
    defaultConverterMap.put("nopexception",
        NopThrowableInformationRegexConverter.class.getName());

    defaultConverterMap.put("cn", ContextNameRegexConverter.class.getName());
    defaultConverterMap.put("contextName", ContextNameRegexConverter.class.getName());
    
    defaultConverterMap.put("caller", CallerDataRegexConverter.class.getName());

    defaultConverterMap.put("marker", MarkerRegexConverter.class.getName());

    defaultConverterMap.put("property", PropertyRegexConverter.class.getName());
    
    defaultConverterMap.put("n", LineSeparatorRegexConverter.class.getName());
  }
  
  @Override
  public Map<String, String> getDefaultConverterMap() {
    return defaultConverterMap;
  }

  @Override
  public String doLayout(ILoggingEvent event) {
    if (!isStarted()) {
      return CoreConstants.EMPTY_STRING;
    }
    return writeLoopOnConverters(event);
  }
  
}
