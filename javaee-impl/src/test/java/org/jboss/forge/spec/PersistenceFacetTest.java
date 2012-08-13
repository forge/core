/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec;

import static org.junit.Assert.assertNotNull;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class PersistenceFacetTest extends AbstractJPATest
{
   @Test
   public void testCDintoProjectRegistersPresistenceFacet() throws Exception
   {
      Shell shell = getShell();
      Project project = getProject();

      PersistenceFacet persistence = project.getFacet(PersistenceFacet.class);
      assertNotNull(persistence);

      shell.execute("cd /");
      // FIXME weld bug with javassist - needs fixing.
      // assertNull(getProject());

      shell.execute("cd - ");
      assertNotNull(getProject());

      project = getProject();
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      assertNotNull(javaSourceFacet);

      persistence = project.getFacet(PersistenceFacet.class);
      assertNotNull(persistence);
   }

   @Test
   public void testCanWritePersistenceConfigFile() throws Exception
   {
      Project project = getProject();

      PersistenceFacet persistence = project.getFacet(PersistenceFacet.class);
      assertNotNull(persistence);

      PersistenceDescriptor model = persistence.getConfig();
      model.exportAsString().contains("2.0");
   }
}
