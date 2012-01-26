/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.forge.parser.spi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jface.text.Document;
import org.jboss.forge.parser.ParserException;
import org.jboss.forge.parser.java.JavaAnnotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.ast.TypeDeclarationFinderVisitor;
import org.jboss.forge.parser.java.impl.JavaAnnotationImpl;
import org.jboss.forge.parser.java.impl.JavaClassImpl;
import org.jboss.forge.parser.java.impl.JavaEnumImpl;
import org.jboss.forge.parser.java.impl.JavaInterfaceImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaParserImpl implements JavaParserProvider
{

   @Override
   public JavaSource<?> parse(final File file) throws FileNotFoundException
   {
      FileInputStream stream = new FileInputStream(file);
      return parse(stream);
   }

   @Override
   public JavaSource<?> parse(final InputStream data)
   {
      try
      {
         char[] source = Util.getInputStreamAsCharArray(data, data.available(), "ISO8859_1");
         return parse(source);
      }
      catch (IOException e)
      {
         throw new IllegalArgumentException("InputStream must be a parsable java file: ", e);
      }
      finally
      {
         if (data != null)
         {
            try
            {
               data.close();
            }
            catch (IOException e)
            {
               throw new IllegalStateException(e);
            }
         }
      }
   }

   @Override
   public JavaSource<?> parse(final char[] data)
   {
      return parse(new String(data));
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public JavaSource<?> parse(final String data)
   {
      Document document = new Document(data);
      ASTParser parser = ASTParser.newParser(AST.JLS3);

      parser.setSource(document.get().toCharArray());
      Map options = JavaCore.getOptions();
      options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);
      parser.setCompilerOptions(options);

      parser.setResolveBindings(true);
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
      CompilationUnit unit = (CompilationUnit) parser.createAST(null);
      unit.recordModifications();

      TypeDeclarationFinderVisitor visitor = new TypeDeclarationFinderVisitor();
      unit.accept(visitor);

      AbstractTypeDeclaration declaration = visitor.getTypeDeclaration();
      if (declaration instanceof TypeDeclaration)
      {
         if (((TypeDeclaration) declaration).isInterface())
         {
            return new JavaInterfaceImpl(document, unit);
         }
         else
         {
            return new JavaClassImpl(document, unit);
         }
      }
      else if (declaration instanceof EnumDeclaration)
      {
         return new JavaEnumImpl(document, unit);
      }
      else if (declaration instanceof AnnotationTypeDeclaration)
      {
         return new JavaAnnotationImpl(document, unit);
      }
      else
      {
         throw new ParserException("Unknown JavaSource type.");
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends JavaSource<?>> T create(final Class<T> type)
   {
      if (JavaClass.class.isAssignableFrom(type))
         return (T) parse("public class JavaClass { }");

      if (JavaEnum.class.isAssignableFrom(type))
         return (T) parse("public enum JavaEnum { }");

      if (JavaAnnotation.class.isAssignableFrom(type))
         return (T) parse("public @interface JavaAnnotation { }");

      if (JavaInterface.class.isAssignableFrom(type))
         return (T) parse("public interface JavaInterface { }");

      throw new ParserException("Unknown JavaSource type [" + type.getName() + "]");
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends JavaSource<?>> T parse(final Class<T> type, final InputStream data)
   {

      JavaSource<?> source = parse(data);
      if (type.isAssignableFrom(source.getClass()))
      {
         return (T) source;
      }
      throw new ParserException("Source does not represent a [" + type.getSimpleName() + "], instead was ["
               + source.getClass().getSimpleName() + "] - Cannot convert.");
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends JavaSource<?>> T parse(final Class<T> type, final char[] data)
   {
      JavaSource<?> source = parse(data);
      if (type.isAssignableFrom(source.getClass()))
      {
         return (T) source;
      }
      throw new ParserException("Source does not represent a [" + type.getSimpleName() + "], instead was ["
               + source.getClass().getSimpleName() + "] - Cannot convert.");
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends JavaSource<?>> T parse(final Class<T> type, final String data)
   {
      JavaSource<?> source = parse(data);
      if (type.isAssignableFrom(source.getClass()))
      {
         return (T) source;
      }
      throw new ParserException("Source does not represent a [" + type.getSimpleName() + "], instead was ["
               + source.getClass().getSimpleName() + "] - Cannot convert.");
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T extends JavaSource<?>> T parse(final Class<T> type, final File file) throws FileNotFoundException
   {
      JavaSource<?> source = parse(file);
      if (type.isAssignableFrom(source.getClass()))
      {
         return (T) source;
      }
      throw new ParserException("Source does not represent a [" + type.getSimpleName() + "], instead was ["
               + source.getClass().getSimpleName() + "] - Cannot convert.");
   }
}
