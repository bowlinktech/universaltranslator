package com.hel.ut.reference;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessCategoryList {

    @SuppressWarnings("rawtypes")
    public Map getCategories() throws Exception {

        LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();

        categories.put("batch", "Batch");
        categories.put("transaction", "Transaction");

        return categories;
    }

}
