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

/**
 * Thrown to indicate that the required layout pattern cannot be determined. 
 * 
 * @author Anthony Trinh
 */
public class UnknownLayoutPatternException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a {@code UnknownLayoutPatternException} with
   * no detail message
   */
  public UnknownLayoutPatternException() {
    super();
  }
  
  /**
   * Constructs a {@code UnknownLayoutPatternException} with
   * the specified detail message
   * 
   * @param message the detail message
   */
  public UnknownLayoutPatternException(String message) {
    super(message);
  }
}
