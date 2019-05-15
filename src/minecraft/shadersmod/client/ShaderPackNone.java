package shadersmod.client;

import java.io.InputStream;

public class ShaderPackNone implements IShaderPack {
  public ShaderPackNone() {}
  
  public void close() {}
  
  public InputStream getResourceAsStream(String resName) {
    return null;
  }
  
  public boolean hasDirectory(String name)
  {
    return false;
  }
  
  public String getName()
  {
    return Shaders.packNameNone;
  }
}
