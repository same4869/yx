package com.ml.yx.model;

/**
 * Created by Lijj on 16/4/5.
 */
public class AccountBean extends BaseBean {
    private String accountId;
    private String token;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
