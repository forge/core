#!/bin/sh
# This is the Forge install script!
# To install Forge
# Just open up your terminal and type:
# curl  | sh  //DECIDE THE URL
# This way to install FORGE currently works with:
# Mac OSX and Linux
# forge-distribution-2.12.1.Final
# POSIX sh script

set -e
set -u

#using stderr
exec 1>&2

RELEASE="2.12.1"
PACKAGE=forge-distribution-2.12.1.Final

#OS Check
UNAME=$(uname)
if [ "$UNAME" != "Linux" -a "$UNAME" != "Darwin" ] ; then
    echo "Sorry: this OS is not supported yet."
    exit 1
fi

#Java Check
if type -p java; then
	_java=java
elif [ -n "$JAVA_HOME" -a -x "$JAVA_HOME/bin/java" ];  then   
    _java="$JAVA_HOME/bin/java"
else
    echo "Java not installed . Please install java. Aborting"
	exit 1
fi

if [ "$_java" ]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [ "$version" \< "1.7" ]; then           
        echo "JDK Version is less than 1.7 . Forge requires JDK 7+ . Aborting."
		exit 1
    fi
fi

trap "echo Installation failed." EXIT

#Clean Installation
if [ -d ~/forge ]; then
  echo "Removing your existing Forge installation."
  rm -rf ~/forge
fi


ZIP_URL="https://repository.jboss.org/nexus/service/local/repositories/releases/content/org/jboss/forge/forge-distribution/2.12.1.Final/forge-distribution-2.12.1.Final-offline.zip"
INSTALL_DIR=~/forge
INSTALLER_DIR=~/.forge
rm -rf $INSTALLER_DIR
mkdir $INSTALLER_DIR
mkdir $INSTALL_DIR
echo "Downloading Forge $RELEASE "
curl --progress-bar --fail "$ZIP_URL"  >> $INSTALLER_DIR/forge_installer.zip 
test -f $INSTALLER_DIR/forge_installer.zip 
unzip $INSTALLER_DIR/forge_installer.zip  -d $INSTALL_DIR
rm -rf $INSTALLER_DIR
test -x $INSTALL_DIR/$PACKAGE
echo 
echo "FORGE ${RELEASE} has been installed inside the directory : "$ ~/"forge."
echo 

trap - EXIT

FORGE_HOME=~/forge/$PACKAGE

#Adding The Path Variables to ~/.bash_profile
if [ -f ~/.bash_profile ];then
{
echo export FORGE_HOME=~/forge/$PACKAGE 
echo 
echo export PATH="$PATH":$FORGE_HOME/bin 
} >> ~/.bash_profile
fi

#Adding The Path Variables to ~/.bashrc
if [ -f ~/.bashrc ];then
{
echo export FORGE_HOME=~/forge/$PACKAGE 
echo 
echo export PATH="$PATH":$FORGE_HOME/bin
} >> ~/.bashrc
fi

#Adding The Path Variables to ~/.profile
if [ -f ~/.profile ];then
{	
echo export FORGE_HOME=~/forge/$PACKAGE 
echo 
echo export PATH="$PATH":$FORGE_HOME/bin 
} >> ~/.profile
fi

cat <<EOF

To get started fast:
run forge - located in $FORGE_HOME/bin/
localhost:~ $ forge
[~] $

If you have not yet seen the Forge built-in commands, you may either press TAB to see a list of the currently available commands, or get a more descriptive list by typing:

$ command-list

Then to get started - see the docs at
http://forge.jboss.org/documentation 
Consider installing Git and Maven 3.1+ (both optional)

Restart Terminal to use forge.

EOF

trap - EXIT
