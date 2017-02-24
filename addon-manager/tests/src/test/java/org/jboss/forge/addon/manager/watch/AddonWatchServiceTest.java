/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.manager.watch;

import static org.hamcrest.CoreMatchers.endsWith;

import java.io.File;

import org.jboss.forge.addon.manager.watch.AddonWatchServiceImpl;
import org.jboss.forge.furnace.addons.AddonId;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddonWatchServiceTest
{

   /**
    * Test method for
    * {@link org.jboss.forge.addon.watch.ui.AddonWatchCommand#getInstallationPathFor(org.jboss.forge.furnace.addons.AddonId)}.
    */
   @Test
   public void testGetInstallationPathFor()
   {
      AddonId id = AddonId.from("org.foo:bar", "1.0.0-SNAPSHOT");
      File path = AddonWatchServiceImpl.getInstallationPathFor(id);
      Assert.assertThat(path.toString(), endsWith("/.m2/repository/org/foo/bar/1.0.0-SNAPSHOT/bar-1.0.0-SNAPSHOT.jar"));
   }

}
