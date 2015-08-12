# DreamLinx Engine

DreamLinx Engine is a platform to develop daemon application in Java. Otherwise it can be used for other applications. It is part of DreamLinx.

## Build

### Install external libs

> $ mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dfile=lib/ojdbc6-11.2.0.4.jar -Dpackaging=jar

> $ mvn install:install-file -DgroupId=com.oracle -DartifactId=ucp -Dversion=11.2.0.4 -Dfile=lib/ucp-11.2.0.4.jar -Dpackaging=jar


## Development

### Create Eclipse project files

> $ mvn eclipse:eclipse
