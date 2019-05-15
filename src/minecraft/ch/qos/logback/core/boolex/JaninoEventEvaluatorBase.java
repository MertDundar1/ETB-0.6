package ch.qos.logback.core.boolex;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.janino.ScriptEvaluator;













public abstract class JaninoEventEvaluatorBase<E>
  extends EventEvaluatorBase<E>
{
  static Class<?> EXPRESSION_TYPE;
  static Class<?>[] THROWN_EXCEPTIONS;
  public static final int ERROR_THRESHOLD = 4;
  private String expression;
  ScriptEvaluator scriptEvaluator;
  
  static
  {
    EXPRESSION_TYPE = Boolean.TYPE;
    THROWN_EXCEPTIONS = new Class[1];
    


    THROWN_EXCEPTIONS[0] = EvaluationException.class;
  }
  



  private int errorCount = 0;
  








  protected List<Matcher> matcherList = new ArrayList();
  
  public void start()
  {
    try {
      assert (context != null);
      scriptEvaluator = new ScriptEvaluator(getDecoratedExpression(), EXPRESSION_TYPE, getParameterNames(), getParameterTypes(), THROWN_EXCEPTIONS);
      
      super.start();
    } catch (Exception e) {
      addError("Could not start evaluator with expression [" + expression + "]", e);
    }
  }
  
  public boolean evaluate(E event) throws EvaluationException
  {
    if (!isStarted()) {
      throw new IllegalStateException("Evaluator [" + name + "] was called in stopped state");
    }
    try
    {
      Boolean result = (Boolean)scriptEvaluator.evaluate(getParameterValues(event));
      return result.booleanValue();
    } catch (Exception ex) {
      errorCount += 1;
      if (errorCount >= 4) {
        stop();
      }
      throw new EvaluationException("Evaluator [" + name + "] caused an exception", ex);
    }
  }
  
  public String getExpression()
  {
    return expression;
  }
  
  public void setExpression(String expression) {
    this.expression = expression;
  }
  
  public void addMatcher(Matcher matcher) {
    matcherList.add(matcher);
  }
  
  public List<Matcher> getMatcherList() {
    return matcherList;
  }
  
  public JaninoEventEvaluatorBase() {}
  
  protected abstract String getDecoratedExpression();
  
  protected abstract String[] getParameterNames();
  
  protected abstract Class<?>[] getParameterTypes();
  
  protected abstract Object[] getParameterValues(E paramE);
}
