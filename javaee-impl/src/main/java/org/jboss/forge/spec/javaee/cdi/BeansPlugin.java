/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee.cdi;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.SyntaxError;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.PickupResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.util.Lists;
import org.jboss.forge.spec.javaee.CDIFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Kevin Pollet
 */
@Alias("beans")
@RequiresFacet(CDIFacet.class)
public class BeansPlugin implements Plugin
{
   private static final Dependency JAVAEE6_DEPENDENCY = DependencyBuilder.create("org.jboss.spec:jboss-javaee-6.0");
   private static final Dependency JAVAEE6_CDI = DependencyBuilder.create("javax.enterprise:cdi-api");

   @Inject
   private Event<InstallFacets> install;

   @Inject
   private Event<PickupResource> pickup;

   @Inject
   private Project project;

   @Inject
   private ShellPrompt prompt;

   @Inject
   Shell shell;

   @Inject
   @Current
   private JavaResource resource;

   DependencyFacet dependencyFacet;

   @SetupCommand
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(CDIFacet.class))
      {
         dependencyFacet = project.getFacet(DependencyFacet.class);

         install.fire(new InstallFacets(CDIFacet.class));
         installDependencies();
      }

   }

   private void installDependencies()
   {
      if (!dependencyFacet.hasDependency(JAVAEE6_CDI)
               && !dependencyFacet.hasDependency(JAVAEE6_DEPENDENCY)
               && prompt.promptBoolean("Do you want to add CDI dependencies?", false))
      {
         Dependency dependency = prompt.promptChoiceTyped("Which dependency do you want to install?",
                  Arrays.asList(JAVAEE6_DEPENDENCY, JAVAEE6_CDI));

         List<Dependency> versions = dependencyFacet.resolveAvailableVersions(dependency);
         dependency = prompt.promptChoiceTyped("Install which version?", versions, Lists.lastElement(versions));

         if (prompt.promptBoolean("Should the scope be 'provided'?", project.hasFacet(WebResourceFacet.class)))
         {
            dependency = DependencyBuilder.create(dependency).setScopeType(ScopeType.PROVIDED);
         }
         dependencyFacet.addDependency(dependency);
         ShellMessages.info(shell, "Added " + dependency.toString());
      }
   }

   @Command("list-interceptors")
   public void listInterceptors(final PipeOut out)
   {
      CDIFacet cdi = project.getFacet(CDIFacet.class);
      List<String> interceptors = cdi.getConfig().getInterceptors();
      for (String i : interceptors)
      {
         out.println(i);
      }
   }

   @Command("list-decorators")
   public void listDecorators(final PipeOut out)
   {
      CDIFacet cdi = project.getFacet(CDIFacet.class);
      List<String> decorators = cdi.getConfig().getDecorators();
      for (String d : decorators)
      {
         out.println(d);
      }
   }

   @Command("list-alternatives")
   public void listAlternatives(final PipeOut out)
   {
      CDIFacet cdi = project.getFacet(CDIFacet.class);
      List<String> classes = cdi.getConfig().getAlternativeClasses();
      List<String> stereotypes = cdi.getConfig().getAlternativeStereotypes();

      if (!out.isPiped())
         out.println(ShellColor.BOLD, "Stereotypes:");

      for (String s : stereotypes)
      {
         out.println(s);
      }

      if (!out.isPiped())
         out.println(ShellColor.BOLD, "Classes:");

      for (String c : classes)
      {
         out.println(c);
      }
   }

   @Command("new-conversation")
   @RequiresResource(JavaResource.class)
   public void newConversation(
            @Option(name = "timeout") final Long timeout,
            @Option(name = "named", defaultValue = "") final String name,
            @Option(name = "beginMethodName", defaultValue = "beginConversation") final String beginName,
            @Option(name = "endMethodName", defaultValue = "endConversation") final String endName,
            @Option(name = "conversationFieldName", defaultValue = "conversation") final String fieldName,
            @Option(name = "overwrite") boolean overwrite,
            final PipeOut out) throws FileNotFoundException
   {
      if (resource.exists())
      {
         if (resource.getJavaSource().isClass())
         {
            JavaClass javaClass = (JavaClass) resource.getJavaSource();

            if (javaClass.hasField(fieldName) && !javaClass.getField(fieldName).isType(Conversation.class))
            {
               if (overwrite)
               {
                  javaClass.removeField(javaClass.getField(fieldName));
               }
               else
               {
                  throw new RuntimeException("Field [" + fieldName + "] exists. Re-run with '--overwrite' to continue.");
               }
            }
            if (javaClass.hasMethodSignature(beginName) && javaClass.getMethod(beginName).getParameters().size() == 0)
            {
               if (overwrite)
               {
                  javaClass.removeMethod(javaClass.getMethod(beginName));
               }
               else
               {
                  throw new RuntimeException("Method [" + beginName
                           + "] exists. Re-run with '--overwrite' to continue.");
               }
            }
            if (javaClass.hasMethodSignature(endName) && javaClass.getMethod(endName).getParameters().size() == 0)
            {
               if (overwrite)
               {
                  javaClass.removeMethod(javaClass.getMethod(endName));
               }
               else
               {
                  throw new RuntimeException("Method [" + endName + "] exists. Re-run with '--overwrite' to continue.");
               }
            }

            javaClass.addField().setPrivate().setName(fieldName).setType(Conversation.class)
                     .addAnnotation(Inject.class);

            Method<JavaClass> beginMethod = javaClass.addMethod().setName(beginName).setReturnTypeVoid().setPublic()
                     .setBody(fieldName + ".begin(" + name + ");");

            if (timeout != null)
            {
               beginMethod.setBody(beginMethod.getBody() + "\n" + fieldName + ".setTimeout(" + timeout + ");");
            }

            javaClass.addMethod().setName(endName).setReturnTypeVoid().setPublic()
                     .setBody(fieldName + ".end();");

            if (javaClass.hasSyntaxErrors())
            {
               ShellMessages.info(out, "Modified Java class contains syntax errors:");
               for (SyntaxError error : javaClass.getSyntaxErrors())
               {
                  out.print(error.getDescription());
               }
            }

            resource.setContents(javaClass);
         }
         else
         {
            ShellMessages.error(out, "Must operate on a Java Class file, not an ["
                     + resource.getJavaSource().getSourceType() + "]");
         }
      }
   }

   @Command("new-bean")
   public void newBean(
            @Option(required = true,
                     name = "type") final JavaResource resource,
            @Option(required = true, name = "scoped") final BeanScope scope,
            @Option(required = false, name = "overwrite") final boolean overwrite
            ) throws FileNotFoundException
   {
      if (!resource.exists() || overwrite)
      {
         JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
         if (resource.createNewFile())
         {
            JavaClass javaClass = JavaParser.create(JavaClass.class);
            javaClass.setName(java.calculateName(resource));
            javaClass.setPackage(java.calculatePackage(resource));

            if (BeanScope.CUSTOM.equals(scope))
            {
               String annoType = prompt.promptCommon("Enter the qualified custom scope type:", PromptType.JAVA_CLASS);
               javaClass.addAnnotation(annoType);
            }
            else if (!BeanScope.DEPENDENT.equals(scope))
            {
               javaClass.addAnnotation(scope.getAnnotation());
            }
            resource.setContents(javaClass);
            pickup.fire(new PickupResource(resource));
         }
      }
      else
      {
         throw new RuntimeException("Type already exists [" + resource.getFullyQualifiedName()
                  + "] Re-run with '--overwrite' to continue.");
      }
   }
}
