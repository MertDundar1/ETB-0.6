package com.ibm.icu.util;

import com.ibm.icu.impl.Grego;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.StringTokenizer;



























public class VTimeZone
  extends BasicTimeZone
{
  private static final long serialVersionUID = -6851467294127795902L;
  private BasicTimeZone tz;
  private List<String> vtzlines;
  
  public static VTimeZone create(String tzid)
  {
    VTimeZone vtz = new VTimeZone(tzid);
    tz = ((BasicTimeZone)TimeZone.getTimeZone(tzid, 0));
    olsonzid = tz.getID();
    
    return vtz;
  }
  








  public static VTimeZone create(Reader reader)
  {
    VTimeZone vtz = new VTimeZone();
    if (vtz.load(reader)) {
      return vtz;
    }
    return null;
  }
  





  public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds)
  {
    return tz.getOffset(era, year, month, day, dayOfWeek, milliseconds);
  }
  




  public void getOffset(long date, boolean local, int[] offsets)
  {
    tz.getOffset(date, local, offsets);
  }
  



  /**
   * @deprecated
   */
  public void getOffsetFromLocal(long date, int nonExistingTimeOpt, int duplicatedTimeOpt, int[] offsets)
  {
    tz.getOffsetFromLocal(date, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
  }
  




  public int getRawOffset()
  {
    return tz.getRawOffset();
  }
  




  public boolean inDaylightTime(Date date)
  {
    return tz.inDaylightTime(date);
  }
  




  public void setRawOffset(int offsetMillis)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen VTimeZone instance.");
    }
    tz.setRawOffset(offsetMillis);
  }
  




  public boolean useDaylightTime()
  {
    return tz.useDaylightTime();
  }
  




  public boolean observesDaylightTime()
  {
    return tz.observesDaylightTime();
  }
  




  public boolean hasSameRules(TimeZone other)
  {
    if (this == other) {
      return true;
    }
    if ((other instanceof VTimeZone)) {
      return tz.hasSameRules(tz);
    }
    return tz.hasSameRules(other);
  }
  








  public String getTZURL()
  {
    return tzurl;
  }
  






  public void setTZURL(String url)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen VTimeZone instance.");
    }
    tzurl = url;
  }
  








  public Date getLastModified()
  {
    return lastmod;
  }
  






  public void setLastModified(Date date)
  {
    if (isFrozen()) {
      throw new UnsupportedOperationException("Attempt to modify a frozen VTimeZone instance.");
    }
    lastmod = date;
  }
  






  public void write(Writer writer)
    throws IOException
  {
    BufferedWriter bw = new BufferedWriter(writer);
    if (vtzlines != null) {
      for (String line : vtzlines) {
        if (line.startsWith("TZURL:")) {
          if (tzurl != null) {
            bw.write("TZURL");
            bw.write(":");
            bw.write(tzurl);
            bw.write("\r\n");
          }
        } else if (line.startsWith("LAST-MODIFIED:")) {
          if (lastmod != null) {
            bw.write("LAST-MODIFIED");
            bw.write(":");
            bw.write(getUTCDateTimeString(lastmod.getTime()));
            bw.write("\r\n");
          }
        } else {
          bw.write(line);
          bw.write("\r\n");
        }
      }
      bw.flush();
    } else {
      String[] customProperties = null;
      if ((olsonzid != null) && (ICU_TZVERSION != null)) {
        customProperties = new String[1];
        customProperties[0] = ("X-TZINFO:" + olsonzid + "[" + ICU_TZVERSION + "]");
      }
      writeZone(writer, tz, customProperties);
    }
  }
  










  public void write(Writer writer, long start)
    throws IOException
  {
    TimeZoneRule[] rules = tz.getTimeZoneRules(start);
    

    RuleBasedTimeZone rbtz = new RuleBasedTimeZone(tz.getID(), (InitialTimeZoneRule)rules[0]);
    for (int i = 1; i < rules.length; i++) {
      rbtz.addTransitionRule(rules[i]);
    }
    String[] customProperties = null;
    if ((olsonzid != null) && (ICU_TZVERSION != null)) {
      customProperties = new String[1];
      customProperties[0] = ("X-TZINFO:" + olsonzid + "[" + ICU_TZVERSION + "/Partial@" + start + "]");
    }
    
    writeZone(writer, rbtz, customProperties);
  }
  















  public void writeSimple(Writer writer, long time)
    throws IOException
  {
    TimeZoneRule[] rules = tz.getSimpleTimeZoneRulesNear(time);
    

    RuleBasedTimeZone rbtz = new RuleBasedTimeZone(tz.getID(), (InitialTimeZoneRule)rules[0]);
    for (int i = 1; i < rules.length; i++) {
      rbtz.addTransitionRule(rules[i]);
    }
    String[] customProperties = null;
    if ((olsonzid != null) && (ICU_TZVERSION != null)) {
      customProperties = new String[1];
      customProperties[0] = ("X-TZINFO:" + olsonzid + "[" + ICU_TZVERSION + "/Simple@" + time + "]");
    }
    
    writeZone(writer, rbtz, customProperties);
  }
  






  public TimeZoneTransition getNextTransition(long base, boolean inclusive)
  {
    return tz.getNextTransition(base, inclusive);
  }
  




  public TimeZoneTransition getPreviousTransition(long base, boolean inclusive)
  {
    return tz.getPreviousTransition(base, inclusive);
  }
  




  public boolean hasEquivalentTransitions(TimeZone other, long start, long end)
  {
    if (this == other) {
      return true;
    }
    return tz.hasEquivalentTransitions(other, start, end);
  }
  




  public TimeZoneRule[] getTimeZoneRules()
  {
    return tz.getTimeZoneRules();
  }
  




  public TimeZoneRule[] getTimeZoneRules(long start)
  {
    return tz.getTimeZoneRules(start);
  }
  




  public Object clone()
  {
    if (isFrozen()) {
      return this;
    }
    return cloneAsThawed();
  }
  




  private String olsonzid = null;
  private String tzurl = null;
  private Date lastmod = null;
  
  private static String ICU_TZVERSION;
  
  private static final String ICU_TZINFO_PROP = "X-TZINFO";
  
  private static final int DEF_DSTSAVINGS = 3600000;
  
  private static final long DEF_TZSTARTTIME = 0L;
  
  private static final long MIN_TIME = Long.MIN_VALUE;
  
  private static final long MAX_TIME = Long.MAX_VALUE;
  
  private static final String COLON = ":";
  
  private static final String SEMICOLON = ";";
  
  private static final String EQUALS_SIGN = "=";
  
  private static final String COMMA = ",";
  
  private static final String NEWLINE = "\r\n";
  
  private static final String ICAL_BEGIN_VTIMEZONE = "BEGIN:VTIMEZONE";
  
  private static final String ICAL_END_VTIMEZONE = "END:VTIMEZONE";
  private static final String ICAL_BEGIN = "BEGIN";
  private static final String ICAL_END = "END";
  private static final String ICAL_VTIMEZONE = "VTIMEZONE";
  private static final String ICAL_TZID = "TZID";
  private static final String ICAL_STANDARD = "STANDARD";
  private static final String ICAL_DAYLIGHT = "DAYLIGHT";
  private static final String ICAL_DTSTART = "DTSTART";
  private static final String ICAL_TZOFFSETFROM = "TZOFFSETFROM";
  private static final String ICAL_TZOFFSETTO = "TZOFFSETTO";
  private static final String ICAL_RDATE = "RDATE";
  private static final String ICAL_RRULE = "RRULE";
  private static final String ICAL_TZNAME = "TZNAME";
  private static final String ICAL_TZURL = "TZURL";
  private static final String ICAL_LASTMOD = "LAST-MODIFIED";
  private static final String ICAL_FREQ = "FREQ";
  private static final String ICAL_UNTIL = "UNTIL";
  private static final String ICAL_YEARLY = "YEARLY";
  private static final String ICAL_BYMONTH = "BYMONTH";
  private static final String ICAL_BYDAY = "BYDAY";
  private static final String ICAL_BYMONTHDAY = "BYMONTHDAY";
  private static final String[] ICAL_DOW_NAMES = { "SU", "MO", "TU", "WE", "TH", "FR", "SA" };
  


  private static final int[] MONTHLENGTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  private static final int INI = 0;
  private static final int VTZ = 1;
  
  static {
    try { ICU_TZVERSION = TimeZone.getTZDataVersion();
    }
    catch (MissingResourceException e) {
      ICU_TZVERSION = null;
    }
  }
  




  private VTimeZone(String tzid)
  {
    super(tzid);
  }
  









  private boolean load(Reader reader)
  {
    try
    {
      vtzlines = new LinkedList();
      boolean eol = false;
      boolean start = false;
      boolean success = false;
      StringBuilder line = new StringBuilder();
      for (;;) {
        int ch = reader.read();
        if (ch == -1)
        {
          if ((!start) || (!line.toString().startsWith("END:VTIMEZONE"))) break;
          vtzlines.add(line.toString());
          success = true; break;
        }
        

        if (ch != 13)
        {



          if (eol) {
            if ((ch != 9) && (ch != 32))
            {
              if ((start) && 
                (line.length() > 0)) {
                vtzlines.add(line.toString());
              }
              
              line.setLength(0);
              if (ch != 10) {
                line.append((char)ch);
              }
            }
            eol = false;
          }
          else if (ch == 10)
          {
            eol = true;
            if (start) {
              if (line.toString().startsWith("END:VTIMEZONE")) {
                vtzlines.add(line.toString());
                success = true;
                break;
              }
            }
            else if (line.toString().startsWith("BEGIN:VTIMEZONE")) {
              vtzlines.add(line.toString());
              line.setLength(0);
              start = true;
              eol = false;
            }
          }
          else {
            line.append((char)ch);
          }
        }
      }
      if (!success) {
        return false;
      }
    }
    catch (IOException ioe) {
      return false;
    }
    
    return parse();
  }
  









  private boolean parse()
  {
    if ((vtzlines == null) || (vtzlines.size() == 0)) {
      return false;
    }
    


    String tzid = null;
    
    int state = 0;
    boolean dst = false;
    String from = null;
    String to = null;
    String tzname = null;
    String dtstart = null;
    boolean isRRULE = false;
    List<String> dates = null;
    List<TimeZoneRule> rules = new ArrayList();
    int initialRawOffset = 0;
    int initialDSTSavings = 0;
    long firstStart = Long.MAX_VALUE;
    
    for (String line : vtzlines) {
      int valueSep = line.indexOf(":");
      if (valueSep >= 0)
      {

        String name = line.substring(0, valueSep);
        String value = line.substring(valueSep + 1);
        
        switch (state) {
        case 0: 
          if ((name.equals("BEGIN")) && (value.equals("VTIMEZONE"))) {
            state = 1;
          }
          break;
        case 1: 
          if (name.equals("TZID")) {
            tzid = value;
          } else if (name.equals("TZURL")) {
            tzurl = value;
          } else if (name.equals("LAST-MODIFIED"))
          {

            lastmod = new Date(parseDateTimeString(value, 0));
          } else if (name.equals("BEGIN")) {
            boolean isDST = value.equals("DAYLIGHT");
            if ((value.equals("STANDARD")) || (isDST))
            {
              if (tzid == null) {
                state = 3;
              }
              else
              {
                dates = null;
                isRRULE = false;
                from = null;
                to = null;
                tzname = null;
                dst = isDST;
                state = 2;
              }
            }
            else {
              state = 3;
              break;
            }
          } else { if (!name.equals("END")) break; }
          break;
        


        case 2: 
          if (name.equals("DTSTART")) {
            dtstart = value;
          } else if (name.equals("TZNAME")) {
            tzname = value;
          } else if (name.equals("TZOFFSETFROM")) {
            from = value;
          } else if (name.equals("TZOFFSETTO")) {
            to = value;
          } else if (name.equals("RDATE"))
          {
            if (isRRULE) {
              state = 3;
            }
            else {
              if (dates == null) {
                dates = new LinkedList();
              }
              

              StringTokenizer st = new StringTokenizer(value, ",");
              while (st.hasMoreTokens()) {
                String date = st.nextToken();
                dates.add(date);
              }
            } } else if (name.equals("RRULE"))
          {
            if ((!isRRULE) && (dates != null)) {
              state = 3;
            } else {
              if (dates == null) {
                dates = new LinkedList();
              }
              isRRULE = true;
              dates.add(value);
            } } else if (name.equals("END"))
          {
            if ((dtstart == null) || (from == null) || (to == null)) {
              state = 3;
            }
            else
            {
              if (tzname == null) {
                tzname = getDefaultTZName(tzid, dst);
              }
              

              TimeZoneRule rule = null;
              int fromOffset = 0;
              int toOffset = 0;
              int rawOffset = 0;
              int dstSavings = 0;
              long start = 0L;
              try
              {
                fromOffset = offsetStrToMillis(from);
                toOffset = offsetStrToMillis(to);
                
                if (dst)
                {
                  if (toOffset - fromOffset > 0) {
                    rawOffset = fromOffset;
                    dstSavings = toOffset - fromOffset;
                  }
                  else {
                    rawOffset = toOffset - 3600000;
                    dstSavings = 3600000;
                  }
                } else {
                  rawOffset = toOffset;
                  dstSavings = 0;
                }
                

                start = parseDateTimeString(dtstart, fromOffset);
                

                Date actualStart = null;
                if (isRRULE) {
                  rule = createRuleByRRULE(tzname, rawOffset, dstSavings, start, dates, fromOffset);
                } else {
                  rule = createRuleByRDATE(tzname, rawOffset, dstSavings, start, dates, fromOffset);
                }
                if (rule != null) {
                  actualStart = rule.getFirstStart(fromOffset, 0);
                  if (actualStart.getTime() < firstStart)
                  {
                    firstStart = actualStart.getTime();
                    


                    if (dstSavings > 0) {
                      initialRawOffset = fromOffset;
                      initialDSTSavings = 0;
                    }
                    else if (fromOffset - toOffset == 3600000) {
                      initialRawOffset = fromOffset - 3600000;
                      initialDSTSavings = 3600000;
                    } else {
                      initialRawOffset = fromOffset;
                      initialDSTSavings = 0;
                    }
                  }
                }
              }
              catch (IllegalArgumentException iae) {}
              


              if (rule == null) {
                state = 3;
              }
              else {
                rules.add(rule);
                state = 1;
              }
            } }
          break;
        }
        if (state == 3) {
          vtzlines = null;
          return false;
        }
      }
    }
    
    if (rules.size() == 0) {
      return false;
    }
    

    InitialTimeZoneRule initialRule = new InitialTimeZoneRule(getDefaultTZName(tzid, false), initialRawOffset, initialDSTSavings);
    


    RuleBasedTimeZone rbtz = new RuleBasedTimeZone(tzid, initialRule);
    
    int finalRuleIdx = -1;
    int finalRuleCount = 0;
    for (int i = 0; i < rules.size(); i++) {
      TimeZoneRule r = (TimeZoneRule)rules.get(i);
      if (((r instanceof AnnualTimeZoneRule)) && 
        (((AnnualTimeZoneRule)r).getEndYear() == Integer.MAX_VALUE)) {
        finalRuleCount++;
        finalRuleIdx = i;
      }
    }
    
    if (finalRuleCount > 2)
    {
      return false;
    }
    
    if (finalRuleCount == 1) {
      if (rules.size() == 1)
      {


        rules.clear();
      }
      else {
        AnnualTimeZoneRule finalRule = (AnnualTimeZoneRule)rules.get(finalRuleIdx);
        int tmpRaw = finalRule.getRawOffset();
        int tmpDST = finalRule.getDSTSavings();
        

        Date finalStart = finalRule.getFirstStart(initialRawOffset, initialDSTSavings);
        Date start = finalStart;
        for (int i = 0; i < rules.size(); i++) {
          if (finalRuleIdx != i)
          {

            TimeZoneRule r = (TimeZoneRule)rules.get(i);
            Date lastStart = r.getFinalStart(tmpRaw, tmpDST);
            if (lastStart.after(start)) {
              start = finalRule.getNextStart(lastStart.getTime(), r.getRawOffset(), r.getDSTSavings(), false);
            }
          }
        }
        
        TimeZoneRule newRule;
        TimeZoneRule newRule;
        if (start == finalStart)
        {
          newRule = new TimeArrayTimeZoneRule(finalRule.getName(), finalRule.getRawOffset(), finalRule.getDSTSavings(), new long[] { finalStart.getTime() }, 2);


        }
        else
        {


          int[] fields = Grego.timeToFields(start.getTime(), null);
          newRule = new AnnualTimeZoneRule(finalRule.getName(), finalRule.getRawOffset(), finalRule.getDSTSavings(), finalRule.getRule(), finalRule.getStartYear(), fields[0]);
        }
        





        rules.set(finalRuleIdx, newRule);
      }
    }
    
    for (TimeZoneRule r : rules) {
      rbtz.addTransitionRule(r);
    }
    
    tz = rbtz;
    setID(tzid);
    return true;
  }
  


  private static String getDefaultTZName(String tzid, boolean isDST)
  {
    if (isDST) {
      return tzid + "(DST)";
    }
    return tzid + "(STD)";
  }
  



  private static TimeZoneRule createRuleByRRULE(String tzname, int rawOffset, int dstSavings, long start, List<String> dates, int fromOffset)
  {
    if ((dates == null) || (dates.size() == 0)) {
      return null;
    }
    
    String rrule = (String)dates.get(0);
    
    long[] until = new long[1];
    int[] ruleFields = parseRRULE(rrule, until);
    if (ruleFields == null)
    {
      return null;
    }
    
    int month = ruleFields[0];
    int dayOfWeek = ruleFields[1];
    int nthDayOfWeek = ruleFields[2];
    int dayOfMonth = ruleFields[3];
    
    if (dates.size() == 1)
    {
      if (ruleFields.length > 4)
      {

        if ((ruleFields.length != 10) || (month == -1) || (dayOfWeek == 0))
        {

          return null;
        }
        int firstDay = 31;
        int[] days = new int[7];
        for (int i = 0; i < 7; i++) {
          days[i] = ruleFields[(3 + i)];
          


          days[i] = (days[i] > 0 ? days[i] : MONTHLENGTH[month] + days[i] + 1);
          firstDay = days[i] < firstDay ? days[i] : firstDay;
        }
        
        for (int i = 1; i < 7; i++) {
          boolean found = false;
          for (int j = 0; j < 7; j++) {
            if (days[j] == firstDay + i) {
              found = true;
              break;
            }
          }
          if (!found)
          {
            return null;
          }
        }
        
        dayOfMonth = firstDay;
      }
    }
    else
    {
      if ((month == -1) || (dayOfWeek == 0) || (dayOfMonth == 0))
      {
        return null;
      }
      

      if (dates.size() > 7) {
        return null;
      }
      




      int earliestMonth = month;
      int daysCount = ruleFields.length - 3;
      int earliestDay = 31;
      for (int i = 0; i < daysCount; i++) {
        int dom = ruleFields[(3 + i)];
        dom = dom > 0 ? dom : MONTHLENGTH[month] + dom + 1;
        earliestDay = dom < earliestDay ? dom : earliestDay;
      }
      
      int anotherMonth = -1;
      for (int i = 1; i < dates.size(); i++) {
        rrule = (String)dates.get(i);
        long[] unt = new long[1];
        int[] fields = parseRRULE(rrule, unt);
        

        if (unt[0] > until[0]) {
          until = unt;
        }
        

        if ((fields[0] == -1) || (fields[1] == 0) || (fields[3] == 0)) {
          return null;
        }
        
        int count = fields.length - 3;
        if (daysCount + count > 7)
        {
          return null;
        }
        

        if (fields[1] != dayOfWeek) {
          return null;
        }
        
        if (fields[0] != month) {
          if (anotherMonth == -1) {
            int diff = fields[0] - month;
            if ((diff == -11) || (diff == -1))
            {
              anotherMonth = fields[0];
              earliestMonth = anotherMonth;
              
              earliestDay = 31;
            } else if ((diff == 11) || (diff == 1))
            {
              anotherMonth = fields[0];
            }
            else {
              return null;
            }
          } else if ((fields[0] != month) && (fields[0] != anotherMonth))
          {
            return null;
          }
        }
        
        if (fields[0] == earliestMonth) {
          for (int j = 0; j < count; j++) {
            int dom = fields[(3 + j)];
            dom = dom > 0 ? dom : MONTHLENGTH[fields[0]] + dom + 1;
            earliestDay = dom < earliestDay ? dom : earliestDay;
          }
        }
        daysCount += count;
      }
      if (daysCount != 7)
      {
        return null;
      }
      month = earliestMonth;
      dayOfMonth = earliestDay;
    }
    

    int[] dfields = Grego.timeToFields(start + fromOffset, null);
    int startYear = dfields[0];
    if (month == -1)
    {
      month = dfields[1];
    }
    if ((dayOfWeek == 0) && (nthDayOfWeek == 0) && (dayOfMonth == 0))
    {
      dayOfMonth = dfields[2];
    }
    int timeInDay = dfields[5];
    
    int endYear = Integer.MAX_VALUE;
    if (until[0] != Long.MIN_VALUE) {
      Grego.timeToFields(until[0], dfields);
      endYear = dfields[0];
    }
    

    DateTimeRule adtr = null;
    if ((dayOfWeek == 0) && (nthDayOfWeek == 0) && (dayOfMonth != 0))
    {
      adtr = new DateTimeRule(month, dayOfMonth, timeInDay, 0);
    } else if ((dayOfWeek != 0) && (nthDayOfWeek != 0) && (dayOfMonth == 0))
    {
      adtr = new DateTimeRule(month, nthDayOfWeek, dayOfWeek, timeInDay, 0);
    } else if ((dayOfWeek != 0) && (nthDayOfWeek == 0) && (dayOfMonth != 0))
    {

      adtr = new DateTimeRule(month, dayOfMonth, dayOfWeek, true, timeInDay, 0);
    }
    else {
      return null;
    }
    
    return new AnnualTimeZoneRule(tzname, rawOffset, dstSavings, adtr, startYear, endYear);
  }
  





  private static final int TZI = 2;
  




  private static final int ERR = 3;
  



  private static int[] parseRRULE(String rrule, long[] until)
  {
    int month = -1;
    int dayOfWeek = 0;
    int nthDayOfWeek = 0;
    int[] dayOfMonth = null;
    
    long untilTime = Long.MIN_VALUE;
    boolean yearly = false;
    boolean parseError = false;
    StringTokenizer st = new StringTokenizer(rrule, ";");
    
    while (st.hasMoreTokens())
    {
      String prop = st.nextToken();
      int sep = prop.indexOf("=");
      String value; if (sep != -1) {
        String attr = prop.substring(0, sep);
        value = prop.substring(sep + 1);
      } else {
        parseError = true;
        break; }
      String value;
      String attr;
      if (attr.equals("FREQ"))
      {
        if (value.equals("YEARLY")) {
          yearly = true;
        } else {
          parseError = true;
          break;
        }
      } else if (attr.equals("UNTIL"))
      {
        try {
          untilTime = parseDateTimeString(value, 0);
        } catch (IllegalArgumentException iae) {
          parseError = true;
          break;
        }
      } else { if (attr.equals("BYMONTH"))
        {

          if (value.length() > 2) {
            parseError = true;
            break;
          }
          try {
            month = Integer.parseInt(value) - 1;
            if ((month < 0) || (month >= 12)) {
              parseError = true;
              break;
            }
          } catch (NumberFormatException nfe) {
            parseError = true;
            break;
          } }
        if (attr.equals("BYDAY"))
        {




          int length = value.length();
          if ((length < 2) || (length > 4)) {
            parseError = true;
            break;
          }
          if (length > 2)
          {
            int sign = 1;
            if (value.charAt(0) == '+') {
              sign = 1;
            } else if (value.charAt(0) == '-') {
              sign = -1;
            } else if (length == 4) {
              parseError = true;
              break;
            }
            try {
              int n = Integer.parseInt(value.substring(length - 3, length - 2));
              if ((n == 0) || (n > 4)) {
                parseError = true;
                break;
              }
              nthDayOfWeek = n * sign;
            } catch (NumberFormatException nfe) {
              parseError = true;
              break;
            }
            value = value.substring(length - 2);
          }
          
          for (int wday = 0; wday < ICAL_DOW_NAMES.length; wday++) {
            if (value.equals(ICAL_DOW_NAMES[wday])) {
              break;
            }
          }
          if (wday < ICAL_DOW_NAMES.length)
          {
            dayOfWeek = wday + 1;
          } else {
            parseError = true;
            break;
          }
        } else if (attr.equals("BYMONTHDAY"))
        {



          StringTokenizer days = new StringTokenizer(value, ",");
          int count = days.countTokens();
          dayOfMonth = new int[count];
          int index = 0;
          for (;;) { if (days.hasMoreTokens()) {
              try {
                dayOfMonth[(index++)] = Integer.parseInt(days.nextToken());
              } catch (NumberFormatException nfe) {
                parseError = true;
              }
            }
          }
        }
      }
    }
    if (parseError) {
      return null;
    }
    if (!yearly)
    {
      return null;
    }
    
    until[0] = untilTime;
    
    int[] results;
    if (dayOfMonth == null) {
      int[] results = new int[4];
      results[3] = 0;
    } else {
      results = new int[3 + dayOfMonth.length];
      for (int i = 0; i < dayOfMonth.length; i++) {
        results[(3 + i)] = dayOfMonth[i];
      }
    }
    results[0] = month;
    results[1] = dayOfWeek;
    results[2] = nthDayOfWeek;
    return results;
  }
  


  private static TimeZoneRule createRuleByRDATE(String tzname, int rawOffset, int dstSavings, long start, List<String> dates, int fromOffset)
  {
    long[] times;
    

    if ((dates == null) || (dates.size() == 0))
    {

      long[] times = new long[1];
      times[0] = start;
    } else {
      times = new long[dates.size()];
      int idx = 0;
      try {
        for (String date : dates) {
          times[(idx++)] = parseDateTimeString(date, fromOffset);
        }
      } catch (IllegalArgumentException iae) {
        return null;
      }
    }
    return new TimeArrayTimeZoneRule(tzname, rawOffset, dstSavings, times, 2);
  }
  


  private void writeZone(Writer w, BasicTimeZone basictz, String[] customProperties)
    throws IOException
  {
    writeHeader(w);
    
    if ((customProperties != null) && (customProperties.length > 0)) {
      for (int i = 0; i < customProperties.length; i++) {
        if (customProperties[i] != null) {
          w.write(customProperties[i]);
          w.write("\r\n");
        }
      }
    }
    
    long t = Long.MIN_VALUE;
    String dstName = null;
    int dstFromOffset = 0;
    int dstFromDSTSavings = 0;
    int dstToOffset = 0;
    int dstStartYear = 0;
    int dstMonth = 0;
    int dstDayOfWeek = 0;
    int dstWeekInMonth = 0;
    int dstMillisInDay = 0;
    long dstStartTime = 0L;
    long dstUntilTime = 0L;
    int dstCount = 0;
    AnnualTimeZoneRule finalDstRule = null;
    
    String stdName = null;
    int stdFromOffset = 0;
    int stdFromDSTSavings = 0;
    int stdToOffset = 0;
    int stdStartYear = 0;
    int stdMonth = 0;
    int stdDayOfWeek = 0;
    int stdWeekInMonth = 0;
    int stdMillisInDay = 0;
    long stdStartTime = 0L;
    long stdUntilTime = 0L;
    int stdCount = 0;
    AnnualTimeZoneRule finalStdRule = null;
    
    int[] dtfields = new int[6];
    boolean hasTransitions = false;
    
    for (;;)
    {
      TimeZoneTransition tzt = basictz.getNextTransition(t, false);
      if (tzt == null) {
        break;
      }
      hasTransitions = true;
      t = tzt.getTime();
      String name = tzt.getTo().getName();
      boolean isDst = tzt.getTo().getDSTSavings() != 0;
      int fromOffset = tzt.getFrom().getRawOffset() + tzt.getFrom().getDSTSavings();
      int fromDSTSavings = tzt.getFrom().getDSTSavings();
      int toOffset = tzt.getTo().getRawOffset() + tzt.getTo().getDSTSavings();
      Grego.timeToFields(tzt.getTime() + fromOffset, dtfields);
      int weekInMonth = Grego.getDayOfWeekInMonth(dtfields[0], dtfields[1], dtfields[2]);
      int year = dtfields[0];
      boolean sameRule = false;
      if (isDst) {
        if ((finalDstRule == null) && ((tzt.getTo() instanceof AnnualTimeZoneRule)) && 
          (((AnnualTimeZoneRule)tzt.getTo()).getEndYear() == Integer.MAX_VALUE)) {
          finalDstRule = (AnnualTimeZoneRule)tzt.getTo();
        }
        
        if (dstCount > 0) {
          if ((year == dstStartYear + dstCount) && (name.equals(dstName)) && (dstFromOffset == fromOffset) && (dstToOffset == toOffset) && (dstMonth == dtfields[1]) && (dstDayOfWeek == dtfields[3]) && (dstWeekInMonth == weekInMonth) && (dstMillisInDay == dtfields[5]))
          {







            dstUntilTime = t;
            dstCount++;
            sameRule = true;
          }
          if (!sameRule) {
            if (dstCount == 1) {
              writeZonePropsByTime(w, true, dstName, dstFromOffset, dstToOffset, dstStartTime, true);
            }
            else {
              writeZonePropsByDOW(w, true, dstName, dstFromOffset, dstToOffset, dstMonth, dstWeekInMonth, dstDayOfWeek, dstStartTime, dstUntilTime);
            }
          }
        }
        
        if (!sameRule)
        {
          dstName = name;
          dstFromOffset = fromOffset;
          dstFromDSTSavings = fromDSTSavings;
          dstToOffset = toOffset;
          dstStartYear = year;
          dstMonth = dtfields[1];
          dstDayOfWeek = dtfields[3];
          dstWeekInMonth = weekInMonth;
          dstMillisInDay = dtfields[5];
          dstStartTime = dstUntilTime = t;
          dstCount = 1;
        }
        if ((finalStdRule != null) && (finalDstRule != null)) {
          break;
        }
      } else {
        if ((finalStdRule == null) && ((tzt.getTo() instanceof AnnualTimeZoneRule)) && 
          (((AnnualTimeZoneRule)tzt.getTo()).getEndYear() == Integer.MAX_VALUE)) {
          finalStdRule = (AnnualTimeZoneRule)tzt.getTo();
        }
        
        if (stdCount > 0) {
          if ((year == stdStartYear + stdCount) && (name.equals(stdName)) && (stdFromOffset == fromOffset) && (stdToOffset == toOffset) && (stdMonth == dtfields[1]) && (stdDayOfWeek == dtfields[3]) && (stdWeekInMonth == weekInMonth) && (stdMillisInDay == dtfields[5]))
          {







            stdUntilTime = t;
            stdCount++;
            sameRule = true;
          }
          if (!sameRule) {
            if (stdCount == 1) {
              writeZonePropsByTime(w, false, stdName, stdFromOffset, stdToOffset, stdStartTime, true);
            }
            else {
              writeZonePropsByDOW(w, false, stdName, stdFromOffset, stdToOffset, stdMonth, stdWeekInMonth, stdDayOfWeek, stdStartTime, stdUntilTime);
            }
          }
        }
        
        if (!sameRule)
        {
          stdName = name;
          stdFromOffset = fromOffset;
          stdFromDSTSavings = fromDSTSavings;
          stdToOffset = toOffset;
          stdStartYear = year;
          stdMonth = dtfields[1];
          stdDayOfWeek = dtfields[3];
          stdWeekInMonth = weekInMonth;
          stdMillisInDay = dtfields[5];
          stdStartTime = stdUntilTime = t;
          stdCount = 1;
        }
        if ((finalStdRule != null) && (finalDstRule != null)) {
          break;
        }
      }
    }
    if (!hasTransitions)
    {
      int offset = basictz.getOffset(0L);
      boolean isDst = offset != basictz.getRawOffset();
      writeZonePropsByTime(w, isDst, getDefaultTZName(basictz.getID(), isDst), offset, offset, 0L - offset, false);
    }
    else {
      if (dstCount > 0) {
        if (finalDstRule == null) {
          if (dstCount == 1) {
            writeZonePropsByTime(w, true, dstName, dstFromOffset, dstToOffset, dstStartTime, true);
          }
          else {
            writeZonePropsByDOW(w, true, dstName, dstFromOffset, dstToOffset, dstMonth, dstWeekInMonth, dstDayOfWeek, dstStartTime, dstUntilTime);
          }
          
        }
        else if (dstCount == 1) {
          writeFinalRule(w, true, finalDstRule, dstFromOffset - dstFromDSTSavings, dstFromDSTSavings, dstStartTime);


        }
        else if (isEquivalentDateRule(dstMonth, dstWeekInMonth, dstDayOfWeek, finalDstRule.getRule())) {
          writeZonePropsByDOW(w, true, dstName, dstFromOffset, dstToOffset, dstMonth, dstWeekInMonth, dstDayOfWeek, dstStartTime, Long.MAX_VALUE);
        }
        else
        {
          writeZonePropsByDOW(w, true, dstName, dstFromOffset, dstToOffset, dstMonth, dstWeekInMonth, dstDayOfWeek, dstStartTime, dstUntilTime);
          
          writeFinalRule(w, true, finalDstRule, dstFromOffset - dstFromDSTSavings, dstFromDSTSavings, dstStartTime);
        }
      }
      


      if (stdCount > 0) {
        if (finalStdRule == null) {
          if (stdCount == 1) {
            writeZonePropsByTime(w, false, stdName, stdFromOffset, stdToOffset, stdStartTime, true);
          }
          else {
            writeZonePropsByDOW(w, false, stdName, stdFromOffset, stdToOffset, stdMonth, stdWeekInMonth, stdDayOfWeek, stdStartTime, stdUntilTime);
          }
          
        }
        else if (stdCount == 1) {
          writeFinalRule(w, false, finalStdRule, stdFromOffset - stdFromDSTSavings, stdFromDSTSavings, stdStartTime);


        }
        else if (isEquivalentDateRule(stdMonth, stdWeekInMonth, stdDayOfWeek, finalStdRule.getRule())) {
          writeZonePropsByDOW(w, false, stdName, stdFromOffset, stdToOffset, stdMonth, stdWeekInMonth, stdDayOfWeek, stdStartTime, Long.MAX_VALUE);
        }
        else
        {
          writeZonePropsByDOW(w, false, stdName, stdFromOffset, stdToOffset, stdMonth, stdWeekInMonth, stdDayOfWeek, stdStartTime, stdUntilTime);
          
          writeFinalRule(w, false, finalStdRule, stdFromOffset - stdFromDSTSavings, stdFromDSTSavings, stdStartTime);
        }
      }
    }
    


    writeFooter(w);
  }
  



  private static boolean isEquivalentDateRule(int month, int weekInMonth, int dayOfWeek, DateTimeRule dtrule)
  {
    if ((month != dtrule.getRuleMonth()) || (dayOfWeek != dtrule.getRuleDayOfWeek())) {
      return false;
    }
    if (dtrule.getTimeRuleType() != 0)
    {
      return false;
    }
    if ((dtrule.getDateRuleType() == 1) && (dtrule.getRuleWeekInMonth() == weekInMonth))
    {
      return true;
    }
    int ruleDOM = dtrule.getRuleDayOfMonth();
    if (dtrule.getDateRuleType() == 2) {
      if ((ruleDOM % 7 == 1) && ((ruleDOM + 6) / 7 == weekInMonth)) {
        return true;
      }
      if ((month != 1) && ((MONTHLENGTH[month] - ruleDOM) % 7 == 6) && (weekInMonth == -1 * ((MONTHLENGTH[month] - ruleDOM + 1) / 7)))
      {
        return true;
      }
    }
    if (dtrule.getDateRuleType() == 3) {
      if ((ruleDOM % 7 == 0) && (ruleDOM / 7 == weekInMonth)) {
        return true;
      }
      if ((month != 1) && ((MONTHLENGTH[month] - ruleDOM) % 7 == 0) && (weekInMonth == -1 * ((MONTHLENGTH[month] - ruleDOM) / 7 + 1)))
      {
        return true;
      }
    }
    return false;
  }
  


  private static void writeZonePropsByTime(Writer writer, boolean isDst, String tzname, int fromOffset, int toOffset, long time, boolean withRDATE)
    throws IOException
  {
    beginZoneProps(writer, isDst, tzname, fromOffset, toOffset, time);
    if (withRDATE) {
      writer.write("RDATE");
      writer.write(":");
      writer.write(getDateTimeString(time + fromOffset));
      writer.write("\r\n");
    }
    endZoneProps(writer, isDst);
  }
  


  private static void writeZonePropsByDOM(Writer writer, boolean isDst, String tzname, int fromOffset, int toOffset, int month, int dayOfMonth, long startTime, long untilTime)
    throws IOException
  {
    beginZoneProps(writer, isDst, tzname, fromOffset, toOffset, startTime);
    
    beginRRULE(writer, month);
    writer.write("BYMONTHDAY");
    writer.write("=");
    writer.write(Integer.toString(dayOfMonth));
    
    if (untilTime != Long.MAX_VALUE) {
      appendUNTIL(writer, getDateTimeString(untilTime + fromOffset));
    }
    writer.write("\r\n");
    
    endZoneProps(writer, isDst);
  }
  


  private static void writeZonePropsByDOW(Writer writer, boolean isDst, String tzname, int fromOffset, int toOffset, int month, int weekInMonth, int dayOfWeek, long startTime, long untilTime)
    throws IOException
  {
    beginZoneProps(writer, isDst, tzname, fromOffset, toOffset, startTime);
    
    beginRRULE(writer, month);
    writer.write("BYDAY");
    writer.write("=");
    writer.write(Integer.toString(weekInMonth));
    writer.write(ICAL_DOW_NAMES[(dayOfWeek - 1)]);
    
    if (untilTime != Long.MAX_VALUE) {
      appendUNTIL(writer, getDateTimeString(untilTime + fromOffset));
    }
    writer.write("\r\n");
    
    endZoneProps(writer, isDst);
  }
  



  private static void writeZonePropsByDOW_GEQ_DOM(Writer writer, boolean isDst, String tzname, int fromOffset, int toOffset, int month, int dayOfMonth, int dayOfWeek, long startTime, long untilTime)
    throws IOException
  {
    if (dayOfMonth % 7 == 1)
    {
      writeZonePropsByDOW(writer, isDst, tzname, fromOffset, toOffset, month, (dayOfMonth + 6) / 7, dayOfWeek, startTime, untilTime);
    }
    else if ((month != 1) && ((MONTHLENGTH[month] - dayOfMonth) % 7 == 6))
    {
      writeZonePropsByDOW(writer, isDst, tzname, fromOffset, toOffset, month, -1 * ((MONTHLENGTH[month] - dayOfMonth + 1) / 7), dayOfWeek, startTime, untilTime);
    }
    else
    {
      beginZoneProps(writer, isDst, tzname, fromOffset, toOffset, startTime);
      

      int startDay = dayOfMonth;
      int currentMonthDays = 7;
      
      if (dayOfMonth <= 0)
      {
        int prevMonthDays = 1 - dayOfMonth;
        currentMonthDays -= prevMonthDays;
        
        int prevMonth = month - 1 < 0 ? 11 : month - 1;
        



        writeZonePropsByDOW_GEQ_DOM_sub(writer, prevMonth, -prevMonthDays, dayOfWeek, prevMonthDays, Long.MAX_VALUE, fromOffset);
        

        startDay = 1;
      } else if (dayOfMonth + 6 > MONTHLENGTH[month])
      {

        int nextMonthDays = dayOfMonth + 6 - MONTHLENGTH[month];
        currentMonthDays -= nextMonthDays;
        
        int nextMonth = month + 1 > 11 ? 0 : month + 1;
        
        writeZonePropsByDOW_GEQ_DOM_sub(writer, nextMonth, 1, dayOfWeek, nextMonthDays, Long.MAX_VALUE, fromOffset);
      }
      writeZonePropsByDOW_GEQ_DOM_sub(writer, month, startDay, dayOfWeek, currentMonthDays, untilTime, fromOffset);
      endZoneProps(writer, isDst);
    }
  }
  



  private static void writeZonePropsByDOW_GEQ_DOM_sub(Writer writer, int month, int dayOfMonth, int dayOfWeek, int numDays, long untilTime, int fromOffset)
    throws IOException
  {
    int startDayNum = dayOfMonth;
    boolean isFeb = month == 1;
    if ((dayOfMonth < 0) && (!isFeb))
    {
      startDayNum = MONTHLENGTH[month] + dayOfMonth + 1;
    }
    beginRRULE(writer, month);
    writer.write("BYDAY");
    writer.write("=");
    writer.write(ICAL_DOW_NAMES[(dayOfWeek - 1)]);
    writer.write(";");
    writer.write("BYMONTHDAY");
    writer.write("=");
    
    writer.write(Integer.toString(startDayNum));
    for (int i = 1; i < numDays; i++) {
      writer.write(",");
      writer.write(Integer.toString(startDayNum + i));
    }
    
    if (untilTime != Long.MAX_VALUE) {
      appendUNTIL(writer, getDateTimeString(untilTime + fromOffset));
    }
    writer.write("\r\n");
  }
  



  private static void writeZonePropsByDOW_LEQ_DOM(Writer writer, boolean isDst, String tzname, int fromOffset, int toOffset, int month, int dayOfMonth, int dayOfWeek, long startTime, long untilTime)
    throws IOException
  {
    if (dayOfMonth % 7 == 0)
    {
      writeZonePropsByDOW(writer, isDst, tzname, fromOffset, toOffset, month, dayOfMonth / 7, dayOfWeek, startTime, untilTime);
    }
    else if ((month != 1) && ((MONTHLENGTH[month] - dayOfMonth) % 7 == 0))
    {
      writeZonePropsByDOW(writer, isDst, tzname, fromOffset, toOffset, month, -1 * ((MONTHLENGTH[month] - dayOfMonth) / 7 + 1), dayOfWeek, startTime, untilTime);
    }
    else if ((month == 1) && (dayOfMonth == 29))
    {
      writeZonePropsByDOW(writer, isDst, tzname, fromOffset, toOffset, 1, -1, dayOfWeek, startTime, untilTime);
    }
    else
    {
      writeZonePropsByDOW_GEQ_DOM(writer, isDst, tzname, fromOffset, toOffset, month, dayOfMonth - 6, dayOfWeek, startTime, untilTime);
    }
  }
  



  private static void writeFinalRule(Writer writer, boolean isDst, AnnualTimeZoneRule rule, int fromRawOffset, int fromDSTSavings, long startTime)
    throws IOException
  {
    DateTimeRule dtrule = toWallTimeRule(rule.getRule(), fromRawOffset, fromDSTSavings);
    




    int timeInDay = dtrule.getRuleMillisInDay();
    if (timeInDay < 0) {
      startTime += 0 - timeInDay;
    } else if (timeInDay >= 86400000) {
      startTime -= timeInDay - 86399999;
    }
    
    int toOffset = rule.getRawOffset() + rule.getDSTSavings();
    switch (dtrule.getDateRuleType()) {
    case 0: 
      writeZonePropsByDOM(writer, isDst, rule.getName(), fromRawOffset + fromDSTSavings, toOffset, dtrule.getRuleMonth(), dtrule.getRuleDayOfMonth(), startTime, Long.MAX_VALUE);
      
      break;
    case 1: 
      writeZonePropsByDOW(writer, isDst, rule.getName(), fromRawOffset + fromDSTSavings, toOffset, dtrule.getRuleMonth(), dtrule.getRuleWeekInMonth(), dtrule.getRuleDayOfWeek(), startTime, Long.MAX_VALUE);
      
      break;
    case 2: 
      writeZonePropsByDOW_GEQ_DOM(writer, isDst, rule.getName(), fromRawOffset + fromDSTSavings, toOffset, dtrule.getRuleMonth(), dtrule.getRuleDayOfMonth(), dtrule.getRuleDayOfWeek(), startTime, Long.MAX_VALUE);
      
      break;
    case 3: 
      writeZonePropsByDOW_LEQ_DOM(writer, isDst, rule.getName(), fromRawOffset + fromDSTSavings, toOffset, dtrule.getRuleMonth(), dtrule.getRuleDayOfMonth(), dtrule.getRuleDayOfWeek(), startTime, Long.MAX_VALUE);
    }
    
  }
  



  private static DateTimeRule toWallTimeRule(DateTimeRule rule, int rawOffset, int dstSavings)
  {
    if (rule.getTimeRuleType() == 0) {
      return rule;
    }
    int wallt = rule.getRuleMillisInDay();
    if (rule.getTimeRuleType() == 2) {
      wallt += rawOffset + dstSavings;
    } else if (rule.getTimeRuleType() == 1) {
      wallt += dstSavings;
    }
    
    int month = -1;int dom = 0;int dow = 0;int dtype = -1;
    int dshift = 0;
    if (wallt < 0) {
      dshift = -1;
      wallt += 86400000;
    } else if (wallt >= 86400000) {
      dshift = 1;
      wallt -= 86400000;
    }
    
    month = rule.getRuleMonth();
    dom = rule.getRuleDayOfMonth();
    dow = rule.getRuleDayOfWeek();
    dtype = rule.getDateRuleType();
    
    if (dshift != 0) {
      if (dtype == 1)
      {
        int wim = rule.getRuleWeekInMonth();
        if (wim > 0) {
          dtype = 2;
          dom = 7 * (wim - 1) + 1;
        } else {
          dtype = 3;
          dom = MONTHLENGTH[month] + 7 * (wim + 1);
        }
      }
      

      dom += dshift;
      if (dom == 0) {
        month--;
        month = month < 0 ? 11 : month;
        dom = MONTHLENGTH[month];
      } else if (dom > MONTHLENGTH[month]) {
        month++;
        month = month > 11 ? 0 : month;
        dom = 1;
      }
      if (dtype != 0)
      {
        dow += dshift;
        if (dow < 1) {
          dow = 7;
        } else if (dow > 7) {
          dow = 1;
        }
      }
    }
    DateTimeRule modifiedRule;
    DateTimeRule modifiedRule;
    if (dtype == 0) {
      modifiedRule = new DateTimeRule(month, dom, wallt, 0);
    } else {
      modifiedRule = new DateTimeRule(month, dom, dow, dtype == 2, wallt, 0);
    }
    
    return modifiedRule;
  }
  

  private static void beginZoneProps(Writer writer, boolean isDst, String tzname, int fromOffset, int toOffset, long startTime)
    throws IOException
  {
    writer.write("BEGIN");
    writer.write(":");
    if (isDst) {
      writer.write("DAYLIGHT");
    } else {
      writer.write("STANDARD");
    }
    writer.write("\r\n");
    

    writer.write("TZOFFSETTO");
    writer.write(":");
    writer.write(millisToOffset(toOffset));
    writer.write("\r\n");
    

    writer.write("TZOFFSETFROM");
    writer.write(":");
    writer.write(millisToOffset(fromOffset));
    writer.write("\r\n");
    

    writer.write("TZNAME");
    writer.write(":");
    writer.write(tzname);
    writer.write("\r\n");
    

    writer.write("DTSTART");
    writer.write(":");
    writer.write(getDateTimeString(startTime + fromOffset));
    writer.write("\r\n");
  }
  


  private static void endZoneProps(Writer writer, boolean isDst)
    throws IOException
  {
    writer.write("END");
    writer.write(":");
    if (isDst) {
      writer.write("DAYLIGHT");
    } else {
      writer.write("STANDARD");
    }
    writer.write("\r\n");
  }
  

  private static void beginRRULE(Writer writer, int month)
    throws IOException
  {
    writer.write("RRULE");
    writer.write(":");
    writer.write("FREQ");
    writer.write("=");
    writer.write("YEARLY");
    writer.write(";");
    writer.write("BYMONTH");
    writer.write("=");
    writer.write(Integer.toString(month + 1));
    writer.write(";");
  }
  

  private static void appendUNTIL(Writer writer, String until)
    throws IOException
  {
    if (until != null) {
      writer.write(";");
      writer.write("UNTIL");
      writer.write("=");
      writer.write(until);
    }
  }
  

  private void writeHeader(Writer writer)
    throws IOException
  {
    writer.write("BEGIN");
    writer.write(":");
    writer.write("VTIMEZONE");
    writer.write("\r\n");
    writer.write("TZID");
    writer.write(":");
    writer.write(tz.getID());
    writer.write("\r\n");
    if (tzurl != null) {
      writer.write("TZURL");
      writer.write(":");
      writer.write(tzurl);
      writer.write("\r\n");
    }
    if (lastmod != null) {
      writer.write("LAST-MODIFIED");
      writer.write(":");
      writer.write(getUTCDateTimeString(lastmod.getTime()));
      writer.write("\r\n");
    }
  }
  

  private static void writeFooter(Writer writer)
    throws IOException
  {
    writer.write("END");
    writer.write(":");
    writer.write("VTIMEZONE");
    writer.write("\r\n");
  }
  


  private static String getDateTimeString(long time)
  {
    int[] fields = Grego.timeToFields(time, null);
    StringBuilder sb = new StringBuilder(15);
    sb.append(numToString(fields[0], 4));
    sb.append(numToString(fields[1] + 1, 2));
    sb.append(numToString(fields[2], 2));
    sb.append('T');
    
    int t = fields[5];
    int hour = t / 3600000;
    t %= 3600000;
    int min = t / 60000;
    t %= 60000;
    int sec = t / 1000;
    
    sb.append(numToString(hour, 2));
    sb.append(numToString(min, 2));
    sb.append(numToString(sec, 2));
    return sb.toString();
  }
  


  private static String getUTCDateTimeString(long time)
  {
    return getDateTimeString(time) + "Z";
  }
  



  private static long parseDateTimeString(String str, int offset)
  {
    int year = 0;int month = 0;int day = 0;int hour = 0;int min = 0;int sec = 0;
    boolean isUTC = false;
    boolean isValid = false;
    
    if (str != null)
    {


      int length = str.length();
      if ((length == 15) || (length == 16))
      {



        if (str.charAt(8) == 'T')
        {


          if (length == 16) {
            if (str.charAt(15) == 'Z')
            {


              isUTC = true;
            }
          } else {
            try {
              year = Integer.parseInt(str.substring(0, 4));
              month = Integer.parseInt(str.substring(4, 6)) - 1;
              day = Integer.parseInt(str.substring(6, 8));
              hour = Integer.parseInt(str.substring(9, 11));
              min = Integer.parseInt(str.substring(11, 13));
              sec = Integer.parseInt(str.substring(13, 15));
            }
            catch (NumberFormatException nfe)
            {
              break label249;
            }
            int maxDayOfMonth = Grego.monthLength(year, month);
            if ((year >= 0) && (month >= 0) && (month <= 11) && (day >= 1) && (day <= maxDayOfMonth) && (hour >= 0) && (hour < 24) && (min >= 0) && (min < 60) && (sec >= 0) && (sec < 60))
            {



              isValid = true; }
          } } } }
    label249:
    if (!isValid) {
      throw new IllegalArgumentException("Invalid date time string format");
    }
    
    long time = Grego.fieldsToDay(year, month, day) * 86400000L;
    time += hour * 3600000 + min * 60000 + sec * 1000;
    if (!isUTC) {
      time -= offset;
    }
    return time;
  }
  


  private static int offsetStrToMillis(String str)
  {
    boolean isValid = false;
    int sign = 0;int hour = 0;int min = 0;int sec = 0;
    

    if (str != null)
    {

      int length = str.length();
      if ((length == 5) || (length == 7))
      {



        char s = str.charAt(0);
        if (s == '+') {
          sign = 1;
        } else { if (s != '-') break label119;
          sign = -1;
        }
        


        try
        {
          hour = Integer.parseInt(str.substring(1, 3));
          min = Integer.parseInt(str.substring(3, 5));
          if (length == 7) {
            sec = Integer.parseInt(str.substring(5, 7));
          }
        } catch (NumberFormatException nfe) {
          break label119;
        }
        isValid = true;
      } }
    label119:
    if (!isValid) {
      throw new IllegalArgumentException("Bad offset string");
    }
    int millis = sign * ((hour * 60 + min) * 60 + sec) * 1000;
    return millis;
  }
  


  private static String millisToOffset(int millis)
  {
    StringBuilder sb = new StringBuilder(7);
    if (millis >= 0) {
      sb.append('+');
    } else {
      sb.append('-');
      millis = -millis;
    }
    
    int t = millis / 1000;
    
    int sec = t % 60;
    t = (t - sec) / 60;
    int min = t % 60;
    int hour = t / 60;
    
    sb.append(numToString(hour, 2));
    sb.append(numToString(min, 2));
    sb.append(numToString(sec, 2));
    
    return sb.toString();
  }
  


  private static String numToString(int num, int width)
  {
    String str = Integer.toString(num);
    int len = str.length();
    if (len >= width) {
      return str.substring(len - width, len);
    }
    StringBuilder sb = new StringBuilder(width);
    for (int i = len; i < width; i++) {
      sb.append('0');
    }
    sb.append(str);
    return sb.toString();
  }
  

  private transient boolean isFrozen = false;
  



  public boolean isFrozen()
  {
    return isFrozen;
  }
  



  public TimeZone freeze()
  {
    isFrozen = true;
    return this;
  }
  



  public TimeZone cloneAsThawed()
  {
    VTimeZone vtz = (VTimeZone)super.cloneAsThawed();
    tz = ((BasicTimeZone)tz.cloneAsThawed());
    isFrozen = false;
    return vtz;
  }
  
  private VTimeZone() {}
}
