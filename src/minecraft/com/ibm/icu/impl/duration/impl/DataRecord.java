package com.ibm.icu.impl.duration.impl;

import java.util.ArrayList;
import java.util.List;









public class DataRecord
{
  byte pl;
  String[][] pluralNames;
  byte[] genders;
  String[] singularNames;
  String[] halfNames;
  String[] numberNames;
  String[] mediumNames;
  String[] shortNames;
  String[] measures;
  String[] rqdSuffixes;
  String[] optSuffixes;
  String[] halves;
  byte[] halfPlacements;
  byte[] halfSupport;
  String fifteenMinutes;
  String fiveMinutes;
  boolean requiresDigitSeparator;
  String digitPrefix;
  String countSep;
  String shortUnitSep;
  String[] unitSep;
  boolean[] unitSepRequiresDP;
  boolean[] requiresSkipMarker;
  byte numberSystem;
  char zero;
  char decimalSep;
  boolean omitSingularCount;
  boolean omitDualCount;
  byte zeroHandling;
  byte decimalHandling;
  byte fractionHandling;
  String skippedUnitMarker;
  boolean allowZero;
  boolean weeksAloneOnly;
  byte useMilliseconds;
  ScopeData[] scopeData;
  
  public DataRecord() {}
  
  public static DataRecord read(String ln, RecordReader in)
  {
    if (in.open("DataRecord")) {
      DataRecord record = new DataRecord();
      pl = in.namedIndex("pl", EPluralization.names);
      pluralNames = in.stringTable("pluralName");
      genders = in.namedIndexArray("gender", EGender.names);
      singularNames = in.stringArray("singularName");
      halfNames = in.stringArray("halfName");
      numberNames = in.stringArray("numberName");
      mediumNames = in.stringArray("mediumName");
      shortNames = in.stringArray("shortName");
      measures = in.stringArray("measure");
      rqdSuffixes = in.stringArray("rqdSuffix");
      optSuffixes = in.stringArray("optSuffix");
      halves = in.stringArray("halves");
      halfPlacements = in.namedIndexArray("halfPlacement", EHalfPlacement.names);
      
      halfSupport = in.namedIndexArray("halfSupport", EHalfSupport.names);
      
      fifteenMinutes = in.string("fifteenMinutes");
      fiveMinutes = in.string("fiveMinutes");
      requiresDigitSeparator = in.bool("requiresDigitSeparator");
      digitPrefix = in.string("digitPrefix");
      countSep = in.string("countSep");
      shortUnitSep = in.string("shortUnitSep");
      unitSep = in.stringArray("unitSep");
      unitSepRequiresDP = in.boolArray("unitSepRequiresDP");
      requiresSkipMarker = in.boolArray("requiresSkipMarker");
      numberSystem = in.namedIndex("numberSystem", ENumberSystem.names);
      
      zero = in.character("zero");
      decimalSep = in.character("decimalSep");
      omitSingularCount = in.bool("omitSingularCount");
      omitDualCount = in.bool("omitDualCount");
      zeroHandling = in.namedIndex("zeroHandling", EZeroHandling.names);
      
      decimalHandling = in.namedIndex("decimalHandling", EDecimalHandling.names);
      
      fractionHandling = in.namedIndex("fractionHandling", EFractionHandling.names);
      
      skippedUnitMarker = in.string("skippedUnitMarker");
      allowZero = in.bool("allowZero");
      weeksAloneOnly = in.bool("weeksAloneOnly");
      useMilliseconds = in.namedIndex("useMilliseconds", EMilliSupport.names);
      
      if (in.open("ScopeDataList")) {
        List<ScopeData> list = new ArrayList();
        ScopeData data;
        while (null != (data = ScopeData.read(in))) {
          list.add(data);
        }
        if (in.close()) {
          scopeData = ((ScopeData[])list.toArray(new ScopeData[list.size()]));
        }
      }
      
      if (in.close()) {
        return record;
      }
    } else {
      throw new InternalError("did not find DataRecord while reading " + ln);
    }
    
    throw new InternalError("null data read while reading " + ln);
  }
  

  public void write(RecordWriter out)
  {
    out.open("DataRecord");
    out.namedIndex("pl", EPluralization.names, pl);
    out.stringTable("pluralName", pluralNames);
    out.namedIndexArray("gender", EGender.names, genders);
    out.stringArray("singularName", singularNames);
    out.stringArray("halfName", halfNames);
    out.stringArray("numberName", numberNames);
    out.stringArray("mediumName", mediumNames);
    out.stringArray("shortName", shortNames);
    out.stringArray("measure", measures);
    out.stringArray("rqdSuffix", rqdSuffixes);
    out.stringArray("optSuffix", optSuffixes);
    out.stringArray("halves", halves);
    out.namedIndexArray("halfPlacement", EHalfPlacement.names, halfPlacements);
    
    out.namedIndexArray("halfSupport", EHalfSupport.names, halfSupport);
    out.string("fifteenMinutes", fifteenMinutes);
    out.string("fiveMinutes", fiveMinutes);
    out.bool("requiresDigitSeparator", requiresDigitSeparator);
    out.string("digitPrefix", digitPrefix);
    out.string("countSep", countSep);
    out.string("shortUnitSep", shortUnitSep);
    out.stringArray("unitSep", unitSep);
    out.boolArray("unitSepRequiresDP", unitSepRequiresDP);
    out.boolArray("requiresSkipMarker", requiresSkipMarker);
    out.namedIndex("numberSystem", ENumberSystem.names, numberSystem);
    out.character("zero", zero);
    out.character("decimalSep", decimalSep);
    out.bool("omitSingularCount", omitSingularCount);
    out.bool("omitDualCount", omitDualCount);
    out.namedIndex("zeroHandling", EZeroHandling.names, zeroHandling);
    out.namedIndex("decimalHandling", EDecimalHandling.names, decimalHandling);
    
    out.namedIndex("fractionHandling", EFractionHandling.names, fractionHandling);
    
    out.string("skippedUnitMarker", skippedUnitMarker);
    out.bool("allowZero", allowZero);
    out.bool("weeksAloneOnly", weeksAloneOnly);
    out.namedIndex("useMilliseconds", EMilliSupport.names, useMilliseconds);
    if (scopeData != null) {
      out.open("ScopeDataList");
      for (int i = 0; i < scopeData.length; i++) {
        scopeData[i].write(out);
      }
      out.close();
    }
    out.close();
  }
  
  public static class ScopeData { String prefix;
    boolean requiresDigitPrefix;
    String suffix;
    
    public ScopeData() {}
    
    public void write(RecordWriter out) { out.open("ScopeData");
      out.string("prefix", prefix);
      out.bool("requiresDigitPrefix", requiresDigitPrefix);
      out.string("suffix", suffix);
      out.close();
    }
    
    public static ScopeData read(RecordReader in) {
      if (in.open("ScopeData")) {
        ScopeData scope = new ScopeData();
        prefix = in.string("prefix");
        requiresDigitPrefix = in.bool("requiresDigitPrefix");
        suffix = in.string("suffix");
        if (in.close()) {
          return scope;
        }
      }
      return null;
    }
  }
  
  public static abstract interface ETimeLimit {
    public static final byte NOLIMIT = 0;
    public static final byte LT = 1;
    public static final byte MT = 2;
    public static final String[] names = { "NOLIMIT", "LT", "MT" };
  }
  
  public static abstract interface ETimeDirection {
    public static final byte NODIRECTION = 0;
    public static final byte PAST = 1;
    public static final byte FUTURE = 2;
    public static final String[] names = { "NODIRECTION", "PAST", "FUTURE" };
  }
  
  public static abstract interface EUnitVariant {
    public static final byte PLURALIZED = 0;
    public static final byte MEDIUM = 1;
    public static final byte SHORT = 2;
    public static final String[] names = { "PLURALIZED", "MEDIUM", "SHORT" };
  }
  
  public static abstract interface ECountVariant {
    public static final byte INTEGER = 0;
    public static final byte INTEGER_CUSTOM = 1;
    public static final byte HALF_FRACTION = 2;
    public static final byte DECIMAL1 = 3;
    public static final byte DECIMAL2 = 4;
    public static final byte DECIMAL3 = 5;
    public static final String[] names = { "INTEGER", "INTEGER_CUSTOM", "HALF_FRACTION", "DECIMAL1", "DECIMAL2", "DECIMAL3" };
  }
  
  public static abstract interface EPluralization
  {
    public static final byte NONE = 0;
    public static final byte PLURAL = 1;
    public static final byte DUAL = 2;
    public static final byte PAUCAL = 3;
    public static final byte HEBREW = 4;
    public static final byte ARABIC = 5;
    public static final String[] names = { "NONE", "PLURAL", "DUAL", "PAUCAL", "HEBREW", "ARABIC" };
  }
  
  public static abstract interface EHalfPlacement
  {
    public static final byte PREFIX = 0;
    public static final byte AFTER_FIRST = 1;
    public static final byte LAST = 2;
    public static final String[] names = { "PREFIX", "AFTER_FIRST", "LAST" };
  }
  
  public static abstract interface ENumberSystem {
    public static final byte DEFAULT = 0;
    public static final byte CHINESE_TRADITIONAL = 1;
    public static final byte CHINESE_SIMPLIFIED = 2;
    public static final byte KOREAN = 3;
    public static final String[] names = { "DEFAULT", "CHINESE_TRADITIONAL", "CHINESE_SIMPLIFIED", "KOREAN" };
  }
  
  public static abstract interface EZeroHandling
  {
    public static final byte ZPLURAL = 0;
    public static final byte ZSINGULAR = 1;
    public static final String[] names = { "ZPLURAL", "ZSINGULAR" };
  }
  
  public static abstract interface EDecimalHandling {
    public static final byte DPLURAL = 0;
    public static final byte DSINGULAR = 1;
    public static final byte DSINGULAR_SUBONE = 2;
    public static final byte DPAUCAL = 3;
    public static final String[] names = { "DPLURAL", "DSINGULAR", "DSINGULAR_SUBONE", "DPAUCAL" };
  }
  
  public static abstract interface EFractionHandling
  {
    public static final byte FPLURAL = 0;
    public static final byte FSINGULAR_PLURAL = 1;
    public static final byte FSINGULAR_PLURAL_ANDAHALF = 2;
    public static final byte FPAUCAL = 3;
    public static final String[] names = { "FPLURAL", "FSINGULAR_PLURAL", "FSINGULAR_PLURAL_ANDAHALF", "FPAUCAL" };
  }
  
  public static abstract interface EHalfSupport
  {
    public static final byte YES = 0;
    public static final byte NO = 1;
    public static final byte ONE_PLUS = 2;
    public static final String[] names = { "YES", "NO", "ONE_PLUS" };
  }
  
  public static abstract interface EMilliSupport {
    public static final byte YES = 0;
    public static final byte NO = 1;
    public static final byte WITH_SECONDS = 2;
    public static final String[] names = { "YES", "NO", "WITH_SECONDS" };
  }
  
  public static abstract interface ESeparatorVariant {
    public static final byte NONE = 0;
    public static final byte SHORT = 1;
    public static final byte FULL = 2;
    public static final String[] names = { "NONE", "SHORT", "FULL" };
  }
  
  public static abstract interface EGender {
    public static final byte M = 0;
    public static final byte F = 1;
    public static final byte N = 2;
    public static final String[] names = { "M", "F", "N" };
  }
}
