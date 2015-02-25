/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.archetype;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class EmptyArchetypeRegistryTest
{

    @Deployment
    @AddonDeployments({
                   @AddonDeployment(name = "org.jboss.forge.addon:projects"),
                   @AddonDeployment(name = "org.jboss.forge.addon:maven")
    })
    public static ForgeArchive getDeployment()
    {
        ForgeArchive archive = ShrinkWrap
            .create(ForgeArchive.class)
            .addBeansXML()
            .addAsAddonDependencies(
                                    AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                                    AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                                    AddonDependencyEntry.create("org.jboss.forge.addon:projects")
            );

        return archive;
    }

    @Inject
    private ArchetypeCatalogFactoryRegistry archetypeRegistry;

    @Test
    public void testDoesNotHaveArchetypeCatalogFactories() {
        Assert.assertFalse(archetypeRegistry.hasArchetypeCatalogFactories());
    }
}
