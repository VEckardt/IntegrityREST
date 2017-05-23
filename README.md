# IntegrityREST
Demo case how to use RESTeasy together with PTC Integrity

##  Development environment
- PTC Integrity LM 10.9 (also 11.0 should be fine)
- Netbeans 7.4 (or 8)
- Java 1.7 (or 1.8)
- to be able to deploy directly into Integrity Server, I have installed a 5.4 JBoss server locally (Integrity uses an older one), and configured NetBeans with that 

## Deployment
- the pom section for the maven-war-plugin will automatically copy the "war" to my local server for testing
- if you dont have a local server, then copy the "war" into the server's \server\mks\deploy directory manually

## Package / File overview
- index.jsp - should become the online documentation, under construction
- com.ptc.services.restfulwebservices - contains the REST classes
- com.ptc.services.restfulwebservices.api - contains the Integrity Server connection classes
- com.ptc.services.restfulwebservices.excel - contains a demo case for direct Excel creation based on Gateway templates
  (to be documented)
- com.ptc.services.restfulwebservices.gateway - see above
- com.ptc.services.restfulwebservices.model - the main data model classes
- com.ptc.services.restfulwebservices.security - the security class that can be used for more specific security
- com.ptc.services.restfulwebservices.test - for Excel, see above
- com.ptc.services.restfulwebservices.tools - some tools needed mainly also for excel

## Autorization
- Is the "Basic"
- OAuth2 was also considered but is not implemented yet

## Tested with
- SOAP UI
- and the github "Meeting Minutes" (same owner)

## Inside the files
- you will see some references to web pages that I have used as input
