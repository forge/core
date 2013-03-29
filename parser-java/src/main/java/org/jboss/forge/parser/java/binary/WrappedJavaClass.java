package org.jboss.forge.parser.java.binary;

import java.lang.reflect.Modifier;

import org.jboss.forge.parser.java.JavaClass;

public class WrappedJavaClass extends WrappedJavaType<JavaClass> implements JavaClass {

	private final Class<?> clzz;
	
	public WrappedJavaClass(Class<?> c) {
		super(c);
		clzz = c;
	}
	
	@Override
	public boolean isClass() {
		return true;
	}

	@Override
	public String getSuperType() {
		return clzz.getSuperclass().getName();
	}

	@Override
	public JavaClass setSuperType(JavaClass type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JavaClass setSuperType(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JavaClass setSuperType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(clzz.getModifiers());
	}

	@Override
	public JavaClass setAbstract(boolean abstrct) {
		// TODO: implement
		return null;
	}
}
