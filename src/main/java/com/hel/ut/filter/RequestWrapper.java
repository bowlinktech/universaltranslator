/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author chadmccue
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    public RequestWrapper(HttpServletRequest servletRequest) {
        super(servletRequest);
    }

    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        if (value == null) {
            return null;
        }

        return cleanXSS(value);
    }

    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }

        return cleanXSS(value);
    }

    private String cleanXSS(String value) {
        // You'll need to remove the spaces from the html entities below
        //value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
        //value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
        //value = value.replaceAll("'", "& #39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");

        value = value.replaceAll("(?i)<.*?script.*?>.*?<.*?script.*?>", "");
        value = value.replaceAll("(?i)<.*?script.*?>.*?<.*?/script.*?>", "");

        value = value.replaceAll("(?i)<.*?img*?<.*?/img.*?>", "");
        value = value.replaceAll("(?i)<.*?img.*?>", "");
        value = value.replaceAll("(?i)<.*?img.*?", "");
        value = value.replaceAll("(?i).*?src=.*?", "");

        value = value.replaceAll("(?i)<.*?iframe.*?<.*?/iframe.*?>", "");
        value = value.replaceAll("(?i)<.*?iframe.*?>", "");

        value = value.replaceAll("(?i)<script.*?>.*?<script.*?>", "");
        value = value.replaceAll("(?i)< script.*?>.*?<script.*?>", "");
        value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", "");
        value = value.replaceAll("(?i)< script.*?>.*?</script.*?>", "");
        value = value.replaceAll("(?i)<script.*?>.*?</ script.*?>", "");
        value = value.replaceAll("(?i)< script.*?>.*?</ script.*?>", "");
        value = value.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
        value = value.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");

        value = value.replaceAll("(?i).*?javascript.*?:.*? ", "");

        value = value.replaceAll("<script>", "");
        value = value.replaceAll("</script>", "");
        return value;
    }
}
