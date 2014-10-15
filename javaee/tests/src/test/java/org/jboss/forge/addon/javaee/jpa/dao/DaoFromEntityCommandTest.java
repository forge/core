package org.jboss.forge.addon.javaee.jpa.dao;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.jpa.dao.ui.DaoFromEntityCommand;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.building.BuildException;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.JavaClass;
import org.jboss.forge.roaster.model.Method;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DaoFromEntityCommandTest {
	 @Deployment
	   @Dependencies({
	            @AddonDependency(name = "org.jboss.forge.addon:ui"),
	            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
	            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
	            @AddonDependency(name = "org.jboss.forge.addon:maven")
	   })
	   public static ForgeArchive getDeployment()
	   {
	      return ShrinkWrap
	               .create(ForgeArchive.class)
	               .addClass(ProjectHelper.class)
	               .addBeansXML()
	               .addAsAddonDependencies(
	                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
	                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
	                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
	                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
	                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
	                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
	               );
	   }

	   @Inject
	   private UITestHarness testHarness;

	   @Inject
	   private ProjectHelper projectHelper;

	   @Test
	   public void testCreateRESTGeneration() throws Exception
	   {
	      Project project = projectHelper.createWebProject();
	      projectHelper.installJPA_2_0(project);
	      projectHelper.installEJB_3_2(project);
	      project = projectHelper.refreshProject(project);
	      JavaResource entity = projectHelper.createJPAEntity(project, "Customer");
	      try (CommandController controller = testHarness.createCommandController(DaoFromEntityCommand.class,
	               project.getRoot()))
	      {
	         controller.initialize();
	         controller.setValueFor("targets", Arrays.asList(entity.getJavaType()));
	         controller.setValueFor("persistenceUnit", "unit");
	         Assert.assertTrue(controller.isValid());
	         Assert.assertTrue(controller.canExecute());
	         Result result = controller.execute();
	         Assert.assertThat(result, is(not(instanceOf(Failed.class))));
	      }
	      JavaSourceFacet facet = project.getFacet(JavaSourceFacet.class);
	      JavaResource daoResource = facet.getJavaResource("unknown.dao.CustomerDao");
	      Assert.assertTrue(daoResource.exists());
	      Assert.assertThat(daoResource.getJavaType(), is(instanceOf(JavaClass.class)));
	      JavaClass<?> restClass = daoResource.getJavaType();
	      Method<?, ?> method = restClass.getMethod("create", "unknown.model.Customer");
	      Assert.assertNotNull(method);
	      
	       project.getFacet(PackagingFacet.class).createBuilder().build();
	   }
}
