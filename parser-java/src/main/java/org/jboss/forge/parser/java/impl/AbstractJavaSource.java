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
package org.jboss.forge.parser.java.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.ParserException;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Import;
import org.jboss.forge.parser.java.InterfaceCapable;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.SyntaxError;
import org.jboss.forge.parser.java.Visibility;
import org.jboss.forge.parser.java.ast.AnnotationAccessor;
import org.jboss.forge.parser.java.ast.ModifierAccessor;
import org.jboss.forge.parser.java.ast.TypeDeclarationFinderVisitor;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.parser.spi.WildcardImportResolver;

/**
 * Represents a Java Source File
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@SuppressWarnings("unchecked")
public abstract class AbstractJavaSource<O extends JavaSource<O>> implements
         JavaSource<O>, InterfaceCapable<O>
{
   private final AnnotationAccessor<O, O> annotations = new AnnotationAccessor<O, O>();
   private final ModifierAccessor modifiers = new ModifierAccessor();

   private final Document document;
   protected CompilationUnit unit;

   public static ServiceLoader<WildcardImportResolver> loader = ServiceLoader.load(WildcardImportResolver.class);
   private static List<WildcardImportResolver> resolvers;

   public AbstractJavaSource(final Document document, final CompilationUnit unit)
   {
      this.document = document;
      this.unit = unit;
   }

   /*
    * Annotation modifiers
    */
   @Override
   public Annotation<O> addAnnotation()
   {
      return annotations.addAnnotation(this, getBodyDeclaration());
   }

   @Override
   public Annotation<O> addAnnotation(final Class<? extends java.lang.annotation.Annotation> clazz)
   {
      return annotations.addAnnotation(this, getBodyDeclaration(), clazz.getName());
   }

   @Override
   public Annotation<O> addAnnotation(final String className)
   {
      return annotations.addAnnotation(this, getBodyDeclaration(), className);
   }

   @Override
   public List<Annotation<O>> getAnnotations()
   {
      return annotations.getAnnotations(this, getBodyDeclaration());
   }

   @Override
   public boolean hasAnnotation(final Class<? extends java.lang.annotation.Annotation> type)
   {
      return annotations.hasAnnotation(this, getBodyDeclaration(), type.getName());
   }

   @Override
   public boolean hasAnnotation(final String type)
   {
      return annotations.hasAnnotation(this, getBodyDeclaration(), type);
   }

   @Override
   public O removeAnnotation(final Annotation<O> annotation)
   {
      return (O) annotations.removeAnnotation(this, getBodyDeclaration(), annotation);
   }

   @Override
   public Annotation<O> getAnnotation(final Class<? extends java.lang.annotation.Annotation> type)
   {
      return annotations.getAnnotation(this, getBodyDeclaration(), type);
   }

   @Override
   public Annotation<O> getAnnotation(final String type)
   {
      return annotations.getAnnotation(this, getBodyDeclaration(), type);
   }

   /*
    * Import modifiers
    */

   @Override
   public Import addImport(final Class<?> type)
   {
      return addImport(type.getName());
   }

   @Override
   public <T extends JavaSource<?>> Import addImport(final T type)
   {
      return this.addImport(type.getQualifiedName());
   }

   @Override
   public Import addImport(final Import imprt)
   {
      return addImport(imprt.getQualifiedName()).setStatic(imprt.isStatic());
   }

   @Override
   public Import addImport(final String className)
   {
      Import imprt;
      String simpleName = Types.toSimpleName(className);
      if (!hasImport(simpleName) && validImport(className))
      {
         imprt = new ImportImpl(this).setName(className);
         unit.imports().add(imprt.getInternal());
      }
      else if (hasImport(className))
      {
         imprt = getImport(className);
      }
      else
      {
         if (hasImport(simpleName))
         {
            throw new IllegalStateException("Cannot import [" + className
                     + "] because of existing conflicting import [" + getImport(simpleName) + "].");
         }
         else
         {
            throw new IllegalArgumentException("Attempted to import the illegal type [" + className + "]");
         }
      }
      return imprt;
   }

   @Override
   public Import getImport(final String className)
   {
      List<Import> imports = getImports();
      for (Import imprt : imports)
      {
         if (imprt.getQualifiedName().equals(className) || imprt.getSimpleName().equals(className))
         {
            return imprt;
         }
      }
      return null;
   }

   @Override
   public Import getImport(final Class<?> type)
   {
      return getImport(type.getName());
   }

   @Override
   public <T extends JavaSource<?>> Import getImport(final T type)
   {
      return getImport(type.getQualifiedName());
   }

   @Override
   public Import getImport(final Import imprt)
   {
      return getImport(imprt.getQualifiedName());
   }

   @Override
   public List<Import> getImports()
   {
      List<Import> results = new ArrayList<Import>();

      for (ImportDeclaration i : (List<ImportDeclaration>) unit.imports())
      {
         results.add(new ImportImpl(this, i));
      }

      return Collections.unmodifiableList(results);
   }

   @Override
   public boolean hasImport(final Class<?> type)
   {
      return hasImport(type.getName());
   }

   @Override
   public <T extends JavaSource<T>> boolean hasImport(final T type)
   {
      return hasImport(type.getQualifiedName());
   }

   @Override
   public boolean hasImport(final Import imprt)
   {
      return hasImport(imprt.getQualifiedName());
   }

   @Override
   public boolean hasImport(final String type)
   {
      return getImport(type) != null;
   }

   @Override
   public boolean requiresImport(final Class<?> type)
   {
      return requiresImport(type.getName());
   }

   @Override
   public boolean requiresImport(final String type)
   {
      if (!validImport(type)
               || hasImport(type)
               || type.startsWith("java.lang."))
      {
         return false;
      }
      return true;
   }

   @Override
   public String resolveType(final String type)
   {
      String original = type;
      String result = type;

      if (Types.isPrimitive(result))
      {
         return result;
      }

      // Strip away any characters that might hinder the type matching process
      if (Types.isArray(result))
      {
         original = Types.stripArray(result);
         result = Types.stripArray(result);
      }

      if (Types.isGeneric(result))
      {
         original = Types.stripGenerics(result);
         result = Types.stripGenerics(result);
      }

      // Check for direct import matches first since they are the fastest and least work-intensive
      if (Types.isSimpleName(result))
      {
         if (!hasImport(type) && Types.isJavaLang(type))
         {
            result = "java.lang." + result;
         }

         if (result.equals(original))
         {
            for (Import imprt : getImports()) {
               if (Types.areEquivalent(result, imprt.getQualifiedName()))
               {
                  result = imprt.getQualifiedName();
                  break;
               }
            }
         }
      }

      // If we didn't match any imports directly, we might have a wild-card/on-demand import.
      if (Types.isSimpleName(result))
      {
         for (Import imprt : getImports())
         {
            if (imprt.isWildcard())
            {
               // TODO warn if no wild-card resolvers are configured
               // TODO Test wild-card/on-demand import resolving
               for (WildcardImportResolver r : getImportResolvers())
               {
                  result = r.resolve(this, result);
                  if (Types.isQualified(result))
                     break;
               }
            }
         }
      }

      // No import matches and no wild-card/on-demand import matches means this class is in the same package.
      if (Types.isSimpleName(result))
      {
         result = getPackage() + "." + result;
      }

      return result;
   }

   private List<WildcardImportResolver> getImportResolvers()
   {
      if (resolvers == null)
      {
         resolvers = new ArrayList<WildcardImportResolver>();
         for (WildcardImportResolver r : resolvers)
         {
            resolvers.add(r);
         }
      }
      if (resolvers.size() == 0)
      {
         throw new IllegalStateException("No instances of [" + WildcardImportResolver.class.getName()
                  + "] were found on the classpath.");
      }
      return resolvers;
   }

   private boolean validImport(final String type)
   {
      return (type != null) && !type.matches("byte|short|int|long|float|double|char|boolean");
   }

   @Override
   public O removeImport(final String name)
   {
      for (Import i : getImports())
      {
         if (i.getQualifiedName().equals(name))
         {
            removeImport(i);
            break;
         }
      }
      return (O) this;
   }

   @Override
   public O removeImport(final Class<?> clazz)
   {
      return removeImport(clazz.getName());
   }

   @Override
   public <T extends JavaSource<?>> O removeImport(final T type)
   {
      return removeImport(type.getQualifiedName());
   }

   @Override
   public O removeImport(final Import imprt)
   {
      Object internal = imprt.getInternal();
      if (unit.imports().contains(internal))
      {
         unit.imports().remove(internal);
      }
      return (O) this;
   }

   @Override
   public List<Member<O, ?>> getMembers()
   {
      List<Member<O, ?>> result = new ArrayList<Member<O, ?>>();

      return result;
   }

   protected AbstractTypeDeclaration getBodyDeclaration()
   {
      TypeDeclarationFinderVisitor typeDeclarationFinder = new TypeDeclarationFinderVisitor();
      unit.accept(typeDeclarationFinder);
      AbstractTypeDeclaration declaration = typeDeclarationFinder.getTypeDeclaration();
      if (declaration == null)
      {
         throw new RuntimeException(
                  "A type-declaration is required in order to complete the current operation, but no type-declaration exists in compilation unit: "
                           + unit.toString());
      }
      return declaration;
   }

   /*
    * Name modifiers
    */
   @Override
   public String getName()
   {
      return getBodyDeclaration().getName().getIdentifier();
   }

   @Override
   public O setName(final String name)
   {
      getBodyDeclaration().setName(unit.getAST().newSimpleName(name));
      return updateTypeNames(name);
   }

   /**
    * Call-back to allow updating of any necessary internal names with the given name.
    */
   protected abstract O updateTypeNames(String name);

   @Override
   public String getQualifiedName()
   {
      String packg = getPackage();
      String name = getName();
      if ((packg != null) && !packg.isEmpty())
      {
         return packg + "." + name;
      }
      return name;
   }

   /*
    * Package modifiers
    */
   @Override
   public String getPackage()
   {
      PackageDeclaration pkg = unit.getPackage();
      if (pkg != null)
      {
         return pkg.getName().getFullyQualifiedName();
      }
      else
      {
         return null;
      }
   }

   @Override
   public O setPackage(final String name)
   {
      if (unit.getPackage() == null)
      {
         unit.setPackage(unit.getAST().newPackageDeclaration());
      }
      unit.getPackage().setName(unit.getAST().newName(name));
      return (O) this;
   }

   @Override
   public O setDefaultPackage()
   {
      unit.setPackage(null);
      return (O) this;
   }

   @Override
   public boolean isDefaultPackage()
   {
      return unit.getPackage() == null;
   }

   /*
    * Visibility modifiers
    */
   @Override
   public boolean isPackagePrivate()
   {
      return (!isPublic() && !isPrivate() && !isProtected());
   }

   @Override
   public O setPackagePrivate()
   {
      modifiers.clearVisibility(getBodyDeclaration());
      return (O) this;
   }

   @Override
   public boolean isPublic()
   {
      return modifiers.hasModifier(getBodyDeclaration(), ModifierKeyword.PUBLIC_KEYWORD);
   }

   @Override
   public O setPublic()
   {
      modifiers.clearVisibility(getBodyDeclaration());
      modifiers.addModifier(getBodyDeclaration(), ModifierKeyword.PUBLIC_KEYWORD);
      return (O) this;
   }

   @Override
   public boolean isPrivate()
   {
      return modifiers.hasModifier(getBodyDeclaration(), ModifierKeyword.PRIVATE_KEYWORD);
   }

   @Override
   public O setPrivate()
   {
      modifiers.clearVisibility(getBodyDeclaration());
      modifiers.addModifier(getBodyDeclaration(), ModifierKeyword.PRIVATE_KEYWORD);
      return (O) this;
   }

   @Override
   public boolean isProtected()
   {
      return modifiers.hasModifier(getBodyDeclaration(), ModifierKeyword.PROTECTED_KEYWORD);
   }

   @Override
   public O setProtected()
   {
      modifiers.clearVisibility(getBodyDeclaration());
      modifiers.addModifier(getBodyDeclaration(), ModifierKeyword.PROTECTED_KEYWORD);
      return (O) this;
   }

   @Override
   public Visibility getVisibility()
   {
      return Visibility.getFrom(this);
   }

   @Override
   public O setVisibility(final Visibility scope)
   {
      return (O) Visibility.set(this, scope);
   }

   /*
    * Non-manipulation methods.
    */
   /**
    * Return this {@link JavaSource} file as a String
    */
   @Override
   public String toString()
   {
      Document document = new Document(this.document.get());

      try {
         TextEdit edit = unit.rewrite(document, null);
         edit.apply(document);
      }
      catch (Exception e) {
         throw new ParserException("Could not modify source: " + unit.toString(), e);
      }

      String documentString = document.get();
      return documentString;
   }

   @Override
   public Object getInternal()
   {
      return unit;
   }

   @Override
   public O getOrigin()
   {
      // try
      // {
      // TextEdit edit = unit.rewrite(document, null);
      // edit.apply(document);
      // }
      // catch (MalformedTreeException e)
      // {
      // throw new RuntimeException(e);
      // }
      // catch (BadLocationException e)
      // {
      // throw new RuntimeException(e);
      // }

      return (O) this;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + ((toString() == null) ? 0 : unit.toString().hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      return (this == obj)
               || ((obj != null) && (getClass() == obj.getClass()) && this.toString().equals(obj.toString()));
   }

   @Override
   public List<SyntaxError> getSyntaxErrors()
   {
      List<SyntaxError> result = new ArrayList<SyntaxError>();

      IProblem[] problems = unit.getProblems();
      if (problems != null)
      {
         for (IProblem problem : problems)
         {
            result.add(new SyntaxErrorImpl(this, problem));
         }
      }
      return result;
   }

   @Override
   public boolean hasSyntaxErrors()
   {
      return !getSyntaxErrors().isEmpty();
   }

   @Override
   public boolean isClass()
   {
      AbstractTypeDeclaration declaration = getBodyDeclaration();
      return (declaration instanceof TypeDeclaration)
               && !((TypeDeclaration) declaration).isInterface();

   }

   @Override
   public boolean isEnum()
   {
      AbstractTypeDeclaration declaration = getBodyDeclaration();
      return declaration instanceof EnumDeclaration;
   }

   @Override
   public boolean isInterface()
   {
      AbstractTypeDeclaration declaration = getBodyDeclaration();
      return (declaration instanceof TypeDeclaration)
               && ((TypeDeclaration) declaration).isInterface();
   }

   @Override
   public boolean isAnnotation()
   {
      AbstractTypeDeclaration declaration = getBodyDeclaration();
      return declaration instanceof AnnotationTypeDeclaration;
   }

   /*
    * Interfaced Methods
    */

   @Override
   public List<String> getInterfaces()
   {
      List<String> result = new ArrayList<String>();
      List<Type> superTypes = JDTHelper.getInterfaces(getBodyDeclaration());
      for (Type type : superTypes)
      {
         String name = JDTHelper.getTypeName(type);
         if (Types.isSimpleName(name) && this.hasImport(name))
         {
            Import imprt = this.getImport(name);
            String pkg = imprt.getPackage();
            if (!Strings.isNullOrEmpty(pkg))
            {
               name = pkg + "." + name;
            }
         }
         result.add(name);
      }
      return result;
   }

   @Override
   public O addInterface(final String type)
   {
      List<Type> interfaces = JDTHelper.getInterfaces(
               JavaParser.parse(JavaInterfaceImpl.class, "public interface Mock extends " + type
                        + " {}").getBodyDeclaration());

      if (!interfaces.isEmpty())
      {
         if (!this.hasImport(Types.toSimpleName(type)))
         {
            this.addImport(type);
         }

         Type t = interfaces.get(0);
         ASTNode node = ASTNode.copySubtree(unit.getAST(), t);
         JDTHelper.getInterfaces(getBodyDeclaration()).add((Type) node);
      }
      else
      {
         throw new IllegalArgumentException("Could not parse interface declaration [" + type + "]");
      }
      return (O) this;
   }

   @Override
   public O addInterface(final Class<?> type)
   {
      return addInterface(type.getName());
   }

   @Override
   public O addInterface(final JavaInterface type)
   {
      return addInterface(type.getQualifiedName());
   }

   @Override
   public boolean hasInterface(final String type)
   {
      for (String name : getInterfaces())
      {
         if (Types.areEquivalent(name, type))
         {
            return true;
         }
      }
      return false;
   }

   @Override
   public boolean hasInterface(final Class<?> type)
   {
      return hasInterface(type.getName());
   }

   @Override
   public boolean hasInterface(final JavaInterface type)
   {
      return hasInterface(type.getQualifiedName());
   }

   @Override
   public O removeInterface(final String type)
   {
      List<Type> interfaces = JDTHelper.getInterfaces(getBodyDeclaration());
      for (Type i : interfaces)
      {
         if (Types.areEquivalent(i.toString(), type))
         {
            interfaces.remove(i);
            break;
         }
      }
      return (O) this;
   }

   @Override
   public O removeInterface(final Class<?> type)
   {
      return removeInterface(type.getName());
   }

   @Override
   public O removeInterface(final JavaInterface type)
   {
      return removeInterface(type.getQualifiedName());
   }
}
