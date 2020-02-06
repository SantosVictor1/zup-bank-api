CREATE TABLE IF NOT EXISTS waitlist
(
    waitlist_id bigint      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_cpf     varchar(11) not null,
    status  varchar(20),
    message varchar(100),
    UNIQUE KEY UK_user_cpf (user_cpf)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;