package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderManager
{
  private static final Logger logger = ;
  private static final ShaderDefault defaultShaderUniform = new ShaderDefault();
  private static ShaderManager staticShaderManager = null;
  private static int currentProgram = -1;
  private static boolean field_148000_e = true;
  

  private final Map shaderSamplers = Maps.newHashMap();
  private final List samplerNames = Lists.newArrayList();
  private final List shaderSamplerLocations = Lists.newArrayList();
  private final List shaderUniforms = Lists.newArrayList();
  private final List shaderUniformLocations = Lists.newArrayList();
  private final Map mappedShaderUniforms = Maps.newHashMap();
  private final int program;
  private final String programFilename;
  private final boolean useFaceCulling;
  private boolean isDirty;
  private final JsonBlendingMode field_148016_p;
  private final List field_148015_q;
  private final List field_148014_r;
  private final ShaderLoader vertexShaderLoader;
  private final ShaderLoader fragmentShaderLoader;
  private static final String __OBFID = "CL_00001040";
  
  public ShaderManager(IResourceManager resourceManager, String programName) throws JsonException
  {
    JsonParser var3 = new JsonParser();
    ResourceLocation var4 = new ResourceLocation("shaders/program/" + programName + ".json");
    programFilename = programName;
    InputStream var5 = null;
    
    try
    {
      var5 = resourceManager.getResource(var4).getInputStream();
      JsonObject var6 = var3.parse(IOUtils.toString(var5, com.google.common.base.Charsets.UTF_8)).getAsJsonObject();
      String var7 = JsonUtils.getJsonObjectStringFieldValue(var6, "vertex");
      String var28 = JsonUtils.getJsonObjectStringFieldValue(var6, "fragment");
      JsonArray var9 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(var6, "samplers", null);
      
      if (var9 != null)
      {
        int var10 = 0;
        
        for (Iterator var11 = var9.iterator(); var11.hasNext(); var10++)
        {
          JsonElement var12 = (JsonElement)var11.next();
          
          try
          {
            parseSampler(var12);
          }
          catch (Exception var25)
          {
            JsonException var14 = JsonException.func_151379_a(var25);
            var14.func_151380_a("samplers[" + var10 + "]");
            throw var14;
          }
        }
      }
      
      JsonArray var29 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(var6, "attributes", null);
      

      if (var29 != null)
      {
        int var30 = 0;
        field_148015_q = Lists.newArrayListWithCapacity(var29.size());
        field_148014_r = Lists.newArrayListWithCapacity(var29.size());
        
        for (Iterator var32 = var29.iterator(); var32.hasNext(); var30++)
        {
          JsonElement var13 = (JsonElement)var32.next();
          
          try
          {
            field_148014_r.add(JsonUtils.getJsonElementStringValue(var13, "attribute"));
          }
          catch (Exception var24)
          {
            JsonException var15 = JsonException.func_151379_a(var24);
            var15.func_151380_a("attributes[" + var30 + "]");
            throw var15;
          }
        }
      }
      else
      {
        field_148015_q = null;
        field_148014_r = null;
      }
      
      JsonArray var31 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(var6, "uniforms", null);
      
      if (var31 != null)
      {
        int var33 = 0;
        
        for (Iterator var34 = var31.iterator(); var34.hasNext(); var33++)
        {
          JsonElement var36 = (JsonElement)var34.next();
          
          try
          {
            parseUniform(var36);
          }
          catch (Exception var23)
          {
            JsonException var16 = JsonException.func_151379_a(var23);
            var16.func_151380_a("uniforms[" + var33 + "]");
            throw var16;
          }
        }
      }
      
      field_148016_p = JsonBlendingMode.func_148110_a(JsonUtils.getJsonObjectFieldOrDefault(var6, "blend", null));
      useFaceCulling = JsonUtils.getJsonObjectBooleanFieldValueOrDefault(var6, "cull", true);
      vertexShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.VERTEX, var7);
      fragmentShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.FRAGMENT, var28);
      program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
      ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
      setupUniforms();
      
      if (field_148014_r != null)
      {
        Iterator var32 = field_148014_r.iterator();
        
        while (var32.hasNext())
        {
          String var35 = (String)var32.next();
          int var37 = OpenGlHelper.glGetAttribLocation(program, var35);
          field_148015_q.add(Integer.valueOf(var37));
        }
      }
    }
    catch (Exception var26)
    {
      JsonException var8 = JsonException.func_151379_a(var26);
      var8.func_151381_b(var4.getResourcePath());
      throw var8;
    }
    finally
    {
      IOUtils.closeQuietly(var5);
    }
    
    markDirty();
  }
  
  public void deleteShader()
  {
    ShaderLinkHelper.getStaticShaderLinkHelper().deleteShader(this);
  }
  
  public void endShader()
  {
    OpenGlHelper.glUseProgram(0);
    currentProgram = -1;
    staticShaderManager = null;
    field_148000_e = true;
    
    for (int var1 = 0; var1 < shaderSamplerLocations.size(); var1++)
    {
      if (shaderSamplers.get(samplerNames.get(var1)) != null)
      {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + var1);
        GlStateManager.func_179144_i(0);
      }
    }
  }
  
  public void useShader()
  {
    isDirty = false;
    staticShaderManager = this;
    field_148016_p.func_148109_a();
    
    if (program != currentProgram)
    {
      OpenGlHelper.glUseProgram(program);
      currentProgram = program;
    }
    
    if (useFaceCulling)
    {
      GlStateManager.enableCull();
    }
    else
    {
      GlStateManager.disableCull();
    }
    
    for (int var1 = 0; var1 < shaderSamplerLocations.size(); var1++)
    {
      if (shaderSamplers.get(samplerNames.get(var1)) != null)
      {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + var1);
        GlStateManager.func_179098_w();
        Object var2 = shaderSamplers.get(samplerNames.get(var1));
        int var3 = -1;
        
        if ((var2 instanceof Framebuffer))
        {
          var3 = framebufferTexture;
        }
        else if ((var2 instanceof ITextureObject))
        {
          var3 = ((ITextureObject)var2).getGlTextureId();
        }
        else if ((var2 instanceof Integer))
        {
          var3 = ((Integer)var2).intValue();
        }
        
        if (var3 != -1)
        {
          GlStateManager.func_179144_i(var3);
          OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(program, (CharSequence)samplerNames.get(var1)), var1);
        }
      }
    }
    
    Iterator var4 = shaderUniforms.iterator();
    
    while (var4.hasNext())
    {
      ShaderUniform var5 = (ShaderUniform)var4.next();
      var5.upload();
    }
  }
  
  public void markDirty()
  {
    isDirty = true;
  }
  



  public ShaderUniform getShaderUniform(String p_147991_1_)
  {
    return mappedShaderUniforms.containsKey(p_147991_1_) ? (ShaderUniform)mappedShaderUniforms.get(p_147991_1_) : null;
  }
  



  public ShaderUniform getShaderUniformOrDefault(String p_147984_1_)
  {
    return mappedShaderUniforms.containsKey(p_147984_1_) ? (ShaderUniform)mappedShaderUniforms.get(p_147984_1_) : defaultShaderUniform;
  }
  



  private void setupUniforms()
  {
    int var1 = 0;
    


    for (int var2 = 0; var1 < samplerNames.size(); var2++)
    {
      String var3 = (String)samplerNames.get(var1);
      int var4 = OpenGlHelper.glGetUniformLocation(program, var3);
      
      if (var4 == -1)
      {
        logger.warn("Shader " + programFilename + "could not find sampler named " + var3 + " in the specified shader program.");
        shaderSamplers.remove(var3);
        samplerNames.remove(var2);
        var2--;
      }
      else
      {
        shaderSamplerLocations.add(Integer.valueOf(var4));
      }
      
      var1++;
    }
    
    Iterator var5 = shaderUniforms.iterator();
    
    while (var5.hasNext())
    {
      ShaderUniform var6 = (ShaderUniform)var5.next();
      String var3 = var6.getShaderName();
      int var4 = OpenGlHelper.glGetUniformLocation(program, var3);
      
      if (var4 == -1)
      {
        logger.warn("Could not find uniform named " + var3 + " in the specified" + " shader program.");
      }
      else
      {
        shaderUniformLocations.add(Integer.valueOf(var4));
        var6.setUniformLocation(var4);
        mappedShaderUniforms.put(var3, var6);
      }
    }
  }
  
  private void parseSampler(JsonElement p_147996_1_)
  {
    JsonObject var2 = JsonUtils.getElementAsJsonObject(p_147996_1_, "sampler");
    String var3 = JsonUtils.getJsonObjectStringFieldValue(var2, "name");
    
    if (!JsonUtils.jsonObjectFieldTypeIsString(var2, "file"))
    {
      shaderSamplers.put(var3, null);
      samplerNames.add(var3);
    }
    else
    {
      samplerNames.add(var3);
    }
  }
  



  public void addSamplerTexture(String p_147992_1_, Object p_147992_2_)
  {
    if (shaderSamplers.containsKey(p_147992_1_))
    {
      shaderSamplers.remove(p_147992_1_);
    }
    
    shaderSamplers.put(p_147992_1_, p_147992_2_);
    markDirty();
  }
  
  private void parseUniform(JsonElement p_147987_1_) throws JsonException
  {
    JsonObject var2 = JsonUtils.getElementAsJsonObject(p_147987_1_, "uniform");
    String var3 = JsonUtils.getJsonObjectStringFieldValue(var2, "name");
    int var4 = ShaderUniform.parseType(JsonUtils.getJsonObjectStringFieldValue(var2, "type"));
    int var5 = JsonUtils.getJsonObjectIntegerFieldValue(var2, "count");
    float[] var6 = new float[Math.max(var5, 16)];
    JsonArray var7 = JsonUtils.getJsonObjectJsonArrayField(var2, "values");
    
    if ((var7.size() != var5) && (var7.size() > 1))
    {
      throw new JsonException("Invalid amount of values specified (expected " + var5 + ", found " + var7.size() + ")");
    }
    

    int var8 = 0;
    
    for (Iterator var9 = var7.iterator(); var9.hasNext(); var8++)
    {
      JsonElement var10 = (JsonElement)var9.next();
      
      try
      {
        var6[var8] = JsonUtils.getJsonElementFloatValue(var10, "value");
      }
      catch (Exception var13)
      {
        JsonException var12 = JsonException.func_151379_a(var13);
        var12.func_151380_a("values[" + var8 + "]");
        throw var12;
      }
    }
    
    if ((var5 > 1) && (var7.size() == 1))
    {
      while (var8 < var5)
      {
        var6[var8] = var6[0];
        var8++;
      }
    }
    
    int var14 = (var5 > 1) && (var5 <= 4) && (var4 < 8) ? var5 - 1 : 0;
    ShaderUniform var15 = new ShaderUniform(var3, var4 + var14, var5, this);
    
    if (var4 <= 3)
    {
      var15.set((int)var6[0], (int)var6[1], (int)var6[2], (int)var6[3]);
    }
    else if (var4 <= 7)
    {
      var15.func_148092_b(var6[0], var6[1], var6[2], var6[3]);
    }
    else
    {
      var15.set(var6);
    }
    
    shaderUniforms.add(var15);
  }
  

  public ShaderLoader getVertexShaderLoader()
  {
    return vertexShaderLoader;
  }
  
  public ShaderLoader getFragmentShaderLoader()
  {
    return fragmentShaderLoader;
  }
  
  public int getProgram()
  {
    return program;
  }
}
