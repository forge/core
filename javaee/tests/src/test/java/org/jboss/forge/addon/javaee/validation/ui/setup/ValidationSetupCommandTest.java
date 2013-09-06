package org.jboss.forge.addon.javaee.validation.ui.setup;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.descriptor.ValidationDescriptor;
import org.jboss.forge.addon.javaee.facets.ValidationFacet;
import org.jboss.forge.addon.javaee.validation.providers.JavaEEValidatorProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.CommandListener;
import org.jboss.forge.ui.test.CommandTester;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ValidationSetupCommandTest
{

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );
   }

   @Inject
   private JavaEEValidatorProvider defaultProvider;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private CommandTester<ValidationProviderSetupCommand> tester;

   @Test
   public void testNewEntity() throws Exception
   {
      // Execute SUT
      final Project project = projectFactory.createTempProject();
      tester.setInitialSelection(project.getProjectRoot());

      Assert.assertTrue(tester.canExecute());
      // Setting UI values
      tester.setValueFor("providers", defaultProvider);
      Assert.assertTrue(tester.canExecute());

      final AtomicBoolean flag = new AtomicBoolean();
      tester.execute(new CommandListener()
      {
         @Override
         public void commandExecuted(UICommand command, Result result)
         {
            if(result.getMessage().equals("Bean Validation is installed."))
            {
               flag.set(true);
            }
         }
      });
      // Ensure that the two pages were invoked
      Assert.assertEquals(true, flag.get());   }
}
