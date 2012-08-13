/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.resources;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ResourceEventsTest extends AbstractShellTest
{
   @Inject
   private ResourceEventObserver observer;

   @Test
   public void testEventsPropagateToObservers() throws Exception
   {
      initializeJavaProject();
      List<Resource<?>> created = observer.getCreated();
      Assert.assertFalse(created.isEmpty());

      List<Resource<?>> modified = observer.getModified();
      Assert.assertFalse(modified.isEmpty());

      List<Resource<?>> deleted = observer.getDeleted();
      Assert.assertTrue(deleted.isEmpty());

   }
}
