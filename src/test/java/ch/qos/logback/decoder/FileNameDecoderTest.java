package ch.qos.logback.decoder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FileNameDecoderTest {
    @Test
    public void testFileName() {
        var decoder = new Decoder("%file %msg%n");
        var event = (StaticLoggingEvent) decoder.decode("Test.java test message");
        assertEquals("Test.java", event.getFileNameOfCaller());
        assertEquals("test message", event.getMessage());

        decoder = new Decoder("%F %msg%n");
        event = (StaticLoggingEvent) decoder.decode("Test.java test message");
        assertEquals("Test.java", event.getFileNameOfCaller());
        assertEquals("test message", event.getMessage());
    }
}
