package ch.qos.logback.classic.net;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.net.JMSAppenderBase;
import ch.qos.logback.core.spi.PreSerializationTransformer;
import java.io.Serializable;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;



























public class JMSTopicAppender
  extends JMSAppenderBase<ILoggingEvent>
{
  static int SUCCESSIVE_FAILURE_LIMIT = 3;
  
  String topicBindingName;
  
  String tcfBindingName;
  TopicConnection topicConnection;
  TopicSession topicSession;
  TopicPublisher topicPublisher;
  int successiveFailureCount = 0;
  
  private PreSerializationTransformer<ILoggingEvent> pst = new LoggingEventPreSerializationTransformer();
  

  public JMSTopicAppender() {}
  

  public void setTopicConnectionFactoryBindingName(String tcfBindingName)
  {
    this.tcfBindingName = tcfBindingName;
  }
  


  public String getTopicConnectionFactoryBindingName()
  {
    return tcfBindingName;
  }
  



  public void setTopicBindingName(String topicBindingName)
  {
    this.topicBindingName = topicBindingName;
  }
  


  public String getTopicBindingName()
  {
    return topicBindingName;
  }
  



  public void start()
  {
    try
    {
      Context jndi = buildJNDIContext();
      

      TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory)lookup(jndi, tcfBindingName);
      

      if (userName != null) {
        topicConnection = topicConnectionFactory.createTopicConnection(userName, password);
      }
      else {
        topicConnection = topicConnectionFactory.createTopicConnection();
      }
      



      topicSession = topicConnection.createTopicSession(false, 1);
      


      Topic topic = (Topic)lookup(jndi, topicBindingName);
      

      topicPublisher = topicSession.createPublisher(topic);
      

      topicConnection.start();
      
      jndi.close();
    } catch (Exception e) {
      addError("Error while activating options for appender named [" + name + "].", e);
    }
    

    if ((topicConnection != null) && (topicSession != null) && (topicPublisher != null))
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
      if (topicSession != null) {
        topicSession.close();
      }
      if (topicConnection != null) {
        topicConnection.close();
      }
    } catch (Exception e) {
      addError("Error while closing JMSAppender [" + name + "].", e);
    }
    

    topicPublisher = null;
    topicSession = null;
    topicConnection = null;
  }
  




  public void append(ILoggingEvent event)
  {
    if (!isStarted()) {
      return;
    }
    try
    {
      ObjectMessage msg = topicSession.createObjectMessage();
      Serializable so = pst.transform(event);
      msg.setObject(so);
      topicPublisher.publish(msg);
      successiveFailureCount = 0;
    } catch (Exception e) {
      successiveFailureCount += 1;
      if (successiveFailureCount > SUCCESSIVE_FAILURE_LIMIT) {
        stop();
      }
      addError("Could not publish message in JMSTopicAppender [" + name + "].", e);
    }
  }
  



  protected TopicConnection getTopicConnection()
  {
    return topicConnection;
  }
  



  protected TopicSession getTopicSession()
  {
    return topicSession;
  }
  



  protected TopicPublisher getTopicPublisher()
  {
    return topicPublisher;
  }
}
