/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.persistence.Embeddable;
import javax.persistence.GenerationType;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jpa.EntityIdType;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.parser.java.converters.PackageRootConverter;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ch.schulz@joinout.de">Christoph "criztovyl" Schulz</a>
 */
@FacetConstraint(JavaSourceFacet.class)
public class JPANewEntityCommandImpl extends AbstractJPACommand<JavaClassSource> implements JPANewEntityCommand
{
   @Inject
   @WithAttributes(label = "ID Column Generation Strategy", defaultValue = "AUTO")
   private UISelectOne<GenerationType> idStrategy;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> targetLocation;

   @Inject
   @WithAttributes(label = "Table Name")
   private UIInput<String> tableName;

   @Inject
   @WithAttributes(label = "Entity ID Strategy", defaultValue = "LONG_PROPERTY")
   private UISelectOne<EntityIdType> idType;

   @Inject
   @WithAttributes(label = "Entity ID Class", description = "The Class for the entity ID.", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> idClass;

   @Inject
   private PersistenceOperations persistenceOperations;

   @Inject
   private Configuration configuration;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("JPA: New Entity")
               .description("Create a new JPA Entity");
   }

   @Override
   protected String getType()
   {
      return "JPA Entity";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      idStrategy.setDefaultValue(GenerationType.AUTO);
      idType.setDefaultValue(EntityIdType.LONG_PROPERTY);
      idClass.setRequired(() -> idType.getValue().isClassRequired());
      idClass.setValueConverter(new PackageRootConverter(getProjectFactory(), builder));
      builder.add(idStrategy).add(tableName)
          .add(idType).add(idClass);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      GenerationType idStrategyChosen = idStrategy.getValue();
      if (idStrategyChosen == null)
      {
         idStrategyChosen = GenerationType.AUTO;
      }

      EntityIdType idTypeChosen = idType.getValue();
      if (idTypeChosen == null)
      {
         idTypeChosen = EntityIdType.LONG_PROPERTY;
      }

      ConfigurationFacet facet = project.getFacet(ConfigurationFacet.class);
      Configuration config = facet.getConfiguration();
      String idPropertyName = config.getString(PersistenceOperations.ID_PROPERTY_NAME_CONFIGURATION_KEY,
               configuration.getString(PersistenceOperations.ID_PROPERTY_NAME_CONFIGURATION_KEY, "id"));
      String versionPropertyName = config.getString(PersistenceOperations.VERSION_PROPERTY_NAME_CONFIGURATION_KEY,
               configuration.getString(PersistenceOperations.VERSION_PROPERTY_NAME_CONFIGURATION_KEY, "version"));

      switch (idTypeChosen)
      {
      case LONG_PROPERTY:
         return persistenceOperations.newEntity(source, idStrategyChosen, tableName.getValue(), idPropertyName,
                  versionPropertyName);
      case EMBEDDED_ID:
         if (!getIdClass(project, idClass.getValue()).hasAnnotation(Embeddable.class))
         {
            throw new IllegalArgumentException(
                     "The provided ID class for @EmbeddedId is missing the @Embeddable annotation!");
         }
         return persistenceOperations.newEntityEmbeddedId(source, tableName.getValue(), idPropertyName,
                  idClass.getValue(), versionPropertyName);
      case ID_CLASS:
         try
         {
            return persistenceOperations.newEntityIdClass(source, tableName.getValue(),
                     getIdClass(project, idClass.getValue()),
                     versionPropertyName);
         }
         catch (ResourceException e)
         {
            throw new IllegalArgumentException("The provided ID class for @IdClass does not exist!", e);
         }
      default:
         throw new IllegalArgumentException("Unknown Enum value " + idTypeChosen + "!");
      }
   }

   public JavaClassSource getIdClass(Project project, String idClassName) throws FileNotFoundException,
            IllegalArgumentException
   {
      JavaResource jr = project.getFacet(JavaSourceFacet.class).getJavaResource(idClass.getValue());

      if (!(jr.getJavaType() instanceof JavaClassSource))
      {
         throw new IllegalArgumentException("The found JavaResource for the ID class is not a JavaClassResource, " +
                  "which is required. Found resource: " + jr.getClass().getCanonicalName());
      }

      return (JavaClassSource) jr.getJavaType();

   }
}
