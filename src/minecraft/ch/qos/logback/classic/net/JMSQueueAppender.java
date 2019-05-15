package ch.qos.logback.classic.net;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.JMSAppenderBase;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import java.io.Serializable;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.Context;



























public class JMSQueueAppender
  extends JMSAppenderBase<ILoggingEvent>
{
  static int SUCCESSIVE_FAILURE_LIMIT = 3;
  
  String queueBindingName;
  
  String qcfBindingName;
  QueueConnection queueConnection;
  QueueSession queueSession;
  QueueSender queueSender;
  int successiveFailureCount = 0;
  
  private PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
  

  public JMSQueueAppender() {}
  

  public void setQueueConnectionFactoryBindingName(String qcfBindingName)
  {
    this.qcfBindingName = qcfBindingName;
  }
  


  public String getQueueConnectionFactoryBindingName()
  {
    return qcfBindingName;
  }
  



  public void setQueueBindingName(String queueBindingName)
  {
    this.queueBindingName = queueBindingName;
  }
  


  public String getQueueBindingName()
  {
    return queueBindingName;
  }
  



  public void start()
  {
    try
    {
      Context jndi = buildJNDIContext();
      

      QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory)lookup(jndi, qcfBindingName);
      

      if (userName != null) {
        queueConnection = queueConnectionFactory.createQueueConnection(userName, password);
      }
      else {
        queueConnection = queueConnectionFactory.createQueueConnection();
      }
      



      queueSession = queueConnection.createQueueSession(false, 1);
      


      Queue queue = (Queue)lookup(jndi, queueBindingName);
      

      queueSender = queueSession.createSender(queue);
      

      queueConnection.start();
      
      jndi.close();
    } catch (Exception e) {
      addError("Error while activating options for appender named [" + name + "].", e);
    }
    

    if ((queueConnection != null) && (queueSession != null) && (queueSender != null))
    {
      super.start();
    }
  }
  




  public synchronized void stop()
  {
    if (!started) {
      return;
    }
    
    started = false;
    try
    {
      if (queueSession != null) {
        queueSession.close();
      }
      if (queueConnection != null) {
        queueConnection.close();
      }
    } catch (Exception e) {
      addError("Error while closing JMSAppender [" + name + "].", e);
    }
    

    queueSender = null;
    queueSession = null;
    queueConnection = null;
  }
  



  public void append(ILoggingEvent event)
  {
    if (!isStarted()) {
      return;
    }
    try
    {
      ObjectMessage msg = queueSession.createObjectMessage();
      Serializable so = pst.transform(event);
      msg.setObject(so);
      queueSender.send(msg);
      successiveFailureCount = 0;
    } catch (Exception e) {
      successiveFailureCount += 1;
      if (successiveFailureCount > SUCCESSIVE_FAILURE_LIMIT) {
        stop();
      }
      addError("Could not send message in JMSQueueAppender [" + name + "].", e);
    }
  }
  




  protected QueueConnection getQueueConnection()
  {
    return queueConnection;
  }
  



  protected QueueSession getQueueSession()
  {
    return queueSession;
  }
  



  protected QueueSender getQueueSender()
  {
    return queueSender;
  }
}
