/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.ejb;

import java.io.FileNotFoundException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaMethodResource;
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
import org.jboss.forge.spec.javaee.ejb.api.EjbType;
import org.jboss.forge.spec.javaee.ejb.api.JmsDestinationType;
import org.jboss.forge.spec.javaee.ejb.api.TransactionAttributeType;

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

   @Inject
   private Event<PickupResource> pickup;

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
            @Option(required = true, name = "packageAndName", description = "The ejb name with package: i.e. by.giava.service.Flower") final JavaResource resource,
            @Option(required = false, name = "type", defaultValue = "stateless") EjbType type,
            @Option(required = false, name = "overwrite") final boolean overwrite)
            throws FileNotFoundException
   {
      JavaClass ejb = null;
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      if (!resource.exists() || overwrite)
      {
         if (resource.createNewFile())
         {
            JavaClass javaClass = JavaParser.create(JavaClass.class);
            javaClass.setName(java.calculateName(resource));
            javaClass.setPackage(java.calculatePackage(resource));
            ejb = javaClass;
         }
      }
      else if (overwrite)
      {
         ejb = getJavaClassFrom(resource);
      }
      else
      {
         throw new RuntimeException("PackageAndName already exists ["
                  + resource.getFullyQualifiedName()
                  + "] Re-run with '--overwrite' to continue.");
      }
      if (type == null)
      {
         type = EjbType.stateless;
      }
      if (EjbType.messagedriven.equals(type))
      {
         String destinationType = shell.promptCommon(
                  "Destination type: javax.jms.Queue or javax.jms.Topic:",
                  PromptType.JAVA_CLASS,
                  JmsDestinationType.queue.getDestinationType());
         String destinationName = shell.promptCommon("Destination Name:",
                  PromptType.ANY, "queue/test");
         String name = ejb.getName();
         ejb.addImport(ActivationConfigProperty.class);
         ejb.addImport(MessageDriven.class);
         ejb.addImport(Message.class);
         ejb.addInterface(MessageListener.class);
         ejb.addMethod("public void onMessage(Message message) {}");
         ejb.addAnnotation(EjbType.messagedriven.getAnnotation())
                  // .setLiteralValue("name", "testName");
                  .setLiteralValue("name", "\"" + name + "\"")
                  .setLiteralValue(
                           "activationConfig",
                           "{@ActivationConfigProperty(propertyName = \"destinationType\", propertyValue = \""
                                    + destinationType
                                    + "\"), "
                                    + "@ActivationConfigProperty(propertyName = \"destination\", propertyValue = \""
                                    + destinationName + "\")" + "}");

      }
      else
      {
         ejb.addAnnotation(type.getAnnotation());
         ejb.addAnnotation("javax.ejb.LocalBean");
      }
      save(ejb);
      pickup.fire(new PickupResource(resource));

   }

   /*
    * add @TransactionAttribute(TransactionAttributeType.MANDATORY|REQUIRED| REQUIRES_NEW|SUPPORTS|NOT_SUPPORTED|NEVER)
    */

   @Command("add-transactionAttribute")
   @RequiresResource({ JavaResource.class, JavaMethodResource.class })
   public void addTransactionAttribute(
            @Option(required = true, name = "type") final TransactionAttributeType transactionAttributeType,
            final PipeOut out) throws FileNotFoundException
   {
      JavaSource<?> ejb;
      if (resource instanceof JavaResource)
      {

         ejb = getJavaClassFrom(resource);
         if (ejb.hasAnnotation(transactionAttributeType.getAnnotation()))
         {
            throw new RuntimeException(
                     "Current class have already TransactionAttribute annotation!");
         }
         ejb.addAnnotation(TransactionAttribute.class).setStringValue(
                  transactionAttributeType.getAnnotation());
         save(ejb);
      }
      else if (resource instanceof JavaMethodResource)
      {
         Method<? extends JavaSource<?>> m = ((JavaMethodResource) resource)
                  .getUnderlyingResourceObject();
         if (m.hasAnnotation(transactionAttributeType.getAnnotation()))
         {
            throw new RuntimeException(
                     "Current method have already TransactionAttribute annotation!");
         }
         m.addAnnotation(TransactionAttribute.class).setStringValue(
                  transactionAttributeType.getAnnotation());
         ejb = m.getOrigin();
         save(ejb);
      }
      else
      {
         throw new RuntimeException(
                  "Impossibile to add transactionAttribute to method "
                           + resource.getName());
      }
   }

   public static JavaClass getJavaClassFrom(Resource<?> resource)
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

}
