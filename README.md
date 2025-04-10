# Demo - Java Security      
[![Quality Gate Status](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=alert_status&token=sqb_dc7abafef23808b17a7115494e7b3e3a80926ef0)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Maintainability Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=software_quality_maintainability_rating&token=sqb_dc7abafef23808b17a7115494e7b3e3a80926ef0)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Reliability Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=software_quality_reliability_rating&token=sqb_dc7abafef23808b17a7115494e7b3e3a80926ef0)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Security Rating](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=software_quality_security_rating&token=sqb_dc7abafef23808b17a7115494e7b3e3a80926ef0)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security) [![Security Hotspots](https://nautilus.sonarqube.org/api/project_badges/measure?project=demo%3Ajava-security&metric=security_hotspots&token=sqb_dc7abafef23808b17a7115494e7b3e3a80926ef0)](https://nautilus.sonarqube.org/dashboard?id=demo%3Ajava-security)


## Use case
This example demonstrates:
- Vulnerabilities
- Security Hotspots

It also demonstrates the possibility to define your own custom sources, sanitizers and sinks to detect more injection cases
(or avoid false positives)

## ⚠️ Warning
Please do not point this project at the live demo environment used at Sonar.
Also note that this project requires two environment variables to run:

- `SONAR_HOST_URL`
- `SONAR_TOKEN`

If you want to run the bash script to initialize this in your own SonaqQube instance, set the above variables for your individual SonarQube instance.

## Usage

You first must make sure maven is installed on your machine. For MacOS:

`brew install maven`

Next, set your environment variables:

`export SONAR_HOST_URL=https://myngrok-url.ngrok.io`
`export SONAR_TOKEN=<INSERT USER TOKEN HERE>`

You can generate a Sonar token by following the document below:

https://docs.sonarsource.com/sonarqube-server/latest/user-guide/managing-tokens/#generating-a-token

## Run a scan

You can then run a scan by running

```bash
mvn clean verify jacoco:report sonar:sonar -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_TOKEN -Dsonar.projectKey=$PK
```
Where `$PK` is your project key from your SonarQube project.

## Running the script

Once your variables are set, run `./run.sh`

This will:
- Delete the project key **demo:java-security** if it exists in SonarQube (to start from a scratch)
- Run `mvn clean verify sonar:sonar` to re-create the project

Project consists of a single class (`Insecure.java`) with a number of Vulnerabilities and Security Hotspots.

## Custom security configuration 
At the bottom of the class you see a bunch of methods that demonstrate custom injections.
- The method without sanitization (`doSomething()`) has an injection vulnerability
- The method with custom sanitization (`doSomethingSanitized()`) has no vulnerability

The custom security configuration file is in the root directory [here](s3649JavaSqlInjectionConfig.json)