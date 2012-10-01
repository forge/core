/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.ejb;

import java.io.FileNotFoundException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaMethodResource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.ejb.api.EJBType;
import org.jboss.forge.spec.javaee.ejb.api.JMSDestinationType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("ejb")
@RequiresProject
public class EJBPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   @Current
   private Resource<?> resource;

   @Inject
   private Shell shell;

   @Inject
   private Event<InstallFacets> request;

   @SetupCommand
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(EJBFacet.class))
      {
         request.fire(new InstallFacets(EJBFacet.class));
      }
      if (project.hasFacet(EJBFacet.class))
      {
         ShellMessages.success(out,
                  "Enterprise Java Beans (EJB) is installed.");
      }
   }

   /*
    * default: create EJB STATELESS WITH LOCALBEAN ANNOTATION
    */
   @Command("new-ejb")
   public void newEjb(
            @Option(required = false,
                     help = "the package in which to build this Class",
                     description = "source package",
                     type = PromptType.JAVA_PACKAGE,
                     name = "package") final String packageName,
            @Option(required = true, name = "named", help = "the class name of ejb") String name,
            @Option(required = false, name = "type", defaultValue = "STATELESS") EJBType type)
            throws FileNotFoundException
   {
      JavaClass ejb = null;
      String ejbPackage;
      if ((packageName != null) && !"".equals(packageName))
      {
         ejbPackage = packageName;
      }
      else if (getPackagePortionOfCurrentDirectory() != null)
      {
         ejbPackage = getPackagePortionOfCurrentDirectory();
      }
      else
      {
         ejbPackage = shell.promptCommon(
                  "In which package you'd like to create this @Entity, or enter for default",
                  PromptType.JAVA_PACKAGE, getEntityPackage());
      }
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource(ejbPackage + "." + name);
      if (!resource.exists())
      {
         if (resource.createNewFile())
         {
            JavaClass javaClass = JavaParser.create(JavaClass.class);
            javaClass.setName(name);
            javaClass.setPackage(ejbPackage);
            ejb = javaClass;
         }
      }
      else
      {
         boolean overwrite = shell.promptBoolean("The EJB class already exists, do you want to overwrite it ?");
         if (overwrite)
         {
            ejb = getJavaClassFrom(resource);
         }
         else
         {
            throw new RuntimeException("The ejb class already exists ["
                     + resource.getFullyQualifiedName()
                     + "] ");
         }

      }
      if (type == null)
      {
         type = EJBType.STATELESS;
      }
      if (EJBType.MESSAGEDRIVEN != type)
      {
         ejb.addAnnotation(type.getAnnotation());
         ejb.addAnnotation("javax.ejb.LocalBean");
      }
      else
      {
         String destinationType = shell.promptCommon(
                  "Destination type: javax.jms.Queue or javax.jms.Topic:",
                  PromptType.JAVA_CLASS,
                  JMSDestinationType.QUEUE.getDestinationType());
         String destinationName = shell.promptCommon("Destination Name:",
                  PromptType.ANY, "queue/test");
         ejb.addImport(ActivationConfigProperty.class);
         ejb.addImport(Message.class);
         ejb.addInterface(MessageListener.class);
         ejb.addMethod("public void onMessage(Message message) {}");
         ejb.addAnnotation(EJBType.MESSAGEDRIVEN.getAnnotation())
                  .setLiteralValue("name", "\"" + name + "\"")
                  .setLiteralValue(
                           "activationConfig",
                           "{@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \""
                                    + destinationType
                                    + "\"), "
                                    + "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \""
                                    + destinationName + "\")" + "}");

      }
      save(ejb);
      shell.setCurrentResource(resource);

   }

   /*
    * add @TransactionAttribute(TransactionAttributeType.MANDATORY|REQUIRED| REQUIRES_NEW|SUPPORTS|NOT_SUPPORTED|NEVER)
    */

   @Command("add-transaction-attribute")
   @RequiresResource({ JavaResource.class, JavaMethodResource.class })
   public void addTransactionAttribute(
            @Option(required = true, name = "type") final TransactionAttributeType transactionAttributeType,
            final PipeOut out) throws FileNotFoundException
   {
      JavaSource<?> ejb;
      Annotation<?> annotation;
      if (resource instanceof JavaResource)
      {

         ejb = getJavaClassFrom(resource);

         if (ejb.hasAnnotation(TransactionAttribute.class))
         {
            annotation = ejb.getAnnotation(TransactionAttribute.class);
         }
         else
         {
            annotation = ejb.addAnnotation(TransactionAttribute.class);
         }
         annotation.setEnumValue(transactionAttributeType);
      }
      else if (resource instanceof JavaMethodResource)
      {
         Method<? extends JavaSource<?>> m = ((JavaMethodResource) resource)
                  .getUnderlyingResourceObject();
         if (m.hasAnnotation(TransactionAttribute.class))
         {
            annotation = m.getAnnotation(TransactionAttribute.class);
         }
         else
         {
            annotation = m.addAnnotation(TransactionAttribute.class);
         }
         annotation.setEnumValue(transactionAttributeType);
         ejb = m.getOrigin();
      }
      else
      {
         throw new RuntimeException(
                  "Impossibile to add transactionAttribute to "
                           + resource.getName());
      }
      save(ejb);
   }

   private JavaClass getJavaClassFrom(Resource<?> resource)
            throws FileNotFoundException
   {
      JavaSource<?> source = ((JavaResource) resource).getJavaSource();
      if (!source.isClass())
      {
         throw new IllegalStateException(
                  "Current resource is not a JavaClass!");
      }
      return (JavaClass) source;
   }

   private void save(JavaSource<?> javaSource) throws FileNotFoundException
   {
      JavaSourceFacet javaSourceFacet = project
               .getFacet(JavaSourceFacet.class);
      javaSourceFacet.saveJavaSource(javaSource);
   }

   /**
    * Retrieves the package portion of the current directory if it is a package, null otherwise.
    * 
    * @return String representation of the current package, or null
    */
   private String getPackagePortionOfCurrentDirectory()
   {
      for (DirectoryResource r : project.getFacet(JavaSourceFacet.class).getSourceFolders())
      {
         final DirectoryResource currentDirectory = shell.getCurrentDirectory();
         if (ResourceUtil.isChildOf(r, currentDirectory))
         {
            return currentDirectory.getFullyQualifiedName().replace(r.getFullyQualifiedName() + "/", "")
                     .replaceAll("/", ".");
         }
      }
      return null;
   }

   private String getEntityPackage()
   {
      JavaSourceFacet sourceFacet = project.getFacet(JavaSourceFacet.class);
      return sourceFacet.getBasePackage();
   }

}
