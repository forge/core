/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.util;

import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class MetadataTest
{

   @Test
   public void testMetadataBuilder()
   {
      UICommandMetadata metadata = new MockCommand().getMetadata(null);
      Assert.assertEquals(MockCommand.class.getName(), metadata.getName());
      Assert.assertEquals(Categories.createDefault(), metadata.getCategory());
      Assert.assertEquals(UICommandMetadata.NO_DESCRIPTION, metadata.getDescription());
      Assert.assertEquals(MockCommand.class.getResource("MockCommand.txt"), metadata.getDocLocation());
   }

   @Test
   public void testMetadataBuilderNameOverride()
   {
      UICommandMetadata metadata = new MockCommand3().getMetadata(null);
      Assert.assertEquals("A Name", metadata.getName());
      Assert.assertEquals(Categories.createDefault(), metadata.getCategory());
      Assert.assertEquals(UICommandMetadata.NO_DESCRIPTION, metadata.getDescription());
      Assert.assertNull(metadata.getDocLocation());
   }

   @Test
   public void testMetadataBuilderWithDescriptionAndCategory()
   {
      UICommandMetadata metadata = new MockCommand2().getMetadata(null);
      Assert.assertEquals(MockCommand2.class.getName(), metadata.getName());
      Assert.assertNotNull(metadata.getCategory());
      Assert.assertEquals("A Category", metadata.getCategory().getName());
      Assert.assertNull(metadata.getCategory().getSubCategory());
      Assert.assertEquals("A Description", metadata.getDescription());
      Assert.assertNull(metadata.getDocLocation());
   }
}
