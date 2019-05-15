package com.mojang.authlib.yggdrasil.response;

import com.google.gson.JsonObject;

public class ProfileSearchResultsResponse
  extends Response
{
  private com.mojang.authlib.GameProfile[] profiles;
  
  public ProfileSearchResultsResponse() {}
  
  public com.mojang.authlib.GameProfile[] getProfiles() { return profiles; }
  
  public static class Serializer implements com.google.gson.JsonDeserializer<ProfileSearchResultsResponse> {
    public Serializer() {}
    
    public ProfileSearchResultsResponse deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
      ProfileSearchResultsResponse result = new ProfileSearchResultsResponse();
      
      if ((json instanceof JsonObject)) {
        JsonObject object = (JsonObject)json;
        if (object.has("error")) {
          result.setError(object.getAsJsonPrimitive("error").getAsString());
        }
        if (object.has("errorMessage")) {
          result.setError(object.getAsJsonPrimitive("errorMessage").getAsString());
        }
        if (object.has("cause")) {
          result.setError(object.getAsJsonPrimitive("cause").getAsString());
        }
      } else {
        profiles = ((com.mojang.authlib.GameProfile[])context.deserialize(json, [Lcom.mojang.authlib.GameProfile.class));
      }
      
      return result;
    }
  }
}
