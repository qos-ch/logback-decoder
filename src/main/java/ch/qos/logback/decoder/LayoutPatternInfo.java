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
 * Contains the individual parts of a single layout pattern, including
 * format modifier, conversion modifier, and name.
 * 
 * The individual parts are defined as (without the parentheses):
 * 
 *    %(format)(name){(conversion)}
 * 
 * The "name" field must be one of {@link PatternNames}.
 */
public class LayoutPatternInfo {
  private String formatModifier;
  private String conversionModifier;
  private String name;
  
  /**
   * Constructs a <code>LayoutPatternInfo</code>
   * 
   * @param name name of the pattern
   * @param formatModifier format modifier
   * @param conversionModifier conversion modifier
   */
  public LayoutPatternInfo(String name, String formatModifier, String conversionModifier) {
    this.name = name;
    this.formatModifier = formatModifier;
    this.conversionModifier = conversionModifier;
  }
  
  /**
   * Gets the format modifier
   * 
   * @return the format modifier
   */
  public String getFormatModifier() { return formatModifier; }
  
  /**
   * Gets the conversion modifier
   * 
   * @return the conversion modifier
   */
  public String getConversionModifier() { return conversionModifier; }
  
  /**
   * Gets the name of this layout pattern
   * 
   * @return the name
   */
  public String getName() { return name; }
}
