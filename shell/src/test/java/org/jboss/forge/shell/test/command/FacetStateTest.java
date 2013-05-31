/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.test.command;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.Root;
import org.jboss.seam.render.RenderRoot;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.solder.SolderRoot;
import org.jboss.weld.exceptions.DefinitionException;
import org.junit.Test;
import org.junit.runner.RunWith;

import test.org.jboss.forge.shell.test.command.IllegalFacet;

@RunWith(Arquillian.class)
public class FacetStateTest
{

   @Deployment
   @ShouldThrowException(DefinitionException.class)
   public static JavaArchive getDeployment()
   {

      JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar").addPackages(true, Root.class.getPackage())
               .addPackages(true, RenderRoot.class.getPackage()).addPackages(true, SolderRoot.class.getPackage())
               .addClass(IllegalFacet.class)
               .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));

      return archive;
   }

   @Test
   public void testIllegalFacet() throws Exception
   {
   }
}