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
public class fieldSelectOptions {

    private String optionValue;
    private String optionDesc = null;
    private String defaultValue;

    public String getoptionValue() {
        return optionValue;
    }

    public void setoptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public String getoptionDesc() {
        return optionDesc;
    }

    public void setoptionDesc(String optionDesc) {
        this.optionDesc = optionDesc;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
