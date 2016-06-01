/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.mock.command;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ResourceTestCommand extends AbstractUICommand
{
   @Inject
   private UIInput<FileResource<?>> singleFileResource;

   @Inject
   private UIInputMany<FileResource<?>> manyFileResource;

   @Inject
   private UIInput<JavaResource> javaResource;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(singleFileResource).add(manyFileResource).add(javaResource);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("resourcecommand");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIOutput output = context.getUIContext().getProvider().getOutput();
      output.info(output.out(), "Single File Resource: " + singleFileResource.getValue());
      output.info(output.out(), "Many File Resource: " + manyFileResource.getValue());
      output.info(output.out(), "Java Resource: " + javaResource.getValue());
      return Results.success();
   }
}