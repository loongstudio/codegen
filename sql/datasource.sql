CREATE TABLE IF NOT EXISTS `datasource`
(
    `id`            bigint NOT NULL,
    `name`          varchar(128)      DEFAULT NULL,
    `type`          int               DEFAULT 0,
    `url`           varchar(255)      DEFAULT NULL,
    `ip`            int UNSIGNED      DEFAULT NULL,
    `port`          SMALLINT UNSIGNED DEFAULT NULL,
    `username`      varchar(50)       DEFAULT NULL,
    `password`      varchar(50)       DEFAULT NULL,
    `have_deleted`  tinyint(1)        DEFAULT 0,
    `description`   varchar(255)      DEFAULT NULL,
    `created_at`    datetime          DEFAULT NULL,
    `updated_at`    datetime          DEFAULT NULL,
    `created_by`    varchar(128)      DEFAULT NULL,
    `updated_by`    varchar(128)      DEFAULT NULL,
    PRIMARY KEY (`id`)
);