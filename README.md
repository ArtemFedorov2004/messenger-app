# Мессенджер на React и Spring boot

## Стек технологий

**Бэкенд:**

* Spring Boot 3.x
* PostgreSQL
* Flyway (миграции)
* Lombok
* WebSockets
* Testcontainers

**Фронтенд:**

* React
* Ant Design (UI)
* MobX (state management)

## Серверная часть (Spring Boot)

### Модель данных

**Сущности**

**Пользователь (`User`)**

Каждый пользователь в системе содержит:

* Уникальное имя
* Почту (уникальное, обязательное поле)
* Пароль (обязательное поле)
* Роль (обязательное поле, значения: ROLE_USER/ROLE_ADMIN)

**Особенности:**

* Реализует UserDetails для интеграции с Spring Security
* Роли хранятся как `varchar(50)`

**Чат (`Chat`)**

Содержит:

* Уникальный идентификатор

**Связи:**

* Многие-ко-многим с User

**Сообщение (`Message`)**

Сообщение включает:

* Контент (текст, обязательный, длина >= `1`)
* Дату создания (`timestamp`, обязательный)
* Дату редактирования (`timestamp`)

**Связи:**

* Многие-к-одному с Chat через
* Многие-к-одному с User (отправитель)

### Миграции базы данных (Flyway)

Система использует Flyway для управления миграциями базы данных. Все объекты базы данных создаются в схеме `messenger`.

Скрипты расположены в `db/migration`:

**`V0.0.1__Basic_schema.sql `- Базовая схема.**

### ORM

Система использует **Java Persistence API (JPA)** с реализацией **Hibernate** для работы с базой данных.

### REST API

**AuthenticationRestController**

Контроллер для операций аутентификации.

**Эндпоинты**

**POST** `/api/registration`

Регистрация нового пользователя.

**Данные запроса (RegistrationPayload):**

```json
{
  "username": "string (required, not blank)",
  "email": "string (required, valid email format)",
  "password": "string (required, min 8 characters)"
}
```

**Успешный ответ (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

* Устанавливает HTTP-only куку `refreshToken` с refresh-токеном

**Ошибки:**

* **400 Bad Request:**
    * Если данные не прошли валидацию
    * Если имя пользователя или email уже заняты

**POST** `/api/login`

Авторизация существующего пользователя.

**Данные запроса (LoginPayload):**

```json
{
  "username": "string (required, not blank)",
  "password": "string (required, min 8 characters)"
}
```

**Успешный ответ (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

* Устанавливает HTTP-only куку `refreshToken`

**Ошибки:**

* `400 Bad Request`: Если данные не прошли валидацию
* `401 Unauthorized`: Если неверные учетные данные

**POST** `/api/logout`

Выход из системы.

**Запрос:** Без тела

**Ответ:**

* `200 OK`: Пустой ответ
* Очищает куку `refreshToken` (устанавливает maxAge=`0`)

**POST** `/api/refresh`

Обновление access-токена с помощью refresh-токена.

**Запрос:**

* Требуется кука `refreshToken`

**Успешный ответ (200 OK):**

```json
{
  "accessToken": "string",
  "refreshToken": "string"
}
```

* Устанавливает новую куку `refreshToken` с обновленным токеном

**Ошибки:**

* `400 Bad Request`: Если отсутствует кука `refreshToken`
* `401 Unauthorized`: Если refresh-токен невалиден

**UsersRestController**

Контроллер для поиска пользователей.

**Эндпоинт:** GET `/api/users`

**Параметры запроса:**

* `query` (необязательный) - строка для поиска пользователей
* `page` (по умолчанию `0`) - номер страницы
* `size` (по умолчанию `5`) - количество элементов на странице

**Логика работы:**

1. Если `query` не указан или пуст - возвращает пустую страницу (`Page.empty()`)
2. Ищет пользователей по запросу, исключая текущего пользователя
3. Возвращает результаты с пагинацией

**Формат ответа (200 OK):**

```
{
  "content": [
    {
      "username": "string",
      "email": "string"
    }
    // ... другие пользователи
  ],
  "pageable": {
    // стандартная информация о пагинации
  }
}
```

**UserRestController**

Контроллер для получения информации о конкретном пользователе.

**Эндпоинт:** GET `/api/users/{username}`

**Параметры:**

* `username` - имя пользователя в URL

**Логика работы:**

1. Находит пользователя по username
2. Если пользователь не найден - возвращает 404 Not Found
3. Возвращает данные пользователя

**Формат ответа (200 OK):**

```json
{
  "username": "string",
  "email": "string"
}
```

**PrivateChatsRestController**

Контроллер для работы с приватными чатами.

**Эндпоинты:**

**GET `/api/private-chats` - Получение списка чатов пользователя**

**Логика:**

* Возвращает все приватные чаты, где текущий пользователь участник

**POST `/api/private-chats` - Создание нового приватного чата**

**Тело запроса:**

```json
{
  "participantName": "string (required, not blank)"
}
```

**Валидация:**

* Участник не может быть текущим пользователем (400 Bad Request)
* Участник должен существовать (400 Bad Request)
* Чат между этими пользователями не должен существовать (400 Bad Request)

**Успешный ответ (201 Created):**

```json
{
  "id": "number",
  "participantName": "string",
  "lastMessage": "object"
}
```

**MessagesRestController**

Контроллер для работы с сообщениями в чатах.

**Эндпоинты:**

**GET `/api/private-chats/{chatId}/messages` - Получение сообщений чата**

**Параметры:**

* `chatId` - id чата в URL
* `page` (по умолчанию `0`) - номер страницы
* `size` (по умолчанию `20`) - количество сообщений на странице

**Правила доступа:**

* Только участники чата могут видеть сообщения (иначе 403 Forbidden)

**POST `/api/private-chats/{chatId}/messages` - Создание нового сообщения**

**Тело запроса:**

```json
{
  "content": "string (required, not blank)"
}
```

**Правила доступа:**

* Только участники чата могут отправлять сообщения (403 Forbidden)

**Успешный ответ (201 Created):**

```json
{
  "id": "number",
  "chatId": "number",
  "senderName": "string",
  "content": "string",
  "createdAt": "timestamp",
  "editedAt": "timestamp"
}
```

**MessageRestController**

Контроллер для управления конкретными сообщениями.

**Эндпоинты:**

**PATCH `/api/private-chat-messages/{messageId}` - Редактирование сообщения**

**Тело запроса:**

```json
{
  "content": "string (required, not blank)"
}
```

**Правила доступа:**

* Только автор сообщения может его редактировать (403 Forbidden)

**Успешный ответ (200 OK):** Обновленное сообщение

**DELETE `/api/private-chat-messages/{messageId}` - Удаление сообщения**

**Правила доступа:**

* Только автор сообщения может его удалить (403 Forbidden)

**Успешный ответ:** 204 No Content

### Локализация и обработка ошибок

Система поддерживает локализацию сообщений об ошибках (в настоящее время только на русском языке). Все входящие данные
проходят валидацию. Файл `messages.properties` содержит все сообщения об ошибках. Система возвращает ошибки в формате
Problem Details:

```json
{
  "title": "Not Found",
  "status": 404,
  "detail": "Пользователь не найден",
  "instance": "/api/users/undefined"
}
```

### Безопасность

**Зависимости**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<!-- JWT зависимости -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
</dependency>
```

**Основные компоненты**

1. `SecurityBeans` - главный класс конфигурации безопасности
2. `JwtAuthenticationFilter` - фильтр для JWT аутентификации

**Особенности реализации**

**Фильтр JWT:**

* Извлекает токен из заголовка Authorization
* Проверяет валидность токена
* Устанавливает аутентификацию в SecurityContext

Используется `BCryptPasswordEncoder` для хеширования

**Обработка ошибок безопасности**

* `401 Unauthorized` - при проблемах аутентификации
* `403 Forbidden` - при отсутствии прав доступа

### WebSocket

**Зависимость:**

```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

**Конфигурация (`WebSocketConfig`):**

* Endpoint: `/api/ws`
* Префиксы:
    * `/app` - для приема сообщений от клиентов
    * `/user` - для персональных уведомлений
* JWT аутентификация при подключении через STOMP

**Система уведомлений**

**Сервис (`DefaultNotificationService`):**

* Отправляет уведомления через `SimpMessagingTemplate`
* 2 типа каналов:
    * `/queue/private-chat-notifications` - уведомления о чатах
    * `/queue/message-notifications` - уведомления о сообщениях

**Типы уведомлений:**

1. **События чата:**
    * `NEW_CHAT` - создан новый чат
    * `EDIT_CHAT` - изменения в чате
2. **События сообщений:**
    * `NEW_MESSAGE` - новое сообщение
    * `EDIT_MESSAGE` - редактирование сообщения
    * `DELETE_MESSAGE` - удаление сообщения

**Логика работы:**

1. При действиях (создание/редактирование/удаление) сервис:
    * Определяет получателей (другой участник чата)
    * Формирует payload с данными
    * Отправляет через WebSocket

2. Клиенты подписываются на персональные очереди:
    * `/user/queue/private-chat-notifications`
    * `/user/queue/message-notifications`

### Конфигурации

**Профили Spring Boot:**

1. `standalone` - Локальная разработка
2. `docker` - Docker Compose
3. `prod` - Для продакшн окружения

### Запуск приложения

**Локальная разработка**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=standalone
```

**Docker**

Сборка и запуск контейнера для текущей версии приложения (0.0.1-SNAPSHOT):

```bash
docker build --build-arg JAR_FILE=messenger-server/target/messenger-server-0.0.1-SNAPSHOT-exec.jar -t messenger/messenger-server:0.0.1 .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker --name messenger-server messenger/messenger-server:0.0.1
```

### Тестирование

Запуск тестов

```bash
# Все тесты
mvn clean verify

# Только unit-тесты
mvn clean test

# Только интеграционные тесты
mvn failsafe:integration-test
```

Для проверки работы с реальной базой данных используется Testcontainers.

### Docker-образ для Spring Boot приложения

Используется **многоэтапная сборка**:

1. Этап распаковки: извлечение слоев JAR-файла
2. Этап сборки: создание итогового образа

**Распаковка JAR:**

1. Принимает аргумент `JAR_FILE` (путь к jar-файлу)
2. Использует Spring Boot Layertools для распаковки:

* Разделяет на слои согласно` layers.idx`

**Финальный образ:**

* Создается отдельная группа `spring-boot-group`
* Добавляется пользователь `spring-boot`
* Все дальнейшие команды выполняются от этого пользователя

Делаем это для того, чтобы все действия выполнялись в рамках данного контейнера от имени пользователя отличного от root

**Запуск приложения:**

* `${JAVA_OPTS}`: переменная для JVM-флагов
* `${0} ${@}`: передача аргументов командной строки

## Клиентская часть мессенджера (React)

Использованы следующие технологий:

* **State management:** MobX
* **UI библиотека:** Ant Design
* **Маршрутизация:** React Router
* **HTTP клиент:** Axios
* **WebSocket**

### Страницы приложения

1. **Главная страница (Home.jsx)**
2. **Страница входа (Login.jsx)**
3. **Страница регистрации (Registration.jsx)**
4. **Страница чатов (Chat.jsx)**
5. **Профиль пользователя (Profile.jsx)**

### Система маршрутизации

**Public routes (доступны без авторизации):**

* `/` - Главная страница
* `/login` - Страница входа
* `/registration` - Страница регистрации

Private routes (требуют авторизации):

* `/chat` - Страница чатов
* `/profile` - Профиль пользователя

**Логика защиты маршрутов:**

* При попытке доступа к приватному маршруту без авторизации - редирект на `/login`

### Система аутентификации

**HTTP клиенты**

1. **Базовый клиент ($api)**
2. **Авторизованный клиент ($authApi)**

**Файл окружения (`.env`)**

В корне проекта находится файл `.env`. Он содержит базовый URL API сервера - `REACT_APP_API_URL`.

**Процесс входа пользователя**

Когда пользователь вводит свои учетные данные на странице входа:

1. Приложение отправляет логин и пароль на сервер
2. Сервер проверяет данные и при успешной проверке:
    * Возвращает access token в теле ответа
    * Устанавливает refresh token в HTTP-only cookie
3. Клиентское приложение сохраняет полученный access token в localStorage

**Механизм проверки авторизации**

При каждом запуске приложения:

1. Проверяется наличие access token в localStorage
2. Если токен присутствует:
    * Отправляется запрос для обновления токена
    * Если refresh token валиден, то сервер возвращает обновленные токены
    * Загружаются данные пользователя

**Работа с защищенными запросами**

Для всех запросов, требующих авторизации:

1. Приложение автоматически добавляет access token в заголовок Authorization
2. Если токен просрочен (сервер возвращает ошибку 401):
    * Приложение пытается один раз обновить токен, используя refresh token из cookie
    * При успешном обновлении повторяет оригинальный запрос с новым токеном

### Главная страница (Home.jsx)

**Содержание:**

* Описание проекта
* Карусель с логотипами технологий
* Ссылки на GitHub и контакты

**Навбар:**

* Отображает текущего пользователя (если авторизован)
* Кнопки:
    * "Перейти в чат" (для авторизованных)
    * "Выйти" (для авторизованных)
    * "Войти" (для не авторизованных)

### Страница входа (Login.jsx)

**Функциональность:**

* Форма входа с валидацией полей
* Обработка ошибок сервера
* Переход на страницу регистрации

**Элементы формы:**

1. **Поле имени пользователя:**
    * Обязательное поле
2. **Поле пароля:**
    * Обязательное поле
    * Минимум 8 символов
3. **Кнопка отправки:**
    * Активируется только при валидной форме

**Обработка ответа сервера**

Успешный сценарий (200 OK):

* Происходит перенаправление на страницу чатов (/chat)

Ошибочный сценарий (401 Unauthorized):

* Появляется красное уведомление: "Неверное имя пользователя или пароль"

**Навигация:**

* Ссылка на страницу регистрации

### Страница регистрации (Registration.jsx)

**Функциональность:**

* Форма регистрации нового пользователя
* Валидация всех полей
* Обработка конфликтов данных

**Поля формы:**

1. Имя пользователя:
    * Обязательное
    * Уникальное (проверка на сервере)
2. Email:
    * Обязательное
    * Валидный формат email
    * Уникальный (проверка на сервере)
3. Пароль:
    * Обязательное
    * Минимум 8 символов

**Обработка ошибок:**

* **400 статус:**
    * "Имя пользователя уже занято"
    * "Адрес электронной почты уже занят"

* Ошибки отображаются в соответствующих полях

**Навигация:**

* Ссылка на страницу входа

### Страница профиля (Profile.jsx)

**Структура страницы:**

* Заголовок "Профиль пользователя"
* Аватар пользователя (стандартное изображение)
* Карточка с данными пользователя
* Поля:
    * Имя пользователя
    * Email

### Страница чата (Chat.jsx)

#### Общая структура интерфейса

Страница чата разделена на два основных столбца:

**Левый столбец (навигация)**

1. **Меню пользователя** (Dropdown)
2. **Кнопка поиска пользователей**
3. **Список чатов пользователя**

**Правый столбец (основное содержимое)**

1. **Шапка чата** (информация о собеседнике)
2. **Список сообщений** с бесконечной подгрузкой
3. **Форма отправки сообщений**

#### Детальное описание компонентов

1. **Меню пользователя (Dropdown)**

**Элементы меню:**

* **Аккаунт** - перенаправляет на страницу профиля (`/profile`)
* **Выйти** - выполняет выход из системы

2. **Список чатов**

**Особенности:**

* Загружается при монтировании компонента
* Подсвечивает выбранный чат
* При клике обновляет правую часть интерфейса

3. **Шапка чата (RightColumnHeader)**

**Отображает:**

* Аватар собеседника
* Имя собеседника

4. **Список сообщений (MessageList)**

**Особенности реализации:**

* Использует **Infinite Scroll** для подгрузки сообщений
* Первоначально загружаются последние сообщения
* При скролле вверх подгружаются более старые сообщения

**Пагинация:**

* Размер страницы: `MESSAGES_BATCH_SIZE`

5. **Форма отправки сообщений**

**Элементы:**

* Поле ввода сообщения
* Кнопка отправки (иконка бумажного самолетика)

**Функционал:**

* Отправка по клику или нажатию Enter

6. **Контекстное меню сообщений**

Для собственных сообщений доступно меню (по правому клику):

* **Удалить** - удаляет сообщение
* **Редактировать** - переносит текст сообщения в поле ввода

#### Модальное окно поиска пользователей

**Активация и отображение**

Модальное окно появляется при нажатии на кнопку "Поиск" в левом столбце интерфейса чата. Оно реализовано с
использованием компонента `Modal` из Ant Design. Закрывается при клике вне окна или на крестик.

**Поле поиска**

В верхней части модального окна расположен инпут для ввода поискового запроса.

**Особенности работы:**

1. Пользователь начинает вводить текст (имя или email)
2. Применяется debounce с задержкой 300мс
3. После остановки ввода на 300мс отправляется запрос

**Механизм debounce**

Реализован с помощью хука `useDebouncedCallback`. Это предотвращает множественные запросы при каждом нажатии клавиши.

**Отображение результатов**

Результаты поиска выводятся в компоненте `UsersSearchList`.
Особенности:

* Отображается индикатор загрузки
* Список пользователей с аватарками и именами
* Пагинация при большом количестве результатов

**Логика взаимодействия с результатами поиска**

При клике на пользователя, с которым уже есть чат:

1. Модальное окно закрывается
2. В правой части интерфейса отображаются сообщения
3. Выбранный чат подсвечивается в списке слева

Если чата с выбранным пользователем нет:

1. В правой части отображается компонент `NewPrivateChat`
2. Отображается заглушка: "Начните общение - напишите первое сообщение"
3. При отправке сообщения:
    * Сначала создается новый чат
    * Затем отправляется сообщение
    * Чат добавляется в список слева
    * Интерфейс переключается в режим обычной переписки

#### Работа с WebSocket

**Инициализация соединения:**

```js
const client = new StompClient(accessToken, username, chat);
client.connect();
```

**Очистка при размонтировании:**

```js
return () => {
  client.disconnect();
};
```

### MobX-store

Приложение использует MobX для управления состоянием через два основных store:

1. **UserStore** - управление данными аутентификации и пользователя
2. **ChatStore** - управление данными чатов и сообщений

**UserStore: управление пользовательскими данными**

```js
{
  _isAuth: false,          // Флаг аутентификации
  _user: {},               // Данные текущего пользователя
  _isSearchUsers: false    // Флаг открытия поиска пользователей
}
```

**ChatStore: управление чатами и сообщениями**

```js
{
  _chats: [],             // Список чатов пользователя
  _isChatSelected: false, // Флаг выбора чата
  _selectedChat: {},      // Данные выбранного чата
  _messages: [],          // Сообщения текущего чата
  _messagesLoading: false // Флаг загрузки сообщений
}
```

**Принципы работы MobX в приложении**

* Все свойства помечены как observable с помощью `makeAutoObservable()`
* Компоненты оборачиваются в `observer()`
* UserStore, ChatStore доступны через useContext(Context)

```js
const {chat} = useContext(Context);
```

### WebSocket клиента (StompClient)

Класс `StompClient` реализует:

* Установку WebSocket соединения с сервером
* Подписку на персональные уведомления
* Обработку входящих уведомлений

Клиент создается с тремя ключевыми параметрами:

* Токен доступа (для авторизации)
* Имя пользователя
* Хранилище состояния чатов (для обновления интерфейса)

После успешного подключения клиент подписывается на два персональных канала уведомлений:

1. **Уведомления о чатах** - получает информацию о:
    * Создании новых чатов
    * Изменениях в существующих чатах
2. **Уведомления о сообщениях** - обрабатывает события:
    * Поступление новых сообщений
    * Редактирование существующих сообщений
    * Удаление сообщений

При получении уведомления:

* Данные парсятся из JSON-формата
* Определяется тип уведомления
* В зависимости от типа выполняются соответствующие действия

### Docker

Перед сборкой Docker-образа необходимо:

1. Установить все зависимости: `npm install`
2. Собрать приложение: `npm run build`

Сборка и запуск контейнера для текущей версии приложения (0.1.0):

```bash
docker build -t messenger/messenger-ui:0.1.0 .
docker run -p 3000:3000 --name messenger-ui messenger/messenger-ui:0.1.0
```

## PostgreSQL

**Запуск PostgreSQL в Docker**

Для локальной разработки база данных запускается следующей командой:

```bash
docker run --name messenger-db -p 5433:5432 \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=messenger\
  postgres:16
```

# Лицензия

Apache License. Подробнее см. в файле [LICENSE](LICENSE).




Docker Compose
Файл docker-compose.yaml разворачивает полную среду для разработки и тестирования платформы

Все Spring Boot приложение запускаются с профилем docker.

Запуск

Требуется предварительно собрать JAR-файл и и build у реакта

у и еще стоит написать что у меня есть 

вот какой вот контейнер
nginx:
image: nginx:stable-alpine
ports:
- "80:80"
volumes:
- './nginx.conf:/etc/nginx/nginx.conf'
depends_on:
- ui
networks:
- messenger-network

и вот его конфиг
user root;
worker_processes 1;

events {
}

http {
server {
listen 80;

        server_name localhost;

        location / {
            proxy_pass http://ui:3000;
        }

        location /api {
            proxy_pass http://server:8080;
        }
    }
}


Команда запуска

docker compose up -d


про конфиг в проде

также у меня есть папка в корпе проекта

config/prod

в котор 2 файла
docker-compose.yml
и
nginx.conf - это конфиг nginx

docker-compose.yml а докер комозе это тот файл который разворачивается в проде

вот так он выглядит
name: messenger
services:
db:
image: postgres:16
environment:
POSTGRES_USER: admin
POSTGRES_PASSWORD: admin
POSTGRES_DB: messenger
networks:
- messenger-network
server:
image: artem20fedorov00/messenger-server
restart: always
environment:
SPRING_PROFILES_ACTIVE: prod
depends_on:
- db
networks:
- messenger-network
ui:
image: artem20fedorov00/messenger-ui
restart: always
depends_on:
- server
networks:
- messenger-network
nginx:
image: nginx:stable-alpine
ports:
- "80:80"
volumes:
- './nginx.conf:/etc/nginx/nginx.conf'
depends_on:
- ui
networks:
- messenger-network
networks:
messenger-network:

основная суть в том

что я пушу образы своих этих двух приложений на свой докер хаб

и потом уже их разворачиваю в проде

про гитхаб воркфлоу

у меня есть 4
pipeline-build-messenger-ui.yml
pipeline-deploy-messenger-server.yml
pipeline-deploy-messenger-ui.yml
pipeline-test-messenger-server.yml

вот как они выглядят
name: build messenger ui

on:
pull_request:
branches: [ master ]
paths:
- messenger-ui/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-ui.yml
jobs:
build:
runs-on: ubuntu-latest
defaults:
run:
working-directory: ./messenger-ui

    steps:
      - name: Checkout repository
        uses: actions/checkout@v2

      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: 20

      - name: Install dependencies
        run: npm install

      - name: build
        run: CI=false npm run build
name: Deploy Messenger Server

on:
push:
branches: [ master ]
paths:
- messenger-server/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-server.yml
jobs:
test:
name: Run Unit/Integration Tests
runs-on: ubuntu-latest
steps:
- name: Checkout code
uses: actions/checkout@v4
with:
fetch-depth: 0

      - name: Setup JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'oracle'

      - name: Running Tests
        run: |
          cd messenger-server
          mvn clean verify

build:
runs-on: ubuntu-latest
name: Build Messenger Server
needs: [ test ]
steps:
- name: Checkout code
uses: actions/checkout@v4
with:
fetch-depth: 0

      - name: Setup JDK
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'oracle'

      - name: Compile project
        run: |
          cd messenger-server
          mvn clean package -DskipTests

build-image:
name: Build and Push Docker Image
runs-on: ubuntu-latest
needs: [ build ]
steps:
- name: Checkout code
uses: actions/checkout@v4

      - name: Setup JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'oracle'

      - name: Extract project version
        id: extract_version
        run: |
          cd messenger-server
          echo "VERSION=$( mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression="project.version" -q -DforceStdout)" >> $GITHUB_OUTPUT

      - name: Compile project
        run: |
          cd messenger-server
          mvn clean package

      - name: Login to DockerHub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Build and Push Docker Image
        uses: docker/build-push-action@v5
        with:
          context: .
          file: messenger-server/Dockerfile
          push: true
          platforms: linux/amd64
          tags: |
            ${{ secrets.DOCKERHUB_USERNAME }}/messenger-server:latest
            ${{ secrets.DOCKERHUB_USERNAME }}/messenger-server:${{ steps.extract_version.outputs.VERSION }}
          build-args: |
            JAR_FILE=messenger-server/target/messenger-server-${{ steps.extract_version.outputs.VERSION }}-exec.jar

deploy:
name: Deploy to VPS
runs-on: ubuntu-latest
needs: [ build-image ]
steps:
- name: Checkout code
uses: actions/checkout@v4

      - name: Set up SSH key
        run: |
          mkdir -p ~/.ssh
          echo "$SSH_PRIVATE_KEY" > ~/.ssh/id_rsa
          chmod 600 ~/.ssh/id_rsa
        env:
          SSH_PRIVATE_KEY: ${{ secrets.SSH_PRIVATE_KEY  }}

      - name: Add SSH known hosts
        run: |
          mkdir -p ~/.ssh
          ssh-keyscan -H ${{ secrets.VPS_IP }} >> ~/.ssh/known_hosts

      - name: Create deployment folder
        run: ssh ${{ secrets.VPS_USERNAME }}@${{ secrets.VPS_IP }} "mkdir -p messenger-app"

      - name: Copy docker-compose file
        run: |
          cd config/prod
          scp docker-compose.yml ${{ secrets.VPS_USERNAME }}@${{ secrets.VPS_IP }}:messenger-app/docker-compose.yml

      - name: Deploy Application to VPS
        run: |
          ssh ${{ secrets.VPS_USERNAME }}@${{ secrets.VPS_IP }} << 'EOF'
           cd messenger-app
           docker compose -f docker-compose.yml pull -q
           docker compose -f docker-compose.yml up -d
          EOF
- name: Deploy messenger ui

on:
push:
branches: [ master ]
paths:
- messenger-ui/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-ui.yml
jobs:
build-image:
name: Build Docker image
runs-on: ubuntu-latest
defaults:
run:
working-directory: ./messenger-ui
steps:
- name: Checkout code
uses: actions/checkout@v4
with:
fetch-depth: 0
- name: Extract project version
id: extract_version
run: |
echo "VERSION=$(jq -r '.version' package.json)" >> $GITHUB_OUTPUT
- name: Set up Node.js
uses: actions/setup-node@v4
with:
node-version: 20
- name: Install dependencies
run: npm install
- name: Build project
run: CI=false npm run build
- name: Login to DockerHub
uses: docker/login-action@v3
with:
username: ${{ secrets.DOCKERHUB_USERNAME }}
password: ${{ secrets.DOCKERHUB_TOKEN }}
- name: Build image & Push to DockerHub
uses: docker/build-push-action@v5
with:
context: messenger-ui
file: messenger-ui/Dockerfile
push: true
platforms: linux/amd64
tags: |
${{ secrets.DOCKERHUB_USERNAME }}/messenger-ui:latest
${{ secrets.DOCKERHUB_USERNAME }}/messenger-ui:${{ steps.extract_version.outputs.VERSION }}

deploy:
name: Deploy
runs-on: ubuntu-latest
needs: [ build-image ]
steps:
- name: Checkout code
uses: actions/checkout@v4

      - name: Set up SSH key
        run: |
          mkdir -p ~/.ssh
          echo "$SSH_PRIVATE_KEY" > ~/.ssh/id_rsa
          chmod 600 ~/.ssh/id_rsa
        env:
          SSH_PRIVATE_KEY: ${{ secrets.SSH_PRIVATE_KEY  }}

      - name: Add SSH known hosts
        run: |
          mkdir -p ~/.ssh
          ssh-keyscan -H ${{ secrets.VPS_IP }} >> ~/.ssh/known_hosts

      - name: Create deployment folder
        run: ssh ${{ secrets.VPS_USERNAME }}@${{ secrets.VPS_IP }} "mkdir -p messenger-app"

      - name: Copy docker-compose file
        run: |
          cd config/prod
          scp docker-compose.yml ${{ secrets.VPS_USERNAME }}@${{ secrets.VPS_IP }}:messenger-app/docker-compose.yml

      - name: Copy nginx.conf file
        run: |
          cd config/prod
          scp nginx.conf ${{ secrets.VPS_USERNAME }}@${{ secrets.VPS_IP }}:messenger-app/nginx.conf

      - name: Deploy to VPS
        run: |
          ssh ${{ secrets.VPS_USERNAME }}@${{ secrets.VPS_IP }} << 'EOF'
           cd messenger-app
           docker compose -f docker-compose.yml pull -q
           docker compose -f docker-compose.yml up -d
          EOF
name: Run Unit/Integration Tests for Messenger Server

on:
pull_request:
branches: [ master ]
paths:
- messenger-server/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-server.yml
jobs:
build:
runs-on: ubuntu-latest
defaults:
run:
working-directory: ./messenger-server

    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'oracle'

      - name: Run Unit Tests
        run: mvn clean test

      - name: Run Integration Tests
        run: mvn failsafe:integration-test


build messenger ui
срабатывате когда мы делаем PR в master
вот по таким путям
paths:
- messenger-ui/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-ui.yml

он проверяет собирается ли реакт приложение

Run Unit/Integration Tests for Messenger Server

pull_request:
branches: [ master ]
paths:
- messenger-server/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-server.yml

суть в том что мы последовательно запускаем 
юнит тесты а затем интеграционные

Deploy Messenger Server

push:
branches: [ master ]
paths:
- messenger-server/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-server.yml

сначала мы запускаем тесты

затем мы собираем проект

затем мы собираем образ и пушим его на докер хаб

затем разворачиваем новый образ на сервере

Deploy messenger ui

push:
branches: [ master ]
paths:
- messenger-ui/**
- 'docker-compose.yml'
- .github/workflows/*-messenger-ui.yml

мы собираем проект

затем мы собираем образ и пушим его на докер хаб

затем разворачиваем новый образ на сервере
