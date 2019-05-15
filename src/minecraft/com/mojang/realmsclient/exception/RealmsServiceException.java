package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.client.RealmsError;

public class RealmsServiceException extends Exception
{
  public final int httpResultCode;
  public final String httpResponseContent;
  public final int errorCode;
  public final String errorMsg;
  
  public RealmsServiceException(int httpResultCode, String httpResponseText, RealmsError error)
  {
    super(httpResponseText);
    this.httpResultCode = httpResultCode;
    httpResponseContent = httpResponseText;
    errorCode = error.getErrorCode();
    errorMsg = error.getErrorMessage();
  }
  
  public RealmsServiceException(int httpResultCode, String httpResponseText, int errorCode, String errorMsg) {
    super(httpResponseText);
    this.httpResultCode = httpResultCode;
    httpResponseContent = httpResponseText;
    this.errorCode = errorCode;
    this.errorMsg = errorMsg;
  }
  
  public String toString()
  {
    if (errorCode != -1) {
      String translationKey = "mco.errorMessage." + errorCode;
      String translated = net.minecraft.realms.RealmsScreen.getLocalizedString(translationKey);
      
      return (translated.equals(translationKey) ? errorMsg : translated) + " - " + errorCode;
    }
    return "Realms (" + httpResultCode + ") " + httpResponseContent;
  }
}
