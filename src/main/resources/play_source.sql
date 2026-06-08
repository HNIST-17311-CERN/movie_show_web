-- 在线播放资源表
CREATE TABLE movie_play_source (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY    COMMENT '资源ID',
    movie_id     BIGINT       NULL                    COMMENT '关联电影ID（可选）',
    url          VARCHAR(1000) NOT NULL               COMMENT '文件路径',
    name         VARCHAR(255)  NULL                   COMMENT '资源名称（前端显示）',
    create_time  DATETIME     DEFAULT NOW()           COMMENT '创建时间'
);
