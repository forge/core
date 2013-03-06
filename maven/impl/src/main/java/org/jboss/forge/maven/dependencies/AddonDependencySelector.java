package org.jboss.forge.maven.dependencies;

import org.sonatype.aether.collection.DependencyCollectionContext;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.util.graph.selector.StaticDependencySelector;

/**
 * A dependency selector that filters based on their scope and classifier "forge-addon"
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AddonDependencySelector
         implements DependencySelector
{
   private static final String FORGE_ADDON = "forge-addon";
   private final int depth;

   public AddonDependencySelector()
   {
      this.depth = 0;
   }

   public AddonDependencySelector(int depth)
   {
      this.depth = depth;
   }

   @Override
   public boolean selectDependency(Dependency dependency)
   {
      if (depth < 1)
         return true;

      String scope = dependency.getScope();
      String classifier = dependency.getArtifact().getClassifier();

      if ("test".equals(scope))
         return false;

      boolean result = (FORGE_ADDON.equals(classifier) && depth == 1)
               || (!FORGE_ADDON.equals(classifier) && !"provided".equals(scope));
      return result;
   }

   @Override
   public DependencySelector deriveChildSelector(DependencyCollectionContext context)
   {
      if ((depth > 0) && FORGE_ADDON.equals(context.getDependency().getArtifact().getClassifier()))
      {
         return new StaticDependencySelector(false);
      }
      return new AddonDependencySelector(depth + 1);
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      else if (null == obj || !getClass().equals(obj.getClass()))
      {
         return false;
      }

      AddonDependencySelector that = (AddonDependencySelector) obj;
      return depth == that.depth;
   }

   @Override
   public int hashCode()
   {
      int hash = 17;
      hash = hash * 31 + depth;
      return hash;
   }

}
