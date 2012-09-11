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

import ch.qos.logback.core.pattern.parser2.PatternInfo;

/**
 * Given an appropriate string value, a FieldCapturer will set the 
 * field of E when its {@link #captureField(Object, String)} is called.
 *
 * @param <E> some event type
 */
public interface FieldCapturer<E> {

  /**
   * Given fieldAsStr, sets the appropriate field of the event.
   *
   * @param event the event whose field should be captured
   * @param fieldAsStr the field as a string
   * @param info sub-pattern information from original layout 
   * pattern, including format and conversion modifiers, that 
   * can be used to parse {@code fieldAsStr}
   */
  void captureField(E event, String fieldAsStr, PatternInfo info);

}
