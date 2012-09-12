/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.ejb;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.MessageListener;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.events.PickupResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.spec.javaee.EJBFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("ejb")
@RequiresProject
public class EJBPlugin implements Plugin {
	@Inject
	private Project project;

	@Inject
	@Current
	private JavaResource resource;

	@Inject
	private Shell shell;

	@Inject
	private Event<InstallFacets> request;

	@Inject
	private Event<PickupResource> pickup;

	@SetupCommand
	public void setup(final PipeOut out) {
		if (!project.hasFacet(EJBFacet.class)) {
			request.fire(new InstallFacets(EJBFacet.class));
		}

		if (project.hasFacet(EJBFacet.class)) {
			ShellMessages.success(out,
					"Enterprise Java Beans (EJB) is installed.");
		}
	}

	/*
	 * default: create EJB STATELESS WITH LOCALBEAN ANNOTATION
	 */
	@Command("new-ejb")
	public void newEjb(
			@Option(required = true, name = "packageAndName", description = "The ejb name with package: i.e. by.giava.service.Flower") final JavaResource resource,
			@Option(required = false, name = "type", defaultValue = "STATELESS") EjbType type,
			@Option(required = false, name = "overwrite") final boolean overwrite)
			throws FileNotFoundException {
		JavaClass ejb = null;
		if (!resource.exists() || overwrite) {
			JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
			if (resource.createNewFile()) {
				JavaClass javaClass = JavaParser.create(JavaClass.class);
				javaClass.setName(java.calculateName(resource));
				javaClass.setPackage(java.calculatePackage(resource));
				ejb = javaClass;
			}
		} else if (overwrite) {
			ejb = getJavaClassFrom(resource);
		} else {
			throw new RuntimeException("PackageAndName already exists ["
					+ resource.getFullyQualifiedName()
					+ "] Re-run with '--overwrite' to continue.");
		}
		if (type == null) {
			type = EjbType.STATELESS;
		}
		if (EjbType.MESSAGEDRIVEN.equals(type)) {
			String destinationType = shell.promptCommon(
					"Destination type: javax.jms.Queue or javax.jms.Topic:",
					PromptType.JAVA_CLASS,
					JmsDestinationType.QUEUE.getDestinationType());
			String destinationName = shell.promptCommon("Destination Name:",
					PromptType.ANY, "queue/test");
			try {

				ejb.addImport(ActivationConfigProperty.class);
				ejb.addImport(MessageDriven.class);
				ejb.addInterface(MessageListener.class);
				ejb.addMethod("public void onMessage(Message message) {}");
				ejb.addAnnotation(EjbType.MESSAGEDRIVEN.getAnnotation())
						// .setLiteralValue("name", "testName");
						.setLiteralValue(
								" @MessageDriven(name = \""
										+ resource.getName().substring(
												0,
												resource.getName().lastIndexOf(
														"."))
										+ "\", activationConfig = {"
										+ "		@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \""
										+ destinationType
										+ "\"),"
										+ "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \""
										+ destinationName + ") })");

			} catch (Exception e) {
				shell.println("Exception: " + e.getMessage());
			}
		} else {
			ejb.addAnnotation(type.getAnnotation());
			ejb.addAnnotation("javax.ejb.LocalBean");
		}

		resource.setContents(ejb);
		pickup.fire(new PickupResource(resource));

	}

	/*
	 * add some interface with all methods
	 */
	@Command("add-implements")
	@RequiresResource(JavaResource.class)
	public void addInterface(
			@Option(name = "type", required = true, type = PromptType.JAVA_CLASS, description = "The qualified Class to be used as interface") final String type,
			final PipeOut out) throws FileNotFoundException,
			ClassNotFoundException {
		try {
			JavaClass ejb = getJavaClass();
			if (!ejb.getInterfaces().contains(type)) {
				shell.println("type: " + type);
				ejb.addImport(type);
				addMethodTo(ejb, type);
				resource.setContents(ejb);
			} else {
				throw new RuntimeException(
						"Current resource contains Class to be used as interface!");
			}
		} catch (FileNotFoundException e) {
			shell.println("Could not locate the Class to be used as interface. No update was made.");
		} catch (Exception e) {
			shell.println("Exception: " + e);
		}
	}

	/*
	 * add some superclass
	 */
	@Command("add-extends")
	@RequiresResource(JavaResource.class)
	public void addSuperClass(
			@Option(name = "type", required = true, type = PromptType.JAVA_CLASS, description = "The qualified Class to be used as super class") final String type,
			final PipeOut out) throws FileNotFoundException {
		try {
			JavaClass ejb = getJavaClass();
			if (!ejb.getSuperType().contains(type)) {
				ejb.setSuperType(type);
			} else {
				throw new RuntimeException(
						"Current resource contains Class to be used as super class!");
			}
			resource.setContents(ejb);
		} catch (FileNotFoundException e) {
			shell.println("Could not locate the Class to be used as super class. No update was made.");
		}

	}

	/*
	 * add @Inject with some class
	 */
	@Command("add-inject")
	@RequiresResource(JavaResource.class)
	public void addInject(
			@Option(name = "named", required = true, description = "The field name", type = PromptType.JAVA_VARIABLE_NAME) final String fieldName,
			@Option(name = "type", required = true, type = PromptType.JAVA_CLASS, description = "The qualified Class to be used as this field's type") final String type) {
		try {
			JavaClass ejb = getJavaClass();
			String javaType = (type.toLowerCase().endsWith(".java")) ? type
					.substring(0, type.length() - 5) : type;

			addFieldTo(ejb, javaType, fieldName, Inject.class);
			resource.setContents(ejb);
		} catch (FileNotFoundException e) {
			shell.println("Could not locate the Class to be used as this field's type. No update was made.");
		}
	}

	/*
	 * add @TransactionAttribute(TransactionAttributeType.MANDATORY|REQUIRED|
	 * REQUIRES_NEW|SUPPORTS|NOT_SUPPORTED|NEVER)
	 */

	@Command("add-transactionAttribute")
	@RequiresResource(JavaResource.class)
	public void addTransactionAttribute(
			@Option(required = true, name = "type") final TransactionAttributeType transactionAttributeType,
			final PipeOut out) throws FileNotFoundException {
		JavaClass ejb = getJavaClass();
		if (!ejb.getAnnotations().contains(TransactionAttributeType.class)) {
			ejb.addAnnotation(TransactionAttribute.class).setEnumValue(
					transactionAttributeType);
			resource.setContents(ejb);
		} else {
			throw new RuntimeException(
					"Current resource contains TransactionAttributeType!");
		}

	}

	private JavaClass getJavaClass() throws FileNotFoundException {
		if (resource instanceof JavaResource) {
			return getJavaClassFrom(resource);
		} else {
			throw new RuntimeException(
					"Current resource is not a JavaResource!");
		}

	}

	private JavaClass getJavaClassFrom(final Resource<?> resource)
			throws FileNotFoundException {
		JavaSource<?> source = ((JavaResource) resource).getJavaSource();
		if (!source.isClass()) {
			throw new IllegalStateException(
					"Current resource is not a JavaClass!");
		}
		return (JavaClass) source;
	}

	private Field<JavaClass> addFieldTo(final JavaClass targetEjb,
			final String fieldType, final String fieldName,
			final Class<? extends java.lang.annotation.Annotation> annotation)
			throws FileNotFoundException {
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

	private void addMethodTo(JavaClass javaClass, String interfaceClass) {

		javaClass.addInterface(interfaceClass);
		Class clazz = null;
		try {
			clazz = Class.forName(interfaceClass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						// System.out.println(i + ") " + class1);
					} else {
						// System.out.println("NO) " + class1);
					}
				}
				methodJavaClass.setParameters(sb.toString().substring(1));
			}
		}
	}

	private static List<String> getMethods(String name)
			throws ClassNotFoundException {
		List<String> methodList = new ArrayList<String>();
		Class clazz = Class.forName(name);
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			methodList.add(method.toString().replaceAll("abstract", "") + "{}");
			// TODO ADD RETURN ID RETURNTYPE != void
			if (method.getReturnType() != null
					&& !void.class.equals(method.getReturnType())) {
				System.out.println("RET TYPE: " + method.getReturnType());
			} else {
				System.out.println("VOID");
			}
			System.out.println("NAME: " + method.getName());
			Class<?>[] params = method.getParameterTypes();
			for (Class<?> class1 : params) {
				System.out.println("PARAM: " + class1);
			}
		}
		return methodList;
	}

	public static void main(String[] args) throws ClassNotFoundException {
		String interfaceT = "org.jboss.forge.spec.javaee.ejb.TestEjb";
		List<String> metodi = getMethods(interfaceT);
		for (String string : metodi) {
			System.out.println(string);
		}
	}

}
