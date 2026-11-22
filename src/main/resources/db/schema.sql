-- =====================================================================
-- 宠物寄养洗护预约管理平台 · 建表 SQL（骨架阶段：鉴权三表）
-- 完整表结构见《开发设计文档》§6；业务表（宠物/房型笼位/订单/照护/结算/洗护/会员/评价售后）
-- 在对应里程碑（M2~M5）按需补充。
-- =====================================================================

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NULL COMMENT '登录名（员工/管理员）',
    password    VARCHAR(100) NULL COMMENT 'BCrypt 密码（微信用户为空）',
    openid      VARCHAR(64)  NULL COMMENT '微信 openid（宠物主）',
    unionid     VARCHAR(64)  NULL COMMENT '微信 unionid',
    nickname    VARCHAR(50)  NULL COMMENT '昵称',
    phone       VARCHAR(20)  NULL COMMENT '手机号',
    avatar_url  VARCHAR(255) NULL COMMENT '头像 URL',
    user_type   TINYINT      NOT NULL COMMENT '1宠物主 2员工 3管理员',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_openid (openid),
    KEY idx_user_type (user_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_code   VARCHAR(50)  NOT NULL COMMENT '角色编码',
    role_name   VARCHAR(50)  NOT NULL COMMENT '角色名称',
    description VARCHAR(255) NULL COMMENT '描述',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
