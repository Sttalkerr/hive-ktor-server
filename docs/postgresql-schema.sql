create extension if not exists "pgcrypto";

create table if not exists producers (
    id uuid primary key default gen_random_uuid(),
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    stage_name varchar(120) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists beats (
    id uuid primary key default gen_random_uuid(),
    producer_id uuid not null references producers(id) on delete cascade,
    title varchar(160) not null,
    genre varchar(80) not null,
    bpm integer not null check (bpm between 40 and 260),
    price numeric(10, 2) not null check (price >= 0),
    description text not null,
    mp3_file_name varchar(255) not null,
    mp3_storage_path text not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists beat_statistics (
    beat_id uuid primary key references beats(id) on delete cascade,
    plays_count integer not null default 0 check (plays_count >= 0),
    likes_count integer not null default 0 check (likes_count >= 0),
    purchases_count integer not null default 0 check (purchases_count >= 0),
    revenue_total numeric(12, 2) not null default 0 check (revenue_total >= 0),
    updated_at timestamptz not null default now()
);

create table if not exists beat_events (
    id uuid primary key default gen_random_uuid(),
    beat_id uuid not null references beats(id) on delete cascade,
    event_type varchar(20) not null check (event_type in ('play', 'like', 'purchase')),
    event_value numeric(12, 2) not null default 0 check (event_value >= 0),
    created_at timestamptz not null default now()
);

create index if not exists idx_beats_producer_id on beats(producer_id);
create index if not exists idx_beats_title on beats(title);
create index if not exists idx_beat_events_beat_id on beat_events(beat_id);
create index if not exists idx_beat_events_created_at on beat_events(created_at);
