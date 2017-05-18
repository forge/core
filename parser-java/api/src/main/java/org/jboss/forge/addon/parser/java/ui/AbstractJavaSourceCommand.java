/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.parser.java.beans.ProjectOperations;
import org.jboss.forge.addon.parser.java.converters.PackageRootConverter;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Completers;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.JavaInterface;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.SyntaxError;
import org.jboss.forge.roaster.model.source.ExtendableSource;
import org.jboss.forge.roaster.model.source.InterfaceCapableSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.util.Types;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraints({
         @FacetConstraint(value = JavaSourceFacet.class, type = FacetConstraintType.REQUIRED)
})
public abstract class AbstractJavaSourceCommand<SOURCETYPE extends JavaSource<?>> extends AbstractProjectCommand
         implements JavaSourceDecorator<SOURCETYPE>
{
   private UIInput<String> targetPackage;
   private UIInput<String> named;
   private UIInput<Boolean> overwrite;
   private UIInput<String> extendsType;
   private UIInputMany<String> implementsType;

   private JavaSourceDecorator<SOURCETYPE> delegate = this;

   /**
    * Specifies which {@link JavaSourceDecorator} should be used to customize the generated Java code.
    * <p>
    * Note that, since this class implements {@link JavaSourceDecorator} itself, this class is its own delegate by
    * default, calling {@link #decorateSource(UIExecutionContext, Project, JavaSource)}.
    * 
    * @param delegate the {@link JavaSourceDecorator} to be used to customize the generated Java code
    */
   public void setJavaSourceDecorator(JavaSourceDecorator<SOURCETYPE> delegate)
   {
      this.delegate = delegate;
   }

   /**
    * Retrieves the {@link JavaSourceDecorator} associated with this class.
    * 
    * @return the {@link JavaSourceDecorator} associated with this class.
    */
   public JavaSourceDecorator<SOURCETYPE> getJavaSourceDecorator()
   {
      return delegate;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      targetPackage = factory.createInput("targetPackage", String.class).setLabel("Package Name")
               .setDescription("The package name where this type will be created");
      targetPackage.getFacet(HintsFacet.class).setInputType(InputType.JAVA_PACKAGE_PICKER);
      targetPackage.setValueConverter(new PackageRootConverter(getProjectFactory(), builder));

      named = factory.createInput("named", String.class).setLabel("Type Name").setRequired(true)
               .setDescription("The type name");

      overwrite = factory.createInput("overwrite", Boolean.class).setLabel("Overwrite")
               .setDescription("The overwrite flag that is used if the class already exists.").setDefaultValue(false);

      extendsType = factory.createInput("extends", String.class).setLabel("Extends")
               .setDescription("The type used in the extends keyword")
               .setEnabled(supportsExtends());
      extendsType.getFacet(HintsFacet.class).setInputType(InputType.JAVA_CLASS_PICKER);

      // When dealing with interfaces, implements == extends
      boolean isInterface = getSourceType() == JavaInterfaceSource.class;
      implementsType = factory.createInputMany(isInterface ? "extends" : "implements", String.class)
               .setLabel(isInterface ? "Extended Interfaces" : "Interfaces")
               .setEnabled(supportsImplements());
      implementsType.getFacet(HintsFacet.class).setInputType(InputType.JAVA_CLASS_PICKER);

      Project project = getSelectedProject(builder);
      final JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      // Setup named
      named.addValidator((context) -> {
         if (!Types.isSimpleName(named.getValue()))
            context.addValidationError(named, "Invalid java type name.");
      });

      overwrite.setEnabled(() -> named.hasValue() && classExists(javaSourceFacet));

      // Setup targetPackage
      if (project.hasFacet(JavaSourceFacet.class))
      {
         final Set<String> packageNames = new TreeSet<>();

         javaSourceFacet.visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource javaResource)
            {
               String packageName = javaSourceFacet.calculatePackage(javaResource);
               packageNames.add(packageName);
            }
         });
         targetPackage.setCompleter(Completers.fromValues(packageNames));
      }
      targetPackage.setDefaultValue(calculateDefaultPackage(builder.getUIContext()));
      builder.add(targetPackage).add(named);

      if (extendsType.isEnabled())
      {
         List<String> extendsList = getExtendsValueChoices(project).stream()
                  .map(JavaResource::getFullyQualifiedTypeName)
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
         extendsType.setCompleter(Completers.fromValues(extendsList));
         builder.add(extendsType);
      }

      if (implementsType.isEnabled())
      {
         List<String> implementsList = getImplementsValueChoices(project).stream()
                  .map(JavaResource::getFullyQualifiedTypeName)
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList());
         implementsType.setCompleter(Completers.fromValues(implementsList));
         builder.add(implementsType);
      }
      builder.add(overwrite);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Java: New " + getType())
               .description("Creates a new Java " + getType())
               .category(Categories.create("Java"));
   }

   /**
    * Get the type for which this command should create a new source file. ("Class", "Enum", "Interface", etc.)
    */
   protected abstract String getType();

   /**
    * Get the {@link JavaSource} type for which this command should create a new source file.
    */
   protected abstract Class<SOURCETYPE> getSourceType();

   /**
    * @return the possible value choices for the extends input
    */
   protected Collection<JavaResource> getExtendsValueChoices(Project project)
   {
      AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
      ProjectOperations projectOperations = addonRegistry.getServices(ProjectOperations.class).get();
      return projectOperations.getProjectClasses(project);
   }

   /**
    * @return the possible value choices for the implements input
    */
   protected Collection<JavaResource> getImplementsValueChoices(Project project)
   {
      AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
      ProjectOperations projectOperations = addonRegistry.getServices(ProjectOperations.class).get();
      return projectOperations.getProjectInterfaces(project);
   }

   /**
    * @return if this new type supports the usage of extends keyword
    */
   protected boolean supportsExtends()
   {
      Class<SOURCETYPE> sourceType = getSourceType();
      return ExtendableSource.class.isAssignableFrom(sourceType);
   }

   protected boolean supportsImplements()
   {
      Class<SOURCETYPE> sourceType = getSourceType();
      return InterfaceCapableSource.class.isAssignableFrom(sourceType);
   }

   private boolean classExists(JavaSourceFacet javaSourceFacet)
   {
      if (!named.hasValue() && !named.hasDefaultValue())
      {
         return false;
      }
      String packageName;
      if (targetPackage.hasValue() || targetPackage.hasDefaultValue())
      {
         packageName = targetPackage.getValue();
      }
      else
      {
         packageName = javaSourceFacet.getBasePackage();
      }
      String source = packageName + "." + named.getValue();
      boolean classAlreadyExists;
      try
      {
         JavaResource parsedJavaResource = javaSourceFacet.getJavaResource(source);
         classAlreadyExists = parsedJavaResource != null && parsedJavaResource.exists();
      }
      catch (Exception ex)
      {
         classAlreadyExists = false;
      }
      return classAlreadyExists;
   }

   @SuppressWarnings("unchecked")
   private SOURCETYPE buildJavaSource(JavaSourceFacet javaFacet)
   {
      if (!named.hasValue() && !named.hasDefaultValue())
      {
         return null;
      }

      SOURCETYPE source = (SOURCETYPE) Roaster.create(getSourceType()).setName(named.getValue());

      if (targetPackage.hasValue() || targetPackage.hasDefaultValue())
      {
         source.setPackage(targetPackage.getValue());
      }
      else
      {
         source.setPackage(javaFacet.getBasePackage());
      }
      if (source instanceof ExtendableSource && extendsType.isEnabled() && extendsType.hasValue())
      {
         ExtendableSource<?> extendableSource = (ExtendableSource<?>) source;
         extendSuperType(extendableSource, extendsType.getValue(), javaFacet);
      }
      if (source instanceof InterfaceCapableSource && implementsType.isEnabled() && implementsType.hasValue())
      {
         InterfaceCapableSource<?> interfaceCapableSource = (InterfaceCapableSource<?>) source;
         implementInterface(interfaceCapableSource, implementsType.getValue(), javaFacet);
      }
      return source;
   }

   protected void extendSuperType(ExtendableSource<?> source, String value, JavaSourceFacet facet)
   {
      JavaResource javaResource = facet.getJavaResource(value);
      try
      {
         if (javaResource != null && javaResource.exists())
         {
            JavaType<?> type = javaResource.getJavaType();
            if (type.isClass())
            {
               JavaClass<?> javaClass = (JavaClass<?>) type;
               source.extendSuperType(javaClass);
               return;
            }
         }
         else
         {
            // TODO: It may be a compiled class
         }
      }
      catch (FileNotFoundException e)
      {
      }
      source.setSuperType(value);
   }

   protected void implementInterface(InterfaceCapableSource<?> source, Iterable<String> value, JavaSourceFacet facet)
   {
      for (String type : value)
      {
         JavaResource javaResource = facet.getJavaResource(type);
         try
         {
            if (javaResource != null && javaResource.exists())
            {
               JavaType<?> javaType = javaResource.getJavaType();
               if (javaType.isInterface())
               {
                  source.implementInterface((JavaInterface<?>) javaType);
                  continue;
               }
            }
            else
            {
               // TODO: It may be a compiled class
            }
         }
         catch (FileNotFoundException e)
         {
         }
         source.addInterface(type);
      }
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      Project project = getSelectedProject(validator);
      final JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);

      if (classExists(javaSourceFacet) && overwrite.isEnabled() && overwrite.getValue() == false)
      {
         validator.addValidationError(named, getType() + " " + targetPackage.getValue() + "." + named.getValue()
                  + " already exists. Use the --overwrite flag to allow the overwrite.");
      }
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Project project = getSelectedProject(uiContext);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      SOURCETYPE source = buildJavaSource(javaSourceFacet);
      JavaResource javaResource;
      if (source.hasSyntaxErrors())
      {
         UIOutput output = uiContext.getProvider().getOutput();
         PrintStream err = output.err();
         err.println("Syntax Errors:");
         for (SyntaxError error : source.getSyntaxErrors())
         {
            err.println(error);
         }
         err.println();
         return Results.fail("Syntax Errors found. See above");
      }
      else
      {
         SOURCETYPE decorated = delegate.decorateSource(context, project, source);
         if (decorated != null)
            source = decorated;
         javaResource = javaSourceFacet.saveJavaSource(source);
      }

      uiContext.setSelection(javaResource);
      return Results.success(getType() + " " + source.getQualifiedName() + " was created");
   }

   /**
    * Override this method to do any necessary work to customize the generated {@link JavaResource}.
    */
   public SOURCETYPE decorateSource(UIExecutionContext context, Project project, SOURCETYPE source)
            throws Exception
   {
      return source;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   protected UIInput<String> getTargetPackage()
   {
      return targetPackage;
   }

   protected UIInput<String> getNamed()
   {
      return named;
   }

   protected UIInput<Boolean> getOverwrite()
   {
      return overwrite;
   }

   protected UIInput<String> getExtendsType()
   {
      return extendsType;
   }

   protected UIInputMany<String> getImplementsType()
   {
      return implementsType;
   }

   protected String calculateDefaultPackage(UIContext context)
   {
      String packageName;
      Project project = getSelectedProject(context);
      if (project != null)
      {
         packageName = project.getFacet(JavaSourceFacet.class).getBasePackage();
      }
      else
      {
         packageName = null;
      }
      return packageName;
   }
}
