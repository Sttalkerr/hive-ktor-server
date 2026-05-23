# Hive Studio API Contract

Base path: `/api/v1`

## Authentication

### `POST /auth/register`

Creates a producer account.

Request body:

```json
{
  "email": "producer@example.com",
  "password": "secret123",
  "stageName": "Night Hive"
}
```

Response `201 Created`:

```json
{
  "id": "uuid",
  "email": "producer@example.com",
  "stageName": "Night Hive",
  "token": "jwt-token"
}
```

### `POST /auth/login`

Authenticates the producer.

Request body:

```json
{
  "email": "producer@example.com",
  "password": "secret123"
}
```

Response `200 OK`:

```json
{
  "id": "uuid",
  "email": "producer@example.com",
  "stageName": "Night Hive",
  "token": "jwt-token"
}
```

## Profile

### `GET /profile`

Returns the current producer profile.

Response `200 OK`:

```json
{
  "id": "uuid",
  "email": "producer@example.com",
  "stageName": "Night Hive",
  "createdAt": "2026-05-24T00:00:00Z"
}
```

## Beats

### `GET /beats`

Returns a producer beat list with search and sorting.

Query params:

- `query` - search by beat title
- `genre` - optional genre filter
- `sort` - `newest`, `oldest`, `title`

Response `200 OK`:

```json
[
  {
    "id": "uuid",
    "title": "Midnight Pulse",
    "genre": "Trap",
    "bpm": 140,
    "price": 29.99,
    "description": "Dark trap beat",
    "mp3FileName": "midnight-pulse.mp3",
    "createdAt": "2026-05-24T00:00:00Z"
  }
]
```

### `GET /beats/{beatId}`

Returns a single beat card.

### `POST /beats`

Creates a beat using `multipart/form-data`.

Form fields:

- `title`
- `genre`
- `bpm`
- `price`
- `description`
- `mp3`

Response `201 Created`:

```json
{
  "id": "uuid",
  "title": "Midnight Pulse",
  "genre": "Trap",
  "bpm": 140,
  "price": 29.99,
  "description": "Dark trap beat",
  "mp3FileName": "midnight-pulse.mp3",
  "createdAt": "2026-05-24T00:00:00Z"
}
```

### `DELETE /beats/{beatId}`

Deletes a beat and its statistics.

Response `204 No Content`

## Statistics

### `GET /beats/{beatId}/stats`

Returns aggregated beat statistics.

Response `200 OK`:

```json
{
  "beatId": "uuid",
  "playsCount": 124,
  "likesCount": 37,
  "purchasesCount": 9,
  "revenueTotal": 269.91,
  "updatedAt": "2026-05-24T00:00:00Z"
}
```

## Simulated Activity

Because the system has only one user role, statistics are created by service-side activity simulation.

### `POST /beats/{beatId}/simulate/play`

Adds one play event.

### `POST /beats/{beatId}/simulate/like`

Adds one like event.

### `POST /beats/{beatId}/simulate/purchase`

Adds one purchase event and increases revenue by the beat price.
