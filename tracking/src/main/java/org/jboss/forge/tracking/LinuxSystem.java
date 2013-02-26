/*******************************************************************************
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.forge.tracking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author André Dietisheim
 */
public class LinuxSystem
{

   public static final LinuxSystem INSTANCE = new LinuxSystem();

   /**
    * @see <a href="http://linuxmafia.com/faq/Admin/release-files.html"> an extensive list of release file locations</a>
    *
    * @see <a href="http://superuser.com/questions/11008/how-do-i-find-out-what-version-of-linux-im-running">
    *      release-file strings</a>
    */
   public final LinuxDistro CENTOS = new ReleaseFileContentCheckedDistro("CentOS", "/etc/redhat-release");
   public final LinuxDistro DEBIAN = new LinuxDistro("Debian", "/etc/debian_version");
   public final LinuxDistro FEDORA = new LinuxDistro("Fedora", "/etc/fedora-release");
   public final LinuxDistro GENTOO = new LinuxDistro("Gentoo", "/etc/gentoo-release");
   public final LinuxDistro YELLOWDOG = new LinuxDistro("YellowDog", "/etc/yellowdog-release");
   public final LinuxDistro KNOPPIX = new LinuxDistro("Knoppix", "knoppix_version");
   public final LinuxDistro MANDRAKE = new LinuxDistro("Mandrake", "/etc/mandrake-release");
   public final LinuxDistro MANDRIVA = new LinuxDistro("Mandriva", "/etc/mandriva-release");
   public final LinuxDistro MINT = new ReleaseFileContentCheckedDistro("LinuxMint", "/etc/lsb-release");
   public final LinuxDistro PLD = new LinuxDistro("PLD", "/etc/pld-release");
   public final LinuxDistro REDHAT = new ReleaseFileContentCheckedDistro("Red Hat", "/etc/redhat-release");
   public final LinuxDistro SLACKWARE = new LinuxDistro("Slackware", "/etc/slackware-version");
   public final LinuxDistro SUSE = new LinuxDistro("SUSE", "/etc/SuSE-release");
   public final LinuxDistro UBUNTU = new ReleaseFileContentCheckedDistro("Ubuntu", "/etc/lsb-release");

   private final LinuxDistro[] ALL = new LinuxDistro[] {
            CENTOS,
            MINT,
            UBUNTU,
            DEBIAN,
            FEDORA,
            GENTOO,
            KNOPPIX,
            MANDRAKE,
            MANDRIVA,
            PLD,
            REDHAT,
            SLACKWARE,
            SUSE,
            YELLOWDOG
   };

   public LinuxDistro getDistro()
   {
      for (LinuxDistro distro : ALL)
      {
         if (distro.isDistro())
         {
            return distro;
         }
      }
      return null;

   }

   public String getDistroNameAndVersion()
   {
      LinuxDistro distro = getDistro();
      if (distro != null)
      {
         return distro.getNameAndVersion();
      }
      else
      {
         return "Unknown";
      }
   }

   protected boolean exists(String releaseFilePath)
   {
      return releaseFilePath != null
               && releaseFilePath.length() > 0
               && new File(releaseFilePath).exists();
   }

   protected String getDistroFileContent(String filePath) throws IOException
   {
      int charachtersToRead = 1024;
      StringBuilder builder = new StringBuilder(charachtersToRead);
      BufferedReader reader = new BufferedReader(new FileReader(filePath));
      char[] buf = new char[charachtersToRead];
      int charRead = 0;
      while ((charRead = reader.read(buf)) != -1 && builder.length() < charachtersToRead)
      {
         String readData = String.valueOf(buf, 0, charRead);
         builder.append(readData);
      }
      reader.close();
      return builder.toString();
   }

   public class LinuxDistro
   {

      /**
       * The pattern to match the contents of the release-file - /etc/fedora-release etc. Attention: Ubuntu has
       * multi-line release file
       */
      private final Pattern VERSION_REGEX = Pattern.compile("([0-9.]+)");

      protected final String releaseFilePath;
      private String name;

      protected LinuxDistro(String name, String releaseFilePath)
      {
         this.name = name;
         this.releaseFilePath = releaseFilePath;
      }

      protected boolean isDistro()
      {
         return exists(getReleaseFilePath());
      }

      public String getName()
      {
         return name;
      }

      public String getVersion()
      {
         try
         {
            String distroString = getDistroFileContent(getReleaseFilePath());
            Matcher matcher = VERSION_REGEX.matcher(distroString);
            if (matcher.find())
            {
               return matcher.group(1);
            }
         }
         catch (IOException e)
         {
         }
         return "";
      }

      public String getNameAndVersion()
      {
         return new StringBuilder().append(getName()).append(" ").append(getVersion()).toString();
      }

      public String getReleaseFilePath()
      {
         return releaseFilePath;
      }

      protected boolean distroFileContains(String value)
      {
         try
         {
            boolean fileExists = exists(getReleaseFilePath());
            if (fileExists)
            {
               String content = getDistroFileContent(getReleaseFilePath());
               return content != null && content.indexOf(value) >= 0;
            }
         }
         catch (IOException e)
         {
         }
         return false;

      }

      @Override
      public String toString()
      {
         return name;
      }
   }

   /**
    * A distribution definition that checks for presence of the given distro name in the given release file.
    */
   public class ReleaseFileContentCheckedDistro extends LinuxDistro
   {

      public ReleaseFileContentCheckedDistro(String name, String releaseFilePath)
      {
         super(name, releaseFilePath);
      }

      @Override
      protected boolean isDistro()
      {
         return distroFileContains(getName());
      }
   }
}
