/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ServletFacetTest extends SingletonAbstractShellTest
{
   @Test
   public void testWebXMLCreatedWhenInstalled() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "");
      getShell().execute("project install-facet forge.spec.servlet");
      assertTrue(project.hasFacet(ServletFacet.class));
      WebAppDescriptor config = project.getFacet(ServletFacet.class).getConfig();

      assertNotNull(config);
      assertTrue(config.exportAsString().contains("3.0"));
   }

}
