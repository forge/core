/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.util.concurrent.Callable;

import javax.annotation.Priority;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author Martin Kouba
 */
public abstract class AbstractEnablementCDICommand extends AbstractCDICommand<JavaClassSource>
{

   @Inject
   @WithAttributes(label = "Enabled", description = "Enables for the bean archive (adds to beans.xml)")
   private UIInput<Boolean> enabled;

   @Inject
   @WithAttributes(label = "Priority", description = "Enables globally (for the application)")
   private UIInput<Integer> priority;

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   // Note that enablement should be initialized last
   protected void initializeEnablementUI(UIBuilder builder) {
      enabled.setEnabled(hasEnablement());
      if (getSelectedProject(builder).hasFacet(CDIFacet_1_1.class))
      {
         priority.setEnabled(hasEnablement());
         builder.add(priority);
      }
      else
      {
         priority.setEnabled(false);
      }
      builder.add(enabled);
   }

   protected Callable<Boolean> hasEnablement()
   {
      return () -> true;
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project,
            JavaClassSource source) throws Exception
   {
      if (enabled.getValue())
      {
         // TODO: Create a parent BeansDescriptor
         enable(project.getFacet(CDIFacet.class), source);
      }
      if (priority.isEnabled() && priority.hasValue())
      {
         source.addAnnotation(Priority.class).setLiteralValue(String.valueOf(priority.getValue()));
      }
      return source;
   }

   protected abstract void enable(CDIFacet<?> facet, JavaClassSource source);

   protected void checkEnablementConflict(UIValidationContext validator, String message)
   {
      if (enabled.getValue() && (priority.isEnabled() && priority.hasValue()))
      {
         validator.addValidationWarning(priority, message);
      }
   }

}
