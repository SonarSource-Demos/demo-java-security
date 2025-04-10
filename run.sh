#!/bin/bash  

PK="demo:java-security"
MAIN_BRANCH="main"
NEWCODE_TAG="newcode-start"

# Check if the SONAR_HOST_URL contains the substring "nautilus"
if [[ "$SONAR_HOST_URL" == *"nautilus"* || "$SONAR_HOST_URL" == *"sonarcloud"* ]]; then
    echo "The SONAR_HOST_URL contains 'nautilus' or is pointing to SonarCloud."
    echo "Please ONLY run this script when setting your environment variables to your local machine, or via ngrok tunneling to your local machine" 
    echo "Exiting script."
    exit 0
else
    echo "The SONAR_HOST_URL does not contain 'nautilus' or SonarCloud. Continuing with the script."
    echo "Deleting project from $SONAR_HOST_URL"
    curl -X POST -u $SONAR_TOKEN: $SONAR_HOST_URL/api/projects/delete?project=$PK

    # Checkout commit for new code period
    git checkout tags/$NEWCODE_TAG
    NEWCODE_DATE=`git show -s --format=%cd --date=format:'%Y-%m-%d'`

    # Run baseline analysis - wait for quality gate to ensure analysis ID is available
    mvn clean verify sonar:sonar -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_TOKEN -Dsonar.projectKey=$PK \
      -Dsonar.projectDate=$NEWCODE_DATE -Dsonar.qualitygate.wait=true

    #Retrive analysis ID from SQ
    ANALYSIS_DATA=`curl -X POST -u $SONAR_TOKEN: $SONAR_HOST_URL/api/project_analyses/search?project=$PK`
    ANALYSIS_KEY=`echo $ANALYSIS_DATA | jq --raw-output '.analyses[0].key'`

    #Rename main branch
    curl -X POST -u $SONAR_TOKEN: \
      "$SONAR_HOST_URL/api/project_branches/rename?project=$PK&name=main"

    #Set NCP to initial analysis
    curl -X POST -u $SONAR_TOKEN: \
      "$SONAR_HOST_URL/api/new_code_periods/set?project=$PK&type=SPECIFIC_ANALYSIS&value=$ANALYSIS_KEY&branch=$MAIN_BRANCH"

    #checkout HEAD
    git checkout $MAIN_BRANCH

    #Run New code analysis
    mvn clean verify jacoco:report sonar:sonar -Dsonar.host.url=$SONAR_HOST_URL -Dsonar.login=$SONAR_TOKEN -Dsonar.projectKey=$PK
fi