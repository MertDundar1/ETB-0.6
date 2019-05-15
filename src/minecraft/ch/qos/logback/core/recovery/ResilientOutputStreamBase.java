package ch.qos.logback.core.recovery;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;














public abstract class ResilientOutputStreamBase
  extends OutputStream
{
  static final int STATUS_COUNT_LIMIT = 8;
  private int noContextWarning = 0;
  private int statusCount = 0;
  
  private Context context;
  
  private RecoveryCoordinator recoveryCoordinator;
  protected OutputStream os;
  protected boolean presumedClean = true;
  
  public ResilientOutputStreamBase() {}
  
  private boolean isPresumedInError() { return (recoveryCoordinator != null) && (!presumedClean); }
  
  public void write(byte[] b, int off, int len)
  {
    if (isPresumedInError()) {
      if (!recoveryCoordinator.isTooSoon()) {
        attemptRecovery();
      }
      return;
    }
    try
    {
      os.write(b, off, len);
      postSuccessfulWrite();
    } catch (IOException e) {
      postIOFailure(e);
    }
  }
  
  public void write(int b)
  {
    if (isPresumedInError()) {
      if (!recoveryCoordinator.isTooSoon()) {
        attemptRecovery();
      }
      return;
    }
    try {
      os.write(b);
      postSuccessfulWrite();
    } catch (IOException e) {
      postIOFailure(e);
    }
  }
  
  public void flush()
  {
    if (os != null) {
      try {
        os.flush();
        postSuccessfulWrite();
      } catch (IOException e) {
        postIOFailure(e);
      }
    }
  }
  
  abstract String getDescription();
  
  abstract OutputStream openNewOutputStream() throws IOException;
  
  private void postSuccessfulWrite() {
    if (recoveryCoordinator != null) {
      recoveryCoordinator = null;
      statusCount = 0;
      addStatus(new InfoStatus("Recovered from IO failure on " + getDescription(), this));
    }
  }
  
  public void postIOFailure(IOException e)
  {
    addStatusIfCountNotOverLimit(new ErrorStatus("IO failure while writing to " + getDescription(), this, e));
    
    presumedClean = false;
    if (recoveryCoordinator == null) {
      recoveryCoordinator = new RecoveryCoordinator();
    }
  }
  
  public void close() throws IOException
  {
    if (os != null) {
      os.close();
    }
  }
  
  void attemptRecovery() {
    try {
      close();
    }
    catch (IOException e) {}
    
    addStatusIfCountNotOverLimit(new InfoStatus("Attempting to recover from IO failure on " + getDescription(), this));
    

    try
    {
      os = openNewOutputStream();
      presumedClean = true;
    } catch (IOException e) {
      addStatusIfCountNotOverLimit(new ErrorStatus("Failed to open " + getDescription(), this, e));
    }
  }
  
  void addStatusIfCountNotOverLimit(Status s)
  {
    statusCount += 1;
    if (statusCount < 8) {
      addStatus(s);
    }
    
    if (statusCount == 8) {
      addStatus(s);
      addStatus(new InfoStatus("Will supress future messages regarding " + getDescription(), this));
    }
  }
  
  public void addStatus(Status status)
  {
    if (context == null) {
      if (noContextWarning++ == 0) {
        System.out.println("LOGBACK: No context given for " + this);
      }
      return;
    }
    StatusManager sm = context.getStatusManager();
    if (sm != null) {
      sm.add(status);
    }
  }
  
  public Context getContext() {
    return context;
  }
  
  public void setContext(Context context) {
    this.context = context;
  }
}
