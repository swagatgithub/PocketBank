package com.example.pocketbank.model;

import androidx.annotation.NonNull;

public class investment
{

    public String getInvestmentName() {
        return investmentName;
    }

    public void setInvestmentName(String investmentName) {
        this.investmentName = investmentName;
    }

    public String getStartDate() {
        return StartDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "investment{" +
                "investmentName='" + investmentName + '\'' +
                ", StartDate='" + StartDate + '\'' +
                ", finishDate='" + finishDate + '\'' +
                ", investmentAmount=" + investmentAmount +
                ", monthlyRateOfInterest=" + monthlyRateOfInterest +
                ", profit=" + profit +
                '}';
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Double getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(Double investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public Double getMonthlyRateOfInterest() {
        return monthlyRateOfInterest;
    }

    public void setMonthlyRateOfInterest(Double monthlyRateOfInterest) {
        this.monthlyRateOfInterest = monthlyRateOfInterest;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double monthlyProfit) {
        this.profit = monthlyProfit;
    }

    private String investmentName, StartDate , finishDate ;

    private Double investmentAmount , monthlyRateOfInterest , profit;

}
