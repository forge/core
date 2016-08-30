/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest.ui;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * Creates a Cross Origin Resource Sharing Filter
 *
 * @see https://issues.jboss.org/browse/FORGE-1929
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CrossOriginResourceSharingFilterCommand extends AbstractRestNewCommand<JavaClassSource>
{

   @Inject
   @WithAttributes(label = "Access-Control-Allow-Origin", defaultValue = "*", description = "The Access-Control-Allow-Origin header indicates whether a resource can be shared based by returning the value of the Origin request header, \"*\", or \"null\" in the response")
   private UIInput<String> accessControlAllowOrigin;

   @Inject
   @WithAttributes(label = "Access-Control-Allow-Methods", description = "The Access-Control-Allow-Methods header indicates, as part of the response to a preflight request, which methods can be used during the actual request.")
   private UISelectMany<String> accessControlAllowMethods;

   @Inject
   @WithAttributes(label = "Access-Control-Allow-Headers", description = "The Access-Control-Allow-Headers header indicates, as part of the response to a preflight request, which header field names can be used during the actual request")
   private UIInputMany<String> accessControlAllowHeaders;

   @Inject
   @WithAttributes(label = "Access-Control-Allow-Credentials", defaultValue = "true", description = "The Access-Control-Allow-Credentials header indicates whether the response to request can be exposed when the omit credentials flag is unset. When part of the response to a preflight request it indicates that the actual request can include user credentials.")
   private UIInput<Boolean> accessControlAllowCredentials;

   // @Inject
   // @WithAttributes(label = "Access-Control-Expose-Headers", description =
   // "The Access-Control-Expose-Headers header indicates which headers are safe to expose to the API of a CORS API
   // specification.")
   // private UIInputMany<String> accessControlExposeHeaders;
   //
   // @Inject
   // @WithAttributes(label = "Access-Control-Max-Age", defaultValue = "151200", description =
   // "The Access-Control-Max-Age header indicates how long the results of a preflight request can be cached in a
   // preflight result cache.")
   // private UIInput<Integer> accessControlMaxAge;
   //
   // @Inject
   // @WithAttributes(label = "Origin", description =
   // "The Origin header indicates where the cross-origin request or preflight request originates from.")
   // private UIInput<String> origin;
   //
   // @Inject
   // @WithAttributes(label = "Access-Control-Request-Method", description =
   // "The Access-Control-Request-Method header indicates which method will be used in the actual request as part of the
   // preflight request")
   // private UIInput<String> accessControlRequestMethod;
   //
   // @Inject
   // @WithAttributes(label = "Access-Control-Request-Headers", description =
   // "The Access-Control-Request-Headers header indicates which headers will be used in the actual request as part of
   // the preflight request")
   // private UIInputMany<String> accessControlRequestHeaders;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      getNamed().setDefaultValue("NewCrossOriginResourceSharingFilter");
      accessControlAllowHeaders.setValue(Arrays.asList("Content-Type", "User-Agent", "X-Requested-With",
               "X-Requested-By", "Cache-Control"));
      accessControlAllowMethods.setValueChoices(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
               HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
      accessControlAllowMethods.setValue(Arrays.asList(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT,
               HttpMethod.DELETE));
      builder.add(accessControlAllowMethods).add(accessControlAllowHeaders)
               .add(accessControlAllowOrigin).add(accessControlAllowCredentials);
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("REST: New " + getType())
               .description("Generate a " + getType());
   }

   @Override
   protected String getType()
   {
      return "Cross Origin Resource Sharing Filter";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.addAnnotation(Provider.class);
      source.addInterface(ContainerResponseFilter.class);
      MethodSource<JavaClassSource> method = source.addMethod().setName("filter").setPublic().setReturnTypeVoid();
      method.addAnnotation(Override.class);
      method.addParameter(ContainerRequestContext.class, "request");
      method.addParameter(ContainerResponseContext.class, "response");
      StringBuilder body = new StringBuilder();
      {
         body.append("response.getHeaders().putSingle(\"Access-Control-Allow-Origin\",\"").append(
                  accessControlAllowOrigin.getValue()).append("\");");
      }
      body.append(OperatingSystemUtils.getLineSeparator());
      {
         body.append("response.getHeaders().putSingle(\"Access-Control-Expose-Headers\",\"").append("Location")
                  .append("\");");
      }
      body.append(OperatingSystemUtils.getLineSeparator());
      {
         body.append("response.getHeaders().putSingle(\"Access-Control-Allow-Methods\",\"");
         List<String> list = Lists.toList(accessControlAllowMethods.getValue());
         body.append(Strings.join(list.toArray(), ", "));
         body.append("\");");
      }
      body.append(OperatingSystemUtils.getLineSeparator());
      {
         body.append("response.getHeaders().putSingle(\"Access-Control-Allow-Headers\",\"");
         List<String> list = Lists.toList(accessControlAllowHeaders.getValue());
         body.append(Strings.join(list.toArray(), ", "));
         body.append("\");");
      }
      if (accessControlAllowCredentials.getValue())
      {
         body.append("response.getHeaders().putSingle(\"Access-Control-Allow-Credentials\",\"true\");");

      }
      method.setBody(body.toString());
      return source;
   }
}
