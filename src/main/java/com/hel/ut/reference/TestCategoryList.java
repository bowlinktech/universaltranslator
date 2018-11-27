package com.hel.ut.reference;

import java.util.LinkedHashMap;
import java.util.Map;

public class TestCategoryList {

    @SuppressWarnings("rawtypes")
    public Map getCategories() throws Exception {

        LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();

        categories.put("cv", "Counseling Visit");
        categories.put("ms", "Medical Services");
        categories.put("proc", "Procedural Visit");
        categories.put("pv", "Preventive Visit");
        categories.put("vt", "Office Visit");

        return categories;
    }

}
