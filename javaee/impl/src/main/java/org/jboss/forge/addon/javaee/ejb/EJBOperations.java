/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ejb;

import java.io.FileNotFoundException;
import java.io.Serializable;

import javax.ejb.ActivationConfigProperty;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.persistence.GenerationType;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.furnace.util.Assert;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * This class contains EJB specific operations
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class EJBOperations
{
   /**
    * Creates a new {@link JavaResource} in the specified project. If no project is available, use
    * {@link EJBOperations#newEntity(DirectoryResource, String, String, GenerationType)}
    *
    * @param project the current project in which to create the bean. Must not be null
    * @param ejbName the name of the bean
    * @param targetPackage the package of the bean to be created
    * @param ejbType the {@link EJBType} chosen for this bean
    * @param serializable whether or not the EJB should be serializable
    * @param destType JMS destination type
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEJB(
            final Project project,
            final String ejbName,
            final String targetPackage,
            final EJBType ejbType,
            final boolean serializable
            ) throws FileNotFoundException
   {
      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClassSource javaClass = createJavaClass(ejbName, targetPackage, ejbType, serializable);
      return java.saveJavaSource(javaClass);
   }

   /**
    * Creates a new {@link JavaResource} in the specified target. If a project is available, use
    * {@link EJBOperations#newEntity(Project, String, String, GenerationType)}
    *
    * @param target the target directory resource to create this class
    * @param ejbName the name of the class
    * @param ejbPackage the package of the class to be created
    * @param ejbType the ID strategy chosen for this class
    * @param serializable whether or not the EJB should be serializable
    * @param destType JMS destination type
    * @return the created java resource
    * @throws FileNotFoundException if something wrong happens while saving the {@link JavaClass}
    */
   public JavaResource newEJB(DirectoryResource target,
            final String ejbName,
            final String ejbPackage,
            final EJBType ejbType,
            final boolean serializable
            )
   {
      JavaClassSource javaClass = createJavaClass(ejbName, ejbPackage, ejbType, serializable);
      JavaResource javaResource = getJavaResource(target, javaClass.getName());
      javaResource.setContents(javaClass);
      return javaResource;
   }

   private JavaClassSource createJavaClass(
            final String className,
            final String classPackage,
            final EJBType type,
            boolean serializable)
   {
      JavaClassSource ejb = Roaster.create(JavaClassSource.class)
               .setName(className)
               .setPublic()
               .getOrigin();

      ejb.addAnnotation(type.getAnnotation());
      if (EJBType.MESSAGEDRIVEN != type)
      {
         ejb.addAnnotation("javax.ejb.LocalBean");
      }

      if (serializable)
      {
         ejb.addInterface(Serializable.class);
         ejb.addField("private static final long serialVersionUID = -1L;");
      }

      if (classPackage != null && !classPackage.isEmpty())
      {
         ejb.setPackage(classPackage);
      }
      return ejb;
   }

   public JavaClassSource setupMessageDrivenBean(JavaClassSource ejb, JMSDestinationType destType, String destName)
   {
      Assert.notNull(destType, "JMS Destination type must not be null when bean is Message Driven");
      Assert.notNull(destName, "JMS Destination name must not be null when bean is Message Driven");

      ejb.addImport(ActivationConfigProperty.class);
      ejb.addImport(Message.class);
      ejb.addInterface(MessageListener.class);
      ejb.addMethod("public void onMessage(Message message) {}");

      AnnotationSource<JavaClassSource> annotation = ejb.getAnnotation(EJBType.MESSAGEDRIVEN.getAnnotation());
      if (annotation == null)
      {
         annotation = ejb.addAnnotation(EJBType.MESSAGEDRIVEN.getAnnotation());
      }

      annotation.setLiteralValue("name", "\"" + ejb.getName() + "\"")
               .setLiteralValue(
                        "activationConfig",
                        "{@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \""
                                 + destType.getDestinationType()
                                 + "\"), "
                                 + "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \""
                                 + destName + "\")" + "}");

      return ejb;
   }

   private JavaResource getJavaResource(final DirectoryResource sourceDir, final String relativePath)
   {
      String path = relativePath.trim().endsWith(".java")
               ? relativePath.substring(0, relativePath.lastIndexOf(".java")) : relativePath;
      path = path.replace(".", "/") + ".java";
      JavaResource target = sourceDir.getChildOfType(JavaResource.class, path);
      return target;
   }
}