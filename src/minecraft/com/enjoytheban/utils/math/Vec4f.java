package com.enjoytheban.utils.math;

public class Vec4f {
  public float x;
  public float y;
  public float w;
  public float h;
  
  public float getX() {
    return x;
  }
  
  public float getY() {
    return y;
  }
  
  public float getW() {
    return w;
  }
  
  public float getH() {
    return h;
  }
  
  public void setX(float x) {
    this.x = x;
  }
  
  public void setY(float y) {
    this.y = y;
  }
  
  public void setW(float w) {
    this.w = w;
  }
  
  public void setH(float h) {
    this.h = h;
  }
  
  public Vec4f(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
}
