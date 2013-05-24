## JBoss Forge 2.0

The fastest way to build applications, share your software, and enjoy doing it.


What's new and noteworthy? 
-------------------------------------------------------------------------------

* **Addons**: What were previously called "Plugins" in Forge 1, are now "Addons" in Forge 2. This decision was made to clear up confusing verbiage like, "Plugin X has N Plugins" (due to the org.jboss.forge.plugins.Plugin interface.)

* We are currently in the process of migrating Forge 1 to Forge 2, so expect to find some Forge 1 functionality missing in this first Alpha version.

* **Modular Container fully rewritten**: The Forge runtime is now a fully functional Java module system based on JBoss Modules (The same engine behind JBoss AS 7+ and JBoss EAP). This means you may now pick-and-choose which addons are important for you.

* **Smaller, leaner, and faster**: Forge 2 now sports a slimmer 7 megabyte download size, and starts up in under three seconds. (Compared to upwards of 10+ seconds for Forge 1)

* **Better IDE Integration**: Forge 2 addons have been de-coupled from the command line, meaning you can create addons that run as wizards in the IDE, or commands in the shell - the same code works in both environments.

## Download Forge 2:    
Forge 2 is packaged inside an Eclipse plugin and also as a standalone ZIP file. They are independent of each other.
It is worth mentioning that the Eclipse plugin does not support access to shell yet.

- Eclipse Update Site - http://download.jboss.org/jbosstools/builds/staging/jbosstools-forge_master/all/repo/
- Command line tools - https://repository.jboss.org/nexus/service/local/artifact/maven/redirect?r=releases&g=org.jboss.forge&a=forge-distribution&v=2.0.0.Alpha2&e=zip


Get Started with the Command line tools:
-------------------------------------------------------------------------------
* Download [JBoss Forge 2.0.0.Alpha4](https://repository.jboss.org/nexus/service/local/artifact/maven/redirect?r=releases&g=org.jboss.forge&a=forge-distribution&v=2.0.0.Alpha4&e=zip)
* Extract the ZIP to a folder and navigate to forge-2.0.0.Alpha4/bin folder

Forge is now ready to go. 

Install the required addons by running the following commands:

```shell
    forge --install groupId:artifactId,version
```

- Forge will install the required dependencies for each addon.

If you wish to install the prototype Forge 2 Shell called Aesh, be sure to run the following:
```shell    
    forge --install shell
```

If you wish to remove any addon, you can use the following command:

```shell    
    forge --remove groupId:artifactId,version
```

Get Started with the Forge 2 Eclipse Plugin:
-------------------------------------------------------------------------------

- Install the Forge 2 Eclipse Plugin from http://download.jboss.org/jbosstools/builds/staging/jbosstools-forge_master/all/repo/ and restart Eclipse

This plugin starts the Forge 2 Container and your installed addons, so you can use them directly in your workspace
- Press Ctrl + 5 to show the installed addons that you may interact with (these addons use the UI addon, hence providing a user interface - see Developing an UI Addon for more details).

NOTE: The eclipse plugin already bundles the following addons 
* addon-manager
* convert
* dependencies
* environment
* facets
* maven,projects
* resources
* ui
* ui-spi 

In Forge 2.0.0.Alpha2, you MUST delete these addons from your ~/.forge location, otherwise you'll get some ClassCastExceptions (see [this issue](https://issues.jboss.org/browse/FORGE-843))


Developing an Addon
-------------------------------------------------------------------------------
Forge addons are simple Maven projects with a special classifier "forge-addon". This classifier is used while installing an addon so Forge can calculate its dependencies automatically releasing you from the [Classloader hell](http://robjsoftware.org/2007/07/13/classloader-hell/)

Create a Maven project

Forge Addons must be packaged with a 'forge-addon' classifier. Add this plugin configuration to your pom.xml:

```xml 
    <build>
      <plugins>
         <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <executions>
               <execution>
                  <id>create-forge-addon</id>
                  <phase>package</phase>
                  <goals>
                     <goal>jar</goal>
                  </goals>
                  <inherited>false</inherited>
                  <configuration>
                     <classifier>forge-addon</classifier>
                  </configuration>
               </execution>
            </executions>
         </plugin>
      </plugins>
    </build>
```

To expose services in your Addon for injection and service lookups in other addons, your types must be annotated with **@Exported**: 
```java
    @Exported
    public class ServiceImpl
    {
       public ServiceResult performTask() {
            // Do stuff...
       }
    }
```

However, best practices favor placing the **@Exported** annotation on a service interface, otherwise consumers will be required to request your specific service implementation. For example:
```java
    @Exported
    public interface ServiceType
    {
       public ServiceResult performTask();
    }
```

Then simply implement the service interface, and the **@Exported** annotation will be inherited automatically:
```java
    public class ServiceImpl implements ServiceType
    {
       public ServiceResult performTask() {
            // Do stuff...
       }
    }
```

Install your project in the local maven repository:

```shell
    mvn clean install
```
Run

```shell
    ./forge --install yourgroupId:artifactId,version
```

**NOTE: This coordinate is NOT the same as maven's. You MUST use a comma (,) between the artifactId and the version**

Add User Inputs to your Addon
-------------------------------------------------------------------------------
- Follow the procedures described in (Developing an Addon)
- Add a dependency to the UI addon in your pom.xml, like this

```xml 
      <dependency>
         <groupId>org.jboss.forge</groupId>
         <artifactId>ui</artifactId>
         <classifier>forge-addon</classifier>
         <version>2.0.0.Alpha4</version>
      </dependency>
```

- Create a java class

If your wizard contains a single page, implement UICommand, otherwise, UIWizard
  
Restart Forge Container inside Eclipse
----------------------------------------------------------------------------------
 
 If you need to restart the forge container running inside eclipse:
 
 - Open the Plug-in Registry view
 - In the pull down menu, check the 'Show Advanced Operations'
 - Choose the plugin named 'org.jboss.tools.forge.core.ext'
 - Right-click on it and select 'Stop' and then 'Start'
 
