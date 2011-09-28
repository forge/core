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
package org.jboss.forge.spec.javaee.rest;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.shrinkwrap.descriptor.impl.base.Strings;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("rest")
@RequiresFacet(RestFacet.class)
public class RestPlugin implements Plugin
{
   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @Inject
   @Current
   private Resource<?> currentResource;

   @SetupCommand
   public void setup(final PipeOut out)
   {
      if (!project.hasFacet(RestFacet.class))
      {
         request.fire(new InstallFacets(RestFacet.class));
      }

      if (project.hasFacet(RestFacet.class))
      {
         ShellMessages.success(out, "Rest Web Services (JAX-RS) is installed.");
      }
   }

   @SuppressWarnings("unchecked")
   @Command(value = "endpoint-from-entity", help = "Creates a REST endpoint from an existing domain @Entity object")
   public void endpointFromEntity(final PipeOut out,
            @Option(required = false) JavaResource[] targets) throws FileNotFoundException
   {
      /*
       * Make sure we have all the features we need for this to work.
       */
      if (!project.hasAllFacets(Arrays.asList(CDIFacet.class, PersistenceFacet.class)))
      {
         request.fire(new InstallFacets(true, CDIFacet.class, PersistenceFacet.class));
      }

      if (((targets == null) || (targets.length < 1))
               && (currentResource instanceof JavaResource))
      {
         targets = new JavaResource[] { (JavaResource) currentResource };
      }

      List<JavaResource> javaTargets = selectTargets(out, targets);
      if (javaTargets.isEmpty())
      {
         ShellMessages.error(out, "Must specify a domain @Entity on which to operate.");
         return;
      }

      final JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      for (JavaResource jr : javaTargets)
      {
         JavaClass entity = (JavaClass) (jr).getJavaSource();
         entity.addAnnotation(XmlRootElement.class);

         JavaClass endpoint = JavaParser.parse(JavaClass.class,
                  getClass().getResourceAsStream("/org/jboss/forge/rest/Endpoint.jv"));
         endpoint.setPackage(java.getBasePackage() + ".rest");

         /*
          * Configure endpoint
          */
         endpoint.addImport(entity.getQualifiedName());
         endpoint.setName(getEntityTable(entity) + "Endpoint");
         endpoint.getAnnotation(Path.class).setStringValue("/" + getEntityTable(entity).toLowerCase());

         /*
          * Configure listAll
          */
         Method<JavaClass> listAll = endpoint.getMethod("listAll");
         listAll.setReturnType("List<" + getEntityTable(entity) + ">");
         listAll.setBody(listAll.getBody().replaceAll("Object", getEntityTable(entity)));
         listAll.setBody(listAll.getBody().replaceAll("Table", getEntityTable(entity)));

         /*
          * Configure findById
          */
         Method<JavaClass> find = endpoint.getMethod("findById", long.class);
         find.setReturnType(entity);
         find.setBody(find.getBody().replaceAll("Object", entity.getName()));

         java.saveJavaSource(entity);
         java.saveJavaSource(endpoint);
         ShellMessages.success(out, "Generated REST endpoint for [" + entity.getQualifiedName() + "]");
      }
   }

   private String getEntityTable(final JavaClass entity)
   {
      String table = entity.getName();
      if (entity.hasAnnotation(Entity.class))
      {
         Annotation<JavaClass> a = entity.getAnnotation(Entity.class);
         if (!Strings.isNullOrEmpty(a.getStringValue("name")))
         {
            table = a.getStringValue("name");
         }
         else if (!Strings.isNullOrEmpty(a.getStringValue()))
         {
            table = a.getStringValue();
         }
      }
      return table;
   }

   private List<JavaResource> selectTargets(final PipeOut out, Resource<?>[] targets)
            throws FileNotFoundException
   {
      List<JavaResource> results = new ArrayList<JavaResource>();
      if (targets == null)
      {
         targets = new Resource<?>[] {};
      }
      for (Resource<?> r : targets)
      {
         if (r instanceof JavaResource)
         {
            JavaSource<?> entity = ((JavaResource) r).getJavaSource();
            if (entity instanceof JavaClass)
            {
               if (entity.hasAnnotation(Entity.class))
               {
                  results.add((JavaResource) r);
               }
               else
               {
                  displaySkippingResourceMsg(out, entity);
               }
            }
            else
            {
               displaySkippingResourceMsg(out, entity);
            }
         }
      }
      return results;
   }

   private void displaySkippingResourceMsg(final PipeOut out, final JavaSource<?> entity)
   {
      if (!out.isPiped())
      {
         ShellMessages.info(out, "Skipped non-@Entity Java resource ["
                  + entity.getQualifiedName() + "]");
      }
   }
}
