/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.cdi;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.FileNotFoundException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.NormalScope;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.inject.Scope;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaAnnotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.SyntaxError;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
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
import org.jboss.forge.spec.javaee.CDIFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author Kevin Pollet
 */
@Alias("beans")
@RequiresFacet(CDIFacet.class)
public class BeansPlugin implements Plugin
{
   private static final Set<ElementType> STEREOTYPE_TARGETS;
   static
   {
      Set<ElementType> stereotypeTargets = new LinkedHashSet<ElementType>();
      Collections.addAll(stereotypeTargets, TYPE, METHOD, FIELD);
      STEREOTYPE_TARGETS = Collections.unmodifiableSet(stereotypeTargets);
   }

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

   @SetupCommand
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(CDIFacet.class))
      {
         install.fire(new InstallFacets(CDIFacet.class));
      }

      // TODO enable java SE support

      if (project.hasFacet(CDIFacet.class))
      {
         ShellMessages.success(out, "Beans (CDI) is installed.");
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
            @Option(name = "overwrite") final boolean overwrite,
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
            if (javaClass.hasMethodSignature(beginName) && (javaClass.getMethod(beginName).getParameters().size() == 0))
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
            if (javaClass.hasMethodSignature(endName) && (javaClass.getMethod(endName).getParameters().size() == 0))
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
      if (resource.exists() && !overwrite)
      {
         throw new RuntimeException("Type already exists [" + resource.getFullyQualifiedName()
                  + "] Re-run with '--overwrite' to continue.");
      }
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass javaClass = JavaParser.create(JavaClass.class);
      javaClass.setName(java.calculateName(resource));
      javaClass.setPackage(java.calculatePackage(resource));

      if (BeanScope.CUSTOM == scope)
      {
         String annoType = prompt.promptCommon("Enter the qualified custom scope type:", PromptType.JAVA_CLASS);
         javaClass.addAnnotation(annoType);
      }
      else if (BeanScope.DEPENDENT != scope)
      {
         javaClass.addAnnotation(scope.getAnnotation());
      }
      java.saveJavaSource(javaClass);
      pickup.fire(new PickupResource(java.getJavaResource(javaClass)));
   }

   @Command("new-qualifier")
   public void newQualifier(
            @Option(required = true,
                     name = "type") final JavaResource resource,
            @Option(required = false, name = "overwrite") final boolean overwrite,
            @Option(required = false, name = "inherited") final boolean inherited
            ) throws FileNotFoundException
   {
      if (resource.exists() && !overwrite)
      {
         throw new RuntimeException("Type already exists [" + resource.getFullyQualifiedName()
                  + "] Re-run with '--overwrite' to continue.");
      }

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaAnnotation qualifier = JavaParser.create(JavaAnnotation.class);
      qualifier.setName(java.calculateName(resource));
      qualifier.setPackage(java.calculatePackage(resource));
      qualifier.addAnnotation(Qualifier.class);
      if (inherited)
      {
         qualifier.addAnnotation(Inherited.class);
      }
      qualifier.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      qualifier.addAnnotation(Target.class).setEnumValue(METHOD, FIELD, PARAMETER, TYPE);
      qualifier.addAnnotation(Documented.class);

      java.saveJavaSource(qualifier);
      pickup.fire(new PickupResource(java.getJavaResource(qualifier)));
   }

   @Command("new-stereotype")
   public void newStereotype(
            @Option(required = true,
                     name = "type") final JavaResource resource,
            @Option(required = false, name = "overwrite") final boolean overwrite,
            @Option(required = false, name = "inherited") final boolean inherited,
            @Option(required = false, name = "named") final boolean named,
            @Option(required = false, name = "alternative") final boolean alternative,
            @Option(required = false,
                     help = "allow this stereotype to be used on any valid @Target element type (TYPE, METHOD, FIELD)",
                     description = "all @Target element types",
                     name = "all-targets") final boolean allTargets
            ) throws FileNotFoundException
   {
      if (resource.exists() && !overwrite)
      {
         throw new RuntimeException("Type already exists [" + resource.getFullyQualifiedName()
                  + "] Re-run with '--overwrite' to continue.");
      }
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaAnnotation stereotype = JavaParser.create(JavaAnnotation.class);
      stereotype.setName(java.calculateName(resource));
      stereotype.setPackage(java.calculatePackage(resource));
      stereotype.addAnnotation(Stereotype.class);
      if (inherited)
      {
         stereotype.addAnnotation(Inherited.class);
      }
      if (named)
      {
         stereotype.addAnnotation(Named.class);
      }
      if (alternative)
      {
         stereotype.addAnnotation(Alternative.class);
      }
      stereotype.addAnnotation(Retention.class).setEnumValue(RUNTIME);

      final Set<ElementType> targetTypes;
      if (allTargets)
      {
         targetTypes = STEREOTYPE_TARGETS;
      }
      else
      {
         Set<ElementType> input;
         while (true)
         {
            input = shell.promptMultiSelect("Select target element types", STEREOTYPE_TARGETS);
            if (input.isEmpty())
            {
               ShellMessages.error(shell, "No target element types selected");
               continue;
            }
            if (input.contains(TYPE) && input.size() == 2)
            {
               ShellMessages.error(shell, "Invalid combination of target element types: " + input);
               continue;
            }
            break;
         }
         targetTypes = input;
      }

      stereotype.addAnnotation(Target.class).setEnumValue(targetTypes.toArray(new ElementType[targetTypes.size()]));
      stereotype.addAnnotation(Documented.class);

      java.saveJavaSource(stereotype);
      pickup.fire(new PickupResource(java.getJavaResource(stereotype)));
   }

   @Command("new-scope")
   public void newScope(
            @Option(required = true,
                     name = "type") final JavaResource resource,
            @Option(required = false, name = "overwrite") final boolean overwrite,
            @Option(required = false, name = "pseudo", help = "mutually exclusive with 'passivating'") final boolean pseudo,
            @Option(required = false, name = "passivating", help = "mutually exclusive with 'pseudo'") final boolean passivating
            ) throws FileNotFoundException
   {
      if (pseudo && passivating)
      {
         throw new RuntimeException("Cannot create a passivating pseudo-scope!");
      }

      if (resource.exists() && !overwrite)
      {
         throw new RuntimeException("Type already exists [" + resource.getFullyQualifiedName()
                  + "] Re-run with '--overwrite' to continue.");
      }

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaAnnotation scope = JavaParser.create(JavaAnnotation.class);
      scope.setName(java.calculateName(resource));
      scope.setPackage(java.calculatePackage(resource));

      if (pseudo)
      {
         scope.addAnnotation(Scope.class);
      }
      else
      {
         Annotation<JavaAnnotation> normalScope = scope.addAnnotation(NormalScope.class);
         if (passivating)
         {
            normalScope.setLiteralValue("passivating", Boolean.toString(true));
         }
      }
      scope.addAnnotation(Retention.class).setEnumValue(RUNTIME);
      scope.addAnnotation(Target.class).setEnumValue(TYPE, METHOD, FIELD);
      scope.addAnnotation(Documented.class);

      java.saveJavaSource(scope);
      pickup.fire(new PickupResource(java.getJavaResource(scope)));
   }

}
