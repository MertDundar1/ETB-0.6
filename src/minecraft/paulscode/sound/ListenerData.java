package paulscode.sound;













public class ListenerData
{
  public Vector3D position;
  










  public Vector3D lookAt;
  










  public Vector3D up;
  










  public Vector3D velocity;
  










  public float angle = 0.0F;
  



  public ListenerData()
  {
    position = new Vector3D(0.0F, 0.0F, 0.0F);
    lookAt = new Vector3D(0.0F, 0.0F, -1.0F);
    up = new Vector3D(0.0F, 1.0F, 0.0F);
    velocity = new Vector3D(0.0F, 0.0F, 0.0F);
    angle = 0.0F;
  }
  















  public ListenerData(float pX, float pY, float pZ, float lX, float lY, float lZ, float uX, float uY, float uZ, float a)
  {
    position = new Vector3D(pX, pY, pZ);
    lookAt = new Vector3D(lX, lY, lZ);
    up = new Vector3D(uX, uY, uZ);
    velocity = new Vector3D(0.0F, 0.0F, 0.0F);
    angle = a;
  }
  








  public ListenerData(Vector3D p, Vector3D l, Vector3D u, float a)
  {
    position = p.clone();
    lookAt = l.clone();
    up = u.clone();
    velocity = new Vector3D(0.0F, 0.0F, 0.0F);
    angle = a;
  }
  















  public void setData(float pX, float pY, float pZ, float lX, float lY, float lZ, float uX, float uY, float uZ, float a)
  {
    position.x = pX;
    position.y = pY;
    position.z = pZ;
    lookAt.x = lX;
    lookAt.y = lY;
    lookAt.z = lZ;
    up.x = uX;
    up.y = uY;
    up.z = uZ;
    angle = a;
  }
  








  public void setData(Vector3D p, Vector3D l, Vector3D u, float a)
  {
    position.x = x;
    position.y = y;
    position.z = z;
    lookAt.x = x;
    lookAt.y = y;
    lookAt.z = z;
    up.x = x;
    up.y = y;
    up.z = z;
    angle = a;
  }
  




  public void setData(ListenerData l)
  {
    position.x = position.x;
    position.y = position.y;
    position.z = position.z;
    lookAt.x = lookAt.x;
    lookAt.y = lookAt.y;
    lookAt.z = lookAt.z;
    up.x = up.x;
    up.y = up.y;
    up.z = up.z;
    angle = angle;
  }
  






  public void setPosition(float x, float y, float z)
  {
    position.x = x;
    position.y = y;
    position.z = z;
  }
  




  public void setPosition(Vector3D p)
  {
    position.x = x;
    position.y = y;
    position.z = z;
  }
  










  public void setOrientation(float lX, float lY, float lZ, float uX, float uY, float uZ)
  {
    lookAt.x = lX;
    lookAt.y = lY;
    lookAt.z = lZ;
    up.x = uX;
    up.y = uY;
    up.z = uZ;
  }
  





  public void setOrientation(Vector3D l, Vector3D u)
  {
    lookAt.x = x;
    lookAt.y = y;
    lookAt.z = z;
    up.x = x;
    up.y = y;
    up.z = z;
  }
  




  public void setVelocity(Vector3D v)
  {
    velocity.x = x;
    velocity.y = y;
    velocity.z = z;
  }
  






  public void setVelocity(float x, float y, float z)
  {
    velocity.x = x;
    velocity.y = y;
    velocity.z = z;
  }
  




  public void setAngle(float a)
  {
    angle = a;
    lookAt.x = (-1.0F * (float)Math.sin(angle));
    lookAt.z = (-1.0F * (float)Math.cos(angle));
  }
}
