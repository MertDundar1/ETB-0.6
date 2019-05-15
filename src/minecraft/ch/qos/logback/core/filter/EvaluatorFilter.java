package ch.qos.logback.core.filter;

import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.spi.FilterReply;




























public class EvaluatorFilter<E>
  extends AbstractMatcherFilter<E>
{
  EventEvaluator<E> evaluator;
  
  public EvaluatorFilter() {}
  
  public void start()
  {
    if (evaluator != null) {
      super.start();
    } else {
      addError("No evaluator set for filter " + getName());
    }
  }
  
  public EventEvaluator<E> getEvaluator() {
    return evaluator;
  }
  
  public void setEvaluator(EventEvaluator<E> evaluator) {
    this.evaluator = evaluator;
  }
  

  public FilterReply decide(E event)
  {
    if ((!isStarted()) || (!evaluator.isStarted())) {
      return FilterReply.NEUTRAL;
    }
    try {
      if (evaluator.evaluate(event)) {
        return onMatch;
      }
      return onMismatch;
    }
    catch (EvaluationException e) {
      addError("Evaluator " + evaluator.getName() + " threw an exception", e); }
    return FilterReply.NEUTRAL;
  }
}
