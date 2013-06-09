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
package ch.qos.logback.core.pattern.parser2;

import java.util.List;

import ch.qos.logback.decoder.PatternNames;

/**
 * Contains the individual parts of a single layout pattern, including
 * format modifier, conversion option, and name.
 *
 * The individual parts are defined as (without the square brackets):
 *
 * <blockquote>
 *    <pre>%[format][name]{[option]}</pre>
 * </blockquote>
 *
 * The "name" field must be one of {@link PatternNames}.
 */
public class PatternInfo {
  private String formatModifier;
  private String option;
  private String name;
  private String contents;
  private String original;
  private int start;
  private int end;
  private List<PatternInfo> children;

  /**
   * Gets the format modifier
   *
   * @return the format modifier
   */
  public String getFormatModifier() { return formatModifier; }

  /**
   * Sets the format modifier
   *
   * @param s the desired value
   * @return this {@code LayoutPatternInfo}
   */
  public PatternInfo setFormatModifier(String s) {
    formatModifier = s;
    return this;
  }

  /**
   * Gets the conversion option
   *
   * @return the conversion option
   */
  public String getOption() { return option; }

  /**
   * Sets the conversion option
   *
   * @param s the desired value
   * @return this {@code LayoutPatternInfo}
   */
  public PatternInfo setOption(String s) {
    option = s;
    return this;
  }

  /**
   * Gets the name of this layout pattern
   *
   * @return the name
   */
  public String getName() { return name; }

  /**
   * Sets the name of this layout pattern
   *
   * @param s the desired value
   * @return this {@code LayoutPatternInfo}
   */
  public PatternInfo setName(String s) {
    name = s;
    return this;
  }

  /**
   * Gets the starting position of this sub-pattern within
   * the full layout pattern
   *
   * @return the starting zero-based index
   */
  public int start() { return start; }

  /**
   * Sets the starting position of this sub-pattern within
   * the full layout pattern
   *
   * @param index the desired value
   * @return this {@code LayoutPatternInfo}
   */
  public PatternInfo setStart(int index) {
    start = index;
    return this;
  }

  /**
   * Gets the end position of this sub-pattern within the
   * full layout pattern
   *
   * @return the end zero-based index
   */
  public int end() { return end; }

  /**
   * Sets the end position of this sub-pattern within the
   * full layout pattern
   *
   * @param index the desired value
   */
  public PatternInfo setEnd(int index) {
    end = index;
    return this;
  }

  /**
   * Gets the contents (inner text of a grouping)
   *
   * @return the contents
   */
  public String contents() { return contents; }

  /**
   * Sets the contents (inner text of a grouping)
   *
   * @param s the desired value
   * @return this {@code LayoutPatternInfo}
   */
  public PatternInfo setGroup(String s) {
    contents = s;
    return this;
  }

  /**
   * Gets the children (sub-patterns from inner text of a grouping)
   * of this sub-pattern
   *
   * @return the children
   */
  public List<PatternInfo> getChildren() { return children; }

  /**
   * Sets the children (sub-patterns from inner text of a grouping)
   * of this sub-pattern
   *
   * @param children the desired value
   * @return this {@code LayoutPatternInfo}
   */
  public PatternInfo setChildren(List<PatternInfo> children) {
    this.children = children;
    return this;
  }

  /**
   * Gets the original sub-pattern text
   *
   * @return the complete sub-pattern
   */
  public String getOriginal() { return original; }

  /**
   * Sets the original sub-pattern text
   *
   * @param s the desired value
   * @return this {@code LayoutPatternInfo}
   */
  public PatternInfo setOriginal(String s) {
    original = s;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    appendString(sb, 0);
    return sb.toString();
  }

  /**
   * Appends a specified number of tabs to a string builder
   *
   * @param sb destination string builder
   * @param count number of tabs to append
   */
  private void appendTabs(StringBuilder sb, int count) {
    for (int i = 0; i < count; i++) sb.append("  ");
  }

  /**
   * Appends a formatted string representation of
   * this {@code LayoutPatternInfo}
   *
   * @param sb destination string builder
   * @param level tab level
   */
  private void appendString(StringBuilder sb, int level) {

    appendTabs(sb, level);
    sb.append("{\n");
    appendTabs(sb, level+1);
    sb.append("Indexes:      [").append(start).append(",").append(end).append("),\n");

    if (original != null) {
      appendTabs(sb, level+1);
      sb.append("Original:     ").append(original).append(",\n");
    }
    if (name != null) {
      appendTabs(sb, level+1);
      sb.append("Name:         ").append(name).append(",\n");
    }
    if (contents != null) {
      appendTabs(sb, level+1);
      sb.append("Contents:     ").append(contents).append(",\n");
    }
    if (formatModifier != null) {
      appendTabs(sb, level+1);
      sb.append("Format Mod:   ").append(formatModifier).append(",\n");
    }
    if (option != null) {
      appendTabs(sb, level+1);
      sb.append("Option:       ").append(option).append(",\n");
    }
    if (children != null && !children.isEmpty()) {
      appendTabs(sb, level+1);
      sb.append("Children: {\n");

      for (PatternInfo c : children) {
        c.appendString(sb, level+2);
      }

      appendTabs(sb, level+1);
      sb.append("}\n");
    }
    appendTabs(sb, level);
    sb.append("},\n");
  }
}
