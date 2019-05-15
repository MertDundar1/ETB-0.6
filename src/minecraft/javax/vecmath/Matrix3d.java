package javax.vecmath;

import java.io.PrintStream;
import java.io.Serializable;





















































































public class Matrix3d
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 6837536777072402710L;
  public double m00;
  public double m01;
  public double m02;
  public double m10;
  public double m11;
  public double m12;
  public double m20;
  public double m21;
  public double m22;
  private static final double EPS = 1.110223024E-16D;
  private static final double ERR_EPS = 1.0E-8D;
  private static double xin;
  private static double yin;
  private static double zin;
  private static double xout;
  private static double yout;
  private static double zout;
  
  public Matrix3d(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22)
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
  






  public Matrix3d(double[] v)
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
  






  public Matrix3d(Matrix3d m1)
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
  






  public Matrix3d(Matrix3f m1)
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
  




  public Matrix3d()
  {
    m00 = 0.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    
    m10 = 0.0D;
    m11 = 0.0D;
    m12 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 0.0D;
  }
  




  public String toString()
  {
    return m00 + ", " + m01 + ", " + m02 + "\n" + m10 + ", " + m11 + ", " + m12 + "\n" + m20 + ", " + m21 + ", " + m22 + "\n";
  }
  






  public final void setIdentity()
  {
    m00 = 1.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    
    m10 = 0.0D;
    m11 = 1.0D;
    m12 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
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
      default: 
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
      }
      
      break;
    default: 
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
    
    




    throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
  }
  




  public final void getRow(int row, Vector3d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
    }
  }
  





  public final void getRow(int row, double[] v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
    }
  }
  






  public final void getColumn(int column, Vector3d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
    }
  }
  






  public final void getColumn(int column, double[] v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
    }
  }
  









  public final void setRow(int row, double x, double y, double z)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
    }
    
  }
  




  public final void setRow(int row, Vector3d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
    }
    
  }
  




  public final void setRow(int row, double[] v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
    }
    
  }
  






  public final void setColumn(int column, double x, double y, double z)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
    }
    
  }
  




  public final void setColumn(int column, Vector3d v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
    }
    
  }
  




  public final void setColumn(int column, double[] v)
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
      throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
    }
    
  }
  







  public final double getScale()
  {
    double[] tmp_scale = new double[3];
    double[] tmp_rot = new double[9];
    getScaleRotate(tmp_scale, tmp_rot);
    
    return max3(tmp_scale);
  }
  





  public final void add(double scalar)
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
  







  public final void add(double scalar, Matrix3d m1)
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
  





  public final void add(Matrix3d m1, Matrix3d m2)
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
  




  public final void add(Matrix3d m1)
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
  






  public final void sub(Matrix3d m1, Matrix3d m2)
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
  





  public final void sub(Matrix3d m1)
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
    double temp = m10;
    m10 = m01;
    m01 = temp;
    
    temp = m20;
    m20 = m02;
    m02 = temp;
    
    temp = m21;
    m21 = m12;
    m12 = temp;
  }
  




  public final void transpose(Matrix3d m1)
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
  }
  





  public final void set(AxisAngle4d a1)
  {
    double mag = Math.sqrt(x * x + y * y + z * z);
    
    if (mag < 1.110223024E-16D) {
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
  }
  





  public final void set(AxisAngle4f a1)
  {
    double mag = Math.sqrt(x * x + y * y + z * z);
    if (mag < 1.110223024E-16D) {
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
  






  public final void set(double[] m)
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
  






  public final void invert(Matrix3d m1)
  {
    invertGeneral(m1);
  }
  



  public final void invert()
  {
    invertGeneral(this);
  }
  







  private final void invertGeneral(Matrix3d m1)
  {
    double[] result = new double[9];
    int[] row_perm = new int[3];
    
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
    


    if (!luDecomposition(tmp, row_perm))
    {
      throw new SingularMatrixException(VecMathI18N.getString("Matrix3d12"));
    }
    

    for (int i = 0; i < 9; i++) result[i] = 0.0D;
    result[0] = 1.0D;result[4] = 1.0D;result[8] = 1.0D;
    luBacksubstitution(tmp, row_perm, result);
    
    m00 = result[0];
    m01 = result[1];
    m02 = result[2];
    
    m10 = result[3];
    m11 = result[4];
    m12 = result[5];
    
    m20 = result[6];
    m21 = result[7];
    m22 = result[8];
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
        throw new RuntimeException(VecMathI18N.getString("Matrix3d13"));
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
  









  public final double determinant()
  {
    double total = m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20);
    

    return total;
  }
  





  public final void set(double scale)
  {
    m00 = scale;
    m01 = 0.0D;
    m02 = 0.0D;
    
    m10 = 0.0D;
    m11 = scale;
    m12 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = scale;
  }
  







  public final void rotX(double angle)
  {
    double sinAngle = Math.sin(angle);
    double cosAngle = Math.cos(angle);
    
    m00 = 1.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    
    m10 = 0.0D;
    m11 = cosAngle;
    m12 = (-sinAngle);
    
    m20 = 0.0D;
    m21 = sinAngle;
    m22 = cosAngle;
  }
  







  public final void rotY(double angle)
  {
    double sinAngle = Math.sin(angle);
    double cosAngle = Math.cos(angle);
    
    m00 = cosAngle;
    m01 = 0.0D;
    m02 = sinAngle;
    
    m10 = 0.0D;
    m11 = 1.0D;
    m12 = 0.0D;
    
    m20 = (-sinAngle);
    m21 = 0.0D;
    m22 = cosAngle;
  }
  







  public final void rotZ(double angle)
  {
    double sinAngle = Math.sin(angle);
    double cosAngle = Math.cos(angle);
    
    m00 = cosAngle;
    m01 = (-sinAngle);
    m02 = 0.0D;
    
    m10 = sinAngle;
    m11 = cosAngle;
    m12 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 1.0D;
  }
  




  public final void mul(double scalar)
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
  







  public final void mul(double scalar, Matrix3d m1)
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
  










  public final void mul(Matrix3d m1)
  {
    double m00 = this.m00 * m00 + this.m01 * m10 + this.m02 * m20;
    double m01 = this.m00 * m01 + this.m01 * m11 + this.m02 * m21;
    double m02 = this.m00 * m02 + this.m01 * m12 + this.m02 * m22;
    
    double m10 = this.m10 * m00 + this.m11 * m10 + this.m12 * m20;
    double m11 = this.m10 * m01 + this.m11 * m11 + this.m12 * m21;
    double m12 = this.m10 * m02 + this.m11 * m12 + this.m12 * m22;
    
    double m20 = this.m20 * m00 + this.m21 * m10 + this.m22 * m20;
    double m21 = this.m20 * m01 + this.m21 * m11 + this.m22 * m21;
    double m22 = this.m20 * m02 + this.m21 * m12 + this.m22 * m22;
    
    this.m00 = m00;this.m01 = m01;this.m02 = m02;
    this.m10 = m10;this.m11 = m11;this.m12 = m12;
    this.m20 = m20;this.m21 = m21;this.m22 = m22;
  }
  






  public final void mul(Matrix3d m1, Matrix3d m2)
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

      double m00 = m00 * m00 + m01 * m10 + m02 * m20;
      double m01 = m00 * m01 + m01 * m11 + m02 * m21;
      double m02 = m00 * m02 + m01 * m12 + m02 * m22;
      
      double m10 = m10 * m00 + m11 * m10 + m12 * m20;
      double m11 = m10 * m01 + m11 * m11 + m12 * m21;
      double m12 = m10 * m02 + m11 * m12 + m12 * m22;
      
      double m20 = m20 * m00 + m21 * m10 + m22 * m20;
      double m21 = m20 * m01 + m21 * m11 + m22 * m21;
      double m22 = m20 * m02 + m21 * m12 + m22 * m22;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
  }
  






  public final void mulNormalize(Matrix3d m1)
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
    
    compute_svd(tmp, tmp_scale, tmp_rot);
    
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
  









  public final void mulNormalize(Matrix3d m1, Matrix3d m2)
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
    
    compute_svd(tmp, tmp_scale, tmp_rot);
    
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
  







  public final void mulTransposeBoth(Matrix3d m1, Matrix3d m2)
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

      double m00 = m00 * m00 + m10 * m01 + m20 * m02;
      double m01 = m00 * m10 + m10 * m11 + m20 * m12;
      double m02 = m00 * m20 + m10 * m21 + m20 * m22;
      
      double m10 = m01 * m00 + m11 * m01 + m21 * m02;
      double m11 = m01 * m10 + m11 * m11 + m21 * m12;
      double m12 = m01 * m20 + m11 * m21 + m21 * m22;
      
      double m20 = m02 * m00 + m12 * m01 + m22 * m02;
      double m21 = m02 * m10 + m12 * m11 + m22 * m12;
      double m22 = m02 * m20 + m12 * m21 + m22 * m22;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
  }
  







  public final void mulTransposeRight(Matrix3d m1, Matrix3d m2)
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

      double m00 = m00 * m00 + m01 * m01 + m02 * m02;
      double m01 = m00 * m10 + m01 * m11 + m02 * m12;
      double m02 = m00 * m20 + m01 * m21 + m02 * m22;
      
      double m10 = m10 * m00 + m11 * m01 + m12 * m02;
      double m11 = m10 * m10 + m11 * m11 + m12 * m12;
      double m12 = m10 * m20 + m11 * m21 + m12 * m22;
      
      double m20 = m20 * m00 + m21 * m01 + m22 * m02;
      double m21 = m20 * m10 + m21 * m11 + m22 * m12;
      double m22 = m20 * m20 + m21 * m21 + m22 * m22;
      
      this.m00 = m00;this.m01 = m01;this.m02 = m02;
      this.m10 = m10;this.m11 = m11;this.m12 = m12;
      this.m20 = m20;this.m21 = m21;this.m22 = m22;
    }
  }
  






  public final void mulTransposeLeft(Matrix3d m1, Matrix3d m2)
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

      double m00 = m00 * m00 + m10 * m10 + m20 * m20;
      double m01 = m00 * m01 + m10 * m11 + m20 * m21;
      double m02 = m00 * m02 + m10 * m12 + m20 * m22;
      
      double m10 = m01 * m00 + m11 * m10 + m21 * m20;
      double m11 = m01 * m01 + m11 * m11 + m21 * m21;
      double m12 = m01 * m02 + m11 * m12 + m21 * m22;
      
      double m20 = m02 * m00 + m12 * m10 + m22 * m20;
      double m21 = m02 * m01 + m12 * m11 + m22 * m21;
      double m22 = m02 * m02 + m12 * m12 + m22 * m22;
      
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
  







  public final void normalize(Matrix3d m1)
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
    
    compute_svd(tmp, tmp_scale, tmp_rot);
    
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
  





  public final void normalizeCP()
  {
    double mag = 1.0D / Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
    m00 *= mag;
    m10 *= mag;
    m20 *= mag;
    
    mag = 1.0D / Math.sqrt(m01 * m01 + m11 * m11 + m21 * m21);
    m01 *= mag;
    m11 *= mag;
    m21 *= mag;
    
    m02 = (m10 * m21 - m11 * m20);
    m12 = (m01 * m20 - m00 * m21);
    m22 = (m00 * m11 - m01 * m10);
  }
  






  public final void normalizeCP(Matrix3d m1)
  {
    double mag = 1.0D / Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
    m00 *= mag;
    m10 *= mag;
    m20 *= mag;
    
    mag = 1.0D / Math.sqrt(m01 * m01 + m11 * m11 + m21 * m21);
    m01 *= mag;
    m11 *= mag;
    m21 *= mag;
    
    m02 = (m10 * m21 - m11 * m20);
    m12 = (m01 * m20 - m00 * m21);
    m22 = (m00 * m11 - m01 * m10);
  }
  





  public boolean equals(Matrix3d m1)
  {
    try
    {
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m10 == m10) && (m11 == m11) && (m12 == m12) && (m20 == m20) && (m21 == m21) && (m22 == m22);
    }
    catch (NullPointerException e2) {}
    
    return false;
  }
  







  public boolean equals(Object t1)
  {
    try
    {
      Matrix3d m2 = (Matrix3d)t1;
      return (m00 == m00) && (m01 == m01) && (m02 == m02) && (m10 == m10) && (m11 == m11) && (m12 == m12) && (m20 == m20) && (m21 == m21) && (m22 == m22);
    }
    catch (ClassCastException e1)
    {
      return false; } catch (NullPointerException e2) {}
    return false;
  }
  












  public boolean epsilonEquals(Matrix3d m1, double epsilon)
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
    diff = m10 - m10;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m11 - m11;
    if ((diff < 0.0D ? -diff : diff) > epsilon) { return false;
    }
    diff = m12 - m12;
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
    return true;
  }
  








  public int hashCode()
  {
    long bits = 1L;
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m00);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m01);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m02);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m10);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m11);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m12);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m20);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m21);
    bits = 31L * bits + VecMathUtil.doubleToLongBits(m22);
    return (int)(bits ^ bits >> 32);
  }
  




  public final void setZero()
  {
    m00 = 0.0D;
    m01 = 0.0D;
    m02 = 0.0D;
    
    m10 = 0.0D;
    m11 = 0.0D;
    m12 = 0.0D;
    
    m20 = 0.0D;
    m21 = 0.0D;
    m22 = 0.0D;
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
  






  public final void negate(Matrix3d m1)
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
  






  public final void transform(Tuple3d t)
  {
    double x = m00 * x + m01 * y + m02 * z;
    double y = m10 * x + m11 * y + m12 * z;
    double z = m20 * x + m21 * y + m22 * z;
    t.set(x, y, z);
  }
  






  public final void transform(Tuple3d t, Tuple3d result)
  {
    double x = m00 * x + m01 * y + m02 * z;
    double y = m10 * x + m11 * y + m12 * z;
    z = (m20 * x + m21 * y + m22 * z);
    x = x;
    y = y;
  }
  



  final void getScaleRotate(double[] scales, double[] rots)
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
    compute_svd(tmp, scales, rots);
  }
  



  static void compute_svd(double[] m, double[] outScale, double[] outRot)
  {
    double[] u1 = new double[9];
    double[] v1 = new double[9];
    double[] t1 = new double[9];
    double[] t2 = new double[9];
    
    double[] tmp = t1;
    double[] single_values = t2;
    
    double[] rot = new double[9];
    double[] e = new double[3];
    double[] scales = new double[3];
    
    int negCnt = 0;
    





    for (int i = 0; i < 9; i++) {
      rot[i] = m[i];
    }
    

    if (m[3] * m[3] < 1.110223024E-16D) {
      u1[0] = 1.0D;u1[1] = 0.0D;u1[2] = 0.0D;
      u1[3] = 0.0D;u1[4] = 1.0D;u1[5] = 0.0D;
      u1[6] = 0.0D;u1[7] = 0.0D;u1[8] = 1.0D;
    } else if (m[0] * m[0] < 1.110223024E-16D) {
      tmp[0] = m[0];
      tmp[1] = m[1];
      tmp[2] = m[2];
      m[0] = m[3];
      m[1] = m[4];
      m[2] = m[5];
      
      m[3] = (-tmp[0]);
      m[4] = (-tmp[1]);
      m[5] = (-tmp[2]);
      
      u1[0] = 0.0D;u1[1] = 1.0D;u1[2] = 0.0D;
      u1[3] = -1.0D;u1[4] = 0.0D;u1[5] = 0.0D;
      u1[6] = 0.0D;u1[7] = 0.0D;u1[8] = 1.0D;
    } else {
      double g = 1.0D / Math.sqrt(m[0] * m[0] + m[3] * m[3]);
      double c1 = m[0] * g;
      double s1 = m[3] * g;
      tmp[0] = (c1 * m[0] + s1 * m[3]);
      tmp[1] = (c1 * m[1] + s1 * m[4]);
      tmp[2] = (c1 * m[2] + s1 * m[5]);
      
      m[3] = (-s1 * m[0] + c1 * m[3]);
      m[4] = (-s1 * m[1] + c1 * m[4]);
      m[5] = (-s1 * m[2] + c1 * m[5]);
      
      m[0] = tmp[0];
      m[1] = tmp[1];
      m[2] = tmp[2];
      u1[0] = c1;u1[1] = s1;u1[2] = 0.0D;
      u1[3] = (-s1);u1[4] = c1;u1[5] = 0.0D;
      u1[6] = 0.0D;u1[7] = 0.0D;u1[8] = 1.0D;
    }
    


    if (m[6] * m[6] >= 1.110223024E-16D) {
      if (m[0] * m[0] < 1.110223024E-16D) {
        tmp[0] = m[0];
        tmp[1] = m[1];
        tmp[2] = m[2];
        m[0] = m[6];
        m[1] = m[7];
        m[2] = m[8];
        
        m[6] = (-tmp[0]);
        m[7] = (-tmp[1]);
        m[8] = (-tmp[2]);
        
        tmp[0] = u1[0];
        tmp[1] = u1[1];
        tmp[2] = u1[2];
        u1[0] = u1[6];
        u1[1] = u1[7];
        u1[2] = u1[8];
        
        u1[6] = (-tmp[0]);
        u1[7] = (-tmp[1]);
        u1[8] = (-tmp[2]);
      } else {
        double g = 1.0D / Math.sqrt(m[0] * m[0] + m[6] * m[6]);
        double c2 = m[0] * g;
        double s2 = m[6] * g;
        tmp[0] = (c2 * m[0] + s2 * m[6]);
        tmp[1] = (c2 * m[1] + s2 * m[7]);
        tmp[2] = (c2 * m[2] + s2 * m[8]);
        
        m[6] = (-s2 * m[0] + c2 * m[6]);
        m[7] = (-s2 * m[1] + c2 * m[7]);
        m[8] = (-s2 * m[2] + c2 * m[8]);
        m[0] = tmp[0];
        m[1] = tmp[1];
        m[2] = tmp[2];
        
        tmp[0] = (c2 * u1[0]);
        tmp[1] = (c2 * u1[1]);
        u1[2] = s2;
        
        tmp[6] = (-u1[0] * s2);
        tmp[7] = (-u1[1] * s2);
        u1[8] = c2;
        u1[0] = tmp[0];
        u1[1] = tmp[1];
        u1[6] = tmp[6];
        u1[7] = tmp[7];
      }
    }
    

    if (m[2] * m[2] < 1.110223024E-16D) {
      v1[0] = 1.0D;v1[1] = 0.0D;v1[2] = 0.0D;
      v1[3] = 0.0D;v1[4] = 1.0D;v1[5] = 0.0D;
      v1[6] = 0.0D;v1[7] = 0.0D;v1[8] = 1.0D;
    } else if (m[1] * m[1] < 1.110223024E-16D) {
      tmp[2] = m[2];
      tmp[5] = m[5];
      tmp[8] = m[8];
      m[2] = (-m[1]);
      m[5] = (-m[4]);
      m[8] = (-m[7]);
      
      m[1] = tmp[2];
      m[4] = tmp[5];
      m[7] = tmp[8];
      
      v1[0] = 1.0D;v1[1] = 0.0D;v1[2] = 0.0D;
      v1[3] = 0.0D;v1[4] = 0.0D;v1[5] = -1.0D;
      v1[6] = 0.0D;v1[7] = 1.0D;v1[8] = 0.0D;
    } else {
      double g = 1.0D / Math.sqrt(m[1] * m[1] + m[2] * m[2]);
      double c3 = m[1] * g;
      double s3 = m[2] * g;
      tmp[1] = (c3 * m[1] + s3 * m[2]);
      m[2] = (-s3 * m[1] + c3 * m[2]);
      m[1] = tmp[1];
      
      tmp[4] = (c3 * m[4] + s3 * m[5]);
      m[5] = (-s3 * m[4] + c3 * m[5]);
      m[4] = tmp[4];
      
      tmp[7] = (c3 * m[7] + s3 * m[8]);
      m[8] = (-s3 * m[7] + c3 * m[8]);
      m[7] = tmp[7];
      
      v1[0] = 1.0D;v1[1] = 0.0D;v1[2] = 0.0D;
      v1[3] = 0.0D;v1[4] = c3;v1[5] = (-s3);
      v1[6] = 0.0D;v1[7] = s3;v1[8] = c3;
    }
    


    if (m[7] * m[7] >= 1.110223024E-16D) {
      if (m[4] * m[4] < 1.110223024E-16D) {
        tmp[3] = m[3];
        tmp[4] = m[4];
        tmp[5] = m[5];
        m[3] = m[6];
        m[4] = m[7];
        m[5] = m[8];
        
        m[6] = (-tmp[3]);
        m[7] = (-tmp[4]);
        m[8] = (-tmp[5]);
        
        tmp[3] = u1[3];
        tmp[4] = u1[4];
        tmp[5] = u1[5];
        u1[3] = u1[6];
        u1[4] = u1[7];
        u1[5] = u1[8];
        
        u1[6] = (-tmp[3]);
        u1[7] = (-tmp[4]);
        u1[8] = (-tmp[5]);
      }
      else {
        double g = 1.0D / Math.sqrt(m[4] * m[4] + m[7] * m[7]);
        double c4 = m[4] * g;
        double s4 = m[7] * g;
        tmp[3] = (c4 * m[3] + s4 * m[6]);
        m[6] = (-s4 * m[3] + c4 * m[6]);
        m[3] = tmp[3];
        
        tmp[4] = (c4 * m[4] + s4 * m[7]);
        m[7] = (-s4 * m[4] + c4 * m[7]);
        m[4] = tmp[4];
        
        tmp[5] = (c4 * m[5] + s4 * m[8]);
        m[8] = (-s4 * m[5] + c4 * m[8]);
        m[5] = tmp[5];
        
        tmp[3] = (c4 * u1[3] + s4 * u1[6]);
        u1[6] = (-s4 * u1[3] + c4 * u1[6]);
        u1[3] = tmp[3];
        
        tmp[4] = (c4 * u1[4] + s4 * u1[7]);
        u1[7] = (-s4 * u1[4] + c4 * u1[7]);
        u1[4] = tmp[4];
        
        tmp[5] = (c4 * u1[5] + s4 * u1[8]);
        u1[8] = (-s4 * u1[5] + c4 * u1[8]);
        u1[5] = tmp[5];
      }
    }
    single_values[0] = m[0];
    single_values[1] = m[4];
    single_values[2] = m[8];
    e[0] = m[1];
    e[1] = m[5];
    
    if ((e[0] * e[0] >= 1.110223024E-16D) || (e[1] * e[1] >= 1.110223024E-16D))
    {

      compute_qr(single_values, e, u1, v1);
    }
    
    scales[0] = single_values[0];
    scales[1] = single_values[1];
    scales[2] = single_values[2];
    


    if ((almostEqual(Math.abs(scales[0]), 1.0D)) && 
      (almostEqual(Math.abs(scales[1]), 1.0D)) && 
      (almostEqual(Math.abs(scales[2]), 1.0D)))
    {

      for (i = 0; i < 3; i++) {
        if (scales[i] < 0.0D)
          negCnt++;
      }
      if ((negCnt == 0) || (negCnt == 2))
      {
        double tmp2063_2062 = (outScale[2] = 1.0D);outScale[1] = tmp2063_2062;outScale[0] = tmp2063_2062;
        for (i = 0; i < 9; i++) {
          outRot[i] = rot[i];
        }
        return;
      }
    }
    

    transpose_mat(u1, t1);
    transpose_mat(v1, t2);
    












    svdReorder(m, t1, t2, scales, outRot, outScale);
  }
  


  static void svdReorder(double[] m, double[] t1, double[] t2, double[] scales, double[] outRot, double[] outScale)
  {
    int[] out = new int[3];
    int[] in = new int[3];
    
    double[] mag = new double[3];
    double[] rot = new double[9];
    


    if (scales[0] < 0.0D) {
      scales[0] = (-scales[0]);
      t2[0] = (-t2[0]);
      t2[1] = (-t2[1]);
      t2[2] = (-t2[2]);
    }
    if (scales[1] < 0.0D) {
      scales[1] = (-scales[1]);
      t2[3] = (-t2[3]);
      t2[4] = (-t2[4]);
      t2[5] = (-t2[5]);
    }
    if (scales[2] < 0.0D) {
      scales[2] = (-scales[2]);
      t2[6] = (-t2[6]);
      t2[7] = (-t2[7]);
      t2[8] = (-t2[8]);
    }
    
    mat_mul(t1, t2, rot);
    
    int i;
    if ((almostEqual(Math.abs(scales[0]), Math.abs(scales[1]))) && 
      (almostEqual(Math.abs(scales[1]), Math.abs(scales[2])))) {
      for (i = 0; i < 9; i++) {
        outRot[i] = rot[i];
      }
      for (i = 0; i < 3;) {
        outScale[i] = scales[i];i++; continue;
        




        if (scales[0] > scales[1]) {
          if (scales[0] > scales[2]) {
            if (scales[2] > scales[1]) {
              out[0] = 0;out[1] = 2;out[2] = 1;
            } else {
              out[0] = 0;out[1] = 1;out[2] = 2;
            }
          } else {
            out[0] = 2;out[1] = 0;out[2] = 1;
          }
        }
        else if (scales[1] > scales[2]) {
          if (scales[2] > scales[0]) {
            out[0] = 1;out[1] = 2;out[2] = 0;
          } else {
            out[0] = 1;out[1] = 0;out[2] = 2;
          }
        } else {
          out[0] = 2;out[1] = 1;out[2] = 0;
        }
        









        mag[0] = (m[0] * m[0] + m[1] * m[1] + m[2] * m[2]);
        mag[1] = (m[3] * m[3] + m[4] * m[4] + m[5] * m[5]);
        mag[2] = (m[6] * m[6] + m[7] * m[7] + m[8] * m[8]);
        int in1;
        int in2; int in1; int in0; if (mag[0] > mag[1]) { int in2;
          if (mag[0] > mag[2]) { int in1;
            if (mag[2] > mag[1])
            {
              int in0 = 0;int in2 = 1;in1 = 2;
            }
            else {
              int in0 = 0;int in1 = 1;in2 = 2;
            }
          }
          else {
            int in2 = 0;int in0 = 1;in1 = 2;
          }
        } else { int in2;
          if (mag[1] > mag[2]) { int in0;
            if (mag[2] > mag[0])
            {
              int in1 = 0;int in2 = 1;in0 = 2;
            }
            else {
              int in1 = 0;int in0 = 1;in2 = 2;
            }
          }
          else {
            in2 = 0;in1 = 1;in0 = 2;
          }
        }
        

        int index = out[in0];
        outScale[0] = scales[index];
        
        index = out[in1];
        outScale[1] = scales[index];
        
        index = out[in2];
        outScale[2] = scales[index];
        

        index = out[in0];
        outRot[0] = rot[index];
        
        index = out[in0] + 3;
        outRot[3] = rot[index];
        
        index = out[in0] + 6;
        outRot[6] = rot[index];
        
        index = out[in1];
        outRot[1] = rot[index];
        
        index = out[in1] + 3;
        outRot[4] = rot[index];
        
        index = out[in1] + 6;
        outRot[7] = rot[index];
        
        index = out[in2];
        outRot[2] = rot[index];
        
        index = out[in2] + 3;
        outRot[5] = rot[index];
        
        index = out[in2] + 6;
        outRot[8] = rot[index];
      }
    }
  }
  


  static int compute_qr(double[] s, double[] e, double[] u, double[] v)
  {
    double[] cosl = new double[2];
    double[] cosr = new double[2];
    double[] sinl = new double[2];
    double[] sinr = new double[2];
    double[] m = new double[9];
    



    int MAX_INTERATIONS = 10;
    double CONVERGE_TOL = 4.89E-15D;
    
    double c_b48 = 1.0D;
    double c_b71 = -1.0D;
    
    boolean converged = false;
    

    int first = 1;
    
    if ((Math.abs(e[1]) < 4.89E-15D) || (Math.abs(e[0]) < 4.89E-15D)) { converged = true;
    }
    for (int k = 0; (k < 10) && (!converged); k++) {
      double shift = compute_shift(s[1], e[1], s[2]);
      double f = (Math.abs(s[0]) - shift) * (d_sign(c_b48, s[0]) + shift / s[0]);
      double g = e[0];
      double r = compute_rot(f, g, sinr, cosr, 0, first);
      f = cosr[0] * s[0] + sinr[0] * e[0];
      e[0] = (cosr[0] * e[0] - sinr[0] * s[0]);
      g = sinr[0] * s[1];
      s[1] = (cosr[0] * s[1]);
      
      r = compute_rot(f, g, sinl, cosl, 0, first);
      first = 0;
      s[0] = r;
      f = cosl[0] * e[0] + sinl[0] * s[1];
      s[1] = (cosl[0] * s[1] - sinl[0] * e[0]);
      g = sinl[0] * e[1];
      e[1] = (cosl[0] * e[1]);
      
      r = compute_rot(f, g, sinr, cosr, 1, first);
      e[0] = r;
      f = cosr[1] * s[1] + sinr[1] * e[1];
      e[1] = (cosr[1] * e[1] - sinr[1] * s[1]);
      g = sinr[1] * s[2];
      s[2] = (cosr[1] * s[2]);
      
      r = compute_rot(f, g, sinl, cosl, 1, first);
      s[1] = r;
      f = cosl[1] * e[1] + sinl[1] * s[2];
      s[2] = (cosl[1] * s[2] - sinl[1] * e[1]);
      e[1] = f;
      

      double utemp = u[0];
      u[0] = (cosl[0] * utemp + sinl[0] * u[3]);
      u[3] = (-sinl[0] * utemp + cosl[0] * u[3]);
      utemp = u[1];
      u[1] = (cosl[0] * utemp + sinl[0] * u[4]);
      u[4] = (-sinl[0] * utemp + cosl[0] * u[4]);
      utemp = u[2];
      u[2] = (cosl[0] * utemp + sinl[0] * u[5]);
      u[5] = (-sinl[0] * utemp + cosl[0] * u[5]);
      
      utemp = u[3];
      u[3] = (cosl[1] * utemp + sinl[1] * u[6]);
      u[6] = (-sinl[1] * utemp + cosl[1] * u[6]);
      utemp = u[4];
      u[4] = (cosl[1] * utemp + sinl[1] * u[7]);
      u[7] = (-sinl[1] * utemp + cosl[1] * u[7]);
      utemp = u[5];
      u[5] = (cosl[1] * utemp + sinl[1] * u[8]);
      u[8] = (-sinl[1] * utemp + cosl[1] * u[8]);
      


      double vtemp = v[0];
      v[0] = (cosr[0] * vtemp + sinr[0] * v[1]);
      v[1] = (-sinr[0] * vtemp + cosr[0] * v[1]);
      vtemp = v[3];
      v[3] = (cosr[0] * vtemp + sinr[0] * v[4]);
      v[4] = (-sinr[0] * vtemp + cosr[0] * v[4]);
      vtemp = v[6];
      v[6] = (cosr[0] * vtemp + sinr[0] * v[7]);
      v[7] = (-sinr[0] * vtemp + cosr[0] * v[7]);
      
      vtemp = v[1];
      v[1] = (cosr[1] * vtemp + sinr[1] * v[2]);
      v[2] = (-sinr[1] * vtemp + cosr[1] * v[2]);
      vtemp = v[4];
      v[4] = (cosr[1] * vtemp + sinr[1] * v[5]);
      v[5] = (-sinr[1] * vtemp + cosr[1] * v[5]);
      vtemp = v[7];
      v[7] = (cosr[1] * vtemp + sinr[1] * v[8]);
      v[8] = (-sinr[1] * vtemp + cosr[1] * v[8]);
      

      m[0] = s[0];m[1] = e[0];m[2] = 0.0D;
      m[3] = 0.0D;m[4] = s[1];m[5] = e[1];
      m[6] = 0.0D;m[7] = 0.0D;m[8] = s[2];
      
      if ((Math.abs(e[1]) < 4.89E-15D) || (Math.abs(e[0]) < 4.89E-15D)) { converged = true;
      }
    }
    if (Math.abs(e[1]) < 4.89E-15D) {
      compute_2X2(s[0], e[0], s[1], s, sinl, cosl, sinr, cosr, 0);
      
      double utemp = u[0];
      u[0] = (cosl[0] * utemp + sinl[0] * u[3]);
      u[3] = (-sinl[0] * utemp + cosl[0] * u[3]);
      utemp = u[1];
      u[1] = (cosl[0] * utemp + sinl[0] * u[4]);
      u[4] = (-sinl[0] * utemp + cosl[0] * u[4]);
      utemp = u[2];
      u[2] = (cosl[0] * utemp + sinl[0] * u[5]);
      u[5] = (-sinl[0] * utemp + cosl[0] * u[5]);
      


      double vtemp = v[0];
      v[0] = (cosr[0] * vtemp + sinr[0] * v[1]);
      v[1] = (-sinr[0] * vtemp + cosr[0] * v[1]);
      vtemp = v[3];
      v[3] = (cosr[0] * vtemp + sinr[0] * v[4]);
      v[4] = (-sinr[0] * vtemp + cosr[0] * v[4]);
      vtemp = v[6];
      v[6] = (cosr[0] * vtemp + sinr[0] * v[7]);
      v[7] = (-sinr[0] * vtemp + cosr[0] * v[7]);
    } else {
      compute_2X2(s[1], e[1], s[2], s, sinl, cosl, sinr, cosr, 1);
      
      double utemp = u[3];
      u[3] = (cosl[0] * utemp + sinl[0] * u[6]);
      u[6] = (-sinl[0] * utemp + cosl[0] * u[6]);
      utemp = u[4];
      u[4] = (cosl[0] * utemp + sinl[0] * u[7]);
      u[7] = (-sinl[0] * utemp + cosl[0] * u[7]);
      utemp = u[5];
      u[5] = (cosl[0] * utemp + sinl[0] * u[8]);
      u[8] = (-sinl[0] * utemp + cosl[0] * u[8]);
      


      double vtemp = v[1];
      v[1] = (cosr[0] * vtemp + sinr[0] * v[2]);
      v[2] = (-sinr[0] * vtemp + cosr[0] * v[2]);
      vtemp = v[4];
      v[4] = (cosr[0] * vtemp + sinr[0] * v[5]);
      v[5] = (-sinr[0] * vtemp + cosr[0] * v[5]);
      vtemp = v[7];
      v[7] = (cosr[0] * vtemp + sinr[0] * v[8]);
      v[8] = (-sinr[0] * vtemp + cosr[0] * v[8]);
    }
    
    return 0;
  }
  
  static double max(double a, double b) { if (a > b) {
      return a;
    }
    return b;
  }
  
  static double min(double a, double b) { if (a < b) {
      return a;
    }
    return b;
  }
  
  static double d_sign(double a, double b) {
    double x = a >= 0.0D ? a : -a;
    return b >= 0.0D ? x : -x;
  }
  



  static double compute_shift(double f, double g, double h)
  {
    double fa = Math.abs(f);
    double ga = Math.abs(g);
    double ha = Math.abs(h);
    double fhmn = min(fa, ha);
    double fhmx = max(fa, ha);
    double ssmin; if (fhmn == 0.0D) {
      double ssmin = 0.0D;
      if (fhmx != 0.0D)
      {
        double d1 = min(fhmx, ga) / max(fhmx, ga); }
    } else {
      double ssmin;
      if (ga < fhmx) {
        double as = fhmn / fhmx + 1.0D;
        double at = (fhmx - fhmn) / fhmx;
        double d__1 = ga / fhmx;
        double au = d__1 * d__1;
        double c = 2.0D / (Math.sqrt(as * as + au) + Math.sqrt(at * at + au));
        ssmin = fhmn * c;
      } else {
        double au = fhmx / ga;
        double ssmin; if (au == 0.0D) {
          ssmin = fhmn * fhmx / ga;
        } else {
          double as = fhmn / fhmx + 1.0D;
          double at = (fhmx - fhmn) / fhmx;
          double d__1 = as * au;
          double d__2 = at * au;
          double c = 1.0D / (Math.sqrt(d__1 * d__1 + 1.0D) + Math.sqrt(d__2 * d__2 + 1.0D));
          ssmin = fhmn * c * au;
          ssmin += ssmin;
        }
      }
    }
    
    return ssmin;
  }
  
  static int compute_2X2(double f, double g, double h, double[] single_values, double[] snl, double[] csl, double[] snr, double[] csr, int index)
  {
    double c_b3 = 2.0D;
    double c_b4 = 1.0D;
    










    double ssmax = single_values[0];
    double ssmin = single_values[1];
    double clt = 0.0D;
    double crt = 0.0D;
    double slt = 0.0D;
    double srt = 0.0D;
    double tsign = 0.0D;
    
    double ft = f;
    double fa = Math.abs(ft);
    double ht = h;
    double ha = Math.abs(h);
    
    int pmax = 1;
    boolean swap; boolean swap; if (ha > fa) {
      swap = true;
    } else {
      swap = false;
    }
    if (swap) {
      pmax = 3;
      double temp = ft;
      ft = ht;
      ht = temp;
      temp = fa;
      fa = ha;
      ha = temp;
    }
    
    double gt = g;
    double ga = Math.abs(gt);
    if (ga == 0.0D)
    {
      single_values[1] = ha;
      single_values[0] = fa;
      clt = 1.0D;
      crt = 1.0D;
      slt = 0.0D;
      srt = 0.0D;
    } else {
      boolean gasmal = true;
      
      if (ga > fa) {
        pmax = 2;
        if (fa / ga < 1.110223024E-16D)
        {
          gasmal = false;
          ssmax = ga;
          if (ha > 1.0D) {
            ssmin = fa / (ga / ha);
          } else {
            ssmin = fa / ga * ha;
          }
          clt = 1.0D;
          slt = ht / gt;
          srt = 1.0D;
          crt = ft / gt;
        }
      }
      if (gasmal)
      {
        double d = fa - ha;
        double l; double l; if (d == fa)
        {
          l = 1.0D;
        } else {
          l = d / fa;
        }
        
        double m = gt / ft;
        
        double t = 2.0D - l;
        
        double mm = m * m;
        double tt = t * t;
        double s = Math.sqrt(tt + mm);
        double r;
        double r; if (l == 0.0D) {
          r = Math.abs(m);
        } else {
          r = Math.sqrt(l * l + mm);
        }
        
        double a = (s + r) * 0.5D;
        
        if (ga > fa) {
          pmax = 2;
          if (fa / ga < 1.110223024E-16D)
          {
            gasmal = false;
            ssmax = ga;
            if (ha > 1.0D) {
              ssmin = fa / (ga / ha);
            } else {
              ssmin = fa / ga * ha;
            }
            clt = 1.0D;
            slt = ht / gt;
            srt = 1.0D;
            crt = ft / gt;
          }
        }
        if (gasmal)
        {
          d = fa - ha;
          if (d == fa)
          {
            l = 1.0D;
          } else {
            l = d / fa;
          }
          
          m = gt / ft;
          
          t = 2.0D - l;
          
          mm = m * m;
          tt = t * t;
          s = Math.sqrt(tt + mm);
          
          if (l == 0.0D) {
            r = Math.abs(m);
          } else {
            r = Math.sqrt(l * l + mm);
          }
          
          a = (s + r) * 0.5D;
          

          ssmin = ha / a;
          ssmax = fa * a;
          if (mm == 0.0D)
          {
            if (l == 0.0D) {
              t = d_sign(c_b3, ft) * d_sign(c_b4, gt);
            } else {
              t = gt / d_sign(d, ft) + m / t;
            }
          } else {
            t = (m / (s + t) + m / (r + l)) * (a + 1.0D);
          }
          l = Math.sqrt(t * t + 4.0D);
          crt = 2.0D / l;
          srt = t / l;
          clt = (crt + srt * m) / a;
          slt = ht / ft * srt / a;
        }
      }
      if (swap) {
        csl[0] = srt;
        snl[0] = crt;
        csr[0] = slt;
        snr[0] = clt;
      } else {
        csl[0] = clt;
        snl[0] = slt;
        csr[0] = crt;
        snr[0] = srt;
      }
      
      if (pmax == 1) {
        tsign = d_sign(c_b4, csr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, f);
      }
      if (pmax == 2) {
        tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, g);
      }
      if (pmax == 3) {
        tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, snl[0]) * d_sign(c_b4, h);
      }
      single_values[index] = d_sign(ssmax, tsign);
      double d__1 = tsign * d_sign(c_b4, f) * d_sign(c_b4, h);
      single_values[(index + 1)] = d_sign(ssmin, d__1);
    }
    

    return 0;
  }
  






  static double compute_rot(double f, double g, double[] sin, double[] cos, int index, int first)
  {
    double safmn2 = 2.002083095183101E-146D;
    double safmx2 = 4.9947976805055876E145D;
    double r;
    double r; double cs; double sn; if (g == 0.0D) {
      double cs = 1.0D;
      double sn = 0.0D;
      r = f; } else { double r;
      if (f == 0.0D) {
        double cs = 0.0D;
        double sn = 1.0D;
        r = g;
      } else {
        double f1 = f;
        double g1 = g;
        double scale = max(Math.abs(f1), Math.abs(g1));
        if (scale >= 4.9947976805055876E145D) {
          int count = 0;
          while (scale >= 4.9947976805055876E145D) {
            count++;
            f1 *= 2.002083095183101E-146D;
            g1 *= 2.002083095183101E-146D;
            scale = max(Math.abs(f1), Math.abs(g1));
          }
          double r = Math.sqrt(f1 * f1 + g1 * g1);
          double cs = f1 / r;
          double sn = g1 / r;
          int i__1 = count;
          for (int i = 1; i <= count; i++)
            r *= 4.9947976805055876E145D;
        }
        if (scale <= 2.002083095183101E-146D) {
          int count = 0;
          while (scale <= 2.002083095183101E-146D) {
            count++;
            f1 *= 4.9947976805055876E145D;
            g1 *= 4.9947976805055876E145D;
            scale = max(Math.abs(f1), Math.abs(g1));
          }
          double r = Math.sqrt(f1 * f1 + g1 * g1);
          double cs = f1 / r;
          double sn = g1 / r;
          int i__1 = count;
          for (int i = 1; i <= count; i++) {
            r *= 2.002083095183101E-146D;
          }
        }
        r = Math.sqrt(f1 * f1 + g1 * g1);
        cs = f1 / r;
        sn = g1 / r;
        
        if ((Math.abs(f) > Math.abs(g)) && (cs < 0.0D)) {
          cs = -cs;
          sn = -sn;
          r = -r;
        }
      } }
    sin[index] = sn;
    cos[index] = cs;
    return r;
  }
  
  static void print_mat(double[] mat)
  {
    for (int i = 0; i < 3; i++) {
      System.out.println(mat[(i * 3 + 0)] + " " + mat[(i * 3 + 1)] + " " + mat[(i * 3 + 2)] + "\n");
    }
  }
  

  static void print_det(double[] mat)
  {
    double det = mat[0] * mat[4] * mat[8] + mat[1] * mat[5] * mat[6] + mat[2] * mat[3] * mat[7] - mat[2] * mat[4] * mat[6] - mat[0] * mat[5] * mat[7] - mat[1] * mat[3] * mat[8];
    




    System.out.println("det= " + det);
  }
  
  static void mat_mul(double[] m1, double[] m2, double[] m3) {
    double[] tmp = new double[9];
    
    tmp[0] = (m1[0] * m2[0] + m1[1] * m2[3] + m1[2] * m2[6]);
    tmp[1] = (m1[0] * m2[1] + m1[1] * m2[4] + m1[2] * m2[7]);
    tmp[2] = (m1[0] * m2[2] + m1[1] * m2[5] + m1[2] * m2[8]);
    
    tmp[3] = (m1[3] * m2[0] + m1[4] * m2[3] + m1[5] * m2[6]);
    tmp[4] = (m1[3] * m2[1] + m1[4] * m2[4] + m1[5] * m2[7]);
    tmp[5] = (m1[3] * m2[2] + m1[4] * m2[5] + m1[5] * m2[8]);
    
    tmp[6] = (m1[6] * m2[0] + m1[7] * m2[3] + m1[8] * m2[6]);
    tmp[7] = (m1[6] * m2[1] + m1[7] * m2[4] + m1[8] * m2[7]);
    tmp[8] = (m1[6] * m2[2] + m1[7] * m2[5] + m1[8] * m2[8]);
    
    for (int i = 0; i < 9; i++)
      m3[i] = tmp[i];
  }
  
  static void transpose_mat(double[] in, double[] out) {
    out[0] = in[0];
    out[1] = in[3];
    out[2] = in[6];
    
    out[3] = in[1];
    out[4] = in[4];
    out[5] = in[7];
    
    out[6] = in[2];
    out[7] = in[5];
    out[8] = in[8];
  }
  
  static double max3(double[] values) { if (values[0] > values[1]) {
      if (values[0] > values[2]) {
        return values[0];
      }
      return values[2];
    }
    if (values[1] > values[2]) {
      return values[1];
    }
    return values[2];
  }
  
  private static final boolean almostEqual(double a, double b)
  {
    if (a == b) {
      return true;
    }
    double EPSILON_ABSOLUTE = 1.0E-6D;
    double EPSILON_RELATIVE = 1.0E-4D;
    double diff = Math.abs(a - b);
    double absA = Math.abs(a);
    double absB = Math.abs(b);
    double max = absA >= absB ? absA : absB;
    
    if (diff < 1.0E-6D) {
      return true;
    }
    if (diff / max < 1.0E-4D) {
      return true;
    }
    return false;
  }
  







  public Object clone()
  {
    Matrix3d m1 = null;
    try {
      m1 = (Matrix3d)super.clone();
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
}
