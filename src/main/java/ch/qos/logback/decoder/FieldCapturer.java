package ch.qos.logback.decoder;


public abstract class FieldCapturer<E> {

  protected boolean capturing;
  protected String regexExpression;

  public String getRegexExpression() {
    return regexExpression;
  }

  public boolean isCapturing() {
    return capturing;
  }

  abstract void captureField(E event, String fieldAsStr);


}
