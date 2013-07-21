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

import java.util.List;

import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * A special LoggingEvent that holds constant/static field values that
 * are normally calculated dynamically.
 */
public class StaticLoggingEvent extends LoggingEvent implements IStaticLoggingEvent {
  private String _classNameOfCaller;
  private String _contextName;
  private String _fileNameOfCaller;
  private int    _lineNumberOfCaller;
  private String _methodNameOfCaller;
  private List<StackTraceElement> _callerStackTrace;

  @Override
  public void setCallerStackData(List<StackTraceElement> stackTrace) {
    _callerStackTrace = stackTrace;
  }

  @Override
  public StackTraceElement[] getCallerData() {
    return _callerStackTrace == null
        ? new StackTraceElement[0]
        : _callerStackTrace.toArray(new StackTraceElement[0]);
  }

  @Override
  public void setClassNameOfCaller(String className) {
    _classNameOfCaller = className;
  }

  @Override
  public void setContextName(String contextName) {
    _contextName = contextName;
  }

  @Override
  public void setFileNameOfCaller(String fileName) {
    _fileNameOfCaller = fileName;
  }

  @Override
  public void setLineNumberOfCaller(int lineNumber) {
    _lineNumberOfCaller = lineNumber;
  }

  @Override
  public void setMethodOfCaller(String methodName) {
    _methodNameOfCaller = methodName;
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
}
