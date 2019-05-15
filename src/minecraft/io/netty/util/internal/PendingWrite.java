package io.netty.util.internal;

import io.netty.util.Recycler;
import io.netty.util.Recycler.Handle;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

















public final class PendingWrite
{
  private static final Recycler<PendingWrite> RECYCLER = new Recycler()
  {
    protected PendingWrite newObject(Recycler.Handle handle) {
      return new PendingWrite(handle, null);
    }
  };
  private final Recycler.Handle handle;
  private Object msg;
  private Promise<Void> promise;
  
  public static PendingWrite newInstance(Object msg, Promise<Void> promise) {
    PendingWrite pending = (PendingWrite)RECYCLER.get();
    msg = msg;
    promise = promise;
    return pending;
  }
  



  private PendingWrite(Recycler.Handle handle)
  {
    this.handle = handle;
  }
  


  public boolean recycle()
  {
    msg = null;
    promise = null;
    return RECYCLER.recycle(this, handle);
  }
  


  public boolean failAndRecycle(Throwable cause)
  {
    ReferenceCountUtil.release(msg);
    if (promise != null) {
      promise.setFailure(cause);
    }
    return recycle();
  }
  


  public boolean successAndRecycle()
  {
    if (promise != null) {
      promise.setSuccess(null);
    }
    return recycle();
  }
  
  public Object msg() {
    return msg;
  }
  
  public Promise<Void> promise() {
    return promise;
  }
  


  public Promise<Void> recycleAndGet()
  {
    Promise<Void> promise = this.promise;
    recycle();
    return promise;
  }
}
