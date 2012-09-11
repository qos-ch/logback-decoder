/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2011, QOS.ch. All rights reserved.
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

/**
 * INTERNAL CLASS
 * @hidden
 */
class Node {
  static final int LITERAL = 0;
  static final int SIMPLE_KEYWORD = 1;
  static final int COMPOSITE_KEYWORD = 2;
  static final int UNKNOWN_POSITION = -1;

  final int type;
  final int position;
  final Object value;
  Node next;

  Node(int type) {
    this(type, null);
  }

  Node(int type, Object value) {
    this(type, value, UNKNOWN_POSITION);
  }

  Node(int type, Object value, int position) {
    this.type = type;
    this.value = value;
    this.position = position;
  }

  /**
   * @return Returns the character position within the original pattern string.
   */
  public int getPosition() {
    return position;
  }

  /**
   * @return Returns the type.
   */
  public int getType() {
    return type;
  }

  /**
   * @return Returns the value.
   */
  public Object getValue() {
    return value;
  }

  public Node getNext() {
    return next;
  }

  public void setNext(Node next) {
    this.next = next;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Node)) {
      return false;
    }
    Node r = (Node) o;

    return (type == r.type)
        && (value != null ? value.equals(r.value) : r.value == null)
        && (next != null ? next.equals(r.next) : r.next == null);
  }

  @Override
  public int hashCode() {
    int result = type;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  String printNext() {
    if (next != null) {
      return " -> " + next;
    } else {
      return "";
    }
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    switch (type) {
    case LITERAL:
      buf.append("@" + position).append("LITERAL(" + value + ")");
      break;
    default:
      buf.append(super.toString());
    }

    buf.append(printNext());

    return buf.toString();
  }
}