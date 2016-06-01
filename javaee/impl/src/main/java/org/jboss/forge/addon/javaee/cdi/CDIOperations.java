/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.decorator.Decorator;
import javax.ejb.MessageDriven;
import javax.faces.convert.FacesConverter;
import javax.faces.validator.FacesValidator;
import javax.inject.Qualifier;
import javax.interceptor.Interceptor;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.ws.rs.Path;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.resources.JavaResourceVisitor;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.visit.VisitContext;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * This class contains CDI specific operations
 *
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CDIOperations
{

   public static final List<String> DEFAULT_QUALIFIERS = Arrays.asList("javax.enterprise.inject.Default",
            "javax.enterprise.inject.Any");

   /**
    * Returns all the injectable objects from the given {@link Project}. Most of the Java EE components can be injected,
    * except JPA entities, message driven beans....
    */
   public List<JavaResource> getProjectInjectableBeans(Project project)
   {
      final List<JavaResource> beans = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  JavaSource<?> javaSource = resource.getJavaType();
                  if (!javaSource.isAnnotation() && !javaSource.isEnum()
                           // CDI
                           && !javaSource.hasAnnotation(Decorator.class)
                           && !javaSource.hasAnnotation(Interceptor.class)
                           // EJB
                           && !javaSource.hasAnnotation(MessageDriven.class)
                           // JPA
                           && !javaSource.hasAnnotation(Entity.class)
                           && !javaSource.hasAnnotation(Embeddable.class)
                           && !javaSource.hasAnnotation(MappedSuperclass.class)
                           // JSF
                           && !javaSource.hasAnnotation(FacesConverter.class)
                           && !javaSource.hasAnnotation(FacesValidator.class)
                           // REST
                           && !javaSource.hasAnnotation(Path.class)
                           // Servlet
                           && !javaSource.hasAnnotation(WebServlet.class)
                           && !javaSource.hasAnnotation(WebFilter.class)
                           && !javaSource.hasAnnotation(WebListener.class)
                           // Bean Validation
                           && !javaSource.hasImport(ConstraintValidator.class)
                           && !javaSource.hasImport(Payload.class)
                  )
                  {
                     beans.add(resource);
                  }
               }
               catch (ResourceException | FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      return beans;
   }

   /**
    * Returns all the objects from the given {@link Project} that support injection point. Most of the Java EE
    * components can use @Inject, except most of the JPA artifacts (entities, embeddable...)
    */
   public List<JavaResource> getProjectInjectionPointBeans(Project project)
   {
      final List<JavaResource> beans = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  JavaSource<?> javaSource = resource.getJavaType();
                  if (javaSource.isClass()
                           // JPA
                           && !javaSource.hasAnnotation(Entity.class)
                           && !javaSource.hasAnnotation(MappedSuperclass.class)
                           && !javaSource.hasAnnotation(Embeddable.class)
                           // Bean Validation
                           && !javaSource.hasImport(Payload.class)
                  )
                  {
                     beans.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      return beans;
   }

   /**
    * Returns all the qualifiers from the given {@link Project}
    */
   public List<JavaResource> getProjectQualifiers(Project project)
   {
      final List<JavaResource> qualifiers = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               try
               {
                  JavaType<?> javaType = resource.getJavaType();
                  if (javaType.isAnnotation() && javaType.hasAnnotation(Qualifier.class))
                  {
                     qualifiers.add(resource);
                  }
               }
               catch (FileNotFoundException e)
               {
                  // ignore
               }
            }
         });
      }
      return qualifiers;
   }

   /**
    * Returns all the possible event types from the given {@link Project}
    */
   public List<JavaResource> getProjectEventTypes(Project project)
   {
      final List<JavaResource> eventTypes = new ArrayList<>();
      if (project != null)
      {
         project.getFacet(JavaSourceFacet.class).visitJavaSources(new JavaResourceVisitor()
         {
            @Override
            public void visit(VisitContext context, JavaResource resource)
            {
               eventTypes.add(resource);
            }
         });
      }
      return eventTypes;
   }
}