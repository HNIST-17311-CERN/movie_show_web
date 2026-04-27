package org.example.Entity;

import java.sql.Timestamp;

public class OperationLog {

    private Long id;
    private String username;
    private String method;
    private String operation;
    private String params;
    private String result;
    private String errorMsg;
    private Long executeTime;
    private Timestamp createTime;

    public OperationLog()
    {

    }


    public OperationLog(Long id, String username, String method, String operation, String params, String result, String errorMsg, Long executeTime, Timestamp createTime)
    {
        this.id = id;
        this.username = username;
        this.method = method;
        this.operation = operation;
        this.params = params;
        this.result = result;
        this.errorMsg = errorMsg;
        this.executeTime = executeTime;
        this.createTime = createTime;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    public String getParams()
    {
        return params;
    }

    public void setParams(String params)
    {
        this.params = params;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }

    public Long getExecuteTime()
    {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime)
    {
        this.executeTime = executeTime;
    }

    public Timestamp getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime)
    {
        this.createTime = createTime;
    }

    // getter / setter 省略（自己生成）
}