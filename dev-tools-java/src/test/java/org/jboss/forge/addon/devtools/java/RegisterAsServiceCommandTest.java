package org.jboss.forge.addon.devtools.java;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RegisterAsServiceCommandTest
{
   @Deployment
   @Dependencies({
            // FIXME remove hard-coded version
            @AddonDependency(name = "org.jboss.forge.addon:dev-tools-java", version = "2.0.0-SNAPSHOT"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:dev-tools-java")
               );

      return archive;
   }

   @Inject
   private RegisterAsServiceCommand command;

   @Test
   public void testCommandInjection() throws Exception
   {
      Assert.assertNotNull(command);
      Assert.assertNotNull(command.toString());
   }
}