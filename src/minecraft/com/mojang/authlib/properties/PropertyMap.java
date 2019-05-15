package com.mojang.authlib.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PropertyMap extends com.google.common.collect.ForwardingMultimap<String, Property>
{
  private final com.google.common.collect.Multimap<String, Property> properties;
  
  public PropertyMap()
  {
    properties = com.google.common.collect.LinkedHashMultimap.create();
  }
  

  protected com.google.common.collect.Multimap<String, Property> delegate() { return properties; }
  
  public static class Serializer implements com.google.gson.JsonSerializer<PropertyMap>, com.google.gson.JsonDeserializer<PropertyMap> {
    public Serializer() {}
    
    public PropertyMap deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
      PropertyMap result = new PropertyMap();
      java.util.Iterator i$;
      java.util.Map.Entry<String, com.google.gson.JsonElement> entry; if ((json instanceof JsonObject)) {
        JsonObject object = (JsonObject)json;
        
        for (i$ = object.entrySet().iterator(); i$.hasNext();) { entry = (java.util.Map.Entry)i$.next();
          if ((entry.getValue() instanceof JsonArray)) {
            for (com.google.gson.JsonElement element : (JsonArray)entry.getValue()) {
              result.put(entry.getKey(), new Property((String)entry.getKey(), element.getAsString()));
            }
          }
        }
      } else if ((json instanceof JsonArray)) {
        for (com.google.gson.JsonElement element : (JsonArray)json) {
          if ((element instanceof JsonObject)) {
            JsonObject object = (JsonObject)element;
            String name = object.getAsJsonPrimitive("name").getAsString();
            String value = object.getAsJsonPrimitive("value").getAsString();
            
            if (object.has("signature")) {
              result.put(name, new Property(name, value, object.getAsJsonPrimitive("signature").getAsString()));
            } else {
              result.put(name, new Property(name, value));
            }
          }
        }
      }
      
      return result;
    }
    
    public com.google.gson.JsonElement serialize(PropertyMap src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context)
    {
      JsonArray result = new JsonArray();
      
      for (Property property : src.values()) {
        JsonObject object = new JsonObject();
        
        object.addProperty("name", property.getName());
        object.addProperty("value", property.getValue());
        
        if (property.hasSignature()) {
          object.addProperty("signature", property.getSignature());
        }
        
        result.add(object);
      }
      
      return result;
    }
  }
}
