package org.jboss.forge.container;

public enum Status
{
   STARTING, STARTED, STOPPING, STOPPED, FAILED, WAITING, UNKNOWN;

   public boolean isFailed()
   {
      return this == FAILED;
   }

   public boolean isStarted()
   {
      return this == STARTED;
   }

   public boolean isStarting()
   {
      return this == STARTING;
   }

   public boolean isStopped()
   {
      return this == STOPPED;
   }

   public boolean isStopping()
   {
      return this == STOPPING;
   }

   public boolean isWaiting()
   {
      return this == WAITING;
   }

}