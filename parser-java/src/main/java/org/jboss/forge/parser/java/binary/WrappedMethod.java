package org.jboss.forge.parser.java.binary;

import java.lang.reflect.Modifier;
import java.util.List;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.java.Type;
import org.jboss.forge.parser.java.Visibility;

public class WrappedMethod<O extends JavaSource<O>> implements Method<O> {

	private final java.lang.reflect.Method method;
	private final WrappedMethodHolder<O> holder;
	private final AnnotationTarget<O, Method<O>> annotationTarget;
	
	public WrappedMethod(WrappedMethodHolder<O> holder, java.lang.reflect.Method m) {
		this.method = m;
		this.holder = holder;
		this.annotationTarget = new WrappedAnnotationTarget<O, Method<O>>(m);
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(method.getModifiers());
	}

	@Override
	public Method<O> setAbstract(boolean abstrct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return method.getName();
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(method.getModifiers());
	}

	@Override
	public Method<O> setFinal(boolean finl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}

	@Override
	public Method<O> setStatic(final boolean statc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPackagePrivate() {
		return !isPrivate() && !isProtected() && !isPublic();
	}

	@Override
	public Method<O> setPackagePrivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPublic() {
		return Modifier.isPublic(method.getModifiers());
	}

	@Override
	public Method<O> setPublic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPrivate() {
		return Modifier.isPrivate(method.getModifiers());
	}

	@Override
	public Method<O> setPrivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isProtected() {
		return Modifier.isProtected(method.getModifiers());
	}

	@Override
	public Method<O> setProtected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Visibility getVisibility() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> setVisibility(Visibility scope) {
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
	public String getBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> setBody(String body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> setConstructor(boolean constructor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConstructor() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Method<O> setName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQualifiedReturnType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type<O> getReturnTypeInspector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> setReturnType(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> setReturnType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> setReturnType(JavaSource<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReturnTypeVoid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Method<O> setReturnTypeVoid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> setParameters(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Parameter<O>> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> addThrows(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> addThrows(Class<? extends Exception> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getThrownExceptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> removeThrows(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> removeThrows(Class<? extends Exception> type) {
		// TODO Auto-generated method stub
		return null;
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

	public Method<O> removeAnnotation(Annotation<O> annotation) {
		return annotationTarget.removeAnnotation(annotation);
	}


	public String toString() {
		return method.toString();
	}
	
}
