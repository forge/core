/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * Creates a new REST Endpoint
 *
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class RestNewEndpointCommandImpl extends AbstractRestNewCommand<JavaClassSource>
         implements RestNewEndpointCommand
{
   @Inject
   @WithAttributes(label = "Methods", description = "REST methods to be defined", defaultValue = "GET")
   private UISelectMany<RestMethod> methods;

   @Inject
   @WithAttributes(label = "Path", description = "The root path of the endpoint")
   private UIInput<String> path;

   @Inject
   private Inflector inflector;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("REST: New Endpoint")
               .description("Creates a new REST Endpoint");
   }

   @Override
   protected String getType()
   {
      return "REST";
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
      builder.add(methods).add(path);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      if (path.hasValue())
      {
         source.addAnnotation(Path.class).setStringValue("/" + path.getValue());
      }
      else
      {
         source.addAnnotation(Path.class).setStringValue("/" + inflector.lowerCamelCase(getNamed().getValue().replace(
                  "Endpoint", "")));
      }
      for (RestMethod method : methods.getValue())
      {
         MethodSource<?> doGet = source.addMethod().setPublic().setName(method.getMethodName())
                  .setReturnType("javax.ws.rs.core.Response");
         doGet.addAnnotation(method.getMethodAnnotation());

         switch (method)
         {
         case GET:
            doGet.addAnnotation(javax.ws.rs.Produces.class).setStringArrayValue(new String[] { MediaType.TEXT_PLAIN });
            doGet.setBody("return Response.ok(\"method " + method.getMethodName() + " invoked\").build();");
            break;
         case POST:
            source.addImport(UriBuilder.class);
            doGet.addAnnotation(javax.ws.rs.Consumes.class).setStringArrayValue(
                     new String[] { MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON });
            doGet.addParameter(String.class, "entity");
            doGet.setBody("return Response.created(UriBuilder.fromResource(" + getNamed().getValue()
                     + ".class).build()).build();");
            break;
         case PUT:
            source.addImport(UriBuilder.class);
            doGet.addAnnotation(javax.ws.rs.Consumes.class).setStringArrayValue(
                     new String[] { MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON });
            doGet.addParameter(String.class, "entity");
            doGet.setBody("return Response.created(UriBuilder.fromResource(" + getNamed().getValue()
                     + ".class).build()).build();");
            break;
         case DELETE:
            doGet.addAnnotation(javax.ws.rs.Path.class).setStringValue("/{id}");
            doGet.addParameter(Long.class, "id").addAnnotation(PathParam.class).setStringValue("id");
            doGet.setBody("return Response.noContent().build();");
            break;
         }
      }

      return source;
   }

   @Override
   public UISelectMany<RestMethod> getMethods()
   {
      return methods;
   }

   @Override
   public UIInput<String> getPath()
   {
      return path;
   }
}
