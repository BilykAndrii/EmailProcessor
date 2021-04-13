# Email processor

Java Spring Boot application 
* process zipped and ciphered .csv file
* check extracted data for specific pattern
* send filtered data to API

### Run

Run locally as Spring Boot standalone application
* from IDE
* terminal  
  mvn spring-boot:run
* or terminal  
  mvn clean package  
  then
  java -jar email-processor-0.0.1-SNAPSHOT.jar

https://docs.spring.io/spring-boot/docs/1.5.16.RELEASE/reference/html/using-boot-running-your-application.html  
https://spring.io/guides/gs/spring-boot/
  
### API
Application exposes endpoint "http://localhost:8080/upload"  
for file uploading and processing  
use POST with "file" param as multipart/form-data

### Properties
Please check "application.properties" file which contains
 * password for decription
 * API parameters
 * email-pattern 
 * size of objects chuck for API   
   etc  

