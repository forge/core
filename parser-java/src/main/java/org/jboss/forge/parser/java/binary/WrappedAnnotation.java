package org.jboss.forge.parser.java.binary;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.ValuePair;

/**
 * wraps the reflective java.lang.annotation.Annotation.
 * 
 * @author jfraney
 *
 * @param <O>
 */
public class WrappedAnnotation<O extends JavaSource<O>> implements Annotation<O> {

	private final java.lang.annotation.Annotation annotation;
	
	public WrappedAnnotation(java.lang.annotation.Annotation annotation) {
		this.annotation = annotation;
	}

	@Override
	public Object getInternal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O getOrigin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSingleValue() {
		try {
			Class<? extends java.lang.annotation.Annotation> t = annotation.annotationType();
			return t.getDeclaredMethods().length == 1 && t.getMethod("value") != null;
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
		return false;
	}

	@Override
	public boolean isMarker() {
		try {
			Class<? extends java.lang.annotation.Annotation> t = annotation.annotationType();
			return t.getDeclaredMethods().length == 0;
		} catch (SecurityException e) {
		}
		return false;
	}

	@Override
	public boolean isNormal() {
		try {
			Class<? extends java.lang.annotation.Annotation> t = annotation.annotationType();
			return t.getDeclaredMethods().length >= 1;
		} catch (SecurityException e) {
		}
		return false;
	}

	@Override
	public String getName() {
		return annotation.getClass().getName();
	}

	@Override
	public String getQualifiedName() {
		return  annotation.annotationType().getName();
	}

	@Override
	public <T extends Enum<T>> T getEnumValue(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Enum<T>> T getEnumValue(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Enum<T>> T[] getEnumArrayValue(Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Enum<T>> T[] getEnumArrayValue(Class<T> type, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLiteralValue() {
		return getLiteralValue("value");
	}

	@Override
	public String getLiteralValue(String name) {
		Object value = getValue(name);
		if(value instanceof String) {
			StringBuilder b = new StringBuilder();
			value = b.append('"').append(value).append('"');
		}
		return value == null ? null : value.toString();
	}

	@Override
	public List<ValuePair> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringValue() {
		return getStringValue("value");
	}

	@Override
	public String getStringValue(String name) {
		return getValue(name) == null ? null : getValue(name).toString();
	}

	private Object getValue(String name) {
		Object result = null;
		if(isSingleValue() || isNormal()) {
			try {
				result = annotation.annotationType().getMethod(name).invoke(annotation);
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			} catch (NoSuchMethodException e) {
			} catch (SecurityException e) {
			}
		}
		return result;
	}

	@Override
	public Annotation<O> removeValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> removeAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setName(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setEnumValue(String name, Enum<?> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setEnumValue(Enum<?>... value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setEnumArrayValue(String name,
			Enum<?>... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setEnumArrayValue(Enum<?>... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setLiteralValue(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setLiteralValue(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setStringValue(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setStringValue(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> getAnnotationValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> getAnnotationValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setAnnotationValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setAnnotationValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getClassValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?> getClassValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?>[] getClassArrayValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?>[] getClassArrayValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setClassValue(String name, Class<?> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setClassValue(Class<?> value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setClassArrayValue(String name,
			Class<?>... values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> setClassArrayValue(Class<?>... values) {
		// TODO Auto-generated method stub
		return null;
	}

}
