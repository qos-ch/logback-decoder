package ch.qos.logback.decoder;
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

/**
 * Constant layout-pattern names
 * 
 * @author Anthony Trinh
 */
public class PatternNames {
  public static final String IDENTITY = "BARE";
  public static final String REPLACE = "replace";
  
  public static final String DATE = "date";
  public static final String DATE_1 = "d";
  
  public static final String RELATIVE_TIME = "relative";
  public static final String RELATIVE_TIME_1 = "r";
  
  public static final String LEVEL = "level";
  public static final String LEVEL_1 = "le";
  public static final String LEVEL_2 = "p";

  public static final String THREAD_NAME = "thread";
  public static final String THREAD_NAME_1 = "t";
  
  public static final String LOGGER_NAME = "logger";
  public static final String LOGGER_NAME_1 = "lo";
  public static final String LOGGER_NAME_2 = "c";

  public static final String MESSAGE = "message";
  public static final String MESSAGE_1 = "m";
  public static final String MESSAGE_2 = "msg";
  
  public static final String CLASS_OF_CALLER = "class";
  public static final String CLASS_OF_CALLER_1 = "C";
  
  public static final String METHOD_OF_CALLER = "method";
  public static final String METHOD_OF_CALLER_1 = "M";
  
  public static final String LINE_OF_CALLER = "line";
  public static final String LINE_OF_CALLER_1 = "L";
  
  public static final String FILE_OF_CALLER = "file";
  public static final String FILE_OF_CALLER_1 = "F";
  
  public static final String MDC = "mdc";
  public static final String MDC_1 = "X";
  
  public static final String EXCEPTION = "exception";
  public static final String EXCEPTION_1 = "ex";
  public static final String EXCEPTION_2 = "throwable";
  
  public static final String EXT_EXCEPTION = "xException";
  public static final String EXT_EXCEPTION_1 = "xEx";
  public static final String EXT_EXCEPTION_2 = "xThrowable";
  
  public static final String ROOT_EXCEPTION = "rootException";
  public static final String ROOT_EXCEPTION_1 = "rEx";
  
  public static final String NOPEX = "nopexception";
  public static final String NOPEX_1 = "nopex";

  public static final String CONTEXT_NAME = "contextName";
  public static final String CONTEXT_NAME_1 = "cn";
  
  public static final String CALLER_STACKTRACE = "caller";

  public static final String MARKER = "marker";

  public static final String PROPERTY = "property";
  
  public static final String NEWLINE = "n";
}
