package org.jboss.forge.spec.javaee.ejb.util;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;

public class JavaUtils {

	public static JavaClass getJavaClassFrom(Resource<?> resource)
			throws FileNotFoundException {
		JavaSource<?> source = ((JavaResource) resource).getJavaSource();
		if (!source.isClass()) {
			throw new IllegalStateException(
					"Current resource is not a JavaClass!");
		}
		return (JavaClass) source;
	}

	public static Field<JavaClass> addFieldTo(JavaClass targetEjb,
			String fieldType, String fieldName,
			Class<? extends java.lang.annotation.Annotation> annotation,
			Project project, Shell shell) throws FileNotFoundException {
		if (targetEjb.hasField(fieldName)) {
			throw new IllegalStateException("Ejb already has a field named ["
					+ fieldName + "]");
		}

		JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

		Field<JavaClass> field = targetEjb.addField();
		field.setName(fieldName).setPrivate()
				.setType(Types.toSimpleName(fieldType))
				.addAnnotation(annotation);
		targetEjb.addImport(fieldType);
		java.saveJavaSource(targetEjb);
		shell.println("Added field to " + targetEjb.getQualifiedName() + ": "
				+ field);

		return field;
	}

	public static void addMethodTo(JavaClass javaClass, String interfaceClass,
			Shell shell) {
		javaClass.addInterface(interfaceClass);
		Class clazz = null;
		try {
			clazz = Class.forName(interfaceClass);
		} catch (ClassNotFoundException e) {
			shell.println("Exception: " + e);
		}
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			org.jboss.forge.parser.java.Method<JavaClass> methodJavaClass = javaClass
					.addMethod();
			if (!method.getReturnType().isPrimitive()) {
				javaClass.addImport(method.getReturnType());
				methodJavaClass.setBody(" return null;");
			} else {
				methodJavaClass.setBody("");
			}
			methodJavaClass.setPublic().setReturnType(method.getReturnType())
					.setName(method.getName());
			Class<?>[] params = method.getParameterTypes();
			if (params != null && params.length > 0) {
				int i = 0;
				StringBuffer sb = new StringBuffer();
				for (Class<?> class1 : params) {
					sb.append("," + class1.getName() + " arg" + i);
					i++;
					if (!javaClass.getInterfaces().contains(class1)) {
						javaClass.addImport(class1);
					}
				}
				methodJavaClass.setParameters(sb.toString().substring(1));
			}
		}
	}
}
