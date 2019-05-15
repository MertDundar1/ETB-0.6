package javax.vecmath;

import java.io.PrintStream;
import java.io.Serializable;

















































public class GMatrix
  implements Serializable, Cloneable
{
  static final long serialVersionUID = 2777097312029690941L;
  private static final boolean debug = false;
  int nRow;
  int nCol;
  double[][] values;
  private static final double EPS = 1.0E-10D;
  
  public GMatrix(int nRow, int nCol)
  {
    values = new double[nRow][nCol];
    this.nRow = nRow;
    this.nCol = nCol;
    

    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
    int l;
    int l;
    if (nRow < nCol) {
      l = nRow;
    } else {
      l = nCol;
    }
    for (i = 0; i < l; i++) {
      values[i][i] = 1.0D;
    }
  }
  












  public GMatrix(int nRow, int nCol, double[] matrix)
  {
    values = new double[nRow][nCol];
    this.nRow = nRow;
    this.nCol = nCol;
    

    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = matrix[(i * nCol + j)];
      }
    }
  }
  





  public GMatrix(GMatrix matrix)
  {
    nRow = nRow;
    nCol = nCol;
    values = new double[nRow][nCol];
    

    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = values[i][j];
      }
    }
  }
  







  public final void mul(GMatrix m1)
  {
    if ((nCol != nRow) || (nCol != nCol))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix0"));
    }
    double[][] tmp = new double[nRow][nCol];
    
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        tmp[i][j] = 0.0D;
        for (int k = 0; k < nCol; k++) {
          tmp[i][j] += values[i][k] * values[k][j];
        }
      }
    }
    
    values = tmp;
  }
  








  public final void mul(GMatrix m1, GMatrix m2)
  {
    if ((nCol != nRow) || (nRow != nRow) || (nCol != nCol))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix1"));
    }
    double[][] tmp = new double[nRow][nCol];
    
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        tmp[i][j] = 0.0D;
        for (int k = 0; k < nCol; k++) {
          tmp[i][j] += values[i][k] * values[k][j];
        }
      }
    }
    
    values = tmp;
  }
  










  public final void mul(GVector v1, GVector v2)
  {
    if (nRow < v1.getSize())
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix2"));
    }
    if (nCol < v2.getSize())
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix3"));
    }
    for (int i = 0; i < v1.getSize(); i++) {
      for (int j = 0; j < v2.getSize(); j++) {
        values[i][j] = (values[i] * values[j]);
      }
    }
  }
  






  public final void add(GMatrix m1)
  {
    if (nRow != nRow)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix4"));
    }
    if (nCol != nCol)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix5"));
    }
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] += values[i][j];
      }
    }
  }
  







  public final void add(GMatrix m1, GMatrix m2)
  {
    if (nRow != nRow)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix6"));
    }
    if (nCol != nCol)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix7"));
    }
    if ((nCol != nCol) || (nRow != nRow))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix8"));
    }
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] += values[i][j];
      }
    }
  }
  






  public final void sub(GMatrix m1)
  {
    if (nRow != nRow)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix9"));
    }
    if (nCol != nCol)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix28"));
    }
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] -= values[i][j];
      }
    }
  }
  







  public final void sub(GMatrix m1, GMatrix m2)
  {
    if (nRow != nRow)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix10"));
    }
    if (nCol != nCol)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix11"));
    }
    if ((nRow != nRow) || (nCol != nCol))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix12"));
    }
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] -= values[i][j];
      }
    }
  }
  




  public final void negate()
  {
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = (-values[i][j]);
      }
    }
  }
  






  public final void negate(GMatrix m1)
  {
    if ((nRow != nRow) || (nCol != nCol))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix13"));
    }
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = (-values[i][j]);
      }
    }
  }
  




  public final void setIdentity()
  {
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
    int l;
    int l;
    if (nRow < nCol) {
      l = nRow;
    } else {
      l = nCol;
    }
    for (i = 0; i < l; i++) {
      values[i][i] = 1.0D;
    }
  }
  




  public final void setZero()
  {
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
  }
  






  public final void identityMinus()
  {
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = (-values[i][j]);
      }
    }
    int l;
    int l;
    if (nRow < nCol) {
      l = nRow;
    } else {
      l = nCol;
    }
    for (i = 0; i < l; i++) {
      values[i][i] += 1.0D;
    }
  }
  




  public final void invert()
  {
    invertGeneral(this);
  }
  





  public final void invert(GMatrix m1)
  {
    invertGeneral(m1);
  }
  




















  public final void copySubMatrix(int rowSource, int colSource, int numRow, int numCol, int rowDest, int colDest, GMatrix target)
  {
    if (this != target) {
      for (int i = 0; i < numRow; i++) {
        for (int j = 0; j < numCol; j++) {
          values[(rowDest + i)][(colDest + j)] = values[(rowSource + i)][(colSource + j)];
        }
      }
    }
    
    double[][] tmp = new double[numRow][numCol];
    for (int i = 0; i < numRow; i++) {
      for (int j = 0; j < numCol; j++) {
        tmp[i][j] = values[(rowSource + i)][(colSource + j)];
      }
    }
    for (i = 0; i < numRow; i++) {
      for (int j = 0; j < numCol; j++) {
        values[(rowDest + i)][(colDest + j)] = tmp[i][j];
      }
    }
  }
  








  public final void setSize(int nRow, int nCol)
  {
    double[][] tmp = new double[nRow][nCol];
    int maxRow;
    int maxRow;
    if (this.nRow < nRow) {
      maxRow = this.nRow;
    } else
      maxRow = nRow;
    int maxCol;
    int maxCol; if (this.nCol < nCol) {
      maxCol = this.nCol;
    } else {
      maxCol = nCol;
    }
    for (int i = 0; i < maxRow; i++) {
      for (int j = 0; j < maxCol; j++) {
        tmp[i][j] = values[i][j];
      }
    }
    
    this.nRow = nRow;
    this.nCol = nCol;
    
    values = tmp;
  }
  










  public final void set(double[] matrix)
  {
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = matrix[(nCol * i + j)];
      }
    }
  }
  






  public final void set(Matrix3f m1)
  {
    if ((nCol < 3) || (nRow < 3)) {
      nCol = 3;
      nRow = 3;
      values = new double[nRow][nCol];
    }
    
    values[0][0] = m00;
    values[0][1] = m01;
    values[0][2] = m02;
    
    values[1][0] = m10;
    values[1][1] = m11;
    values[1][2] = m12;
    
    values[2][0] = m20;
    values[2][1] = m21;
    values[2][2] = m22;
    
    for (int i = 3; i < nRow; i++) {
      for (int j = 3; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
  }
  




  public final void set(Matrix3d m1)
  {
    if ((nRow < 3) || (nCol < 3)) {
      values = new double[3][3];
      nRow = 3;
      nCol = 3;
    }
    
    values[0][0] = m00;
    values[0][1] = m01;
    values[0][2] = m02;
    
    values[1][0] = m10;
    values[1][1] = m11;
    values[1][2] = m12;
    
    values[2][0] = m20;
    values[2][1] = m21;
    values[2][2] = m22;
    
    for (int i = 3; i < nRow; i++) {
      for (int j = 3; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
  }
  





  public final void set(Matrix4f m1)
  {
    if ((nRow < 4) || (nCol < 4)) {
      values = new double[4][4];
      nRow = 4;
      nCol = 4;
    }
    
    values[0][0] = m00;
    values[0][1] = m01;
    values[0][2] = m02;
    values[0][3] = m03;
    
    values[1][0] = m10;
    values[1][1] = m11;
    values[1][2] = m12;
    values[1][3] = m13;
    
    values[2][0] = m20;
    values[2][1] = m21;
    values[2][2] = m22;
    values[2][3] = m23;
    
    values[3][0] = m30;
    values[3][1] = m31;
    values[3][2] = m32;
    values[3][3] = m33;
    
    for (int i = 4; i < nRow; i++) {
      for (int j = 4; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
  }
  




  public final void set(Matrix4d m1)
  {
    if ((nRow < 4) || (nCol < 4)) {
      values = new double[4][4];
      nRow = 4;
      nCol = 4;
    }
    
    values[0][0] = m00;
    values[0][1] = m01;
    values[0][2] = m02;
    values[0][3] = m03;
    
    values[1][0] = m10;
    values[1][1] = m11;
    values[1][2] = m12;
    values[1][3] = m13;
    
    values[2][0] = m20;
    values[2][1] = m21;
    values[2][2] = m22;
    values[2][3] = m23;
    
    values[3][0] = m30;
    values[3][1] = m31;
    values[3][2] = m32;
    values[3][3] = m33;
    
    for (int i = 4; i < nRow; i++) {
      for (int j = 4; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
  }
  






  public final void set(GMatrix m1)
  {
    if ((nRow < nRow) || (nCol < nCol)) {
      nRow = nRow;
      nCol = nCol;
      values = new double[nRow][nCol];
    }
    
    for (int i = 0; i < Math.min(nRow, nRow); i++) {
      for (int j = 0; j < Math.min(nCol, nCol); j++) {
        values[i][j] = values[i][j];
      }
    }
    
    for (i = nRow; i < nRow; i++) {
      for (int j = nCol; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
  }
  




  public final int getNumRow()
  {
    return nRow;
  }
  




  public final int getNumCol()
  {
    return nCol;
  }
  






  public final double getElement(int row, int column)
  {
    return values[row][column];
  }
  







  public final void setElement(int row, int column, double value)
  {
    values[row][column] = value;
  }
  





  public final void getRow(int row, double[] array)
  {
    for (int i = 0; i < nCol; i++) {
      array[i] = values[row][i];
    }
  }
  





  public final void getRow(int row, GVector vector)
  {
    if (vector.getSize() < nCol) {
      vector.setSize(nCol);
    }
    for (int i = 0; i < nCol; i++) {
      values[i] = values[row][i];
    }
  }
  





  public final void getColumn(int col, double[] array)
  {
    for (int i = 0; i < nRow; i++) {
      array[i] = values[i][col];
    }
  }
  






  public final void getColumn(int col, GVector vector)
  {
    if (vector.getSize() < nRow) {
      vector.setSize(nRow);
    }
    for (int i = 0; i < nRow; i++) {
      values[i] = values[i][col];
    }
  }
  





  public final void get(Matrix3d m1)
  {
    if ((nRow < 3) || (nCol < 3)) {
      m1.setZero();
      if (nCol > 0) {
        if (nRow > 0) {
          m00 = values[0][0];
          if (nRow > 1) {
            m10 = values[1][0];
            if (nRow > 2) {
              m20 = values[2][0];
            }
          }
        }
        if (nCol > 1) {
          if (nRow > 0) {
            m01 = values[0][1];
            if (nRow > 1) {
              m11 = values[1][1];
              if (nRow > 2) {
                m21 = values[2][1];
              }
            }
          }
          if ((nCol > 2) && 
            (nRow > 0)) {
            m02 = values[0][2];
            if (nRow > 1) {
              m12 = values[1][2];
              if (nRow > 2) {
                m22 = values[2][2];
              }
            }
          }
        }
      }
    }
    else {
      m00 = values[0][0];
      m01 = values[0][1];
      m02 = values[0][2];
      
      m10 = values[1][0];
      m11 = values[1][1];
      m12 = values[1][2];
      
      m20 = values[2][0];
      m21 = values[2][1];
      m22 = values[2][2];
    }
  }
  






  public final void get(Matrix3f m1)
  {
    if ((nRow < 3) || (nCol < 3)) {
      m1.setZero();
      if (nCol > 0) {
        if (nRow > 0) {
          m00 = ((float)values[0][0]);
          if (nRow > 1) {
            m10 = ((float)values[1][0]);
            if (nRow > 2) {
              m20 = ((float)values[2][0]);
            }
          }
        }
        if (nCol > 1) {
          if (nRow > 0) {
            m01 = ((float)values[0][1]);
            if (nRow > 1) {
              m11 = ((float)values[1][1]);
              if (nRow > 2) {
                m21 = ((float)values[2][1]);
              }
            }
          }
          if ((nCol > 2) && 
            (nRow > 0)) {
            m02 = ((float)values[0][2]);
            if (nRow > 1) {
              m12 = ((float)values[1][2]);
              if (nRow > 2) {
                m22 = ((float)values[2][2]);
              }
            }
          }
        }
      }
    }
    else {
      m00 = ((float)values[0][0]);
      m01 = ((float)values[0][1]);
      m02 = ((float)values[0][2]);
      
      m10 = ((float)values[1][0]);
      m11 = ((float)values[1][1]);
      m12 = ((float)values[1][2]);
      
      m20 = ((float)values[2][0]);
      m21 = ((float)values[2][1]);
      m22 = ((float)values[2][2]);
    }
  }
  





  public final void get(Matrix4d m1)
  {
    if ((nRow < 4) || (nCol < 4)) {
      m1.setZero();
      if (nCol > 0) {
        if (nRow > 0) {
          m00 = values[0][0];
          if (nRow > 1) {
            m10 = values[1][0];
            if (nRow > 2) {
              m20 = values[2][0];
              if (nRow > 3) {
                m30 = values[3][0];
              }
            }
          }
        }
        if (nCol > 1) {
          if (nRow > 0) {
            m01 = values[0][1];
            if (nRow > 1) {
              m11 = values[1][1];
              if (nRow > 2) {
                m21 = values[2][1];
                if (nRow > 3) {
                  m31 = values[3][1];
                }
              }
            }
          }
          if (nCol > 2) {
            if (nRow > 0) {
              m02 = values[0][2];
              if (nRow > 1) {
                m12 = values[1][2];
                if (nRow > 2) {
                  m22 = values[2][2];
                  if (nRow > 3) {
                    m32 = values[3][2];
                  }
                }
              }
            }
            if ((nCol > 3) && 
              (nRow > 0)) {
              m03 = values[0][3];
              if (nRow > 1) {
                m13 = values[1][3];
                if (nRow > 2) {
                  m23 = values[2][3];
                  if (nRow > 3) {
                    m33 = values[3][3];
                  }
                }
              }
            }
          }
        }
      }
    }
    else {
      m00 = values[0][0];
      m01 = values[0][1];
      m02 = values[0][2];
      m03 = values[0][3];
      
      m10 = values[1][0];
      m11 = values[1][1];
      m12 = values[1][2];
      m13 = values[1][3];
      
      m20 = values[2][0];
      m21 = values[2][1];
      m22 = values[2][2];
      m23 = values[2][3];
      
      m30 = values[3][0];
      m31 = values[3][1];
      m32 = values[3][2];
      m33 = values[3][3];
    }
  }
  







  public final void get(Matrix4f m1)
  {
    if ((nRow < 4) || (nCol < 4)) {
      m1.setZero();
      if (nCol > 0) {
        if (nRow > 0) {
          m00 = ((float)values[0][0]);
          if (nRow > 1) {
            m10 = ((float)values[1][0]);
            if (nRow > 2) {
              m20 = ((float)values[2][0]);
              if (nRow > 3) {
                m30 = ((float)values[3][0]);
              }
            }
          }
        }
        if (nCol > 1) {
          if (nRow > 0) {
            m01 = ((float)values[0][1]);
            if (nRow > 1) {
              m11 = ((float)values[1][1]);
              if (nRow > 2) {
                m21 = ((float)values[2][1]);
                if (nRow > 3) {
                  m31 = ((float)values[3][1]);
                }
              }
            }
          }
          if (nCol > 2) {
            if (nRow > 0) {
              m02 = ((float)values[0][2]);
              if (nRow > 1) {
                m12 = ((float)values[1][2]);
                if (nRow > 2) {
                  m22 = ((float)values[2][2]);
                  if (nRow > 3) {
                    m32 = ((float)values[3][2]);
                  }
                }
              }
            }
            if ((nCol > 3) && 
              (nRow > 0)) {
              m03 = ((float)values[0][3]);
              if (nRow > 1) {
                m13 = ((float)values[1][3]);
                if (nRow > 2) {
                  m23 = ((float)values[2][3]);
                  if (nRow > 3) {
                    m33 = ((float)values[3][3]);
                  }
                }
              }
            }
          }
        }
      }
    }
    else {
      m00 = ((float)values[0][0]);
      m01 = ((float)values[0][1]);
      m02 = ((float)values[0][2]);
      m03 = ((float)values[0][3]);
      
      m10 = ((float)values[1][0]);
      m11 = ((float)values[1][1]);
      m12 = ((float)values[1][2]);
      m13 = ((float)values[1][3]);
      
      m20 = ((float)values[2][0]);
      m21 = ((float)values[2][1]);
      m22 = ((float)values[2][2]);
      m23 = ((float)values[2][3]);
      
      m30 = ((float)values[3][0]);
      m31 = ((float)values[3][1]);
      m32 = ((float)values[3][2]);
      m33 = ((float)values[3][3]);
    }
  }
  


  public final void get(GMatrix m1)
  {
    int nc;
    

    int nc;
    
    if (nCol < nCol) {
      nc = nCol;
    } else
      nc = nCol;
    int nr;
    int nr; if (nRow < nRow) {
      nr = nRow;
    } else {
      nr = nRow;
    }
    for (int i = 0; i < nr; i++) {
      for (int j = 0; j < nc; j++) {
        values[i][j] = values[i][j];
      }
    }
    for (i = nr; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
    for (int j = nc; j < nCol; j++) {
      for (i = 0; i < nr; i++) {
        values[i][j] = 0.0D;
      }
    }
  }
  







  public final void setRow(int row, double[] array)
  {
    for (int i = 0; i < nCol; i++) {
      values[row][i] = array[i];
    }
  }
  







  public final void setRow(int row, GVector vector)
  {
    for (int i = 0; i < nCol; i++) {
      values[row][i] = values[i];
    }
  }
  







  public final void setColumn(int col, double[] array)
  {
    for (int i = 0; i < nRow; i++) {
      values[i][col] = array[i];
    }
  }
  







  public final void setColumn(int col, GVector vector)
  {
    for (int i = 0; i < nRow; i++) {
      values[i][col] = values[i];
    }
  }
  









  public final void mulTransposeBoth(GMatrix m1, GMatrix m2)
  {
    if ((nRow != nCol) || (nRow != nCol) || (nCol != nRow))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix14"));
    }
    if ((m1 == this) || (m2 == this)) {
      double[][] tmp = new double[nRow][nCol];
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          tmp[i][j] = 0.0D;
          for (int k = 0; k < nRow; k++) {
            tmp[i][j] += values[k][i] * values[j][k];
          }
        }
      }
      values = tmp;
    } else {
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          values[i][j] = 0.0D;
          for (int k = 0; k < nRow; k++) {
            values[i][j] += values[k][i] * values[j][k];
          }
        }
      }
    }
  }
  








  public final void mulTransposeRight(GMatrix m1, GMatrix m2)
  {
    if ((nCol != nCol) || (nCol != nRow) || (nRow != nRow))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix15"));
    }
    if ((m1 == this) || (m2 == this)) {
      double[][] tmp = new double[nRow][nCol];
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          tmp[i][j] = 0.0D;
          for (int k = 0; k < nCol; k++) {
            tmp[i][j] += values[i][k] * values[j][k];
          }
        }
      }
      values = tmp;
    } else {
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          values[i][j] = 0.0D;
          for (int k = 0; k < nCol; k++) {
            values[i][j] += values[i][k] * values[j][k];
          }
        }
      }
    }
  }
  










  public final void mulTransposeLeft(GMatrix m1, GMatrix m2)
  {
    if ((nRow != nRow) || (nCol != nCol) || (nRow != nCol))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix16"));
    }
    if ((m1 == this) || (m2 == this)) {
      double[][] tmp = new double[nRow][nCol];
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          tmp[i][j] = 0.0D;
          for (int k = 0; k < nRow; k++) {
            tmp[i][j] += values[k][i] * values[k][j];
          }
        }
      }
      values = tmp;
    } else {
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          values[i][j] = 0.0D;
          for (int k = 0; k < nRow; k++) {
            values[i][j] += values[k][i] * values[k][j];
          }
        }
      }
    }
  }
  






  public final void transpose()
  {
    if (nRow != nCol)
    {
      int i = nRow;
      nRow = nCol;
      nCol = i;
      double[][] tmp = new double[nRow][nCol];
      for (i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          tmp[i][j] = values[j][i];
        }
      }
      values = tmp;
    }
    else {
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < i; j++) {
          double swap = values[i][j];
          values[i][j] = values[j][i];
          values[j][i] = swap;
        }
      }
    }
  }
  






  public final void transpose(GMatrix m1)
  {
    if ((nRow != nCol) || (nCol != nRow))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix17"));
    }
    if (m1 != this) {
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          values[i][j] = values[j][i];
        }
      }
    }
    transpose();
  }
  





  public String toString()
  {
    StringBuffer buffer = new StringBuffer(nRow * nCol * 8);
    


    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        buffer.append(values[i][j]).append(" ");
      }
      buffer.append("\n");
    }
    
    return buffer.toString();
  }
  


  private static void checkMatrix(GMatrix m)
  {
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        if (Math.abs(values[i][j]) < 1.0E-10D) {
          System.out.print(" 0.0     ");
        } else {
          System.out.print(" " + values[i][j]);
        }
      }
      System.out.print("\n");
    }
  }
  









  public int hashCode()
  {
    long bits = 1L;
    
    bits = 31L * bits + nRow;
    bits = 31L * bits + nCol;
    
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        bits = 31L * bits + VecMathUtil.doubleToLongBits(values[i][j]);
      }
    }
    
    return (int)(bits ^ bits >> 32);
  }
  








  public boolean equals(GMatrix m1)
  {
    try
    {
      if ((nRow != nRow) || (nCol != nCol)) {
        return false;
      }
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          if (values[i][j] != values[i][j])
            return false;
        }
      }
      return true;
    }
    catch (NullPointerException e2) {}
    return false;
  }
  







  public boolean equals(Object o1)
  {
    try
    {
      GMatrix m2 = (GMatrix)o1;
      
      if ((nRow != nRow) || (nCol != nCol)) {
        return false;
      }
      for (int i = 0; i < nRow; i++) {
        for (int j = 0; j < nCol; j++) {
          if (values[i][j] != values[i][j])
            return false;
        }
      }
      return true;
    }
    catch (ClassCastException e1) {
      return false;
    }
    catch (NullPointerException e2) {}
    return false;
  }
  
  /**
   * @deprecated
   */
  public boolean epsilonEquals(GMatrix m1, float epsilon)
  {
    return epsilonEquals(m1, epsilon);
  }
  











  public boolean epsilonEquals(GMatrix m1, double epsilon)
  {
    if ((nRow != nRow) || (nCol != nCol)) {
      return false;
    }
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        double diff = values[i][j] - values[i][j];
        if ((diff < 0.0D ? -diff : diff) > epsilon)
          return false;
      }
    }
    return true;
  }
  


  public final double trace()
  {
    int l;
    

    int l;
    
    if (nRow < nCol) {
      l = nRow;
    } else {
      l = nCol;
    }
    double t = 0.0D;
    for (int i = 0; i < l; i++) {
      t += values[i][i];
    }
    return t;
  }
  

















  public final int SVD(GMatrix U, GMatrix W, GMatrix V)
  {
    if ((nCol != nCol) || (nCol != nRow))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix18"));
    }
    
    if ((nRow != nRow) || (nRow != nCol))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix25"));
    }
    
    if ((nRow != nRow) || (nCol != nCol))
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix26"));
    }
    










    if ((nRow == 2) && (nCol == 2) && 
      (values[1][0] == 0.0D)) {
      U.setIdentity();
      V.setIdentity();
      
      if (values[0][1] == 0.0D) {
        return 2;
      }
      
      double[] sinl = new double[1];
      double[] sinr = new double[1];
      double[] cosl = new double[1];
      double[] cosr = new double[1];
      double[] single_values = new double[2];
      
      single_values[0] = values[0][0];
      single_values[1] = values[1][1];
      
      compute_2X2(values[0][0], values[0][1], values[1][1], single_values, sinl, cosl, sinr, cosr, 0);
      

      update_u(0, U, cosl, sinl);
      update_v(0, V, cosr, sinr);
      
      return 2;
    }
    


    return computeSVD(this, U, W, V);
  }
  



















  public final int LUD(GMatrix LU, GVector permutation)
  {
    int size = nRow * nCol;
    double[] temp = new double[size];
    int[] even_row_exchange = new int[1];
    int[] row_perm = new int[nRow];
    

    if (nRow != nCol)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix19"));
    }
    
    if (nRow != nRow)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
    }
    
    if (nCol != nCol)
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
    }
    
    if (nRow != permutation.getSize())
    {
      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix20"));
    }
    
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        temp[(i * nCol + j)] = values[i][j];
      }
    }
    

    if (!luDecomposition(nRow, temp, row_perm, even_row_exchange))
    {

      throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
    }
    
    for (i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = temp[(i * nCol + j)];
      }
    }
    
    for (i = 0; i < nRow; i++) {
      values[i] = row_perm[i];
    }
    
    return even_row_exchange[0];
  }
  


  public final void setScale(double scale)
  {
    int l;
    

    int l;
    
    if (nRow < nCol) {
      l = nRow;
    } else {
      l = nCol;
    }
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = 0.0D;
      }
    }
    
    for (i = 0; i < l; i++) {
      values[i][i] = scale;
    }
  }
  







  final void invertGeneral(GMatrix m1)
  {
    int size = nRow * nCol;
    double[] temp = new double[size];
    double[] result = new double[size];
    int[] row_perm = new int[nRow];
    int[] even_row_exchange = new int[1];
    



    if (nRow != nCol)
    {

      throw new MismatchedSizeException(VecMathI18N.getString("GMatrix22"));
    }
    

    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        temp[(i * nCol + j)] = values[i][j];
      }
    }
    

    if (!luDecomposition(nRow, temp, row_perm, even_row_exchange))
    {

      throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
    }
    

    for (i = 0; i < size; i++) {
      result[i] = 0.0D;
    }
    for (i = 0; i < nCol; i++) {
      result[(i + i * nCol)] = 1.0D;
    }
    luBacksubstitution(nRow, temp, row_perm, result);
    
    for (i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = result[(i * nCol + j)];
      }
    }
  }
  


















  static boolean luDecomposition(int dim, double[] matrix0, int[] row_perm, int[] even_row_xchg)
  {
    double[] row_scale = new double[dim];
    





    int ptr = 0;
    int rs = 0;
    even_row_xchg[0] = 1;
    

    int i = dim;
    while (i-- != 0) {
      double big = 0.0D;
      

      int j = dim;
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
    for (int j = 0; j < dim; j++)
    {




      for (i = 0; i < j; i++) {
        int target = mtx + dim * i + j;
        double sum = matrix0[target];
        int k = i;
        int p1 = mtx + dim * i;
        int p2 = mtx + j;
        while (k-- != 0) {
          sum -= matrix0[p1] * matrix0[p2];
          p1++;
          p2 += dim;
        }
        matrix0[target] = sum;
      }
      


      double big = 0.0D;
      int imax = -1;
      for (i = j; i < dim; i++) {
        int target = mtx + dim * i + j;
        double sum = matrix0[target];
        int k = j;
        int p1 = mtx + dim * i;
        int p2 = mtx + j;
        while (k-- != 0) {
          sum -= matrix0[p1] * matrix0[p2];
          p1++;
          p2 += dim;
        }
        matrix0[target] = sum;
        
        double temp;
        if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
          big = temp;
          imax = i;
        }
      }
      
      if (imax < 0) {
        throw new RuntimeException(VecMathI18N.getString("GMatrix24"));
      }
      

      if (j != imax)
      {
        int k = dim;
        int p1 = mtx + dim * imax;
        int p2 = mtx + dim * j;
        while (k-- != 0) {
          double temp = matrix0[p1];
          matrix0[(p1++)] = matrix0[p2];
          matrix0[(p2++)] = temp;
        }
        

        row_scale[imax] = row_scale[j];
        even_row_xchg[0] = (-even_row_xchg[0]);
      }
      

      row_perm[j] = imax;
      

      if (matrix0[(mtx + dim * j + j)] == 0.0D) {
        return false;
      }
      

      if (j != dim - 1) {
        double temp = 1.0D / matrix0[(mtx + dim * j + j)];
        int target = mtx + dim * (j + 1) + j;
        i = dim - 1 - j;
        while (i-- != 0) {
          matrix0[target] *= temp;
          target += dim;
        }
      }
    }
    

    return true;
  }
  


























  static void luBacksubstitution(int dim, double[] matrix1, int[] row_perm, double[] matrix2)
  {
    int rp = 0;
    

    for (int k = 0; k < dim; k++)
    {
      int cv = k;
      int ii = -1;
      

      for (int i = 0; i < dim; i++)
      {

        int ip = row_perm[(rp + i)];
        double sum = matrix2[(cv + dim * ip)];
        matrix2[(cv + dim * ip)] = matrix2[(cv + dim * i)];
        if (ii >= 0)
        {
          int rv = i * dim;
          for (int j = ii; j <= i - 1; j++) {
            sum -= matrix1[(rv + j)] * matrix2[(cv + dim * j)];
          }
        }
        if (sum != 0.0D) {
          ii = i;
        }
        matrix2[(cv + dim * i)] = sum;
      }
      

      for (i = 0; i < dim; i++) {
        int ri = dim - 1 - i;
        int rv = dim * ri;
        double tt = 0.0D;
        for (int j = 1; j <= i; j++) {
          tt += matrix1[(rv + dim - j)] * matrix2[(cv + dim * (dim - j))];
        }
        matrix2[(cv + dim * ri)] = ((matrix2[(cv + dim * ri)] - tt) / matrix1[(rv + ri)]);
      }
    }
  }
  






  static int computeSVD(GMatrix mat, GMatrix U, GMatrix W, GMatrix V)
  {
    GMatrix tmp = new GMatrix(nRow, nCol);
    GMatrix u = new GMatrix(nRow, nCol);
    GMatrix v = new GMatrix(nRow, nCol);
    GMatrix m = new GMatrix(mat);
    int eLength;
    int sLength;
    int eLength; if (nRow >= nCol) {
      int sLength = nCol;
      eLength = nCol - 1;
    } else {
      sLength = nRow;
      eLength = nRow; }
    int vecLength;
    int vecLength;
    if (nRow > nCol) {
      vecLength = nRow;
    } else {
      vecLength = nCol;
    }
    double[] vec = new double[vecLength];
    double[] single_values = new double[sLength];
    double[] e = new double[eLength];
    




    int rank = 0;
    
    U.setIdentity();
    V.setIdentity();
    
    int nr = nRow;
    int nc = nCol;
    

    for (int si = 0; si < sLength; si++)
    {

      if (nr > 1)
      {





        double mag = 0.0D;
        for (int i = 0; i < nr; i++) {
          mag += values[(i + si)][si] * values[(i + si)][si];
        }
        




        mag = Math.sqrt(mag);
        if (values[si][si] == 0.0D) {
          vec[0] = mag;
        } else {
          vec[0] = (values[si][si] + d_sign(mag, values[si][si]));
        }
        
        for (i = 1; i < nr; i++) {
          vec[i] = values[(si + i)][si];
        }
        
        double scale = 0.0D;
        for (i = 0; i < nr; i++)
        {


          scale += vec[i] * vec[i];
        }
        
        scale = 2.0D / scale;
        


        for (int j = si; j < nRow; j++) {
          for (int k = si; k < nRow; k++) {
            values[j][k] = (-scale * vec[(j - si)] * vec[(k - si)]);
          }
        }
        
        for (i = si; i < nRow; i++) {
          values[i][i] += 1.0D;
        }
        

        double t = 0.0D;
        for (i = si; i < nRow; i++) {
          t += values[si][i] * values[i][si];
        }
        values[si][si] = t;
        

        for (j = si; j < nRow; j++) {
          for (int k = si + 1; k < nCol; k++) {
            values[j][k] = 0.0D;
            for (i = si; i < nCol; i++) {
              values[j][k] += values[j][i] * values[i][k];
            }
          }
        }
        
        for (j = si; j < nRow; j++) {
          for (int k = si + 1; k < nCol; k++) {
            values[j][k] = values[j][k];
          }
        }
        






        for (j = si; j < nRow; j++) {
          for (int k = 0; k < nCol; k++) {
            values[j][k] = 0.0D;
            for (i = si; i < nCol; i++) {
              values[j][k] += values[j][i] * values[i][k];
            }
          }
        }
        
        for (j = si; j < nRow; j++) {
          for (int k = 0; k < nCol; k++) {
            values[j][k] = values[j][k];
          }
        }
        







        nr--;
      }
      
      if (nc > 2)
      {




        double mag = 0.0D;
        for (int i = 1; i < nc; i++) {
          mag += values[si][(si + i)] * values[si][(si + i)];
        }
        





        mag = Math.sqrt(mag);
        if (values[si][(si + 1)] == 0.0D) {
          vec[0] = mag;
        }
        else {
          vec[0] = (values[si][(si + 1)] + d_sign(mag, values[si][(si + 1)]));
        }
        
        for (i = 1; i < nc - 1; i++) {
          vec[i] = values[si][(si + i + 1)];
        }
        

        double scale = 0.0D;
        for (i = 0; i < nc - 1; i++)
        {
          scale += vec[i] * vec[i];
        }
        
        scale = 2.0D / scale;
        


        for (int j = si + 1; j < nc; j++) {
          for (int k = si + 1; k < nCol; k++) {
            values[j][k] = (-scale * vec[(j - si - 1)] * vec[(k - si - 1)]);
          }
        }
        
        for (i = si + 1; i < nCol; i++) {
          values[i][i] += 1.0D;
        }
        
        double t = 0.0D;
        for (i = si; i < nCol; i++) {
          t += values[i][(si + 1)] * values[si][i];
        }
        values[si][(si + 1)] = t;
        

        for (j = si + 1; j < nRow; j++) {
          for (int k = si + 1; k < nCol; k++) {
            values[j][k] = 0.0D;
            for (i = si + 1; i < nCol; i++) {
              values[j][k] += values[i][k] * values[j][i];
            }
          }
        }
        
        for (j = si + 1; j < nRow; j++) {
          for (int k = si + 1; k < nCol; k++) {
            values[j][k] = values[j][k];
          }
        }
        







        for (j = 0; j < nRow; j++) {
          for (int k = si + 1; k < nCol; k++) {
            values[j][k] = 0.0D;
            for (i = si + 1; i < nCol; i++) {
              values[j][k] += values[i][k] * values[j][i];
            }
          }
        }
        



        for (j = 0; j < nRow; j++) {
          for (int k = si + 1; k < nCol; k++) {
            values[j][k] = values[j][k];
          }
        }
        





        nc--;
      }
    }
    
    for (int i = 0; i < sLength; i++) {
      single_values[i] = values[i][i];
    }
    
    for (i = 0; i < eLength; i++) {
      e[i] = values[i][(i + 1)];
    }
    










    if ((nRow == 2) && (nCol == 2)) {
      double[] cosl = new double[1];
      double[] cosr = new double[1];
      double[] sinl = new double[1];
      double[] sinr = new double[1];
      
      compute_2X2(single_values[0], e[0], single_values[1], single_values, sinl, cosl, sinr, cosr, 0);
      

      update_u(0, U, cosl, sinl);
      update_v(0, V, cosr, sinr);
      
      return 2;
    }
    

    compute_qr(0, e.length - 1, single_values, e, U, V);
    

    rank = single_values.length;
    


    return rank;
  }
  




  static void compute_qr(int start, int end, double[] s, double[] e, GMatrix u, GMatrix v)
  {
    double[] cosl = new double[1];
    double[] cosr = new double[1];
    double[] sinl = new double[1];
    double[] sinr = new double[1];
    GMatrix m = new GMatrix(nCol, nRow);
    
    int MAX_INTERATIONS = 2;
    double CONVERGE_TOL = 4.89E-15D;
    






















    double c_b48 = 1.0D;
    double c_b71 = -1.0D;
    boolean converged = false;
    



    double f = 0.0D;
    double g = 0.0D;
    
    for (int k = 0; (k < 2) && (!converged); k++) {
      for (int i = start; i <= end; i++)
      {

        if (i == start) { int sl;
          int sl; if (e.length == s.length) {
            sl = end;
          } else {
            sl = end + 1;
          }
          double shift = compute_shift(s[(sl - 1)], e[end], s[sl]);
          

          f = (Math.abs(s[i]) - shift) * (d_sign(c_b48, s[i]) + shift / s[i]);
          g = e[i];
        }
        
        double r = compute_rot(f, g, sinr, cosr);
        if (i != start) {
          e[(i - 1)] = r;
        }
        f = cosr[0] * s[i] + sinr[0] * e[i];
        e[i] = (cosr[0] * e[i] - sinr[0] * s[i]);
        g = sinr[0] * s[(i + 1)];
        s[(i + 1)] = (cosr[0] * s[(i + 1)]);
        

        update_v(i, v, cosr, sinr);
        


        r = compute_rot(f, g, sinl, cosl);
        s[i] = r;
        f = cosl[0] * e[i] + sinl[0] * s[(i + 1)];
        s[(i + 1)] = (cosl[0] * s[(i + 1)] - sinl[0] * e[i]);
        
        if (i < end)
        {
          g = sinl[0] * e[(i + 1)];
          e[(i + 1)] = (cosl[0] * e[(i + 1)]);
        }
        

        update_u(i, u, cosl, sinl);
      }
      



      if (s.length == e.length) {
        double r = compute_rot(f, g, sinr, cosr);
        f = cosr[0] * s[i] + sinr[0] * e[i];
        e[i] = (cosr[0] * e[i] - sinr[0] * s[i]);
        s[(i + 1)] = (cosr[0] * s[(i + 1)]);
        
        update_v(i, v, cosr, sinr);
      }
      










      while ((end - start > 1) && (Math.abs(e[end]) < 4.89E-15D)) {
        end--;
      }
      

      for (int n = end - 2; n > start; n--) {
        if (Math.abs(e[n]) < 4.89E-15D) {
          compute_qr(n + 1, end, s, e, u, v);
          end = n - 1;
          

          while ((end - start > 1) && 
            (Math.abs(e[end]) < 4.89E-15D)) {
            end--;
          }
        }
      }
      



      if ((end - start <= 1) && (Math.abs(e[(start + 1)]) < 4.89E-15D)) {
        converged = true;
      }
    }
    






    if (Math.abs(e[1]) < 4.89E-15D) {
      compute_2X2(s[start], e[start], s[(start + 1)], s, sinl, cosl, sinr, cosr, 0);
      
      e[start] = 0.0D;
      e[(start + 1)] = 0.0D;
    }
    

    int i = start;
    update_u(i, u, cosl, sinl);
    update_v(i, v, cosr, sinr);
  }
  







  private static void print_se(double[] s, double[] e)
  {
    System.out.println("\ns =" + s[0] + " " + s[1] + " " + s[2]);
    System.out.println("e =" + e[0] + " " + e[1]);
  }
  



  private static void update_v(int index, GMatrix v, double[] cosr, double[] sinr)
  {
    for (int j = 0; j < nRow; j++) {
      double vtemp = values[j][index];
      values[j][index] = (cosr[0] * vtemp + sinr[0] * values[j][(index + 1)]);
      
      values[j][(index + 1)] = (-sinr[0] * vtemp + cosr[0] * values[j][(index + 1)]);
    }
  }
  

  private static void chase_up(double[] s, double[] e, int k, GMatrix v)
  {
    double[] cosr = new double[1];
    double[] sinr = new double[1];
    
    GMatrix t = new GMatrix(nRow, nCol);
    GMatrix m = new GMatrix(nRow, nCol);
    










    double f = e[k];
    double g = s[k];
    
    for (int i = k; i > 0; i--) {
      double r = compute_rot(f, g, sinr, cosr);
      f = -e[(i - 1)] * sinr[0];
      g = s[(i - 1)];
      s[i] = r;
      e[(i - 1)] *= cosr[0];
      update_v_split(i, k + 1, v, cosr, sinr, t, m);
    }
    
    s[(i + 1)] = compute_rot(f, g, sinr, cosr);
    update_v_split(i, k + 1, v, cosr, sinr, t, m);
  }
  
  private static void chase_across(double[] s, double[] e, int k, GMatrix u)
  {
    double[] cosl = new double[1];
    double[] sinl = new double[1];
    
    GMatrix t = new GMatrix(nRow, nCol);
    GMatrix m = new GMatrix(nRow, nCol);
    










    double g = e[k];
    double f = s[(k + 1)];
    
    for (int i = k; i < nCol - 2; i++) {
      double r = compute_rot(f, g, sinl, cosl);
      g = -e[(i + 1)] * sinl[0];
      f = s[(i + 2)];
      s[(i + 1)] = r;
      e[(i + 1)] *= cosl[0];
      update_u_split(k, i + 1, u, cosl, sinl, t, m);
    }
    
    s[(i + 1)] = compute_rot(f, g, sinl, cosl);
    update_u_split(k, i + 1, u, cosl, sinl, t, m);
  }
  




  private static void update_v_split(int topr, int bottomr, GMatrix v, double[] cosr, double[] sinr, GMatrix t, GMatrix m)
  {
    for (int j = 0; j < nRow; j++) {
      double vtemp = values[j][topr];
      values[j][topr] = (cosr[0] * vtemp - sinr[0] * values[j][bottomr]);
      values[j][bottomr] = (sinr[0] * vtemp + cosr[0] * values[j][bottomr]);
    }
    











    System.out.println("topr    =" + topr);
    System.out.println("bottomr =" + bottomr);
    System.out.println("cosr =" + cosr[0]);
    System.out.println("sinr =" + sinr[0]);
    System.out.println("\nm =");
    checkMatrix(m);
    System.out.println("\nv =");
    checkMatrix(t);
    m.mul(m, t);
    System.out.println("\nt*m =");
    checkMatrix(m);
  }
  




  private static void update_u_split(int topr, int bottomr, GMatrix u, double[] cosl, double[] sinl, GMatrix t, GMatrix m)
  {
    for (int j = 0; j < nCol; j++) {
      double utemp = values[topr][j];
      values[topr][j] = (cosl[0] * utemp - sinl[0] * values[bottomr][j]);
      values[bottomr][j] = (sinl[0] * utemp + cosl[0] * values[bottomr][j]);
    }
    










    System.out.println("\nm=");
    checkMatrix(m);
    System.out.println("\nu=");
    checkMatrix(t);
    m.mul(t, m);
    System.out.println("\nt*m=");
    checkMatrix(m);
  }
  



  private static void update_u(int index, GMatrix u, double[] cosl, double[] sinl)
  {
    for (int j = 0; j < nCol; j++) {
      double utemp = values[index][j];
      values[index][j] = (cosl[0] * utemp + sinl[0] * values[(index + 1)][j]);
      
      values[(index + 1)][j] = (-sinl[0] * utemp + cosl[0] * values[(index + 1)][j]);
    }
  }
  
  private static void print_m(GMatrix m, GMatrix u, GMatrix v)
  {
    GMatrix mtmp = new GMatrix(nCol, nRow);
    
    mtmp.mul(u, mtmp);
    mtmp.mul(mtmp, v);
    System.out.println("\n m = \n" + toString(mtmp));
  }
  

  private static String toString(GMatrix m)
  {
    StringBuffer buffer = new StringBuffer(nRow * nCol * 8);
    

    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        if (Math.abs(values[i][j]) < 1.0E-9D) {
          buffer.append("0.0000 ");
        } else {
          buffer.append(values[i][j]).append(" ");
        }
      }
      buffer.append("\n");
    }
    return buffer.toString();
  }
  

  private static void print_svd(double[] s, double[] e, GMatrix u, GMatrix v)
  {
    GMatrix mtmp = new GMatrix(nCol, nRow);
    
    System.out.println(" \ns = ");
    for (int i = 0; i < s.length; i++) {
      System.out.println(" " + s[i]);
    }
    
    System.out.println(" \ne = ");
    for (i = 0; i < e.length; i++) {
      System.out.println(" " + e[i]);
    }
    
    System.out.println(" \nu  = \n" + u.toString());
    System.out.println(" \nv  = \n" + v.toString());
    
    mtmp.setIdentity();
    for (i = 0; i < s.length; i++) {
      values[i][i] = s[i];
    }
    for (i = 0; i < e.length; i++) {
      values[i][(i + 1)] = e[i];
    }
    System.out.println(" \nm  = \n" + mtmp.toString());
    
    mtmp.mulTransposeLeft(u, mtmp);
    mtmp.mulTransposeRight(mtmp, v);
    
    System.out.println(" \n u.transpose*m*v.transpose  = \n" + mtmp
      .toString());
  }
  
  static double max(double a, double b) {
    if (a > b) {
      return a;
    }
    return b;
  }
  
  static double min(double a, double b) {
    if (a < b) {
      return a;
    }
    return b;
  }
  



  static double compute_shift(double f, double g, double h)
  {
    double fa = Math.abs(f);
    double ga = Math.abs(g);
    double ha = Math.abs(h);
    double fhmn = min(fa, ha);
    double fhmx = max(fa, ha);
    double ssmin;
    if (fhmn == 0.0D) {
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
    if (ga == 0.0D) {
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
        if (fa / ga < 1.0E-10D) {
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
      if (gasmal) {
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
          if (fa / ga < 1.0E-10D) {
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
        if (gasmal) {
          d = fa - ha;
          if (d == fa) {
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
          
          if (mm == 0.0D) {
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
      
      if (pmax == 1)
      {
        tsign = d_sign(c_b4, csr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, f);
      }
      if (pmax == 2)
      {
        tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, g);
      }
      if (pmax == 3)
      {
        tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, snl[0]) * d_sign(c_b4, h);
      }
      
      single_values[index] = d_sign(ssmax, tsign);
      double d__1 = tsign * d_sign(c_b4, f) * d_sign(c_b4, h);
      single_values[(index + 1)] = d_sign(ssmin, d__1);
    }
    
    return 0;
  }
  







  static double compute_rot(double f, double g, double[] sin, double[] cos)
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
    sin[0] = sn;
    cos[0] = cs;
    return r;
  }
  
  static double d_sign(double a, double b)
  {
    double x = a >= 0.0D ? a : -a;
    return b >= 0.0D ? x : -x;
  }
  







  public Object clone()
  {
    GMatrix m1 = null;
    try {
      m1 = (GMatrix)super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
    

    values = new double[nRow][nCol];
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        values[i][j] = values[i][j];
      }
    }
    
    return m1;
  }
}
