import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import java.lang.Override;

@Entity
public class User2 implements Serializable
{

   @Id
   private @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   Long objectId = null;
   @Version
   private @Column(name = "version")
   int version = 0;

   public Long getObjectId()
   {
      return this.objectId;
   }

   public void setObjectId(final Long id)
   {
      this.objectId = id;
   }

   public int getVersion()
   {
      return this.version;
   }

   public void setVersion(final int version)
   {
      this.version = version;
   }

   public String toString()
   {
      String result = "";
      if (objectId != null)
         result += objectId;
      return result;
   }

   @Override
   public boolean equals(Object that)
   {
      if (this == that)
      {
         return true;
      }
      if (that == null)
      {
         return false;
      }
      if (getClass() != that.getClass())
      {
         return false;
      }
      if (objectId != null)
      {
         return objectId.equals(((User2) that).objectId);
      }
      return super.equals(that);
   }

   @Override
   public int hashCode()
   {
      if (objectId != null)
      {
         return objectId.hashCode();
      }
      return super.hashCode();
   }
}