package com.mojang.authlib.yggdrasil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.response.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;

public class YggdrasilAuthenticationService extends HttpAuthenticationService
{
  private final String clientToken;
  private final Gson gson;
  
  public YggdrasilAuthenticationService(Proxy proxy, String clientToken)
  {
    super(proxy);
    this.clientToken = clientToken;
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(GameProfile.class, new GameProfileSerializer(null));
    builder.registerTypeAdapter(com.mojang.authlib.properties.PropertyMap.class, new com.mojang.authlib.properties.PropertyMap.Serializer());
    builder.registerTypeAdapter(UUID.class, new com.mojang.util.UUIDTypeAdapter());
    builder.registerTypeAdapter(com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse.class, new com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse.Serializer());
    gson = builder.create();
  }
  
  public com.mojang.authlib.UserAuthentication createUserAuthentication(com.mojang.authlib.Agent agent)
  {
    return new YggdrasilUserAuthentication(this, agent);
  }
  
  public com.mojang.authlib.minecraft.MinecraftSessionService createMinecraftSessionService()
  {
    return new YggdrasilMinecraftSessionService(this);
  }
  
  public com.mojang.authlib.GameProfileRepository createProfileRepository()
  {
    return new YggdrasilGameProfileRepository(this);
  }
  
  protected <T extends Response> T makeRequest(URL url, Object input, Class<T> classOfT) throws com.mojang.authlib.exceptions.AuthenticationException {
    try {
      String jsonResult = input == null ? performGetRequest(url) : performPostRequest(url, gson.toJson(input), "application/json");
      T result = (Response)gson.fromJson(jsonResult, classOfT);
      
      if (result == null) { return null;
      }
      if (org.apache.commons.lang3.StringUtils.isNotBlank(result.getError())) {
        if ("UserMigratedException".equals(result.getCause()))
          throw new com.mojang.authlib.exceptions.UserMigratedException(result.getErrorMessage());
        if (result.getError().equals("ForbiddenOperationException")) {
          throw new com.mojang.authlib.exceptions.InvalidCredentialsException(result.getErrorMessage());
        }
        throw new com.mojang.authlib.exceptions.AuthenticationException(result.getErrorMessage());
      }
      

      return result;
    } catch (IOException e) {
      throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
    } catch (IllegalStateException e) {
      throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
    } catch (JsonParseException e) {
      throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
    }
  }
  

  public String getClientToken() { return clientToken; }
  
  private static class GameProfileSerializer implements com.google.gson.JsonSerializer<GameProfile>, com.google.gson.JsonDeserializer<GameProfile> {
    private GameProfileSerializer() {}
    
    public GameProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = (JsonObject)json;
      UUID id = object.has("id") ? (UUID)context.deserialize(object.get("id"), UUID.class) : null;
      String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
      return new GameProfile(id, name);
    }
    
    public JsonElement serialize(GameProfile src, Type typeOfSrc, JsonSerializationContext context)
    {
      JsonObject result = new JsonObject();
      if (src.getId() != null) result.add("id", context.serialize(src.getId()));
      if (src.getName() != null) result.addProperty("name", src.getName());
      return result;
    }
  }
}
