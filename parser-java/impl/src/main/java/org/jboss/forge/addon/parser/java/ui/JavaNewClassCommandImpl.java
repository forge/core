/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import java.io.Serializable;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaNewClassCommandImpl extends AbstractJavaSourceCommand<JavaClassSource>implements JavaNewClassCommand
{
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   @WithAttributes(name = "final", label = "Is Final?")
   private UIInput<Boolean> isFinal;

   @Inject
   @WithAttributes(name = "abstract", label = "Is Abstract?")
   private UIInput<Boolean> isAbstract;

   @Inject
   @WithAttributes(name = "serializable", label = "Is Serializable?")
   private UIInput<Boolean> serializable;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      builder.add(isFinal).add(isAbstract).add(serializable);
   }

   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.setFinal(isFinal.getValue()).setAbstract(isAbstract.getValue());
      if (serializable.getValue())
      {
         source.addInterface(Serializable.class);
         // TODO: Add serialVersionUID?
      }
      return source;
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      super.validate(validator);
      if (isFinal.getValue() && isAbstract.getValue())
      {
         validator.addValidationError(isFinal, "Class cannot be both abstract and final");
      }
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

   @Override
   protected String getType()
   {
      return "Class";
   }
}
