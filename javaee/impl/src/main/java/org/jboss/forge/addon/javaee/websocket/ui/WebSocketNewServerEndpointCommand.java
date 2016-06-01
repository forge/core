/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.websocket.ui;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * Creates a new Web Socket Server Endpoint
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class WebSocketNewServerEndpointCommand extends AbstractWebsocketCommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "uri")
   private UIInput<String> uri;

   @Inject
   @WithAttributes(label = "Methods")
   private UISelectMany<WebSocketMethodType> methods;

   @Inject
   private Inflector inflector;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("WebSocket: New Server Endpoint")
               .description("Create a new WebSocket Server Endpoint");
   }

   @Override
   protected String getType()
   {
      return "WebSocket Server Endpoint";
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
      builder.add(uri).add(methods);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {

      // Class
      if (uri.hasValue())
      {
         source.addAnnotation(ServerEndpoint.class).setStringValue("/" + uri.getValue());
      }
      else
      {
         source.addAnnotation(ServerEndpoint.class)
                  .setStringValue("/" + inflector.lowerCamelCase(getNamed().getValue().replace("Endpoint", "")));
      }

      // Methods
      for (WebSocketMethodType method : methods.getValue())
      {
         MethodSource<?> wsMethod = source.addMethod().setPublic()
                  .setName(inflector.lowerCamelCase(method.getAnnotation().getSimpleName()))
                  .setReturnTypeVoid().setBody("");
         wsMethod.addAnnotation(method.getAnnotation());

         Class[] parameters = method.getParameters();
         for (Class parameter : parameters)
         {
            wsMethod.addParameter(parameter, inflector.lowerCamelCase(parameter.getSimpleName()));
         }
      }
      return source;
   }
}
