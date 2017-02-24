/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet.ui;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * Creates a new Servlet
 *
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@StackConstraint(ServletFacet.class)
public class ServletNewServletCommand extends AbstractServletNewCommand<JavaClassSource>
{
   @Inject
   @WithAttributes(label = "Methods", description = "Servlet methods to be overridden")
   private UISelectMany<ServletMethod> methods;

   @Inject
   @WithAttributes(label = "Url patterns", description = "List of URL patterns the servlet will be mapped to")
   private UIInputMany<String> urlPatterns;

   @Inject
   private Inflector inflector;

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Servlet: New Servlet")
               .description("Creates a new Servlet");
   }

   @Override
   protected String getType()
   {
      return "Servlet";
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
      builder.add(methods).add(urlPatterns);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.setSuperType(HttpServlet.class);

      if (urlPatterns.hasValue())
      {
         List<String> list = Lists.toList(urlPatterns.getValue());
         String[] urlPatternsArray = list.toArray(new String[list.size()]);
         source.addAnnotation(WebServlet.class).setStringArrayValue("urlPatterns", urlPatternsArray);
      }
      else
      {
         source.addAnnotation(WebServlet.class).setStringArrayValue("urlPatterns",
                  new String[] { inflector.lowerCamelCase(getNamed().getValue()) });
      }

      for (ServletMethod method : methods.getValue())
      {
         MethodSource<?> doGet = source.addMethod().setProtected().setName(method.getMethodName()).setReturnTypeVoid();
         doGet.addParameter(HttpServletRequest.class, "request");
         doGet.addParameter(HttpServletResponse.class, "response");
         doGet.addThrows(ServletException.class).addThrows(IOException.class);
         doGet.setBody("response.getWriter().println(\"Method " + method.getMethodName() + " invoked\");")
                  .addAnnotation(
                           Override.class);
      }

      return source;
   }
}
