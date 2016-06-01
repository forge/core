/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * Creates a {@link NamedQuery} annotation in a JPA {@link Entity}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(JPAFacet.class)
public class JPANewNamedQueryCommand extends AbstractJavaEECommand
{
   @Inject
   @WithAttributes(label = "Query Name", description = "The name attribute of the @NamedQuery annotation", required = true)
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Query", description = "The query attribute of the @NamedQuery annotation", required = true)
   private UIInput<String> query;

   @Inject
   @WithAttributes(label = "Target Entity", required = true, type = InputType.DROPDOWN)
   private UISelectOne<JavaResource> targetEntity;

   @Inject
   private PersistenceOperations persistenceOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      UISelection<Resource<?>> selection = context.getInitialSelection();
      Project project = getSelectedProject(builder);
      List<JavaResource> entities = persistenceOperations.getProjectEntities(project);
      targetEntity.setValueChoices(entities);
      int idx = -1;
      if (!selection.isEmpty())
      {
         idx = entities.indexOf(selection.get());
      }
      if (idx != -1)
      {
         targetEntity.setDefaultValue(entities.get(idx));
      }
      builder.add(named).add(query).add(targetEntity);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("JPA: New Named Query")
               .description("Creates a @NamedQuery in a JPA Entity")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "JPA"));
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      String queryName = named.getValue();
      String queryValue = query.getValue();
      JavaResource entity = targetEntity.getValue();
      JavaClassSource javaClass = entity.getJavaType();
      AnnotationSource<JavaClassSource> namedQueriesAnn;
      if (javaClass.hasAnnotation(NamedQueries.class))
      {
         namedQueriesAnn = javaClass.getAnnotation(NamedQueries.class);
      }
      else
      {
         namedQueriesAnn = javaClass.addAnnotation(NamedQueries.class);
      }
      // if (javaClass.hasAnnotation(NamedQuery.class))
      // {
      // // Move this annotation to a NamedQueries annotation
      // AnnotationSource<JavaClassSource> annotation = javaClass.getAnnotation(NamedQuery.class);
      // namedQueriesAnn.addAnnotationValue(annotation);
      // javaClass.removeAnnotation(annotation);
      // }
      AnnotationSource<JavaClassSource> namedQuery = null;
      // Find if an existing NamedQuery exists
      if (namedQueriesAnn.getAnnotationArrayValue() != null)
         for (AnnotationSource<JavaClassSource> namedQueryItem : namedQueriesAnn.getAnnotationArrayValue())
         {
            if (queryName.equals(namedQueryItem.getStringValue("name")))
            {
               namedQuery = namedQueryItem;
               break;
            }
         }
      if (namedQuery == null)
      {
         namedQuery = namedQueriesAnn.addAnnotationValue(NamedQuery.class);
         namedQuery.setStringValue("name", queryName);
      }
      namedQuery.setStringValue("query", queryValue);
      entity.setContents(javaClass);
      return Results.success("Named query " + queryName + " was created.");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

}
