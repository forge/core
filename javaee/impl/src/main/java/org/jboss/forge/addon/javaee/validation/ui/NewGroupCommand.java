/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.parser.java.ui.AbstractJavaSourceCommand;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;

/**
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class NewGroupCommand extends AbstractJavaSourceCommand
{

  @Override
  public Metadata getMetadata(UIContext context)
  {
    return Metadata.from(super.getMetadata(context), getClass())
            .name("Constraint: New Group")
            .description("Create a Bean Validation group")
            .category(Categories.create(super.getMetadata(context).getCategory(), "Bean Validation"));
  }


  @Override
  protected String getType() {
    return "Bean Validation Group";
  }

  @Override
  protected Class<? extends JavaSource<?>> getSourceType()
  {
    return JavaInterface.class;
  }

  @Override
  public Result execute(UIExecutionContext context) throws Exception
  {
    Result result = super.execute(context);
    if (!(result instanceof Failed))
    {
      JavaSourceFacet javaSourceFacet = getSelectedProject(context).getFacet(JavaSourceFacet.class);
      JavaResource javaResource = context.getUIContext().getSelection();
      JavaSource<?> javaSource = javaResource.getJavaSource();
      javaSourceFacet.saveJavaSource(javaSource);
    }
    return result;
  }

  @Override
  protected boolean isProjectRequired()
  {
    return true;
  }

  @Override
  protected String calculateDefaultPackage(UIContext context)
  {
    return getSelectedProject(context).getFacet(MetadataFacet.class).getTopLevelPackage() + ".constraints";
  }
}