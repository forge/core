package org.jboss.forge.classloader.sidewaysproxy;


public interface ContextValue<PAYLOADTYPE> extends Iterable<PAYLOADTYPE>
{
   public void set(PAYLOADTYPE payload);
   public PAYLOADTYPE get();
}
