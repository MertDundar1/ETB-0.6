package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.vecmath.Matrix4f;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class ShaderGroup
{
  private Framebuffer mainFramebuffer;
  private IResourceManager resourceManager;
  private String shaderGroupName;
  private final List listShaders = Lists.newArrayList();
  private final Map mapFramebuffers = Maps.newHashMap();
  private final List listFramebuffers = Lists.newArrayList();
  private Matrix4f projectionMatrix;
  private int mainFramebufferWidth;
  private int mainFramebufferHeight;
  private float field_148036_j;
  private float field_148037_k;
  private static final String __OBFID = "CL_00001041";
  
  public ShaderGroup(TextureManager p_i1050_1_, IResourceManager p_i1050_2_, Framebuffer p_i1050_3_, ResourceLocation p_i1050_4_) throws JsonException
  {
    resourceManager = p_i1050_2_;
    mainFramebuffer = p_i1050_3_;
    field_148036_j = 0.0F;
    field_148037_k = 0.0F;
    mainFramebufferWidth = framebufferWidth;
    mainFramebufferHeight = framebufferHeight;
    shaderGroupName = p_i1050_4_.toString();
    resetProjectionMatrix();
    parseGroup(p_i1050_1_, p_i1050_4_);
  }
  
  public void parseGroup(TextureManager p_152765_1_, ResourceLocation p_152765_2_) throws JsonException
  {
    JsonParser var3 = new JsonParser();
    InputStream var4 = null;
    
    try
    {
      IResource var5 = resourceManager.getResource(p_152765_2_);
      var4 = var5.getInputStream();
      JsonObject var22 = var3.parse(IOUtils.toString(var4, com.google.common.base.Charsets.UTF_8)).getAsJsonObject();
      





      if (JsonUtils.jsonObjectFieldTypeIsArray(var22, "targets"))
      {
        JsonArray var7 = var22.getAsJsonArray("targets");
        int var8 = 0;
        
        for (Iterator var9 = var7.iterator(); var9.hasNext(); var8++)
        {
          JsonElement var10 = (JsonElement)var9.next();
          
          try
          {
            initTarget(var10);
          }
          catch (Exception var19)
          {
            JsonException var12 = JsonException.func_151379_a(var19);
            var12.func_151380_a("targets[" + var8 + "]");
            throw var12;
          }
        }
      }
      
      if (JsonUtils.jsonObjectFieldTypeIsArray(var22, "passes"))
      {
        JsonArray var7 = var22.getAsJsonArray("passes");
        int var8 = 0;
        
        for (Iterator var9 = var7.iterator(); var9.hasNext(); var8++)
        {
          JsonElement var10 = (JsonElement)var9.next();
          
          try
          {
            parsePass(p_152765_1_, var10);
          }
          catch (Exception var18)
          {
            JsonException var12 = JsonException.func_151379_a(var18);
            var12.func_151380_a("passes[" + var8 + "]");
            throw var12;
          }
        }
      }
    }
    catch (Exception var20)
    {
      JsonException var6 = JsonException.func_151379_a(var20);
      var6.func_151381_b(p_152765_2_.getResourcePath());
      throw var6;
    }
    finally
    {
      IOUtils.closeQuietly(var4);
    }
  }
  
  private void initTarget(JsonElement p_148027_1_) throws JsonException
  {
    if (JsonUtils.jsonElementTypeIsString(p_148027_1_))
    {
      addFramebuffer(p_148027_1_.getAsString(), mainFramebufferWidth, mainFramebufferHeight);
    }
    else
    {
      JsonObject var2 = JsonUtils.getElementAsJsonObject(p_148027_1_, "target");
      String var3 = JsonUtils.getJsonObjectStringFieldValue(var2, "name");
      int var4 = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(var2, "width", mainFramebufferWidth);
      int var5 = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(var2, "height", mainFramebufferHeight);
      
      if (mapFramebuffers.containsKey(var3))
      {
        throw new JsonException(var3 + " is already defined");
      }
      
      addFramebuffer(var3, var4, var5);
    }
  }
  
  private void parsePass(TextureManager p_152764_1_, JsonElement p_152764_2_) throws JsonException
  {
    JsonObject var3 = JsonUtils.getElementAsJsonObject(p_152764_2_, "pass");
    String var4 = JsonUtils.getJsonObjectStringFieldValue(var3, "name");
    String var5 = JsonUtils.getJsonObjectStringFieldValue(var3, "intarget");
    String var6 = JsonUtils.getJsonObjectStringFieldValue(var3, "outtarget");
    Framebuffer var7 = getFramebuffer(var5);
    Framebuffer var8 = getFramebuffer(var6);
    
    if (var7 == null)
    {
      throw new JsonException("Input target '" + var5 + "' does not exist");
    }
    if (var8 == null)
    {
      throw new JsonException("Output target '" + var6 + "' does not exist");
    }
    

    Shader var9 = addShader(var4, var7, var8);
    JsonArray var10 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(var3, "auxtargets", null);
    
    if (var10 != null)
    {
      int var11 = 0;
      
      for (Iterator var12 = var10.iterator(); var12.hasNext(); var11++)
      {
        JsonElement var13 = (JsonElement)var12.next();
        
        try
        {
          JsonObject var14 = JsonUtils.getElementAsJsonObject(var13, "auxtarget");
          String var30 = JsonUtils.getJsonObjectStringFieldValue(var14, "name");
          String var16 = JsonUtils.getJsonObjectStringFieldValue(var14, "id");
          Framebuffer var17 = getFramebuffer(var16);
          
          if (var17 == null)
          {
            ResourceLocation var18 = new ResourceLocation("textures/effect/" + var16 + ".png");
            
            try
            {
              resourceManager.getResource(var18);
            }
            catch (FileNotFoundException var24)
            {
              throw new JsonException("Render target or texture '" + var16 + "' does not exist");
            }
            
            p_152764_1_.bindTexture(var18);
            ITextureObject var19 = p_152764_1_.getTexture(var18);
            int var20 = JsonUtils.getJsonObjectIntegerFieldValue(var14, "width");
            int var21 = JsonUtils.getJsonObjectIntegerFieldValue(var14, "height");
            boolean var22 = JsonUtils.getJsonObjectBooleanFieldValue(var14, "bilinear");
            
            if (var22)
            {
              GL11.glTexParameteri(3553, 10241, 9729);
              GL11.glTexParameteri(3553, 10240, 9729);
            }
            else
            {
              GL11.glTexParameteri(3553, 10241, 9728);
              GL11.glTexParameteri(3553, 10240, 9728);
            }
            
            var9.addAuxFramebuffer(var30, Integer.valueOf(var19.getGlTextureId()), var20, var21);
          }
          else
          {
            var9.addAuxFramebuffer(var30, var17, framebufferTextureWidth, framebufferTextureHeight);
          }
        }
        catch (Exception var25)
        {
          JsonException var15 = JsonException.func_151379_a(var25);
          var15.func_151380_a("auxtargets[" + var11 + "]");
          throw var15;
        }
      }
    }
    
    JsonArray var26 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(var3, "uniforms", null);
    
    if (var26 != null)
    {
      int var27 = 0;
      
      for (Iterator var28 = var26.iterator(); var28.hasNext(); var27++)
      {
        JsonElement var29 = (JsonElement)var28.next();
        
        try
        {
          initUniform(var29);
        }
        catch (Exception var23)
        {
          JsonException var31 = JsonException.func_151379_a(var23);
          var31.func_151380_a("uniforms[" + var27 + "]");
          throw var31;
        }
      }
    }
  }
  
  private void initUniform(JsonElement p_148028_1_)
    throws JsonException
  {
    JsonObject var2 = JsonUtils.getElementAsJsonObject(p_148028_1_, "uniform");
    String var3 = JsonUtils.getJsonObjectStringFieldValue(var2, "name");
    ShaderUniform var4 = ((Shader)listShaders.get(listShaders.size() - 1)).getShaderManager().getShaderUniform(var3);
    
    if (var4 == null)
    {
      throw new JsonException("Uniform '" + var3 + "' does not exist");
    }
    

    float[] var5 = new float[4];
    int var6 = 0;
    JsonArray var7 = JsonUtils.getJsonObjectJsonArrayField(var2, "values");
    
    for (Iterator var8 = var7.iterator(); var8.hasNext(); var6++)
    {
      JsonElement var9 = (JsonElement)var8.next();
      
      try
      {
        var5[var6] = JsonUtils.getJsonElementFloatValue(var9, "value");
      }
      catch (Exception var12)
      {
        JsonException var11 = JsonException.func_151379_a(var12);
        var11.func_151380_a("values[" + var6 + "]");
        throw var11;
      }
    }
    
    switch (var6)
    {
    case 0: 
    default: 
      break;
    
    case 1: 
      var4.set(var5[0]);
      break;
    
    case 2: 
      var4.set(var5[0], var5[1]);
      break;
    
    case 3: 
      var4.set(var5[0], var5[1], var5[2]);
      break;
    
    case 4: 
      var4.set(var5[0], var5[1], var5[2], var5[3]);
    }
    
  }
  
  public Framebuffer func_177066_a(String p_177066_1_)
  {
    return (Framebuffer)mapFramebuffers.get(p_177066_1_);
  }
  
  public void addFramebuffer(String p_148020_1_, int p_148020_2_, int p_148020_3_)
  {
    Framebuffer var4 = new Framebuffer(p_148020_2_, p_148020_3_, true);
    var4.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
    mapFramebuffers.put(p_148020_1_, var4);
    
    if ((p_148020_2_ == mainFramebufferWidth) && (p_148020_3_ == mainFramebufferHeight))
    {
      listFramebuffers.add(var4);
    }
  }
  
  public void deleteShaderGroup()
  {
    Iterator var1 = mapFramebuffers.values().iterator();
    
    while (var1.hasNext())
    {
      Framebuffer var2 = (Framebuffer)var1.next();
      var2.deleteFramebuffer();
    }
    
    var1 = listShaders.iterator();
    
    while (var1.hasNext())
    {
      Shader var3 = (Shader)var1.next();
      var3.deleteShader();
    }
    
    listShaders.clear();
  }
  
  public Shader addShader(String p_148023_1_, Framebuffer p_148023_2_, Framebuffer p_148023_3_) throws JsonException
  {
    Shader var4 = new Shader(resourceManager, p_148023_1_, p_148023_2_, p_148023_3_);
    listShaders.add(listShaders.size(), var4);
    return var4;
  }
  
  private void resetProjectionMatrix()
  {
    projectionMatrix = new Matrix4f();
    projectionMatrix.setIdentity();
    projectionMatrix.m00 = (2.0F / mainFramebuffer.framebufferTextureWidth);
    projectionMatrix.m11 = (2.0F / -mainFramebuffer.framebufferTextureHeight);
    projectionMatrix.m22 = -0.0020001999F;
    projectionMatrix.m33 = 1.0F;
    projectionMatrix.m03 = -1.0F;
    projectionMatrix.m13 = 1.0F;
    projectionMatrix.m23 = -1.0001999F;
  }
  
  public void createBindFramebuffers(int p_148026_1_, int p_148026_2_)
  {
    mainFramebufferWidth = mainFramebuffer.framebufferTextureWidth;
    mainFramebufferHeight = mainFramebuffer.framebufferTextureHeight;
    resetProjectionMatrix();
    Iterator var3 = listShaders.iterator();
    
    while (var3.hasNext())
    {
      Shader var4 = (Shader)var3.next();
      var4.setProjectionMatrix(projectionMatrix);
    }
    
    var3 = listFramebuffers.iterator();
    
    while (var3.hasNext())
    {
      Framebuffer var5 = (Framebuffer)var3.next();
      var5.createBindFramebuffer(p_148026_1_, p_148026_2_);
    }
  }
  
  public void loadShaderGroup(float p_148018_1_)
  {
    if (p_148018_1_ < field_148037_k)
    {
      field_148036_j += 1.0F - field_148037_k;
      field_148036_j += p_148018_1_;
    }
    else
    {
      field_148036_j += p_148018_1_ - field_148037_k;
    }
    
    for (field_148037_k = p_148018_1_; field_148036_j > 20.0F; field_148036_j -= 20.0F) {}
    



    Iterator var2 = listShaders.iterator();
    
    while (var2.hasNext())
    {
      Shader var3 = (Shader)var2.next();
      var3.loadShader(field_148036_j / 20.0F);
    }
  }
  
  public final String getShaderGroupName()
  {
    return shaderGroupName;
  }
  
  private Framebuffer getFramebuffer(String p_148017_1_)
  {
    return p_148017_1_.equals("minecraft:main") ? mainFramebuffer : p_148017_1_ == null ? null : (Framebuffer)mapFramebuffers.get(p_148017_1_);
  }
}
