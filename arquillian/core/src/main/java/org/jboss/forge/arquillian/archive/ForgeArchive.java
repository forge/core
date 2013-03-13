package org.jboss.forge.arquillian.archive;

import java.util.List;

import org.jboss.forge.container.addons.AddonDependency;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;

/**
 * Archive representing a Forge Addon deployment.
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
    * Adds the given {@link AddonDependency} instances as addon module dependencies for this test deployment.
    */
   ForgeArchive addAsAddonDependencies(AddonDependency... dependencies);

   /**
    * Get the currently specified {@link AddonDependency} instances for this addon test deployment.
    */
   List<AddonDependency> getAddonDependencies();

   /**
    * Adds an empty beans.xml file in this archive
    */
   ForgeArchive addBeansXML();

   /**
    * Adds an beans.xml file in this archive with the specified content
    */
   ForgeArchive addBeansXML(Asset resource);
}
