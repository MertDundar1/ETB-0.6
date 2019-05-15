package com.mojang.authlib;

import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GameProfile
{
  private final UUID id;
  private final String name;
  private final PropertyMap properties = new PropertyMap();
  



  private boolean legacy;
  




  public GameProfile(UUID id, String name)
  {
    if ((id == null) && (StringUtils.isBlank(name))) { throw new IllegalArgumentException("Name and ID cannot both be blank");
    }
    this.id = id;
    this.name = name;
  }
  






  public UUID getId()
  {
    return id;
  }
  






  public String getName()
  {
    return name;
  }
  




  public PropertyMap getProperties()
  {
    return properties;
  }
  






  public boolean isComplete()
  {
    return (id != null) && (StringUtils.isNotBlank(getName()));
  }
  
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if ((o == null) || (getClass() != o.getClass())) { return false;
    }
    GameProfile that = (GameProfile)o;
    
    if (id != null ? !id.equals(id) : id != null) return false;
    if (name != null ? !name.equals(name) : name != null) { return false;
    }
    return true;
  }
  
  public int hashCode()
  {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
  
  public String toString()
  {
    return new ToStringBuilder(this).append("id", id).append("name", name).append("properties", properties).append("legacy", legacy).toString();
  }
  




  public boolean isLegacy()
  {
    return legacy;
  }
}
