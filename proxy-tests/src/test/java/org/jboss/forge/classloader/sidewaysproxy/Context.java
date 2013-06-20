package org.jboss.forge.classloader.sidewaysproxy;

public interface Context
{
   ContextValue<Payload> get();
   void set(ContextValue<Payload> payload);
}
