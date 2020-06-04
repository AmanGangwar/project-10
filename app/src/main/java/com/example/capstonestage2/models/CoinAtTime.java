package com.example.capstonestage2.models;

public class CoinAtTime extends Coin {

    private long atTime;

    public CoinAtTime(String name, double currentPrice) {
        super(name, currentPrice);
    }

    public long getAtTime() {
        return atTime;
    }

    public void setAtTime(long atTime) {
        this.atTime = atTime;
    }
}
