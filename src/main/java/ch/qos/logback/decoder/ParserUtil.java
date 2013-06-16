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

/**
 * Parser utility
 */
public class ParserUtil {

  /**
   * Determines if the character at the specified position
   * of a string is escaped with a backslash
   *
   * @param s string to evaluate
   * @param pos the position of the character to evaluate
   * @return true if the character is escaped; otherwise false
   */
  public static boolean isEscaped(String s, int pos) {

    // Count the backslashes preceding this position. If it's
    // even, there is no escape and the slashes are just literals.
    // If it's odd, one of the slashes (the last one) is escaping
    // the character at the given position.
    int numSlashes = 0;
    while (pos > 0 && (s.charAt(pos - 1) == '\\')) {
      pos--;
      numSlashes++;
    }
    return numSlashes % 2 != 0;
  }

  /**
   * Determines if the character at the specific position
   * of a string is quoted (by single-quote or double)
   *
   * @param s string to evaluate
   * @param pos the position of the character to evaluate
   * @return
   */
  public static boolean isQuoted(String s, int pos) {
    boolean quoted = false;

    int len = s.length();
    if (pos == 0 || pos == len - 1) {
      return false;
    }

    // Find preceding and succeeding quotes
    String firstHalf = s.substring(0, pos);
    int posLeftQ = firstHalf.lastIndexOf('\'');
    int posRightQ = s.indexOf('\'', pos+1);
    quoted = (posLeftQ > -1 && posRightQ > -1);

    if (!quoted) {
      posLeftQ = firstHalf.lastIndexOf('"');
      posRightQ = s.indexOf('"', pos+1);
      quoted = (posLeftQ > -1 && posRightQ > -1);
    }

    return quoted;
  }
}
