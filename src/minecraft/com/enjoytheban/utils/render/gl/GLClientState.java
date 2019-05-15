package com.enjoytheban.utils.render.gl;










public enum GLClientState
  implements GLenum
{
  COLOR("GL_COLOR_ARRAY", 32886), 
  EDGE("GL_EDGE_FLAG_ARRAY", 32889), 
  FOG("GL_FOG_COORD_ARRAY", 33879), 
  INDEX("GL_INDEX_ARRAY", 32887), 
  NORMAL("GL_NORMAL_ARRAY", 32885), 
  SECONDARY_COLOR("GL_SECONDARY_COLOR_ARRAY", 33886), 
  TEXTURE("GL_TEXTURE_COORD_ARRAY", 32888), 
  VERTEX("GL_VERTEX_ARRAY", 32884);
  
  private final String name;
  private final int cap;
  
  private GLClientState(String name, int cap) {
    this.name = name;
    this.cap = cap;
  }
  
  public String getName()
  {
    return name;
  }
  
  public int getCap()
  {
    return cap;
  }
}
