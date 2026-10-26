package com.example.myapplication.model;

import java.io.Serializable;

/**
 * 金融记账交易实体模型
 */
public class FinanceRecord implements Serializable {
    private String type;        // 交易类型：支出 / 收入
    private double amount;      // 交易金额
    private String account;     // 结算账户渠道（如招行、微信等）
    private String remark;      // 交易备注
    private String time;        // 交易发生时间
    private boolean isBudget;   // 是否计入硬性预算
    private boolean isUrgent;   // 是否为重要/大额交易
    private boolean isRemind;   // 是否生成提醒

    public FinanceRecord(String type, double amount, String account, String remark, 
                         String time, boolean isBudget, boolean isUrgent, boolean isRemind) {
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.remark = remark;
        this.time = time;
        this.isBudget = isBudget;
        this.isUrgent = isUrgent;
        this.isRemind = isRemind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isBudget() {
        return isBudget;
    }

    public void setBudget(boolean budget) {
        isBudget = budget;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }

    public boolean isRemind() {
        return isRemind;
    }

    public void setRemind(boolean remind) {
        isRemind = remind;
    }
}
