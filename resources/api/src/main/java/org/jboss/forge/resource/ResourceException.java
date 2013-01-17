package org.jboss.forge.resource;

/**
 * Represents a generic Exception thrown by the Forge {@link Resource} API
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ResourceException extends RuntimeException
{
   private static final long serialVersionUID = 1532458466162580423L;

   public ResourceException()
   {
      super();
   }

   public ResourceException(String message, Throwable e)
   {
      super(message, e);
   }

   public ResourceException(String message)
   {
      super(message);
   }

   public ResourceException(Throwable e)
   {
      super(e);
   }

}