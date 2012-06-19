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
 * A FieldCapturer is responsible for producing the regular expression matching a
 * given field of an event of type E. Given an appropriate string value, a
 * FieldCapturer will set the field of E when its {@link #captureField(Object, String)}
 * is called.
 *
 * @param <E> some event type
 *
 */
public interface FieldCapturer<E> {

  /**
   * Gets the regular expression pattern used to parse a field from an event
   * 
   * @return the pattern as a string
   */
  public String getRegexPattern();


  /**
   * Some FieldCapturer instances are just place holders. If this method returns
   * true, {@link #captureField(Object, String)} method should not be called. More
   * importantly, the text matching the regex need not be stored/captured.
   *
   * @return true if this instance is a placeholder, false otherwise.
   */
  boolean isPlaceHolder();
  /**
   * Some capturer are
   * @return
   */
  boolean isCapturing();

  /**
   * Given fieldAsStr, sets the appropriate field of the event.
   *
   * @param event the event whose field should be captured
   * @param fieldAsStr
   */
  abstract void captureField(E event, String fieldAsStr);

}
