/*
 * Copyright 2012-2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.dev.java;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.Import;
import org.jboss.forge.parser.java.JavaAnnotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.parser.java.SyntaxError;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.PickupResource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.util.JavaColorizer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@Alias("java")
@RequiresFacet(JavaSourceFacet.class)
public class JavaPlugin implements Plugin
{
   @Inject
   @Current
   private JavaResource resource;

   @Inject
   private Project project;

   @Inject
   private ShellPrompt prompt;

   @Inject
   private ShellPrintWriter writer;

   @Inject
   private Event<PickupResource> pickUp;

   @Inject
   private Shell shell;

   @DefaultCommand(help = "Prints all Java system property information.")
   public void info(final PipeOut out)
   {
      for (Entry<Object, Object> entry : System.getProperties().entrySet())
      {
         if (entry.getKey().toString().startsWith("java"))
         {
            out.print(ShellColor.BOLD, entry.getKey().toString() + ": ");
            out.println(entry.getValue().toString());
         }
      }
   }

   @Command("new-class")
   public void newClass(
            @PipeIn final InputStream in,
            @Option(required = false,
                     help = "the package in which to build this Class",
                     description = "source package",
                     type = PromptType.JAVA_PACKAGE,
                     name = "package") final String pckg,
            @Option(name = "named",
                     required = false,
                     description = "The class name",
                     type = PromptType.JAVA_CLASS) final String className,
            @Option(required = false,
                     help = "the class definition: surround with quotes",
                     description = "class definition") final String... def) throws FileNotFoundException
   {

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      JavaClass jc = null;
      if (def != null)
      {
         String classDef = Strings.join(Arrays.asList(def), " ");
         jc = JavaParser.parse(JavaClass.class, classDef);
      }
      else if (in != null)
      {
         jc = JavaParser.parse(JavaClass.class, in);
      }
      else if (className != null)
      {
         jc = JavaParser.create(JavaClass.class).setName(className);
      }
      else
      {
         throw new RuntimeException("arguments required");
      }

      if (pckg != null)
      {
         jc.setPackage(pckg);
      }

      if (!jc.hasSyntaxErrors())
      {
         java.saveJavaSource(jc);
      }
      else
      {
         writer.println(ShellColor.RED, "Syntax Errors:");
         for (SyntaxError error : jc.getSyntaxErrors())
         {
            writer.println(error.toString());
         }
         writer.println();

         if (prompt.promptBoolean("Your class has syntax errors, create anyway?", true))
         {
            java.saveJavaSource(jc);
         }
         else
         {
            return;
         }
      }
      pickUp.fire(new PickupResource(java.getJavaResource(jc)));
   }

   @Command("new-interface")
   public void newInterface(
            @PipeIn final InputStream in,
            @Option(name = "named", required = false, description = "The interface name", type = PromptType.JAVA_CLASS) final String interfaceName,
            @Option(required = false, help = "the package in which to build this Interface", description = "source package", type = PromptType.JAVA_PACKAGE, name = "package") final String pckg,
            @Option(required = false, help = "the interface definition: surround with quotes", description = "interface definition") final String... def)
            throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaInterface jc = null;
      if (def != null)
      {
         String classDef = Strings.join(Arrays.asList(def), " ");
         jc = JavaParser.parse(JavaInterface.class, classDef);
      }
      else if (in != null)
      {
         jc = JavaParser.parse(JavaInterface.class, in);
      }
      else if (interfaceName != null)
      {
         jc = JavaParser.create(JavaInterface.class).setName(interfaceName);
      }
      else
      {
         throw new RuntimeException("arguments required");
      }

      if (pckg != null)
      {
         jc.setPackage(pckg);
      }
      if (!jc.hasSyntaxErrors())
      {
         java.saveJavaSource(jc);
      }
      else
      {
         writer.println(ShellColor.RED, "Syntax Errors:");
         for (SyntaxError error : jc.getSyntaxErrors())
         {
            writer.println(error.toString());
         }
         writer.println();
         if (prompt.promptBoolean(
                  "Your class has syntax errors, create anyway?", true))
         {
            java.saveJavaSource(jc);
         }
         else
         {
            return;
         }
      }
      pickUp.fire(new PickupResource(java.getJavaResource(jc)));
   }

   @Command("new-enum-type")
   public void newEnumType(
            @PipeIn final InputStream in,
            @Option(required = false,
                     help = "the package in which to build this Class",
                     description = "source package",
                     type = PromptType.JAVA_PACKAGE,
                     name = "package") final String pckg,
            @Option(name = "named",
                     required = false,
                     description = "The enum name",
                     type = PromptType.JAVA_CLASS) final String enumName,

            @Option(required = false,
                     help = "the class definition: surround with quotes",
                     description = "class definition") final String... def) throws FileNotFoundException
   {

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      JavaEnum je = null;
      if (def != null)
      {
         String classDef = Strings.join(Arrays.asList(def), " ");
         je = JavaParser.parse(JavaEnum.class, classDef);
      }
      else if (in != null)
      {
         je = JavaParser.parse(JavaEnum.class, in);
      }
      else if (enumName != null)
      {
         je = JavaParser.create(JavaEnum.class).setName(enumName);
      }
      else
      {
         throw new RuntimeException("arguments required");
      }

      if (pckg != null)
      {
         je.setPackage(pckg);
      }

      if (!je.hasSyntaxErrors())
      {
         java.saveJavaSource(je);
      }
      else
      {
         writer.println(ShellColor.RED, "Syntax Errors:");
         for (SyntaxError error : je.getSyntaxErrors())
         {
            writer.println(error.toString());
         }
         writer.println();

         if (prompt.promptBoolean("Your enum has syntax errors, create anyway?", true))
         {
            java.saveJavaSource(je);
         }
         else
         {
            return;
         }
      }

      pickUp.fire(new PickupResource(java.getJavaResource(je)));
   }

   @Command("new-annotation-type")
   public void newAnnotationType(
            @PipeIn final InputStream in,
            @Option(required = false,
                     help = "the package in which to build this Class",
                     description = "source package",
                     type = PromptType.JAVA_PACKAGE,
                     name = "package") final String pckg,
            @Option(name = "named",
                     required = false,
                     description = "The annotation name",
                     type = PromptType.JAVA_CLASS) final String annotationName,
            @Option(required = false,
                     help = "the @Retention policy for this annotation",
                     description = "retention policy",
                     name = "retention-policy") final RetentionPolicy retentionPolicy,
            @Option(required = false,
                     help = "whether the annotation is @Documented",
                     description = "documented",
                     name = "documented") final boolean documented,
            @Option(required = false,
                     help = "whether to omit the @Target annotation (if omitted the user will be prompted for the target types)",
                     description = "omit @Target",
                     name = "no-target") final boolean noTarget,
            @Option(required = false,
                     help = "the annotation definition: surround with quotes",
                     description = "annotation definition") final String... def) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      JavaAnnotation type = null;
      if (def != null)
      {
         String classDef = Strings.join(Arrays.asList(def), " ");
         type = JavaParser.parse(JavaAnnotation.class, classDef);
      }
      else if (in != null)
      {
         type = JavaParser.parse(JavaAnnotation.class, in);
      }
      else if (annotationName != null)
      {
         type = JavaParser.create(JavaAnnotation.class).setName(annotationName);
      }
      else
      {
         throw new RuntimeException("arguments required");
      }

      if (pckg != null)
      {
         type.setPackage(pckg);
      }

      if (documented)
      {
         type.addAnnotation(Documented.class);
      }
      if (retentionPolicy != null)
      {
         type.addAnnotation(Retention.class).setEnumValue(retentionPolicy);
      }
      final Set<ElementType> targetTypes;
      if (noTarget)
      {
         targetTypes = Collections.emptySet();
      }
      else
      {
         targetTypes = shell.promptMultiSelectWithWildcard("*",
                  "Select target element types", ElementType.values());
      }
      if (targetTypes.isEmpty())
      {
         shell.printlnVerbose("Skipping @Target annotation");
      }
      else
      {
         type.addAnnotation(Target.class).setEnumValue(targetTypes.toArray(new ElementType[targetTypes.size()]));
      }

      if (!type.hasSyntaxErrors())
      {
         java.saveJavaSource(type);
      }
      else
      {
         writer.println(ShellColor.RED, "Syntax Errors:");
         for (SyntaxError error : type.getSyntaxErrors())
         {
            writer.println(error.toString());
         }
         writer.println();

         if (prompt.promptBoolean("Your annotation has syntax errors, create anyway?", true))
         {
            java.saveJavaSource(type);
         }
         else
         {
            return;
         }
      }

      pickUp.fire(new PickupResource(java.getJavaResource(type)));
   }

   @Command("new-enum-const")
   @RequiresResource(JavaResource.class)
   public void newEnumConst(
            @PipeIn final String in,
            final PipeOut out,
            @Option(required = false,
                     help = "the enum field definition",
                     description = "enum field definition") final String... def) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String enumConstDef = null;
      if (def != null)
      {
         enumConstDef = Strings.join(Arrays.asList(def), " ");
      }
      else if (in != null)
      {
         enumConstDef = in;
      }
      else
      {
         throw new RuntimeException("arguments required");
      }

      JavaEnum source = (JavaEnum) resource.getJavaSource();
      source.addEnumConstant(enumConstDef);
      java.saveJavaSource(source);

   }

   @Command("list-imports")
   @RequiresResource(JavaResource.class)
   public void listImports(
            final PipeOut out) throws FileNotFoundException
   {
      List<Import> imports = resource.getJavaSource().getImports();
      for (Import i : imports)
      {
         String str = "import " + (i.isStatic() ? "static " : "") + i.getQualifiedName() + ";";
         str = JavaColorizer.format(out, str);
         out.println(str);
      }
   }

   @Command("new-field")
   @RequiresResource(JavaResource.class)
   public void newField(
            @PipeIn final String in,
            final PipeOut out,
            @Option(required = false,
                     help = "the field definition: surround with single quotes",
                     description = "field definition") final String... def) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String fieldDef = null;
      if (def != null)
      {
         fieldDef = Strings.join(Arrays.asList(def), " ");
      }
      else if (in != null)
      {
         fieldDef = in;
      }
      else
      {
         throw new RuntimeException("arguments required");
      }

      JavaSource<?> source = resource.getJavaSource();
      if (source instanceof FieldHolder)
      {
         FieldHolder<?> clazz = ((FieldHolder<?>) source);

         String name = JavaParser.parse(JavaClass.class, "public class Temp{}").addField(fieldDef).getName();
         if (clazz.hasField(name))
         {
            throw new IllegalStateException("Field named [" + name + "] already exists.");
         }

         clazz.addField(fieldDef);
         java.saveJavaSource(source);
      }
   }

   @Command("new-method")
   @RequiresResource(JavaResource.class)
   public void newMethod(
            @PipeIn final String in,
            final PipeOut out,
            @Option(required = false,
                     help = "the method definition: surround with single quotes",
                     description = "method definition") final String... def) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String methodDef = null;
      if (def != null)
      {
         methodDef = Strings.join(Arrays.asList(def), " ");
      }
      else if (in != null)
      {
         methodDef = in;
      }
      else
      {
         throw new RuntimeException("arguments required");
      }

      JavaSource<?> source = resource.getJavaSource();
      if (source instanceof MethodHolder)
      {
         MethodHolder<?> clazz = ((MethodHolder<?>) source);

         Method<JavaClass> method = JavaParser.parse(JavaClass.class, "public class Temp{}").addMethod(methodDef);
         if (clazz.hasMethodSignature(method))
         {
            throw new IllegalStateException("Method with signature [" + method.toSignature()
                     + "] already exists.");
         }

         clazz.addMethod(methodDef);
         java.saveJavaSource(source);
      }
   }

   @Command("new-annotation-element")
   @RequiresResource(JavaResource.class)
   public void newAnnotationElement(
            @PipeIn final String in,
            final PipeOut out,
            @Option(name = "name", required = false, help = "the annotation element name; use with --type", description = "annotation element name") final String name,
            @Option(name = "type", required = false, help = "the annotation element type; use with --name", description = "annotation element type") final String type,
            @Option(required = false,
                     help = "the annotation element definition: surround with single quotes",
                     description = "annotation element definition") final String... def) throws FileNotFoundException
   {
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      String elementDef = null;
      if (def != null)
      {
         elementDef = Strings.join(Arrays.asList(def), " ");
      }
      else if (in != null)
      {
         elementDef = in;
      }
      else if (Strings.isNullOrEmpty(name) || Strings.isNullOrEmpty(type))
      {
         throw new RuntimeException("arguments required");
      }

      JavaSource<?> source = resource.getJavaSource();
      if (source.isAnnotation())
      {
         JavaAnnotation parent = JavaAnnotation.class.cast(source);

         String addName;
         if (elementDef != null)
         {
            addName = JavaParser.parse(JavaAnnotation.class, "public @interface Temp{}")
                     .addAnnotationElement(elementDef).getName();
         }
         else
         {
            addName = name;
         }

         if (parent.hasAnnotationElement(addName))
         {
            throw new IllegalStateException("Element named [" + addName + "] already exists.");
         }

         if (elementDef != null)
         {
            parent.addAnnotationElement(elementDef);
         }
         else
         {
            parent.addAnnotationElement().setName(name).setType(type);
         }
         java.saveJavaSource(source);
      }
   }

}
