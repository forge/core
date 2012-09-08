package test.org.jboss.forge.container;

import javax.enterprise.inject.Typed;

import org.jboss.forge.container.services.Remote;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Remote
@Typed()
public class RemoteService
{
   public void invoke()
   {
   }
}
