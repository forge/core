package org.jboss.forge.maven.providers;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.services.ProjectAssociationProvider;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.Shell;

import javax.inject.Inject;

/**
 * Setup parent-child relation of Maven projects.
 *
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 */
public class MavenMultiModuleProvider implements ProjectAssociationProvider {

   private ProjectFactory projectFactory;

   @Override
   public void setProjectFactory(ProjectFactory factory) {
      projectFactory = factory;
   }

   @Override
   public void associate(Project project, DirectoryResource parentDir) {
      MavenCoreFacet mavenCoreFacet = project.getFacet(MavenCoreFacet.class);

      projectFactory.findProject(parentDir);
      Model parentPom = mavenCoreFacet.getPOM();
      parentPom.setPackaging("pom");
      parentPom.addModule(project.getProjectRoot().toString());
      mavenCoreFacet.setPOM(parentPom);

      projectFactory.findProject(project.getProjectRoot());
      Model pom = mavenCoreFacet.getPOM();

      Parent parent = new Parent();
      parent.setGroupId(parentPom.getGroupId());
      parent.setArtifactId(parentPom.getArtifactId());
      parent.setVersion(parentPom.getVersion());

      pom.setParent(parent);
      mavenCoreFacet.setPOM(pom);
   }
}
