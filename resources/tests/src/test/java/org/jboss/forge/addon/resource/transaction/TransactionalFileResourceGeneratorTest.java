/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.transaction;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.transaction.ChangeSet;
import org.jboss.forge.addon.resource.transaction.ResourceTransaction;
import org.jboss.forge.addon.resource.transaction.ResourceTransactionManager;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class TransactionalFileResourceGeneratorTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge.addon:facets", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:facets", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ResourceFactory factory;

   @Inject
   private ResourceTransactionManager transactionManager;

   @Test
   public void testTransactionManagerNotNull() throws Exception
   {
      Assert.assertNotNull(transactionManager);
   }

   @Test
   public void testTrackResourceCreation() throws Exception
   {
      ResourceTransaction transaction = transactionManager.startTransaction();
      Assert.assertNotNull(transaction);
      File underlyingResource = new File(UUID.randomUUID().toString());
      FileResource<?> resource = factory.create(underlyingResource).reify(FileResource.class);
      Assert.assertNotNull(resource);
      ChangeSet changeSet = transaction.getChangeSet();
      Assert.assertNotNull(changeSet);
      Set<Resource<?>> modifiedResources = changeSet.getModifiedResources();
      Assert.assertNotNull(modifiedResources);
      Assert.assertEquals(1, modifiedResources.size());
      Resource<?> changedResource = modifiedResources.iterator().next();
      Assert.assertTrue(changedResource instanceof FileResource);
      Assert.assertEquals(underlyingResource, changedResource.getUnderlyingResourceObject());
      transaction.commit();
      Assert.assertNull(transactionManager.getCurrentTransaction());
   }
}