package ch.qos.logback.decoder;

import ch.qos.logback.core.pattern.parser2.PatternInfo;

public class MDCMapParser implements FieldCapturer<StaticLoggingEvent> {
    @Override
    public void captureField(StaticLoggingEvent event, CharSequence field, Offset offset, PatternInfo info) {
        // value is CSV. Convert it into Map.
        int startOffset = offset.start;

        int index = 0;
        try {
            while (index < field.length()) {
                // skip leading space
                while (field.charAt(index) == ' ') index++;
                // get key
                int keyStart = index;
                while (field.charAt(index) != '=') index++;
                String key = field.subSequence(keyStart, index).toString();
                index++;
                int valueStart = index;
                while (index < field.length() && field.charAt(index) != ',') index++;
                event.putMDC(key, field.subSequence(valueStart, index).toString(), new Offset(startOffset + valueStart, startOffset + index));
                index++;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse " + field + " as MDC", e);
        }
    }
}
