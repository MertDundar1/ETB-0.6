package com.ibm.icu.util;

import com.ibm.icu.impl.ICUResourceBundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;




















































public class Region
  implements Comparable<Region>
{
  public static final int UNDEFINED_NUMERIC_CODE = -1;
  private String id;
  private int code;
  private RegionType type;
  private Region() {}
  
  public static enum RegionType
  {
    UNKNOWN, 
    





    TERRITORY, 
    





    WORLD, 
    




    CONTINENT, 
    




    SUBCONTINENT, 
    





    GROUPING, 
    





    DEPRECATED;
    





    private RegionType() {}
  }
  




  private Region containingRegion = null;
  private Set<Region> containedRegions = new TreeSet();
  private List<Region> preferredValues = null;
  
  private static boolean regionDataIsLoaded = false;
  
  private static Map<String, Region> regionIDMap = null;
  private static Map<Integer, Region> numericCodeMap = null;
  private static Map<String, Region> regionAliases = null;
  
  private static ArrayList<Region> regions = null;
  private static ArrayList<Set<Region>> availableRegions = null;
  



  private static final String UNKNOWN_REGION_ID = "ZZ";
  



  private static final String OUTLYING_OCEANIA_REGION_ID = "QO";
  



  private static final String WORLD_ID = "001";
  



  private static synchronized void loadRegionData()
  {
    if (regionDataIsLoaded) {
      return;
    }
    
    regionAliases = new HashMap();
    regionIDMap = new HashMap();
    numericCodeMap = new HashMap();
    
    availableRegions = new ArrayList(RegionType.values().length);
    

    UResourceBundle regionCodes = null;
    UResourceBundle territoryAlias = null;
    UResourceBundle codeMappings = null;
    UResourceBundle worldContainment = null;
    UResourceBundle territoryContainment = null;
    UResourceBundle groupingContainment = null;
    
    UResourceBundle rb = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "metadata", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
    regionCodes = rb.get("regionCodes");
    territoryAlias = rb.get("territoryAlias");
    
    UResourceBundle rb2 = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
    codeMappings = rb2.get("codeMappings");
    
    territoryContainment = rb2.get("territoryContainment");
    worldContainment = territoryContainment.get("001");
    groupingContainment = territoryContainment.get("grouping");
    
    String[] continentsArr = worldContainment.getStringArray();
    List<String> continents = Arrays.asList(continentsArr);
    String[] groupingArr = groupingContainment.getStringArray();
    List<String> groupings = Arrays.asList(groupingArr);
    

    int regionCodeSize = regionCodes.getSize();
    regions = new ArrayList(regionCodeSize);
    for (int i = 0; i < regionCodeSize; i++) {
      Region r = new Region();
      String id = regionCodes.getString(i);
      id = id;
      type = RegionType.TERRITORY;
      regionIDMap.put(id, r);
      if (id.matches("[0-9]{3}")) {
        code = Integer.valueOf(id).intValue();
        numericCodeMap.put(Integer.valueOf(code), r);
        type = RegionType.SUBCONTINENT;
      } else {
        code = -1;
      }
      regions.add(r);
    }
    
    Region r;
    
    for (int i = 0; i < territoryAlias.getSize(); i++) {
      UResourceBundle res = territoryAlias.get(i);
      String aliasFrom = res.getKey();
      String aliasTo = res.getString();
      
      if ((regionIDMap.containsKey(aliasTo)) && (!regionIDMap.containsKey(aliasFrom))) {
        regionAliases.put(aliasFrom, regionIDMap.get(aliasTo));
      } else {
        Region r;
        if (regionIDMap.containsKey(aliasFrom)) {
          r = (Region)regionIDMap.get(aliasFrom);
        } else {
          r = new Region();
          id = aliasFrom;
          regionIDMap.put(aliasFrom, r);
          if (aliasFrom.matches("[0-9]{3}")) {
            code = Integer.valueOf(aliasFrom).intValue();
            numericCodeMap.put(Integer.valueOf(code), r);
          } else {
            code = -1;
          }
          regions.add(r);
        }
        type = RegionType.DEPRECATED;
        List<String> aliasToRegionStrings = Arrays.asList(aliasTo.split(" "));
        preferredValues = new ArrayList();
        for (String s : aliasToRegionStrings) {
          if (regionIDMap.containsKey(s)) {
            preferredValues.add(regionIDMap.get(s));
          }
        }
      }
    }
    

    for (int i = 0; i < codeMappings.getSize(); i++) {
      UResourceBundle mapping = codeMappings.get(i);
      if (mapping.getType() == 8) {
        String[] codeMappingStrings = mapping.getStringArray();
        String codeMappingID = codeMappingStrings[0];
        Integer codeMappingNumber = Integer.valueOf(codeMappingStrings[1]);
        String codeMapping3Letter = codeMappingStrings[2];
        
        if (regionIDMap.containsKey(codeMappingID)) {
          Region r = (Region)regionIDMap.get(codeMappingID);
          code = codeMappingNumber.intValue();
          numericCodeMap.put(Integer.valueOf(code), r);
          regionAliases.put(codeMapping3Letter, r);
        }
      }
    }
    


    if (regionIDMap.containsKey("001")) {
      Region r = (Region)regionIDMap.get("001");
      type = RegionType.WORLD;
    }
    
    if (regionIDMap.containsKey("ZZ")) {
      Region r = (Region)regionIDMap.get("ZZ");
      type = RegionType.UNKNOWN;
    }
    
    for (String continent : continents) {
      if (regionIDMap.containsKey(continent)) {
        Region r = (Region)regionIDMap.get(continent);
        type = RegionType.CONTINENT;
      }
    }
    
    for (String grouping : groupings) {
      if (regionIDMap.containsKey(grouping)) {
        Region r = (Region)regionIDMap.get(grouping);
        type = RegionType.GROUPING;
      }
    }
    



    if (regionIDMap.containsKey("QO")) {
      Region r = (Region)regionIDMap.get("QO");
      type = RegionType.SUBCONTINENT;
    }
    

    for (int i = 0; i < territoryContainment.getSize(); i++) {
      UResourceBundle mapping = territoryContainment.get(i);
      String parent = mapping.getKey();
      Region parentRegion = (Region)regionIDMap.get(parent);
      
      for (int j = 0; j < mapping.getSize(); j++) {
        String child = mapping.getString(j);
        Region childRegion = (Region)regionIDMap.get(child);
        if ((parentRegion != null) && (childRegion != null))
        {

          containedRegions.add(childRegion);
          



          if (parentRegion.getType() != RegionType.GROUPING) {
            containingRegion = parentRegion;
          }
        }
      }
    }
    


    for (int i = 0; i < RegionType.values().length; i++) {
      availableRegions.add(new TreeSet());
    }
    
    for (Region ar : regions) {
      Set<Region> currentSet = (Set)availableRegions.get(type.ordinal());
      currentSet.add(ar);
      availableRegions.set(type.ordinal(), currentSet);
    }
    
    regionDataIsLoaded = true;
  }
  










  public static Region getInstance(String id)
  {
    if (id == null) {
      throw new NullPointerException();
    }
    
    loadRegionData();
    
    Region r = (Region)regionIDMap.get(id);
    
    if (r == null) {
      r = (Region)regionAliases.get(id);
    }
    
    if (r == null) {
      throw new IllegalArgumentException("Unknown region id: " + id);
    }
    
    if ((type == RegionType.DEPRECATED) && (preferredValues.size() == 1)) {
      r = (Region)preferredValues.get(0);
    }
    
    return r;
  }
  









  public static Region getInstance(int code)
  {
    loadRegionData();
    
    Region r = (Region)numericCodeMap.get(Integer.valueOf(code));
    
    if (r == null) {
      String pad = "";
      if (code < 10) {
        pad = "00";
      } else if (code < 100) {
        pad = "0";
      }
      String id = pad + Integer.toString(code);
      r = (Region)regionAliases.get(id);
    }
    
    if (r == null) {
      throw new IllegalArgumentException("Unknown region code: " + code);
    }
    
    if ((type == RegionType.DEPRECATED) && (preferredValues.size() == 1)) {
      r = (Region)preferredValues.get(0);
    }
    
    return r;
  }
  









  public static Set<Region> getAvailable(RegionType type)
  {
    loadRegionData();
    return Collections.unmodifiableSet((Set)availableRegions.get(type.ordinal()));
  }
  









  public Region getContainingRegion()
  {
    loadRegionData();
    return containingRegion;
  }
  




  public Region getContainingRegion(RegionType type)
  {
    
    



    if (containingRegion == null) {
      return null;
    }
    if (containingRegion.type.equals(type)) {
      return containingRegion;
    }
    return containingRegion.getContainingRegion(type);
  }
  














  public Set<Region> getContainedRegions()
  {
    loadRegionData();
    return Collections.unmodifiableSet(containedRegions);
  }
  











  public Set<Region> getContainedRegions(RegionType type)
  {
    loadRegionData();
    
    Set<Region> result = new TreeSet();
    Set<Region> cr = getContainedRegions();
    
    for (Region r : cr) {
      if (r.getType() == type) {
        result.add(r);
      } else {
        result.addAll(r.getContainedRegions(type));
      }
    }
    return Collections.unmodifiableSet(result);
  }
  





  public List<Region> getPreferredValues()
  {
    
    



    if (type == RegionType.DEPRECATED) {
      return Collections.unmodifiableList(preferredValues);
    }
    return null;
  }
  




  public boolean contains(Region other)
  {
    
    



    if (containedRegions.contains(other)) {
      return true;
    }
    for (Region cr : containedRegions) {
      if (cr.contains(other)) {
        return true;
      }
    }
    

    return false;
  }
  







  public String toString()
  {
    return id;
  }
  









  public int getNumericCode()
  {
    return code;
  }
  







  public RegionType getType()
  {
    return type;
  }
  




  public int compareTo(Region other)
  {
    return id.compareTo(id);
  }
}
