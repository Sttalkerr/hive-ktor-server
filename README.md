# Hive Studio Server

Ktor API for the Hive Studio coursework project.

## Current Stage

The repository currently contains:

- Ktor scaffold with a working `GET /health` endpoint
- initial PostgreSQL schema draft
- initial API contract draft

## Domain Model

The server is built around a single role: producer.

Core entities:

- `Producer` - account owner who uploads and manages beats
- `Beat` - uploaded music item with metadata and an MP3 file
- `BeatStatistics` - aggregated analytics for a beat
- `BeatEvent` - atomic event used to build statistics

## Planned API Areas

- authentication
- producer profile
- beats catalog and search
- MP3 upload and beat deletion
- analytics and simulated activity events
