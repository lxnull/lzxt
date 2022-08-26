package com.lx.xt.sso.model.params;

public enum LoginType {
    WX(0, "wx");

    private int code;

    private String msg;

    LoginType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
