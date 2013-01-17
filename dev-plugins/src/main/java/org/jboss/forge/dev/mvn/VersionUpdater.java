package org.jboss.forge.dev.mvn;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.enterprise.inject.spi.BeanManager;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Profile;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.maven.dependencies.MavenDependencyAdapter;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.events.UpdatedDependency;
import org.jboss.forge.project.dependencies.events.UpdatingDependency;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellColor;

/**
 * Updates the project dependencies version
 *
 */
public class VersionUpdater
{
   private Project project;
   private Shell shell;
   private ProjectFactory projectFactory;
   private BeanManager manager;

   public VersionUpdater(Project project, Shell shell, ProjectFactory projectFactory, BeanManager manager)
   {
      this.project = project;
      this.shell = shell;
      this.projectFactory = projectFactory;
      this.manager = manager;
   }

   public boolean update()
   {
      return update(project);
   }

   private Properties[] append(Properties[] parent, Properties... children)
   {
      List<Properties> result = new ArrayList<Properties>();
      if (children != null)
      {
         result.addAll(Arrays.asList(children));
      }
      if (parent != null)
      {
         result.addAll(Arrays.asList(parent));
      }
      return result.toArray(new Properties[0]);
   }

   public boolean update(Project currentProject, Properties... parentProperties)
   {
      boolean updated = false;
      MavenCoreFacet mavenFacet = currentProject.getFacet(MavenCoreFacet.class);
      DependencyFacet depFacet = currentProject.getFacet(DependencyFacet.class);

      Model pom = mavenFacet.getPOM();

      for (Dependency dependency : pom.getDependencies())
      {
         if (updateDependency(depFacet, dependency, append(parentProperties, pom.getProperties())))
         {
            mavenFacet.setPOM(pom);
            updated = true;
         }
      }
      if (pom.getDependencyManagement() != null)
      {
         for (Dependency dependency : pom.getDependencyManagement().getDependencies())
         {
            if (updateDependency(depFacet, dependency, append(parentProperties, pom.getProperties())))
            {
               mavenFacet.setPOM(pom);
               updated = true;
            }
         }
      }
      for (Profile profile : pom.getProfiles())
      {
         for (Dependency dependency : profile.getDependencies())
         {
            if (updateDependency(depFacet, dependency,
                     append(parentProperties, profile.getProperties(), pom.getProperties())))
            {
               mavenFacet.setPOM(pom);
               updated = true;
            }
         }
         if (profile.getDependencyManagement() != null)
         {
            for (Dependency dependency : profile.getDependencyManagement().getDependencies())
            {
               if (updateDependency(depFacet, dependency,
                        append(parentProperties, profile.getProperties(), pom.getProperties())))
               {
                  mavenFacet.setPOM(pom);
                  updated = true;
               }
            }
         }
      }
      for (String module : pom.getModules())
      {
         Project subProject = projectFactory.findProject(
                  currentProject.getProjectRoot().getChildDirectory(module));

         if (update(subProject, append(parentProperties, pom.getProperties())))
         {
            mavenFacet.setPOM(pom);
            updated = true;
         }
      }
      return updated;
   }

   private boolean updateDependency(DependencyFacet depFacet, Dependency dependency, Properties... propertySets)
   {
      if (dependency.getVersion() == null)
      {
         return false; // managed ?
      }
      String propertyName = getExpressionName(dependency.getVersion());
      if (isExpression(dependency.getVersion()))
      {
         boolean propertyFound = false;
         for (Properties properties : propertySets)
         {
            if (properties.containsKey(propertyName))
            {
               propertyFound = true;
            }
         }
         if (!propertyFound)
         {
            shell.println(
                     "Can not update dependency with expression located in different pom: " + propertyName);
            shell.println("\t" + dependency);

            return false;
         }
      }

      String version = findNewerVersions(depFacet, dependency);
      if (version != null)
      {
         org.jboss.forge.project.dependencies.Dependency from = depFacet.resolveProperties(
                  new MavenDependencyAdapter(dependency));

         org.jboss.forge.project.dependencies.Dependency to = DependencyBuilder.create(
                  depFacet.resolveProperties(
                           new MavenDependencyAdapter(dependency)
                           )).setVersion(version);

         UpdatingDependency preEvent = new UpdatingDependency(
                  project, from, to);

         manager.fireEvent(preEvent);
         if (!preEvent.isVetoed())
         {
            if (isExpression(dependency.getVersion()))
            {
               for (Properties properties : propertySets)
               {
                  if (properties.containsKey(propertyName))
                  {
                     properties.put(propertyName, version);
                  }
               }
            }
            else
            {
               dependency.setVersion(version);
            }

            UpdatedDependency event = new UpdatedDependency(project, from, to);
            manager.fireEvent(event);
            return true;
         }
         else
         {
            shell.println("Update attempt vetoed by other Plugin");
            for (String message : preEvent.getMessages())
            {
               shell.println("\t" + message);
            }
            return false;
         }
      }
      return false;
   }

   private String findNewerVersions(DependencyFacet facet, Dependency dependency)
   {
      String currentVersion = dependency.getVersion();
      if (currentVersion == null || currentVersion.equals(""))
      {
         return null; // managed??
      }
      else
      {
         org.jboss.forge.project.dependencies.Dependency resolved = facet.resolveProperties(
                  new MavenDependencyAdapter(dependency));

         org.jboss.forge.project.dependencies.Dependency query = DependencyBuilder.create(resolved)
                  .setVersion("(" + resolved.getVersion() + ",)");

         List<org.jboss.forge.project.dependencies.Dependency> foundVersions = facet.resolveAvailableVersions(query);
         if (foundVersions == null || foundVersions.size() == 0)
         {
            return null;
         }
         else
         {
            String gav = dependency.getGroupId() + ":" + dependency.getArtifactId();
            org.jboss.forge.project.dependencies.Dependency latest = foundVersions.get(foundVersions.size() - 1);

            if (resolved.getVersion().equals(latest.getVersion()))
            {
               return null;
            }

            List<Option> options = Arrays.asList(
                     new Option(Option.OptionType.YES, "Yes"),
                     new Option(Option.OptionType.NO, "No"),
                     new Option(Option.OptionType.OTHER,
                              MessageFormat.format("Show {0} other versions",
                                       shell.renderColor(ShellColor.BOLD, String.valueOf(foundVersions.size())))));

            Option option = shell.promptChoiceTyped(
                     MessageFormat.format(
                              "Update {0} from {1} to {2}?",
                              gav, resolved.getVersion(), latest.getVersion()),
                     options, options.get(0));

            switch (option.getType())
            {
            case YES:
               return latest.getVersion();
            case OTHER:
               org.jboss.forge.project.dependencies.Dependency choice = shell.promptChoiceTyped(
                        "Which version would you like to update to?",
                        foundVersions, latest);
               return choice.getVersion();
            default:
               shell.println("Skipping " + gav);
               return null;
            }
         }
      }
   }

   private boolean isExpression(String value)
   {
      return value != null && value.startsWith("${");
   }

   private String getExpressionName(String value)
   {
      return value.replaceAll("\\$\\{(.*)\\}", "$1");
   }

   private static class Option
   {
      public enum OptionType
      {
         YES, NO, OTHER
      }

      private String display;
      private OptionType type;

      public Option(OptionType type, String display)
      {
         this.type = type;
         this.display = display;
      }

      public OptionType getType()
      {
         return type;
      }

      @Override
      public String toString()
      {
         return this.display;
      }
   }
}
