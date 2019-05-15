package optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class ConnectedTextures
{
  private static Map[] spriteQuadMaps = null;
  private static ConnectedProperties[][] blockProperties = null;
  private static ConnectedProperties[][] tileProperties = null;
  private static boolean multipass = false;
  private static final int Y_NEG_DOWN = 0;
  private static final int Y_POS_UP = 1;
  private static final int Z_NEG_NORTH = 2;
  private static final int Z_POS_SOUTH = 3;
  private static final int X_NEG_WEST = 4;
  private static final int X_POS_EAST = 5;
  private static final int Y_AXIS = 0;
  private static final int Z_AXIS = 1;
  private static final int X_AXIS = 2;
  private static final String[] propSuffixes = { "", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
  private static final int[] ctmIndexes = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 0, 0, 0, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 0, 0, 0, 0, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46 };
  public static final IBlockState AIR_DEFAULT_STATE = net.minecraft.init.Blocks.air.getDefaultState();
  private static TextureAtlasSprite emptySprite = null;
  
  public ConnectedTextures() {}
  
  public static synchronized BakedQuad getConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad quad, RenderEnv renderEnv) { TextureAtlasSprite spriteIn = quad.getSprite();
    
    if (spriteIn == null)
    {
      return quad;
    }
    

    Block block = blockState.getBlock();
    EnumFacing side = quad.getFace();
    
    if (((block instanceof BlockPane)) && (spriteIn.getIconName().startsWith("minecraft:blocks/glass_pane_top")))
    {
      IBlockState sprite = blockAccess.getBlockState(blockPos.offset(quad.getFace()));
      
      if (sprite == blockState)
      {
        return getQuad(emptySprite, block, blockState, quad);
      }
    }
    
    TextureAtlasSprite sprite1 = getConnectedTextureMultiPass(blockAccess, blockState, blockPos, side, spriteIn, renderEnv);
    return sprite1 == spriteIn ? quad : getQuad(sprite1, block, blockState, quad);
  }
  

  private static BakedQuad getQuad(TextureAtlasSprite sprite, Block block, IBlockState blockState, BakedQuad quadIn)
  {
    if (spriteQuadMaps == null)
    {
      return quadIn;
    }
    

    int spriteIndex = sprite.getIndexInMap();
    
    if ((spriteIndex >= 0) && (spriteIndex < spriteQuadMaps.length))
    {
      Object quadMap = spriteQuadMaps[spriteIndex];
      
      if (quadMap == null)
      {
        quadMap = new java.util.IdentityHashMap(1);
        spriteQuadMaps[spriteIndex] = ((Map)quadMap);
      }
      
      BakedQuad quad = (BakedQuad)((Map)quadMap).get(quadIn);
      
      if (quad == null)
      {
        quad = makeSpriteQuad(quadIn, sprite);
        ((Map)quadMap).put(quadIn, quad);
      }
      
      return quad;
    }
    

    return quadIn;
  }
  


  private static BakedQuad makeSpriteQuad(BakedQuad quad, TextureAtlasSprite sprite)
  {
    int[] data = (int[])quad.func_178209_a().clone();
    TextureAtlasSprite spriteFrom = quad.getSprite();
    
    for (int bq = 0; bq < 4; bq++)
    {
      fixVertex(data, bq, spriteFrom, sprite);
    }
    
    BakedQuad var5 = new BakedQuad(data, quad.func_178211_c(), quad.getFace(), sprite);
    return var5;
  }
  
  private static void fixVertex(int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo)
  {
    int mul = data.length / 4;
    int pos = mul * vertex;
    float u = Float.intBitsToFloat(data[(pos + 4)]);
    float v = Float.intBitsToFloat(data[(pos + 4 + 1)]);
    double su16 = spriteFrom.getSpriteU16(u);
    double sv16 = spriteFrom.getSpriteV16(v);
    data[(pos + 4)] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(su16));
    data[(pos + 4 + 1)] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(sv16));
  }
  
  private static TextureAtlasSprite getConnectedTextureMultiPass(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing side, TextureAtlasSprite icon, RenderEnv renderEnv)
  {
    TextureAtlasSprite newIcon = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, icon, true, renderEnv);
    
    if (!multipass)
    {
      return newIcon;
    }
    if (newIcon == icon)
    {
      return newIcon;
    }
    

    TextureAtlasSprite mpIcon = newIcon;
    
    for (int i = 0; i < 3; i++)
    {
      TextureAtlasSprite newMpIcon = getConnectedTextureSingle(blockAccess, blockState, blockPos, side, mpIcon, false, renderEnv);
      
      if (newMpIcon == mpIcon) {
        break;
      }
      

      mpIcon = newMpIcon;
    }
    
    return mpIcon;
  }
  

  public static TextureAtlasSprite getConnectedTextureSingle(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing facing, TextureAtlasSprite icon, boolean checkBlocks, RenderEnv renderEnv)
  {
    Block block = blockState.getBlock();
    
    if (!(blockState instanceof BlockStateBase))
    {
      return icon;
    }
    

    BlockStateBase blockStateBase = (BlockStateBase)blockState;
    






    if (tileProperties != null)
    {
      int blockId = icon.getIndexInMap();
      
      if ((blockId >= 0) && (blockId < tileProperties.length))
      {
        ConnectedProperties[] cps = tileProperties[blockId];
        
        if (cps != null)
        {
          int side = getSide(facing);
          
          for (int i = 0; i < cps.length; i++)
          {
            ConnectedProperties cp = cps[i];
            
            if ((cp != null) && (cp.matchesBlockId(blockStateBase.getBlockId())))
            {
              TextureAtlasSprite newIcon = getConnectedTexture(cp, blockAccess, blockStateBase, blockPos, side, icon, renderEnv);
              
              if (newIcon != null)
              {
                return newIcon;
              }
            }
          }
        }
      }
    }
    
    if ((blockProperties != null) && (checkBlocks))
    {
      int blockId = renderEnv.getBlockId();
      
      if ((blockId >= 0) && (blockId < blockProperties.length))
      {
        ConnectedProperties[] cps = blockProperties[blockId];
        
        if (cps != null)
        {
          int side = getSide(facing);
          
          for (int i = 0; i < cps.length; i++)
          {
            ConnectedProperties cp = cps[i];
            
            if ((cp != null) && (cp.matchesIcon(icon)))
            {
              TextureAtlasSprite newIcon = getConnectedTexture(cp, blockAccess, blockStateBase, blockPos, side, icon, renderEnv);
              
              if (newIcon != null)
              {
                return newIcon;
              }
            }
          }
        }
      }
    }
    
    return icon;
  }
  

  public static int getSide(EnumFacing facing)
  {
    if (facing == null)
    {
      return -1;
    }
    

    switch (NamelessClass379831726.$SwitchMap$net$minecraft$util$EnumFacing[facing.ordinal()])
    {
    case 1: 
      return 0;
    
    case 2: 
      return 1;
    
    case 3: 
      return 5;
    
    case 4: 
      return 4;
    
    case 5: 
      return 2;
    
    case 6: 
      return 3;
    }
    
    return -1;
  }
  


  private static EnumFacing getFacing(int side)
  {
    switch (side)
    {
    case 0: 
      return EnumFacing.DOWN;
    
    case 1: 
      return EnumFacing.UP;
    
    case 2: 
      return EnumFacing.NORTH;
    
    case 3: 
      return EnumFacing.SOUTH;
    
    case 4: 
      return EnumFacing.WEST;
    
    case 5: 
      return EnumFacing.EAST;
    }
    
    return EnumFacing.UP;
  }
  

  private static TextureAtlasSprite getConnectedTexture(ConnectedProperties cp, IBlockAccess blockAccess, BlockStateBase blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, RenderEnv renderEnv)
  {
    int vertAxis = 0;
    int metadata = blockState.getMetadata();
    int metadataCheck = metadata;
    Block block = blockState.getBlock();
    
    if ((block instanceof BlockRotatedPillar))
    {
      vertAxis = getWoodAxis(side, metadata);
      
      if (cp.getMetadataMax() <= 3)
      {
        metadataCheck = metadata & 0x3;
      }
    }
    
    if ((block instanceof BlockQuartz))
    {
      vertAxis = getQuartzAxis(side, metadata);
      
      if ((cp.getMetadataMax() <= 2) && (metadataCheck > 2))
      {
        metadataCheck = 2;
      }
    }
    
    if (!cp.matchesBlock(blockState.getBlockId(), metadataCheck))
    {
      return null;
    }
    



    if ((side >= 0) && (faces != 63))
    {
      int y = side;
      
      if (vertAxis != 0)
      {
        y = fixSideByAxis(side, vertAxis);
      }
      
      if ((1 << y & faces) == 0)
      {
        return null;
      }
    }
    
    int y = blockPos.getY();
    
    if ((y >= minHeight) && (y <= maxHeight))
    {
      if (biomes != null)
      {
        BiomeGenBase blockBiome = blockAccess.getBiomeGenForCoords(blockPos);
        
        if (!cp.matchesBiome(blockBiome))
        {
          return null;
        }
      }
      
      switch (method)
      {
      case 1: 
        return getConnectedTextureCtm(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata, renderEnv);
      
      case 2: 
        return getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      
      case 3: 
        return getConnectedTextureTop(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      
      case 4: 
        return getConnectedTextureRandom(cp, blockPos, side);
      
      case 5: 
        return getConnectedTextureRepeat(cp, blockPos, side);
      
      case 6: 
        return getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      
      case 7: 
        return getConnectedTextureFixed(cp);
      
      case 8: 
        return getConnectedTextureHorizontalVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      
      case 9: 
        return getConnectedTextureVerticalHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
      }
      
      return null;
    }
    


    return null;
  }
  


  private static int fixSideByAxis(int side, int vertAxis)
  {
    switch (vertAxis)
    {
    case 0: 
      return side;
    
    case 1: 
      switch (side)
      {
      case 0: 
        return 2;
      
      case 1: 
        return 3;
      
      case 2: 
        return 1;
      
      case 3: 
        return 0;
      }
      
      return side;
    

    case 2: 
      switch (side)
      {
      case 0: 
        return 4;
      
      case 1: 
        return 5;
      
      case 2: 
      case 3: 
      default: 
        return side;
      
      case 4: 
        return 1;
      }
      
      return 0;
    }
    
    
    return side;
  }
  

  private static int getWoodAxis(int side, int metadata)
  {
    int orient = (metadata & 0xC) >> 2;
    
    switch (orient)
    {
    case 1: 
      return 2;
    
    case 2: 
      return 1;
    }
    
    return 0;
  }
  

  private static int getQuartzAxis(int side, int metadata)
  {
    switch (metadata)
    {
    case 3: 
      return 2;
    
    case 4: 
      return 1;
    }
    
    return 0;
  }
  

  private static TextureAtlasSprite getConnectedTextureRandom(ConnectedProperties cp, BlockPos blockPos, int side)
  {
    if (tileIcons.length == 1)
    {
      return tileIcons[0];
    }
    

    int face = side / symmetry * symmetry;
    int rand = Config.getRandom(blockPos, face) & 0x7FFFFFFF;
    int index = 0;
    
    if (weights == null)
    {
      index = rand % tileIcons.length;
    }
    else
    {
      int randWeight = rand % sumAllWeights;
      int[] sumWeights = cp.sumWeights;
      
      for (int i = 0; i < sumWeights.length; i++)
      {
        if (randWeight < sumWeights[i])
        {
          index = i;
          break;
        }
      }
    }
    
    return tileIcons[index];
  }
  

  private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties cp)
  {
    return tileIcons[0];
  }
  
  private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties cp, BlockPos blockPos, int side)
  {
    if (tileIcons.length == 1)
    {
      return tileIcons[0];
    }
    

    int x = blockPos.getX();
    int y = blockPos.getY();
    int z = blockPos.getZ();
    int nx = 0;
    int ny = 0;
    
    switch (side)
    {
    case 0: 
      nx = x;
      ny = z;
      break;
    
    case 1: 
      nx = x;
      ny = z;
      break;
    
    case 2: 
      nx = -x - 1;
      ny = -y;
      break;
    
    case 3: 
      nx = x;
      ny = -y;
      break;
    
    case 4: 
      nx = z;
      ny = -y;
      break;
    
    case 5: 
      nx = -z - 1;
      ny = -y;
    }
    
    nx = nx % width;
    ny %= height;
    
    if (nx < 0)
    {
      nx += width;
    }
    
    if (ny < 0)
    {
      ny += height;
    }
    
    int index = ny * width + nx;
    return tileIcons[index];
  }
  

  private static TextureAtlasSprite getConnectedTextureCtm(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata, RenderEnv renderEnv)
  {
    boolean[] borders = renderEnv.getBorderFlags();
    
    switch (side)
    {
    case 0: 
      borders[0] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
      borders[1] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
      borders[2] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
      borders[3] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
      break;
    
    case 1: 
      borders[0] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
      borders[1] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
      borders[2] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
      borders[3] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
      break;
    
    case 2: 
      borders[0] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
      borders[1] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
      borders[2] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
      borders[3] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
      break;
    
    case 3: 
      borders[0] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
      borders[1] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
      borders[2] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
      borders[3] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
      break;
    
    case 4: 
      borders[0] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
      borders[1] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
      borders[2] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
      borders[3] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
      break;
    
    case 5: 
      borders[0] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
      borders[1] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
      borders[2] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
      borders[3] = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
    }
    
    byte index = 0;
    
    if ((borders[0] & (borders[1] != 0 ? 0 : 1) & (borders[2] != 0 ? 0 : 1) & (borders[3] != 0 ? 0 : 1)) != 0)
    {
      index = 3;
    }
    else if (((borders[0] != 0 ? 0 : 1) & borders[1] & (borders[2] != 0 ? 0 : 1) & (borders[3] != 0 ? 0 : 1)) != 0)
    {
      index = 1;
    }
    else if (((borders[0] != 0 ? 0 : 1) & (borders[1] != 0 ? 0 : 1) & borders[2] & (borders[3] != 0 ? 0 : 1)) != 0)
    {
      index = 12;
    }
    else if (((borders[0] != 0 ? 0 : 1) & (borders[1] != 0 ? 0 : 1) & (borders[2] != 0 ? 0 : 1) & borders[3]) != 0)
    {
      index = 36;
    }
    else if ((borders[0] & borders[1] & (borders[2] != 0 ? 0 : 1) & (borders[3] != 0 ? 0 : 1)) != 0)
    {
      index = 2;
    }
    else if (((borders[0] != 0 ? 0 : 1) & (borders[1] != 0 ? 0 : 1) & borders[2] & borders[3]) != 0)
    {
      index = 24;
    }
    else if ((borders[0] & (borders[1] != 0 ? 0 : 1) & borders[2] & (borders[3] != 0 ? 0 : 1)) != 0)
    {
      index = 15;
    }
    else if ((borders[0] & (borders[1] != 0 ? 0 : 1) & (borders[2] != 0 ? 0 : 1) & borders[3]) != 0)
    {
      index = 39;
    }
    else if (((borders[0] != 0 ? 0 : 1) & borders[1] & borders[2] & (borders[3] != 0 ? 0 : 1)) != 0)
    {
      index = 13;
    }
    else if (((borders[0] != 0 ? 0 : 1) & borders[1] & (borders[2] != 0 ? 0 : 1) & borders[3]) != 0)
    {
      index = 37;
    }
    else if (((borders[0] != 0 ? 0 : 1) & borders[1] & borders[2] & borders[3]) != 0)
    {
      index = 25;
    }
    else if ((borders[0] & (borders[1] != 0 ? 0 : 1) & borders[2] & borders[3]) != 0)
    {
      index = 27;
    }
    else if ((borders[0] & borders[1] & (borders[2] != 0 ? 0 : 1) & borders[3]) != 0)
    {
      index = 38;
    }
    else if ((borders[0] & borders[1] & borders[2] & (borders[3] != 0 ? 0 : 1)) != 0)
    {
      index = 14;
    }
    else if ((borders[0] & borders[1] & borders[2] & borders[3]) != 0)
    {
      index = 26;
    }
    
    if (index == 0)
    {
      return tileIcons[index];
    }
    if (!Config.isConnectedTexturesFancy())
    {
      return tileIcons[index];
    }
    

    switch (side)
    {
    case 0: 
      borders[0] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetNorth(), side, icon, metadata) ? 0 : true);
      borders[1] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetNorth(), side, icon, metadata) ? 0 : true);
      borders[2] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetSouth(), side, icon, metadata) ? 0 : true);
      borders[3] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetSouth(), side, icon, metadata) ? 0 : true);
      break;
    
    case 1: 
      borders[0] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetSouth(), side, icon, metadata) ? 0 : true);
      borders[1] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetSouth(), side, icon, metadata) ? 0 : true);
      borders[2] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetNorth(), side, icon, metadata) ? 0 : true);
      borders[3] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetNorth(), side, icon, metadata) ? 0 : true);
      break;
    
    case 2: 
      borders[0] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetDown(), side, icon, metadata) ? 0 : true);
      borders[1] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetDown(), side, icon, metadata) ? 0 : true);
      borders[2] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetUp(), side, icon, metadata) ? 0 : true);
      borders[3] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetUp(), side, icon, metadata) ? 0 : true);
      break;
    
    case 3: 
      borders[0] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetDown(), side, icon, metadata) ? 0 : true);
      borders[1] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetDown(), side, icon, metadata) ? 0 : true);
      borders[2] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast().offsetUp(), side, icon, metadata) ? 0 : true);
      borders[3] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest().offsetUp(), side, icon, metadata) ? 0 : true);
      break;
    
    case 4: 
      borders[0] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown().offsetSouth(), side, icon, metadata) ? 0 : true);
      borders[1] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown().offsetNorth(), side, icon, metadata) ? 0 : true);
      borders[2] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp().offsetSouth(), side, icon, metadata) ? 0 : true);
      borders[3] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp().offsetNorth(), side, icon, metadata) ? 0 : true);
      break;
    
    case 5: 
      borders[0] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown().offsetNorth(), side, icon, metadata) ? 0 : true);
      borders[1] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown().offsetSouth(), side, icon, metadata) ? 0 : true);
      borders[2] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp().offsetNorth(), side, icon, metadata) ? 0 : true);
      borders[3] = (isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp().offsetSouth(), side, icon, metadata) ? 0 : true);
    }
    
    if ((index == 13) && (borders[0] != 0))
    {
      index = 4;
    }
    else if ((index == 15) && (borders[1] != 0))
    {
      index = 5;
    }
    else if ((index == 37) && (borders[2] != 0))
    {
      index = 16;
    }
    else if ((index == 39) && (borders[3] != 0))
    {
      index = 17;
    }
    else if ((index == 14) && (borders[0] != 0) && (borders[1] != 0))
    {
      index = 7;
    }
    else if ((index == 25) && (borders[0] != 0) && (borders[2] != 0))
    {
      index = 6;
    }
    else if ((index == 27) && (borders[3] != 0) && (borders[1] != 0))
    {
      index = 19;
    }
    else if ((index == 38) && (borders[3] != 0) && (borders[2] != 0))
    {
      index = 18;
    }
    else if ((index == 14) && (borders[0] == 0) && (borders[1] != 0))
    {
      index = 31;
    }
    else if ((index == 25) && (borders[0] != 0) && (borders[2] == 0))
    {
      index = 30;
    }
    else if ((index == 27) && (borders[3] == 0) && (borders[1] != 0))
    {
      index = 41;
    }
    else if ((index == 38) && (borders[3] != 0) && (borders[2] == 0))
    {
      index = 40;
    }
    else if ((index == 14) && (borders[0] != 0) && (borders[1] == 0))
    {
      index = 29;
    }
    else if ((index == 25) && (borders[0] == 0) && (borders[2] != 0))
    {
      index = 28;
    }
    else if ((index == 27) && (borders[3] != 0) && (borders[1] == 0))
    {
      index = 43;
    }
    else if ((index == 38) && (borders[3] == 0) && (borders[2] != 0))
    {
      index = 42;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] != 0) && (borders[2] != 0) && (borders[3] != 0))
    {
      index = 46;
    }
    else if ((index == 26) && (borders[0] == 0) && (borders[1] != 0) && (borders[2] != 0) && (borders[3] != 0))
    {
      index = 9;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] == 0) && (borders[2] != 0) && (borders[3] != 0))
    {
      index = 21;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] != 0) && (borders[2] == 0) && (borders[3] != 0))
    {
      index = 8;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] != 0) && (borders[2] != 0) && (borders[3] == 0))
    {
      index = 20;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] != 0) && (borders[2] == 0) && (borders[3] == 0))
    {
      index = 11;
    }
    else if ((index == 26) && (borders[0] == 0) && (borders[1] == 0) && (borders[2] != 0) && (borders[3] != 0))
    {
      index = 22;
    }
    else if ((index == 26) && (borders[0] == 0) && (borders[1] != 0) && (borders[2] == 0) && (borders[3] != 0))
    {
      index = 23;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] == 0) && (borders[2] != 0) && (borders[3] == 0))
    {
      index = 10;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] == 0) && (borders[2] == 0) && (borders[3] != 0))
    {
      index = 34;
    }
    else if ((index == 26) && (borders[0] == 0) && (borders[1] != 0) && (borders[2] != 0) && (borders[3] == 0))
    {
      index = 35;
    }
    else if ((index == 26) && (borders[0] != 0) && (borders[1] == 0) && (borders[2] == 0) && (borders[3] == 0))
    {
      index = 32;
    }
    else if ((index == 26) && (borders[0] == 0) && (borders[1] != 0) && (borders[2] == 0) && (borders[3] == 0))
    {
      index = 33;
    }
    else if ((index == 26) && (borders[0] == 0) && (borders[1] == 0) && (borders[2] != 0) && (borders[3] == 0))
    {
      index = 44;
    }
    else if ((index == 26) && (borders[0] == 0) && (borders[1] == 0) && (borders[2] == 0) && (borders[3] != 0))
    {
      index = 45;
    }
    
    return tileIcons[index];
  }
  

  private static boolean isNeighbour(ConnectedProperties cp, IBlockAccess iblockaccess, IBlockState blockState, BlockPos blockPos, int side, TextureAtlasSprite icon, int metadata)
  {
    IBlockState neighbourState = iblockaccess.getBlockState(blockPos);
    
    if (blockState == neighbourState)
    {
      return true;
    }
    if (connect == 2)
    {
      if (neighbourState == null)
      {
        return false;
      }
      if (neighbourState == AIR_DEFAULT_STATE)
      {
        return false;
      }
      

      TextureAtlasSprite neighbourIcon = getNeighbourIcon(iblockaccess, blockPos, neighbourState, side);
      return neighbourIcon == icon;
    }
    


    return neighbourState != null;
  }
  

  private static TextureAtlasSprite getNeighbourIcon(IBlockAccess iblockaccess, BlockPos blockPos, IBlockState neighbourState, int side)
  {
    neighbourState = neighbourState.getBlock().getActualState(neighbourState, iblockaccess, blockPos);
    IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().func_175023_a().func_178125_b(neighbourState);
    
    if (model == null)
    {
      return null;
    }
    

    EnumFacing facing = getFacing(side);
    List quads = model.func_177551_a(facing);
    
    if (quads.size() > 0)
    {
      BakedQuad var10 = (BakedQuad)quads.get(0);
      return var10.getSprite();
    }
    

    List quadsGeneral = model.func_177550_a();
    
    for (int i = 0; i < quadsGeneral.size(); i++)
    {
      BakedQuad quad = (BakedQuad)quadsGeneral.get(i);
      
      if (quad.getFace() == facing)
      {
        return quad.getSprite();
      }
    }
    
    return null;
  }
  




  private static TextureAtlasSprite getConnectedTextureHorizontal(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
  {
    boolean left = false;
    boolean right = false;
    

    switch (vertAxis)
    {
    case 0: 
      switch (side)
      {
      case 0: 
      case 1: 
        return null;
      
      case 2: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
        break;
      
      case 3: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
        break;
      
      case 4: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
        break;
      
      case 5: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
      }
      
      break;
    

    case 1: 
      switch (side)
      {
      case 0: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
        break;
      
      case 1: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
        break;
      
      case 2: 
      case 3: 
        return null;
      
      case 4: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
        break;
      
      case 5: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
      }
      
      break;
    

    case 2: 
      switch (side)
      {
      case 0: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
        break;
      
      case 1: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
        break;
      
      case 2: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
        break;
      
      case 3: 
        left = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
        right = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
        break;
      
      case 4: 
      case 5: 
        return null;
      }
      break;
    }
    boolean index = true;
    byte index1;
    byte index1;
    if (left) {
      byte index1;
      if (right)
      {
        index1 = 1;
      }
      else
      {
        index1 = 2; }
    } else {
      byte index1;
      if (right)
      {
        index1 = 0;
      }
      else
      {
        index1 = 3;
      }
    }
    return tileIcons[index1];
  }
  
  private static TextureAtlasSprite getConnectedTextureVertical(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
  {
    boolean bottom = false;
    boolean top = false;
    
    switch (vertAxis)
    {
    case 0: 
      if ((side == 1) || (side == 0))
      {
        return null;
      }
      
      bottom = isNeighbour(cp, blockAccess, blockState, blockPos.offsetDown(), side, icon, metadata);
      top = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
      break;
    
    case 1: 
      if ((side == 3) || (side == 2))
      {
        return null;
      }
      
      bottom = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
      top = isNeighbour(cp, blockAccess, blockState, blockPos.offsetNorth(), side, icon, metadata);
      break;
    
    case 2: 
      if ((side == 5) || (side == 4))
      {
        return null;
      }
      
      bottom = isNeighbour(cp, blockAccess, blockState, blockPos.offsetWest(), side, icon, metadata);
      top = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
    }
    
    boolean index = true;
    byte index1;
    byte index1;
    if (bottom) {
      byte index1;
      if (top)
      {
        index1 = 1;
      }
      else
      {
        index1 = 2; }
    } else {
      byte index1;
      if (top)
      {
        index1 = 0;
      }
      else
      {
        index1 = 3;
      }
    }
    return tileIcons[index1];
  }
  
  private static TextureAtlasSprite getConnectedTextureHorizontalVertical(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
  {
    TextureAtlasSprite[] tileIcons = cp.tileIcons;
    TextureAtlasSprite iconH = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
    
    if ((iconH != null) && (iconH != icon) && (iconH != tileIcons[3]))
    {
      return iconH;
    }
    

    TextureAtlasSprite iconV = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
    return iconV == tileIcons[2] ? tileIcons[6] : iconV == tileIcons[1] ? tileIcons[5] : iconV == tileIcons[0] ? tileIcons[4] : iconV;
  }
  

  private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
  {
    TextureAtlasSprite[] tileIcons = cp.tileIcons;
    TextureAtlasSprite iconV = getConnectedTextureVertical(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
    
    if ((iconV != null) && (iconV != icon) && (iconV != tileIcons[3]))
    {
      return iconV;
    }
    

    TextureAtlasSprite iconH = getConnectedTextureHorizontal(cp, blockAccess, blockState, blockPos, vertAxis, side, icon, metadata);
    return iconH == tileIcons[2] ? tileIcons[6] : iconH == tileIcons[1] ? tileIcons[5] : iconH == tileIcons[0] ? tileIcons[4] : iconH;
  }
  

  private static TextureAtlasSprite getConnectedTextureTop(ConnectedProperties cp, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int vertAxis, int side, TextureAtlasSprite icon, int metadata)
  {
    boolean top = false;
    
    switch (vertAxis)
    {
    case 0: 
      if ((side == 1) || (side == 0))
      {
        return null;
      }
      
      top = isNeighbour(cp, blockAccess, blockState, blockPos.offsetUp(), side, icon, metadata);
      break;
    
    case 1: 
      if ((side == 3) || (side == 2))
      {
        return null;
      }
      
      top = isNeighbour(cp, blockAccess, blockState, blockPos.offsetSouth(), side, icon, metadata);
      break;
    
    case 2: 
      if ((side == 5) || (side == 4))
      {
        return null;
      }
      
      top = isNeighbour(cp, blockAccess, blockState, blockPos.offsetEast(), side, icon, metadata);
    }
    
    return top ? tileIcons[0] : null;
  }
  
  public static void updateIcons(TextureMap textureMap)
  {
    blockProperties = null;
    tileProperties = null;
    spriteQuadMaps = null;
    
    if (Config.isConnectedTextures())
    {
      IResourcePack[] rps = Config.getResourcePacks();
      
      for (int locEmpty = rps.length - 1; locEmpty >= 0; locEmpty--)
      {
        IResourcePack rp = rps[locEmpty];
        updateIcons(textureMap, rp);
      }
      
      updateIcons(textureMap, Config.getDefaultResourcePack());
      ResourceLocation var4 = new ResourceLocation("mcpatcher/ctm/default/empty");
      emptySprite = textureMap.func_174942_a(var4);
      spriteQuadMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
      
      if (blockProperties.length <= 0)
      {
        blockProperties = null;
      }
      
      if (tileProperties.length <= 0)
      {
        tileProperties = null;
      }
    }
  }
  
  private static void updateIconEmpty(TextureMap textureMap) {}
  
  public static void updateIcons(TextureMap textureMap, IResourcePack rp)
  {
    String[] names = ResUtils.collectFiles(rp, "mcpatcher/ctm/", ".properties", getDefaultCtmPaths());
    Arrays.sort(names);
    List tileList = makePropertyList(tileProperties);
    List blockList = makePropertyList(blockProperties);
    
    for (int i = 0; i < names.length; i++)
    {
      String name = names[i];
      Config.dbg("ConnectedTextures: " + name);
      
      try
      {
        ResourceLocation e = new ResourceLocation(name);
        InputStream in = rp.getInputStream(e);
        
        if (in == null)
        {
          Config.warn("ConnectedTextures file not found: " + name);
        }
        else
        {
          Properties props = new Properties();
          props.load(in);
          ConnectedProperties cp = new ConnectedProperties(props, name);
          
          if (cp.isValid(name))
          {
            cp.updateIcons(textureMap);
            addToTileList(cp, tileList);
            addToBlockList(cp, blockList);
          }
        }
      }
      catch (FileNotFoundException var11)
      {
        Config.warn("ConnectedTextures file not found: " + name);
      }
      catch (Exception var12)
      {
        var12.printStackTrace();
      }
    }
    
    blockProperties = propertyListToArray(blockList);
    tileProperties = propertyListToArray(tileList);
    multipass = detectMultipass();
    Config.dbg("Multipass connected textures: " + multipass);
  }
  
  private static List makePropertyList(ConnectedProperties[][] propsArr)
  {
    ArrayList list = new ArrayList();
    
    if (propsArr != null)
    {
      for (int i = 0; i < propsArr.length; i++)
      {
        ConnectedProperties[] props = propsArr[i];
        ArrayList propList = null;
        
        if (props != null)
        {
          propList = new ArrayList(Arrays.asList(props));
        }
        
        list.add(propList);
      }
    }
    
    return list;
  }
  
  private static boolean detectMultipass()
  {
    ArrayList propList = new ArrayList();
    


    for (int props = 0; props < tileProperties.length; props++)
    {
      ConnectedProperties[] matchIconSet = tileProperties[props];
      
      if (matchIconSet != null)
      {
        propList.addAll(Arrays.asList(matchIconSet));
      }
    }
    
    for (props = 0; props < blockProperties.length; props++)
    {
      ConnectedProperties[] matchIconSet = blockProperties[props];
      
      if (matchIconSet != null)
      {
        propList.addAll(Arrays.asList(matchIconSet));
      }
    }
    
    ConnectedProperties[] var6 = (ConnectedProperties[])propList.toArray(new ConnectedProperties[propList.size()]);
    HashSet var7 = new HashSet();
    HashSet tileIconSet = new HashSet();
    
    for (int i = 0; i < var6.length; i++)
    {
      ConnectedProperties cp = var6[i];
      
      if (matchTileIcons != null)
      {
        var7.addAll(Arrays.asList(matchTileIcons));
      }
      
      if (tileIcons != null)
      {
        tileIconSet.addAll(Arrays.asList(tileIcons));
      }
    }
    
    var7.retainAll(tileIconSet);
    return !var7.isEmpty();
  }
  
  private static ConnectedProperties[][] propertyListToArray(List list)
  {
    ConnectedProperties[][] propArr = new ConnectedProperties[list.size()][];
    
    for (int i = 0; i < list.size(); i++)
    {
      List subList = (List)list.get(i);
      
      if (subList != null)
      {
        ConnectedProperties[] subArr = (ConnectedProperties[])subList.toArray(new ConnectedProperties[subList.size()]);
        propArr[i] = subArr;
      }
    }
    
    return propArr;
  }
  
  private static void addToTileList(ConnectedProperties cp, List tileList)
  {
    if (matchTileIcons != null)
    {
      for (int i = 0; i < matchTileIcons.length; i++)
      {
        TextureAtlasSprite icon = matchTileIcons[i];
        
        if (!(icon instanceof TextureAtlasSprite))
        {
          Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + icon + ", name: " + icon.getIconName());
        }
        else
        {
          int tileId = icon.getIndexInMap();
          
          if (tileId < 0)
          {
            Config.warn("Invalid tile ID: " + tileId + ", icon: " + icon.getIconName());
          }
          else
          {
            addToList(cp, tileList, tileId);
          }
        }
      }
    }
  }
  
  private static void addToBlockList(ConnectedProperties cp, List blockList)
  {
    if (matchBlocks != null)
    {
      for (int i = 0; i < matchBlocks.length; i++)
      {
        int blockId = matchBlocks[i].getBlockId();
        
        if (blockId < 0)
        {
          Config.warn("Invalid block ID: " + blockId);
        }
        else
        {
          addToList(cp, blockList, blockId);
        }
      }
    }
  }
  
  private static void addToList(ConnectedProperties cp, List list, int id)
  {
    while (id >= list.size())
    {
      list.add(null);
    }
    
    Object subList = (List)list.get(id);
    
    if (subList == null)
    {
      subList = new ArrayList();
      list.set(id, subList);
    }
    
    ((List)subList).add(cp);
  }
  
  private static String[] getDefaultCtmPaths()
  {
    ArrayList list = new ArrayList();
    String defPath = "mcpatcher/ctm/default/";
    
    if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png")))
    {
      list.add(defPath + "glass.properties");
      list.add(defPath + "glasspane.properties");
    }
    
    if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png")))
    {
      list.add(defPath + "bookshelf.properties");
    }
    
    if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png")))
    {
      list.add(defPath + "sandstone.properties");
    }
    
    String[] colors = { "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black" };
    
    for (int paths = 0; paths < colors.length; paths++)
    {
      String color = colors[paths];
      
      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + color + ".png")))
      {
        list.add(defPath + paths + "_glass_" + color + "/glass_" + color + ".properties");
        list.add(defPath + paths + "_glass_" + color + "/glass_pane_" + color + ".properties");
      }
    }
    
    String[] var5 = (String[])list.toArray(new String[list.size()]);
    return var5;
  }
  
  public static int getPaneTextureIndex(boolean linkP, boolean linkN, boolean linkYp, boolean linkYn)
  {
    return linkYn ? 16 : linkYp ? 48 : linkYn ? 32 : (!linkN) && (linkP) ? 1 : linkYn ? 17 : linkYp ? 49 : linkYn ? 33 : (linkN) && (!linkP) ? 3 : linkYn ? 19 : linkYp ? 51 : linkYn ? 35 : (linkN) && (linkP) ? 2 : linkYn ? 18 : linkYp ? 50 : linkYn ? 34 : 0;
  }
  
  public static int getReversePaneTextureIndex(int texNum)
  {
    int col = texNum % 16;
    return col == 3 ? texNum - 2 : col == 1 ? texNum + 2 : texNum;
  }
  
  public static TextureAtlasSprite getCtmTexture(ConnectedProperties cp, int ctmIndex, TextureAtlasSprite icon)
  {
    if (method != 1)
    {
      return icon;
    }
    if ((ctmIndex >= 0) && (ctmIndex < ctmIndexes.length))
    {
      int index = ctmIndexes[ctmIndex];
      TextureAtlasSprite[] ctmIcons = tileIcons;
      return (index >= 0) && (index < ctmIcons.length) ? ctmIcons[index] : icon;
    }
    

    return icon;
  }
  

  static class NamelessClass379831726
  {
    static final int[] $SwitchMap$net$minecraft$util$EnumFacing = new int[EnumFacing.values().length];
    
    static
    {
      try
      {
        $SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.DOWN.ordinal()] = 1;
      }
      catch (NoSuchFieldError localNoSuchFieldError1) {}
      



      try
      {
        $SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.UP.ordinal()] = 2;
      }
      catch (NoSuchFieldError localNoSuchFieldError2) {}
      



      try
      {
        $SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.EAST.ordinal()] = 3;
      }
      catch (NoSuchFieldError localNoSuchFieldError3) {}
      



      try
      {
        $SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.WEST.ordinal()] = 4;
      }
      catch (NoSuchFieldError localNoSuchFieldError4) {}
      



      try
      {
        $SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.NORTH.ordinal()] = 5;
      }
      catch (NoSuchFieldError localNoSuchFieldError5) {}
      



      try
      {
        $SwitchMap$net$minecraft$util$EnumFacing[EnumFacing.SOUTH.ordinal()] = 6;
      }
      catch (NoSuchFieldError localNoSuchFieldError6) {}
    }
    
    NamelessClass379831726() {}
  }
}
