CREATE TABLE IF NOT EXISTS `template`
(
    `id`              bigint NOT NULL,
    `datasource_id`   bigint NOT NULL,
    `name`            varchar(100) DEFAULT NULL,
    `folder`          varchar(255) DEFAULT NULL,
    `parent_package`  varchar(255) DEFAULT NULL,
    `module`          varchar(100) DEFAULT NULL,
    `datasource_name` varchar(100) DEFAULT NULL,
    `database_name`   varchar(100) DEFAULT NULL,
    `table_name`      varchar(100) DEFAULT NULL,
    `have_deleted`    tinyint(1)   DEFAULT 0,
    `description`     varchar(255) DEFAULT NULL,
    `created_at`      datetime     DEFAULT NULL,
    `updated_at`      datetime     DEFAULT NULL,
    `created_by`      varchar(128) DEFAULT NULL,
    `updated_by`      varchar(128) DEFAULT NULL,
    PRIMARY KEY (`id`)
);