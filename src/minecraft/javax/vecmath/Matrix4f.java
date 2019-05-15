package javax.vecmath;

import java.io.Serializable;
































































































































public class Matrix4f
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -8405036035410109353L;
  public float m00;
  public float m01;
  public float m02;
  public float m03;
  public float m10;
  public float m11;
  public float m12;
  public float m13;
  public float m20;
  public float m21;
  public float m22;
  public float m23;
  public float m30;
  public float m31;
  public float m32;
  public float m33;
  private static final double EPS = 1.0E-8D;
  
  public Matrix4f(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    this.m03 = m03;
    
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    this.m13 = m13;
    
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
    this.m23 = m23;
    
    this.m30 = m30;
    this.m31 = m31;
    this.m32 = m32;
    this.m33 = m33;
  }
  






  public Matrix4f(float[] v)
  {
    m00 = v[0];
    m01 = v[1];
    m02 = v[2];
    m03 = v[3];
    
    m10 = v[4];
    m11 = v[5];
    m12 = v[6];
    m13 = v[7];
    
    m20 = v[8];
    m21 = v[9];
    m22 = v[10];
    m23 = v[11];
    
    m30 = v[12];
    m31 = v[13];
    m32 = v[14];
    m33 = v[15];
  }
  










  public Matrix4f(Quat4f q1, Vector3f t1, float s)
  {
    m00 = ((float)(s * (1.0D - 2.0D * y * y - 2.0D * z * z)));
    m10 = ((float)(s * (2.0D * (x * y + w * z))));
    m20 = ((float)(s * (2.0D * (x * z - w * y))));
    
    m01 = ((float)(s * (2.0D * (x * y - w * z))));
    m11 = ((float)(s * (1.0D - 2.0D * x * x - 2.0D * z * z)));
    m21 = ((float)(s * (2.0D * (y * z + w * x))));
    
    m02 = ((float)(s * (2.0D * (x * z + w * y))));
    m12 = ((float)(s * (2.0D * (y * z - w * x))));
    m22 = ((float)(s * (1.0D - 2.0D * x * x - 2.0D * y * y)));
    
    m03 = x;
    m13 = y;
    m23 = z;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  






  public Matrix4f(Matrix4d m1)
  {
    m00 = ((float)m00);
    m01 = ((float)m01);
    m02 = ((float)m02);
    m03 = ((float)m03);
    
    m10 = ((float)m10);
    m11 = ((float)m11);
    m12 = ((float)m12);
    m13 = ((float)m13);
    
    m20 = ((float)m20);
    m21 = ((float)m21);
    m22 = ((float)m22);
    m23 = ((float)m23);
    
    m30 = ((float)m30);
    m31 = ((float)m31);
    m32 = ((float)m32);
    m33 = ((float)m33);
  }
  







  public Matrix4f(Matrix4f m1)
  {
    m00 = m00;
    m01 = m01;
    m02 = m02;
    m03 = m03;
    
    m10 = m10;
    m11 = m11;
    m12 = m12;
    m13 = m13;
    
    m20 = m20;
    m21 = m21;
    m22 = m22;
    m23 = m23;
    
    m30 = m30;
    m31 = m31;
    m32 = m32;
    m33 = m33;
  }
  











  public Matrix4f(Matrix3f m1, Vector3f t1, float s)
  {
    m00 = (m00 * s);
    m01 = (m01 * s);
    m02 = (m02 * s);
    m03 = x;
    
    m10 = (m10 * s);
    m11 = (m11 * s);
    m12 = (m12 * s);
    m13 = y;
    
    m20 = (m20 * s);
    m21 = (m21 * s);
    m22 = (m22 * s);
    m23 = z;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  





  public Matrix4f()
  {
    m00 = 0.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = 0.0F;
    
    m10 = 0.0F;
    m11 = 0.0F;
    m12 = 0.0F;
    m13 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 0.0F;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 0.0F;
  }
  




  public String toString()
  {
    return m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n" + m10 + ", " + m11 + ", " + m12 + ", " + m13 + "\n" + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n" + m30 + ", " + m31 + ", " + m32 + ", " + m33 + "\n";
  }
  







  public final void setIdentity()
  {
    m00 = 1.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = 0.0F;
    
    m10 = 0.0F;
    m11 = 1.0F;
    m12 = 0.0F;
    m13 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 1.0F;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  






  public final void setElement(int row, int column, float value)
  {
    switch (row)
    {
    case 0: 
      switch (column)
      {
      case 0: 
        m00 = value;
        break;
      case 1: 
        m01 = value;
        break;
      case 2: 
        m02 = value;
        break;
      case 3: 
        m03 = value;
        break;
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
      }
      
      break;
    case 1: 
      switch (column)
      {
      case 0: 
        m10 = value;
        break;
      case 1: 
        m11 = value;
        break;
      case 2: 
        m12 = value;
        break;
      case 3: 
        m13 = value;
        break;
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
      }
      
      break;
    case 2: 
      switch (column)
      {
      case 0: 
        m20 = value;
        break;
      case 1: 
        m21 = value;
        break;
      case 2: 
        m22 = value;
        break;
      case 3: 
        m23 = value;
        break;
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
      }
      
      break;
    case 3: 
      switch (column)
      {
      case 0: 
        m30 = value;
        break;
      case 1: 
        m31 = value;
        break;
      case 2: 
        m32 = value;
        break;
      case 3: 
        m33 = value;
        break;
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
      }
      
      break;
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
    }
    
  }
  





  public final float getElement(int row, int column)
  {
    switch (row)
    {
    case 0: 
      switch (column)
      {
      case 0: 
        return m00;
      case 1: 
        return m01;
      case 2: 
        return m02;
      case 3: 
        return m03;
      }
      break;
    

    case 1: 
      switch (column)
      {
      case 0: 
        return m10;
      case 1: 
        return m11;
      case 2: 
        return m12;
      case 3: 
        return m13;
      }
      break;
    


    case 2: 
      switch (column)
      {
      case 0: 
        return m20;
      case 1: 
        return m21;
      case 2: 
        return m22;
      case 3: 
        return m23;
      }
      break;
    


    case 3: 
      switch (column)
      {
      case 0: 
        return m30;
      case 1: 
        return m31;
      case 2: 
        return m32;
      case 3: 
        return m33;
      }
      break;
    }
    
    



    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
  }
  




  public final void getRow(int row, Vector4f v)
  {
    if (row == 0) {
      x = m00;
      y = m01;
      z = m02;
      w = m03;
    } else if (row == 1) {
      x = m10;
      y = m11;
      z = m12;
      w = m13;
    } else if (row == 2) {
      x = m20;
      y = m21;
      z = m22;
      w = m23;
    } else if (row == 3) {
      x = m30;
      y = m31;
      z = m32;
      w = m33;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f2"));
    }
  }
  





  public final void getRow(int row, float[] v)
  {
    if (row == 0) {
      v[0] = m00;
      v[1] = m01;
      v[2] = m02;
      v[3] = m03;
    } else if (row == 1) {
      v[0] = m10;
      v[1] = m11;
      v[2] = m12;
      v[3] = m13;
    } else if (row == 2) {
      v[0] = m20;
      v[1] = m21;
      v[2] = m22;
      v[3] = m23;
    } else if (row == 3) {
      v[0] = m30;
      v[1] = m31;
      v[2] = m32;
      v[3] = m33;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f2"));
    }
  }
  






  public final void getColumn(int column, Vector4f v)
  {
    if (column == 0) {
      x = m00;
      y = m10;
      z = m20;
      w = m30;
    } else if (column == 1) {
      x = m01;
      y = m11;
      z = m21;
      w = m31;
    } else if (column == 2) {
      x = m02;
      y = m12;
      z = m22;
      w = m32;
    } else if (column == 3) {
      x = m03;
      y = m13;
      z = m23;
      w = m33;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f4"));
    }
  }
  






  public final void getColumn(int column, float[] v)
  {
    if (column == 0) {
      v[0] = m00;
      v[1] = m10;
      v[2] = m20;
      v[3] = m30;
    } else if (column == 1) {
      v[0] = m01;
      v[1] = m11;
      v[2] = m21;
      v[3] = m31;
    } else if (column == 2) {
      v[0] = m02;
      v[1] = m12;
      v[2] = m22;
      v[3] = m32;
    } else if (column == 3) {
      v[0] = m03;
      v[1] = m13;
      v[2] = m23;
      v[3] = m33;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f4"));
    }
  }
  








  public final void setScale(float scale)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((float)(tmp_rot[0] * scale));
    m01 = ((float)(tmp_rot[1] * scale));
    m02 = ((float)(tmp_rot[2] * scale));
    
    m10 = ((float)(tmp_rot[3] * scale));
    m11 = ((float)(tmp_rot[4] * scale));
    m12 = ((float)(tmp_rot[5] * scale));
    
    m20 = ((float)(tmp_rot[6] * scale));
    m21 = ((float)(tmp_rot[7] * scale));
    m22 = ((float)(tmp_rot[8] * scale));
  }
  







  public final void get(Matrix3d m1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = tmp_rot[0];
    m01 = tmp_rot[1];
    m02 = tmp_rot[2];
    
    m10 = tmp_rot[3];
    m11 = tmp_rot[4];
    m12 = tmp_rot[5];
    
    m20 = tmp_rot[6];
    m21 = tmp_rot[7];
    m22 = tmp_rot[8];
  }
  







  public final void get(Matrix3f m1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((float)tmp_rot[0]);
    m01 = ((float)tmp_rot[1]);
    m02 = ((float)tmp_rot[2]);
    
    m10 = ((float)tmp_rot[3]);
    m11 = ((float)tmp_rot[4]);
    m12 = ((float)tmp_rot[5]);
    
    m20 = ((float)tmp_rot[6]);
    m21 = ((float)tmp_rot[7]);
    m22 = ((float)tmp_rot[8]);
  }
  










  public final float get(Matrix3f m1, Vector3f t1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((float)tmp_rot[0]);
    m01 = ((float)tmp_rot[1]);
    m02 = ((float)tmp_rot[2]);
    
    m10 = ((float)tmp_rot[3]);
    m11 = ((float)tmp_rot[4]);
    m12 = ((float)tmp_rot[5]);
    
    m20 = ((float)tmp_rot[6]);
    m21 = ((float)tmp_rot[7]);
    m22 = ((float)tmp_rot[8]);
    
    x = m03;
    y = m13;
    z = m23;
    
    return (float)Matrix3d.max3(tmp_scale);
  }
  







  public final void get(Quat4f q1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    


    double ww = 0.25D * (1.0D + tmp_rot[0] + tmp_rot[4] + tmp_rot[8]);
    if ((ww < 0.0D ? -ww : ww) >= 1.0E-30D) {
      w = ((float)Math.sqrt(ww));
      ww = 0.25D / w;
      x = ((float)((tmp_rot[7] - tmp_rot[5]) * ww));
      y = ((float)((tmp_rot[2] - tmp_rot[6]) * ww));
      z = ((float)((tmp_rot[3] - tmp_rot[1]) * ww));
      return;
    }
    
    w = 0.0F;
    ww = -0.5D * (tmp_rot[4] + tmp_rot[8]);
    if ((ww < 0.0D ? -ww : ww) >= 1.0E-30D) {
      x = ((float)Math.sqrt(ww));
      ww = 0.5D / x;
      y = ((float)(tmp_rot[3] * ww));
      z = ((float)(tmp_rot[6] * ww));
      return;
    }
    
    x = 0.0F;
    ww = 0.5D * (1.0D - tmp_rot[8]);
    if ((ww < 0.0D ? -ww : ww) >= 1.0E-30D) {
      y = ((float)Math.sqrt(ww));
      z = ((float)(tmp_rot[7] / (2.0D * y)));
      return;
    }
    
    y = 0.0F;
    z = 1.0F;
  }
  






  public final void get(Vector3f trans)
  {
    x = m03;
    y = m13;
    z = m23;
  }
  





  public final void getRotationScale(Matrix3f m1)
  {
    m00 = m00;m01 = m01;m02 = m02;
    m10 = m10;m11 = m11;m12 = m12;
    m20 = m20;m21 = m21;m22 = m22;
  }
  







  public final float getScale()
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    return (float)Matrix3d.max3(tmp_scale);
  }
  







  public final void setRotationScale(Matrix3f m1)
  {
    m00 = m00;m01 = m01;m02 = m02;
    m10 = m10;m11 = m11;m12 = m12;
    m20 = m20;m21 = m21;m22 = m22;
  }
  









  public final void setRow(int row, float x, float y, float z, float w)
  {
    switch (row) {
    case 0: 
      m00 = x;
      m01 = y;
      m02 = z;
      m03 = w;
      break;
    
    case 1: 
      m10 = x;
      m11 = y;
      m12 = z;
      m13 = w;
      break;
    
    case 2: 
      m20 = x;
      m21 = y;
      m22 = z;
      m23 = w;
      break;
    
    case 3: 
      m30 = x;
      m31 = y;
      m32 = z;
      m33 = w;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
    }
    
  }
  




  public final void setRow(int row, Vector4f v)
  {
    switch (row) {
    case 0: 
      m00 = x;
      m01 = y;
      m02 = z;
      m03 = w;
      break;
    
    case 1: 
      m10 = x;
      m11 = y;
      m12 = z;
      m13 = w;
      break;
    
    case 2: 
      m20 = x;
      m21 = y;
      m22 = z;
      m23 = w;
      break;
    
    case 3: 
      m30 = x;
      m31 = y;
      m32 = z;
      m33 = w;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
    }
    
  }
  





  public final void setRow(int row, float[] v)
  {
    switch (row) {
    case 0: 
      m00 = v[0];
      m01 = v[1];
      m02 = v[2];
      m03 = v[3];
      break;
    
    case 1: 
      m10 = v[0];
      m11 = v[1];
      m12 = v[2];
      m13 = v[3];
      break;
    
    case 2: 
      m20 = v[0];
      m21 = v[1];
      m22 = v[2];
      m23 = v[3];
      break;
    
    case 3: 
      m30 = v[0];
      m31 = v[1];
      m32 = v[2];
      m33 = v[3];
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
    }
    
  }
  







  public final void setColumn(int column, float x, float y, float z, float w)
  {
    switch (column) {
    case 0: 
      m00 = x;
      m10 = y;
      m20 = z;
      m30 = w;
      break;
    
    case 1: 
      m01 = x;
      m11 = y;
      m21 = z;
      m31 = w;
      break;
    
    case 2: 
      m02 = x;
      m12 = y;
      m22 = z;
      m32 = w;
      break;
    
    case 3: 
      m03 = x;
      m13 = y;
      m23 = z;
      m33 = w;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
    }
    
  }
  




  public final void setColumn(int column, Vector4f v)
  {
    switch (column) {
    case 0: 
      m00 = x;
      m10 = y;
      m20 = z;
      m30 = w;
      break;
    
    case 1: 
      m01 = x;
      m11 = y;
      m21 = z;
      m31 = w;
      break;
    
    case 2: 
      m02 = x;
      m12 = y;
      m22 = z;
      m32 = w;
      break;
    
    case 3: 
      m03 = x;
      m13 = y;
      m23 = z;
      m33 = w;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
    }
    
  }
  




  public final void setColumn(int column, float[] v)
  {
    switch (column) {
    case 0: 
      m00 = v[0];
      m10 = v[1];
      m20 = v[2];
      m30 = v[3];
      break;
    
    case 1: 
      m01 = v[0];
      m11 = v[1];
      m21 = v[2];
      m31 = v[3];
      break;
    
    case 2: 
      m02 = v[0];
      m12 = v[1];
      m22 = v[2];
      m32 = v[3];
      break;
    
    case 3: 
      m03 = v[0];
      m13 = v[1];
      m23 = v[2];
      m33 = v[3];
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
    }
    
  }
  



  public final void add(float scalar)
  {
    m00 += scalar;
    m01 += scalar;
    m02 += scalar;
    m03 += scalar;
    m10 += scalar;
    m11 += scalar;
    m12 += scalar;
    m13 += scalar;
    m20 += scalar;
    m21 += scalar;
    m22 += scalar;
    m23 += scalar;
    m30 += scalar;
    m31 += scalar;
    m32 += scalar;
    m33 += scalar;
  }
  






  public final void add(float scalar, Matrix4f m1)
  {
    m00 += scalar;
    m01 += scalar;
    m02 += scalar;
    m03 += scalar;
    m10 += scalar;
    m11 += scalar;
    m12 += scalar;
    m13 += scalar;
    m20 += scalar;
    m21 += scalar;
    m22 += scalar;
    m23 += scalar;
    m30 += scalar;
    m31 += scalar;
    m32 += scalar;
    m33 += scalar;
  }
  





  public final void add(Matrix4f m1, Matrix4f m2)
  {
    m00 += m00;
    m01 += m01;
    m02 += m02;
    m03 += m03;
    
    m10 += m10;
    m11 += m11;
    m12 += m12;
    m13 += m13;
    
    m20 += m20;
    m21 += m21;
    m22 += m22;
    m23 += m23;
    
    m30 += m30;
    m31 += m31;
    m32 += m32;
    m33 += m33;
  }
  





  public final void add(Matrix4f m1)
  {
    m00 += m00;
    m01 += m01;
    m02 += m02;
    m03 += m03;
    
    m10 += m10;
    m11 += m11;
    m12 += m12;
    m13 += m13;
    
    m20 += m20;
    m21 += m21;
    m22 += m22;
    m23 += m23;
    
    m30 += m30;
    m31 += m31;
    m32 += m32;
    m33 += m33;
  }
  







  public final void sub(Matrix4f m1, Matrix4f m2)
  {
    m00 -= m00;
    m01 -= m01;
    m02 -= m02;
    m03 -= m03;
    
    m10 -= m10;
    m11 -= m11;
    m12 -= m12;
    m13 -= m13;
    
    m20 -= m20;
    m21 -= m21;
    m22 -= m22;
    m23 -= m23;
    
    m30 -= m30;
    m31 -= m31;
    m32 -= m32;
    m33 -= m33;
  }
  





  public final void sub(Matrix4f m1)
  {
    m00 -= m00;
    m01 -= m01;
    m02 -= m02;
    m03 -= m03;
    
    m10 -= m10;
    m11 -= m11;
    m12 -= m12;
    m13 -= m13;
    
    m20 -= m20;
    m21 -= m21;
    m22 -= m22;
    m23 -= m23;
    
    m30 -= m30;
    m31 -= m31;
    m32 -= m32;
    m33 -= m33;
  }
  





  public final void transpose()
  {
    float temp = m10;
    m10 = m01;
    m01 = temp;
    
    temp = m20;
    m20 = m02;
    m02 = temp;
    
    temp = m30;
    m30 = m03;
    m03 = temp;
    
    temp = m21;
    m21 = m12;
    m12 = temp;
    
    temp = m31;
    m31 = m13;
    m13 = temp;
    
    temp = m32;
    m32 = m23;
    m23 = temp;
  }
  




  public final void transpose(Matrix4f m1)
  {
    if (this != m1) {
      m00 = m00;
      m01 = m10;
      m02 = m20;
      m03 = m30;
      
      m10 = m01;
      m11 = m11;
      m12 = m21;
      m13 = m31;
      
      m20 = m02;
      m21 = m12;
      m22 = m22;
      m23 = m32;
      
      m30 = m03;
      m31 = m13;
      m32 = m23;
      m33 = m33;
    } else {
      transpose();
    }
  }
  




  public final void set(Quat4f q1)
  {
    m00 = (1.0F - 2.0F * y * y - 2.0F * z * z);
    m10 = (2.0F * (x * y + w * z));
    m20 = (2.0F * (x * z - w * y));
    
    m01 = (2.0F * (x * y - w * z));
    m11 = (1.0F - 2.0F * x * x - 2.0F * z * z);
    m21 = (2.0F * (y * z + w * x));
    
    m02 = (2.0F * (x * z + w * y));
    m12 = (2.0F * (y * z - w * x));
    m22 = (1.0F - 2.0F * x * x - 2.0F * y * y);
    
    m03 = 0.0F;
    m13 = 0.0F;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  





  public final void set(AxisAngle4f a1)
  {
    float mag = (float)Math.sqrt(x * x + y * y + z * z);
    if (mag < 1.0E-8D) {
      m00 = 1.0F;
      m01 = 0.0F;
      m02 = 0.0F;
      
      m10 = 0.0F;
      m11 = 1.0F;
      m12 = 0.0F;
      
      m20 = 0.0F;
      m21 = 0.0F;
      m22 = 1.0F;
    } else {
      mag = 1.0F / mag;
      float ax = x * mag;
      float ay = y * mag;
      float az = z * mag;
      
      float sinTheta = (float)Math.sin(angle);
      float cosTheta = (float)Math.cos(angle);
      float t = 1.0F - cosTheta;
      
      float xz = ax * az;
      float xy = ax * ay;
      float yz = ay * az;
      
      m00 = (t * ax * ax + cosTheta);
      m01 = (t * xy - sinTheta * az);
      m02 = (t * xz + sinTheta * ay);
      
      m10 = (t * xy + sinTheta * az);
      m11 = (t * ay * ay + cosTheta);
      m12 = (t * yz - sinTheta * ax);
      
      m20 = (t * xz - sinTheta * ay);
      m21 = (t * yz + sinTheta * ax);
      m22 = (t * az * az + cosTheta);
    }
    m03 = 0.0F;
    m13 = 0.0F;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  





  public final void set(Quat4d q1)
  {
    m00 = ((float)(1.0D - 2.0D * y * y - 2.0D * z * z));
    m10 = ((float)(2.0D * (x * y + w * z)));
    m20 = ((float)(2.0D * (x * z - w * y)));
    
    m01 = ((float)(2.0D * (x * y - w * z)));
    m11 = ((float)(1.0D - 2.0D * x * x - 2.0D * z * z));
    m21 = ((float)(2.0D * (y * z + w * x)));
    
    m02 = ((float)(2.0D * (x * z + w * y)));
    m12 = ((float)(2.0D * (y * z - w * x)));
    m22 = ((float)(1.0D - 2.0D * x * x - 2.0D * y * y));
    
    m03 = 0.0F;
    m13 = 0.0F;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  





  public final void set(AxisAngle4d a1)
  {
    double mag = Math.sqrt(x * x + y * y + z * z);
    
    if (mag < 1.0E-8D) {
      m00 = 1.0F;
      m01 = 0.0F;
      m02 = 0.0F;
      
      m10 = 0.0F;
      m11 = 1.0F;
      m12 = 0.0F;
      
      m20 = 0.0F;
      m21 = 0.0F;
      m22 = 1.0F;
    } else {
      mag = 1.0D / mag;
      double ax = x * mag;
      double ay = y * mag;
      double az = z * mag;
      
      float sinTheta = (float)Math.sin(angle);
      float cosTheta = (float)Math.cos(angle);
      float t = 1.0F - cosTheta;
      
      float xz = (float)(ax * az);
      float xy = (float)(ax * ay);
      float yz = (float)(ay * az);
      
      m00 = (t * (float)(ax * ax) + cosTheta);
      m01 = (t * xy - sinTheta * (float)az);
      m02 = (t * xz + sinTheta * (float)ay);
      
      m10 = (t * xy + sinTheta * (float)az);
      m11 = (t * (float)(ay * ay) + cosTheta);
      m12 = (t * yz - sinTheta * (float)ax);
      
      m20 = (t * xz - sinTheta * (float)ay);
      m21 = (t * yz + sinTheta * (float)ax);
      m22 = (t * (float)(az * az) + cosTheta);
    }
    m03 = 0.0F;
    m13 = 0.0F;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  







  public final void set(Quat4d q1, Vector3d t1, double s)
  {
    m00 = ((float)(s * (1.0D - 2.0D * y * y - 2.0D * z * z)));
    m10 = ((float)(s * (2.0D * (x * y + w * z))));
    m20 = ((float)(s * (2.0D * (x * z - w * y))));
    
    m01 = ((float)(s * (2.0D * (x * y - w * z))));
    m11 = ((float)(s * (1.0D - 2.0D * x * x - 2.0D * z * z)));
    m21 = ((float)(s * (2.0D * (y * z + w * x))));
    
    m02 = ((float)(s * (2.0D * (x * z + w * y))));
    m12 = ((float)(s * (2.0D * (y * z - w * x))));
    m22 = ((float)(s * (1.0D - 2.0D * x * x - 2.0D * y * y)));
    
    m03 = ((float)x);
    m13 = ((float)y);
    m23 = ((float)z);
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  







  public final void set(Quat4f q1, Vector3f t1, float s)
  {
    m00 = (s * (1.0F - 2.0F * y * y - 2.0F * z * z));
    m10 = (s * (2.0F * (x * y + w * z)));
    m20 = (s * (2.0F * (x * z - w * y)));
    
    m01 = (s * (2.0F * (x * y - w * z)));
    m11 = (s * (1.0F - 2.0F * x * x - 2.0F * z * z));
    m21 = (s * (2.0F * (y * z + w * x)));
    
    m02 = (s * (2.0F * (x * z + w * y)));
    m12 = (s * (2.0F * (y * z - w * x)));
    m22 = (s * (1.0F - 2.0F * x * x - 2.0F * y * y));
    
    m03 = x;
    m13 = y;
    m23 = z;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  





  public final void set(Matrix4d m1)
  {
    m00 = ((float)m00);
    m01 = ((float)m01);
    m02 = ((float)m02);
    m03 = ((float)m03);
    
    m10 = ((float)m10);
    m11 = ((float)m11);
    m12 = ((float)m12);
    m13 = ((float)m13);
    
    m20 = ((float)m20);
    m21 = ((float)m21);
    m22 = ((float)m22);
    m23 = ((float)m23);
    
    m30 = ((float)m30);
    m31 = ((float)m31);
    m32 = ((float)m32);
    m33 = ((float)m33);
  }
  





  public final void set(Matrix4f m1)
  {
    m00 = m00;
    m01 = m01;
    m02 = m02;
    m03 = m03;
    
    m10 = m10;
    m11 = m11;
    m12 = m12;
    m13 = m13;
    
    m20 = m20;
    m21 = m21;
    m22 = m22;
    m23 = m23;
    
    m30 = m30;
    m31 = m31;
    m32 = m32;
    m33 = m33;
  }
  






  public final void invert(Matrix4f m1)
  {
    invertGeneral(m1);
  }
  



  public final void invert()
  {
    invertGeneral(this);
  }
  







  final void invertGeneral(Matrix4f m1)
  {
    double[] temp = new double[16];
    double[] result = new double[16];
    int[] row_perm = new int[4];
    





    temp[0] = m00;
    temp[1] = m01;
    temp[2] = m02;
    temp[3] = m03;
    
    temp[4] = m10;
    temp[5] = m11;
    temp[6] = m12;
    temp[7] = m13;
    
    temp[8] = m20;
    temp[9] = m21;
    temp[10] = m22;
    temp[11] = m23;
    
    temp[12] = m30;
    temp[13] = m31;
    temp[14] = m32;
    temp[15] = m33;
    

    if (!luDecomposition(temp, row_perm))
    {
      throw new SingularMatrixException(VecMathI18N.getString("Matrix4f12"));
    }
    

    for (int i = 0; i < 16; i++) result[i] = 0.0D;
    result[0] = 1.0D;result[5] = 1.0D;result[10] = 1.0D;result[15] = 1.0D;
    luBacksubstitution(temp, row_perm, result);
    
    m00 = ((float)result[0]);
    m01 = ((float)result[1]);
    m02 = ((float)result[2]);
    m03 = ((float)result[3]);
    
    m10 = ((float)result[4]);
    m11 = ((float)result[5]);
    m12 = ((float)result[6]);
    m13 = ((float)result[7]);
    
    m20 = ((float)result[8]);
    m21 = ((float)result[9]);
    m22 = ((float)result[10]);
    m23 = ((float)result[11]);
    
    m30 = ((float)result[12]);
    m31 = ((float)result[13]);
    m32 = ((float)result[14]);
    m33 = ((float)result[15]);
  }
  






















  static boolean luDecomposition(double[] matrix0, int[] row_perm)
  {
    double[] row_scale = new double[4];
    






    int ptr = 0;
    int rs = 0;
    

    int i = 4;
    while (i-- != 0) {
      double big = 0.0D;
      

      int j = 4;
      while (j-- != 0) {
        double temp = matrix0[(ptr++)];
        temp = Math.abs(temp);
        if (temp > big) {
          big = temp;
        }
      }
      

      if (big == 0.0D) {
        return false;
      }
      row_scale[(rs++)] = (1.0D / big);
    }
    





    int mtx = 0;
    

    for (int j = 0; j < 4; j++)
    {




      for (int i = 0; i < j; i++) {
        int target = mtx + 4 * i + j;
        double sum = matrix0[target];
        int k = i;
        int p1 = mtx + 4 * i;
        int p2 = mtx + j;
        while (k-- != 0) {
          sum -= matrix0[p1] * matrix0[p2];
          p1++;
          p2 += 4;
        }
        matrix0[target] = sum;
      }
      


      double big = 0.0D;
      int imax = -1;
      for (i = j; i < 4; i++) {
        int target = mtx + 4 * i + j;
        double sum = matrix0[target];
        int k = j;
        int p1 = mtx + 4 * i;
        int p2 = mtx + j;
        while (k-- != 0) {
          sum -= matrix0[p1] * matrix0[p2];
          p1++;
          p2 += 4;
        }
        matrix0[target] = sum;
        
        double temp;
        if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
          big = temp;
          imax = i;
        }
      }
      
      if (imax < 0) {
        throw new RuntimeException(VecMathI18N.getString("Matrix4f13"));
      }
      

      if (j != imax)
      {
        int k = 4;
        int p1 = mtx + 4 * imax;
        int p2 = mtx + 4 * j;
        while (k-- != 0) {
          double temp = matrix0[p1];
          matrix0[(p1++)] = matrix0[p2];
          matrix0[(p2++)] = temp;
        }
        

        row_scale[imax] = row_scale[j];
      }
      

      row_perm[j] = imax;
      

      if (matrix0[(mtx + 4 * j + j)] == 0.0D) {
        return false;
      }
      

      if (j != 3) {
        double temp = 1.0D / matrix0[(mtx + 4 * j + j)];
        int target = mtx + 4 * (j + 1) + j;
        i = 3 - j;
        while (i-- != 0) {
          matrix0[target] *= temp;
          target += 4;
        }
      }
    }
    

    return true;
  }
  

























  static void luBacksubstitution(double[] matrix1, int[] row_perm, double[] matrix2)
  {
    int rp = 0;
    

    for (int k = 0; k < 4; k++)
    {
      int cv = k;
      int ii = -1;
      

      for (int i = 0; i < 4; i++)
      {

        int ip = row_perm[(rp + i)];
        double sum = matrix2[(cv + 4 * ip)];
        matrix2[(cv + 4 * ip)] = matrix2[(cv + 4 * i)];
        if (ii >= 0)
        {
          int rv = i * 4;
          for (int j = ii; j <= i - 1; j++) {
            sum -= matrix1[(rv + j)] * matrix2[(cv + 4 * j)];
          }
        }
        if (sum != 0.0D) {
          ii = i;
        }
        matrix2[(cv + 4 * i)] = sum;
      }
      


      int rv = 12;
      matrix2[(cv + 12)] /= matrix1[(rv + 3)];
      
      rv -= 4;
      matrix2[(cv + 8)] = ((matrix2[(cv + 8)] - matrix1[(rv + 3)] * matrix2[(cv + 12)]) / matrix1[(rv + 2)]);
      

      rv -= 4;
      matrix2[(cv + 4)] = ((matrix2[(cv + 4)] - matrix1[(rv + 2)] * matrix2[(cv + 8)] - matrix1[(rv + 3)] * matrix2[(cv + 12)]) / matrix1[(rv + 1)]);
      


      rv -= 4;
      matrix2[(cv + 0)] = ((matrix2[(cv + 0)] - matrix1[(rv + 1)] * matrix2[(cv + 4)] - matrix1[(rv + 2)] * matrix2[(cv + 8)] - matrix1[(rv + 3)] * matrix2[(cv + 12)]) / matrix1[(rv + 0)]);
    }
  }
  











  public final float determinant()
  {
    float det = m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33);
    
    det -= m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33);
    
    det += m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33);
    
    det -= m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);
    

    return det;
  }
  







  public final void set(Matrix3f m1)
  {
    m00 = m00;m01 = m01;m02 = m02;m03 = 0.0F;
    m10 = m10;m11 = m11;m12 = m12;m13 = 0.0F;
    m20 = m20;m21 = m21;m22 = m22;m23 = 0.0F;
    m30 = 0.0F;m31 = 0.0F;m32 = 0.0F;m33 = 1.0F;
  }
  







  public final void set(Matrix3d m1)
  {
    m00 = ((float)m00);m01 = ((float)m01);m02 = ((float)m02);m03 = 0.0F;
    m10 = ((float)m10);m11 = ((float)m11);m12 = ((float)m12);m13 = 0.0F;
    m20 = ((float)m20);m21 = ((float)m21);m22 = ((float)m22);m23 = 0.0F;
    m30 = 0.0F;m31 = 0.0F;m32 = 0.0F;m33 = 1.0F;
  }
  





  public final void set(float scale)
  {
    m00 = scale;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = 0.0F;
    
    m10 = 0.0F;
    m11 = scale;
    m12 = 0.0F;
    m13 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = scale;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  






  public final void set(float[] m)
  {
    m00 = m[0];
    m01 = m[1];
    m02 = m[2];
    m03 = m[3];
    m10 = m[4];
    m11 = m[5];
    m12 = m[6];
    m13 = m[7];
    m20 = m[8];
    m21 = m[9];
    m22 = m[10];
    m23 = m[11];
    m30 = m[12];
    m31 = m[13];
    m32 = m[14];
    m33 = m[15];
  }
  





  public final void set(Vector3f v1)
  {
    m00 = 1.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = x;
    
    m10 = 0.0F;
    m11 = 1.0F;
    m12 = 0.0F;
    m13 = y;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 1.0F;
    m23 = z;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  







  public final void set(float scale, Vector3f t1)
  {
    m00 = scale;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = x;
    
    m10 = 0.0F;
    m11 = scale;
    m12 = 0.0F;
    m13 = y;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = scale;
    m23 = z;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  







  public final void set(Vector3f t1, float scale)
  {
    m00 = scale;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = (scale * x);
    
    m10 = 0.0F;
    m11 = scale;
    m12 = 0.0F;
    m13 = (scale * y);
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = scale;
    m23 = (scale * z);
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  








  public final void set(Matrix3f m1, Vector3f t1, float scale)
  {
    m00 = (m00 * scale);
    m01 = (m01 * scale);
    m02 = (m02 * scale);
    m03 = x;
    
    m10 = (m10 * scale);
    m11 = (m11 * scale);
    m12 = (m12 * scale);
    m13 = y;
    
    m20 = (m20 * scale);
    m21 = (m21 * scale);
    m22 = (m22 * scale);
    m23 = z;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  








  public final void set(Matrix3d m1, Vector3d t1, double scale)
  {
    m00 = ((float)(m00 * scale));
    m01 = ((float)(m01 * scale));
    m02 = ((float)(m02 * scale));
    m03 = ((float)x);
    
    m10 = ((float)(m10 * scale));
    m11 = ((float)(m11 * scale));
    m12 = ((float)(m12 * scale));
    m13 = ((float)y);
    
    m20 = ((float)(m20 * scale));
    m21 = ((float)(m21 * scale));
    m22 = ((float)(m22 * scale));
    m23 = ((float)z);
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  






  public final void setTranslation(Vector3f trans)
  {
    m03 = x;
    m13 = y;
    m23 = z;
  }
  








  public final void rotX(float angle)
  {
    float sinAngle = (float)Math.sin(angle);
    float cosAngle = (float)Math.cos(angle);
    
    m00 = 1.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = 0.0F;
    
    m10 = 0.0F;
    m11 = cosAngle;
    m12 = (-sinAngle);
    m13 = 0.0F;
    
    m20 = 0.0F;
    m21 = sinAngle;
    m22 = cosAngle;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  







  public final void rotY(float angle)
  {
    float sinAngle = (float)Math.sin(angle);
    float cosAngle = (float)Math.cos(angle);
    
    m00 = cosAngle;
    m01 = 0.0F;
    m02 = sinAngle;
    m03 = 0.0F;
    
    m10 = 0.0F;
    m11 = 1.0F;
    m12 = 0.0F;
    m13 = 0.0F;
    
    m20 = (-sinAngle);
    m21 = 0.0F;
    m22 = cosAngle;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  







  public final void rotZ(float angle)
  {
    float sinAngle = (float)Math.sin(angle);
    float cosAngle = (float)Math.cos(angle);
    
    m00 = cosAngle;
    m01 = (-sinAngle);
    m02 = 0.0F;
    m03 = 0.0F;
    
    m10 = sinAngle;
    m11 = cosAngle;
    m12 = 0.0F;
    m13 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 1.0F;
    m23 = 0.0F;
    
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 1.0F;
  }
  




  public final void mul(float scalar)
  {
    m00 *= scalar;
    m01 *= scalar;
    m02 *= scalar;
    m03 *= scalar;
    m10 *= scalar;
    m11 *= scalar;
    m12 *= scalar;
    m13 *= scalar;
    m20 *= scalar;
    m21 *= scalar;
    m22 *= scalar;
    m23 *= scalar;
    m30 *= scalar;
    m31 *= scalar;
    m32 *= scalar;
    m33 *= scalar;
  }
  






  public final void mul(float scalar, Matrix4f m1)
  {
    m00 *= scalar;
    m01 *= scalar;
    m02 *= scalar;
    m03 *= scalar;
    m10 *= scalar;
    m11 *= scalar;
    m12 *= scalar;
    m13 *= scalar;
    m20 *= scalar;
    m21 *= scalar;
    m22 *= scalar;
    m23 *= scalar;
    m30 *= scalar;
    m31 *= scalar;
    m32 *= scalar;
    m33 *= scalar;
  }
  










  public final void mul(Matrix4f m1)
  {
    float m00 = this.m00 * m00 + this.m01 * m10 + this.m02 * m20 + this.m03 * m30;
    
    float m01 = this.m00 * m01 + this.m01 * m11 + this.m02 * m21 + this.m03 * m31;
    
    float m02 = this.m00 * m02 + this.m01 * m12 + this.m02 * m22 + this.m03 * m32;
    
    float m03 = this.m00 * m03 + this.m01 * m13 + this.m02 * m23 + this.m03 * m33;
    

    float m10 = this.m10 * m00 + this.m11 * m10 + this.m12 * m20 + this.m13 * m30;
    
    float m11 = this.m10 * m01 + this.m11 * m11 + this.m12 * m21 + this.m13 * m31;
    
    float m12 = this.m10 * m02 + this.m11 * m12 + this.m12 * m22 + this.m13 * m32;
    
    float m13 = this.m10 * m03 + this.m11 * m13 + this.m12 * m23 + this.m13 * m33;
    

    float m20 = this.m20 * m00 + this.m21 * m10 + this.m22 * m20 + this.m23 * m30;
    
    float m21 = this.m20 * m01 + this.m21 * m11 + this.m22 * m21 + this.m23 * m31;
    
    float m22 = this.m20 * m02 + this.m21 * m12 + this.m22 * m22 + this.m23 * m32;
    
    float m23 = this.m20 * m03 + this.m21 * m13 + this.m22 * m23 + this.m23 * m33;
    

    float m30 = this.m30 * m00 + this.m31 * m10 + this.m32 * m20 + this.m33 * m30;
    
    float m31 = this.m30 * m01 + this.m31 * m11 + this.m32 * m21 + this.m33 * m31;
    
    float m32 = this.m30 * m02 + this.m31 * m12 + this.m32 * m22 + this.m33 * m32;
    
    float m33 = this.m30 * m03 + this.m31 * m13 + this.m32 * m23 + this.m33 * m33;
    

    this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
    this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
    this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
    this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
  }
  






  public final void mul(Matrix4f m1, Matrix4f m2)
  {
    if ((this != m1) && (this != m2))
    {
      this.m00 = (m00 * m00 + m01 * m10 + m02 * m20 + m03 * m30);
      
      this.m01 = (m00 * m01 + m01 * m11 + m02 * m21 + m03 * m31);
      
      this.m02 = (m00 * m02 + m01 * m12 + m02 * m22 + m03 * m32);
      
      this.m03 = (m00 * m03 + m01 * m13 + m02 * m23 + m03 * m33);
      

      this.m10 = (m10 * m00 + m11 * m10 + m12 * m20 + m13 * m30);
      
      this.m11 = (m10 * m01 + m11 * m11 + m12 * m21 + m13 * m31);
      
      this.m12 = (m10 * m02 + m11 * m12 + m12 * m22 + m13 * m32);
      
      this.m13 = (m10 * m03 + m11 * m13 + m12 * m23 + m13 * m33);
      

      this.m20 = (m20 * m00 + m21 * m10 + m22 * m20 + m23 * m30);
      
      this.m21 = (m20 * m01 + m21 * m11 + m22 * m21 + m23 * m31);
      
      this.m22 = (m20 * m02 + m21 * m12 + m22 * m22 + m23 * m32);
      
      this.m23 = (m20 * m03 + m21 * m13 + m22 * m23 + m23 * m33);
      

      this.m30 = (m30 * m00 + m31 * m10 + m32 * m20 + m33 * m30);
      
      this.m31 = (m30 * m01 + m31 * m11 + m32 * m21 + m33 * m31);
      
      this.m32 = (m30 * m02 + m31 * m12 + m32 * m22 + m33 * m32);
      
      this.m33 = (m30 * m03 + m31 * m13 + m32 * m23 + m33 * m33);


    }
    else
    {

      float m00 = m00 * m00 + m01 * m10 + m02 * m20 + m03 * m30;
      float m01 = m00 * m01 + m01 * m11 + m02 * m21 + m03 * m31;
      float m02 = m00 * m02 + m01 * m12 + m02 * m22 + m03 * m32;
      float m03 = m00 * m03 + m01 * m13 + m02 * m23 + m03 * m33;
      
      float m10 = m10 * m00 + m11 * m10 + m12 * m20 + m13 * m30;
      float m11 = m10 * m01 + m11 * m11 + m12 * m21 + m13 * m31;
      float m12 = m10 * m02 + m11 * m12 + m12 * m22 + m13 * m32;
      float m13 = m10 * m03 + m11 * m13 + m12 * m23 + m13 * m33;
      
      float m20 = m20 * m00 + m21 * m10 + m22 * m20 + m23 * m30;
      float m21 = m20 * m01 + m21 * m11 + m22 * m21 + m23 * m31;
      float m22 = m20 * m02 + m21 * m12 + m22 * m22 + m23 * m32;
      float m23 = m20 * m03 + m21 * m13 + m22 * m23 + m23 * m33;
      
      float m30 = m30 * m00 + m31 * m10 + m32 * m20 + m33 * m30;
      float m31 = m30 * m01 + m31 * m11 + m32 * m21 + m33 * m31;
      float m32 = m30 * m02 + m31 * m12 + m32 * m22 + m33 * m32;
      float m33 = m30 * m03 + m31 * m13 + m32 * m23 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  






  public final void mulTransposeBoth(Matrix4f m1, Matrix4f m2)
  {
    if ((this != m1) && (this != m2)) {
      this.m00 = (m00 * m00 + m10 * m01 + m20 * m02 + m30 * m03);
      this.m01 = (m00 * m10 + m10 * m11 + m20 * m12 + m30 * m13);
      this.m02 = (m00 * m20 + m10 * m21 + m20 * m22 + m30 * m23);
      this.m03 = (m00 * m30 + m10 * m31 + m20 * m32 + m30 * m33);
      
      this.m10 = (m01 * m00 + m11 * m01 + m21 * m02 + m31 * m03);
      this.m11 = (m01 * m10 + m11 * m11 + m21 * m12 + m31 * m13);
      this.m12 = (m01 * m20 + m11 * m21 + m21 * m22 + m31 * m23);
      this.m13 = (m01 * m30 + m11 * m31 + m21 * m32 + m31 * m33);
      
      this.m20 = (m02 * m00 + m12 * m01 + m22 * m02 + m32 * m03);
      this.m21 = (m02 * m10 + m12 * m11 + m22 * m12 + m32 * m13);
      this.m22 = (m02 * m20 + m12 * m21 + m22 * m22 + m32 * m23);
      this.m23 = (m02 * m30 + m12 * m31 + m22 * m32 + m32 * m33);
      
      this.m30 = (m03 * m00 + m13 * m01 + m23 * m02 + m33 * m03);
      this.m31 = (m03 * m10 + m13 * m11 + m23 * m12 + m33 * m13);
      this.m32 = (m03 * m20 + m13 * m21 + m23 * m22 + m33 * m23);
      this.m33 = (m03 * m30 + m13 * m31 + m23 * m32 + m33 * m33);


    }
    else
    {

      float m00 = m00 * m00 + m10 * m01 + m20 * m02 + m30 * m03;
      float m01 = m00 * m10 + m10 * m11 + m20 * m12 + m30 * m13;
      float m02 = m00 * m20 + m10 * m21 + m20 * m22 + m30 * m23;
      float m03 = m00 * m30 + m10 * m31 + m20 * m32 + m30 * m33;
      
      float m10 = m01 * m00 + m11 * m01 + m21 * m02 + m31 * m03;
      float m11 = m01 * m10 + m11 * m11 + m21 * m12 + m31 * m13;
      float m12 = m01 * m20 + m11 * m21 + m21 * m22 + m31 * m23;
      float m13 = m01 * m30 + m11 * m31 + m21 * m32 + m31 * m33;
      
      float m20 = m02 * m00 + m12 * m01 + m22 * m02 + m32 * m03;
      float m21 = m02 * m10 + m12 * m11 + m22 * m12 + m32 * m13;
      float m22 = m02 * m20 + m12 * m21 + m22 * m22 + m32 * m23;
      float m23 = m02 * m30 + m12 * m31 + m22 * m32 + m32 * m33;
      
      float m30 = m03 * m00 + m13 * m01 + m23 * m02 + m33 * m03;
      float m31 = m03 * m10 + m13 * m11 + m23 * m12 + m33 * m13;
      float m32 = m03 * m20 + m13 * m21 + m23 * m22 + m33 * m23;
      float m33 = m03 * m30 + m13 * m31 + m23 * m32 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  







  public final void mulTransposeRight(Matrix4f m1, Matrix4f m2)
  {
    if ((this != m1) && (this != m2)) {
      this.m00 = (m00 * m00 + m01 * m01 + m02 * m02 + m03 * m03);
      this.m01 = (m00 * m10 + m01 * m11 + m02 * m12 + m03 * m13);
      this.m02 = (m00 * m20 + m01 * m21 + m02 * m22 + m03 * m23);
      this.m03 = (m00 * m30 + m01 * m31 + m02 * m32 + m03 * m33);
      
      this.m10 = (m10 * m00 + m11 * m01 + m12 * m02 + m13 * m03);
      this.m11 = (m10 * m10 + m11 * m11 + m12 * m12 + m13 * m13);
      this.m12 = (m10 * m20 + m11 * m21 + m12 * m22 + m13 * m23);
      this.m13 = (m10 * m30 + m11 * m31 + m12 * m32 + m13 * m33);
      
      this.m20 = (m20 * m00 + m21 * m01 + m22 * m02 + m23 * m03);
      this.m21 = (m20 * m10 + m21 * m11 + m22 * m12 + m23 * m13);
      this.m22 = (m20 * m20 + m21 * m21 + m22 * m22 + m23 * m23);
      this.m23 = (m20 * m30 + m21 * m31 + m22 * m32 + m23 * m33);
      
      this.m30 = (m30 * m00 + m31 * m01 + m32 * m02 + m33 * m03);
      this.m31 = (m30 * m10 + m31 * m11 + m32 * m12 + m33 * m13);
      this.m32 = (m30 * m20 + m31 * m21 + m32 * m22 + m33 * m23);
      this.m33 = (m30 * m30 + m31 * m31 + m32 * m32 + m33 * m33);


    }
    else
    {

      float m00 = m00 * m00 + m01 * m01 + m02 * m02 + m03 * m03;
      float m01 = m00 * m10 + m01 * m11 + m02 * m12 + m03 * m13;
      float m02 = m00 * m20 + m01 * m21 + m02 * m22 + m03 * m23;
      float m03 = m00 * m30 + m01 * m31 + m02 * m32 + m03 * m33;
      
      float m10 = m10 * m00 + m11 * m01 + m12 * m02 + m13 * m03;
      float m11 = m10 * m10 + m11 * m11 + m12 * m12 + m13 * m13;
      float m12 = m10 * m20 + m11 * m21 + m12 * m22 + m13 * m23;
      float m13 = m10 * m30 + m11 * m31 + m12 * m32 + m13 * m33;
      
      float m20 = m20 * m00 + m21 * m01 + m22 * m02 + m23 * m03;
      float m21 = m20 * m10 + m21 * m11 + m22 * m12 + m23 * m13;
      float m22 = m20 * m20 + m21 * m21 + m22 * m22 + m23 * m23;
      float m23 = m20 * m30 + m21 * m31 + m22 * m32 + m23 * m33;
      
      float m30 = m30 * m00 + m31 * m01 + m32 * m02 + m33 * m03;
      float m31 = m30 * m10 + m31 * m11 + m32 * m12 + m33 * m13;
      float m32 = m30 * m20 + m31 * m21 + m32 * m22 + m33 * m23;
      float m33 = m30 * m30 + m31 * m31 + m32 * m32 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  








  public final void mulTransposeLeft(Matrix4f m1, Matrix4f m2)
  {
    if ((this != m1) && (this != m2)) {
      this.m00 = (m00 * m00 + m10 * m10 + m20 * m20 + m30 * m30);
      this.m01 = (m00 * m01 + m10 * m11 + m20 * m21 + m30 * m31);
      this.m02 = (m00 * m02 + m10 * m12 + m20 * m22 + m30 * m32);
      this.m03 = (m00 * m03 + m10 * m13 + m20 * m23 + m30 * m33);
      
      this.m10 = (m01 * m00 + m11 * m10 + m21 * m20 + m31 * m30);
      this.m11 = (m01 * m01 + m11 * m11 + m21 * m21 + m31 * m31);
      this.m12 = (m01 * m02 + m11 * m12 + m21 * m22 + m31 * m32);
      this.m13 = (m01 * m03 + m11 * m13 + m21 * m23 + m31 * m33);
      
      this.m20 = (m02 * m00 + m12 * m10 + m22 * m20 + m32 * m30);
      this.m21 = (m02 * m01 + m12 * m11 + m22 * m21 + m32 * m31);
      this.m22 = (m02 * m02 + m12 * m12 + m22 * m22 + m32 * m32);
      this.m23 = (m02 * m03 + m12 * m13 + m22 * m23 + m32 * m33);
      
      this.m30 = (m03 * m00 + m13 * m10 + m23 * m20 + m33 * m30);
      this.m31 = (m03 * m01 + m13 * m11 + m23 * m21 + m33 * m31);
      this.m32 = (m03 * m02 + m13 * m12 + m23 * m22 + m33 * m32);
      this.m33 = (m03 * m03 + m13 * m13 + m23 * m23 + m33 * m33);



    }
    else
    {


      float m00 = m00 * m00 + m10 * m10 + m20 * m20 + m30 * m30;
      float m01 = m00 * m01 + m10 * m11 + m20 * m21 + m30 * m31;
      float m02 = m00 * m02 + m10 * m12 + m20 * m22 + m30 * m32;
      float m03 = m00 * m03 + m10 * m13 + m20 * m23 + m30 * m33;
      
      float m10 = m01 * m00 + m11 * m10 + m21 * m20 + m31 * m30;
      float m11 = m01 * m01 + m11 * m11 + m21 * m21 + m31 * m31;
      float m12 = m01 * m02 + m11 * m12 + m21 * m22 + m31 * m32;
      float m13 = m01 * m03 + m11 * m13 + m21 * m23 + m31 * m33;
      
      float m20 = m02 * m00 + m12 * m10 + m22 * m20 + m32 * m30;
      float m21 = m02 * m01 + m12 * m11 + m22 * m21 + m32 * m31;
      float m22 = m02 * m02 + m12 * m12 + m22 * m22 + m32 * m32;
      float m23 = m02 * m03 + m12 * m13 + m22 * m23 + m32 * m33;
      
      float m30 = m03 * m00 + m13 * m10 + m23 * m20 + m33 * m30;
      float m31 = m03 * m01 + m13 * m11 + m23 * m21 + m33 * m31;
      float m32 = m03 * m02 + m13 * m12 + m23 * m22 + m33 * m32;
      float m33 = m03 * m03 + m13 * m13 + m23 * m23 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  







  public boolean equals(Matrix4f m1)
  {
    try
    {
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m03 == m03) && (m10 == m10) && (m11 == m11) && (m12 == m12) && (m13 == m13) && (m20 == m20) && (m21 == m21) && (m22 == m22) && (m23 == m23) && (m30 == m30) && (m31 == m31) && (m32 == m32) && (m33 == m33);
    }
    catch (NullPointerException e2) {}
    



    return false;
  }
  







  public boolean equals(Object t1)
  {
    try
    {
      Matrix4f m2 = (Matrix4f)t1;
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m03 == m03) && (m10 == m10) && (m11 == m11) && (m12 == m12) && (m13 == m13) && (m20 == m20) && (m21 == m21) && (m22 == m22) && (m23 == m23) && (m30 == m30) && (m31 == m31) && (m32 == m32) && (m33 == m33);


    }
    catch (ClassCastException e1)
    {

      return false; } catch (NullPointerException e2) {}
    return false;
  }
  










  public boolean epsilonEquals(Matrix4f m1, float epsilon)
  {
    boolean status = true;
    
    if (Math.abs(m00 - m00) > epsilon) status = false;
    if (Math.abs(m01 - m01) > epsilon) status = false;
    if (Math.abs(m02 - m02) > epsilon) status = false;
    if (Math.abs(m03 - m03) > epsilon) { status = false;
    }
    if (Math.abs(m10 - m10) > epsilon) status = false;
    if (Math.abs(m11 - m11) > epsilon) status = false;
    if (Math.abs(m12 - m12) > epsilon) status = false;
    if (Math.abs(m13 - m13) > epsilon) { status = false;
    }
    if (Math.abs(m20 - m20) > epsilon) status = false;
    if (Math.abs(m21 - m21) > epsilon) status = false;
    if (Math.abs(m22 - m22) > epsilon) status = false;
    if (Math.abs(m23 - m23) > epsilon) { status = false;
    }
    if (Math.abs(m30 - m30) > epsilon) status = false;
    if (Math.abs(m31 - m31) > epsilon) status = false;
    if (Math.abs(m32 - m32) > epsilon) status = false;
    if (Math.abs(m33 - m33) > epsilon) { status = false;
    }
    return status;
  }
  









  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.floatToIntBits(m00);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m01);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m02);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m03);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m10);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m11);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m12);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m13);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m20);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m21);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m22);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m23);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m30);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m31);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m32);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m33);
    return (int)(bits ^ bits >> 32);
  }
  








  public final void transform(Tuple4f vec, Tuple4f vecOut)
  {
    float x = m00 * x + m01 * y + m02 * z + m03 * w;
    
    float y = m10 * x + m11 * y + m12 * z + m13 * w;
    
    float z = m20 * x + m21 * y + m22 * z + m23 * w;
    
    w = (m30 * x + m31 * y + m32 * z + m33 * w);
    
    x = x;
    y = y;
    z = z;
  }
  








  public final void transform(Tuple4f vec)
  {
    float x = m00 * x + m01 * y + m02 * z + m03 * w;
    
    float y = m10 * x + m11 * y + m12 * z + m13 * w;
    
    float z = m20 * x + m21 * y + m22 * z + m23 * w;
    
    w = (m30 * x + m31 * y + m32 * z + m33 * w);
    
    x = x;
    y = y;
    z = z;
  }
  








  public final void transform(Point3f point, Point3f pointOut)
  {
    float x = m00 * x + m01 * y + m02 * z + m03;
    float y = m10 * x + m11 * y + m12 * z + m13;
    z = (m20 * x + m21 * y + m22 * z + m23);
    x = x;
    y = y;
  }
  








  public final void transform(Point3f point)
  {
    float x = m00 * x + m01 * y + m02 * z + m03;
    float y = m10 * x + m11 * y + m12 * z + m13;
    z = (m20 * x + m21 * y + m22 * z + m23);
    x = x;
    y = y;
  }
  








  public final void transform(Vector3f normal, Vector3f normalOut)
  {
    float x = m00 * x + m01 * y + m02 * z;
    float y = m10 * x + m11 * y + m12 * z;
    z = (m20 * x + m21 * y + m22 * z);
    x = x;
    y = y;
  }
  








  public final void transform(Vector3f normal)
  {
    float x = m00 * x + m01 * y + m02 * z;
    float y = m10 * x + m11 * y + m12 * z;
    z = (m20 * x + m21 * y + m22 * z);
    x = x;
    y = y;
  }
  











  public final void setRotation(Matrix3d m1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((float)(m00 * tmp_scale[0]));
    m01 = ((float)(m01 * tmp_scale[1]));
    m02 = ((float)(m02 * tmp_scale[2]));
    
    m10 = ((float)(m10 * tmp_scale[0]));
    m11 = ((float)(m11 * tmp_scale[1]));
    m12 = ((float)(m12 * tmp_scale[2]));
    
    m20 = ((float)(m20 * tmp_scale[0]));
    m21 = ((float)(m21 * tmp_scale[1]));
    m22 = ((float)(m22 * tmp_scale[2]));
  }
  










  public final void setRotation(Matrix3f m1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((float)(m00 * tmp_scale[0]));
    m01 = ((float)(m01 * tmp_scale[1]));
    m02 = ((float)(m02 * tmp_scale[2]));
    
    m10 = ((float)(m10 * tmp_scale[0]));
    m11 = ((float)(m11 * tmp_scale[1]));
    m12 = ((float)(m12 * tmp_scale[2]));
    
    m20 = ((float)(m20 * tmp_scale[0]));
    m21 = ((float)(m21 * tmp_scale[1]));
    m22 = ((float)(m22 * tmp_scale[2]));
  }
  









  public final void setRotation(Quat4f q1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((float)((1.0F - 2.0F * y * y - 2.0F * z * z) * tmp_scale[0]));
    m10 = ((float)(2.0F * (x * y + w * z) * tmp_scale[0]));
    m20 = ((float)(2.0F * (x * z - w * y) * tmp_scale[0]));
    
    m01 = ((float)(2.0F * (x * y - w * z) * tmp_scale[1]));
    m11 = ((float)((1.0F - 2.0F * x * x - 2.0F * z * z) * tmp_scale[1]));
    m21 = ((float)(2.0F * (y * z + w * x) * tmp_scale[1]));
    
    m02 = ((float)(2.0F * (x * z + w * y) * tmp_scale[2]));
    m12 = ((float)(2.0F * (y * z - w * x) * tmp_scale[2]));
    m22 = ((float)((1.0F - 2.0F * x * x - 2.0F * y * y) * tmp_scale[2]));
  }
  











  public final void setRotation(Quat4d q1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((float)((1.0D - 2.0D * y * y - 2.0D * z * z) * tmp_scale[0]));
    m10 = ((float)(2.0D * (x * y + w * z) * tmp_scale[0]));
    m20 = ((float)(2.0D * (x * z - w * y) * tmp_scale[0]));
    
    m01 = ((float)(2.0D * (x * y - w * z) * tmp_scale[1]));
    m11 = ((float)((1.0D - 2.0D * x * x - 2.0D * z * z) * tmp_scale[1]));
    m21 = ((float)(2.0D * (y * z + w * x) * tmp_scale[1]));
    
    m02 = ((float)(2.0D * (x * z + w * y) * tmp_scale[2]));
    m12 = ((float)(2.0D * (y * z - w * x) * tmp_scale[2]));
    m22 = ((float)((1.0D - 2.0D * x * x - 2.0D * y * y) * tmp_scale[2]));
  }
  









  public final void setRotation(AxisAngle4f a1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    double mag = Math.sqrt(x * x + y * y + z * z);
    if (mag < 1.0E-8D) {
      m00 = 1.0F;
      m01 = 0.0F;
      m02 = 0.0F;
      
      m10 = 0.0F;
      m11 = 1.0F;
      m12 = 0.0F;
      
      m20 = 0.0F;
      m21 = 0.0F;
      m22 = 1.0F;
    } else {
      mag = 1.0D / mag;
      double ax = x * mag;
      double ay = y * mag;
      double az = z * mag;
      
      double sinTheta = Math.sin(angle);
      double cosTheta = Math.cos(angle);
      double t = 1.0D - cosTheta;
      
      double xz = x * z;
      double xy = x * y;
      double yz = y * z;
      
      m00 = ((float)((t * ax * ax + cosTheta) * tmp_scale[0]));
      m01 = ((float)((t * xy - sinTheta * az) * tmp_scale[1]));
      m02 = ((float)((t * xz + sinTheta * ay) * tmp_scale[2]));
      
      m10 = ((float)((t * xy + sinTheta * az) * tmp_scale[0]));
      m11 = ((float)((t * ay * ay + cosTheta) * tmp_scale[1]));
      m12 = ((float)((t * yz - sinTheta * ax) * tmp_scale[2]));
      
      m20 = ((float)((t * xz - sinTheta * ay) * tmp_scale[0]));
      m21 = ((float)((t * yz + sinTheta * ax) * tmp_scale[1]));
      m22 = ((float)((t * az * az + cosTheta) * tmp_scale[2]));
    }
  }
  





  public final void setZero()
  {
    m00 = 0.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    m03 = 0.0F;
    m10 = 0.0F;
    m11 = 0.0F;
    m12 = 0.0F;
    m13 = 0.0F;
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 0.0F;
    m23 = 0.0F;
    m30 = 0.0F;
    m31 = 0.0F;
    m32 = 0.0F;
    m33 = 0.0F;
  }
  



  public final void negate()
  {
    m00 = (-m00);
    m01 = (-m01);
    m02 = (-m02);
    m03 = (-m03);
    m10 = (-m10);
    m11 = (-m11);
    m12 = (-m12);
    m13 = (-m13);
    m20 = (-m20);
    m21 = (-m21);
    m22 = (-m22);
    m23 = (-m23);
    m30 = (-m30);
    m31 = (-m31);
    m32 = (-m32);
    m33 = (-m33);
  }
  





  public final void negate(Matrix4f m1)
  {
    m00 = (-m00);
    m01 = (-m01);
    m02 = (-m02);
    m03 = (-m03);
    m10 = (-m10);
    m11 = (-m11);
    m12 = (-m12);
    m13 = (-m13);
    m20 = (-m20);
    m21 = (-m21);
    m22 = (-m22);
    m23 = (-m23);
    m30 = (-m30);
    m31 = (-m31);
    m32 = (-m32);
    m33 = (-m33);
  }
  
  private final void getScaleRotate(double[] scales, double[] rots) {
    double[] tmp = new double[9];
    tmp[0] = m00;
    tmp[1] = m01;
    tmp[2] = m02;
    
    tmp[3] = m10;
    tmp[4] = m11;
    tmp[5] = m12;
    
    tmp[6] = m20;
    tmp[7] = m21;
    tmp[8] = m22;
    
    Matrix3d.compute_svd(tmp, scales, rots);
  }
  









  public Object clone()
  {
    Matrix4f m1 = null;
    try {
      m1 = (Matrix4f)super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
    
    return m1;
  }
  






  public final float getM00()
  {
    return m00;
  }
  






  public final void setM00(float m00)
  {
    this.m00 = m00;
  }
  






  public final float getM01()
  {
    return m01;
  }
  






  public final void setM01(float m01)
  {
    this.m01 = m01;
  }
  






  public final float getM02()
  {
    return m02;
  }
  






  public final void setM02(float m02)
  {
    this.m02 = m02;
  }
  






  public final float getM10()
  {
    return m10;
  }
  






  public final void setM10(float m10)
  {
    this.m10 = m10;
  }
  






  public final float getM11()
  {
    return m11;
  }
  






  public final void setM11(float m11)
  {
    this.m11 = m11;
  }
  






  public final float getM12()
  {
    return m12;
  }
  






  public final void setM12(float m12)
  {
    this.m12 = m12;
  }
  






  public final float getM20()
  {
    return m20;
  }
  






  public final void setM20(float m20)
  {
    this.m20 = m20;
  }
  






  public final float getM21()
  {
    return m21;
  }
  






  public final void setM21(float m21)
  {
    this.m21 = m21;
  }
  






  public final float getM22()
  {
    return m22;
  }
  






  public final void setM22(float m22)
  {
    this.m22 = m22;
  }
  






  public final float getM03()
  {
    return m03;
  }
  






  public final void setM03(float m03)
  {
    this.m03 = m03;
  }
  






  public final float getM13()
  {
    return m13;
  }
  






  public final void setM13(float m13)
  {
    this.m13 = m13;
  }
  






  public final float getM23()
  {
    return m23;
  }
  






  public final void setM23(float m23)
  {
    this.m23 = m23;
  }
  






  public final float getM30()
  {
    return m30;
  }
  







  public final void setM30(float m30)
  {
    this.m30 = m30;
  }
  






  public final float getM31()
  {
    return m31;
  }
  






  public final void setM31(float m31)
  {
    this.m31 = m31;
  }
  






  public final float getM32()
  {
    return m32;
  }
  







  public final void setM32(float m32)
  {
    this.m32 = m32;
  }
  






  public final float getM33()
  {
    return m33;
  }
  






  public final void setM33(float m33)
  {
    this.m33 = m33;
  }
}
