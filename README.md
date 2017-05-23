# IntegrityREST
Demo case how to use RESTeasy (http://resteasy.jboss.org/) together with PTC Integrity.

RESTeasy is part of a standard Integrity Server since 10.9 (perhaps 10.8 too). Just look for "resteasy" on the server under server/mks

##  Development environment
- PTC Integrity LM 10.9 (also 11.0 should be fine)
- Netbeans 7.4 (or 8)
- Java 1.7 (or 1.8)
- to be able to deploy directly into Integrity Server, I have installed a 5.4 JBoss server locally (Integrity uses an older one), and configured NetBeans with that. Then I could pick the target JBoss in NB, and was ready to start with my Web Project

## Deployment
- the pom.xml section for the maven-war-plugin will automatically copy the "war" to my local server for testing
- if you don't have a local server, then copy the "war" into the server's \server\mks\deploy directory manually

## Developer hints
- The interaction with the IntegrityREST is implemented as JSON data stream (put and get for the documents)
- internal authentification is done by reading the Server API configuration credentials
- User identification is outstanding (prototype is separately under construction)
- the word "restfulwebservices" I might change to "integrityrest" soon
- Solution is amaizingly stable 

## Package / File overview
- index.jsp - should become the online documentation, still under construction
- com.ptc.services.restfulwebservices - contains the REST classes
- com.ptc.services.restfulwebservices.api - contains the Integrity Server connection classes
- com.ptc.services.restfulwebservices.excel - contains a demo case for direct Excel creation based on Gateway templates
  (to be documented)
- com.ptc.services.restfulwebservices.gateway - see above
- com.ptc.services.restfulwebservices.model - the main data model classes
- com.ptc.services.restfulwebservices.security - the security class that can be used for more specific security
- com.ptc.services.restfulwebservices.test - for Excel, see above
- com.ptc.services.restfulwebservices.tools - some tools needed mainly also for excel

## The Excel project inside
I am used to work with the Integrity Gateway, and enhanced it with my own Custom Gateway functionality. This Excel part here included, is out of my own Custom Gateway project, and creates now formatted Excel files out of REST services. If you are interested I am pleased to handover more details in a web session. 

## Authorization
- Is the "Basic"
- OAuth2 was also considered but is not implemented yet

## Tested with
- SOAP UI
- and the github "Meeting Minutes" (same owner)

## Inside the files
- you will see some references to web pages that I have used as input
