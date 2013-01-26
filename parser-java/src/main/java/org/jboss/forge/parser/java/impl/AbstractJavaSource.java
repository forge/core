/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.jdt.core.dom.BodyDeclaration;
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
import org.jboss.forge.parser.java.util.Formatter;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.parser.java.util.Types;
import org.jboss.forge.parser.spi.JavaParserImpl;
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

   protected final Document document;
   protected final CompilationUnit unit;
   protected final BodyDeclaration body;
   protected final JavaSource<?> enclosingType;

   public static ServiceLoader<WildcardImportResolver> loader = ServiceLoader.load(WildcardImportResolver.class);
   private static List<WildcardImportResolver> resolvers;

   public AbstractJavaSource(JavaSource<?> enclosingType, final Document document, final CompilationUnit unit,
            BodyDeclaration body)
   {
      this.enclosingType = enclosingType == null ? this : enclosingType;
      this.document = document;
      this.unit = unit;
      this.body = body;
   }

   @Override
   public JavaSource<?> getEnclosingType()
   {
      return enclosingType;
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
      return addImport(type.getCanonicalName());
   }

   @Override
   public <T extends JavaSource<?>> Import addImport(final T type)
   {
      String qualifiedName = type.getQualifiedName();
      return this.addImport(qualifiedName);
   }

   @Override
   public Import addImport(final Import imprt)
   {
      return addImport(imprt.getQualifiedName()).setStatic(imprt.isStatic());
   }

   @Override
   public Import addImport(final String className)
   {
      String strippedClassName = Types.stripGenerics(Types.stripArray(className));
      Import imprt;
      if (Types.isSimpleName(strippedClassName) && !hasImport(strippedClassName))
      {
         throw new IllegalArgumentException("Cannot import class without a package [" + strippedClassName + "]");
      }

      if (!hasImport(strippedClassName) && validImport(strippedClassName))
      {
         imprt = new ImportImpl(this).setName(strippedClassName);
         unit.imports().add(imprt.getInternal());
      }
      else if (hasImport(strippedClassName))
      {
         imprt = getImport(strippedClassName);
      }
      else
      {
         throw new IllegalArgumentException("Attempted to import the illegal type [" + strippedClassName + "]");
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
      String resultType = type;
      if (Types.isArray(type))
      {
         resultType = Types.stripArray(type);
      }
      if (Types.isGeneric(type))
      {
         resultType = Types.stripGenerics(type);
      }
      return getImport(resultType) != null;
   }

   @Override
   public boolean requiresImport(final Class<?> type)
   {
      return requiresImport(type.getName());
   }

   @Override
   public boolean requiresImport(final String type)
   {
      String resultType = type;
      if (Types.isArray(resultType))
      {
         resultType = Types.stripArray(type);
      }
      if (Types.isGeneric(resultType))
      {
         resultType = Types.stripGenerics(resultType);
      }
      if (!validImport(resultType)
               || hasImport(resultType)
               || Types.isJavaLang(resultType))
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

      if (Types.isPrimitive(result))
      {
         return result;
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
            for (Import imprt : getImports())
            {
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
         if (getPackage() != null)
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
      return !Strings.isNullOrEmpty(type) && !Types.isPrimitive(type);
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
      if (body instanceof AbstractTypeDeclaration)
         return (AbstractTypeDeclaration) body;
      throw new ParserException("Source body was not of the expected type.");
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

   @Override
   public String getCanonicalName()
   {
      String result = getName();

      JavaSource<?> enclosingType = this;
      while (enclosingType != enclosingType.getEnclosingType())
      {
         enclosingType = getEnclosingType();
         result = enclosingType.getEnclosingType().getName() + "." + result;
         enclosingType = enclosingType.getEnclosingType();
      }

      if (!Strings.isNullOrEmpty(getPackage()))
         result = getPackage() + "." + result;

      return result;
   }

   /**
    * Call-back to allow updating of any necessary internal names with the given name.
    */
   protected abstract O updateTypeNames(String name);

   @Override
   public String getQualifiedName()
   {
      String result = getName();

      JavaSource<?> enclosingType = this;
      while (enclosingType != enclosingType.getEnclosingType())
      {
         enclosingType = getEnclosingType();
         result = enclosingType.getEnclosingType().getName() + "$" + result;
         enclosingType = enclosingType.getEnclosingType();
      }

      if (!Strings.isNullOrEmpty(getPackage()))
         result = getPackage() + "." + result;

      return result;
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

      try
      {
         TextEdit edit = unit.rewrite(document, null);
         edit.apply(document);
      }
      catch (Exception e)
      {
         throw new ParserException("Could not modify source: " + unit.toString(), e);
      }

      return Formatter.format(document.get());
   }

   @Override
   public Object getInternal()
   {
      return unit;
   }

   @Override
   public O getOrigin()
   {
      return (O) this;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((body == null) ? 0 : body.hashCode());
      result = prime * result + ((document == null) ? 0 : document.hashCode());
      result = prime * result + ((enclosingType == null) ? 0 : enclosingType.hashCode());
      result = prime * result + ((unit == null) ? 0 : unit.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      AbstractJavaSource<?> other = (AbstractJavaSource<?>) obj;
      if (body == null)
      {
         if (other.body != null)
            return false;
      }
      else if (!body.equals(other.body))
         return false;
      if (document == null)
      {
         if (other.document != null)
            return false;
      }
      else if (!document.equals(other.document))
         return false;
      if (enclosingType == null)
      {
         if (other.enclosingType != null)
            return false;
      }
      else if (!enclosingType.equals(other.enclosingType))
         return false;
      if (unit == null)
      {
         if (other.unit != null)
            return false;
      }
      else if (!unit.equals(other.unit))
         return false;
      return true;
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
      if (!this.hasInterface(type))
      {
         Type interfaceType = JDTHelper.getInterfaces(
                  JavaParser.parse(JavaInterfaceImpl.class,
                           "public interface Mock extends " + Types.toSimpleName(type)
                                    + " {}").getBodyDeclaration()).get(0);

         if (this.hasInterface(Types.toSimpleName(type)) || this.hasImport(Types.toSimpleName(type)))
         {
            interfaceType = JDTHelper.getInterfaces(JavaParser.parse(JavaInterfaceImpl.class,
                     "public interface Mock extends " + type + " {}").getBodyDeclaration()).get(0);
         }

         this.addImport(type);

         ASTNode node = ASTNode.copySubtree(unit.getAST(), interfaceType);
         JDTHelper.getInterfaces(getBodyDeclaration()).add((Type) node);
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

   @Override
   public List<JavaSource<?>> getNestedClasses()
   {
      List<AbstractTypeDeclaration> declarations = getNestedDeclarations(body);

      List<JavaSource<?>> result = new ArrayList<JavaSource<?>>();
      for (AbstractTypeDeclaration declaration : declarations)
      {
         result.add(JavaParserImpl.getJavaSource(this, document, unit, declaration));
      }
      return result;
   }

   private List<AbstractTypeDeclaration> getNestedDeclarations(BodyDeclaration body)
   {

      TypeDeclarationFinderVisitor typeDeclarationFinder = new TypeDeclarationFinderVisitor();
      body.accept(typeDeclarationFinder);
      List<AbstractTypeDeclaration> declarations = typeDeclarationFinder.getTypeDeclarations();

      List<AbstractTypeDeclaration> result = new ArrayList<AbstractTypeDeclaration>(declarations);
      if (!declarations.isEmpty())
      {
         // We don't want to return the current class' declaration.
         result.remove(declarations.remove(0));
         for (AbstractTypeDeclaration declaration : declarations)
         {
            result.removeAll(getNestedDeclarations(declaration));
         }
      }

      return result;
   }

}