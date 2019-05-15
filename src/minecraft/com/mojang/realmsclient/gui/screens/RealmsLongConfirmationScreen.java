package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;


public class RealmsLongConfirmationScreen
  extends RealmsScreen
{
  private final Type type;
  private final String line2;
  private final String line3;
  protected final RealmsScreen parent;
  protected final String yesButton;
  protected final String noButton;
  private final String okButton;
  protected final int id;
  private final boolean yesNoQuestion;
  
  public RealmsLongConfirmationScreen(RealmsScreen parent, Type type, String line2, String line3, boolean yesNoQuestion, int id)
  {
    this.parent = parent;
    this.id = id;
    this.type = type;
    this.line2 = line2;
    this.line3 = line3;
    this.yesNoQuestion = yesNoQuestion;
    yesButton = getLocalizedString("gui.yes");
    noButton = getLocalizedString("gui.no");
    okButton = getLocalizedString("mco.gui.ok");
  }
  
  public void init()
  {
    if (yesNoQuestion) {
      buttonsAdd(newButton(0, width() / 2 - 105, RealmsConstants.row(8), 100, 20, yesButton));
      buttonsAdd(newButton(1, width() / 2 + 5, RealmsConstants.row(8), 100, 20, noButton));
    } else {
      buttonsAdd(newButton(0, width() / 2 - 50, RealmsConstants.row(8), 100, 20, okButton));
    }
  }
  
  public void buttonClicked(RealmsButton button)
  {
    parent.confirmResult(button.id() == 0, id);
  }
  
  public void keyPressed(char eventCharacter, int eventKey)
  {
    if (eventKey == 1) {
      parent.confirmResult(false, id);
    }
  }
  
  public void render(int xm, int ym, float a)
  {
    renderBackground();
    
    drawCenteredString(type.text, width() / 2, RealmsConstants.row(2), type.colorCode);
    drawCenteredString(line2, width() / 2, RealmsConstants.row(4), 16777215);
    
    drawCenteredString(line3, width() / 2, RealmsConstants.row(6), 16777215);
    
    super.render(xm, ym, a);
  }
  
  public static enum Type {
    Warning("Warning!", 16711680), 
    Info("Info!", 8226750);
    
    private Type(String text, int colorCode) {
      this.text = text;
      this.colorCode = colorCode;
    }
    
    public final int colorCode;
    public final String text;
  }
}
