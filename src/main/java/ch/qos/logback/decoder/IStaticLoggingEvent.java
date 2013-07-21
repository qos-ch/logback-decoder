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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

public interface IStaticLoggingEvent extends ILoggingEvent {
  void setCallerStackData(List<StackTraceElement> stackTrace);
  void setClassNameOfCaller(String className);
  void setContextName(String contextName);
  void setFileNameOfCaller(String fileName);
  void setLevel(Level level);
  void setLineNumberOfCaller(int lineNumber);
  void setLoggerName(String loggerName);
  void setMessage(String message);
  void setMethodOfCaller(String methodName);
  void setThreadName(String threadName);
  void setTimeStamp(long ms);
}
