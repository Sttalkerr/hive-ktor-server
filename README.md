# Hive Studio Server

Серверная часть курсового проекта `Hive Studio`, написанная на `Ktor`.

## Что реализовано

- авторизация продюсера
- профиль продюсера
- публичный каталог битов
- список собственных битов
- создание, редактирование и удаление бита
- загрузка `mp3` и квадратной обложки
- статистика по биту
- история аналитики по дням
- симуляция событий `play / like / purchase`
- хранение данных в `PostgreSQL`

## Технологии

- `Ktor`
- `Exposed`
- `PostgreSQL`
- `HikariCP`
- `Kotlinx Serialization`

## Структура данных

Основные сущности:

- `Producer`
- `Beat`
- `BeatStatistics`
- `BeatEvent`

## Конфигурация

Основной конфиг:

[`src/main/resources/application.yaml`](/Users/matthew/AndroidStudioProjects/kurs/hive-studio-server/src/main/resources/application.yaml)

Текущие параметры для локального запуска:

```yaml
database:
  jdbcUrl: "jdbc:postgresql://localhost:5432/hive_studio"
  user: "matthew"
  password: ""
  connectOnStartup: true
  showSql: false
```

## Запуск

1. Убедись, что локально поднят `PostgreSQL`.
2. Убедись, что существует база `hive_studio`.
3. Запусти сервер:

```bash
cd /Users/matthew/AndroidStudioProjects/kurs/hive-studio-server
./gradlew run
```

Сервер стартует на:

`http://localhost:8081`

## Проверка

Проверка health endpoint:

```bash
curl http://localhost:8081/health
```

Проверка тестов:

```bash
cd /Users/matthew/AndroidStudioProjects/kurs/hive-studio-server
./gradlew test
```

## Полезные документы

- API контракт: [`docs/api-contract.md`](/Users/matthew/AndroidStudioProjects/kurs/hive-studio-server/docs/api-contract.md)
- SQL схема: [`docs/postgresql-schema.sql`](/Users/matthew/AndroidStudioProjects/kurs/hive-studio-server/docs/postgresql-schema.sql)
- Сценарий демонстрации: [`docs/demo-flow.md`](/Users/matthew/AndroidStudioProjects/kurs/hive-studio-server/docs/demo-flow.md)
