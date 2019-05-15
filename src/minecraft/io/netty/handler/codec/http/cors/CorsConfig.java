package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.StringUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;



















public final class CorsConfig
{
  private final Set<String> origins;
  private final boolean anyOrigin;
  private final boolean enabled;
  private final Set<String> exposeHeaders;
  private final boolean allowCredentials;
  private final long maxAge;
  private final Set<HttpMethod> allowedRequestMethods;
  private final Set<String> allowedRequestHeaders;
  private final boolean allowNullOrigin;
  private final Map<CharSequence, Callable<?>> preflightHeaders;
  private final boolean shortCurcuit;
  
  private CorsConfig(Builder builder)
  {
    origins = new LinkedHashSet(origins);
    anyOrigin = anyOrigin;
    enabled = enabled;
    exposeHeaders = exposeHeaders;
    allowCredentials = allowCredentials;
    maxAge = maxAge;
    allowedRequestMethods = requestMethods;
    allowedRequestHeaders = requestHeaders;
    allowNullOrigin = allowNullOrigin;
    preflightHeaders = preflightHeaders;
    shortCurcuit = shortCurcuit;
  }
  




  public boolean isCorsSupportEnabled()
  {
    return enabled;
  }
  




  public boolean isAnyOriginSupported()
  {
    return anyOrigin;
  }
  




  public String origin()
  {
    return origins.isEmpty() ? "*" : (String)origins.iterator().next();
  }
  




  public Set<String> origins()
  {
    return origins;
  }
  








  public boolean isNullOriginAllowed()
  {
    return allowNullOrigin;
  }
  





















  public Set<String> exposedHeaders()
  {
    return Collections.unmodifiableSet(exposeHeaders);
  }
  
















  public boolean isCredentialsAllowed()
  {
    return allowCredentials;
  }
  









  public long maxAge()
  {
    return maxAge;
  }
  





  public Set<HttpMethod> allowedRequestMethods()
  {
    return Collections.unmodifiableSet(allowedRequestMethods);
  }
  







  public Set<String> allowedRequestHeaders()
  {
    return Collections.unmodifiableSet(allowedRequestHeaders);
  }
  




  public HttpHeaders preflightResponseHeaders()
  {
    if (this.preflightHeaders.isEmpty()) {
      return HttpHeaders.EMPTY_HEADERS;
    }
    HttpHeaders preflightHeaders = new DefaultHttpHeaders();
    for (Map.Entry<CharSequence, Callable<?>> entry : this.preflightHeaders.entrySet()) {
      Object value = getValue((Callable)entry.getValue());
      if ((value instanceof Iterable)) {
        preflightHeaders.add((CharSequence)entry.getKey(), (Iterable)value);
      } else {
        preflightHeaders.add((CharSequence)entry.getKey(), value);
      }
    }
    return preflightHeaders;
  }
  









  public boolean isShortCurcuit()
  {
    return shortCurcuit;
  }
  
  private static <T> T getValue(Callable<T> callable) {
    try {
      return callable.call();
    } catch (Exception e) {
      throw new IllegalStateException("Could not generate value for callable [" + callable + ']', e);
    }
  }
  
  public String toString()
  {
    return StringUtil.simpleClassName(this) + "[enabled=" + enabled + ", origins=" + origins + ", anyOrigin=" + anyOrigin + ", exposedHeaders=" + exposeHeaders + ", isCredentialsAllowed=" + allowCredentials + ", maxAge=" + maxAge + ", allowedRequestMethods=" + allowedRequestMethods + ", allowedRequestHeaders=" + allowedRequestHeaders + ", preflightHeaders=" + preflightHeaders + ']';
  }
  












  public static Builder withAnyOrigin()
  {
    return new Builder();
  }
  




  public static Builder withOrigin(String origin)
  {
    if ("*".equals(origin)) {
      return new Builder();
    }
    return new Builder(new String[] { origin });
  }
  




  public static Builder withOrigins(String... origins)
  {
    return new Builder(origins);
  }
  

  public static class Builder
  {
    private final Set<String> origins;
    
    private final boolean anyOrigin;
    
    private boolean allowNullOrigin;
    private boolean enabled = true;
    private boolean allowCredentials;
    private final Set<String> exposeHeaders = new HashSet();
    private long maxAge;
    private final Set<HttpMethod> requestMethods = new HashSet();
    private final Set<String> requestHeaders = new HashSet();
    private final Map<CharSequence, Callable<?>> preflightHeaders = new HashMap();
    

    private boolean noPreflightHeaders;
    
    private boolean shortCurcuit;
    

    public Builder(String... origins)
    {
      this.origins = new LinkedHashSet(Arrays.asList(origins));
      anyOrigin = false;
    }
    




    public Builder()
    {
      anyOrigin = true;
      origins = Collections.emptySet();
    }
    






    public Builder allowNullOrigin()
    {
      allowNullOrigin = true;
      return this;
    }
    




    public Builder disable()
    {
      enabled = false;
      return this;
    }
    
























    public Builder exposeHeaders(String... headers)
    {
      exposeHeaders.addAll(Arrays.asList(headers));
      return this;
    }
    














    public Builder allowCredentials()
    {
      allowCredentials = true;
      return this;
    }
    








    public Builder maxAge(long max)
    {
      maxAge = max;
      return this;
    }
    






    public Builder allowedRequestMethods(HttpMethod... methods)
    {
      requestMethods.addAll(Arrays.asList(methods));
      return this;
    }
    















    public Builder allowedRequestHeaders(String... headers)
    {
      requestHeaders.addAll(Arrays.asList(headers));
      return this;
    }
    









    public Builder preflightResponseHeader(CharSequence name, Object... values)
    {
      if (values.length == 1) {
        preflightHeaders.put(name, new CorsConfig.ConstantValueGenerator(values[0], null));
      } else {
        preflightResponseHeader(name, Arrays.asList(values));
      }
      return this;
    }
    










    public <T> Builder preflightResponseHeader(CharSequence name, Iterable<T> value)
    {
      preflightHeaders.put(name, new CorsConfig.ConstantValueGenerator(value, null));
      return this;
    }
    














    public <T> Builder preflightResponseHeader(String name, Callable<T> valueGenerator)
    {
      preflightHeaders.put(name, valueGenerator);
      return this;
    }
    




    public Builder noPreflightResponseHeaders()
    {
      noPreflightHeaders = true;
      return this;
    }
    




    public CorsConfig build()
    {
      if ((preflightHeaders.isEmpty()) && (!noPreflightHeaders)) {
        preflightHeaders.put("Date", new CorsConfig.DateValueGenerator());
        preflightHeaders.put("Content-Length", new CorsConfig.ConstantValueGenerator("0", null));
      }
      return new CorsConfig(this, null);
    }
    









    public Builder shortCurcuit()
    {
      shortCurcuit = true;
      return this;
    }
  }
  




  private static final class ConstantValueGenerator
    implements Callable<Object>
  {
    private final Object value;
    




    private ConstantValueGenerator(Object value)
    {
      if (value == null) {
        throw new IllegalArgumentException("value must not be null");
      }
      this.value = value;
    }
    
    public Object call()
    {
      return value;
    }
  }
  

  public static final class DateValueGenerator
    implements Callable<Date>
  {
    public DateValueGenerator() {}
    
    public Date call()
      throws Exception
    {
      return new Date();
    }
  }
}
