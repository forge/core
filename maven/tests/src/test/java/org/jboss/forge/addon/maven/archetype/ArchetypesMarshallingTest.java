/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.archetype;

import java.net.URL;

import org.apache.maven.archetype.catalog.io.xpp3.ArchetypeCatalogXpp3Reader;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypesMarshallingTest
{

   @Test
   public void testXMLSchemalessArchetypeCatalog() throws Exception
   {
      URL u = getClass().getResource("schemaless-archetype-catalog.xml");
      assertUnmarshalling(u);
   }

   @Test
   public void testXMLSchemaArchetypeCatalog() throws Exception
   {
      URL u = getClass().getResource("schema-archetype-catalog.xml");
      assertUnmarshalling(u);
   }

   private void assertUnmarshalling(URL u) throws Exception
   {
      Assert.assertNotNull(new ArchetypeCatalogXpp3Reader().read(u.openStream()));
   }

}
