ALTER TABLE chat_message
    ADD COLUMN prompt_tokens INT NOT NULL DEFAULT 0,
    ADD COLUMN completion_tokens INT NOT NULL DEFAULT 0,
    ADD COLUMN total_tokens INT NOT NULL DEFAULT 0;
