package com.sun.jna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;



























































public abstract interface Library
{
  public static final String OPTION_TYPE_MAPPER = "type-mapper";
  public static final String OPTION_FUNCTION_MAPPER = "function-mapper";
  public static final String OPTION_INVOCATION_MAPPER = "invocation-mapper";
  public static final String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
  public static final String OPTION_ALLOW_OBJECTS = "allow-objects";
  public static final String OPTION_CALLING_CONVENTION = "calling-convention";
  
  public static class Handler
    implements InvocationHandler
  {
    static final Method OBJECT_TOSTRING;
    static final Method OBJECT_HASHCODE;
    static final Method OBJECT_EQUALS;
    private final NativeLibrary nativeLibrary;
    private final Class interfaceClass;
    private final Map options;
    private FunctionMapper functionMapper;
    private final InvocationMapper invocationMapper;
    
    static
    {
      try
      {
        OBJECT_TOSTRING = (Library.1.class$java$lang$Object == null ? (Library.1.class$java$lang$Object = Library.1.class$("java.lang.Object")) : Library.1.class$java$lang$Object).getMethod("toString", new Class[0]);
        OBJECT_HASHCODE = (Library.1.class$java$lang$Object == null ? (Library.1.class$java$lang$Object = Library.1.class$("java.lang.Object")) : Library.1.class$java$lang$Object).getMethod("hashCode", new Class[0]);
        OBJECT_EQUALS = (Library.1.class$java$lang$Object == null ? (Library.1.class$java$lang$Object = Library.1.class$("java.lang.Object")) : Library.1.class$java$lang$Object).getMethod("equals", new Class[] { Library.1.class$java$lang$Object == null ? (Library.1.class$java$lang$Object = Library.1.class$("java.lang.Object")) : Library.1.class$java$lang$Object });
      }
      catch (Exception e) {
        throw new Error("Error retrieving Object.toString() method");
      }
    }
    
    private static class FunctionNameMap implements FunctionMapper {
      private final Map map;
      
      public FunctionNameMap(Map map) { this.map = new HashMap(map); }
      
      public String getFunctionName(NativeLibrary library, Method method) {
        String name = method.getName();
        if (map.containsKey(name)) {
          return (String)map.get(name);
        }
        return name;
      }
    }
    






    private final Map functions = new WeakHashMap();
    
    public Handler(String libname, Class interfaceClass, Map options) {
      if ((libname != null) && ("".equals(libname.trim()))) {
        throw new IllegalArgumentException("Invalid library name \"" + libname + "\"");
      }
      

      this.interfaceClass = interfaceClass;
      options = new HashMap(options);
      int callingConvention = (Library.1.class$com$sun$jna$AltCallingConvention == null ? (Library.1.class$com$sun$jna$AltCallingConvention = Library.1.class$("com.sun.jna.AltCallingConvention")) : Library.1.class$com$sun$jna$AltCallingConvention).isAssignableFrom(interfaceClass) ? 1 : 0;
      

      if (options.get("calling-convention") == null) {
        options.put("calling-convention", new Integer(callingConvention));
      }
      
      this.options = options;
      nativeLibrary = NativeLibrary.getInstance(libname, options);
      functionMapper = ((FunctionMapper)options.get("function-mapper"));
      if (functionMapper == null)
      {
        functionMapper = new FunctionNameMap(options);
      }
      invocationMapper = ((InvocationMapper)options.get("invocation-mapper"));
    }
    
    public NativeLibrary getNativeLibrary() {
      return nativeLibrary;
    }
    
    public String getLibraryName() {
      return nativeLibrary.getName();
    }
    

    public Class getInterfaceClass() { return interfaceClass; }
    
    private static class FunctionInfo { private FunctionInfo() {}
      FunctionInfo(Library.1 x0) { this(); }
      
      InvocationHandler handler;
      Function function;
      boolean isVarArgs;
      Map options;
    }
    
    public Object invoke(Object proxy, Method method, Object[] inArgs)
      throws Throwable
    {
      if (OBJECT_TOSTRING.equals(method)) {
        return "Proxy interface to " + nativeLibrary;
      }
      if (OBJECT_HASHCODE.equals(method)) {
        return new Integer(hashCode());
      }
      if (OBJECT_EQUALS.equals(method)) {
        Object o = inArgs[0];
        if ((o != null) && (Proxy.isProxyClass(o.getClass()))) {
          return Function.valueOf(Proxy.getInvocationHandler(o) == this);
        }
        return Boolean.FALSE;
      }
      
      FunctionInfo f = null;
      synchronized (functions) {
        f = (FunctionInfo)functions.get(method);
        if (f == null) {
          f = new FunctionInfo(null);
          isVarArgs = Function.isVarArgs(method);
          if (invocationMapper != null) {
            handler = invocationMapper.getInvocationHandler(nativeLibrary, method);
          }
          if (handler == null)
          {
            String methodName = functionMapper.getFunctionName(nativeLibrary, method);
            
            if (methodName == null)
            {
              methodName = method.getName();
            }
            function = nativeLibrary.getFunction(methodName, method);
            options = new HashMap(options);
            options.put("invoking-method", method);
          }
          functions.put(method, f);
        }
      }
      if (isVarArgs) {
        inArgs = Function.concatenateVarArgs(inArgs);
      }
      if (handler != null) {
        return handler.invoke(proxy, method, inArgs);
      }
      return function.invoke(method.getReturnType(), inArgs, options);
    }
  }
}
