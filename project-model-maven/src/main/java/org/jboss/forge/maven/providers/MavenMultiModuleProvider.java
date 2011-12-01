package org.jboss.forge.maven.providers;

import javax.inject.Inject;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.jboss.forge.maven.MavenCoreFacet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.services.ProjectAssociationProvider;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;

/**
 * Setup parent-child relation of Maven projects.
 * 
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class MavenMultiModuleProvider implements ProjectAssociationProvider
{
   @Inject
   private ProjectFactory projectFactory;

   @Override
   public void associate(final Project project, final DirectoryResource parentDir)
   {
      if (canAssociate(project, parentDir))
      {

         Project parent = projectFactory.findProject(parentDir);
         MavenCoreFacet parentMCF = parent.getFacet(MavenCoreFacet.class);
         Model parentPom = parentMCF.getPOM();
         parentPom.setPackaging("pom");
         parentPom.addModule(project.getProjectRoot().toString());
         parentMCF.setPOM(parentPom);

         MavenCoreFacet mcf = project.getFacet(MavenCoreFacet.class);
         Model pom = mcf.getPOM();

         Parent parentEntry = new Parent();
         parentEntry.setGroupId(parentPom.getGroupId());
         parentEntry.setArtifactId(parentPom.getArtifactId());
         parentEntry.setVersion(parentPom.getVersion());

         pom.setParent(parentEntry);
         mcf.setPOM(pom);
      }
   }

   @Override
   public boolean canAssociate(final Project project, final DirectoryResource parent)
   {
      return parent.getChild("pom.xml").exists() && project.getProjectRoot().getChild("pom.xml").exists();
   }
}
