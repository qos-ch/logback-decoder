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
package ch.qos.logback.decoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * A special LoggingEvent that holds constant/static field values that
 * are normally calculated dynamically.
 */
public class StaticLoggingEvent extends LoggingEvent {
  private String _classNameOfCaller;
  private String _contextName;
  private String _fileNameOfCaller;
  private int    _lineNumberOfCaller;
  private String _methodNameOfCaller;
  private List<StackTraceElement> _callerStackTrace;
  private long relativeTimestamp; // the number of milliseconds elapsed since the start of the application until the creation of the logging event.
  private Map<String, String> properties = Collections.emptyMap();

  public Offset levelOffset;
  public Offset threadNameOffset;
  public Offset loggerNameOffset;
  public Offset messageOffset;
  public Offset classNameOfCallerOffset;
  public Offset methodOfCallerOffset;
  public Offset contextNameOffset;
  public Offset fileNameOffset;
  public Map<String, Offset> propertyOffsets = Collections.emptyMap();
  public Map<String, Offset> mdcPropertyOffsets = Collections.emptyMap();

  public void setCallerStackData(List<StackTraceElement> stackTrace) {
    _callerStackTrace = stackTrace;
  }

  @Override
  public StackTraceElement[] getCallerData() {
    return _callerStackTrace == null
        ? new StackTraceElement[0]
        : _callerStackTrace.toArray(new StackTraceElement[0]);
  }

  public void setClassNameOfCaller(String className) {
    _classNameOfCaller = className;
  }

  public void setContextName(String contextName) {
    _contextName = contextName;
  }

  public void setFileNameOfCaller(String fileName) {
    _fileNameOfCaller = fileName;
  }

  public void setLineNumberOfCaller(int lineNumber) {
    _lineNumberOfCaller = lineNumber;
  }

  public void setMethodOfCaller(String methodName) {
    _methodNameOfCaller = methodName;
  }

  public void setRelativeTimestamp(long relativeTimestamp) {
    this.relativeTimestamp = relativeTimestamp;
  }

  public String getClassNameOfCaller() {
    return _classNameOfCaller;
  }

  public String getContextName() {
    return _contextName;
  }

  public String getFileNameOfCaller() {
    return _fileNameOfCaller;
  }

  public int getLineNumberOfCaller() {
    return _lineNumberOfCaller;
  }

  public String getMethodOfCaller() {
    return _methodNameOfCaller;
  }

  public long getRelativeTimestamp() {
    return relativeTimestamp;
  }

  public Map<String, String> getProperties() {
    return this.properties;
  }

  public void putProperty(String key, String value, Offset offset) {
    if (properties.isEmpty()) {
      properties = new HashMap<>();
      propertyOffsets = new HashMap<>();
    }
    properties.put(key, value);
    propertyOffsets.put(key, offset);
  }

  public void putMDC(String key, String value, Offset offset) {
    if (mdcPropertyOffsets.isEmpty()) {
      mdcPropertyOffsets = new HashMap<>();
      setMDCPropertyMap(new HashMap<>());
    }
    getMDCPropertyMap().put(key, value);
    mdcPropertyOffsets.put(key, offset);
  }
}
