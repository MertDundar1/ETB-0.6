package com.mojang.realmsclient.gui.screens;

import com.google.common.cache.LoadingCache;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ServerActivity;
import com.mojang.realmsclient.dto.ServerActivityList;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsBufferBuilder;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.lwjgl.opengl.GL11;

public class RealmsActivityScreen extends RealmsScreen
{
  private static final org.apache.logging.log4j.Logger LOGGER = ;
  private final RealmsScreen lastScreen;
  private final RealmsServer serverData;
  private volatile List<ActivityRow> activityMap = new ArrayList();
  private DetailsList list;
  private int matrixWidth;
  private String toolTip;
  private volatile List<Day> dayList = new ArrayList();
  private List<Color> colors = java.util.Arrays.asList(new Color[] { new Color(79, 243, 29), new Color(243, 175, 29), new Color(243, 29, 190), new Color(29, 165, 243), new Color(29, 243, 130), new Color(243, 29, 64), new Color(29, 74, 243) });
  private int colorIndex = 0;
  private long periodInMillis;
  private int maxKeyWidth = 0;
  private Boolean noActivity = Boolean.valueOf(false);
  private int activityPoint = 0;
  private int dayWidth = 0;
  private double hourWidth = 0.0D;
  private double minuteWidth = 0.0D;
  
  private int BUTTON_BACK_ID = 0;
  
  private static LoadingCache<String, String> activitiesNameCache = com.google.common.cache.CacheBuilder.newBuilder().build(new com.google.common.cache.CacheLoader()
  {
    public String load(String uuid) throws Exception {
      String name = Realms.uuidToName(uuid);
      
      if (name == null) {
        throw new Exception("Couldn't get username");
      }
      return name;
    }
  });
  










  public RealmsActivityScreen(RealmsScreen lastScreen, RealmsServer serverData)
  {
    this.lastScreen = lastScreen;
    this.serverData = serverData;
    getActivities();
  }
  
  public void mouseEvent()
  {
    super.mouseEvent();
    list.mouseEvent();
  }
  
  public void init()
  {
    org.lwjgl.input.Keyboard.enableRepeatEvents(true);
    buttonsClear();
    
    matrixWidth = width();
    list = new DetailsList();
    
    buttonsAdd(newButton(BUTTON_BACK_ID, width() / 2 - 100, height() - 30, 200, 20, getLocalizedString("gui.back")));
  }
  
  private Color getColor() {
    if (colorIndex > colors.size() - 1) {
      colorIndex = 0;
    }
    
    return (Color)colors.get(colorIndex++);
  }
  
  private void getActivities()
  {
    new Thread()
    {
      public void run() {
        RealmsClient client = RealmsClient.createRealmsClient();
        try {
          ServerActivityList activities = client.getActivity(serverData.id);
          activityMap = RealmsActivityScreen.this.convertToActivityMatrix(activities);
          
          List<RealmsActivityScreen.Day> tempDayList = new ArrayList();
          
          for (RealmsActivityScreen.ActivityRow row : activityMap) {
            for (RealmsActivityScreen.Activity activity : activities) {
              String day = new SimpleDateFormat("dd/MM").format(new Date(start));
              RealmsActivityScreen.Day the_day = new RealmsActivityScreen.Day(day, Long.valueOf(start));
              
              if (!tempDayList.contains(the_day)) {
                tempDayList.add(the_day);
              }
            }
          }
          
          Collections.sort(tempDayList);
          
          for (RealmsActivityScreen.ActivityRow row : activityMap) {
            for (RealmsActivityScreen.Activity activity : activities) {
              String day = new SimpleDateFormat("dd/MM").format(new Date(start));
              RealmsActivityScreen.Day the_day = new RealmsActivityScreen.Day(day, Long.valueOf(start));
              
              dayIndex = (tempDayList.indexOf(the_day) + 1);
            }
          }
          
          dayList = tempDayList;
        } catch (RealmsServiceException e) {
          e.printStackTrace();
        }
      }
    }.start();
  }
  
  private List<ActivityRow> convertToActivityMatrix(ServerActivityList serverActivityList) {
    List<ActivityRow> activityRows = com.google.common.collect.Lists.newArrayList();
    periodInMillis = periodInMillis;
    long base = System.currentTimeMillis() - periodInMillis;
    
    for (ServerActivity sa : serverActivities) {
      ActivityRow activityRow = find(profileUuid, activityRows);
      Calendar joinTime = Calendar.getInstance(java.util.TimeZone.getDefault());
      joinTime.setTimeInMillis(joinTime);
      Calendar leaveTime = Calendar.getInstance(java.util.TimeZone.getDefault());
      leaveTime.setTimeInMillis(leaveTime);
      
      Activity e = new Activity(base, joinTime.getTimeInMillis(), leaveTime.getTimeInMillis(), null);
      
      if (activityRow == null) {
        String name = "";
        try
        {
          name = (String)activitiesNameCache.get(profileUuid);
        }
        catch (Exception exception) {
          LOGGER.error("Could not get name for " + profileUuid, exception); }
        continue;
        

        activityRow = new ActivityRow(profileUuid, new ArrayList(), name, profileUuid);
        activities.add(e);
        activityRows.add(activityRow);
      } else {
        activities.add(e);
      }
    }
    
    Collections.sort(activityRows);
    
    for (ActivityRow row : activityRows) {
      color = getColor();
      Collections.sort(activities);
    }
    
    noActivity = Boolean.valueOf(activityRows.size() == 0);
    
    return activityRows;
  }
  
  private ActivityRow find(String key, List<ActivityRow> rows) {
    for (ActivityRow row : rows) {
      if (key.equals(key)) {
        return row;
      }
    }
    
    return null;
  }
  
  public void tick()
  {
    super.tick();
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (button.id() == BUTTON_BACK_ID) {
      Realms.setScreen(lastScreen);
    }
  }
  
  public void keyPressed(char ch, int eventKey)
  {
    if (eventKey == 1) {
      Realms.setScreen(lastScreen);
    }
  }
  
  public void render(int xm, int ym, float a)
  {
    toolTip = null;
    
    renderBackground();
    
    for (ActivityRow row : activityMap) {
      int keyWidth = fontWidth(name);
      
      if (keyWidth > maxKeyWidth) {
        maxKeyWidth = (keyWidth + 10);
      }
    }
    
    int keyRightPadding = 25;
    activityPoint = (maxKeyWidth + keyRightPadding);
    
    int spaceLeft = matrixWidth - activityPoint - 10;
    int days = dayList.size() < 1 ? 1 : dayList.size();
    
    dayWidth = (spaceLeft / days);
    hourWidth = (dayWidth / 24.0D);
    minuteWidth = (hourWidth / 60.0D);
    
    list.render(xm, ym, a);
    Tezzelator t;
    if ((activityMap != null) && (activityMap.size() > 0)) {
      t = Tezzelator.instance;
      
      GL11.glDisable(3553);
      t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
      t.vertex(activityPoint, height() - 40, 0.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(activityPoint + 1, height() - 40, 0.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(activityPoint + 1, 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      t.vertex(activityPoint, 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      t.end();
      GL11.glEnable(3553);
      
      for (Day day : dayList) {
        int daysIndex = dayList.indexOf(day) + 1;
        
        drawString(day, activityPoint + (daysIndex - 1) * dayWidth + (dayWidth - fontWidth(day)) / 2 + 2, height() - 52, 16777215);
        
        GL11.glDisable(3553);
        t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        t.vertex(activityPoint + daysIndex * dayWidth, height() - 40, 0.0D).color(128, 128, 128, 255).endVertex();
        t.vertex(activityPoint + daysIndex * dayWidth + 1, height() - 40, 0.0D).color(128, 128, 128, 255).endVertex();
        t.vertex(activityPoint + daysIndex * dayWidth + 1, 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
        t.vertex(activityPoint + daysIndex * dayWidth, 30.0D, 0.0D).color(128, 128, 128, 255).endVertex();
        t.end();
        GL11.glEnable(3553);
      }
    }
    
    super.render(xm, ym, a);
    

    drawCenteredString(getLocalizedString("mco.activity.title"), width() / 2, 10, 16777215);
    
    if (toolTip != null) {
      renderMousehoverTooltip(toolTip, xm, ym);
    }
    
    if (noActivity.booleanValue()) {
      drawCenteredString(getLocalizedString("mco.activity.noactivity", new Object[] { Long.valueOf(TimeUnit.DAYS.convert(periodInMillis, TimeUnit.MILLISECONDS)) }), width() / 2, height() / 2 - 20, 16777215);
    }
  }
  
  static class Color
  {
    int r;
    int g;
    int b;
    
    Color(int r, int g, int b) {
      this.r = r;
      this.g = g;
      this.b = b;
    }
  }
  
  static class Day implements Comparable<Day>
  {
    String day;
    Long timestamp;
    
    public int compareTo(Day o) {
      return timestamp.compareTo(timestamp);
    }
    
    Day(String day, Long timestamp) {
      this.day = day;
      this.timestamp = timestamp;
    }
    
    public boolean equals(Object d)
    {
      if (!(d instanceof Day)) {
        return false;
      }
      
      Day that = (Day)d;
      
      return day.equals(day);
    }
  }
  
  static class ActivityRow implements Comparable<ActivityRow>
  {
    String key;
    List<RealmsActivityScreen.Activity> activities;
    RealmsActivityScreen.Color color;
    String name;
    String uuid;
    
    public int compareTo(ActivityRow o) {
      return name.compareTo(name);
    }
    
    ActivityRow(String key, List<RealmsActivityScreen.Activity> activities, String name, String uuid) {
      this.key = key;
      this.activities = activities;
      this.name = name;
      this.uuid = uuid;
    }
  }
  
  static class Activity implements Comparable<Activity> {
    long base;
    long start;
    long end;
    int dayIndex;
    
    private Activity(long base, long start, long end) {
      this.base = base;
      this.start = start;
      this.end = end;
    }
    
    public int compareTo(Activity o)
    {
      return (int)(start - start);
    }
    
    public int hourIndice() {
      String hour = new SimpleDateFormat("HH").format(new Date(start));
      return Integer.parseInt(hour);
    }
    
    public int minuteIndice() {
      String minute = new SimpleDateFormat("mm").format(new Date(start));
      return Integer.parseInt(minute);
    }
  }
  
  static class ActivityRender {
    double start;
    double width;
    String tooltip;
    
    private ActivityRender(double start, double width, String tooltip) {
      this.start = start;
      this.width = width;
      this.tooltip = tooltip;
    }
  }
  
  class DetailsList extends net.minecraft.realms.RealmsScrolledSelectionList {
    public DetailsList() {
      super(height(), 30, height() - 40, fontLineHeight() + 1);
    }
    
    public int getItemCount()
    {
      return activityMap.size();
    }
    

    public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse) {}
    

    public boolean isSelectedItem(int item)
    {
      return false;
    }
    


    public int getMaxPosition() { return getItemCount() * (fontLineHeight() + 1) + 15; }
    
    protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) { int r;
      int g;
      int b;
      if ((activityMap != null) && (activityMap.size() > i)) {
        RealmsActivityScreen.ActivityRow row = (RealmsActivityScreen.ActivityRow)activityMap.get(i);
        drawString(name, 20, y, activityMap.get(i)).uuid.equals(Realms.getUUID()) ? 8388479 : 16777215);
        
        r = color.r;
        g = color.g;
        b = color.b;
        
        GL11.glDisable(3553);
        t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        t.vertex(activityPoint - 8, y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
        t.vertex(activityPoint - 3, y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
        t.vertex(activityPoint - 3, y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
        t.vertex(activityPoint - 8, y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
        t.end();
        GL11.glEnable(3553);
        
        RealmsScreen.bindFace(activityMap.get(i)).uuid, activityMap.get(i)).name);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RealmsScreen.blit(10, y, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
        RealmsScreen.blit(10, y, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
        
        List<RealmsActivityScreen.ActivityRender> toRender = new ArrayList();
        
        for (RealmsActivityScreen.Activity activity : activities) {
          int minute = activity.minuteIndice();
          int hour = activity.hourIndice();
          double itemWidth = minuteWidth * TimeUnit.MINUTES.convert(end - start, TimeUnit.MILLISECONDS);
          
          if (itemWidth < 3.0D) {
            itemWidth = 3.0D;
          }
          
          double pos = activityPoint + (dayWidth * dayIndex - dayWidth) + hour * hourWidth + minute * minuteWidth;
          
          SimpleDateFormat format = new SimpleDateFormat("HH:mm");
          Date startDate = new Date(start);
          Date endDate = new Date(end);
          int length = (int)Math.ceil(TimeUnit.SECONDS.convert(end - start, TimeUnit.MILLISECONDS) / 60.0D);
          
          if (length < 1) {
            length = 1;
          }
          
          String tooltip = "[" + format.format(startDate) + " - " + format.format(endDate) + "] " + length + (length > 1 ? " minutes" : " minute");
          
          boolean exists = false;
          
          for (RealmsActivityScreen.ActivityRender render : toRender) {
            if (start + width >= pos - 0.5D) {
              double overlap = start + width - pos;
              double padding = Math.max(0.0D, pos - (start + width));
              
              width = (width - Math.max(0.0D, overlap) + itemWidth + padding); RealmsActivityScreen.ActivityRender 
                tmp821_819 = render;821819tooltip = (821819tooltip + "\n" + tooltip);
              
              exists = true;
              break;
            }
          }
          
          if (!exists) {
            toRender.add(new RealmsActivityScreen.ActivityRender(pos, itemWidth, tooltip, null));
          }
        }
        
        for (RealmsActivityScreen.ActivityRender render : toRender) {
          GL11.glDisable(3553);
          
          t.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
          t.vertex(start, y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
          t.vertex(start + width, y + 6.5D, 0.0D).color(r, g, b, 255).endVertex();
          t.vertex(start + width, y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
          t.vertex(start, y + 1.5D, 0.0D).color(r, g, b, 255).endVertex();
          t.end();
          
          GL11.glEnable(3553);
          
          if ((xm() >= start) && (xm() <= start + width) && (ym() >= y + 1.5D) && (ym() <= y + 6.5D)) {
            toolTip = tooltip.trim();
          }
        }
      }
    }
    
    public int getScrollbarPosition()
    {
      return width() - 7;
    }
  }
  
  protected void renderMousehoverTooltip(String msg, int x, int y) {
    if (msg == null) {
      return;
    }
    
    int index = 0;
    int width = 0;
    
    for (String s : msg.split("\n")) {
      int the_width = fontWidth(s);
      
      if (the_width > width) {
        width = the_width;
      }
    }
    
    int rx = x - width - 5;
    int ry = y;
    
    if (rx < 0) {
      rx = x + 12;
    }
    
    for (String s : msg.split("\n")) {
      fillGradient(rx - 3, ry - (index == 0 ? 3 : 0) + index, rx + width + 3, ry + 8 + 3 + index, -1073741824, -1073741824);
      fontDrawShadow(s, rx, ry + index, -1);
      index += 10;
    }
  }
}
