-- =====================================================================
-- 宠物寄养洗护预约管理平台 · 建表 SQL
-- 骨架阶段包含：鉴权三表 + 门店/宠物/房型/笼位（M2）
-- 其余业务表（寄养订单/照护/结算/洗护/会员/评价售后）在 M3~M5 补充
-- =====================================================================

-- ---------- 用户与权限 ----------
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

-- ---------- 门店 ----------
CREATE TABLE IF NOT EXISTS store (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name           VARCHAR(100) NOT NULL COMMENT '门店名称',
    address        VARCHAR(255) NULL COMMENT '地址',
    phone          VARCHAR(20)  NULL COMMENT '电话',
    business_hours VARCHAR(100) NULL COMMENT '营业时间',
    status         TINYINT      DEFAULT 1 COMMENT '1营业 0停业',
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店表';

-- ---------- 宠物档案 ----------
CREATE TABLE IF NOT EXISTS pet (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    owner_id            BIGINT        NOT NULL COMMENT '宠物主用户ID',
    name                VARCHAR(50)   NOT NULL COMMENT '宠物名',
    species             VARCHAR(50)   NULL COMMENT '犬/猫/其他',
    breed               VARCHAR(50)   NULL COMMENT '品种',
    gender              TINYINT       NULL COMMENT '0未知 1公 2母',
    birth_date          DATE          NULL COMMENT '生日',
    weight              DECIMAL(6,2)  NULL COMMENT '体重(kg)',
    avatar_url          VARCHAR(255)  NULL COMMENT '头像',
    vaccine_expire_date DATE          NULL COMMENT '疫苗到期日',
    allergy_note        VARCHAR(500)  NULL COMMENT '过敏/注意事项',
    status              TINYINT       DEFAULT 1 COMMENT '1正常 0删除',
    created_at          DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

-- ---------- 房型与笼位 ----------
CREATE TABLE IF NOT EXISTS room_type (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    store_id    BIGINT        NOT NULL DEFAULT 1 COMMENT '门店ID（预留多门店）',
    name        VARCHAR(50)   NOT NULL COMMENT '标准间/豪华间/猫咪房',
    size        VARCHAR(50)   NULL COMMENT '面积/规格',
    daily_price DECIMAL(10,2) NOT NULL COMMENT '每日价格',
    description VARCHAR(255)  NULL COMMENT '描述',
    status      TINYINT       DEFAULT 1 COMMENT '1启用 0停用',
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_store (store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房型表';

CREATE TABLE IF NOT EXISTS cage (
    id           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    store_id     BIGINT      NOT NULL DEFAULT 1 COMMENT '门店ID',
    room_type_id BIGINT      NOT NULL COMMENT '房型ID',
    cage_no      VARCHAR(20) NOT NULL COMMENT '笼位号 A01',
    status       TINYINT     DEFAULT 1 COMMENT '1启用 2停用 3维修',
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_store_cage (store_id, cage_no),
    KEY idx_room_type (room_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='笼位表';
