package org.jboss.forge.addon.convert;

public abstract class AbstractConverter<SOURCETYPE, TARGETTYPE> implements Converter<SOURCETYPE, TARGETTYPE>
{

   private Class<SOURCETYPE> sourceType;
   private Class<TARGETTYPE> targetType;

   public AbstractConverter(Class<SOURCETYPE> sourceType, Class<TARGETTYPE> targetType)
   {
      super();
      this.sourceType = sourceType;
      this.targetType = targetType;
   }

   public Class<SOURCETYPE> getSourceType()
   {
      return sourceType;
   }

   public Class<TARGETTYPE> getTargetType()
   {
      return targetType;
   }
}
