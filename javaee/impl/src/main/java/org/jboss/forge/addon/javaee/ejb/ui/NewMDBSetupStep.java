/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ejb.EJBOperations;
import org.jboss.forge.addon.javaee.ejb.JMSDestinationType;
import org.jboss.forge.addon.javaee.jms.JMSFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@StackConstraint(EJBFacet.class)
public class NewMDBSetupStep extends AbstractJavaEECommand implements UIWizardStep
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("MDB Configuration")
               .description("Specify Message Driven Bean attributes for new EJB.")
               .category(Categories.create("EJB", "Message Driven Beans"));
   }

   @Inject
   EJBOperations operations;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   @WithAttributes(required = true, label = "JMS Version", defaultValue = "1.1")
   private UISelectOne<JMSFacet> jmsVersion;

   @Inject
   @WithAttributes(label = "JMS Destination Type", required = true)
   private UISelectOne<JMSDestinationType> destType;

   @Inject
   @WithAttributes(label = "JMS Destination Name", required = true)
   private UIInput<String> destName;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      destType.setDefaultValue(JMSDestinationType.QUEUE);
      Project project = getSelectedProject(builder);
      if (project.hasFacet(JMSFacet.class))
      {
         jmsVersion.setEnabled(false).setValue(project.getFacet(JMSFacet.class));
      }
      builder.add(jmsVersion).add(destName).add(destType);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      List<Result> results = new ArrayList<>();
      if (jmsVersion.isEnabled() && facetFactory.install(getSelectedProject(context), jmsVersion.getValue()))
      {
         results.add(Results.success("JMS has been installed."));
      }
      JavaResource ejbResource = (JavaResource) context.getUIContext().getAttributeMap().get(JavaResource.class);
      JavaClassSource ejb = operations.setupMessageDrivenBean((JavaClassSource) ejbResource.getJavaType(),
               destType.getValue(),
               destName.getValue());
      ejbResource.setContents(ejb);
      results.add(Results.success("Configured Message Driven EJB."));
      return Results.aggregate(results);
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}
