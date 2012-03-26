import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import java.lang.Override;

@Entity
public class User3 implements Serializable
{
   private short objectId = -1;
   
   @Version
   private @Column(name = "version")
   int version = 0;

   @Id @GeneratedValue(strategy = GenerationType.AUTO)
   @Column(name = "id", updatable = false, nullable = false)
   public short getObjectId()
   {
      return this.objectId;
   }

   public void setObjectId(final short id)
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
}