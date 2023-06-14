package br.com.igormartinez.potygames.exceptions;

import java.io.Serializable;

public class ExceptionResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance;

    public ExceptionResponse(String type, String title, Integer status, String detail, String instance) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    public String toJsonString(){
        return "{\"type\":\""+type+"\",\"title\":\""+title+"\",\"status\":\""+status+"\",\"detail\":\""+detail+"\",\"instance\":\""+instance+"\"}";
    }
}
