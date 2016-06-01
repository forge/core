/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.File;

import org.apache.maven.cli.CliRequest;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CliRequestTest
{

   @Test
   public void testCliRequestCreated() throws Exception
   {
      MavenFacetImpl mavenFacetImpl = new MavenFacetImpl();
      String[] params = { "A", "B", "C" };
      File tempDirectory = OperatingSystemUtils.getTempDirectory();
      String workingDir = tempDirectory.getAbsolutePath();
      CliRequest cliRequest = mavenFacetImpl.createCliRequest(params, workingDir);
      Assert.assertThat(cliRequest, notNullValue());
      Assert.assertThat(cliRequest.getWorkingDirectory(), equalTo(workingDir));
      Assert.assertThat(cliRequest.getMultiModuleProjectDirectory(), equalTo(tempDirectory));
   }

}
