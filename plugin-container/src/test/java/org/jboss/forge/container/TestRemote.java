package org.jboss.forge.container;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.services.Remote;

@Typed() // simulate bean from other container
public class TestRemote implements Remote
{
   public void invoke()
   {
   }
}
