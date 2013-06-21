package org.jboss.forge.classloader.sidewaysproxy;

import java.util.Iterator;

public class ContextValueImpl<PAYLOADTYPE> implements ContextValue<PAYLOADTYPE>
{
   private PAYLOADTYPE payload;

   @Override
   public void set(PAYLOADTYPE payload)
   {
      this.payload = payload;
   }

   @Override
   public PAYLOADTYPE get()
   {
      return payload;
   }

   @Override
   public Iterator<PAYLOADTYPE> iterator()
   {
      return null;
   }
}
