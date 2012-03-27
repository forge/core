/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
      for (org.sonatype.aether.graph.Exclusion exclusion : exclusions) {
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
      for (org.sonatype.aether.graph.Dependency dependency : dependencies) {
         result.add(new MavenDependencyAdapter(dependency));
      }
      return result;
   }
}
