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

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * Tests the {@link PatternParser} class
 */
public class PatternParserTest {

  @Test
  public void testGetsName() {
    final String PATT = "%msg%n";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(2, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("msg", inf.getName());
  }

  @Test
  public void testGetsGroupContents() {
    final String PATT = "%cyan(%logger [%thread])";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("%logger [%thread]", inf.contents());
  }

  @Test
  public void testGetsGroupContentsWithEscapedParens() {
    final String PATT = "%cyan(%logger \\(%thread\\))";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("%logger \\(%thread\\)", inf.contents());
  }

  @Test
  public void testGroupContentsWith2SlashesDoesNotGetCloseParen() {
    // this is supposed to trick the parser into thinking that
    // there's an escaped right-paren, but the escape is itself
    // escaped, which makes it a literal slash followed by a
    // right-paren that should be processed
    final String PATT = "%cyan(%logger \\(%thread\\\\))";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("%logger \\(%thread\\\\", inf.contents());
  }

  @Test
  public void testGroupContentsWith2SlashesDoesNotGetDecoyParens() {
    final String PATT = "%cyan(%logger \\(%thread\\\\) ) )";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("%logger \\(%thread\\\\", inf.contents());
  }

  @Test
  public void testGroupContentsWith3SlashesGetsCloseParen() {
    // the last of the "triplets" is actually escaping its
    // adjacent right-paren
    final String PATT = "%cyan(%logger \\(%thread\\\\\\))";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("%logger \\(%thread\\\\\\)", inf.contents());
  }

  @Test
  public void testGetsGroupContentsIgnoresUnrelatedCloseParen() {
    // this pattern has an extra close-paren at the end, which the parser should ignore
    final String PATT = "%cyan(%logger \\(%thread\\)) foo bar)";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("%logger \\(%thread\\)", inf.contents());
  }

  @Test
  public void testGetsOptionWithSingleQuotes() {
    final String PATT = "%replace(%logger [%thread]){'\\d{14,16}', 'XXXX'}";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("'\\d{14,16}', 'XXXX'", inf.getOption());
  }

  @Test
  public void testGetsOptionWithDoubleQuotes() {
    final String PATT = "%replace(%logger [%thread]){\"\\d{14,16}\", \"XXXX\"}";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("\"\\d{14,16}\", \"XXXX\"", inf.getOption());
  }

  @Test
  public void testGetsOptionWithEscapedBrackets() {
    final String PATT = "%replace(%logger [%thread]){'\\d{14,16}', '\\{foo\\}'}";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("'\\d{14,16}', '\\{foo\\}'", inf.getOption());
  }

  @Test
  public void testGetsOptionIgnoresUnrelatedCloseBracket() {
    final String PATT = "%replace(%logger [%thread]){'\\d{14,16}', '\\{foo\\}'} foo bar}";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("'\\d{14,16}', '\\{foo\\}'", inf.getOption());
  }
  @Test
  public void testGetsFormatModifierLeftPad() {
    final String PATT = "%20logger";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("20", inf.getFormatModifier());
  }

  @Test
  public void testGetsFormatModifierRightPad() {
    final String PATT = "%-20logger";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("-20", inf.getFormatModifier());
  }

  @Test
  public void testGetsFormatModifierTruncated() {
    final String PATT = "%.20logger";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals(".20", inf.getFormatModifier());
  }

  @Test
  public void testGetsFormatModifierLeftPadTruncated() {
    final String PATT = "%20.30logger";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("20.30", inf.getFormatModifier());
  }

  @Test
  public void testGetsFormatModifierRightPadTruncated() {
    final String PATT = "%-20.30logger";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals("-20.30", inf.getFormatModifier());
  }

  @Test
  public void testGetsFormatModifierTruncatedEndIfExceedLen() {
    final String PATT = "%.-30logger";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(1, results.size());
    PatternInfo inf = results.get(0);
    assertEquals(".-30", inf.getFormatModifier());
  }

  @Test
  public void testGetsChildren() {
    final String PATT = "%replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} - %msg%n";
    List<PatternInfo> results = PatternParser.parse(PATT);
    assertEquals(3, results.size());
    PatternInfo inf = results.get(0);
    assertNotNull("Missing children", inf.getChildren());
    assertEquals(2, inf.getChildren().size());
  }

  @Test
  public void testGetsNoChildren() {
    final String PATT = "%replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} - %msg%n";
    List<PatternInfo> results = PatternParser.parse(PATT);
    PatternInfo inf = results.get(0);

    // get %logger
    PatternInfo child1 = inf.getChildren().get(0);
    assertNull(child1.getChildren());
  }

  @Test
  public void testEscapesRegexCharsInPatternLiterals() {
    final String PATT = "{%d} %replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} [%level] - %msg%n";
    final String ESC = PatternParser.ESC_SEQ;
    final String PATT2 = ESC + "{%d" + ESC + "} %replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} " + ESC + "[%level" + ESC + "] - %msg%n";

    assertEquals(PATT2, PatternParser.escapeRegexCharsInPattern(PATT));
  }

  @Test
  public void testDoesNotEscapePatternThatHasNoRegexCharsInLiterals() {
    final String PATT = "%d %replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} %level - %msg%n";
    assertEquals(PATT, PatternParser.escapeRegexCharsInPattern(PATT));
  }

  @Test
  public void testUnescapesEscapedRegexCharsInPatternLiterals() {
    final String PATT = "{%d} %replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} [%level] - %msg%n";
    final String ESC = PatternParser.ESC_SEQ;
    final String PATT2 = ESC + "{%d" + ESC + "} %replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} " + ESC + "[%level" + ESC + "] - %msg%n";

    assertEquals(PATT, PatternParser.unescapeRegexCharsInPattern(PATT2));
  }

  @Test
  public void testDoesNotUnescapePatternThatHasNoEscapes() {
    final String PATT = "{%d} %replace(%logger [%thread]){'\\d{14,16}', 'XXXX'} [%level] - %msg%n";
    assertEquals(PATT, PatternParser.unescapeRegexCharsInPattern(PATT));
  }
}
