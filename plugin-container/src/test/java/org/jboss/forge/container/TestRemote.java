package org.jboss.forge.container;

import javax.inject.Inject;

import org.jboss.forge.container.services.Remote;

public class TestRemote implements Remote
{
   @Inject
   public TestRemote cycle;

   public void invoke()
   {
   }
}
