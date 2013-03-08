package org.jboss.forge.parser.java.binary;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.Import;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.SourceType;
import org.jboss.forge.parser.java.SyntaxError;
import org.jboss.forge.parser.java.Visibility;

public abstract class WrappedJavaSource<O extends JavaSource<O>> implements JavaSource<O> {

	private final Class<?> clzz;
	private final AnnotationTarget<O, O> annotationTarget;

	public WrappedJavaSource(Class<?> c) {
		clzz = c;
		annotationTarget = new WrappedAnnotationTarget<O, O>(c);
	}
	
	@Override
	public String getCanonicalName() {
		return clzz.getCanonicalName();
	}

	@Override
	public String getQualifiedName() {
		return clzz.getName();
	}

	@Override
	public List<SyntaxError> getSyntaxErrors() {
		return Collections.emptyList();
	}

	@Override
	public boolean hasSyntaxErrors() {
		return false;
	}

	@Override
	public boolean isClass() {
		return false;
	}

	@Override
	public boolean isEnum() {
		return false;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public boolean isAnnotation() {
		return false;
	}

	@Override
	public JavaSource<?> getEnclosingType() {
		JavaSource<?> result = this;
		if(clzz.getEnclosingClass() != null) {
			result = new WrappedJavaClass(clzz.getEnclosingClass());
		}
		return result;
	}

	@Override
	public List<JavaSource<?>> getNestedClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SourceType getSourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPackage() {
		return clzz.getPackage().getName();
	}

	@Override
	public O setPackage(String name) {
		return null;
	}

	@Override
	public O setDefaultPackage() {
		return null;
	}

	@Override
	public boolean isDefaultPackage() {
		return clzz.getPackage().getName().length() == 0;
	}

	@Override
	public Import addImport(String className) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Import addImport(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Import addImport(Import imprt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends JavaSource<?>> Import addImport(T type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasImport(Class<?> type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasImport(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requiresImport(Class<?> type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requiresImport(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends JavaSource<T>> boolean hasImport(T type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasImport(Import imprt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Import getImport(String literalValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Import getImport(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends JavaSource<?>> Import getImport(T type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Import getImport(Import imprt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O removeImport(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O removeImport(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends JavaSource<?>> O removeImport(S type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O removeImport(Import imprt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Import> getImports() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String resolveType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return clzz.getSimpleName();
	}

	@Override
	public O setName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPackagePrivate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public O setPackagePrivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPublic() {
		return Modifier.isPublic(clzz.getModifiers());
	}

	@Override
	public O setPublic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPrivate() {
		return Modifier.isPrivate(clzz.getModifiers());
	}

	@Override
	public O setPrivate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isProtected() {
		return Modifier.isProtected(clzz.getModifiers());
	}

	@Override
	public O setProtected() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Visibility getVisibility() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O setVisibility(Visibility scope) {
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
		WrappedJavaSource<O> other = (WrappedJavaSource<O>) obj;
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
