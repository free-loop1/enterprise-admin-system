USE enterprise_admin;

-- 阶段 3：为用户表增加 Token 版本字段。
-- 初始值为 0；退出、修改密码和重置密码后递增。
ALTER TABLE sys_user
    ADD COLUMN token_version int unsigned NOT NULL DEFAULT 0
        COMMENT 'Token 版本号，用于使旧 Token 失效'
        AFTER status,
    ADD UNIQUE KEY uk_sys_user_phone (phone),
    ADD UNIQUE KEY uk_sys_user_email (email);

-- 验证 token_version 字段。
SHOW COLUMNS
FROM sys_user
LIKE 'token_version';

-- 验证用户表索引。
SHOW INDEX
FROM sys_user;