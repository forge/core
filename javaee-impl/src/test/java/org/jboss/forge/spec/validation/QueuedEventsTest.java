/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.validation;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class QueuedEventsTest extends AbstractShellTest
{
   @Inject
   private MockFacetInstalledObserver observer;

   @Test
   public void testPromptBoolean() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("");
      getShell().execute("project install-facet forge.spec.validation");

      assertTrue(project.hasFacet(ValidationFacet.class));
      assertTrue(observer.observed() instanceof ValidationFacet);
   }
}
