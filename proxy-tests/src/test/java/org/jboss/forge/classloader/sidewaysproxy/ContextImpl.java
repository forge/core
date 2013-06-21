package org.jboss.forge.classloader.sidewaysproxy;

public class ContextImpl implements Context
{
   private ContextValue<Payload> payload;

   @Override
   public void set(ContextValue<Payload> payload)
   {
      this.payload = payload;
   }

   @Override
   public ContextValue<Payload> get()
   {
      return payload;
   }

}
