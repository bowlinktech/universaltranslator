package com.hel.ut.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.stereotype.Repository;

import com.hel.ut.dao.UtilitiesDAO;

import java.util.ResourceBundle;

@Repository
public class UtilitiesDAOImpl implements UtilitiesDAO {

    public Connection getConnection() {
        /**
         * we read properties from here*
         */
        ResourceBundle rb = ResourceBundle.getBundle("database");
        String user = rb.getString("jdbc.user");
        String JDBC_DRIVER = rb.getString("jdbc.driver");
        String url = rb.getString("jdbc.url");
        String pw = rb.getString("jdbc.password");

        String connStr = url + "?"
                + "user=" + user + "&password=" + pw;
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(connStr);

        } catch (Exception ex) {
            System.out.println("error for getConnection" + ex);
        }

        return conn;
    }

}
