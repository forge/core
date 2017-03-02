/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui.result;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ResultsTest
{

   /**
    * Test method for {@link org.jboss.forge.addon.ui.result.Results#success()}.
    */
   @Test
   public void testSuccess()
   {
      Result success = Results.success();
      assertThat(success.getMessage(), nullValue());
      assertThat(success.getEntity().isPresent(), is(false));
   }

   /**
    * Test method for {@link org.jboss.forge.addon.ui.result.Results#success(java.lang.String)}.
    */
   @Test
   public void testSuccessString()
   {
      Result success = Results.success("Foo");
      assertThat(success.getMessage(), equalTo("Foo"));
      assertThat(success.getEntity().isPresent(), is(false));
   }

   /**
    * Test method for {@link org.jboss.forge.addon.ui.result.Results#success(java.lang.String, java.lang.Object)}.
    */
   @Test
   public void testSuccessStringEntity()
   {
      Object anything = new Object();
      Result success = Results.success("Foo", anything);
      assertThat(success.getMessage(), equalTo("Foo"));
      assertThat(success.getEntity().isPresent(), is(true));
      assertSame(anything, success.getEntity().get());
   }

   /**
    * Test method for {@link org.jboss.forge.addon.ui.result.Results#fail()}.
    */
   @Test
   public void testFail()
   {
      Failed failed = Results.fail();
      assertThat(failed.getMessage(), nullValue());
      assertThat(failed.getException(), nullValue());
      assertThat(failed.getEntity().isPresent(), is(false));
   }

   /**
    * Test method for {@link org.jboss.forge.addon.ui.result.Results#fail(java.lang.String)}.
    */
   @Test
   public void testFailString()
   {
      Failed failed = Results.fail("Foo");
      assertThat(failed.getMessage(), equalTo("Foo"));
      assertThat(failed.getException(), nullValue());
      assertThat(failed.getEntity().isPresent(), is(false));
   }

   /**
    * Test method for {@link org.jboss.forge.addon.ui.result.Results#fail(java.lang.String, java.lang.Throwable)}.
    */
   @Test
   public void testFailStringThrowable()
   {
      NullPointerException e = new NullPointerException();
      Failed failed = Results.fail("Foo", e);
      assertThat(failed.getMessage(), equalTo("Foo"));
      assertSame(e, failed.getException());
      assertThat(failed.getEntity().isPresent(), is(false));
   }

   /**
    * Test method for
    * {@link org.jboss.forge.addon.ui.result.Results#fail(java.lang.String, java.lang.Throwable, java.lang.Object)}.
    */
   @Test
   public void testFailStringThrowableObject()
   {
      Object anything = new Object();
      NullPointerException e = new NullPointerException();
      Failed failed = Results.fail("Foo", e, anything);
      assertThat(failed.getMessage(), equalTo("Foo"));
      assertSame(e, failed.getException());
      assertThat(failed.getEntity().isPresent(), is(true));
      assertSame(anything, failed.getEntity().get());
   }
}
