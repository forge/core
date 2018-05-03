/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.controller.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class MethodCountCommand implements UICommand
{

   private Map<String, AtomicInteger> counts = new HashMap<>();

   @Inject
   private UIInput<String> firstName;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      counts.computeIfAbsent("initializeUI", k -> new AtomicInteger()).incrementAndGet();
      builder.add(firstName);
   }

   @Override
   public void validate(UIValidationContext context)
   {
      counts.computeIfAbsent("validate", k -> new AtomicInteger()).incrementAndGet();
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      counts.computeIfAbsent("execute", k -> new AtomicInteger()).incrementAndGet();
      return Results.success("Hello, " + firstName.getValue());
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      counts.computeIfAbsent("getMetadata", k -> new AtomicInteger()).incrementAndGet();
      return Metadata.forCommand(getClass()).description("generic test command")
               .category(Categories.create("Example"));
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      counts.computeIfAbsent("isEnabled", k -> new AtomicInteger()).incrementAndGet();
      return true;
   }

   /**
    * @return the counts
    */
   public Map<String, AtomicInteger> getCounts()
   {
      return counts;
   }

}
