CREATE TABLE IF NOT EXISTS user (
    user_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name varchar(200) NOT NULL,
    cpf varchar(11) NOT NULL,
    email varchar(100) NOT NULL,
    is_active boolean NOT NULL,
    UNIQUE KEY UK_cpf (cpf),
    UNIQUE KEY UK_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS account (
    account_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    balance double NOT NULL,
    acc_limit double NOT NULL,
    number varchar(10) NOT NULL,
    is_active boolean NOT NULL,
    user_user_id bigint NOT NULL,
    UNIQUE KEY UK_number (number),
    FOREIGN KEY (user_user_id) REFERENCES user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS activity (
    activity_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    activity_date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    value double NOT NULL,
    operation varchar(50) NOT NULL,
    account_account_id bigint NOT NULL,
    FOREIGN KEY (account_account_id) REFERENCES account(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS transfer (
    transfer_id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT,
    value double NOT NULL,
    date timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS transfer_account (
    transfer_id bigint NOT NULL,
    account_id bigint NOT NULL,
    FOREIGN KEY (transfer_id) REFERENCES transfer(transfer_id),
    FOREIGN KEY (account_id) REFERENCES account(account_id)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;