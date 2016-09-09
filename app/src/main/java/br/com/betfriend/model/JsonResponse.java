package br.com.betfriend.model;

/**
 * Created by vitorbr on 09/09/16.
 */
public class JsonResponse {

    private Integer code;

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
