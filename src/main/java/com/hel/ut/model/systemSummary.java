/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

/**
 *
 * @author chadmccue
 */
public class systemSummary {

    int batchesPastHour = 0;
    int batchesToday = 0;
    int batchesThisWeek = 0;
    int batchesThisMonth = 0;
    int batchesInError = 0;
    int batchesToProcess = 0;

    public int getBatchesPastHour() {
        return batchesPastHour;
    }

    public void setBatchesPastHour(int batchesPastHour) {
        this.batchesPastHour = batchesPastHour;
    }

    public int getBatchesToday() {
        return batchesToday;
    }

    public void setBatchesToday(int batchesToday) {
        this.batchesToday = batchesToday;
    }

    public int getBatchesThisWeek() {
        return batchesThisWeek;
    }

    public void setBatchesThisWeek(int batchesThisWeek) {
        this.batchesThisWeek = batchesThisWeek;
    }

    public int getBatchesThisMonth() {
        return batchesThisMonth;
    }

    public void setBatchesThisMonth(int batchesThisMonth) {
        this.batchesThisMonth = batchesThisMonth;
    }

    public int getBatchesInError() {
        return batchesInError;
    }

    public void setBatchesInError(int batchesInError) {
        this.batchesInError = batchesInError;
    }

    public int getbatchesToProcess() {
        return batchesToProcess;
    }

    public void setbatchesToProcess(int batchesToProcess) {
        this.batchesToProcess = batchesToProcess;
    }
}
