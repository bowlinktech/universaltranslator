/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model.custom;

import java.util.Date;

/**
 *
 * @author chadmccue
 */
public class searchParameters {

    Date fromDate;
    Date toDate;
    String searchTerm = "";
    int page = 1;
    String section = "";
    Integer viewOnly = 0;

    public Date getfromDate() {
        return fromDate;
    }

    public void setfromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date gettoDate() {
        return toDate;
    }

    public void settoDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getsearchTerm() {
        return searchTerm;
    }

    public void setsearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public int getpage() {
        return page;
    }

    public void setpage(int page) {
        this.page = page;
    }

    public String getsection() {
        return section;
    }

    public void setsection(String section) {
        this.section = section;
    }

    public Integer getViewOnly() {
        return viewOnly;
    }

    public void setViewOnly(Integer viewOnly) {
        this.viewOnly = viewOnly;
    }

}
