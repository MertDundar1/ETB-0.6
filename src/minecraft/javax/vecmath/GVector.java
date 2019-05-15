package javax.vecmath;

import java.io.Serializable;













































public class GVector
  implements Serializable, Cloneable
{
  private int length;
  double[] values;
  static final long serialVersionUID = 1398850036893875112L;
  
  public GVector(int length)
  {
    this.length = length;
    values = new double[length];
    for (int i = 0; i < length; i++) { values[i] = 0.0D;
    }
  }
  








  public GVector(double[] vector)
  {
    length = vector.length;
    values = new double[vector.length];
    for (int i = 0; i < length; i++) { values[i] = vector[i];
    }
  }
  






  public GVector(GVector vector)
  {
    values = new double[length];
    length = length;
    for (int i = 0; i < length; i++) { values[i] = values[i];
    }
  }
  




  public GVector(Tuple2f tuple)
  {
    values = new double[2];
    values[0] = x;
    values[1] = y;
    length = 2;
  }
  





  public GVector(Tuple3f tuple)
  {
    values = new double[3];
    values[0] = x;
    values[1] = y;
    values[2] = z;
    length = 3;
  }
  





  public GVector(Tuple3d tuple)
  {
    values = new double[3];
    values[0] = x;
    values[1] = y;
    values[2] = z;
    length = 3;
  }
  





  public GVector(Tuple4f tuple)
  {
    values = new double[4];
    values[0] = x;
    values[1] = y;
    values[2] = z;
    values[3] = w;
    length = 4;
  }
  





  public GVector(Tuple4d tuple)
  {
    values = new double[4];
    values[0] = x;
    values[1] = y;
    values[2] = z;
    values[3] = w;
    length = 4;
  }
  











  public GVector(double[] vector, int length)
  {
    this.length = length;
    values = new double[length];
    for (int i = 0; i < length; i++) {
      values[i] = vector[i];
    }
  }
  






  public final double norm()
  {
    double sq = 0.0D;
    

    for (int i = 0; i < length; i++) {
      sq += values[i] * values[i];
    }
    
    return Math.sqrt(sq);
  }
  






  public final double normSquared()
  {
    double sq = 0.0D;
    

    for (int i = 0; i < length; i++) {
      sq += values[i] * values[i];
    }
    
    return sq;
  }
  




  public final void normalize(GVector v1)
  {
    double sq = 0.0D;
    

    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector0"));
    }
    for (int i = 0; i < length; i++) {
      sq += values[i] * values[i];
    }
    

    double invMag = 1.0D / Math.sqrt(sq);
    
    for (i = 0; i < length; i++) {
      values[i] *= invMag;
    }
  }
  




  public final void normalize()
  {
    double sq = 0.0D;
    

    for (int i = 0; i < length; i++) {
      sq += values[i] * values[i];
    }
    

    double invMag = 1.0D / Math.sqrt(sq);
    
    for (i = 0; i < length; i++) {
      values[i] *= invMag;
    }
  }
  








  public final void scale(double s, GVector v1)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector1"));
    }
    for (int i = 0; i < length; i++) {
      values[i] *= s;
    }
  }
  






  public final void scale(double s)
  {
    for (int i = 0; i < length; i++) {
      values[i] *= s;
    }
  }
  










  public final void scaleAdd(double s, GVector v1, GVector v2)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector2"));
    }
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector3"));
    }
    for (int i = 0; i < length; i++) {
      values[i] = (values[i] * s + values[i]);
    }
  }
  







  public final void add(GVector vector)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector4"));
    }
    for (int i = 0; i < length; i++) {
      values[i] += values[i];
    }
  }
  








  public final void add(GVector vector1, GVector vector2)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector5"));
    }
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector6"));
    }
    for (int i = 0; i < length; i++) {
      values[i] += values[i];
    }
  }
  






  public final void sub(GVector vector)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector7"));
    }
    for (int i = 0; i < length; i++) {
      values[i] -= values[i];
    }
  }
  









  public final void sub(GVector vector1, GVector vector2)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector8"));
    }
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector9"));
    }
    for (int i = 0; i < length; i++) {
      values[i] -= values[i];
    }
  }
  




  public final void mul(GMatrix m1, GVector v1)
  {
    if (m1.getNumCol() != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector10"));
    }
    if (length != m1.getNumRow())
      throw new MismatchedSizeException(VecMathI18N.getString("GVector11"));
    double[] v;
    double[] v;
    if (v1 != this) {
      v = values;
    } else {
      v = (double[])values.clone();
    }
    
    for (int j = length - 1; j >= 0; j--) {
      values[j] = 0.0D;
      for (int i = length - 1; i >= 0; i--) {
        values[j] += values[j][i] * v[i];
      }
    }
  }
  









  public final void mul(GVector v1, GMatrix m1)
  {
    if (m1.getNumRow() != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector12"));
    }
    if (length != m1.getNumCol())
      throw new MismatchedSizeException(VecMathI18N.getString("GVector13"));
    double[] v;
    double[] v;
    if (v1 != this) {
      v = values;
    } else {
      v = (double[])values.clone();
    }
    
    for (int j = length - 1; j >= 0; j--) {
      values[j] = 0.0D;
      for (int i = length - 1; i >= 0; i--) {
        values[j] += values[i][j] * v[i];
      }
    }
  }
  


  public final void negate()
  {
    for (int i = length - 1; i >= 0; i--) {
      values[i] *= -1.0D;
    }
  }
  


  public final void zero()
  {
    for (int i = 0; i < length; i++) {
      values[i] = 0.0D;
    }
  }
  





  public final void setSize(int length)
  {
    double[] tmp = new double[length];
    int max;
    int max;
    if (this.length < length) {
      max = this.length;
    } else {
      max = length;
    }
    for (int i = 0; i < max; i++) {
      tmp[i] = values[i];
    }
    this.length = length;
    
    values = tmp;
  }
  






  public final void set(double[] vector)
  {
    for (int i = length - 1; i >= 0; i--) {
      values[i] = vector[i];
    }
  }
  




  public final void set(GVector vector)
  {
    if (length < length) {
      length = length;
      values = new double[length];
      for (int i = 0; i < length; i++)
        values[i] = values[i];
    }
    for (int i = 0; i < length; i++)
      values[i] = values[i];
    for (i = length; i < length; i++) {
      values[i] = 0.0D;
    }
  }
  




  public final void set(Tuple2f tuple)
  {
    if (length < 2) {
      length = 2;
      values = new double[2];
    }
    values[0] = x;
    values[1] = y;
    for (int i = 2; i < length; i++) { values[i] = 0.0D;
    }
  }
  




  public final void set(Tuple3f tuple)
  {
    if (length < 3) {
      length = 3;
      values = new double[3];
    }
    values[0] = x;
    values[1] = y;
    values[2] = z;
    for (int i = 3; i < length; i++) { values[i] = 0.0D;
    }
  }
  



  public final void set(Tuple3d tuple)
  {
    if (length < 3) {
      length = 3;
      values = new double[3];
    }
    values[0] = x;
    values[1] = y;
    values[2] = z;
    for (int i = 3; i < length; i++) { values[i] = 0.0D;
    }
  }
  



  public final void set(Tuple4f tuple)
  {
    if (length < 4) {
      length = 4;
      values = new double[4];
    }
    values[0] = x;
    values[1] = y;
    values[2] = z;
    values[3] = w;
    for (int i = 4; i < length; i++) { values[i] = 0.0D;
    }
  }
  



  public final void set(Tuple4d tuple)
  {
    if (length < 4) {
      length = 4;
      values = new double[4];
    }
    values[0] = x;
    values[1] = y;
    values[2] = z;
    values[3] = w;
    for (int i = 4; i < length; i++) { values[i] = 0.0D;
    }
  }
  



  public final int getSize()
  {
    return values.length;
  }
  





  public final double getElement(int index)
  {
    return values[index];
  }
  






  public final void setElement(int index, double value)
  {
    values[index] = value;
  }
  



  public String toString()
  {
    StringBuffer buffer = new StringBuffer(length * 8);
    


    for (int i = 0; i < length; i++) {
      buffer.append(values[i]).append(" ");
    }
    
    return buffer.toString();
  }
  










  public int hashCode()
  {
    long bits = 1L;
    
    for (int i = 0; i < length; i++) {
      bits = 31L * bits + VecMathUtil.doubleToLongBits(values[i]);
    }
    
    return (int)(bits ^ bits >> 32);
  }
  






  public boolean equals(GVector vector1)
  {
    try
    {
      if (length != length) { return false;
      }
      for (int i = 0; i < length; i++) {
        if (values[i] != values[i]) { return false;
        }
      }
      return true;
    } catch (NullPointerException e2) {}
    return false;
  }
  






  public boolean equals(Object o1)
  {
    try
    {
      GVector v2 = (GVector)o1;
      
      if (length != length) { return false;
      }
      for (int i = 0; i < length; i++) {
        if (values[i] != values[i]) return false;
      }
      return true;
    } catch (ClassCastException e1) {
      return false; } catch (NullPointerException e2) {}
    return false;
  }
  












  public boolean epsilonEquals(GVector v1, double epsilon)
  {
    if (length != length) { return false;
    }
    for (int i = 0; i < length; i++) {
      double diff = values[i] - values[i];
      if ((diff < 0.0D ? -diff : diff) > epsilon) return false;
    }
    return true;
  }
  





  public final double dot(GVector v1)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector14"));
    }
    double result = 0.0D;
    for (int i = 0; i < length; i++) {
      result += values[i] * values[i];
    }
    return result;
  }
  












  public final void SVDBackSolve(GMatrix U, GMatrix W, GMatrix V, GVector b)
  {
    if ((nRow != b.getSize()) || (nRow != nCol) || (nRow != nRow))
    {

      throw new MismatchedSizeException(VecMathI18N.getString("GVector15"));
    }
    
    if ((nCol != values.length) || (nCol != nCol) || (nCol != nRow))
    {

      throw new MismatchedSizeException(VecMathI18N.getString("GVector23"));
    }
    
    GMatrix tmp = new GMatrix(nRow, nCol);
    tmp.mul(U, V);
    tmp.mulTransposeRight(U, W);
    tmp.invert();
    mul(tmp, b);
  }
  













  public final void LUDBackSolve(GMatrix LU, GVector b, GVector permutation)
  {
    int size = nRow * nCol;
    
    double[] temp = new double[size];
    double[] result = new double[size];
    int[] row_perm = new int[b.getSize()];
    

    if (nRow != b.getSize()) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector16"));
    }
    
    if (nRow != permutation.getSize()) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector24"));
    }
    
    if (nRow != nCol) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector25"));
    }
    
    for (int i = 0; i < nRow; i++) {
      for (int j = 0; j < nCol; j++) {
        temp[(i * nCol + j)] = values[i][j];
      }
    }
    
    for (i = 0; i < size; i++) result[i] = 0.0D;
    for (i = 0; i < nRow; i++) result[(i * nCol)] = values[i];
    for (i = 0; i < nCol; i++) { row_perm[i] = ((int)values[i]);
    }
    GMatrix.luBacksubstitution(nRow, temp, row_perm, result);
    
    for (i = 0; i < nRow; i++) { values[i] = result[(i * nCol)];
    }
  }
  






  public final double angle(GVector v1)
  {
    return Math.acos(dot(v1) / (norm() * v1.norm()));
  }
  
  /**
   * @deprecated
   */
  public final void interpolate(GVector v1, GVector v2, float alpha)
  {
    interpolate(v1, v2, alpha);
  }
  
  /**
   * @deprecated
   */
  public final void interpolate(GVector v1, float alpha)
  {
    interpolate(v1, alpha);
  }
  








  public final void interpolate(GVector v1, GVector v2, double alpha)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector20"));
    }
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector21"));
    }
    for (int i = 0; i < length; i++) {
      values[i] = ((1.0D - alpha) * values[i] + alpha * values[i]);
    }
  }
  






  public final void interpolate(GVector v1, double alpha)
  {
    if (length != length) {
      throw new MismatchedSizeException(VecMathI18N.getString("GVector22"));
    }
    for (int i = 0; i < length; i++) {
      values[i] = ((1.0D - alpha) * values[i] + alpha * values[i]);
    }
  }
  







  public Object clone()
  {
    GVector v1 = null;
    try {
      v1 = (GVector)super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
    

    values = new double[length];
    for (int i = 0; i < length; i++) {
      values[i] = values[i];
    }
    
    return v1;
  }
}
