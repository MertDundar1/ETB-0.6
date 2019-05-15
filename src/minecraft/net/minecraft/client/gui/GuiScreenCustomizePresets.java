package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.ChunkProviderSettings.Factory;
import org.lwjgl.input.Keyboard;

public class GuiScreenCustomizePresets extends GuiScreen
{
  private static final List field_175310_f = ;
  private ListPreset field_175311_g;
  private GuiButton field_175316_h;
  private GuiTextField field_175317_i;
  private GuiCustomizeWorldScreen field_175314_r;
  protected String field_175315_a = "Customize World Presets";
  private String field_175313_s;
  private String field_175312_t;
  private static final String __OBFID = "CL_00001937";
  
  public GuiScreenCustomizePresets(GuiCustomizeWorldScreen p_i45524_1_)
  {
    field_175314_r = p_i45524_1_;
  }
  



  public void initGui()
  {
    buttonList.clear();
    Keyboard.enableRepeatEvents(true);
    field_175315_a = I18n.format("createWorld.customize.custom.presets.title", new Object[0]);
    field_175313_s = I18n.format("createWorld.customize.presets.share", new Object[0]);
    field_175312_t = I18n.format("createWorld.customize.presets.list", new Object[0]);
    field_175317_i = new GuiTextField(2, fontRendererObj, 50, 40, width - 100, 20);
    field_175311_g = new ListPreset();
    field_175317_i.setMaxStringLength(2000);
    field_175317_i.setText(field_175314_r.func_175323_a());
    buttonList.add(this.field_175316_h = new GuiButton(0, width / 2 - 102, height - 27, 100, 20, I18n.format("createWorld.customize.presets.select", new Object[0])));
    buttonList.add(new GuiButton(1, width / 2 + 3, height - 27, 100, 20, I18n.format("gui.cancel", new Object[0])));
    func_175304_a();
  }
  


  public void handleMouseInput()
    throws IOException
  {
    super.handleMouseInput();
    field_175311_g.func_178039_p();
  }
  



  public void onGuiClosed()
  {
    Keyboard.enableRepeatEvents(false);
  }
  


  protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    throws IOException
  {
    field_175317_i.mouseClicked(mouseX, mouseY, mouseButton);
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  



  protected void keyTyped(char typedChar, int keyCode)
    throws IOException
  {
    if (!field_175317_i.textboxKeyTyped(typedChar, keyCode))
    {
      super.keyTyped(typedChar, keyCode);
    }
  }
  
  protected void actionPerformed(GuiButton button) throws IOException
  {
    switch (id)
    {
    case 0: 
      field_175314_r.func_175324_a(field_175317_i.getText());
      mc.displayGuiScreen(field_175314_r);
      break;
    
    case 1: 
      mc.displayGuiScreen(field_175314_r);
    }
    
  }
  


  public void drawScreen(int mouseX, int mouseY, float partialTicks)
  {
    drawDefaultBackground();
    field_175311_g.drawScreen(mouseX, mouseY, partialTicks);
    drawCenteredString(fontRendererObj, field_175315_a, width / 2, 8, 16777215);
    drawString(fontRendererObj, field_175313_s, 50, 30, 10526880);
    drawString(fontRendererObj, field_175312_t, 50, 70, 10526880);
    field_175317_i.drawTextBox();
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
  



  public void updateScreen()
  {
    field_175317_i.updateCursorCounter();
    super.updateScreen();
  }
  
  public void func_175304_a()
  {
    field_175316_h.enabled = func_175305_g();
  }
  
  private boolean func_175305_g()
  {
    return ((field_175311_g.field_178053_u > -1) && (field_175311_g.field_178053_u < field_175310_f.size())) || (field_175317_i.getText().length() > 1);
  }
  
  static
  {
    ChunkProviderSettings.Factory var0 = ChunkProviderSettings.Factory.func_177865_a("{ \"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":8.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":0.5, \"biomeScaleWeight\":2.0, \"biomeScaleOffset\":0.375, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":255 }");
    ResourceLocation var1 = new ResourceLocation("textures/gui/presets/water.png");
    field_175310_f.add(new Info(I18n.format("createWorld.customize.custom.preset.waterWorld", new Object[0]), var1, var0));
    var0 = ChunkProviderSettings.Factory.func_177865_a("{\"coordinateScale\":3000.0, \"heightScale\":6000.0, \"upperLimitScale\":250.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }");
    var1 = new ResourceLocation("textures/gui/presets/isles.png");
    field_175310_f.add(new Info(I18n.format("createWorld.customize.custom.preset.isleLand", new Object[0]), var1, var0));
    var0 = ChunkProviderSettings.Factory.func_177865_a("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":5.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":1.0, \"biomeScaleWeight\":4.0, \"biomeScaleOffset\":1.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }");
    var1 = new ResourceLocation("textures/gui/presets/delight.png");
    field_175310_f.add(new Info(I18n.format("createWorld.customize.custom.preset.caveDelight", new Object[0]), var1, var0));
    var0 = ChunkProviderSettings.Factory.func_177865_a("{\"coordinateScale\":738.41864, \"heightScale\":157.69133, \"upperLimitScale\":801.4267, \"lowerLimitScale\":1254.1643, \"depthNoiseScaleX\":374.93652, \"depthNoiseScaleZ\":288.65228, \"depthNoiseScaleExponent\":1.2092624, \"mainNoiseScaleX\":1355.9908, \"mainNoiseScaleY\":745.5343, \"mainNoiseScaleZ\":1183.464, \"baseSize\":1.8758626, \"stretchY\":1.7137525, \"biomeDepthWeight\":1.7553768, \"biomeDepthOffset\":3.4701107, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":2.535211, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }");
    var1 = new ResourceLocation("textures/gui/presets/madness.png");
    field_175310_f.add(new Info(I18n.format("createWorld.customize.custom.preset.mountains", new Object[0]), var1, var0));
    var0 = ChunkProviderSettings.Factory.func_177865_a("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":1000.0, \"mainNoiseScaleY\":3000.0, \"mainNoiseScaleZ\":1000.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":20 }");
    var1 = new ResourceLocation("textures/gui/presets/drought.png");
    field_175310_f.add(new Info(I18n.format("createWorld.customize.custom.preset.drought", new Object[0]), var1, var0));
    var0 = ChunkProviderSettings.Factory.func_177865_a("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":2.0, \"lowerLimitScale\":64.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":6 }");
    var1 = new ResourceLocation("textures/gui/presets/chaos.png");
    field_175310_f.add(new Info(I18n.format("createWorld.customize.custom.preset.caveChaos", new Object[0]), var1, var0));
    var0 = ChunkProviderSettings.Factory.func_177865_a("{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":true, \"seaLevel\":40 }");
    var1 = new ResourceLocation("textures/gui/presets/luck.png");
    field_175310_f.add(new Info(I18n.format("createWorld.customize.custom.preset.goodLuck", new Object[0]), var1, var0));
  }
  
  static class Info
  {
    public String field_178955_a;
    public ResourceLocation field_178953_b;
    public ChunkProviderSettings.Factory field_178954_c;
    private static final String __OBFID = "CL_00001936";
    
    public Info(String p_i45523_1_, ResourceLocation p_i45523_2_, ChunkProviderSettings.Factory p_i45523_3_)
    {
      field_178955_a = p_i45523_1_;
      field_178953_b = p_i45523_2_;
      field_178954_c = p_i45523_3_;
    }
  }
  
  class ListPreset extends GuiSlot
  {
    public int field_178053_u = -1;
    private static final String __OBFID = "CL_00001935";
    
    public ListPreset()
    {
      super(width, height, 80, height - 32, 38);
    }
    
    protected int getSize()
    {
      return GuiScreenCustomizePresets.field_175310_f.size();
    }
    
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY)
    {
      field_178053_u = slotIndex;
      func_175304_a();
      field_175317_i.setText(field_175310_fgetfield_175311_g.field_178053_u)).field_178954_c.toString());
    }
    
    protected boolean isSelected(int slotIndex)
    {
      return slotIndex == field_178053_u;
    }
    
    protected void drawBackground() {}
    
    private void func_178051_a(int p_178051_1_, int p_178051_2_, ResourceLocation p_178051_3_)
    {
      int var4 = p_178051_1_ + 5;
      drawHorizontalLine(var4 - 1, var4 + 32, p_178051_2_ - 1, -2039584);
      drawHorizontalLine(var4 - 1, var4 + 32, p_178051_2_ + 32, -6250336);
      drawVerticalLine(var4 - 1, p_178051_2_ - 1, p_178051_2_ + 32, -2039584);
      drawVerticalLine(var4 + 32, p_178051_2_ - 1, p_178051_2_ + 32, -6250336);
      net.minecraft.client.renderer.GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      mc.getTextureManager().bindTexture(p_178051_3_);
      boolean var6 = true;
      boolean var7 = true;
      Tessellator var8 = Tessellator.getInstance();
      WorldRenderer var9 = var8.getWorldRenderer();
      var9.startDrawingQuads();
      var9.addVertexWithUV(var4 + 0, p_178051_2_ + 32, 0.0D, 0.0D, 1.0D);
      var9.addVertexWithUV(var4 + 32, p_178051_2_ + 32, 0.0D, 1.0D, 1.0D);
      var9.addVertexWithUV(var4 + 32, p_178051_2_ + 0, 0.0D, 1.0D, 0.0D);
      var9.addVertexWithUV(var4 + 0, p_178051_2_ + 0, 0.0D, 0.0D, 0.0D);
      var8.draw();
    }
    
    protected void drawSlot(int p_180791_1_, int p_180791_2_, int p_180791_3_, int p_180791_4_, int p_180791_5_, int p_180791_6_)
    {
      GuiScreenCustomizePresets.Info var7 = (GuiScreenCustomizePresets.Info)GuiScreenCustomizePresets.field_175310_f.get(p_180791_1_);
      func_178051_a(p_180791_2_, p_180791_3_, field_178953_b);
      fontRendererObj.drawString(field_178955_a, p_180791_2_ + 32 + 10, p_180791_3_ + 14, 16777215);
    }
  }
}
