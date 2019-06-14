package ch.qos.logback.decoder;

import ch.qos.logback.core.pattern.parser2.PatternInfo;

public class PropertyParser implements FieldCapturer<StaticLoggingEvent> {
    private final String key;

    public PropertyParser(String key) {
        this.key = key;
    }

    @Override
    public void captureField(StaticLoggingEvent event, CharSequence field, Offset offset, PatternInfo info) {
        if (field.length() > 0)  {
            event.putProperty(key, field.toString(), offset);
        }
    }
}
