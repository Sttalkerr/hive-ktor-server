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