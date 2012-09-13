package org.jboss.forge.arquillian.archive;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;

/**
 * Traditional WAR (Java Web Archive) structure. Used in construction of web applications.
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface ForgeArchive extends Archive<ForgeArchive>, LibraryContainer<ForgeArchive>,
         ResourceContainer<ForgeArchive>, ServiceProviderContainer<ForgeArchive>
{
}
