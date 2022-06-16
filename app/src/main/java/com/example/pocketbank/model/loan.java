package com.example.pocketbank.model;

public class loan
{

    private String loanType , startDate , finishDate , lenderName;
    private double initialAmount , monthlyROI , monthlyPayment , loanRemainedAmount;

    public String getLoanType()
    {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public double getMonthlyROI() {
        return monthlyROI;
    }

    public void setMonthlyROI(double monthlyROI) {
        this.monthlyROI = monthlyROI;
    }

    public double getMonthlyPayment() {
        return monthlyPayment;
    }

    public void setMonthlyPayment(double monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public double getLoanRemainedAmount() {
        return loanRemainedAmount;
    }

    public void setLoanRemainedAmount(double loanRemainedAmount) {
        this.loanRemainedAmount = loanRemainedAmount;
    }
}
