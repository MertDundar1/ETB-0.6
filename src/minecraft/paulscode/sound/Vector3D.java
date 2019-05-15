package paulscode.sound;














public class Vector3D
{
  public float x;
  












  public float y;
  












  public float z;
  












  public Vector3D()
  {
    x = 0.0F;
    y = 0.0F;
    z = 0.0F;
  }
  






  public Vector3D(float nx, float ny, float nz)
  {
    x = nx;
    y = ny;
    z = nz;
  }
  





  public Vector3D clone()
  {
    return new Vector3D(x, y, z);
  }
  






  public Vector3D cross(Vector3D A, Vector3D B)
  {
    return new Vector3D(y * z - y * z, z * x - z * x, x * y - x * y);
  }
  








  public Vector3D cross(Vector3D B)
  {
    return new Vector3D(y * z - y * z, z * x - z * x, x * y - x * y);
  }
  










  public float dot(Vector3D A, Vector3D B)
  {
    return x * x + y * y + z * z;
  }
  





  public float dot(Vector3D B)
  {
    return x * x + y * y + z * z;
  }
  






  public Vector3D add(Vector3D A, Vector3D B)
  {
    return new Vector3D(x + x, y + y, z + z);
  }
  





  public Vector3D add(Vector3D B)
  {
    return new Vector3D(x + x, y + y, z + z);
  }
  






  public Vector3D subtract(Vector3D A, Vector3D B)
  {
    return new Vector3D(x - x, y - y, z - z);
  }
  





  public Vector3D subtract(Vector3D B)
  {
    return new Vector3D(x - x, y - y, z - z);
  }
  




  public float length()
  {
    return (float)Math.sqrt(x * x + y * y + z * z);
  }
  



  public void normalize()
  {
    double t = Math.sqrt(x * x + y * y + z * z);
    x = ((float)(x / t));
    y = ((float)(y / t));
    z = ((float)(z / t));
  }
  





  public String toString()
  {
    return "Vector3D (" + x + ", " + y + ", " + z + ")";
  }
}
