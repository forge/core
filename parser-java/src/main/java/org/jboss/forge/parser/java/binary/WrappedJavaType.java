package org.jboss.forge.parser.java.binary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.InterfaceCapable;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.JavaType;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;

public class WrappedJavaType<O extends JavaSource<O>> extends WrappedJavaSource<O> implements JavaType<O> {

	private final Class<?> clzz;
	private final AnnotationTarget<O, O> annotationTarget;
	private final FieldHolder<O> fieldHolder;
	private final MethodHolder<O> methodHolder;
	private final InterfaceCapable<O> interfaceCapable;

	@SuppressWarnings("unchecked")
	public WrappedJavaType(Class<?> c) {
		super(c);
		clzz = c;
		annotationTarget = new WrappedAnnotationTarget<O, O>(c);
		fieldHolder = new WrappedFieldHolder<O>((O)this, c);
		methodHolder = new WrappedMethodHolder<O>((O)this, c);
		interfaceCapable = new WrappedInterfaceCapable<O>((O)this, c);
	}
	

	@Override
	public List<Member<O, ?>> getMembers() {
		List<Member<O, ?>> result = new ArrayList<Member<O,?>>();
		result.addAll(getMethods());
		result.addAll(getFields());
		return result;
	}

	@Override
	public List<String> getInterfaces() {
		return interfaceCapable.getInterfaces();
	}

	@Override
	public O addInterface(String type) {
		return interfaceCapable.addInterface(type);
	}

	@Override
	public O addInterface(Class<?> type) {
		return interfaceCapable.addInterface(type);
	}

	@Override
	public O addInterface(JavaInterface type) {
		return interfaceCapable.addInterface(type);
	}

	@Override
	public boolean hasInterface(String type) {
		return interfaceCapable.hasInterface(type);
	}

	@Override
	public boolean hasInterface(Class<?> type) {
		return interfaceCapable.hasInterface(type);
	}

	@Override
	public boolean hasInterface(JavaInterface type) {
		return interfaceCapable.hasInterface(type);
	}

	@Override
	public O removeInterface(String type) {
		return interfaceCapable.removeInterface(type);
	}

	@Override
	public O removeInterface(Class<?> type) {
		return interfaceCapable.removeInterface(type);
	}

	@Override
	public O removeInterface(JavaInterface type) {
		return interfaceCapable.removeInterface(type);
	}

	@Override
	public Field<O> addField() {
		return fieldHolder.addField();
	}

	@Override
	public Field<O> addField(String declaration) {
		return fieldHolder.addField(declaration);
	}

	@Override
	public boolean hasField(String name) {
		return fieldHolder.hasField(name);
	}

	@Override
	public boolean hasField(Field<O> field) {
		return fieldHolder.hasField(field);
	}

	@Override
	public Field<O> getField(String name) {
		return fieldHolder.getField(name);
	}

	@Override
	public List<Field<O>> getFields() {
		return fieldHolder.getFields();
	}

	@Override
	public O removeField(Field<O> field) {
		return fieldHolder.removeField(field);
	}

	@Override
	public Method<O> addMethod() {
		return methodHolder.addMethod();
	}

	@Override
	public Method<O> addMethod(String method) {
		return methodHolder.addMethod(method);
	}

	@Override
	public boolean hasMethod(Method<O> name) {
		return methodHolder.hasMethod(name);
	}

	@Override
	public boolean hasMethodSignature(Method<?> method) {
		return methodHolder.hasMethodSignature(method);
	}

	@Override
	public boolean hasMethodSignature(String name) {
		return methodHolder.hasMethodSignature(name);
	}

	@Override
	public boolean hasMethodSignature(String name, String... paramTypes) {
		return methodHolder.hasMethodSignature(name, paramTypes);
	}

	@Override
	public boolean hasMethodSignature(String name, Class<?>... paramTypes) {
		return methodHolder.hasMethodSignature(name, paramTypes);
	}

	@Override
	public Method<O> getMethod(String name) {
		return methodHolder.getMethod(name);
	}

	@Override
	public Method<O> getMethod(String name, String... paramTypes) {
		return methodHolder.getMethod(name, paramTypes);
	}

	@Override
	public Method<O> getMethod(String name, Class<?>... paramTypes) {
		return methodHolder.getMethod(name, paramTypes);
	}

	@Override
	public List<Method<O>> getMethods() {
		return methodHolder.getMethods();
	}

	@Override
	public O removeMethod(Method<O> method) {
		return methodHolder.removeMethod(method);
	}

	@Override
	public O addGenericType(String genericType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O removeGenericType(String genericType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getGenericTypes() {
		// TODO: what error? cannot get generic types from the binary class
		return Collections.emptyList();
	}

	@Override
	public Annotation<O> addAnnotation() {
		return annotationTarget.addAnnotation();
	}

	@Override
	public Annotation<O> addAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		return annotationTarget.addAnnotation(type);
	}

	@Override
	public Annotation<O> addAnnotation(String className) {
		return annotationTarget.addAnnotation(className);
	}

	@Override
	public List<Annotation<O>> getAnnotations() {
		return annotationTarget.getAnnotations();
	}

	@Override
	public boolean hasAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		return annotationTarget.hasAnnotation(type);
	}

	@Override
	public boolean hasAnnotation(String type) {
		return annotationTarget.hasAnnotation(type);
	}

	@Override
	public Annotation<O> getAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		return annotationTarget.getAnnotation(type);
	}

	@Override
	public Annotation<O> getAnnotation(String type) {
		return annotationTarget.getAnnotation(type);
	}

	@Override
	public O removeAnnotation(
			Annotation<O> annotation) {
		return  annotationTarget.removeAnnotation(annotation);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clzz == null) ? 0 : clzz.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		WrappedJavaType<O> other = (WrappedJavaType<O>) obj;
		if (clzz == null) {
			if (other.clzz != null) {
				return false;
			}
		} else if (!clzz.equals(other.clzz)) {
			return false;
		}
		return true;
	}

}
