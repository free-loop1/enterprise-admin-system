USE enterprise_admin;
CREATE TABLE sys_user
(
    id         bigint unsigned not null auto_increment comment '用户主键',
    username   varchar(50)  not null comment '登录用户名',
    password   varchar(255) not null comment '密码哈希存储',
    nickname   varchar(50)  not null comment '用户昵称',
    phone      varchar(20)           default null comment '手机号码',
    email      varchar(100)          default null comment '电子邮箱',
    status     tinyint unsigned not null default 1 comment '状态：0-禁用，1-正常',
    created_at datetime     not null default current_timestamp comment '创建时间',
    updated_at datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    deleted    tinyint unsigned not null default 0 comment '逻辑删除：0-未删除，1-已删除',
    primary key (id),
    unique key uk_sys_user_username (username),
    constraint chk_sys_user_status check ( status in (0, 1) ),
    constraint chk_sys_user_deleted check (deleted in (0, 1))
) engine = innodb
    default character set = utf8mb4
    collate = utf8mb4_0900_ai_ci
    comment = '系统用户表';
