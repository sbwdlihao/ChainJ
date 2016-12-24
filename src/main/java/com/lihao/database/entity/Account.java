package com.lihao.database.entity;

/**
 * Created by sbwdlihao on 13/12/2016.
 */


public class Account {
    private String accountId;
    private String alias;
    private String tags;

    public Account() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
