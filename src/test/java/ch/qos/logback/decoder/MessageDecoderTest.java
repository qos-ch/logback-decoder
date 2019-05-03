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

import ch.qos.logback.classic.Level;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests decoding %message
 *
 * @author Anthony Trinh
 */
public class MessageDecoderTest {
  @Test
  public void decodesNumericMessage() {
    assertEquals("123", getMessage("123"));
  }

  @Test
  public void decodesSimpleMessage() {
    assertEquals("This is just a test", getMessage("This is just a test"));
  }

  @Test
  public void testMultiline() {
    assertEquals("This\nis\ntest.", getMessage("This\nis\ntest."));
  }

  @Test
  public void testMDCProperties() {
    String input = "20:44:20.120 [JGroups-Executor-17] INFO c._.u.s.xmpp.server.XMPPConnection SID:123abc CID:456xyz - START handlePresence(PbxUserPresence{extension='1234', message='', status=3, pbxId='customerABC', pnRegistered=false, timestamp=0})";
    Decoder decoder = new Decoder("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} SID:%X{SID} CID:%X{CID} - %msg%n");
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("JGroups-Executor-17", event.getThreadName());
    assertEquals(Level.INFO, event.getLevel());
    assertEquals("c._.u.s.xmpp.server.XMPPConnection", event.getLoggerName());
    assertEquals("START handlePresence(PbxUserPresence{extension='1234', message='', status=3, pbxId='customerABC', pnRegistered=false, timestamp=0})", event.getMessage());
    assertEquals("123abc", event.getMDCPropertyMap().get("SID"));
    assertEquals("456xyz", event.getMDCPropertyMap().get("CID"));
    Offset offset = event.mdcPropertyOffsets.get("SID");
    assertEquals("123abc", input.substring(offset.start, offset.end));
    offset = event.mdcPropertyOffsets.get("CID");
    assertEquals("456xyz", input.substring(offset.start, offset.end));

    // pattern with default values
    decoder = new Decoder("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} SID:%X{SID:-123} CID:%X{CID:-456} - %msg%n");
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("123abc", event.getMDCPropertyMap().get("SID"));
    assertEquals("456xyz", event.getMDCPropertyMap().get("CID"));
    offset = event.mdcPropertyOffsets.get("SID");
    assertEquals("123abc", input.substring(offset.start, offset.end));
    offset = event.mdcPropertyOffsets.get("CID");
    assertEquals("456xyz", input.substring(offset.start, offset.end));

    // no value in MDC
    input = "21:22:07.629 [Incoming-11,shared=uc-transport] INFO o.a.v.x.e.w.server.XMPPWebsocket SID: CID: - > TO_IP /0.0.0.0:8204";
    decoder = new Decoder("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} SID:%X{SID} CID:%X{CID} - %msg%n");
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals(Level.INFO, event.getLevel());
    assertEquals("o.a.v.x.e.w.server.XMPPWebsocket", event.getLoggerName());
    assertTrue(event.getMDCPropertyMap().isEmpty());
    assertTrue(event.mdcPropertyOffsets.isEmpty());

    // pattern without key
    decoder = new Decoder("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} ID:%X - %msg%n");
    input = "20:44:20.120 [JGroups-Executor-17] INFO c._.u.s.xmpp.server.XMPPConnection ID:SID=123abc,CID=456xyz - START handlePresence(PbxUserPresence{extension='1234', message='', status=3, pbxId='customerABC', pnRegistered=false, timestamp=0})";
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("123abc", event.getMDCPropertyMap().get("SID"));
    assertEquals("456xyz", event.getMDCPropertyMap().get("CID"));
    offset = event.mdcPropertyOffsets.get("SID");
    assertEquals("123abc", input.substring(offset.start, offset.end));
    offset = event.mdcPropertyOffsets.get("CID");
    assertEquals("456xyz", input.substring(offset.start, offset.end));

    input = "20:44:20.120 [JGroups-Executor-17] INFO c._.u.s.xmpp.server.XMPPConnection ID:SID=123abc, CID=456xyz - START handlePresence(PbxUserPresence{extension='1234', message='', status=3, pbxId='customerABC', pnRegistered=false, timestamp=0})";
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("123abc", event.getMDCPropertyMap().get("SID"));
    assertEquals("456xyz", event.getMDCPropertyMap().get("CID"));
    offset = event.mdcPropertyOffsets.get("SID");
    assertEquals("123abc", input.substring(offset.start, offset.end));
    offset = event.mdcPropertyOffsets.get("CID");
    assertEquals("456xyz", input.substring(offset.start, offset.end));

    input = "20:44:20.120 [JGroups-Executor-17] INFO c._.u.s.xmpp.server.XMPPConnection ID: - START handlePresence(PbxUserPresence{extension='1234', message='', status=3, pbxId='customerABC', pnRegistered=false, timestamp=0})";
    event = (StaticLoggingEvent)decoder.decode(input);
    assertTrue(event.getMDCPropertyMap().isEmpty());
    assertTrue(event.mdcPropertyOffsets.isEmpty());

    // key contains dash '_'
    decoder = new Decoder("%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{request_id}] - %msg%n");
    input = "2018-02-04 12:00:00.000 [123xyz] - test test test\n";
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("123xyz", event.getMDCPropertyMap().get("request_id"));

    // key contains dash '-'
    decoder = new Decoder("%d{yyyy-MM-dd HH:mm:ss.SSS} [%X{request-id}] - %msg%n");
    input = "2018-02-04 12:00:00.000 [123xyz] - test test test\n";
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("123xyz", event.getMDCPropertyMap().get("request-id"));

    // value contains space
    input = "2018-02-04 12:00:00.000 [123 xyz] - test test test\n";
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("123 xyz", event.getMDCPropertyMap().get("request-id"));

    // key-value pairs where value contains space
    decoder = new Decoder("%d{yyyy-MM-dd HH:mm:ss.SSS} [%X] - %msg%n");
    input = "2018-02-04 12:00:00.000 [request-id=b3735b37e2bb48bb871937826fccbc21, src-addr=172.68.226.92 185.221.220.107, src-port=33146, dst-addr=192.168.134.77, dst-port=9124] - test test test";
    event = (StaticLoggingEvent)decoder.decode(input);
    assertEquals("b3735b37e2bb48bb871937826fccbc21", event.getMDCPropertyMap().get("request-id"));
    assertEquals("172.68.226.92 185.221.220.107", event.getMDCPropertyMap().get("src-addr"));
    assertEquals("33146", event.getMDCPropertyMap().get("src-port"));
    assertEquals("192.168.134.77", event.getMDCPropertyMap().get("dst-addr"));
    assertEquals("9124", event.getMDCPropertyMap().get("dst-port"));
  }

  private String getMessage(String message) {
    final String INPUT = "2013-06-12 15:27:15.044 INFO: " + message;
    final String PATT = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level: %msg%n";
    Decoder decoder = new Decoder(PATT);
    StaticLoggingEvent event = (StaticLoggingEvent)decoder.decode(INPUT);
    assertNotNull(event);
    assertEquals(message, INPUT.substring(event.messageOffset.start, event.messageOffset.end));
    return event.getMessage();
  }
}
