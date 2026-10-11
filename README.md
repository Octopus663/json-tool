# JSON Tool

Веб-застосунок на Spring Boot для створення, валідації (синтаксис і JSON Schema), перегляду з підсвіткою та експорту JSON-документів.

## Технології

- Java 21, Spring Boot 3 (Web, Data JPA, Security, Thymeleaf, Validation)
- MySQL (основна БД), H2 (тести)
- Jackson + [networknt json-schema-validator](https://github.com/networknt/json-schema-validator) (JSON Schema draft-07)

## Запуск

1. Створіть базу даних MySQL `jsontooldb`.
2. Задайте змінні оточення (або створіть файл `application-local.properties` у корені проєкту — він у `.gitignore`):

   | Змінна | Опис | За замовчуванням |
   |---|---|---|
   | `DB_URL` | JDBC URL | `jdbc:mysql://localhost:3306/jsontooldb` |
   | `DB_USERNAME` | користувач БД | — |
   | `DB_PASSWORD` | пароль БД | — |
   | `API_USERNAME` | логін для REST API | `admin` |
   | `API_PASSWORD` | пароль для REST API | генерується при старті й пишеться в лог |

   Приклад `application-local.properties`:

   ```properties
   spring.datasource.username=myuser
   spring.datasource.password=mypassword
   app.api.password=secret
   ```

3. Запустіть:

   ```bash
   ./mvnw spring-boot:run
   ```

   Застосунок буде доступний на http://localhost:8080.

## Тести

```bash
./mvnw verify
```

Тести використовують вбудовану БД H2, тому MySQL для них не потрібен.

## Сторінки

| URL | Опис |
|---|---|
| `/` | Головна |
| `/documents` | Список документів, експорт, скасування останнього створення |
| `/documents/new` | Редактор з валідацією |
| `/documents/{id}` | Перегляд з підсвіткою синтаксису |
| `/documents/{id}/export/markdown`, `/documents/{id}/export/txt` | Експорт |
| `/documents/highlight-demo` | Демонстрація Flyweight |

## REST API (HTTP Basic)

| Метод | URL | Опис |
|---|---|---|
| `GET` | `/api/documents` | Список документів |
| `GET` | `/api/documents/{id}` | Документ за id (404, якщо не знайдено) |
| `POST` | `/api/documents` | Створити документ: `{"name": "...", "content": "{...}"}` → `201 Created` або `400` |

```bash
curl -u admin:$API_PASSWORD http://localhost:8080/api/documents
```

Консольний клієнт: `com.example.jsontool.client.JsonToolClient` (читає `API_USERNAME`, `API_PASSWORD`, `API_URL`).

## Патерни проєктування

| Патерн | Де |
|---|---|
| Strategy | `validation/Validation`, `SyntaxValidation`, `SchemaValidation`, контекст — `service/ValidationService` |
| Command | `command/Command`, `CreateDocumentCommand`, invoker — `CommandManager` (undo) |
| Observer | `observer/*`, subject — `service/JsonEditorService` |
| Template Method | `template/DocumentExporter`, `MarkdownExporter`, `TxtExporter` |
| Flyweight | `flyweight/JsonStyleFactory`, `JsonStyle`, `JsonToken`, використання — `service/SyntaxHighlighterService` |

> Історія команд (undo) спільна для всіх користувачів застосунку.

## Звіти

Звіти до лабораторних робіт лежать у каталозі [`docs/`](docs/).
