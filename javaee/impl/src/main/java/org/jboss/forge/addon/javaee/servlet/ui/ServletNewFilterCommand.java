/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet.ui;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;

/**
 * Creates a new Servlet
 *
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@StackConstraint(ServletFacet.class)
public class ServletNewFilterCommand extends AbstractServletNewCommand<JavaClassSource>
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Servlet: New Filter")
               .description("Creates a new Servlet Filter");
   }

   @Override
   protected String getType()
   {
      return "Servlet Filter";
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
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.addInterface(Filter.class).addAnnotation(WebFilter.class);

      MethodSource<?> init = source.addMethod().setPublic().setName("init").setReturnTypeVoid();
      init.addParameter(FilterConfig.class, "filterConfig");
      init.addThrows(ServletException.class);
      init.setBody("").addAnnotation(Override.class);

      MethodSource<?> doFilter = source.addMethod().setPublic().setName("doFilter").setReturnTypeVoid();
      doFilter.addParameter(ServletRequest.class, "request");
      doFilter.addParameter(ServletResponse.class, "response");
      doFilter.addParameter(FilterChain.class, "chain");
      doFilter.addThrows(IOException.class);
      doFilter.addThrows(ServletException.class);
      doFilter.setBody("").addAnnotation(Override.class);

      MethodSource<?> destroy = source.addMethod().setPublic().setName("destroy").setReturnTypeVoid();
      destroy.setBody("").addAnnotation(Override.class);

      return source;
   }
}
