/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Exclusion;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.packaging.PackagingType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenDependencyAdapter extends org.apache.maven.model.Dependency implements Dependency
{
   private static final long serialVersionUID = -518791785675970540L;

   public MavenDependencyAdapter(final org.apache.maven.model.Dependency dep)
   {
      if (dep == null)
      {
         throw new IllegalArgumentException("Dependency must not be null.");
      }

      org.apache.maven.model.Dependency clone = dep.clone();

      this.setArtifactId(clone.getArtifactId());
      this.setGroupId(clone.getGroupId());
      this.setClassifier("".equals(clone.getClassifier()) ? null : clone.getClassifier());
      this.setExclusions(clone.getExclusions());
      this.setOptional(clone.isOptional());
      this.setScope(clone.getScope());
      this.setType(clone.getType());
      this.setVersion(clone.getVersion());
      this.setSystemPath(clone.getSystemPath());
   }

   public MavenDependencyAdapter(final Dependency dep)
   {
      if (dep == null)
      {
         throw new IllegalArgumentException("Dependency must not be null.");
      }

      this.setArtifactId(dep.getArtifactId());
      this.setGroupId(dep.getGroupId());
      this.setVersion(dep.getVersion());
      this.setScope(dep.getScopeType());
      this.setType(dep.getPackagingType());
      this.setClassifier(dep.getClassifier());
      this.setSystemPath(dep.getSystemPath());

      for (Dependency exclusion : dep.getExcludedDependencies())
      {
         Exclusion temp = new Exclusion();
         temp.setArtifactId(exclusion.getArtifactId());
         temp.setGroupId(exclusion.getGroupId());
         this.getExclusions().add(temp);
      }
   }

   public MavenDependencyAdapter(final org.sonatype.aether.graph.Dependency dep)
   {
      if (dep == null)
      {
         throw new IllegalArgumentException("Dependency must not be null.");
      }

      this.setArtifactId(dep.getArtifact().getArtifactId());
      this.setGroupId(dep.getArtifact().getGroupId());
      this.setClassifier("".equals(dep.getArtifact().getClassifier()) ? null : dep.getArtifact().getClassifier());
      this.setExclusions(dep.getExclusions());
      this.setOptional(dep.isOptional());
      this.setScope(dep.getScope());
      this.setType(dep.getArtifact().getExtension());
      this.setVersion(dep.getArtifact().getVersion());
   }

   private void setExclusions(final Collection<org.sonatype.aether.graph.Exclusion> exclusions)
   {
      List<Exclusion> result = new ArrayList<Exclusion>();
      for (org.sonatype.aether.graph.Exclusion exclusion : exclusions)
      {
         Exclusion temp = new Exclusion();
         temp.setArtifactId(exclusion.getArtifactId());
         temp.setGroupId(exclusion.getGroupId());
         result.add(temp);
      }
      super.setExclusions(result);
   }

   @Override
   public String getScope()
   {
      return super.getScope() == null ? null : super.getScope().toLowerCase().trim();
   }

   @Override
   public String getScopeType()
   {
      return getScope();
   }

   @Override
   public ScopeType getScopeTypeEnum()
   {
      return ScopeType.from(getScope());
   }

   @Override
   public List<Dependency> getExcludedDependencies()
   {
      List<Dependency> result = new ArrayList<Dependency>();
      List<Exclusion> exclusions = this.getExclusions();
      for (Exclusion exclusion : exclusions)
      {
         Dependency dep = DependencyBuilder.create().setArtifactId(exclusion.getArtifactId())
                  .setGroupId(exclusion.getGroupId());
         result.add(dep);
      }
      return result;
   }

   public static List<Dependency> fromMavenList(final List<org.apache.maven.model.Dependency> dependencies)
   {
      List<Dependency> result = new ArrayList<Dependency>();

      for (org.apache.maven.model.Dependency dep : dependencies)
      {
         result.add(new MavenDependencyAdapter(dep));
      }

      return result;
   }

   public static List<org.apache.maven.model.Dependency> toMavenList(final List<Dependency> dependencies)
   {
      List<org.apache.maven.model.Dependency> result = new ArrayList<org.apache.maven.model.Dependency>();

      for (Dependency dep : dependencies)
      {
         result.add(new MavenDependencyAdapter(dep));
      }

      return result;
   }

   @Override
   public String getPackagingType()
   {
      return getType();
   }

   @Override
   public PackagingType getPackagingTypeEnum()
   {
      return PackagingType.from(getPackagingType());
   }

   @Override
   public String getType()
   {
      return super.getType() == null ? null : super.getType().toLowerCase().trim();
   }

   @Override
   public String toString()
   {
      return DependencyBuilder.toString(this);
   }

   @Override
   public String toCoordinates()
   {
      return DependencyBuilder.toId(this);
   }

   public static List<Dependency> fromAetherList(final List<org.sonatype.aether.graph.Dependency> dependencies)
   {
      List<Dependency> result = new ArrayList<Dependency>();
      for (org.sonatype.aether.graph.Dependency dependency : dependencies)
      {
         result.add(new MavenDependencyAdapter(dependency));
      }
      return result;
   }

   @Override
   public boolean isSnapshot()
   {
      return getVersion().endsWith("-SNAPSHOT");
   }
}
