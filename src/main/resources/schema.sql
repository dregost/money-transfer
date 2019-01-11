CREATE TABLE IF NOT EXISTS account_event (
  id           BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
  aggregate_id VARCHAR(36) NOT NULL,
  event        CLOB        NOT NULL
);

CREATE TABLE IF NOT EXISTS transfer_event (
  id           BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
  aggregate_id VARCHAR(36) NOT NULL,
  event        CLOB        NOT NULL
);

CREATE TABLE IF NOT EXISTS account (
  id     VARCHAR(36) NOT NULL PRIMARY KEY,
  balance DECIMAL     NOT NULL
);

CREATE TABLE IF NOT EXISTS transfer (
  id              VARCHAR(36) NOT NULL PRIMARY KEY,
  from_account_id VARCHAR(36) NOT NULL,
  to_account_id   VARCHAR(36) NOT NULL,
  amount          DECIMAL     NOT NULL,
  status          VARCHAR(10)
);