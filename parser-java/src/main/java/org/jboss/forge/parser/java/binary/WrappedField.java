package org.jboss.forge.parser.java.binary;

import java.lang.reflect.Modifier;
import java.util.List;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.Visibility;

/**
 * wraps reflection's Field.
 * @author jfraney
 *
 */
public class WrappedField<O extends JavaSource<O>> implements Field<O> {

	private final java.lang.reflect.Field field;
	private final WrappedFieldHolder<O> holder;
	private final AnnotationTarget<O, Field<O>> annotationTarget;
	
	public WrappedField(WrappedFieldHolder<O> holder, java.lang.reflect.Field f) {
		this.field = f;
		this.annotationTarget = new WrappedAnnotationTarget<O, Field<O>>(f);
		this.holder = holder;
		
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(field.getModifiers());
	}

	@Override
	public Field<O> setFinal(boolean finl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(field.getModifiers());
	}

	@Override
	public Field<O> setStatic(boolean statc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPackagePrivate() {
		return !isProtected() && !isPublic() && !isPrivate();
	}

	@Override
	public Field<O> setPackagePrivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPublic() {
		return Modifier.isPublic(field.getModifiers());
	}

	@Override
	public Field<O> setPublic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPrivate() {
		return Modifier.isPrivate(field.getModifiers());
	}

	@Override
	public Field<O> setPrivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isProtected() {
		return Modifier.isProtected(field.getModifiers());
	}

	@Override
	public Field<O> setProtected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Visibility getVisibility() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field<O> setVisibility(Visibility scope) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getInternal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O getOrigin() {
		return holder.getDelegatee();
	}

	@Override
	public Field<O> setName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return field.getType().getSimpleName();
	}

	@Override
	public String getQualifiedType() {
		return field.getType().getName();
	}

	@Override
	public Type<O> getTypeInspector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isType(Class<?> type) {
		return field.getType().equals(type);
	}

	@Override
	public boolean isType(String type) {
		return field.getType().getName().equals(type);
	}

	@Override
	public Field<O> setType(Class<?> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field<O> setType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field<O> setType(JavaSource<?> entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringInitializer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLiteralInitializer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field<O> setLiteralInitializer(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field<O> setStringInitializer(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPrimitive() {
		return field.getType().isPrimitive();
	}

	public Annotation<O> addAnnotation() {
		return annotationTarget.addAnnotation();
	}

	public Annotation<O> addAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		return annotationTarget.addAnnotation(type);
	}

	public Annotation<O> addAnnotation(String className) {
		return annotationTarget.addAnnotation(className);
	}

	public List<Annotation<O>> getAnnotations() {
		return annotationTarget.getAnnotations();
	}

	public boolean hasAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		return annotationTarget.hasAnnotation(type);
	}

	public boolean hasAnnotation(String type) {
		return annotationTarget.hasAnnotation(type);
	}

	public Annotation<O> getAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		return annotationTarget.getAnnotation(type);
	}

	public Annotation<O> getAnnotation(String type) {
		return annotationTarget.getAnnotation(type);
	}

	public Field<O> removeAnnotation(Annotation<O> annotation) {
		return annotationTarget.removeAnnotation(annotation);
	}
	
	public String toString() {
		return field.toString();
	}

}
