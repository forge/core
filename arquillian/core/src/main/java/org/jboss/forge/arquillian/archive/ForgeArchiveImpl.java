/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.arquillian.archive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeArchiveImpl extends ContainerBase<ForgeArchive> implements ForgeArchive
{
   // -------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(ForgeArchiveImpl.class.getName());

   /**
    * Path to the web inside of the Archive.
    */
   private static final ArchivePath PATH_ROOT = ArchivePaths.root();

   /**
    * Path to the classes inside of the Archive.
    */
   private static final ArchivePath PATH_CLASSES = ArchivePaths.create(PATH_ROOT, "");

   /**
    * Path to the libraries inside of the Archive.
    */
   private static final ArchivePath PATH_LIBRARY = ArchivePaths.create(PATH_ROOT, "lib");

   /**
    * Path to the manifests inside of the Archive.
    */
   private static final ArchivePath PATH_MANIFEST = ArchivePaths.create("META-INF");

   /**
    * Path to the forge XML config file inside of the Archive.
    */
   private static final ArchivePath PATH_FORGE_XML = ArchivePaths.create("META-INF/forge.xml");

   /**
    * Path to web archive service providers.
    */
   private static final ArchivePath PATH_SERVICE_PROVIDERS = ArchivePaths.create(PATH_CLASSES, "META-INF/services");

   private List<AddonDependencyEntry> addonDependencies = new ArrayList<AddonDependencyEntry>();

   // -------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   // -------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Create a new {@link ForgeArchive} with any type storage engine as backing.
    * 
    * @param delegate The storage backing.
    */
   public ForgeArchiveImpl(final Archive<?> delegate)
   {
      super(ForgeArchive.class, delegate);
   }

   // -------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getManifestPath()
    */
   @Override
   protected ArchivePath getManifestPath()
   {
      return PATH_MANIFEST;
   }

   protected ArchivePath getForgeXMLPath()
   {
      return PATH_FORGE_XML;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getClassesPath()
    */
   @Override
   protected ArchivePath getClassesPath()
   {
      return PATH_CLASSES;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getLibraryPath()
    */
   @Override
   protected ArchivePath getLibraryPath()
   {
      return PATH_LIBRARY;
   }

   protected ArchivePath getServiceProvidersPath()
   {
      return PATH_SERVICE_PROVIDERS;
   }

   @Override
   protected ArchivePath getResourcePath()
   {
      return PATH_CLASSES;
   }

   @Override
   public ForgeArchive setAsForgeXML(final Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return add(resource, getForgeXMLPath());
   }

   @Override
   public ForgeArchive addAsAddonDependencies(AddonDependencyEntry... dependencies)
   {
      if (dependencies != null)
         addonDependencies.addAll(Arrays.asList(dependencies));
      return this;
   }

   @Override
   public List<AddonDependencyEntry> getAddonDependencies()
   {
      return addonDependencies;
   }

   @Override
   public ForgeArchive addBeansXML()
   {
      addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
      return this;
   }

   @Override
   public ForgeArchive addBeansXML(Asset resource)
   {
      addAsManifestResource(resource, ArchivePaths.create("beans.xml"));
      return this;
   }
}
