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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Tests decoding %caller
 *
 * @author Anthony Trinh
 */
public class CallerStackTraceDecoderTest extends DecoderTest {

  @Test
  public void decodesCallerStackTraceWithFileAndLine() {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [main] KdbFxFeedhandlerApp: Foo Bar\n"
        + "Caller+0   at mainPackage.sub.sample.Bar.sampleMethodName(Bar.java:22)\n"
        + "Caller+1   at mainPackage.sub.sample.Bar.createLoggingRequest(Bar.java:17)\n";

    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{0}: %msg%caller%n";
    decoder.setLayoutPattern(PATT);
    ILoggingEvent event = decoder.decode(INPUT);
    assertNotNull(event);

    StackTraceElement[] stackTrace = event.getCallerData();
    assertNotNull(stackTrace);
    assertEquals(2, stackTrace.length);

    assertEquals("mainPackage.sub.sample.Bar", stackTrace[0].getClassName());
    assertEquals("sampleMethodName", stackTrace[0].getMethodName());
    assertEquals("Bar.java", stackTrace[0].getFileName());
    assertEquals(22, stackTrace[0].getLineNumber());

    assertEquals("mainPackage.sub.sample.Bar", stackTrace[1].getClassName());
    assertEquals("createLoggingRequest", stackTrace[1].getMethodName());
    assertEquals("Bar.java", stackTrace[1].getFileName());
    assertEquals(17, stackTrace[1].getLineNumber());
  }

  @Test
  public void decodesCallerStackTraceWithNoFileInfo() {
    final String INPUT = "2013-06-12 15:27:15.044 INFO  [main] KdbFxFeedhandlerApp: Foo Bar\n"
        + "Caller+0   at mainPackage.sub.sample.Bar.sampleMethodName\n"
        + "Caller+1   at mainPackage.sub.sample.Bar.createLoggingRequest\n";

    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{0}: %msg%caller%n";
    decoder.setLayoutPattern(PATT);
    ILoggingEvent event = decoder.decode(INPUT);
    assertNotNull(event);

    StackTraceElement[] stackTrace = event.getCallerData();
    assertNotNull(stackTrace);
    assertEquals(2, stackTrace.length);

    assertEquals("mainPackage.sub.sample.Bar", stackTrace[0].getClassName());
    assertEquals("sampleMethodName", stackTrace[0].getMethodName());
    assertEquals("", stackTrace[0].getFileName());
    assertEquals(0, stackTrace[0].getLineNumber());

    assertEquals("mainPackage.sub.sample.Bar", stackTrace[1].getClassName());
    assertEquals("createLoggingRequest", stackTrace[1].getMethodName());
    assertEquals("", stackTrace[1].getFileName());
    assertEquals(0, stackTrace[1].getLineNumber());
  }
}
