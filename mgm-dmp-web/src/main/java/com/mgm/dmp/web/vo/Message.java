package com.mgm.dmp.web.vo;

import java.io.Serializable;

public class Message implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3968105626121162878L;
    private String type;
    private String code;
    private String msg;
    
    public Message(String type, String code, String msg){
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
