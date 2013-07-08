package org.jboss.forge.arquillian.archive;

import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.shrinkwrap.api.Archive;

/**
 * Archive representing a Furnace AddonDependency deployment.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ForgeRemoteAddon extends Archive<ForgeRemoteAddon>
{
   AddonId getAddonId();

   public ForgeRemoteAddon setAddonId(AddonId id);
}
