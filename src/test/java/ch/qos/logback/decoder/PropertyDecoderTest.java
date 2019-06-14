package ch.qos.logback.decoder;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PropertyDecoderTest {
    @Test
    public void testProperty() {
        var decoder = new Decoder("abc=%property{abc} %msg%n");
        var event = (StaticLoggingEvent) decoder.decode("abc=xyz test message");
        assertEquals(Map.of("abc", "xyz"), event.getProperties());
        assertEquals(4, event.propertyOffsets.get("abc").start);
        assertEquals(7, event.propertyOffsets.get("abc").end);
        assertEquals("test message", event.getMessage());

        decoder = new Decoder("abc=%property{abc} def=%property{def} %msg%n");
        event = (StaticLoggingEvent) decoder.decode("abc=xyz def=zyx test message");
        assertEquals(Map.of("abc", "xyz", "def", "zyx"), event.getProperties());
        assertEquals(4, event.propertyOffsets.get("abc").start);
        assertEquals(7, event.propertyOffsets.get("abc").end);
        assertEquals(12, event.propertyOffsets.get("def").start);
        assertEquals(15, event.propertyOffsets.get("def").end);
        assertEquals("test message", event.getMessage());
    }

    @Test
    public void testPropertyWithNoKey() {
        var decoder = new Decoder("abc=%property{abc} %msg%n");
        var event = (StaticLoggingEvent) decoder.decode("abc=Property_HAS_NO_KEY test message");
        assertEquals(Map.of("abc", "Property_HAS_NO_KEY"), event.getProperties());
        assertEquals("test message", event.getMessage());
    }
}
