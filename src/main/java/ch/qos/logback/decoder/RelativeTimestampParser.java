package ch.qos.logback.decoder;

import ch.qos.logback.core.pattern.parser2.PatternInfo;

public class RelativeTimestampParser implements FieldCapturer<StaticLoggingEvent> {
    @Override
    public void captureField(StaticLoggingEvent event, CharSequence text, Offset offset, PatternInfo info) {
        try {
            event.setRelativeTimestamp(Long.parseLong(text, 0, text.length(), 10));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse the relative timestamp: " + text);
        }
    }
}
