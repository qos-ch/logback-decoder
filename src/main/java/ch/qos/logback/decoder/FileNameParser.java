package ch.qos.logback.decoder;

import ch.qos.logback.core.pattern.parser2.PatternInfo;

public class FileNameParser implements FieldCapturer<StaticLoggingEvent> {
    @Override
    public void captureField(StaticLoggingEvent event, CharSequence text, Offset offset, PatternInfo info) {
        event.setFileNameOfCaller(text.toString());
        event.fileNameOffset = offset;
    }
}
