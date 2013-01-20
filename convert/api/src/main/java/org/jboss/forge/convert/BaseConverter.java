package org.jboss.forge.convert;

public abstract class BaseConverter<SOURCETYPE, TARGETTYPE> implements Converter<SOURCETYPE, TARGETTYPE>
{

   private Class<SOURCETYPE> sourceType;
   private Class<TARGETTYPE> targetType;

   public BaseConverter(Class<SOURCETYPE> sourceType, Class<TARGETTYPE> targetType)
   {
      super();
      this.sourceType = sourceType;
      this.targetType = targetType;
   }

   @Override
   public Class<SOURCETYPE> getSourceType()
   {
      return sourceType;
   }

   @Override
   public Class<TARGETTYPE> getTargetType()
   {
      return targetType;
   }

}
