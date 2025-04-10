# Demo - Java Security      
[![Quality Gate Status](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=alert_status&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Maintainability Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=sqale_rating&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Reliability Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=reliability_rating&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Security Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=security_rating&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Security Hotspots](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=security_hotspots&token=squ_1e4f3504bdc994f093721895e070abe7c11b1632)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security)
## Use case
This example demonstrates:
- Vulnerabilities
- Security Hotspots

It also demonstrates the possibility to define your own custom sources, sanitizers and sinks to detect more injection cases
(or avoid false positives)

## :boom: Warning
Please do not point this project at the live SonarQube project on https://nautilus.sonarqube.org. It requires two environment variables to run:

`SONAR_HOST_URL`
`SONAR_TOKEN`

If you want to run the bash script to initialize this in your own SonaqQube instance, set the above variables for your individual SonarQube instance, not for `nautilus`. The bash script below deletes and re-initializes the project, and we do *not* want the project on `nautilus` to be deleted.

## Usage

You first must make sure maven is installed on your machine. For MacOS:

`brew install maven`

Next, set your environment variables:

`export SONAR_HOST_URL=https://myngrok-url.ngrok.io`
`export SONAR_TOKEN=<INSERT USER TOKEN HERE>`

You can generate a Sonar token by following the document below:

https://docs.sonarsource.com/sonarqube-server/latest/user-guide/managing-tokens/#generating-a-token

Once you set these variables, run `./run.sh`

This will:
- Delete the project key **training:java-security** if it exists in SonarQube (to start from a scratch)
- Run `mvn clean verify sonar:sonar` to re-create the project

Project consists of a single class (`Insecure.java`) with a number of Vulnerabilities and Security Hotspots.

## Custom security configuration 
At the bottom of the class you see a bunch of methods that demonstrate custom injections.
- The method without sanitization (`doSomething()`) has an injection vulnerability
- The method with custom sanitization (`doSomethingSanitized()`) has no vulnerability

The custom security configuration file is in the root directory [here](s3649JavaSqlInjectionConfig.json)
