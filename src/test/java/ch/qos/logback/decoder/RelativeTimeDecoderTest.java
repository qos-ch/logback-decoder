package ch.qos.logback.decoder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RelativeTimeDecoderTest {
    @Test
    public void testRelativeTime() {
        var decoder = new Decoder("%relative %msg%n");
        var event = (StaticLoggingEvent) decoder.decode("1234 test message");
        assertEquals(1234L, event.getRelativeTimestamp());
        assertEquals("test message", event.getMessage());

        decoder = new Decoder("%r %msg%n");
        event = (StaticLoggingEvent) decoder.decode("1234 test message");
        assertEquals(1234L, event.getRelativeTimestamp());
        assertEquals("test message", event.getMessage());
    }

    @Test
    public void testRelativeTimeWithModifier() {
        var decoder = new Decoder("%-4relative %msg%n");
        var event = (StaticLoggingEvent) decoder.decode("1234 test message");
        assertEquals(1234L, event.getRelativeTimestamp());
        assertEquals("test message", event.getMessage());

        event = (StaticLoggingEvent) decoder.decode("123  test message");
        assertEquals(123L, event.getRelativeTimestamp());
        assertEquals("test message", event.getMessage());

        decoder = new Decoder("%4relative %msg%n");
        event = (StaticLoggingEvent) decoder.decode("1234 test message");
        assertEquals(1234L, event.getRelativeTimestamp());
        assertEquals("test message", event.getMessage());

        decoder = new Decoder("%4relative %msg%n");
        event = (StaticLoggingEvent) decoder.decode("123 test message");
        assertEquals(123L, event.getRelativeTimestamp());
        assertEquals("test message", event.getMessage());
    }
}
