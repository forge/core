package org.jboss.forge.ui;

import java.util.Set;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.forge.container.services.RemoteInstance;
import org.jboss.forge.container.services.ServiceRegistry;
import org.jboss.forge.ui.wizard.UIWizard;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class UIWizardDiscoveryTest
{
   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addClasses(MyFirstWizard.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private ServiceRegistry registry;

   @Test
   public void testWizardRegisteredWithoutRemote() throws Exception
   {
      Set<RemoteInstance<UIWizard>> wizards = registry.getRemoteInstances(UIWizard.class);
      Assert.assertFalse(wizards.isEmpty());
      RemoteInstance<UIWizard> instance = wizards.iterator().next();
      Assert.assertNotNull(instance);
      UIWizard uiWizard = instance.get();
      Assert.assertNotNull(uiWizard);
   }

}