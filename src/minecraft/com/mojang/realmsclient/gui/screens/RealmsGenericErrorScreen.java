package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;



public class RealmsGenericErrorScreen
  extends RealmsScreen
{
  private final RealmsScreen nextScreen;
  private static final int OK_BUTTON_ID = 10;
  private String line1;
  private String line2;
  
  public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, RealmsScreen nextScreen)
  {
    this.nextScreen = nextScreen;
    errorMessage(realmsServiceException);
  }
  
  public RealmsGenericErrorScreen(String message, RealmsScreen nextScreen)
  {
    this.nextScreen = nextScreen;
    errorMessage(message);
  }
  
  public RealmsGenericErrorScreen(String title, String message, RealmsScreen nextScreen)
  {
    this.nextScreen = nextScreen;
    errorMessage(title, message);
  }
  
  private void errorMessage(RealmsServiceException realmsServiceException) {
    if (errorCode != -1) {
      line1 = ("Realms (" + errorCode + "):");
      String translationKey = "mco.errorMessage." + errorCode;
      String translated = getLocalizedString(translationKey);
      line2 = (translated.equals(translationKey) ? errorMsg : translated);
    } else {
      line1 = ("An error occurred (" + httpResultCode + "):");
      line2 = httpResponseContent;
    }
  }
  
  private void errorMessage(String message) {
    line1 = "An error occurred: ";
    line2 = message;
  }
  
  private void errorMessage(String title, String message) {
    line1 = title;
    line2 = message;
  }
  
  public void init()
  {
    buttonsClear();
    
    buttonsAdd(newButton(10, width() / 2 - 100, height() - 52, 200, 20, "Ok"));
  }
  
  public void tick()
  {
    super.tick();
  }
  
  public void buttonClicked(RealmsButton button)
  {
    if (button.id() == 10) {
      Realms.setScreen(nextScreen);
    }
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    







    drawCenteredString(line1, width() / 2, 80, 16777215);
    drawCenteredString(line2, width() / 2, 100, 16711680);
    
    super.render(xm, ym, a);
  }
}
