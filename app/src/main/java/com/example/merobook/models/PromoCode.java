package com.example.merobook.models;

import java.util.Date;

public class PromoCode {
    private String code;
    private int coins;
    private Date validFrom;
    private Date validTill;

    public PromoCode(String code, int coins, Date validFrom, Date validTill) {
        this.code = code;
        this.coins = coins;
        this.validFrom = validFrom;
        this.validTill = validTill;
    }

    public String getCode() {
        return code;
    }

    public int getCoins() {
        return coins;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public Date getValidTill() {
        return validTill;
    }
}

