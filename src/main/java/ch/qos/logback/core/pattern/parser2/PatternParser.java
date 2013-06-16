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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.decoder.ParserUtil;
import ch.qos.logback.decoder.PatternNames;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;

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
  private static final Pattern REGEX_PATTERN;
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
        "(?<" + NAME   + ">" + names + ")?" +                   // pattern name (optional)
        "(?<" + GROUP  + ">\\([^)]*?\\))?" +                    // grouping (optional)
        "(?<" + OPTION + ">\\{[^}]*?\\})?";                     // conversion option (optional)

    REGEX_PATTERN = Pattern.compile(REGEX);
  }

  private static Logger logger(){
    return LoggerFactory.getLogger(PatternParser.class);
  }

  private static DateFormat parseDateFormat(String option) {
    TimeZone tz = null;

    // default to ISO8601 if no conversion pattern given
    if (option == null || option.isEmpty() || option.equalsIgnoreCase(CoreConstants.ISO8601_STR)) {
      option = CoreConstants.ISO8601_PATTERN;
    }

    // Parse the last option in the conversion pattern as a time zone.
    // Make sure the comma is not escaped/quoted.
    int idx = option.lastIndexOf(",");
    if ((idx > -1)
        && (idx + 1 < option.length()
        && !ParserUtil.isEscaped(option, idx)
        && !ParserUtil.isQuoted(option, idx))) {

      // make sure the string isn't the millisecond pattern, which
      // can appear after a comma
      String tzStr = option.substring(idx + 1).trim();
      if (!tzStr.startsWith("SSS")) {
        option = option.substring(0, idx);
        tz = TimeZone.getTimeZone(tzStr);
        if (!tz.getID().equalsIgnoreCase(tzStr)) {
          logger().warn("Time zone (\"{}\") defaulting to \"{}\".", tzStr, tz.getID());
        }
      }
    }

    // strip quotes from date format because SimpleDateFormat doesn't understand them
    if (option.length() > 1 && option.startsWith("\"") && option.endsWith("\"")) {
      option = option.substring(1, option.length() - 1);
    }

    DateFormat format = new SimpleDateFormat(option);
    format.setLenient(true);

    if (tz != null) {
      format.setTimeZone(tz);
    }

    return format;
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

    Matcher m = REGEX_PATTERN.matcher(layoutPattern);

    // make sure more than one group is detected (otherwise we might have only
    // matched the percent symbol)
    while (m.find() && m.groupCount() > 1) {

      int start = m.start();

      // ignore pattern with escaped percent symbol
      if (ParserUtil.isEscaped(layoutPattern, start)) {
        continue;
      }

      // parse the "group" and "option" (if any)
      CapturedText group = getEnclosedText(layoutPattern, m.start(GROUP), '(', ')', false);
      int nextPos        = minX(group.end(), m.start(OPTION));
      CapturedText opt   = getEnclosedText(layoutPattern, nextPos, '{', '}', true);
      int end            = minX(opt.end(), m.end());

      boolean isDate = PatternNames.getFullName(m.group(NAME)).equals("date");
      PatternInfo inf = isDate ? new DatePatternInfo() : new PatternInfo();

      inf.setOriginal(m.group(0))
          .setStart(start)
          .setEnd(end)
          .setGroup(group.value())
          .setName(m.group(NAME))
          .setOption(opt.value())
          .setFormatModifier(m.group(FORMAT));

      if (isDate) {
        ((DatePatternInfo)inf).setDateFormat(parseDateFormat(opt.value()));
      }

      // recursively set children
      if (!group.value().isEmpty()) {
        inf.setChildren(parse(group.value()));
      }

      list.add(inf);
    }

    return list.isEmpty() ? null : list;
  }

  /**
   * Find a non-escaped character in a string
   *
   * @param haystack string to evaluate
   * @param start position in the string from which to start the search
   * @param needle character to find (an open/close char)
   * @param ignoreQuoted flag to ignore open/close chars between quotes
   * @return if found, the position of the character in the string; otherwise, -1
   */
  private static int findNonEscaped(String haystack, int start, char needle, boolean ignoreQuoted) {
    int i = start - 1;
    boolean found = false;
    while ((i = haystack.indexOf(needle, i + 1)) >= 0) {
      if (ignoreQuoted && ParserUtil.isQuoted(haystack, i)) {
        continue;
      }
      if (!ParserUtil.isEscaped(haystack, i)) {
        found = true;
        break;
      }
    }
    return found ? i : -1;
  }

  /**
   * Gets the text between two enclosing delimiters (an opening paren/bracket
   * and a closing paren/bracket).
   *
   * @param s string to evaluate
   * @param start position in the string from which to start the search
   * @param opener the opening character for a text group (a paren or bracket)
   * @param closer the closing character for a text group
   * @param ignoreQuoted flag to ignore open/close chars between quotes
   * @return a {@link CapturedText}, containing the text
   */
  private static CapturedText getEnclosedText(String s, int start, char opener, char closer, boolean ignoreQuoted) {
    if (start < 0) return CapturedText.EMPTY;

    // find the opening character
    int i = findNonEscaped(s, start, opener, ignoreQuoted);
    if (i > -1) {
      start = i + 1;
    }

    // ...and the closing character; then get the text in between
    i = findNonEscaped(s, start, closer, ignoreQuoted);
    if (i > -1) {
      s = s.substring(start, i);
    }

    return new CapturedText(i, s);
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

  /**
   * Gets the minimum of two values -- the first of which
   * must be greater than -1
   *
   * @param a first value to compare, must be > -1
   * @param b second value
   * @return if a > -1, then the minimum of a or b; otherwise, b
   */
  private static int minX(int a, int b) {
    if (a > -1) {
      return a < b ? a : b;
    } else {
      return b;
    }
  }

  /**
   * Data struct for captured text (for a group or option)
   */
  private static class CapturedText {
    public static final CapturedText EMPTY = new CapturedText(-1, "");

    private int endPos;
    private String value;

    public CapturedText(int endPos, String value) {
      this.endPos = endPos;
      this.value = value;
    }

    /** Gets the ending position of the captured text */
    public int end() { return endPos; }

    /** Gets the string value of the captured text */
    public String value() { return value; }
  }
}
