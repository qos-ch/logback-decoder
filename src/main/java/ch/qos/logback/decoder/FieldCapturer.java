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
 * A capturing group (as in regular expressions) for a particular logback
 * layout pattern.
 * 
 * @param <E>
 *          An {@link Event} type (XXX: Should this actually be "E extends Event"?)
 */
public abstract class FieldCapturer<E> {

  protected boolean capturing;
  protected String  regexPattern;

  /**
   * Gets the regular expression pattern used to parse a field from an event
   * 
   * @return the pattern as a string
   */
  public String getRegexPattern() {
    return regexPattern;
  }

  /**
   * Determines if this capturer is in the process of capturing fields
   * 
   * @return <c>true</c> if capture is in progress; <c>false</c> otherwise
   */
  public boolean isCapturing() {
    return capturing;
  }

  /**
   * Parses a field from an event
   * 
   * @param event
   *            the event to be evaluated
   * @param fieldAsStr
   *            (XXX: I'm not entirely sure. I'm guessing this is the field's layout pattern. e.g., "%msg") 
   */
  abstract void captureField(E event, String fieldAsStr);

}
