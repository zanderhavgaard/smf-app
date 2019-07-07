package com.mobile.Smf.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class InsertMySql implements Callable<Boolean> {

    private String DB_URL, user, pass, instruction;

    public InsertMySql(String DB_URL, String user, String pass, String sqlInstruction) {

        this.DB_URL = DB_URL;
        this.user = user;
        this.pass = pass;
        this.instruction = sqlInstruction;
    }

    public Boolean call() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, user, pass);

            Log.d("InsertMySql", "Connection established");

            Statement st = con.createStatement();

            Log.d("Insert - instruction", instruction);

            int eval = st.executeUpdate(instruction);

            if(eval < 1)
                return false;

        } catch (ClassNotFoundException e) { e.printStackTrace(); return false;}
        catch (SQLException ex) { ex.printStackTrace(); return false; }
        return true;
    }
}