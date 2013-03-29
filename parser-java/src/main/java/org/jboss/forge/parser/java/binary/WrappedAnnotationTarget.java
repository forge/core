package org.jboss.forge.parser.java.binary;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.JavaSource;

/**
 * supports wrapped methods and fields.
 * @author jfraney
 *
 * @param <O>
 * @param <T>
 */
class WrappedAnnotationTarget<O extends JavaSource<O>, T> implements AnnotationTarget<O, T> {

	private final AnnotatedElement annotatedElement;
	
	public WrappedAnnotationTarget(AnnotatedElement annotatedElement) {
		this.annotatedElement = annotatedElement;
	}
	
	@Override
	public Annotation<O> addAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> addAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation<O> addAnnotation(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Annotation<O>> getAnnotations() {
		List<Annotation<O>> result = new ArrayList<Annotation<O>>();
		for(java.lang.annotation.Annotation a: annotatedElement.getAnnotations()) {
			Annotation<O> wrapped = new WrappedAnnotation<O>(a);
			result.add(wrapped);
		}
		return result;
	}

	@Override
	public boolean hasAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		return annotatedElement.isAnnotationPresent(type);
	}


	@Override
	public boolean hasAnnotation(String type) {
		boolean result = false;
		Class<? extends java.lang.annotation.Annotation> c = getAnnotationClass(type);
		
		if(c != null) {
			result = hasAnnotation(c);
		}
		return result;
	}
	

	@Override
	public Annotation<O> getAnnotation(
			Class<? extends java.lang.annotation.Annotation> type) {
		Annotation<O> result = null;
		java.lang.annotation.Annotation annotation = annotatedElement.getAnnotation(type);
		if(annotation != null) {
			result = new WrappedAnnotation<O>(annotation);
		}
		return result;
	}
	
	@Override
	public Annotation<O> getAnnotation(String type) {
		Annotation<O> result = null;
		Class<? extends java.lang.annotation.Annotation> c = getAnnotationClass(type);
		
		if(c != null) {
			result = getAnnotation(c);
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends java.lang.annotation.Annotation> getAnnotationClass(
			String type) {
		Class<? extends java.lang.annotation.Annotation> c = null;
		try {
			c = (Class<? extends java.lang.annotation.Annotation>) Class.forName(type);
		} catch (ClassNotFoundException e) {
		} catch (ClassCastException e) {
		}
		return c;
	}

	@Override
	public T removeAnnotation(Annotation<O> annotation) {
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
		// TODO Auto-generated method stub
		return null;
	}

}
