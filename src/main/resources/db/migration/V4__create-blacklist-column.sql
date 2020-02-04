CREATE TABLE IF NOT EXISTS blacklist
(
    blacklist_id bigint      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_cpf     varchar(11) not null,
    is_approved  boolean,
    UNIQUE KEY UK_user_cpf (user_cpf)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;