# 🚀 Онлайн-площадка для перепродажи б/у вещей Ads-Online

Групповая дипломная работа курса "Профессия Java-разработчик IND" платформы Skypro. 

Проект представляет собой онлайн-площадку для перепродажи б/у вещей. В приложении настроена базовая аутентификация пользователей, есть возможность оставлять свои объявления и просматривать объявления других людей, а также оставлять к ним комментарии.

![Демо работы приложения](https://github.com/sbakhareva/graduation-work/blob/main/src/main/resources/static/images/InShot_20250723_122956528.gif)

## 🛠 Стек используемых технологий: 

### Backend

- Java 17 (LTS)

- Spring Boot 3.4.5 (основной фреймворк)

- Spring Security (аутентификация и авторизация)

- Spring Data JPA (работа с базой данных)

- Liquibase 4.32.0 (управление миграциями БД)

- Lombok (генерация boilerplate-кода)

### Базы данных
- PostgreSQL (основная production-БД)

- H2 Database (тестовая БД)

### Документация

- OpenAPI 3.0 + Swagger UI (через springdoc-openapi 2.8.9)

### Тестирование

- JUnit 5 (интеграционные и unit-тесты)

- Mockito 5.17.0 (мокирование зависимостей)

- Spring Security Test (тесты безопасности)

- Spring Boot Test (интеграция с Spring Context)

### Сборка и развертывание
- Maven (управление зависимостями)

- Spring Boot Maven Plugin (сборка исполняемого JAR)

С подробными **требованиями для развертывания** приложения можно ознакомиться [здесь](https://github.com/sbakhareva/graduation-work/wiki/%D0%A2%D1%80%D0%B5%D0%B1%D0%BE%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F-%D0%B4%D0%BB%D1%8F-%D1%80%D0%B0%D0%B7%D0%B2%D0%B5%D1%80%D1%82%D1%8B%D0%B2%D0%B0%D0%BD%D0%B8%D1%8F-%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D1%8F).

## 📡 Примеры API

### Регистрация
```http
POST http://localhost:8080/register
Content-Type: application/json

{
  "username": "user@gmail.com",
  "password": "password",
  "firstName": "firstname",
  "lastName": "lastname",
  "phone": "+7 987 654-32-10",
  "role": "USER"
}
```

### Получение объявлений авторизованного пользователя
```http
GET http://localhost:8080/ads/me
Content-Type: application/json
Authorization: Basic user@gmail.com password
```

### Создание объявления
```http
POST http://localhost:8080/ads
Content-Type: application/json
Authorization: Basic user@gmail.com password

{
  "price": 1000000,
  "description": "piece of heaven",
  "title": "jojo's bizzare adventure manga"
}
```

## 📖 Документация
Документация проекта, включая требования в формате UserStory, схему компонентов и документ OpenAPI, оформлены в [Wiki проекта](https://github.com/sbakhareva/graduation-work/wiki).

## 🤝 Участники
В работе над проектом принимали участие:
* [Бахарева Снежана | sbakhareva](https://github.com/sbakhareva)
* [Морев Евгений | alexxaski](https://github.com/alexxaski)

