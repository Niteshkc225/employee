# API Automation Suite
* This Automation suite is intended to Test the REST API calls.
* This Suite includes RestAssured, Cucumber, Wiremock, Extent and TestNG frameworks.
* API suite ensures all the APIs in the back end functions as intended.


## Features of the this Automation Suite are listed below : 

1. Runs with TestNG + RestAssured framework.
2. Good Reporting with Extent Reports.
3. Good Logging with Log4J.
4. Runs with Maven Build with goal as Test and can be easily integrated with any CI/CD pipeline.

### Basic Instructions

To run the rest api tests, run below command from **employee** folder : 
```
mvn verify
```
* feature files are placed under `src/test/resources`.
* Test report can be found under `target/test-reports` folder.
