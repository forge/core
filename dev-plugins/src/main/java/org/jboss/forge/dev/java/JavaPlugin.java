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
package org.jboss.forge.dev.java;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.Import;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.parser.java.SyntaxError;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
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
         java.saveEnumTypeSource(je);
      }
      else
      {
         writer.println(ShellColor.RED, "Syntax Errors:");
         for (SyntaxError error : je.getSyntaxErrors())
         {
            writer.println(error.toString());
         }
         writer.println();

         if (prompt.promptBoolean("Your class has syntax errors, create anyway?", true))
         {
            java.saveEnumTypeSource(je);
         }
      }

      pickUp.fire(new PickupResource(java.getEnumTypeResource(je)));
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
      java.saveEnumTypeSource(source);
      
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
}
