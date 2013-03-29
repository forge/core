package org.jboss.forge.parser.java.binary;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.InterfaceCapable;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;

public class WrappedJavaEnum extends WrappedJavaSource<JavaEnum> implements JavaEnum {
	private final InterfaceCapable<JavaEnum> interfaceCapable;
	private final FieldHolder<JavaEnum> fieldHolder;
	private final MethodHolder<JavaEnum> methodHolder;
	
	public WrappedJavaEnum(Class<?> c) {
		super(c);
		interfaceCapable = new WrappedInterfaceCapable<JavaEnum>(this, c);
		fieldHolder = new WrappedFieldHolder<JavaEnum>(this, c);
		methodHolder = new WrappedMethodHolder<JavaEnum>(this, c);
	}

	@Override
	public boolean isClass() {
		return false;
	}

	@Override
	public EnumConstant<JavaEnum> addEnumConstant() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumConstant<JavaEnum> addEnumConstant(String declaration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumConstant<JavaEnum> getEnumConstant(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EnumConstant<JavaEnum>> getEnumConstants() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getInterfaces() {
		return interfaceCapable.getInterfaces();
	}

	@Override
	public JavaEnum addInterface(String type) {
		return interfaceCapable.addInterface(type);
	}

	@Override
	public JavaEnum addInterface(Class<?> type) {
		return interfaceCapable.addInterface(type);
	}

	@Override
	public JavaEnum addInterface(JavaInterface type) {
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
	public JavaEnum removeInterface(String type) {
		return interfaceCapable.removeInterface(type);
	}

	@Override
	public JavaEnum removeInterface(Class<?> type) {
		return interfaceCapable.removeInterface(type);
	}

	@Override
	public JavaEnum removeInterface(JavaInterface type) {
		return interfaceCapable.removeInterface(type);
	}

	@Override
	public Field<JavaEnum> addField() {
		return fieldHolder.addField();
	}

	@Override
	public Field<JavaEnum> addField(String declaration) {
		return fieldHolder.addField(declaration);
	}

	@Override
	public boolean hasField(String name) {
		return fieldHolder.hasField(name);
	}

	@Override
	public boolean hasField(Field<JavaEnum> field) {
		return fieldHolder.hasField(field);
	}

	@Override
	public Field<JavaEnum> getField(String name) {
		return fieldHolder.getField(name);
	}

	@Override
	public List<Field<JavaEnum>> getFields() {
		return fieldHolder.getFields();
	}

	@Override
	public JavaEnum removeField(Field<JavaEnum> field) {
		return fieldHolder.removeField(field);
	}

	@Override
	public Method<JavaEnum> addMethod() {
		return methodHolder.addMethod();
	}

	@Override
	public Method<JavaEnum> addMethod(String method) {
		return methodHolder.addMethod(method);
	}

	@Override
	public boolean hasMethod(Method<JavaEnum> name) {
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
	public Method<JavaEnum> getMethod(String name) {
		return methodHolder.getMethod(name);
	}

	@Override
	public Method<JavaEnum> getMethod(String name, String... paramTypes) {
		return methodHolder.getMethod(name, paramTypes);
	}

	@Override
	public Method<JavaEnum> getMethod(String name, Class<?>... paramTypes) {
		return methodHolder.getMethod(name, paramTypes);
	}

	@Override
	public List<Method<JavaEnum>> getMethods() {
		return methodHolder.getMethods();
	}

	@Override
	public JavaEnum removeMethod(Method<JavaEnum> method) {
		return methodHolder.removeMethod(method);
	}

	@Override
	public List<Member<JavaEnum, ?>> getMembers() {
		List<Member<JavaEnum, ?>> result = new ArrayList<Member<JavaEnum, ?>>();
		result.addAll(methodHolder.getMembers());
		result.addAll(fieldHolder.getMembers());
		return result;
	}

	
}
