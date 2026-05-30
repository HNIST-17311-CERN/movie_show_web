package org.example.Entity;

import java.time.LocalDateTime;

public class Resource_Submission
{

    /*
    CREATE TABLE resource_submission (
      id            BIGINT AUTO_INCREMENT PRIMARY KEY   COMMENT '提交ID',
      movie_id      BIGINT        NOT NULL              COMMENT '关联电影ID',
      movie_name    VARCHAR(255)  NULL                  COMMENT '电影名称（冗余，方便审核时看）',
      name          VARCHAR(255)  NOT NULL              COMMENT '资源名称',
      url           VARCHAR(1000) NOT NULL              COMMENT '下载地址',
      type          VARCHAR(20)   DEFAULT '磁力'        COMMENT '资源类型：磁力/网盘',
      quality       VARCHAR(50)                          COMMENT '画质（720P/1080P/4K/蓝光）',
      size          VARCHAR(50)                          COMMENT '文件大小',
      submitter     VARCHAR(100)                         COMMENT '提交者用户名',
      submitter_id  BIGINT        NULL                  COMMENT '提交者用户ID',
      status        VARCHAR(20)   DEFAULT 'pending'     COMMENT '审核状态：pending/approved/rejected',
      review_msg    VARCHAR(500)                         COMMENT '管理员拒绝理由',
      note          VARCHAR(500)                         COMMENT '提交者备注',
      create_time   DATETIME      DEFAULT NOW()          COMMENT '提交时间'
      ) COMMENT '用户资源提交审核表';
     */

    private int id;
    private int movie_id;
    private String movie_name;
    private String resouce_name;
    private String url;
    private String type;
    private String quality;
    private String size;
    private String submitter;
    private Long submitter_id;
    private String status;
    private String review_msg;
    private String note;
    private LocalDateTime createTime;


    public Resource_Submission()
    {}

    public Resource_Submission(int id, int movie_id, String movie_name, String resouce_name, String url, String type, String quality, String size, String submitter, String status, String review_msg, LocalDateTime createTime)
    {
        this.id = id;
        this.movie_id = movie_id;
        this.movie_name = movie_name;
        this.resouce_name = resouce_name;
        this.url = url;
        this.type = type;
        this.quality = quality;
        this.size = size;
        this.submitter = submitter;
        this.status = status;
        this.review_msg = review_msg;
        this.createTime = createTime;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getMovie_id()
    {
        return movie_id;
    }

    public void setMovie_id(int movie_id)
    {
        this.movie_id = movie_id;
    }

    public String getMovie_name()
    {
        return movie_name;
    }

    public void setMovie_name(String movie_name)
    {
        this.movie_name = movie_name;
    }

    public String getResouce_name()
    {
        return resouce_name;
    }

    public void setResouce_name(String resouce_name)
    {
        this.resouce_name = resouce_name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getQuality()
    {
        return quality;
    }

    public void setQuality(String quality)
    {
        this.quality = quality;
    }

    public String getSize()
    {
        return size;
    }

    public void setSize(String size)
    {
        this.size = size;
    }

    public String getSubmitter()
    {
        return submitter;
    }

    public void setSubmitter(String submitter)
    {
        this.submitter = submitter;
    }

    public Long getSubmitter_id() { return submitter_id; }
    public void setSubmitter_id(Long submitter_id) { this.submitter_id = submitter_id; }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getReview_msg()
    {
        return review_msg;
    }

    public void setReview_msg(String review_msg)
    {
        this.review_msg = review_msg;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime)
    {
        this.createTime = createTime;
    }
}
