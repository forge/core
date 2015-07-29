package org.jboss.forge.addon.javaee.security.ui;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.security.TransportGuarantee;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Adds a security constraint to a web resource collection.
 *
 * @author <a href="mailto:ivan.st.ivanov@gmail.com">Ivan St. Ivanov</a>
 */
@FacetConstraint(ServletFacet.class)
public class AddSecurityConstraintCommand extends AbstractJavaEECommand
{

   @Inject
   @WithAttributes(label = "Display name", shortName = 'n', description = "The display name of this security constraint")
   private UIInput<String> displayName;

   @Inject
   @WithAttributes(label = "Web resource name", shortName = 'w', required = true, description = "The collective name of the web resources covered by this constraint")
   private UIInput<String> webResourceName;

   @Inject
   @WithAttributes(label = "Description", shortName = 'd', description = "Short description of this web resource collection")
   private UIInput<String> description;

   @Inject
   @WithAttributes(label = "URL patterns", shortName = 'u', required = true, description = "The URL patterns that will be covered by ths web resource collection")
   private UIInputMany<String> urlPatterns;

   @Inject
   @WithAttributes(label = "HTTP methods", shortName = 'h', description = "The HTTP methods that will be covered by ths web resource collection")
   private UIInputMany<String> httpMethods;

   @Inject
   @WithAttributes(label = "Security roles", shortName = 'r', description = "The roles permitted to perform the constrained requests")
   private UIInputMany<String> securityRoles;

   @Inject
   @WithAttributes(label = "Enable user data constraint", shortName = 'u', description = "Establish a requirement to access the constrained request over a protected transport layer")
   private UIInput<Boolean> enableUserDataConstraint;

   @Inject
   @WithAttributes(label = "Transport guarantee", shortName = 't', description = "The type of requirement for used data constraint")
   private UIInput<TransportGuarantee> transportGuarantee;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      ServletFacet<?> servletFacet = getSelectedProject(builder.getUIContext()).getFacet(ServletFacet.class);
      final List<String> roles = servletFacet.getSecurityRoles();
      httpMethods.setCompleter(new UICompleter<String>()
      {
         @Override public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input,
                  String value)
         {
            return Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS");
         }
      });
      securityRoles.setCompleter(new UICompleter<String>()
      {
         @Override public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input,
                  String value)
         {
            return roles;
         }
      });
      enableUserDataConstraint.setDefaultValue(false);
      transportGuarantee.setEnabled(new Callable<Boolean>()
      {
         @Override public Boolean call() throws Exception
         {
            return enableUserDataConstraint.getValue();
         }
      });
      builder.add(displayName).add(webResourceName).add(description).add(urlPatterns).add(httpMethods)
               .add(securityRoles).add(enableUserDataConstraint).add(transportGuarantee);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      ServletFacet servletFacet = getSelectedProject(context).getFacet(ServletFacet.class);

      if (securityRoles.getValue().iterator().hasNext())
      {
         createNonExistingRoles(servletFacet, securityRoles.getValue());
      }

      TransportGuarantee transportGuaranteeValue = null;
      if (enableUserDataConstraint.getValue())
      {
         transportGuaranteeValue = transportGuarantee.getValue();
      }

      servletFacet.addSecurityConstraint(displayName.getValue(), webResourceName.getValue(), description.getValue(),
               httpMethods.getValue(), urlPatterns.getValue(), securityRoles.getValue(), transportGuaranteeValue);
      return Results.success("Security constraint was added successfully");
   }

   private void createNonExistingRoles(ServletFacet<?> servletFacet, Iterable<String> securityRoles)
   {
      List<String> existingRoles = servletFacet.getSecurityRoles();
      for (String securityRole : securityRoles)
      {
         if (!existingRoles.contains(securityRole))
         {
            servletFacet.addSecurityRole(securityRole);
         }
      }
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass()).name("Security: Add Constraint")
               .description("Add security constraint")
               .category(Categories.create(super.getMetadata(context).getCategory().getName(), "Security"));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }
}
