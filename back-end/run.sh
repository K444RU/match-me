#!/bin/bash
mvn clean

# Check if the first argument is 'dev' for developer mode
if [ "$1" == "dev" ]; then
  # Run the application in developer mode

  mvn spring-boot:run -Dspring-boot.run.profiles=dev 
else
  # Run the application in production mode
  mvn spring-boot:run -Dspring-boot.run.profiles=prod
fi