package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class AuthenticationRequest
{
  private com.mojang.authlib.Agent agent;
  private String username;
  private String password;
  private String clientToken;
  private boolean requestUser = true;
  
  public AuthenticationRequest(YggdrasilUserAuthentication authenticationService, String username, String password) {
    agent = authenticationService.getAgent();
    this.username = username;
    clientToken = authenticationService.getAuthenticationService().getClientToken();
    this.password = password;
  }
}
