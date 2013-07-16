/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.maven.addon;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.settings.Settings;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.jboss.forge.addon.manager.spi.AddonDependencyResolver;
import org.jboss.forge.addon.manager.spi.AddonInfo;
import org.jboss.forge.addon.maven.dependencies.AddonDependencySelector;
import org.jboss.forge.addon.maven.dependencies.MavenContainer;
import org.jboss.forge.furnace.addons.AddonId;

/**
 * Maven implementation of the {@link AddonDependencyResolver} used by the AddonManager
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class MavenAddonDependencyResolver implements AddonDependencyResolver
{
   private static final String FORGE_ADDON_CLASSIFIER = "forge-addon";
   private final MavenContainer container = new MavenContainer();

   @Override
   public AddonInfo resolveAddonDependencyHierarchy(AddonId addonId)
   {
      String coords = toMavenCoords(addonId);
      RepositorySystem system = container.getRepositorySystem();
      Settings settings = container.getSettings();
      DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);

      DependencyNode dependencyNode = traverseAddonGraph(coords, system, settings, session);
      return fromNode(addonId, dependencyNode, system, settings, session);
   }

   @Override
   public File[] resolveResources(final AddonId addonId)
   {
      RepositorySystem system = container.getRepositorySystem();
      Settings settings = container.getSettings();
      DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);
      final String mavenCoords = toMavenCoords(addonId);
      Artifact queryArtifact = new DefaultArtifact(mavenCoords);
      session.setDependencyTraverser(new DependencyTraverser()
      {
         @Override
         public boolean traverseDependency(Dependency dependency)
         {
            Artifact artifact = dependency.getArtifact();
            boolean isForgeAddon = "forge-addon".equals(artifact.getClassifier());
            // We don't want to traverse non-addons optional dependencies
            if (!isForgeAddon && dependency.isOptional())
            {
               return false;
            }
            boolean shouldRecurse = !"test".equals(dependency.getScope());
            return shouldRecurse;
         }

         @Override
         public DependencyTraverser deriveChildTraverser(DependencyCollectionContext context)
         {
            return this;
         }
      });
      session.setDependencySelector(new AddonDependencySelector());
      Dependency dependency = new Dependency(queryArtifact, null);
      CollectRequest collectRequest = new CollectRequest(dependency,
               container.getEnabledRepositoriesFromProfile(settings));
      DependencyResult result;
      try
      {
         result = system.resolveDependencies(session, new DependencyRequest(collectRequest, null));
      }
      catch (DependencyResolutionException e)
      {
         throw new RuntimeException(e);
      }
      Set<File> files = new HashSet<File>();
      List<ArtifactResult> artifactResults = result.getArtifactResults();
      for (ArtifactResult artifactResult : artifactResults)
      {
         Artifact artifact = artifactResult.getArtifact();
         if (FORGE_ADDON_CLASSIFIER.equals(artifact.getClassifier())
                  && !mavenCoords.equals(artifact.toString()))
         {
            continue;
         }
         files.add(artifact.getFile());
      }
      return files.toArray(new File[files.size()]);
   }

   @Override
   public AddonId[] resolveVersions(final String addonName)
   {
      String addonNameSplit;
      String version;

      String[] split = addonName.split(",");
      if (split.length == 2)
      {
         addonNameSplit = split[0];
         version = split[1];
      }
      else
      {
         addonNameSplit = addonName;
         version = null;
      }

      VersionRangeResult versions = getVersions(addonNameSplit, version);
      List<Version> versionsList = versions.getVersions();
      int size = versionsList.size();
      AddonId[] addons = new AddonId[size];
      for (int i = 0; i < size; i++)
      {
         addons[i] = AddonId.from(addonName, versionsList.get(i).toString());
      }
      return addons;
   }

   private VersionRangeResult getVersions(String addonName, String version)
   {
      try
      {
         String[] split = addonName.split(",");
         if (split.length == 2)
         {
            version = split[1];
         }
         if (version == null || version.isEmpty())
         {
            version = "[,)";
         }
         else if (!version.matches("(\\(|\\[).*?(\\)|\\])"))
         {
            version = "[" + version + "]";
         }

         RepositorySystem system = container.getRepositorySystem();
         Settings settings = container.getSettings();
         DefaultRepositorySystemSession session = container.setupRepoSession(system, settings);

         Artifact artifact = new DefaultArtifact(toMavenCoords(AddonId.from(addonName, version)));

         List<RemoteRepository> remoteRepos = container.getEnabledRepositoriesFromProfile(settings);

         VersionRangeRequest rangeRequest = new VersionRangeRequest(artifact, remoteRepos, null);

         VersionRangeResult rangeResult = system.resolveVersionRange(session, rangeRequest);
         return rangeResult;
      }
      catch (Exception e)
      {
         throw new RuntimeException("Failed to look up versions for [" + addonName + "]", e);
      }
   }

   private AddonInfo fromNode(AddonId id, DependencyNode dependencyNode, RepositorySystem system, Settings settings,
            DefaultRepositorySystemSession session)
   {
      AddonInfoBuilder builder = AddonInfoBuilder.from(id);
      List<DependencyNode> children = dependencyNode.getChildren();
      for (DependencyNode child : children)
      {
         Dependency dependency = child.getDependency();
         Artifact artifact = dependency.getArtifact();
         if (isAddon(artifact))
         {
            AddonId childId = toAddonId(artifact);
            boolean exported = false;
            boolean optional = dependency.isOptional();
            String scope = dependency.getScope();
            if (scope != null && !optional)
            {
               if ("compile".equalsIgnoreCase(scope) || "runtime".equalsIgnoreCase(scope))
                  exported = true;
               else if ("provided".equalsIgnoreCase(scope))
                  exported = false;
            }
            DependencyNode node = traverseAddonGraph(toMavenCoords(childId), system, settings, session);
            AddonInfo addonInfo = fromNode(childId, node, system, settings, session);
            if (optional)
            {
               builder.addOptionalDependency(addonInfo, exported);
            }
            else
            {
               builder.addRequiredDependency(addonInfo, exported);
            }
         }
      }
      return new LazyAddonInfo(this, builder);
   }

   private DependencyNode traverseAddonGraph(String coords, RepositorySystem system, Settings settings,
            DefaultRepositorySystemSession session)
   {
      session.setDependencyTraverser(new DependencyTraverser()
      {
         @Override
         public boolean traverseDependency(Dependency dependency)
         {
            boolean isForgeAddon = "forge-addon".equals(dependency.getArtifact().getClassifier());
            // We don't want to traverse non-addons optional dependencies
            if (!isForgeAddon && dependency.isOptional())
            {
               return false;
            }
            boolean shouldRecurse = !"test".equals(dependency.getScope());
            return shouldRecurse;
         }

         @Override
         public DependencyTraverser deriveChildTraverser(DependencyCollectionContext context)
         {
            return this;
         }
      });
      session.setDependencySelector(new AddonDependencySelector());
      Artifact queryArtifact = new DefaultArtifact(coords);
      CollectRequest collectRequest = new CollectRequest(new Dependency(queryArtifact, null),
               container.getEnabledRepositoriesFromProfile(settings));
      CollectResult result;
      try
      {
         result = system.collectDependencies(session, collectRequest);
      }
      catch (DependencyCollectionException e)
      {
         throw new RuntimeException(e);
      }
      return result.getRoot();
   }

   private String toMavenCoords(AddonId addonId)
   {
      String coords = addonId.getName() + ":jar:" + FORGE_ADDON_CLASSIFIER + ":" + addonId.getVersion();
      return coords;
   }

   private boolean isAddon(Artifact artifact)
   {
      return FORGE_ADDON_CLASSIFIER.equals(artifact.getClassifier());
   }

   private AddonId toAddonId(Artifact artifact)
   {
      if (isAddon(artifact))
      {
         return AddonId.from(artifact.getGroupId() + ":" + artifact.getArtifactId(), artifact.getBaseVersion());
      }
      throw new IllegalArgumentException("Not a forge-addon: " + artifact);
   }
}
