package javax.vecmath;

import java.io.Serializable;

































































































































public class Matrix4d
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 8223903484171633710L;
  public double m00;
  public double m01;
  public double m02;
  public double m03;
  public double m10;
  public double m11;
  public double m12;
  public double m13;
  public double m20;
  public double m21;
  public double m22;
  public double m23;
  public double m30;
  public double m31;
  public double m32;
  public double m33;
  private static final double EPS = 1.0E-10D;
  
  public Matrix4d(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33)
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
  






  public Matrix4d(double[] v)
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
  










  public Matrix4d(Quat4d q1, Vector3d t1, double s)
  {
    m00 = (s * (1.0D - 2.0D * y * y - 2.0D * z * z));
    m10 = (s * (2.0D * (x * y + w * z)));
    m20 = (s * (2.0D * (x * z - w * y)));
    
    m01 = (s * (2.0D * (x * y - w * z)));
    m11 = (s * (1.0D - 2.0D * x * x - 2.0D * z * z));
    m21 = (s * (2.0D * (y * z + w * x)));
    
    m02 = (s * (2.0D * (x * z + w * y)));
    m12 = (s * (2.0D * (y * z - w * x)));
    m22 = (s * (1.0D - 2.0D * x * x - 2.0D * y * y));
    
    m03 = x;
    m13 = y;
    m23 = z;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  










  public Matrix4d(Quat4f q1, Vector3d t1, double s)
  {
    m00 = (s * (1.0D - 2.0D * y * y - 2.0D * z * z));
    m10 = (s * (2.0D * (x * y + w * z)));
    m20 = (s * (2.0D * (x * z - w * y)));
    
    m01 = (s * (2.0D * (x * y - w * z)));
    m11 = (s * (1.0D - 2.0D * x * x - 2.0D * z * z));
    m21 = (s * (2.0D * (y * z + w * x)));
    
    m02 = (s * (2.0D * (x * z + w * y)));
    m12 = (s * (2.0D * (y * z - w * x)));
    m22 = (s * (1.0D - 2.0D * x * x - 2.0D * y * y));
    
    m03 = x;
    m13 = y;
    m23 = z;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  






  public Matrix4d(Matrix4d m1)
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
  






  public Matrix4d(Matrix4f m1)
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
  










  public Matrix4d(Matrix3f m1, Vector3d t1, double s)
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
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  










  public Matrix4d(Matrix3d m1, Vector3d t1, double s)
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
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  




  public Matrix4d()
  {
    m00 = 0.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = 0.0D;
    
    m10 = 0.0D;
    m11 = 0.0D;
    m12 = 0.0D;
    m13 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 0.0D;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 0.0D;
  }
  




  public String toString()
  {
    return m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n" + m10 + ", " + m11 + ", " + m12 + ", " + m13 + "\n" + m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n" + m30 + ", " + m31 + ", " + m32 + ", " + m33 + "\n";
  }
  







  public final void setIdentity()
  {
    m00 = 1.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = 0.0D;
    
    m10 = 0.0D;
    m11 = 1.0D;
    m12 = 0.0D;
    m13 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  






  public final void setElement(int row, int column, double value)
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
      }
      
      break;
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
    }
    
  }
  





  public final double getElement(int row, int column)
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
    
    



    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d1"));
  }
  




  public final void getRow(int row, Vector4d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
    }
  }
  





  public final void getRow(int row, double[] v)
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
    }
    else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
    }
  }
  







  public final void getColumn(int column, Vector4d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));
    }
  }
  









  public final void getColumn(int column, double[] v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));
    }
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
  









  public final double get(Matrix3d m1, Vector3d t1)
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
    
    x = m03;
    y = m13;
    z = m23;
    
    return Matrix3d.max3(tmp_scale);
  }
  









  public final double get(Matrix3f m1, Vector3d t1)
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
    
    return Matrix3d.max3(tmp_scale);
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
  







  public final void get(Quat4d q1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    


    double ww = 0.25D * (1.0D + tmp_rot[0] + tmp_rot[4] + tmp_rot[8]);
    if ((ww < 0.0D ? -ww : ww) >= 1.0E-30D) {
      w = Math.sqrt(ww);
      ww = 0.25D / w;
      x = ((tmp_rot[7] - tmp_rot[5]) * ww);
      y = ((tmp_rot[2] - tmp_rot[6]) * ww);
      z = ((tmp_rot[3] - tmp_rot[1]) * ww);
      return;
    }
    
    w = 0.0D;
    ww = -0.5D * (tmp_rot[4] + tmp_rot[8]);
    if ((ww < 0.0D ? -ww : ww) >= 1.0E-30D) {
      x = Math.sqrt(ww);
      ww = 0.5D / x;
      y = (tmp_rot[3] * ww);
      z = (tmp_rot[6] * ww);
      return;
    }
    
    x = 0.0D;
    ww = 0.5D * (1.0D - tmp_rot[8]);
    if ((ww < 0.0D ? -ww : ww) >= 1.0E-30D) {
      y = Math.sqrt(ww);
      z = (tmp_rot[7] / (2.0D * y));
      return;
    }
    
    y = 0.0D;
    z = 1.0D;
  }
  




  public final void get(Vector3d trans)
  {
    x = m03;
    y = m13;
    z = m23;
  }
  





  public final void getRotationScale(Matrix3f m1)
  {
    m00 = ((float)m00);m01 = ((float)m01);m02 = ((float)m02);
    m10 = ((float)m10);m11 = ((float)m11);m12 = ((float)m12);
    m20 = ((float)m20);m21 = ((float)m21);m22 = ((float)m22);
  }
  





  public final void getRotationScale(Matrix3d m1)
  {
    m00 = m00;m01 = m01;m02 = m02;
    m10 = m10;m11 = m11;m12 = m12;
    m20 = m20;m21 = m21;m22 = m22;
  }
  








  public final double getScale()
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    
    return Matrix3d.max3(tmp_scale);
  }
  






  public final void setRotationScale(Matrix3d m1)
  {
    m00 = m00;m01 = m01;m02 = m02;
    m10 = m10;m11 = m11;m12 = m12;
    m20 = m20;m21 = m21;m22 = m22;
  }
  





  public final void setRotationScale(Matrix3f m1)
  {
    m00 = m00;m01 = m01;m02 = m02;
    m10 = m10;m11 = m11;m12 = m12;
    m20 = m20;m21 = m21;m22 = m22;
  }
  






  public final void setScale(double scale)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = (tmp_rot[0] * scale);
    m01 = (tmp_rot[1] * scale);
    m02 = (tmp_rot[2] * scale);
    
    m10 = (tmp_rot[3] * scale);
    m11 = (tmp_rot[4] * scale);
    m12 = (tmp_rot[5] * scale);
    
    m20 = (tmp_rot[6] * scale);
    m21 = (tmp_rot[7] * scale);
    m22 = (tmp_rot[8] * scale);
  }
  









  public final void setRow(int row, double x, double y, double z, double w)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
    }
    
  }
  





  public final void setRow(int row, Vector4d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
    }
    
  }
  




  public final void setRow(int row, double[] v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
    }
    
  }
  







  public final void setColumn(int column, double x, double y, double z, double w)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
    }
    
  }
  




  public final void setColumn(int column, Vector4d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
    }
    
  }
  




  public final void setColumn(int column, double[] v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
    }
    
  }
  



  public final void add(double scalar)
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
  






  public final void add(double scalar, Matrix4d m1)
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
  





  public final void add(Matrix4d m1, Matrix4d m2)
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
  




  public final void add(Matrix4d m1)
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
  






  public final void sub(Matrix4d m1, Matrix4d m2)
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
  






  public final void sub(Matrix4d m1)
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
    double temp = m10;
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
  




  public final void transpose(Matrix4d m1)
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
  





  public final void set(double[] m)
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
  







  public final void set(Matrix3f m1)
  {
    m00 = m00;m01 = m01;m02 = m02;m03 = 0.0D;
    m10 = m10;m11 = m11;m12 = m12;m13 = 0.0D;
    m20 = m20;m21 = m21;m22 = m22;m23 = 0.0D;
    m30 = 0.0D;m31 = 0.0D;m32 = 0.0D;m33 = 1.0D;
  }
  







  public final void set(Matrix3d m1)
  {
    m00 = m00;m01 = m01;m02 = m02;m03 = 0.0D;
    m10 = m10;m11 = m11;m12 = m12;m13 = 0.0D;
    m20 = m20;m21 = m21;m22 = m22;m23 = 0.0D;
    m30 = 0.0D;m31 = 0.0D;m32 = 0.0D;m33 = 1.0D;
  }
  





  public final void set(Quat4d q1)
  {
    m00 = (1.0D - 2.0D * y * y - 2.0D * z * z);
    m10 = (2.0D * (x * y + w * z));
    m20 = (2.0D * (x * z - w * y));
    
    m01 = (2.0D * (x * y - w * z));
    m11 = (1.0D - 2.0D * x * x - 2.0D * z * z);
    m21 = (2.0D * (y * z + w * x));
    
    m02 = (2.0D * (x * z + w * y));
    m12 = (2.0D * (y * z - w * x));
    m22 = (1.0D - 2.0D * x * x - 2.0D * y * y);
    
    m03 = 0.0D;
    m13 = 0.0D;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  





  public final void set(AxisAngle4d a1)
  {
    double mag = Math.sqrt(x * x + y * y + z * z);
    
    if (mag < 1.0E-10D) {
      m00 = 1.0D;
      m01 = 0.0D;
      m02 = 0.0D;
      
      m10 = 0.0D;
      m11 = 1.0D;
      m12 = 0.0D;
      
      m20 = 0.0D;
      m21 = 0.0D;
      m22 = 1.0D;
    } else {
      mag = 1.0D / mag;
      double ax = x * mag;
      double ay = y * mag;
      double az = z * mag;
      
      double sinTheta = Math.sin(angle);
      double cosTheta = Math.cos(angle);
      double t = 1.0D - cosTheta;
      
      double xz = ax * az;
      double xy = ax * ay;
      double yz = ay * az;
      
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
    
    m03 = 0.0D;
    m13 = 0.0D;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  





  public final void set(Quat4f q1)
  {
    m00 = (1.0D - 2.0D * y * y - 2.0D * z * z);
    m10 = (2.0D * (x * y + w * z));
    m20 = (2.0D * (x * z - w * y));
    
    m01 = (2.0D * (x * y - w * z));
    m11 = (1.0D - 2.0D * x * x - 2.0D * z * z);
    m21 = (2.0D * (y * z + w * x));
    
    m02 = (2.0D * (x * z + w * y));
    m12 = (2.0D * (y * z - w * x));
    m22 = (1.0D - 2.0D * x * x - 2.0D * y * y);
    
    m03 = 0.0D;
    m13 = 0.0D;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  





  public final void set(AxisAngle4f a1)
  {
    double mag = Math.sqrt(x * x + y * y + z * z);
    
    if (mag < 1.0E-10D) {
      m00 = 1.0D;
      m01 = 0.0D;
      m02 = 0.0D;
      
      m10 = 0.0D;
      m11 = 1.0D;
      m12 = 0.0D;
      
      m20 = 0.0D;
      m21 = 0.0D;
      m22 = 1.0D;
    } else {
      mag = 1.0D / mag;
      double ax = x * mag;
      double ay = y * mag;
      double az = z * mag;
      
      double sinTheta = Math.sin(angle);
      double cosTheta = Math.cos(angle);
      double t = 1.0D - cosTheta;
      
      double xz = ax * az;
      double xy = ax * ay;
      double yz = ay * az;
      
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
    m03 = 0.0D;
    m13 = 0.0D;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  







  public final void set(Quat4d q1, Vector3d t1, double s)
  {
    m00 = (s * (1.0D - 2.0D * y * y - 2.0D * z * z));
    m10 = (s * (2.0D * (x * y + w * z)));
    m20 = (s * (2.0D * (x * z - w * y)));
    
    m01 = (s * (2.0D * (x * y - w * z)));
    m11 = (s * (1.0D - 2.0D * x * x - 2.0D * z * z));
    m21 = (s * (2.0D * (y * z + w * x)));
    
    m02 = (s * (2.0D * (x * z + w * y)));
    m12 = (s * (2.0D * (y * z - w * x)));
    m22 = (s * (1.0D - 2.0D * x * x - 2.0D * y * y));
    
    m03 = x;
    m13 = y;
    m23 = z;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  







  public final void set(Quat4f q1, Vector3d t1, double s)
  {
    m00 = (s * (1.0D - 2.0D * y * y - 2.0D * z * z));
    m10 = (s * (2.0D * (x * y + w * z)));
    m20 = (s * (2.0D * (x * z - w * y)));
    
    m01 = (s * (2.0D * (x * y - w * z)));
    m11 = (s * (1.0D - 2.0D * x * x - 2.0D * z * z));
    m21 = (s * (2.0D * (y * z + w * x)));
    
    m02 = (s * (2.0D * (x * z + w * y)));
    m12 = (s * (2.0D * (y * z - w * x)));
    m22 = (s * (1.0D - 2.0D * x * x - 2.0D * y * y));
    
    m03 = x;
    m13 = y;
    m23 = z;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  







  public final void set(Quat4f q1, Vector3f t1, float s)
  {
    m00 = (s * (1.0D - 2.0D * y * y - 2.0D * z * z));
    m10 = (s * (2.0D * (x * y + w * z)));
    m20 = (s * (2.0D * (x * z - w * y)));
    
    m01 = (s * (2.0D * (x * y - w * z)));
    m11 = (s * (1.0D - 2.0D * x * x - 2.0D * z * z));
    m21 = (s * (2.0D * (y * z + w * x)));
    
    m02 = (s * (2.0D * (x * z + w * y)));
    m12 = (s * (2.0D * (y * z - w * x)));
    m22 = (s * (1.0D - 2.0D * x * x - 2.0D * y * y));
    
    m03 = x;
    m13 = y;
    m23 = z;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
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
  





  public final void set(Matrix4d m1)
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
  






  public final void invert(Matrix4d m1)
  {
    invertGeneral(m1);
  }
  



  public final void invert()
  {
    invertGeneral(this);
  }
  







  final void invertGeneral(Matrix4d m1)
  {
    double[] result = new double[16];
    int[] row_perm = new int[4];
    



    double[] tmp = new double[16];
    
    tmp[0] = m00;
    tmp[1] = m01;
    tmp[2] = m02;
    tmp[3] = m03;
    
    tmp[4] = m10;
    tmp[5] = m11;
    tmp[6] = m12;
    tmp[7] = m13;
    
    tmp[8] = m20;
    tmp[9] = m21;
    tmp[10] = m22;
    tmp[11] = m23;
    
    tmp[12] = m30;
    tmp[13] = m31;
    tmp[14] = m32;
    tmp[15] = m33;
    

    if (!luDecomposition(tmp, row_perm))
    {
      throw new SingularMatrixException(VecMathI18N.getString("Matrix4d10"));
    }
    

    for (int i = 0; i < 16; i++) result[i] = 0.0D;
    result[0] = 1.0D;result[5] = 1.0D;result[10] = 1.0D;result[15] = 1.0D;
    luBacksubstitution(tmp, row_perm, result);
    
    m00 = result[0];
    m01 = result[1];
    m02 = result[2];
    m03 = result[3];
    
    m10 = result[4];
    m11 = result[5];
    m12 = result[6];
    m13 = result[7];
    
    m20 = result[8];
    m21 = result[9];
    m22 = result[10];
    m23 = result[11];
    
    m30 = result[12];
    m31 = result[13];
    m32 = result[14];
    m33 = result[15];
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
        throw new RuntimeException(VecMathI18N.getString("Matrix4d11"));
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
  











  public final double determinant()
  {
    double det = m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33);
    
    det -= m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33);
    
    det += m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33);
    
    det -= m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32);
    

    return det;
  }
  





  public final void set(double scale)
  {
    m00 = scale;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = 0.0D;
    
    m10 = 0.0D;
    m11 = scale;
    m12 = 0.0D;
    m13 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = scale;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  





  public final void set(Vector3d v1)
  {
    m00 = 1.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = x;
    
    m10 = 0.0D;
    m11 = 1.0D;
    m12 = 0.0D;
    m13 = y;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
    m23 = z;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  







  public final void set(double scale, Vector3d v1)
  {
    m00 = scale;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = x;
    
    m10 = 0.0D;
    m11 = scale;
    m12 = 0.0D;
    m13 = y;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = scale;
    m23 = z;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  







  public final void set(Vector3d v1, double scale)
  {
    m00 = scale;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = (scale * x);
    
    m10 = 0.0D;
    m11 = scale;
    m12 = 0.0D;
    m13 = (scale * y);
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = scale;
    m23 = (scale * z);
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
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
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  









  public final void set(Matrix3d m1, Vector3d t1, double scale)
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
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  






  public final void setTranslation(Vector3d trans)
  {
    m03 = x;
    m13 = y;
    m23 = z;
  }
  







  public final void rotX(double angle)
  {
    double sinAngle = Math.sin(angle);
    double cosAngle = Math.cos(angle);
    
    m00 = 1.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = 0.0D;
    
    m10 = 0.0D;
    m11 = cosAngle;
    m12 = (-sinAngle);
    m13 = 0.0D;
    
    m20 = 0.0D;
    m21 = sinAngle;
    m22 = cosAngle;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  







  public final void rotY(double angle)
  {
    double sinAngle = Math.sin(angle);
    double cosAngle = Math.cos(angle);
    
    m00 = cosAngle;
    m01 = 0.0D;
    m02 = sinAngle;
    m03 = 0.0D;
    
    m10 = 0.0D;
    m11 = 1.0D;
    m12 = 0.0D;
    m13 = 0.0D;
    
    m20 = (-sinAngle);
    m21 = 0.0D;
    m22 = cosAngle;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  







  public final void rotZ(double angle)
  {
    double sinAngle = Math.sin(angle);
    double cosAngle = Math.cos(angle);
    
    m00 = cosAngle;
    m01 = (-sinAngle);
    m02 = 0.0D;
    m03 = 0.0D;
    
    m10 = sinAngle;
    m11 = cosAngle;
    m12 = 0.0D;
    m13 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
    m23 = 0.0D;
    
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 1.0D;
  }
  




  public final void mul(double scalar)
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
  






  public final void mul(double scalar, Matrix4d m1)
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
  










  public final void mul(Matrix4d m1)
  {
    double m00 = this.m00 * m00 + this.m01 * m10 + this.m02 * m20 + this.m03 * m30;
    
    double m01 = this.m00 * m01 + this.m01 * m11 + this.m02 * m21 + this.m03 * m31;
    
    double m02 = this.m00 * m02 + this.m01 * m12 + this.m02 * m22 + this.m03 * m32;
    
    double m03 = this.m00 * m03 + this.m01 * m13 + this.m02 * m23 + this.m03 * m33;
    

    double m10 = this.m10 * m00 + this.m11 * m10 + this.m12 * m20 + this.m13 * m30;
    
    double m11 = this.m10 * m01 + this.m11 * m11 + this.m12 * m21 + this.m13 * m31;
    
    double m12 = this.m10 * m02 + this.m11 * m12 + this.m12 * m22 + this.m13 * m32;
    
    double m13 = this.m10 * m03 + this.m11 * m13 + this.m12 * m23 + this.m13 * m33;
    

    double m20 = this.m20 * m00 + this.m21 * m10 + this.m22 * m20 + this.m23 * m30;
    
    double m21 = this.m20 * m01 + this.m21 * m11 + this.m22 * m21 + this.m23 * m31;
    
    double m22 = this.m20 * m02 + this.m21 * m12 + this.m22 * m22 + this.m23 * m32;
    
    double m23 = this.m20 * m03 + this.m21 * m13 + this.m22 * m23 + this.m23 * m33;
    

    double m30 = this.m30 * m00 + this.m31 * m10 + this.m32 * m20 + this.m33 * m30;
    
    double m31 = this.m30 * m01 + this.m31 * m11 + this.m32 * m21 + this.m33 * m31;
    
    double m32 = this.m30 * m02 + this.m31 * m12 + this.m32 * m22 + this.m33 * m32;
    
    double m33 = this.m30 * m03 + this.m31 * m13 + this.m32 * m23 + this.m33 * m33;
    

    this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
    this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
    this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
    this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
  }
  






  public final void mul(Matrix4d m1, Matrix4d m2)
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


      double m00 = m00 * m00 + m01 * m10 + m02 * m20 + m03 * m30;
      double m01 = m00 * m01 + m01 * m11 + m02 * m21 + m03 * m31;
      double m02 = m00 * m02 + m01 * m12 + m02 * m22 + m03 * m32;
      double m03 = m00 * m03 + m01 * m13 + m02 * m23 + m03 * m33;
      
      double m10 = m10 * m00 + m11 * m10 + m12 * m20 + m13 * m30;
      double m11 = m10 * m01 + m11 * m11 + m12 * m21 + m13 * m31;
      double m12 = m10 * m02 + m11 * m12 + m12 * m22 + m13 * m32;
      double m13 = m10 * m03 + m11 * m13 + m12 * m23 + m13 * m33;
      
      double m20 = m20 * m00 + m21 * m10 + m22 * m20 + m23 * m30;
      double m21 = m20 * m01 + m21 * m11 + m22 * m21 + m23 * m31;
      double m22 = m20 * m02 + m21 * m12 + m22 * m22 + m23 * m32;
      double m23 = m20 * m03 + m21 * m13 + m22 * m23 + m23 * m33;
      
      double m30 = m30 * m00 + m31 * m10 + m32 * m20 + m33 * m30;
      double m31 = m30 * m01 + m31 * m11 + m32 * m21 + m33 * m31;
      double m32 = m30 * m02 + m31 * m12 + m32 * m22 + m33 * m32;
      double m33 = m30 * m03 + m31 * m13 + m32 * m23 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  







  public final void mulTransposeBoth(Matrix4d m1, Matrix4d m2)
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

      double m00 = m00 * m00 + m10 * m01 + m20 * m02 + m30 * m03;
      double m01 = m00 * m10 + m10 * m11 + m20 * m12 + m30 * m13;
      double m02 = m00 * m20 + m10 * m21 + m20 * m22 + m30 * m23;
      double m03 = m00 * m30 + m10 * m31 + m20 * m32 + m30 * m33;
      
      double m10 = m01 * m00 + m11 * m01 + m21 * m02 + m31 * m03;
      double m11 = m01 * m10 + m11 * m11 + m21 * m12 + m31 * m13;
      double m12 = m01 * m20 + m11 * m21 + m21 * m22 + m31 * m23;
      double m13 = m01 * m30 + m11 * m31 + m21 * m32 + m31 * m33;
      
      double m20 = m02 * m00 + m12 * m01 + m22 * m02 + m32 * m03;
      double m21 = m02 * m10 + m12 * m11 + m22 * m12 + m32 * m13;
      double m22 = m02 * m20 + m12 * m21 + m22 * m22 + m32 * m23;
      double m23 = m02 * m30 + m12 * m31 + m22 * m32 + m32 * m33;
      
      double m30 = m03 * m00 + m13 * m01 + m23 * m02 + m33 * m03;
      double m31 = m03 * m10 + m13 * m11 + m23 * m12 + m33 * m13;
      double m32 = m03 * m20 + m13 * m21 + m23 * m22 + m33 * m23;
      double m33 = m03 * m30 + m13 * m31 + m23 * m32 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  









  public final void mulTransposeRight(Matrix4d m1, Matrix4d m2)
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

      double m00 = m00 * m00 + m01 * m01 + m02 * m02 + m03 * m03;
      double m01 = m00 * m10 + m01 * m11 + m02 * m12 + m03 * m13;
      double m02 = m00 * m20 + m01 * m21 + m02 * m22 + m03 * m23;
      double m03 = m00 * m30 + m01 * m31 + m02 * m32 + m03 * m33;
      
      double m10 = m10 * m00 + m11 * m01 + m12 * m02 + m13 * m03;
      double m11 = m10 * m10 + m11 * m11 + m12 * m12 + m13 * m13;
      double m12 = m10 * m20 + m11 * m21 + m12 * m22 + m13 * m23;
      double m13 = m10 * m30 + m11 * m31 + m12 * m32 + m13 * m33;
      
      double m20 = m20 * m00 + m21 * m01 + m22 * m02 + m23 * m03;
      double m21 = m20 * m10 + m21 * m11 + m22 * m12 + m23 * m13;
      double m22 = m20 * m20 + m21 * m21 + m22 * m22 + m23 * m23;
      double m23 = m20 * m30 + m21 * m31 + m22 * m32 + m23 * m33;
      
      double m30 = m30 * m00 + m31 * m01 + m32 * m02 + m33 * m03;
      double m31 = m30 * m10 + m31 * m11 + m32 * m12 + m33 * m13;
      double m32 = m30 * m20 + m31 * m21 + m32 * m22 + m33 * m23;
      double m33 = m30 * m30 + m31 * m31 + m32 * m32 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  







  public final void mulTransposeLeft(Matrix4d m1, Matrix4d m2)
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


      double m00 = m00 * m00 + m10 * m10 + m20 * m20 + m30 * m30;
      double m01 = m00 * m01 + m10 * m11 + m20 * m21 + m30 * m31;
      double m02 = m00 * m02 + m10 * m12 + m20 * m22 + m30 * m32;
      double m03 = m00 * m03 + m10 * m13 + m20 * m23 + m30 * m33;
      
      double m10 = m01 * m00 + m11 * m10 + m21 * m20 + m31 * m30;
      double m11 = m01 * m01 + m11 * m11 + m21 * m21 + m31 * m31;
      double m12 = m01 * m02 + m11 * m12 + m21 * m22 + m31 * m32;
      double m13 = m01 * m03 + m11 * m13 + m21 * m23 + m31 * m33;
      
      double m20 = m02 * m00 + m12 * m10 + m22 * m20 + m32 * m30;
      double m21 = m02 * m01 + m12 * m11 + m22 * m21 + m32 * m31;
      double m22 = m02 * m02 + m12 * m12 + m22 * m22 + m32 * m32;
      double m23 = m02 * m03 + m12 * m13 + m22 * m23 + m32 * m33;
      
      double m30 = m03 * m00 + m13 * m10 + m23 * m20 + m33 * m30;
      double m31 = m03 * m01 + m13 * m11 + m23 * m21 + m33 * m31;
      double m32 = m03 * m02 + m13 * m12 + m23 * m22 + m33 * m32;
      double m33 = m03 * m03 + m13 * m13 + m23 * m23 + m33 * m33;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;this.m03 = m03;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;this.m13 = m13;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;this.m23 = m23;
      this.m30 = m30;this.m31 = m31;this.m32 = m32;this.m33 = m33;
    }
  }
  







  public boolean equals(Matrix4d m1)
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
      Matrix4d m2 = (Matrix4d)t1;
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m03 == m03) && (m10 == m10) && (m11 == m11) && (m12 == m12) && (m13 == m13) && (m20 == m20) && (m21 == m21) && (m22 == m22) && (m23 == m23) && (m30 == m30) && (m31 == m31) && (m32 == m32) && (m33 == m33);


    }
    catch (ClassCastException e1)
    {

      return false; } catch (NullPointerException e2) {}
    return false;
  }
  
  /**
   * @deprecated
   */
  public boolean epsilonEquals(Matrix4d m1, float epsilon) {
    return epsilonEquals(m1, epsilon);
  }
  










  public boolean epsilonEquals(Matrix4d m1, double epsilon)
  {
    double diff = m00 - m00;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m01 - m01;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m02 - m02;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m03 - m03;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m10 - m10;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m11 - m11;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m12 - m12;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m13 - m13;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m20 - m20;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m21 - m21;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m22 - m22;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m23 - m23;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m30 - m30;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m31 - m31;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m32 - m32;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m33 - m33;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    return true;
  }
  







  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m00);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m01);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m02);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m03);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m10);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m11);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m12);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m13);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m20);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m21);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m22);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m23);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m30);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m31);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m32);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m33);
    return (int)(bits ^ bits >> 32);
  }
  








  public final void transform(Tuple4d vec, Tuple4d vecOut)
  {
    double x = m00 * x + m01 * y + m02 * z + m03 * w;
    
    double y = m10 * x + m11 * y + m12 * z + m13 * w;
    
    double z = m20 * x + m21 * y + m22 * z + m23 * w;
    
    w = (m30 * x + m31 * y + m32 * z + m33 * w);
    
    x = x;
    y = y;
    z = z;
  }
  







  public final void transform(Tuple4d vec)
  {
    double x = m00 * x + m01 * y + m02 * z + m03 * w;
    
    double y = m10 * x + m11 * y + m12 * z + m13 * w;
    
    double z = m20 * x + m21 * y + m22 * z + m23 * w;
    
    w = (m30 * x + m31 * y + m32 * z + m33 * w);
    
    x = x;
    y = y;
    z = z;
  }
  







  public final void transform(Tuple4f vec, Tuple4f vecOut)
  {
    float x = (float)(m00 * x + m01 * y + m02 * z + m03 * w);
    
    float y = (float)(m10 * x + m11 * y + m12 * z + m13 * w);
    
    float z = (float)(m20 * x + m21 * y + m22 * z + m23 * w);
    
    w = ((float)(m30 * x + m31 * y + m32 * z + m33 * w));
    
    x = x;
    y = y;
    z = z;
  }
  







  public final void transform(Tuple4f vec)
  {
    float x = (float)(m00 * x + m01 * y + m02 * z + m03 * w);
    
    float y = (float)(m10 * x + m11 * y + m12 * z + m13 * w);
    
    float z = (float)(m20 * x + m21 * y + m22 * z + m23 * w);
    
    w = ((float)(m30 * x + m31 * y + m32 * z + m33 * w));
    
    x = x;
    y = y;
    z = z;
  }
  









  public final void transform(Point3d point, Point3d pointOut)
  {
    double x = m00 * x + m01 * y + m02 * z + m03;
    double y = m10 * x + m11 * y + m12 * z + m13;
    z = (m20 * x + m21 * y + m22 * z + m23);
    x = x;
    y = y;
  }
  









  public final void transform(Point3d point)
  {
    double x = m00 * x + m01 * y + m02 * z + m03;
    double y = m10 * x + m11 * y + m12 * z + m13;
    z = (m20 * x + m21 * y + m22 * z + m23);
    x = x;
    y = y;
  }
  










  public final void transform(Point3f point, Point3f pointOut)
  {
    float x = (float)(m00 * x + m01 * y + m02 * z + m03);
    float y = (float)(m10 * x + m11 * y + m12 * z + m13);
    z = ((float)(m20 * x + m21 * y + m22 * z + m23));
    x = x;
    y = y;
  }
  








  public final void transform(Point3f point)
  {
    float x = (float)(m00 * x + m01 * y + m02 * z + m03);
    float y = (float)(m10 * x + m11 * y + m12 * z + m13);
    z = ((float)(m20 * x + m21 * y + m22 * z + m23));
    x = x;
    y = y;
  }
  








  public final void transform(Vector3d normal, Vector3d normalOut)
  {
    double x = m00 * x + m01 * y + m02 * z;
    double y = m10 * x + m11 * y + m12 * z;
    z = (m20 * x + m21 * y + m22 * z);
    x = x;
    y = y;
  }
  








  public final void transform(Vector3d normal)
  {
    double x = m00 * x + m01 * y + m02 * z;
    double y = m10 * x + m11 * y + m12 * z;
    z = (m20 * x + m21 * y + m22 * z);
    x = x;
    y = y;
  }
  








  public final void transform(Vector3f normal, Vector3f normalOut)
  {
    float x = (float)(m00 * x + m01 * y + m02 * z);
    float y = (float)(m10 * x + m11 * y + m12 * z);
    z = ((float)(m20 * x + m21 * y + m22 * z));
    x = x;
    y = y;
  }
  








  public final void transform(Vector3f normal)
  {
    float x = (float)(m00 * x + m01 * y + m02 * z);
    float y = (float)(m10 * x + m11 * y + m12 * z);
    z = ((float)(m20 * x + m21 * y + m22 * z));
    x = x;
    y = y;
  }
  









  public final void setRotation(Matrix3d m1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = (m00 * tmp_scale[0]);
    m01 = (m01 * tmp_scale[1]);
    m02 = (m02 * tmp_scale[2]);
    
    m10 = (m10 * tmp_scale[0]);
    m11 = (m11 * tmp_scale[1]);
    m12 = (m12 * tmp_scale[2]);
    
    m20 = (m20 * tmp_scale[0]);
    m21 = (m21 * tmp_scale[1]);
    m22 = (m22 * tmp_scale[2]);
  }
  













  public final void setRotation(Matrix3f m1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = (m00 * tmp_scale[0]);
    m01 = (m01 * tmp_scale[1]);
    m02 = (m02 * tmp_scale[2]);
    
    m10 = (m10 * tmp_scale[0]);
    m11 = (m11 * tmp_scale[1]);
    m12 = (m12 * tmp_scale[2]);
    
    m20 = (m20 * tmp_scale[0]);
    m21 = (m21 * tmp_scale[1]);
    m22 = (m22 * tmp_scale[2]);
  }
  









  public final void setRotation(Quat4f q1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((1.0D - 2.0F * y * y - 2.0F * z * z) * tmp_scale[0]);
    m10 = (2.0D * (x * y + w * z) * tmp_scale[0]);
    m20 = (2.0D * (x * z - w * y) * tmp_scale[0]);
    
    m01 = (2.0D * (x * y - w * z) * tmp_scale[1]);
    m11 = ((1.0D - 2.0F * x * x - 2.0F * z * z) * tmp_scale[1]);
    m21 = (2.0D * (y * z + w * x) * tmp_scale[1]);
    
    m02 = (2.0D * (x * z + w * y) * tmp_scale[2]);
    m12 = (2.0D * (y * z - w * x) * tmp_scale[2]);
    m22 = ((1.0D - 2.0F * x * x - 2.0F * y * y) * tmp_scale[2]);
  }
  












  public final void setRotation(Quat4d q1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    
    m00 = ((1.0D - 2.0D * y * y - 2.0D * z * z) * tmp_scale[0]);
    m10 = (2.0D * (x * y + w * z) * tmp_scale[0]);
    m20 = (2.0D * (x * z - w * y) * tmp_scale[0]);
    
    m01 = (2.0D * (x * y - w * z) * tmp_scale[1]);
    m11 = ((1.0D - 2.0D * x * x - 2.0D * z * z) * tmp_scale[1]);
    m21 = (2.0D * (y * z + w * x) * tmp_scale[1]);
    
    m02 = (2.0D * (x * z + w * y) * tmp_scale[2]);
    m12 = (2.0D * (y * z - w * x) * tmp_scale[2]);
    m22 = ((1.0D - 2.0D * x * x - 2.0D * y * y) * tmp_scale[2]);
  }
  











  public final void setRotation(AxisAngle4d a1)
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    getScaleRotate(tmp_scale, tmp_rot);
    
    double mag = 1.0D / Math.sqrt(x * x + y * y + z * z);
    double ax = x * mag;
    double ay = y * mag;
    double az = z * mag;
    
    double sinTheta = Math.sin(angle);
    double cosTheta = Math.cos(angle);
    double t = 1.0D - cosTheta;
    
    double xz = x * z;
    double xy = x * y;
    double yz = y * z;
    
    m00 = ((t * ax * ax + cosTheta) * tmp_scale[0]);
    m01 = ((t * xy - sinTheta * az) * tmp_scale[1]);
    m02 = ((t * xz + sinTheta * ay) * tmp_scale[2]);
    
    m10 = ((t * xy + sinTheta * az) * tmp_scale[0]);
    m11 = ((t * ay * ay + cosTheta) * tmp_scale[1]);
    m12 = ((t * yz - sinTheta * ax) * tmp_scale[2]);
    
    m20 = ((t * xz - sinTheta * ay) * tmp_scale[0]);
    m21 = ((t * yz + sinTheta * ax) * tmp_scale[1]);
    m22 = ((t * az * az + cosTheta) * tmp_scale[2]);
  }
  




  public final void setZero()
  {
    m00 = 0.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    m03 = 0.0D;
    m10 = 0.0D;
    m11 = 0.0D;
    m12 = 0.0D;
    m13 = 0.0D;
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 0.0D;
    m23 = 0.0D;
    m30 = 0.0D;
    m31 = 0.0D;
    m32 = 0.0D;
    m33 = 0.0D;
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
  





  public final void negate(Matrix4d m1)
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
  
  private final void getScaleRotate(double[] scales, double[] rots) { double[] tmp = new double[9];
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
    Matrix4d m1 = null;
    try {
      m1 = (Matrix4d)super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
    
    return m1;
  }
  






  public final double getM00()
  {
    return m00;
  }
  







  public final void setM00(double m00)
  {
    this.m00 = m00;
  }
  






  public final double getM01()
  {
    return m01;
  }
  






  public final void setM01(double m01)
  {
    this.m01 = m01;
  }
  






  public final double getM02()
  {
    return m02;
  }
  






  public final void setM02(double m02)
  {
    this.m02 = m02;
  }
  






  public final double getM10()
  {
    return m10;
  }
  






  public final void setM10(double m10)
  {
    this.m10 = m10;
  }
  






  public final double getM11()
  {
    return m11;
  }
  






  public final void setM11(double m11)
  {
    this.m11 = m11;
  }
  






  public final double getM12()
  {
    return m12;
  }
  







  public final void setM12(double m12)
  {
    this.m12 = m12;
  }
  






  public final double getM20()
  {
    return m20;
  }
  






  public final void setM20(double m20)
  {
    this.m20 = m20;
  }
  






  public final double getM21()
  {
    return m21;
  }
  






  public final void setM21(double m21)
  {
    this.m21 = m21;
  }
  






  public final double getM22()
  {
    return m22;
  }
  






  public final void setM22(double m22)
  {
    this.m22 = m22;
  }
  






  public final double getM03()
  {
    return m03;
  }
  






  public final void setM03(double m03)
  {
    this.m03 = m03;
  }
  






  public final double getM13()
  {
    return m13;
  }
  






  public final void setM13(double m13)
  {
    this.m13 = m13;
  }
  






  public final double getM23()
  {
    return m23;
  }
  






  public final void setM23(double m23)
  {
    this.m23 = m23;
  }
  






  public final double getM30()
  {
    return m30;
  }
  






  public final void setM30(double m30)
  {
    this.m30 = m30;
  }
  






  public final double getM31()
  {
    return m31;
  }
  






  public final void setM31(double m31)
  {
    this.m31 = m31;
  }
  







  public final double getM32()
  {
    return m32;
  }
  






  public final void setM32(double m32)
  {
    this.m32 = m32;
  }
  






  public final double getM33()
  {
    return m33;
  }
  






  public final void setM33(double m33)
  {
    this.m33 = m33;
  }
}
