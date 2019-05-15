package com.ibm.icu.impl.data;

import com.ibm.icu.util.HebrewHoliday;
import com.ibm.icu.util.Holiday;
import java.util.ListResourceBundle;







public class HolidayBundle_iw_IL
  extends ListResourceBundle
{
  private static final Holiday[] fHolidays = { HebrewHoliday.ROSH_HASHANAH, HebrewHoliday.YOM_KIPPUR, HebrewHoliday.HANUKKAH, HebrewHoliday.PURIM, HebrewHoliday.PASSOVER, HebrewHoliday.SHAVUOT, HebrewHoliday.SELIHOT };
  



  public HolidayBundle_iw_IL() {}
  



  private static final Object[][] fContents = { { "holidays", fHolidays } };
  
  public synchronized Object[][] getContents() {
    return fContents; }
}
