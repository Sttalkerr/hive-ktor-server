# Hive Studio API Contract

Base path: `/api/v1`

## Auth

### `POST /auth/register`

Создаёт аккаунт продюсера.

### `POST /auth/login`

Выполняет вход продюсера.

Оба endpoints возвращают:

```json
{
  "id": "uuid",
  "email": "producer@example.com",
  "stageName": "Night Hive",
  "avatarUrl": "/uploads/avatar.jpg",
  "token": "bearer-token"
}
```

## Profile

### `GET /profile`

Возвращает текущий профиль продюсера.

### `PUT /profile`

Обновляет:

- `stageName`
- `bio`
- `city`
- `contactTag`

### `POST /profile/avatar`

Загружает аватар через `multipart/form-data`.

## Public Catalog

### `GET /catalog/beats`

Возвращает публичный каталог всех битов.

Query params:

- `query`

### `GET /catalog/beats/{beatId}`

Возвращает карточку публичного бита.

### `GET /catalog/beats/{beatId}/stats`

Возвращает публичную статистику бита.

### `GET /catalog/beats/{beatId}/history?days=7`

Возвращает историю аналитики по дням.

## Producer Beats

### `GET /beats`

Возвращает список битов авторизованного продюсера.

### `GET /beats/{beatId}`

Возвращает один бит автора.

### `POST /beats`

Создаёт бит через `multipart/form-data`.

Form fields:

- `title`
- `genre`
- `bpm`
- `price`
- `description`
- `mp3`
- `coverImage`

### `PUT /beats/{beatId}`

Редактирует метаданные бита:

- `title`
- `genre`
- `bpm`
- `price`
- `description`

### `DELETE /beats/{beatId}`

Удаляет бит.

## Statistics

### `GET /beats/{beatId}/stats`

Возвращает агрегированную статистику собственного бита.

### `GET /beats/{beatId}/history?days=7`

Возвращает историю аналитики по дням.

## Simulated Activity

### `POST /beats/{beatId}/simulate/play`

Добавляет прослушивание.

### `POST /beats/{beatId}/simulate/like`

Добавляет лайк.

### `POST /beats/{beatId}/simulate/purchase`

Добавляет покупку и увеличивает выручку.
