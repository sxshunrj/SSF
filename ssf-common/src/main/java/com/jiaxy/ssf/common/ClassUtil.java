package com.jiaxy.ssf.common;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * see spring ClassUtils
 *
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/04/08 14:48
 */
public class ClassUtil {

	/** Suffix for array class names: "[]" */
	public static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: "[" */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/** Prefix for internal non-primitive array class names: "[L" */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	/** The package separator character '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

    /**
	 * Map with primitive wrapper type as key and corresponding primitive
	 * type as value, for example: Integer.class -> int.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);

	/**
	 * Map with primitive type as key and corresponding wrapper
	 * type as value, for example: int.class -> Integer.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<Class<?>, Class<?>>(8);


	/**
	 * Map with primitive type name as key and corresponding primitive
	 * type as value, for example: "int" -> "int.class".
	 */
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<String, Class<?>>(32);


    private static final Map<String,Class<?>> commonClassCache = new HashMap<String, Class<?>>(32);


	private static final ConcurrentHashMap<String,Class<?>> classCache = new ConcurrentHashMap<String, Class<?>>();

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);
		for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
			primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
			registerCommonClasses(entry.getKey());
		}
		Set<Class<?>> primitiveTypes = new HashSet<Class<?>>(32);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		primitiveTypes.addAll(Arrays.asList(
				boolean[].class, byte[].class, char[].class, double[].class,
				float[].class, int[].class, long[].class, short[].class));
		primitiveTypes.add(void.class);
		for (Class<?> primitiveType : primitiveTypes) {
			primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}
		registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,
				Float[].class, Integer[].class, Long[].class, Short[].class);
		registerCommonClasses(Number.class, Number[].class, String.class, String[].class,
				Object.class, Object[].class, Class.class, Class[].class);
		registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,
				Error.class, StackTraceElement.class, StackTraceElement[].class);

    }


	public static ClassLoader getDefaultClassLoader(){
        ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable e){
		}
		if (cl == null){
			cl = ClassUtil.class.getClassLoader();
		}
		return cl;
	}


	public static Class<?> forName(String name,ClassLoader classLoader,boolean isCache) throws ClassNotFoundException {
		if ( isCache && classCache.get(name) != null ){
			return classCache.get(name);
		}
		Class<?> clz = forName(name,classLoader);
		if ( isCache ){
			classCache.put(name,clz);
		}
		return clz;
	}

    public static Class<?> forName(String name,ClassLoader classLoader) throws ClassNotFoundException {
		Assert.notNull(name,"name must not be null");
		Class<?> clz = resolverPrimitiveClassName(name);
		if (clz == null){
			clz = commonClassCache.get(name);
		}
		if (clz != null){
			return clz;
		}
		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)){
			String elementClassName = name.substring(0,name.length() - ARRAY_SUFFIX.length());
			Class<?> elementClz = forName(elementClassName,classLoader);
			return Array.newInstance(elementClz,0).getClass();
		}
		// "[Ljava.lang.String;" style arrays
		if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
			String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[[I" or "[[Ljava.lang.String;" style arrays
		if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
			String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		try {
			return classLoaderToUse.loadClass(name);
		} catch (ClassNotFoundException e){
			int lastDotIndex = name.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
				try {
					return classLoaderToUse.loadClass(innerClassName);
				}
				catch (ClassNotFoundException ex2) {
					// swallow - let original exception get through
				}
			}
			throw e;
		}
    }


	public static Class[] forNames(String[] names) throws ClassNotFoundException {
		if (names == null || names.length == 0){
			return null;
		}
		Class[] clzArr = new Class[names.length];
		for ( int i = 0 ;i < names.length ;i++ ){
			clzArr[i] = forName(names[i],getDefaultClassLoader());
		}
		return clzArr;
	}


	public static Method getMethod(Class<?> clz,String methodName,Class<?>... paramTypes){
		Assert.notNull(clz,"Class must not be null");
		Assert.notNull(methodName,"Method name must not be null");
		try {
			return clz.getMethod(methodName,paramTypes);
		} catch (NoSuchMethodException e){
			throw new IllegalStateException("method not found: ",e);
		}
	}


	/**
	 * Register the given common classes with the ClassUtils cache.
	 */
	private static void registerCommonClasses(Class<?>... commonClasses) {
		for (Class<?> clazz : commonClasses) {
			commonClassCache.put(clazz.getName(), clazz);
		}
	}


	private static Class<?> resolverPrimitiveClassName(String name){
		Class<?> clz = null;
		//try maybe is the primitive type
		if ( name != null && name.length() < 8 ){
			clz = primitiveTypeNameMap.get(name);
		}
		return clz;
	}
}
