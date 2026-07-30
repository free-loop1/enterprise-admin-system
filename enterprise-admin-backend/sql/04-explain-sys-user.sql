USE enterprise_admin;

-- 1. 分析根据主键查询用户详情的执行计划
EXPLAIN FORMAT = TRADITIONAL
SELECT id,
       username,
       nickname,
       phone,
       email,
       status
FROM sys_user
WHERE id = 1
  AND deleted = 0;
-- 结论：
-- type 为 const，实际使用 PRIMARY，预计只检查 1 行。
-- 根据主键查询用户详情能够高效利用主键索引。

-- 2. 分析根据用户名精确查询用户的执行计划
EXPLAIN FORMAT = TRADITIONAL
SELECT id,
       username,
       nickname,
       phone,
       email,
       status
FROM sys_user
WHERE username = 'alice'
  AND deleted = 0;
-- 结论：
-- username 精确查询使用 uk_sys_user_username 唯一索引。
-- type 为 const，预计只检查 1 行。
-- 唯一索引除了防止用户名重复，也能加速用户名精确查询。

-- 3. 分析用户名包含式模糊查询的执行计划
EXPLAIN FORMAT = TRADITIONAL
SELECT id,
       username,
       nickname,
       phone,
       email,
       status
FROM sys_user
WHERE username LIKE '%ali%'
  AND deleted = 0
ORDER BY id DESC
    LIMIT 10;
-- 结论：
-- LIKE '%ali%' 的前导百分号导致 username 唯一索引无法用于快速定位。
-- possible_keys 为 NULL，MySQL 扫描 PRIMARY 是为了满足 id DESC 排序。
-- type=index 表示索引扫描，不是 const 类型的索引精确查找。

-- 4. 分析用户名前缀查询
EXPLAIN FORMAT = TRADITIONAL
SELECT id,
       username,
       nickname,
       phone,
       email,
       status
FROM sys_user
WHERE username LIKE 'ali%'
  AND deleted = 0;
-- 结论：
-- LIKE 'ali%' 没有前导百分号，可以利用 username 索引进行范围查询。
-- type 为 range，实际使用 uk_sys_user_username。
-- 前缀查询通常比包含式查询 LIKE '%ali%' 更容易利用 B+Tree 索引。

-- 5. 联合索引与最左匹配原则
-- 联合索引示例，仅用于学习，暂时不执行：
-- CREATE INDEX idx_sys_user_status_created_at
--     ON sys_user (status, created_at);

-- 可以从联合索引最左侧开始使用：
-- WHERE status = 1

-- 同时提供第一列和第二列，可以继续利用索引：
-- WHERE status = 1
--   AND created_at >= '2026-07-01'

-- 缺少最左侧 status，通常无法有效利用该联合索引：
-- WHERE created_at >= '2026-07-01'

-- 当前 sys_user 暂不新增联合索引，原因：
-- 1. id 主键已经支持详情、修改和删除。
-- 2. username 唯一索引支持重复检查、精确查询和前缀查询。
-- 3. 当前没有稳定的多字段组合查询需求。
-- 4. status 和 deleted 的可选值较少，区分度较低。
-- 5. (username, id) 通常是冗余索引。
-- 6. 额外索引会增加新增、修改和删除数据时的维护成本。