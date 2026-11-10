-- Am.Yo : schéma initial, issu du MCD de docs/conception.md

CREATE TABLE users (
    id            UUID         PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Am.Yo : owner_id nul seulement pour l'upload anonyme (US07)
CREATE TABLE files (
    id             UUID          PRIMARY KEY,
    owner_id       UUID          REFERENCES users (id) ON DELETE CASCADE,
    original_name  VARCHAR(255)  NOT NULL,
    mime_type      VARCHAR(255)  NOT NULL,
    size_bytes     BIGINT        NOT NULL CHECK (size_bytes >= 0),
    storage_key    VARCHAR(64)   NOT NULL UNIQUE,
    download_token VARCHAR(32)   NOT NULL UNIQUE,
    password_hash  VARCHAR(100),
    expires_at     TIMESTAMPTZ   NOT NULL,
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT now(),
    purged_at      TIMESTAMPTZ,
    CONSTRAINT files_expiry_max_7_days CHECK (expires_at <= created_at + INTERVAL '7 days')
);

CREATE INDEX files_owner_created_idx ON files (owner_id, created_at DESC);
CREATE INDEX files_expires_idx ON files (expires_at) WHERE purged_at IS NULL;

-- Am.Yo : tags optionnels (US08), sans doublon par fichier
CREATE TABLE file_tags (
    file_id UUID        NOT NULL REFERENCES files (id) ON DELETE CASCADE,
    label   VARCHAR(30) NOT NULL,
    PRIMARY KEY (file_id, label)
);
