package com.mobile.Smf.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import android.util.Log;

public class queryMySql implements Callable<ResultSet> {

    private String DB_URL, user, pass, instruction;

    public queryMySql(String DB_URL, String user, String pass, String sqlInstruction) {

        this.DB_URL = DB_URL;
        this.user = user;
        this.pass = pass;
        this.instruction = sqlInstruction;
    }

    @Override
    public ResultSet call() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, user, pass);
            //Log.d("queryMySql", "Connection established");

            Statement st = con.createStatement();

            ResultSet r = st.executeQuery(instruction);
            return r;


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
