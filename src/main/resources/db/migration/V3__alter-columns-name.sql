ALTER TABLE transfer CHANGE COLUMN origin_account_account_id origin_account_id BIGINT NOT NULL;
ALTER TABLE transfer CHANGE COLUMN destiny_account_account_id destiny_account_id BIGINT NOT NULL;
ALTER TABLE activity CHANGE COLUMN account_account_id account_id BIGINT NOT NULL;
ALTER TABLE account CHANGE COLUMN user_user_id user_id BIGINT NOT NULL;