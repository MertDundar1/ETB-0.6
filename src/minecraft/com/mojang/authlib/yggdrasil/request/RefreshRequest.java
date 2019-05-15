package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class RefreshRequest
{
  private String clientToken;
  private String accessToken;
  private com.mojang.authlib.GameProfile selectedProfile;
  private boolean requestUser = true;
  
  public RefreshRequest(YggdrasilUserAuthentication authenticationService) {
    this(authenticationService, null);
  }
  
  public RefreshRequest(YggdrasilUserAuthentication authenticationService, com.mojang.authlib.GameProfile profile) {
    clientToken = authenticationService.getAuthenticationService().getClientToken();
    accessToken = authenticationService.getAuthenticatedToken();
    selectedProfile = profile;
  }
}
