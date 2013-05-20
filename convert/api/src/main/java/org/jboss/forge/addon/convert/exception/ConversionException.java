package org.jboss.forge.addon.convert.exception;

public class ConversionException extends RuntimeException
{
   private static final long serialVersionUID = -1744577611317933091L;

   public ConversionException()
   {
      super();
   }

   public ConversionException(String message, Throwable e)
   {
      super(message, e);
   }

   public ConversionException(String message)
   {
      super(message);
   }

   public ConversionException(Throwable e)
   {
      super(e);
   }

}
