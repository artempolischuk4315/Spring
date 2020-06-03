# Spring
Students Rapid Testing System. Student is registering
by e-mail, his Success will be attached to him and on this email will come
Test result messages. The system has a catalog of Tests for
topics, an authorized Student can take tests. At the end of the test
the page should display a form showing student errors. All
data on academic performance and completed courses are displayed on the page
Administrator as a summary table for all Students.


*******
RUNNING THE PROJECT
*******
Clone project to your local repository

From project root folder run - mvn spring-boot:run

Use http://localhost:8090/ to view website

***********
TO LOG IN AS ADMIN: 

Change role in database for choosen created user to ROLE_ADMIN and he will beciome admin. 
Or create user with email "art4315@gmail.com". This user will become admin automatically.  

********
DATABASE:

Set 

*spring.datasource.username= YOUR_USERNAME

*spring.datasource.password=YOUR_PASSWORD
