# Spring
Система Быстрого Тестирования Студентов. Студент регистрируется
э-мейлом, к нему привязуеться его Успешность и на него будут приходить
сообщения о результате тестов. В системе существует каталог Тестов по
темам, авторизованный Студент может проходить тесты. В конце теста
должна на странице отобразится форма где показано ошибки студента. Все
данные об успеваемости и пройденных курсах отображаются на странице
Администратора как сводная таблица по всем Студентам.


(После консультации с перподавателем было решено, что студент не будет проходить тесты, а прохождение будет симулироваться с присвоением случайного рузельтата. С другой стороны администратор получает возможность блокировать тесты и создавать новые)


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
