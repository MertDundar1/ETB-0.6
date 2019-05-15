package com.enjoytheban.utils.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;


public final class GLUProjection
{
  private static GLUProjection instance;
  private final FloatBuffer coords = BufferUtils.createFloatBuffer(3);
  private IntBuffer viewport;
  private FloatBuffer modelview;
  private FloatBuffer projection;
  private Vector3D frustumPos;
  private Vector3D[] frustum;
  private Vector3D[] invFrustum;
  private Vector3D viewVec;
  private double displayWidth;
  private double displayHeight;
  private double widthScale;
  private double heightScale;
  private double bra;
  private double bla;
  private double tra;
  private double tla;
  private Line tb;
  private Line bb;
  private Line lb;
  private Line rb;
  private float fovY;
  private float fovX;
  private Vector3D lookVec;
  
  public GLUProjection() {}
  
  public static GLUProjection getInstance() { if (instance == null) {
      instance = new GLUProjection();
    }
    return instance;
  }
  
  public void updateMatrices(IntBuffer viewport, FloatBuffer modelview, FloatBuffer projection, double widthScale, double heightScale)
  {
    this.viewport = viewport;
    this.modelview = modelview;
    this.projection = projection;
    this.widthScale = widthScale;
    this.heightScale = heightScale;
    
    float fov = (float)Math.toDegrees(Math.atan(1.0D / this.projection.get(5)) * 2.0D);
    fovY = fov;
    displayWidth = this.viewport.get(2);
    displayHeight = this.viewport.get(3);
    fovX = ((float)Math.toDegrees(2.0D * Math.atan(displayWidth / displayHeight * Math.tan(Math.toRadians(fovY) / 2.0D))));
    
    Vector3D ft = new Vector3D(this.modelview.get(12), this.modelview.get(13), this.modelview.get(14));
    Vector3D lv = new Vector3D(this.modelview.get(0), this.modelview.get(1), this.modelview.get(2));
    Vector3D uv = new Vector3D(this.modelview.get(4), this.modelview.get(5), this.modelview.get(6));
    Vector3D fv = new Vector3D(this.modelview.get(8), this.modelview.get(9), this.modelview.get(10));
    
    Vector3D nuv = new Vector3D(0.0D, 1.0D, 0.0D);
    Vector3D nlv = new Vector3D(1.0D, 0.0D, 0.0D);
    Vector3D nfv = new Vector3D(0.0D, 0.0D, 1.0D);
    
    double yaw = Math.toDegrees(Math.atan2(nlv.cross(lv).length(), nlv.dot(lv))) + 180.0D;
    if (x < 0.0D) {
      yaw = 360.0D - yaw;
    }
    double pitch = 0.0D;
    if (((-y > 0.0D) && (yaw >= 90.0D) && (yaw < 270.0D)) || ((y > 0.0D) && ((yaw < 90.0D) || (yaw >= 270.0D)))) {
      pitch = Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv)));
    } else {
      pitch = -Math.toDegrees(Math.atan2(nuv.cross(uv).length(), nuv.dot(uv)));
    }
    lookVec = getRotationVector(yaw, pitch);
    
    Matrix4f modelviewMatrix = new Matrix4f();
    modelviewMatrix.load(this.modelview.asReadOnlyBuffer());
    modelviewMatrix.invert();
    
    frustumPos = new Vector3D(m30, m31, m32);
    frustum = getFrustum(frustumPos.x, frustumPos.y, frustumPos.z, yaw, pitch, fov, 1.0D, displayWidth / displayHeight);
    invFrustum = getFrustum(frustumPos.x, frustumPos.y, frustumPos.z, yaw - 180.0D, -pitch, fov, 1.0D, displayWidth / displayHeight);
    
    viewVec = getRotationVector(yaw, pitch).normalized();
    
    bra = Math.toDegrees(Math.acos(displayHeight * heightScale / Math.sqrt(displayWidth * widthScale * displayWidth * widthScale + displayHeight * heightScale * displayHeight * heightScale)));
    bla = (360.0D - bra);
    tra = (bla - 180.0D);
    tla = (bra + 180.0D);
    
    rb = new Line(displayWidth * this.widthScale, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D);
    tb = new Line(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D);
    lb = new Line(0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D);
    bb = new Line(0.0D, displayHeight * this.heightScale, 0.0D, 1.0D, 0.0D, 0.0D);
  }
  
  public Projection project(double x, double y, double z, ClampMode clampModeOutside, boolean extrudeInverted)
  {
    if ((viewport != null) && (modelview != null) && (projection != null))
    {
      Vector3D posVec = new Vector3D(x, y, z);
      boolean[] frustum = doFrustumCheck(this.frustum, frustumPos, x, y, z);
      boolean outsideFrustum = (frustum[0] != 0) || (frustum[1] != 0) || (frustum[2] != 0) || (frustum[3] != 0);
      if (outsideFrustum)
      {
        boolean opposite = posVec.sub(frustumPos).dot(viewVec) <= 0.0D;
        
        boolean[] invFrustum = doFrustumCheck(this.invFrustum, frustumPos, x, y, z);
        boolean outsideInvertedFrustum = (invFrustum[0] != 0) || (invFrustum[1] != 0) || (invFrustum[2] != 0) || (invFrustum[3] != 0); if (((extrudeInverted) && (!outsideInvertedFrustum)) || ((outsideInvertedFrustum) && (clampModeOutside != ClampMode.NONE)))
        {
          if (((extrudeInverted) && (!outsideInvertedFrustum)) || ((clampModeOutside == ClampMode.DIRECT) && (outsideInvertedFrustum)))
          {
            double vecX = 0.0D;
            double vecY = 0.0D;
            if (GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, coords))
            {
              if (opposite)
              {
                vecX = displayWidth * widthScale - coords.get(0) * widthScale - displayWidth * widthScale / 2.0D;
                vecY = displayHeight * heightScale - (displayHeight - coords.get(1)) * heightScale - displayHeight * heightScale / 2.0D;
              }
              else
              {
                vecX = coords.get(0) * widthScale - displayWidth * widthScale / 2.0D;
                vecY = (displayHeight - coords.get(1)) * heightScale - displayHeight * heightScale / 2.0D;
              }
            }
            else {
              return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
            }
            Vector3D vec = new Vector3D(vecX, vecY, 0.0D).snormalize();
            vecX = x;
            vecY = y;
            
            Line vectorLine = new Line(displayWidth * widthScale / 2.0D, displayHeight * heightScale / 2.0D, 0.0D, vecX, vecY, 0.0D);
            
            double angle = Math.toDegrees(Math.acos(y / Math.sqrt(x * x + y * y)));
            if (vecX < 0.0D) {
              angle = 360.0D - angle;
            }
            Vector3D intersect = new Vector3D(0.0D, 0.0D, 0.0D);
            if ((angle >= bra) && (angle < tra)) {
              intersect = rb.intersect(vectorLine);
            } else if ((angle >= tra) && (angle < tla)) {
              intersect = tb.intersect(vectorLine);
            } else if ((angle >= tla) && (angle < bla)) {
              intersect = lb.intersect(vectorLine);
            } else {
              intersect = bb.intersect(vectorLine);
            }
            return new Projection(x, y, outsideInvertedFrustum ? GLUProjection.Projection.Type.OUTSIDE : GLUProjection.Projection.Type.INVERTED);
          }
          if ((clampModeOutside == ClampMode.ORTHOGONAL) && (outsideInvertedFrustum))
          {
            if (GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, coords))
            {
              double guiX = coords.get(0) * widthScale;
              double guiY = (displayHeight - coords.get(1)) * heightScale;
              if (opposite)
              {
                guiX = displayWidth * widthScale - guiX;
                guiY = displayHeight * heightScale - guiY;
              }
              if (guiX < 0.0D) {
                guiX = 0.0D;
              } else if (guiX > displayWidth * widthScale) {
                guiX = displayWidth * widthScale;
              }
              if (guiY < 0.0D) {
                guiY = 0.0D;
              } else if (guiY > displayHeight * heightScale) {
                guiY = displayHeight * heightScale;
              }
              return new Projection(guiX, guiY, outsideInvertedFrustum ? GLUProjection.Projection.Type.OUTSIDE : GLUProjection.Projection.Type.INVERTED);
            }
            return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
          }
        }
        else
        {
          if (GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, coords))
          {
            double guiX = coords.get(0) * widthScale;
            double guiY = (displayHeight - coords.get(1)) * heightScale;
            if (opposite)
            {
              guiX = displayWidth * widthScale - guiX;
              guiY = displayHeight * heightScale - guiY;
            }
            return new Projection(guiX, guiY, outsideInvertedFrustum ? GLUProjection.Projection.Type.OUTSIDE : GLUProjection.Projection.Type.INVERTED);
          }
          return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
        }
      }
      else
      {
        if (GLU.gluProject((float)x, (float)y, (float)z, modelview, projection, viewport, coords))
        {
          double guiX = coords.get(0) * widthScale;
          double guiY = (displayHeight - coords.get(1)) * heightScale;
          return new Projection(guiX, guiY, GLUProjection.Projection.Type.INSIDE);
        }
        return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
      }
    }
    return new Projection(0.0D, 0.0D, GLUProjection.Projection.Type.FAIL);
  }
  
  public boolean[] doFrustumCheck(Vector3D[] frustumCorners, Vector3D frustumPos, double x, double y, double z)
  {
    Vector3D point = new Vector3D(x, y, z);
    boolean c1 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[3], frustumCorners[0] }, point);
    boolean c2 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[0], frustumCorners[1] }, point);
    boolean c3 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[1], frustumCorners[2] }, point);
    boolean c4 = crossPlane(new Vector3D[] { frustumPos, frustumCorners[2], frustumCorners[3] }, point);
    return new boolean[] { c1, c2, c3, c4 };
  }
  
  public boolean crossPlane(Vector3D[] plane, Vector3D point)
  {
    Vector3D z = new Vector3D(0.0D, 0.0D, 0.0D);
    Vector3D e0 = plane[1].sub(plane[0]);
    Vector3D e1 = plane[2].sub(plane[0]);
    Vector3D normal = e0.cross(e1).snormalize();
    double D = z.sub(normal).dot(plane[2]);
    double dist = normal.dot(point) + D;
    return dist >= 0.0D;
  }
  
  public Vector3D[] getFrustum(double x, double y, double z, double rotationYaw, double rotationPitch, double fov, double farDistance, double aspectRatio)
  {
    Vector3D viewVec = getRotationVector(rotationYaw, rotationPitch).snormalize();
    double hFar = 2.0D * Math.tan(Math.toRadians(fov / 2.0D)) * farDistance;
    double wFar = hFar * aspectRatio;
    Vector3D view = getRotationVector(rotationYaw, rotationPitch).snormalize();
    Vector3D up = getRotationVector(rotationYaw, rotationPitch - 90.0D).snormalize();
    Vector3D right = getRotationVector(rotationYaw + 90.0D, 0.0D).snormalize();
    Vector3D camPos = new Vector3D(x, y, z);
    Vector3D view_camPos_product = view.add(camPos);
    Vector3D fc = new Vector3D(x * farDistance, y * farDistance, z * farDistance);
    Vector3D topLeftfrustum = new Vector3D(x + x * hFar / 2.0D - x * wFar / 2.0D, y + y * hFar / 2.0D - y * wFar / 2.0D, z + z * hFar / 2.0D - z * wFar / 2.0D);
    Vector3D downLeftfrustum = new Vector3D(x - x * hFar / 2.0D - x * wFar / 2.0D, y - y * hFar / 2.0D - y * wFar / 2.0D, z - z * hFar / 2.0D - z * wFar / 2.0D);
    Vector3D topRightfrustum = new Vector3D(x + x * hFar / 2.0D + x * wFar / 2.0D, y + y * hFar / 2.0D + y * wFar / 2.0D, z + z * hFar / 2.0D + z * wFar / 2.0D);
    Vector3D downRightfrustum = new Vector3D(x - x * hFar / 2.0D + x * wFar / 2.0D, y - y * hFar / 2.0D + y * wFar / 2.0D, z - z * hFar / 2.0D + z * wFar / 2.0D);
    return new Vector3D[] { topLeftfrustum, downLeftfrustum, downRightfrustum, topRightfrustum };
  }
  
  public Vector3D[] getFrustum()
  {
    return frustum;
  }
  
  public float getFovX()
  {
    return fovX;
  }
  
  public float getFovY()
  {
    return fovY;
  }
  
  public Vector3D getLookVector()
  {
    return lookVec;
  }
  
  public Vector3D getRotationVector(double rotYaw, double rotPitch)
  {
    double c = Math.cos(-rotYaw * 0.01745329238474369D - 3.141592653589793D);
    double s = Math.sin(-rotYaw * 0.01745329238474369D - 3.141592653589793D);
    double nc = -Math.cos(-rotPitch * 0.01745329238474369D);
    double ns = Math.sin(-rotPitch * 0.01745329238474369D);
    return new Vector3D(s * nc, ns, c * nc);
  }
  
  public static enum ClampMode
  {
    ORTHOGONAL,  DIRECT,  NONE;
  }
  


  public static class Line
  {
    public GLUProjection.Vector3D sourcePoint = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
    public GLUProjection.Vector3D direction = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
    
    public Line(double sx, double sy, double sz, double dx, double dy, double dz)
    {
      sourcePoint.x = sx;
      sourcePoint.y = sy;
      sourcePoint.z = sz;
      direction.x = dx;
      direction.y = dy;
      direction.z = dz;
    }
    
    public GLUProjection.Vector3D intersect(Line line)
    {
      double a = sourcePoint.x;
      double b = direction.x;
      double c = sourcePoint.x;
      double d = direction.x;
      double e = sourcePoint.y;
      double f = direction.y;
      double g = sourcePoint.y;
      double h = direction.y;
      double te = -(a * h - c * h - d * (e - g));
      double be = b * h - d * f;
      if (be == 0.0D) {
        return intersectXZ(line);
      }
      double t = te / be;
      GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
      x = (sourcePoint.x + direction.x * t);
      y = (sourcePoint.y + direction.y * t);
      z = (sourcePoint.z + direction.z * t);
      return result;
    }
    
    private GLUProjection.Vector3D intersectXZ(Line line)
    {
      double a = sourcePoint.x;
      double b = direction.x;
      double c = sourcePoint.x;
      double d = direction.x;
      double e = sourcePoint.z;
      double f = direction.z;
      double g = sourcePoint.z;
      double h = direction.z;
      double te = -(a * h - c * h - d * (e - g));
      double be = b * h - d * f;
      if (be == 0.0D) {
        return intersectYZ(line);
      }
      double t = te / be;
      GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
      x = (sourcePoint.x + direction.x * t);
      y = (sourcePoint.y + direction.y * t);
      z = (sourcePoint.z + direction.z * t);
      return result;
    }
    
    private GLUProjection.Vector3D intersectYZ(Line line)
    {
      double a = sourcePoint.y;
      double b = direction.y;
      double c = sourcePoint.y;
      double d = direction.y;
      double e = sourcePoint.z;
      double f = direction.z;
      double g = sourcePoint.z;
      double h = direction.z;
      double te = -(a * h - c * h - d * (e - g));
      double be = b * h - d * f;
      if (be == 0.0D) {
        return null;
      }
      double t = te / be;
      GLUProjection.Vector3D result = new GLUProjection.Vector3D(0.0D, 0.0D, 0.0D);
      x = (sourcePoint.x + direction.x * t);
      y = (sourcePoint.y + direction.y * t);
      z = (sourcePoint.z + direction.z * t);
      return result;
    }
    
    public GLUProjection.Vector3D intersectPlane(GLUProjection.Vector3D pointOnPlane, GLUProjection.Vector3D planeNormal)
    {
      GLUProjection.Vector3D result = new GLUProjection.Vector3D(sourcePoint.x, sourcePoint.y, sourcePoint.z);
      double d = pointOnPlane.sub(sourcePoint).dot(planeNormal) / direction.dot(planeNormal);
      result.sadd(direction.mul(d));
      if (direction.dot(planeNormal) == 0.0D) {
        return null;
      }
      return result;
    }
  }
  
  public static class Vector3D
  {
    public double x;
    public double y;
    public double z;
    
    public Vector3D(double x, double y, double z)
    {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    
    public Vector3D add(Vector3D v)
    {
      return new Vector3D(x + x, y + y, z + z);
    }
    
    public Vector3D add(double x, double y, double z)
    {
      return new Vector3D(this.x + x, this.y + y, this.z + z);
    }
    
    public Vector3D sub(Vector3D v)
    {
      return new Vector3D(x - x, y - y, z - z);
    }
    
    public Vector3D sub(double x, double y, double z)
    {
      return new Vector3D(this.x - x, this.y - y, this.z - z);
    }
    
    public Vector3D normalized()
    {
      double len = Math.sqrt(x * x + y * y + z * z);
      return new Vector3D(x / len, y / len, z / len);
    }
    
    public double dot(Vector3D v)
    {
      return x * x + y * y + z * z;
    }
    
    public Vector3D cross(Vector3D v)
    {
      return new Vector3D(y * z - z * y, z * x - x * z, x * y - y * x);
    }
    
    public Vector3D mul(double m)
    {
      return new Vector3D(x * m, y * m, z * m);
    }
    
    public Vector3D div(double d)
    {
      return new Vector3D(x / d, y / d, z / d);
    }
    
    public double length()
    {
      return Math.sqrt(x * x + y * y + z * z);
    }
    
    public Vector3D sadd(Vector3D v)
    {
      x += x;
      y += y;
      z += z;
      return this;
    }
    
    public Vector3D sadd(double x, double y, double z)
    {
      this.x += x;
      this.y += y;
      this.z += z;
      return this;
    }
    
    public Vector3D ssub(Vector3D v)
    {
      x -= x;
      y -= y;
      z -= z;
      return this;
    }
    
    public Vector3D ssub(double x, double y, double z)
    {
      this.x -= x;
      this.y -= y;
      this.z -= z;
      return this;
    }
    
    public Vector3D snormalize()
    {
      double len = Math.sqrt(x * x + y * y + z * z);
      x /= len;
      y /= len;
      z /= len;
      return this;
    }
    
    public Vector3D scross(Vector3D v)
    {
      x = (y * z - z * y);
      y = (z * x - x * z);
      z = (x * y - y * x);
      return this;
    }
    
    public Vector3D smul(double m)
    {
      x *= m;
      y *= m;
      z *= m;
      return this;
    }
    
    public Vector3D sdiv(double d)
    {
      x /= d;
      y /= d;
      z /= d;
      return this;
    }
    
    public String toString()
    {
      return "(X: " + x + " Y: " + y + " Z: " + z + ")";
    }
  }
  
  public static class Projection
  {
    private final double x;
    private final double y;
    private final Type t;
    
    public Projection(double x, double y, Type t)
    {
      this.x = x;
      this.y = y;
      this.t = t;
    }
    
    public double getX()
    {
      return x;
    }
    
    public double getY()
    {
      return y;
    }
    
    public Type getType()
    {
      return t;
    }
    
    public boolean isType(Type type)
    {
      return t == type;
    }
    
    public static enum Type
    {
      INSIDE,  OUTSIDE,  INVERTED,  FAIL;
    }
  }
}
