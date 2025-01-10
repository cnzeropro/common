DROP TABLE IF EXISTS `user`;

CREATE TABLE `user`
(
  id          BIGINT      NOT NULL COMMENT '用户标识',
  name        VARCHAR(30) NULL DEFAULT NULL COMMENT '用户名',
  password    VARCHAR(30) NULL DEFAULT NULL COMMENT '密码',
  gender      TINYINT     NULL DEFAULT NULL COMMENT '性别。1：男；2：女；3：保密',
  status      TINYINT     NULL DEFAULT NULL COMMENT '状态。1：正常；2：锁定；3：冻结；4：销户',
  create_by   BIGINT      NULL DEFAULT NULL COMMENT '创建人',
  create_time TIMESTAMP   NULL DEFAULT NULL COMMENT '创建时间',
  update_by   BIGINT      NULL DEFAULT NULL COMMENT '更新人',
  update_time TIMESTAMP   NULL DEFAULT NULL COMMENT '更新时间',
  version     INT         NULL DEFAULT NULL COMMENT '版本号',
  is_deleted  BOOLEAN     NULL DEFAULT NULL COMMENT '是否删除',
  PRIMARY KEY (id)
);
