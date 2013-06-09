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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.decoder.PatternNames;

import com.google.code.regexp.NamedMatcher;
import com.google.code.regexp.NamedPattern;

/**
 * Parses a layout pattern for its individual patterns and conversion specifiers
 * 
 * @author Anthony Trinh
 */
public class PatternParser {

  // group names
  private static final String FORMAT = "fmt";
  private static final String NAME = "name";
  private static final String OPTION = "opt";
  private static final String GROUP = "grp";

  // general regex pattern for layout patterns
  private static final NamedPattern REGEX_PATTERN;
  private static final String REGEX;
  static {
    // get all possible pattern names, sorted by length (longest to shortest)
    // and delimited by pipe
    List<String> patts = new ArrayList<String>(PatternNames.asList());

    // add color names
    patts.add("black");
    patts.add("red");
    patts.add("green");
    patts.add("yellow");
    patts.add("blue");
    patts.add("magenta");
    patts.add("cyan");
    patts.add("white");
    patts.add("boldRed");
    patts.add("boldGreen");
    patts.add("boldYellow");
    patts.add("boldBlue");
    patts.add("boldMagenta");
    patts.add("boldCyan");
    patts.add("boldWhite");
    patts.add("highlight");

    // sort to help regex distinguish between "msg", "mdc", and "m"
    Collections.sort(patts, new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        return s2.compareTo(s1); // reverse order (lexographically and then longest to shortest)
      }
    });

    // join patterns by pipe
    String names = StringUtils.join(patts, "|");

    REGEX = 
        "%" +                                                   // pattern starter (required) 
        "(?<" + FORMAT + ">[-.]{0,2}\\d+(?:\\.\\d+)?)?" +       // format modifier (optional)
        "(?<" + NAME + ">" + names + ")?" +                     // pattern name (optional)
        "(?:\\((?<" + GROUP + ">[^)]*?)\\))?" +                 // grouping (optional)
        "(?:\\{(?<" + OPTION + ">[^}]*?)\\})?";                 // conversion option (optional)

    REGEX_PATTERN = NamedPattern.compile(REGEX);
  }

  /**
   * Parses a layout pattern for its individual patterns and conversion
   * specifiers
   * 
   * @param layoutPattern
   *          layout pattern to parse
   * @return layout-pattern information for each sub pattern; or {@code null} if
   *         no sub-patterns exist in the text
   */
  public static List<PatternInfo> parse(String layoutPattern) {

    List<PatternInfo> list = new LinkedList<PatternInfo>();

    NamedMatcher m = REGEX_PATTERN.matcher(layoutPattern);
    
    // make sure more than one group is detected (otherwise we might have only
    // matched the percent symbol)
    while (m.find() && m.groupCount() > 1) {
      
      int start = m.start();
      
      // ignore pattern with escaped percent
      if (isEscaped(layoutPattern, start)) {
        continue;
      }

      // "group" here is the text in between parentheses within
      // the layout pattern (this is not to be confused with a 
      // regex capture group)
      String group = getTextUpToFirstCloser(m.group(GROUP), '(', ')', false);
      String opt = getTextUpToFirstCloser(buf + m.group(OPTION), '{', '}', true);

      PatternInfo inf = new PatternInfo();
      
      inf.setOriginal(m.group(0))
          .setStart(start)
          .setEnd(m.end())
          .setGroup(group)
          .setName(m.group(NAME))
          .setOption(opt)
          .setFormatModifier(m.group(FORMAT));

      // recursively set children
      if (group != null && !group.isEmpty()) {
        inf.setChildren(parse(group));
      }

      list.add(inf);
    }

    return list.isEmpty() ? null : list;
  }

  /**
   * Counts the occurrences of a target character in a string
   * 
   * @param s string to evaluate
   * @param target the character to find
   * @return the number of times the character occurs in the string
   */
  private static int countOccurences(String s, char target) {
    int i = -1, instances = 0;
    while ((i = s.indexOf(target, i+1)) >= 0) {
      if (!isEscaped(s, i)) {
        instances++;
      }
    }
    return instances;
  }
  
  /**
   * Gets a substring of a given string from the beginning of the string
   * up to the first non-escaped and non-quoted closing character (usually,
   * a right-paren or a right-bracket)
   * 
   * @param s string to evaluate 
   * @param opener the opening character for a text group (a paren or bracket)
   * @param closer the closing character for a text group
   * @param ignoreQuoted flag to ignore open/close chars between quotes
   * @return the substring (which may be equal to original)
   */
  private static String getTextUpToFirstCloser(String s, char opener, char closer, boolean ignoreQuoted) {
    if (s == null) return null;
    
    // if the number of non-escaped opening chars and closing 
    // chars are equal, the string doesn't contain a non-escaped
    // closer
    int numOpeners = countOccurences(s, opener);
    int numClosers = countOccurences(s, closer);
    if (numOpeners != numClosers) {
      // find first non-escaped closer
      int i = -1;
      while ((i = s.indexOf(closer, i+1)) >= 0) {
        if (ignoreQuoted && isQuoted(s, i)) {
          continue;
        }
        if (!isEscaped(s, i)) {
          break;
        }
      }
      // get substring up to that closer
      if (i > -1) {
        s = s.substring(0, i);
      }
    }
    
    return s;
  }
  
  /**
   * Determines if the character at the specified position
   * of a string is escaped with a backslash
   * 
   * @param s string to evaluate
   * @param pos the position of the character to evaluate
   * @return true if the character is escaped; otherwise false
   */
  static private boolean isEscaped(String s, int pos) {
          
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
  static private boolean isQuoted(String s, int pos) {
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

  /**
   * Removes all occurrences of the escape sequence in a string
   * 
   * @param s
   *          the string containing the escape sequence
   * @return the modified string
   */
  public static String removeEscapeSequence(String s) {
    return s.replace(ESC_SEQ, "");
  }

  /**
   * Changes the escape sequences in a string to backslashes
   * 
   * @param s
   *          the string containing the escape sequence
   * @return the modified string
   * 
   */
  public static String switchEscapeSequenceToSlashes(String s) {
    return s.replace(ESC_SEQ, "\\");
  }

  /**
   * This is an escape sequence of an unlikely string, used to escape regex
   * chars in the layout pattern. We can't use a backslash since the
   * pattern-layout converter complains about it. This special escape sequence
   * is converted to a backslash after we pass it through the pattern-layout
   * converter.
   */
  static public final String ESC_SEQ = "##ESC\033\033##";
  static private final int ESC_SEQ_LEN = ESC_SEQ.length();
  static private final Pattern REGEX_CHARS_PATTERN = Pattern.compile("([\\[\\]?.+*$(){}])");
  
  /**
   * Escapes regex characters in a substring. This is used to escape any regex
   * special chars in a layout pattern, which could be unintentionally
   * interpreted by the Java regex engine.
   * 
   * @param s string buffer, containing the substring to be escaped; escape sequences are 
   * inserted for literals in this buffer
   * @param start zero-based starting character position within {@code s} to be escaped
   * @param length number of characters of substring
   * @return number of new characters added to string buffer
   */
  static private int escapeRegexChars(StringBuffer s, int start, int length) {
    String substr = s.substring(start, start + length);
    
    Matcher matcher = REGEX_CHARS_PATTERN.matcher(substr);
    int numNewChars = 0;

    while (matcher.find()) {
      s.insert(matcher.start() + numNewChars + start, ESC_SEQ);
      numNewChars += ESC_SEQ_LEN;
    }
    return numNewChars;
  }
  
  /**
   * Unescapes (removes escape sequence) regex characters in a substring. This
   * is the converse of {@link #escapeRegexChars(StringBuffer, int, int)}.
   * 
   * @param s string buffer, containing the substring to be unescaped; escape sequences are 
   * removed from literals in this buffer
   * @param start zero-based starting character position within {@code s} to be unescaped
   * @param length number of characters of substring
   * @return number of characters removed from the string buffer
   */
  static private int unescapeRegexChars(StringBuffer s, int start, int length) {
    String substr = s.substring(start, start + length);
    int numRemovedChars = 0;
    Matcher matcher = REGEX_CHARS_PATTERN.matcher(substr);
    
    while (matcher.find()) {
      int idxOfRegexChar = matcher.start() - numRemovedChars + start;
      int idxOfEscSeq = idxOfRegexChar - ESC_SEQ_LEN;
      
      if (idxOfEscSeq >= 0) {
        String prefix = s.substring(idxOfEscSeq, idxOfRegexChar);
        if (prefix.equals(ESC_SEQ)) {
          s.delete(idxOfEscSeq, idxOfRegexChar);
          numRemovedChars += ESC_SEQ_LEN;
        }
      }
    }
    return numRemovedChars;
  }
  
  
  /**
   * Escapes regex characters in literals of a layout pattern. Any other regex
   * characters (in keywords, option lists, and format modifiers) are safely
   * ignored.
   * 
   * @param pattern the layout pattern to evaluate
   * @return the layout pattern with escaped regex chars in its literals 
   */
  static public String escapeRegexCharsInPattern(String pattern) {
    StringBuffer buf = new StringBuffer(pattern);
    
    try {
      int numNewChars = 0;
      
      Node n = new Parser<Void>(pattern).parse();
      while (n != null) {
        if (n.getType() == Node.LITERAL) {
          numNewChars += escapeRegexChars(
              buf, 
              numNewChars + n.getPosition(), 
              n.getValue().toString().length()
              );
        }
        n = n.getNext();
      }
      
    } catch (ScanException e) {
      //e.printStackTrace();
    }
    
    return buf.toString();
  }
  
  /**
   * Unescapes (removes escape sequence) regex characters in literals of a 
   * layout pattern. Escape sequences outside of literals (in keywords, 
   * option lists, and format modifiers) are safely ignored.
   * 
   * @param pattern the layout pattern to evaluate
   * @return the layout pattern with escaped regex chars in its literals 
   */
  static public String unescapeRegexCharsInPattern(String pattern) {
    StringBuffer buf = new StringBuffer(pattern);
    
    try {
      int numRemovedChars = 0;
      
      Node n = new Parser<Void>(pattern).parse();
      while (n != null) {
        if (n.getType() == Node.LITERAL) {
          numRemovedChars += unescapeRegexChars(
              buf, 
              n.getPosition() - numRemovedChars, 
              n.getValue().toString().length()
              );
        }
        n = n.getNext();
      }
      
    } catch (ScanException e) {
      //e.printStackTrace();
    }
    
    return buf.toString();
  }
}
