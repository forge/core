/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessBeanAttributes;
import javax.enterprise.inject.spi.ProcessInjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessObserverMethod;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.enterprise.inject.spi.WithAnnotations;
import javax.inject.Inject;

import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.javaee.cdi.CDIOperations;
import org.jboss.forge.addon.javaee.cdi.ui.input.Qualifiers;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.ParameterSource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * @author Martin Kouba
 */
public class CDIAddObserverMethodCommand extends AbstractMethodCDICommand
{

   @Inject
   @WithAttributes(label = "Event Type", description = "The event type of the created method", type = InputType.JAVA_CLASS_PICKER, required = true)
   private UIInput<String> eventType;

   @Inject
   @WithAttributes(label = "Container Lifecyle Event Type", description = "The event type of the created method", type = InputType.DROPDOWN, required = true)
   private UISelectOne<String> containerLifecyleEventType;

   @Inject
   private Qualifiers qualifiers;

   @Inject
   private CDIOperations cdiOperations;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      super.initializeUI(builder);
      eventType.setEnabled(() -> !isTargetClassExtension());
      containerLifecyleEventType.setEnabled(() -> isTargetClassExtension());
      qualifiers.setEnabled(() -> !isTargetClassExtension());
      setupEventType();
      setupContainerLifecycleEventTypes(builder);
      builder.add(eventType).add(containerLifecyleEventType).add(qualifiers);
   }

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("CDI: Add Observer Method")
               .description("Adds a new observer method to a bean");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource javaResource = targetClass.getValue();
      JavaClassSource javaClass = javaResource.getJavaType();
      String eventParamType = getEventType();
      ParameterSource<JavaClassSource> parameter = javaClass.addMethod().setVisibility(accessType.getValue())
               .setReturnTypeVoid()
               .setName(named.getValue())
               .setBody("")
               .addParameter(eventParamType, "event");
      parameter.addAnnotation(Observes.class);
      if (getSelectedProject(context).hasFacet(CDIFacet_1_1.class)
               && eventParamType.equals(ProcessAnnotatedType.class.getName() + "<?>"))
      {
         parameter.addAnnotation(WithAnnotations.class).setClassArrayValue();
      }
      for (String qualifier : qualifiers.getValue())
      {
         parameter.addAnnotation(qualifier);
      }
      javaResource.setContents(javaClass);
      return Results.success();
   }

   @Override
   protected Visibility getDefaultVisibility()
   {
      return Visibility.PACKAGE_PRIVATE;
   }

   @Override
   protected String[] getParamTypes()
   {

      return new String[] { getEventType() };
   }

   private String getEventType()
   {
      return eventType.isEnabled() ? eventType.getValue() : containerLifecyleEventType.getValue();
   }

   private void setupEventType()
   {
      eventType.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(final UIContext context, final InputComponent<?, String> input,
                  final String value)
         {
            final Project project = getSelectedProject(context);
            final List<String> options = new ArrayList<>();
            if (project != null)
            {
               for (JavaResource resource : cdiOperations.getProjectEventTypes(project))
               {
                  try
                  {
                     JavaSource<?> javaSource = resource.getJavaType();
                     String qualifiedName = javaSource.getQualifiedName();
                     if (Strings.isNullOrEmpty(value) || qualifiedName.startsWith(value))
                     {
                        options.add(qualifiedName);
                     }
                  }
                  catch (FileNotFoundException ignored)
                  {
                  }
               }
            }
            return options;
         }
      });
   }

   private void setupContainerLifecycleEventTypes(UIBuilder builder)
   {
      List<String> values = new ArrayList<>();
      values.add(BeforeBeanDiscovery.class.getSimpleName());
      if (getSelectedProject(builder).hasFacet(CDIFacet_1_1.class))
      {
         values.add(AfterTypeDiscovery.class.getSimpleName());
      }
      values.add(AfterBeanDiscovery.class.getSimpleName());
      values.add(AfterDeploymentValidation.class.getSimpleName());
      values.add(BeforeShutdown.class.getSimpleName());
      values.add(ProcessAnnotatedType.class.getSimpleName());
      values.add(ProcessInjectionPoint.class.getSimpleName());
      values.add(ProcessInjectionTarget.class.getSimpleName());
      if (getSelectedProject(builder).hasFacet(CDIFacet_1_1.class))
      {
         values.add(ProcessBeanAttributes.class.getSimpleName());
      }
      values.add(ProcessBean.class.getSimpleName());
      values.add(ProcessProducer.class.getSimpleName());
      values.add(ProcessObserverMethod.class.getSimpleName());
      containerLifecyleEventType.setValueChoices(values)
               .setValueConverter(this::convertContainerLifecycleEventType);
   }

   private String convertContainerLifecycleEventType(String value)
   {
      if (value.equals(ProcessAnnotatedType.class.getSimpleName())
               || value.equals(ProcessInjectionTarget.class.getSimpleName())
               || value.equals(ProcessBeanAttributes.class.getSimpleName())
               || value.equals(ProcessBean.class.getSimpleName()))
      {
         value += "<?>";
      }
      else if (value.equals(ProcessInjectionPoint.class.getSimpleName())
               || value.equals(ProcessProducer.class.getSimpleName())
               || value.equals(ProcessObserverMethod.class.getSimpleName()))
      {
         value += "<?,?>";
      }
      return BeforeBeanDiscovery.class.getPackage().getName() + "." + value;
   }

   private boolean isTargetClassExtension()
   {
      try
      {
         if (targetClass.hasValue() && targetClass.getValue().getJavaType().isClass())
         {
            JavaClass<?> javaClass = targetClass.getValue().getJavaType();
            return javaClass.hasInterface(Extension.class);
         }
      }
      catch (FileNotFoundException ignored)
      {
      }
      return false;
   }

}
