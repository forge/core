package org.jboss.forge.arquillian.archive;

import java.util.List;

import org.jboss.forge.container.AddonDependency;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;

/**
 * Archive representing a Forge RegisteredAddon deployment.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ForgeArchive extends Archive<ForgeArchive>, LibraryContainer<ForgeArchive>,
         ResourceContainer<ForgeArchive>, ServiceProviderContainer<ForgeArchive>
{
   /**
    * Sets the current forge.xml descritor for this archive.
    */
   ForgeArchive setAsForgeXML(Asset resource) throws IllegalArgumentException;

   /**
    * Adds the given {@link Dependencies} instances as addon module dependencies for this test deployment.
    */
   ForgeArchive addAsAddonDependencies(AddonDependency... dependencies);

   /**
    * Get the currently specified {@link Dependencies} instances for this addon test deployment.
    */
   List<AddonDependency> getAddonDependencies();
}
