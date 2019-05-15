package javax.vecmath;

import java.io.Serializable;






























































































public class Matrix3f
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 329697160112089834L;
  public float m00;
  public float m01;
  public float m02;
  public float m10;
  public float m11;
  public float m12;
  public float m20;
  public float m21;
  public float m22;
  private static final double EPS = 1.0E-8D;
  
  public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22)
  {
    this.m00 = m00;
    this.m01 = m01;
    this.m02 = m02;
    
    this.m10 = m10;
    this.m11 = m11;
    this.m12 = m12;
    
    this.m20 = m20;
    this.m21 = m21;
    this.m22 = m22;
  }
  






  public Matrix3f(float[] v)
  {
    m00 = v[0];
    m01 = v[1];
    m02 = v[2];
    
    m10 = v[3];
    m11 = v[4];
    m12 = v[5];
    
    m20 = v[6];
    m21 = v[7];
    m22 = v[8];
  }
  






  public Matrix3f(Matrix3d m1)
  {
    m00 = ((float)m00);
    m01 = ((float)m01);
    m02 = ((float)m02);
    
    m10 = ((float)m10);
    m11 = ((float)m11);
    m12 = ((float)m12);
    
    m20 = ((float)m20);
    m21 = ((float)m21);
    m22 = ((float)m22);
  }
  







  public Matrix3f(Matrix3f m1)
  {
    m00 = m00;
    m01 = m01;
    m02 = m02;
    
    m10 = m10;
    m11 = m11;
    m12 = m12;
    
    m20 = m20;
    m21 = m21;
    m22 = m22;
  }
  





  public Matrix3f()
  {
    m00 = 0.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    
    m10 = 0.0F;
    m11 = 0.0F;
    m12 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 0.0F;
  }
  




  public String toString()
  {
    return m00 + ", " + m01 + ", " + m02 + "\n" + m10 + ", " + m11 + ", " + m12 + "\n" + m20 + ", " + m21 + ", " + m22 + "\n";
  }
  






  public final void setIdentity()
  {
    m00 = 1.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    
    m10 = 0.0F;
    m11 = 1.0F;
    m12 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 1.0F;
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
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
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
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
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
      
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
      }
      
      break;
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
    }
    
  }
  



  public final void getRow(int row, Vector3f v)
  {
    if (row == 0) {
      x = m00;
      y = m01;
      z = m02;
    } else if (row == 1) {
      x = m10;
      y = m11;
      z = m12;
    } else if (row == 2) {
      x = m20;
      y = m21;
      z = m22;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
    }
  }
  





  public final void getRow(int row, float[] v)
  {
    if (row == 0) {
      v[0] = m00;
      v[1] = m01;
      v[2] = m02;
    } else if (row == 1) {
      v[0] = m10;
      v[1] = m11;
      v[2] = m12;
    } else if (row == 2) {
      v[0] = m20;
      v[1] = m21;
      v[2] = m22;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
    }
  }
  






  public final void getColumn(int column, Vector3f v)
  {
    if (column == 0) {
      x = m00;
      y = m10;
      z = m20;
    } else if (column == 1) {
      x = m01;
      y = m11;
      z = m21;
    } else if (column == 2) {
      x = m02;
      y = m12;
      z = m22;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
    }
  }
  






  public final void getColumn(int column, float[] v)
  {
    if (column == 0) {
      v[0] = m00;
      v[1] = m10;
      v[2] = m20;
    } else if (column == 1) {
      v[0] = m01;
      v[1] = m11;
      v[2] = m21;
    } else if (column == 2) {
      v[0] = m02;
      v[1] = m12;
      v[2] = m22;
    } else {
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
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
      }
      break;
    }
    
    



    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
  }
  







  public final void setRow(int row, float x, float y, float z)
  {
    switch (row) {
    case 0: 
      m00 = x;
      m01 = y;
      m02 = z;
      break;
    
    case 1: 
      m10 = x;
      m11 = y;
      m12 = z;
      break;
    
    case 2: 
      m20 = x;
      m21 = y;
      m22 = z;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
    }
    
  }
  




  public final void setRow(int row, Vector3f v)
  {
    switch (row) {
    case 0: 
      m00 = x;
      m01 = y;
      m02 = z;
      break;
    
    case 1: 
      m10 = x;
      m11 = y;
      m12 = z;
      break;
    
    case 2: 
      m20 = x;
      m21 = y;
      m22 = z;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
    }
    
  }
  




  public final void setRow(int row, float[] v)
  {
    switch (row) {
    case 0: 
      m00 = v[0];
      m01 = v[1];
      m02 = v[2];
      break;
    
    case 1: 
      m10 = v[0];
      m11 = v[1];
      m12 = v[2];
      break;
    
    case 2: 
      m20 = v[0];
      m21 = v[1];
      m22 = v[2];
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
    }
    
  }
  






  public final void setColumn(int column, float x, float y, float z)
  {
    switch (column) {
    case 0: 
      m00 = x;
      m10 = y;
      m20 = z;
      break;
    
    case 1: 
      m01 = x;
      m11 = y;
      m21 = z;
      break;
    
    case 2: 
      m02 = x;
      m12 = y;
      m22 = z;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
    }
    
  }
  




  public final void setColumn(int column, Vector3f v)
  {
    switch (column) {
    case 0: 
      m00 = x;
      m10 = y;
      m20 = z;
      break;
    
    case 1: 
      m01 = x;
      m11 = y;
      m21 = z;
      break;
    
    case 2: 
      m02 = x;
      m12 = y;
      m22 = z;
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
    }
    
  }
  




  public final void setColumn(int column, float[] v)
  {
    switch (column) {
    case 0: 
      m00 = v[0];
      m10 = v[1];
      m20 = v[2];
      break;
    
    case 1: 
      m01 = v[0];
      m11 = v[1];
      m21 = v[2];
      break;
    
    case 2: 
      m02 = v[0];
      m12 = v[1];
      m22 = v[2];
      break;
    
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
    }
    
  }
  







  public final float getScale()
  {
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    getScaleRotate(tmp_scale, tmp_rot);
    
    return (float)Matrix3d.max3(tmp_scale);
  }
  





  public final void add(float scalar)
  {
    m00 += scalar;
    m01 += scalar;
    m02 += scalar;
    m10 += scalar;
    m11 += scalar;
    m12 += scalar;
    m20 += scalar;
    m21 += scalar;
    m22 += scalar;
  }
  






  public final void add(float scalar, Matrix3f m1)
  {
    m00 += scalar;
    m01 += scalar;
    m02 += scalar;
    m10 += scalar;
    m11 += scalar;
    m12 += scalar;
    m20 += scalar;
    m21 += scalar;
    m22 += scalar;
  }
  





  public final void add(Matrix3f m1, Matrix3f m2)
  {
    m00 += m00;
    m01 += m01;
    m02 += m02;
    
    m10 += m10;
    m11 += m11;
    m12 += m12;
    
    m20 += m20;
    m21 += m21;
    m22 += m22;
  }
  





  public final void add(Matrix3f m1)
  {
    m00 += m00;
    m01 += m01;
    m02 += m02;
    
    m10 += m10;
    m11 += m11;
    m12 += m12;
    
    m20 += m20;
    m21 += m21;
    m22 += m22;
  }
  






  public final void sub(Matrix3f m1, Matrix3f m2)
  {
    m00 -= m00;
    m01 -= m01;
    m02 -= m02;
    
    m10 -= m10;
    m11 -= m11;
    m12 -= m12;
    
    m20 -= m20;
    m21 -= m21;
    m22 -= m22;
  }
  





  public final void sub(Matrix3f m1)
  {
    m00 -= m00;
    m01 -= m01;
    m02 -= m02;
    
    m10 -= m10;
    m11 -= m11;
    m12 -= m12;
    
    m20 -= m20;
    m21 -= m21;
    m22 -= m22;
  }
  





  public final void transpose()
  {
    float temp = m10;
    m10 = m01;
    m01 = temp;
    
    temp = m20;
    m20 = m02;
    m02 = temp;
    
    temp = m21;
    m21 = m12;
    m12 = temp;
  }
  




  public final void transpose(Matrix3f m1)
  {
    if (this != m1) {
      m00 = m00;
      m01 = m10;
      m02 = m20;
      
      m10 = m01;
      m11 = m11;
      m12 = m21;
      
      m20 = m02;
      m21 = m12;
      m22 = m22;
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
      
      double sinTheta = Math.sin(angle);
      double cosTheta = Math.cos(angle);
      double t = 1.0D - cosTheta;
      
      double xz = ax * az;
      double xy = ax * ay;
      double yz = ay * az;
      
      m00 = ((float)(t * ax * ax + cosTheta));
      m01 = ((float)(t * xy - sinTheta * az));
      m02 = ((float)(t * xz + sinTheta * ay));
      
      m10 = ((float)(t * xy + sinTheta * az));
      m11 = ((float)(t * ay * ay + cosTheta));
      m12 = ((float)(t * yz - sinTheta * ax));
      
      m20 = ((float)(t * xz - sinTheta * ay));
      m21 = ((float)(t * yz + sinTheta * ax));
      m22 = ((float)(t * az * az + cosTheta));
    }
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
  }
  






  public final void set(float[] m)
  {
    m00 = m[0];
    m01 = m[1];
    m02 = m[2];
    
    m10 = m[3];
    m11 = m[4];
    m12 = m[5];
    
    m20 = m[6];
    m21 = m[7];
    m22 = m[8];
  }
  







  public final void set(Matrix3f m1)
  {
    m00 = m00;
    m01 = m01;
    m02 = m02;
    
    m10 = m10;
    m11 = m11;
    m12 = m12;
    
    m20 = m20;
    m21 = m21;
    m22 = m22;
  }
  







  public final void set(Matrix3d m1)
  {
    m00 = ((float)m00);
    m01 = ((float)m01);
    m02 = ((float)m02);
    
    m10 = ((float)m10);
    m11 = ((float)m11);
    m12 = ((float)m12);
    
    m20 = ((float)m20);
    m21 = ((float)m21);
    m22 = ((float)m22);
  }
  







  public final void invert(Matrix3f m1)
  {
    invertGeneral(m1);
  }
  



  public final void invert()
  {
    invertGeneral(this);
  }
  







  private final void invertGeneral(Matrix3f m1)
  {
    double[] temp = new double[9];
    double[] result = new double[9];
    int[] row_perm = new int[3];
    





    temp[0] = m00;
    temp[1] = m01;
    temp[2] = m02;
    
    temp[3] = m10;
    temp[4] = m11;
    temp[5] = m12;
    
    temp[6] = m20;
    temp[7] = m21;
    temp[8] = m22;
    


    if (!luDecomposition(temp, row_perm))
    {
      throw new SingularMatrixException(VecMathI18N.getString("Matrix3f12"));
    }
    

    for (int i = 0; i < 9; i++) result[i] = 0.0D;
    result[0] = 1.0D;result[4] = 1.0D;result[8] = 1.0D;
    luBacksubstitution(temp, row_perm, result);
    
    m00 = ((float)result[0]);
    m01 = ((float)result[1]);
    m02 = ((float)result[2]);
    
    m10 = ((float)result[3]);
    m11 = ((float)result[4]);
    m12 = ((float)result[5]);
    
    m20 = ((float)result[6]);
    m21 = ((float)result[7]);
    m22 = ((float)result[8]);
  }
  






















  static boolean luDecomposition(double[] matrix0, int[] row_perm)
  {
    double[] row_scale = new double[3];
    






    int ptr = 0;
    int rs = 0;
    

    int i = 3;
    while (i-- != 0) {
      double big = 0.0D;
      

      int j = 3;
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
    

    for (int j = 0; j < 3; j++)
    {




      for (int i = 0; i < j; i++) {
        int target = mtx + 3 * i + j;
        double sum = matrix0[target];
        int k = i;
        int p1 = mtx + 3 * i;
        int p2 = mtx + j;
        while (k-- != 0) {
          sum -= matrix0[p1] * matrix0[p2];
          p1++;
          p2 += 3;
        }
        matrix0[target] = sum;
      }
      


      double big = 0.0D;
      int imax = -1;
      for (i = j; i < 3; i++) {
        int target = mtx + 3 * i + j;
        double sum = matrix0[target];
        int k = j;
        int p1 = mtx + 3 * i;
        int p2 = mtx + j;
        while (k-- != 0) {
          sum -= matrix0[p1] * matrix0[p2];
          p1++;
          p2 += 3;
        }
        matrix0[target] = sum;
        
        double temp;
        if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
          big = temp;
          imax = i;
        }
      }
      
      if (imax < 0) {
        throw new RuntimeException(VecMathI18N.getString("Matrix3f13"));
      }
      

      if (j != imax)
      {
        int k = 3;
        int p1 = mtx + 3 * imax;
        int p2 = mtx + 3 * j;
        while (k-- != 0) {
          double temp = matrix0[p1];
          matrix0[(p1++)] = matrix0[p2];
          matrix0[(p2++)] = temp;
        }
        

        row_scale[imax] = row_scale[j];
      }
      

      row_perm[j] = imax;
      

      if (matrix0[(mtx + 3 * j + j)] == 0.0D) {
        return false;
      }
      

      if (j != 2) {
        double temp = 1.0D / matrix0[(mtx + 3 * j + j)];
        int target = mtx + 3 * (j + 1) + j;
        i = 2 - j;
        while (i-- != 0) {
          matrix0[target] *= temp;
          target += 3;
        }
      }
    }
    

    return true;
  }
  

























  static void luBacksubstitution(double[] matrix1, int[] row_perm, double[] matrix2)
  {
    int rp = 0;
    

    for (int k = 0; k < 3; k++)
    {
      int cv = k;
      int ii = -1;
      

      for (int i = 0; i < 3; i++)
      {

        int ip = row_perm[(rp + i)];
        double sum = matrix2[(cv + 3 * ip)];
        matrix2[(cv + 3 * ip)] = matrix2[(cv + 3 * i)];
        if (ii >= 0)
        {
          int rv = i * 3;
          for (int j = ii; j <= i - 1; j++) {
            sum -= matrix1[(rv + j)] * matrix2[(cv + 3 * j)];
          }
        }
        if (sum != 0.0D) {
          ii = i;
        }
        matrix2[(cv + 3 * i)] = sum;
      }
      


      int rv = 6;
      matrix2[(cv + 6)] /= matrix1[(rv + 2)];
      
      rv -= 3;
      matrix2[(cv + 3)] = ((matrix2[(cv + 3)] - matrix1[(rv + 2)] * matrix2[(cv + 6)]) / matrix1[(rv + 1)]);
      

      rv -= 3;
      matrix2[(cv + 0)] = ((matrix2[(cv + 0)] - matrix1[(rv + 1)] * matrix2[(cv + 3)] - matrix1[(rv + 2)] * matrix2[(cv + 6)]) / matrix1[(rv + 0)]);
    }
  }
  







  public final float determinant()
  {
    float total = m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20);
    

    return total;
  }
  





  public final void set(float scale)
  {
    m00 = scale;
    m01 = 0.0F;
    m02 = 0.0F;
    
    m10 = 0.0F;
    m11 = scale;
    m12 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = scale;
  }
  







  public final void rotX(float angle)
  {
    float sinAngle = (float)Math.sin(angle);
    float cosAngle = (float)Math.cos(angle);
    
    m00 = 1.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    
    m10 = 0.0F;
    m11 = cosAngle;
    m12 = (-sinAngle);
    
    m20 = 0.0F;
    m21 = sinAngle;
    m22 = cosAngle;
  }
  







  public final void rotY(float angle)
  {
    float sinAngle = (float)Math.sin(angle);
    float cosAngle = (float)Math.cos(angle);
    
    m00 = cosAngle;
    m01 = 0.0F;
    m02 = sinAngle;
    
    m10 = 0.0F;
    m11 = 1.0F;
    m12 = 0.0F;
    
    m20 = (-sinAngle);
    m21 = 0.0F;
    m22 = cosAngle;
  }
  







  public final void rotZ(float angle)
  {
    float sinAngle = (float)Math.sin(angle);
    float cosAngle = (float)Math.cos(angle);
    
    m00 = cosAngle;
    m01 = (-sinAngle);
    m02 = 0.0F;
    
    m10 = sinAngle;
    m11 = cosAngle;
    m12 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 1.0F;
  }
  




  public final void mul(float scalar)
  {
    m00 *= scalar;
    m01 *= scalar;
    m02 *= scalar;
    
    m10 *= scalar;
    m11 *= scalar;
    m12 *= scalar;
    
    m20 *= scalar;
    m21 *= scalar;
    m22 *= scalar;
  }
  






  public final void mul(float scalar, Matrix3f m1)
  {
    m00 = (scalar * m00);
    m01 = (scalar * m01);
    m02 = (scalar * m02);
    
    m10 = (scalar * m10);
    m11 = (scalar * m11);
    m12 = (scalar * m12);
    
    m20 = (scalar * m20);
    m21 = (scalar * m21);
    m22 = (scalar * m22);
  }
  










  public final void mul(Matrix3f m1)
  {
    float m00 = this.m00 * m00 + this.m01 * m10 + this.m02 * m20;
    float m01 = this.m00 * m01 + this.m01 * m11 + this.m02 * m21;
    float m02 = this.m00 * m02 + this.m01 * m12 + this.m02 * m22;
    
    float m10 = this.m10 * m00 + this.m11 * m10 + this.m12 * m20;
    float m11 = this.m10 * m01 + this.m11 * m11 + this.m12 * m21;
    float m12 = this.m10 * m02 + this.m11 * m12 + this.m12 * m22;
    
    float m20 = this.m20 * m00 + this.m21 * m10 + this.m22 * m20;
    float m21 = this.m20 * m01 + this.m21 * m11 + this.m22 * m21;
    float m22 = this.m20 * m02 + this.m21 * m12 + this.m22 * m22;
    
    this.m00 = m00;this.m01 = m01;this.m02 = m02;
    this.m10 = m10;this.m11 = m11;this.m12 = m12;
    this.m20 = m20;this.m21 = m21;this.m22 = m22;
  }
  






  public final void mul(Matrix3f m1, Matrix3f m2)
  {
    if ((this != m1) && (this != m2)) {
      this.m00 = (m00 * m00 + m01 * m10 + m02 * m20);
      this.m01 = (m00 * m01 + m01 * m11 + m02 * m21);
      this.m02 = (m00 * m02 + m01 * m12 + m02 * m22);
      
      this.m10 = (m10 * m00 + m11 * m10 + m12 * m20);
      this.m11 = (m10 * m01 + m11 * m11 + m12 * m21);
      this.m12 = (m10 * m02 + m11 * m12 + m12 * m22);
      
      this.m20 = (m20 * m00 + m21 * m10 + m22 * m20);
      this.m21 = (m20 * m01 + m21 * m11 + m22 * m21);
      this.m22 = (m20 * m02 + m21 * m12 + m22 * m22);

    }
    else
    {

      float m00 = m00 * m00 + m01 * m10 + m02 * m20;
      float m01 = m00 * m01 + m01 * m11 + m02 * m21;
      float m02 = m00 * m02 + m01 * m12 + m02 * m22;
      
      float m10 = m10 * m00 + m11 * m10 + m12 * m20;
      float m11 = m10 * m01 + m11 * m11 + m12 * m21;
      float m12 = m10 * m02 + m11 * m12 + m12 * m22;
      
      float m20 = m20 * m00 + m21 * m10 + m22 * m20;
      float m21 = m20 * m01 + m21 * m11 + m22 * m21;
      float m22 = m20 * m02 + m21 * m12 + m22 * m22;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
  }
  






  public final void mulNormalize(Matrix3f m1)
  {
    double[] tmp = new double[9];
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    tmp[0] = (m00 * m00 + m01 * m10 + m02 * m20);
    tmp[1] = (m00 * m01 + m01 * m11 + m02 * m21);
    tmp[2] = (m00 * m02 + m01 * m12 + m02 * m22);
    
    tmp[3] = (m10 * m00 + m11 * m10 + m12 * m20);
    tmp[4] = (m10 * m01 + m11 * m11 + m12 * m21);
    tmp[5] = (m10 * m02 + m11 * m12 + m12 * m22);
    
    tmp[6] = (m20 * m00 + m21 * m10 + m22 * m20);
    tmp[7] = (m20 * m01 + m21 * m11 + m22 * m21);
    tmp[8] = (m20 * m02 + m21 * m12 + m22 * m22);
    
    Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
    
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
  








  public final void mulNormalize(Matrix3f m1, Matrix3f m2)
  {
    double[] tmp = new double[9];
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    

    tmp[0] = (m00 * m00 + m01 * m10 + m02 * m20);
    tmp[1] = (m00 * m01 + m01 * m11 + m02 * m21);
    tmp[2] = (m00 * m02 + m01 * m12 + m02 * m22);
    
    tmp[3] = (m10 * m00 + m11 * m10 + m12 * m20);
    tmp[4] = (m10 * m01 + m11 * m11 + m12 * m21);
    tmp[5] = (m10 * m02 + m11 * m12 + m12 * m22);
    
    tmp[6] = (m20 * m00 + m21 * m10 + m22 * m20);
    tmp[7] = (m20 * m01 + m21 * m11 + m22 * m21);
    tmp[8] = (m20 * m02 + m21 * m12 + m22 * m22);
    
    Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
    
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
  






  public final void mulTransposeBoth(Matrix3f m1, Matrix3f m2)
  {
    if ((this != m1) && (this != m2)) {
      this.m00 = (m00 * m00 + m10 * m01 + m20 * m02);
      this.m01 = (m00 * m10 + m10 * m11 + m20 * m12);
      this.m02 = (m00 * m20 + m10 * m21 + m20 * m22);
      
      this.m10 = (m01 * m00 + m11 * m01 + m21 * m02);
      this.m11 = (m01 * m10 + m11 * m11 + m21 * m12);
      this.m12 = (m01 * m20 + m11 * m21 + m21 * m22);
      
      this.m20 = (m02 * m00 + m12 * m01 + m22 * m02);
      this.m21 = (m02 * m10 + m12 * m11 + m22 * m12);
      this.m22 = (m02 * m20 + m12 * m21 + m22 * m22);

    }
    else
    {

      float m00 = m00 * m00 + m10 * m01 + m20 * m02;
      float m01 = m00 * m10 + m10 * m11 + m20 * m12;
      float m02 = m00 * m20 + m10 * m21 + m20 * m22;
      
      float m10 = m01 * m00 + m11 * m01 + m21 * m02;
      float m11 = m01 * m10 + m11 * m11 + m21 * m12;
      float m12 = m01 * m20 + m11 * m21 + m21 * m22;
      
      float m20 = m02 * m00 + m12 * m01 + m22 * m02;
      float m21 = m02 * m10 + m12 * m11 + m22 * m12;
      float m22 = m02 * m20 + m12 * m21 + m22 * m22;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
  }
  








  public final void mulTransposeRight(Matrix3f m1, Matrix3f m2)
  {
    if ((this != m1) && (this != m2)) {
      this.m00 = (m00 * m00 + m01 * m01 + m02 * m02);
      this.m01 = (m00 * m10 + m01 * m11 + m02 * m12);
      this.m02 = (m00 * m20 + m01 * m21 + m02 * m22);
      
      this.m10 = (m10 * m00 + m11 * m01 + m12 * m02);
      this.m11 = (m10 * m10 + m11 * m11 + m12 * m12);
      this.m12 = (m10 * m20 + m11 * m21 + m12 * m22);
      
      this.m20 = (m20 * m00 + m21 * m01 + m22 * m02);
      this.m21 = (m20 * m10 + m21 * m11 + m22 * m12);
      this.m22 = (m20 * m20 + m21 * m21 + m22 * m22);

    }
    else
    {

      float m00 = m00 * m00 + m01 * m01 + m02 * m02;
      float m01 = m00 * m10 + m01 * m11 + m02 * m12;
      float m02 = m00 * m20 + m01 * m21 + m02 * m22;
      
      float m10 = m10 * m00 + m11 * m01 + m12 * m02;
      float m11 = m10 * m10 + m11 * m11 + m12 * m12;
      float m12 = m10 * m20 + m11 * m21 + m12 * m22;
      
      float m20 = m20 * m00 + m21 * m01 + m22 * m02;
      float m21 = m20 * m10 + m21 * m11 + m22 * m12;
      float m22 = m20 * m20 + m21 * m21 + m22 * m22;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
  }
  






  public final void mulTransposeLeft(Matrix3f m1, Matrix3f m2)
  {
    if ((this != m1) && (this != m2)) {
      this.m00 = (m00 * m00 + m10 * m10 + m20 * m20);
      this.m01 = (m00 * m01 + m10 * m11 + m20 * m21);
      this.m02 = (m00 * m02 + m10 * m12 + m20 * m22);
      
      this.m10 = (m01 * m00 + m11 * m10 + m21 * m20);
      this.m11 = (m01 * m01 + m11 * m11 + m21 * m21);
      this.m12 = (m01 * m02 + m11 * m12 + m21 * m22);
      
      this.m20 = (m02 * m00 + m12 * m10 + m22 * m20);
      this.m21 = (m02 * m01 + m12 * m11 + m22 * m21);
      this.m22 = (m02 * m02 + m12 * m12 + m22 * m22);

    }
    else
    {

      float m00 = m00 * m00 + m10 * m10 + m20 * m20;
      float m01 = m00 * m01 + m10 * m11 + m20 * m21;
      float m02 = m00 * m02 + m10 * m12 + m20 * m22;
      
      float m10 = m01 * m00 + m11 * m10 + m21 * m20;
      float m11 = m01 * m01 + m11 * m11 + m21 * m21;
      float m12 = m01 * m02 + m11 * m12 + m21 * m22;
      
      float m20 = m02 * m00 + m12 * m10 + m22 * m20;
      float m21 = m02 * m01 + m12 * m11 + m22 * m21;
      float m22 = m02 * m02 + m12 * m12 + m22 * m22;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
  }
  



  public final void normalize()
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
  





  public final void normalize(Matrix3f m1)
  {
    double[] tmp = new double[9];
    double[] tmp_rot = new double[9];
    double[] tmp_scale = new double[3];
    
    tmp[0] = m00;
    tmp[1] = m01;
    tmp[2] = m02;
    
    tmp[3] = m10;
    tmp[4] = m11;
    tmp[5] = m12;
    
    tmp[6] = m20;
    tmp[7] = m21;
    tmp[8] = m22;
    
    Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
    
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
  




  public final void normalizeCP()
  {
    float mag = 1.0F / (float)Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
    m00 *= mag;
    m10 *= mag;
    m20 *= mag;
    
    mag = 1.0F / (float)Math.sqrt(m01 * m01 + m11 * m11 + m21 * m21);
    m01 *= mag;
    m11 *= mag;
    m21 *= mag;
    
    m02 = (m10 * m21 - m11 * m20);
    m12 = (m01 * m20 - m00 * m21);
    m22 = (m00 * m11 - m01 * m10);
  }
  






  public final void normalizeCP(Matrix3f m1)
  {
    float mag = 1.0F / (float)Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
    m00 *= mag;
    m10 *= mag;
    m20 *= mag;
    
    mag = 1.0F / (float)Math.sqrt(m01 * m01 + m11 * m11 + m21 * m21);
    m01 *= mag;
    m11 *= mag;
    m21 *= mag;
    
    m02 = (m10 * m21 - m11 * m20);
    m12 = (m01 * m20 - m00 * m21);
    m22 = (m00 * m11 - m01 * m10);
  }
  







  public boolean equals(Matrix3f m1)
  {
    try
    {
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m10 == m10) && (m11 == m11) && (m12 == m12) && (m20 == m20) && (m21 == m21) && (m22 == m22);
    }
    catch (NullPointerException e2) {}
    
    return false;
  }
  








  public boolean equals(Object o1)
  {
    try
    {
      Matrix3f m2 = (Matrix3f)o1;
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m10 == m10) && (m11 == m11) && (m12 == m12) && (m20 == m20) && (m21 == m21) && (m22 == m22);
    }
    catch (ClassCastException e1)
    {
      return false; } catch (NullPointerException e2) {}
    return false;
  }
  









  public boolean epsilonEquals(Matrix3f m1, float epsilon)
  {
    boolean status = true;
    
    if (Math.abs(m00 - m00) > epsilon) status = false;
    if (Math.abs(m01 - m01) > epsilon) status = false;
    if (Math.abs(m02 - m02) > epsilon) { status = false;
    }
    if (Math.abs(m10 - m10) > epsilon) status = false;
    if (Math.abs(m11 - m11) > epsilon) status = false;
    if (Math.abs(m12 - m12) > epsilon) { status = false;
    }
    if (Math.abs(m20 - m20) > epsilon) status = false;
    if (Math.abs(m21 - m21) > epsilon) status = false;
    if (Math.abs(m22 - m22) > epsilon) { status = false;
    }
    return status;
  }
  









  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.floatToIntBits(m00);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m01);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m02);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m10);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m11);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m12);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m20);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m21);
    bits = 31L * bits + VecMathUtil.floatToIntBits(m22);
    return (int)(bits ^ bits >> 32);
  }
  




  public final void setZero()
  {
    m00 = 0.0F;
    m01 = 0.0F;
    m02 = 0.0F;
    
    m10 = 0.0F;
    m11 = 0.0F;
    m12 = 0.0F;
    
    m20 = 0.0F;
    m21 = 0.0F;
    m22 = 0.0F;
  }
  




  public final void negate()
  {
    m00 = (-m00);
    m01 = (-m01);
    m02 = (-m02);
    
    m10 = (-m10);
    m11 = (-m11);
    m12 = (-m12);
    
    m20 = (-m20);
    m21 = (-m21);
    m22 = (-m22);
  }
  






  public final void negate(Matrix3f m1)
  {
    m00 = (-m00);
    m01 = (-m01);
    m02 = (-m02);
    
    m10 = (-m10);
    m11 = (-m11);
    m12 = (-m12);
    
    m20 = (-m20);
    m21 = (-m21);
    m22 = (-m22);
  }
  






  public final void transform(Tuple3f t)
  {
    float x = m00 * x + m01 * y + m02 * z;
    float y = m10 * x + m11 * y + m12 * z;
    float z = m20 * x + m21 * y + m22 * z;
    t.set(x, y, z);
  }
  






  public final void transform(Tuple3f t, Tuple3f result)
  {
    float x = m00 * x + m01 * y + m02 * z;
    float y = m10 * x + m11 * y + m12 * z;
    z = (m20 * x + m21 * y + m22 * z);
    x = x;
    y = y;
  }
  



  void getScaleRotate(double[] scales, double[] rot)
  {
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
    Matrix3d.compute_svd(tmp, scales, rot);
  }
  










  public Object clone()
  {
    Matrix3f m1 = null;
    try {
      m1 = (Matrix3f)super.clone();
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
}
