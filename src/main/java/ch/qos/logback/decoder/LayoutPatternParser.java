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

import java.util.ArrayList;
import java.util.List;

import com.google.code.regexp.NamedMatcher;
import com.google.code.regexp.NamedPattern;


/**
 * Parses a layout pattern for its individual patterns and conversion specifiers
 * 
 * @author Anthony Trinh
 */
public class LayoutPatternParser {

  /**
   * Parses a layout pattern for its individual patterns and conversion specifiers
   * 
   * @param layoutPattern layout pattern to parse
   * @return layout-pattern information for each sub pattern
   */
  public static List<LayoutPatternInfo> parse(String layoutPattern) {
    List<LayoutPatternInfo> list = new ArrayList<LayoutPatternInfo>();
    
    // get all possible pattern names, delimited by | 
    StringBuilder sb = new StringBuilder();
    for (String s : PatternNames.asList()) {
      sb.append(s).append("|");
    }
    String names = sb.toString();
    
    // remove trailing pipe
    if (names.endsWith("|")) {
      names = names.substring(0, names.length() - 1);
    }
    
    // regex captures a percent (%) not preceded by foreslash (\), 
    // optionally followed by bracketed text ({...})
    String regex = 
        "(?<esc>\\\\)?%" +                        // percent symbol, possibly escaped (optional) 
        "(?<fmt>[-.]{0,2}\\d+(\\.\\d+)?)?" +      // format modifier (optional)
        "(?<name>" + names + ")" +                // pattern name (required)
        "(?:\\{(?<conv>.*)\\})?";                 // conversion modifier (optional)
    
    NamedPattern p = NamedPattern.compile(regex);
    NamedMatcher m = p.matcher(layoutPattern);
    
    while (m.find() && m.groupCount() > 1) {
      // Ignore pattern with escaped percent. It would be nice to
      // use a look-behind regex specifier, but that only works
      // for full-string matches (we're instead using find() to find
      // substring occurrences, so that won't work for us).
      if (m.group("esc") != null) {
        continue;
      }
      
      list.add(new LayoutPatternInfo(m.group("name"), m.group("fmt"), m.group("conv")));
    }
    
    return list;
  }
}
