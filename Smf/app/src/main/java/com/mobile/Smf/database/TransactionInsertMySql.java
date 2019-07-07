package com.mobile.Smf.database;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class TransactionInsertMySql implements Callable<Boolean> {

    private Connection con;
    private PreparedStatement[] input;
    private String[] params;
    private byte[] pic;

    private boolean debug = false;
    private String Tag = "TransactionInsertMySql";

   /*
   * MAKE THIS NICE!!!!
   * */

    public TransactionInsertMySql(byte[] pic,String ... params) {
        this.pic = pic;
        this.params = params;
        input = new PreparedStatement[params.length-3];
    }


    public Boolean call() {

        Savepoint save1 = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(params[0], params[1], params[2]);

            con.setAutoCommit(false);

            save1 = con.setSavepoint("save1");

            System.out.println(Tag + " " +input.length);

            if(input.length > 1) {
                for (int i = 0; i < input.length; i++) {
                    input[i] = con.prepareStatement(params[i + 3]);
                    input[i].execute();
                }
            } else {
                input[0] = con.prepareStatement(params[3]);
                if(pic != null) {
                    input[0].setBytes(1, pic);
                    if(!debug) {
                        Log.d(Tag,"pic size ="+pic.length+" input[0] = "+input[0]);
                    }
                }
                input[0].execute();
            }

            con.commit();

            } catch (ClassNotFoundException exce) { exce.printStackTrace(); System.out.println("failed in ClassNotFoundException TMSql");}
              catch (SQLException ex) { ex.printStackTrace();
                                    try {
                                        con.rollback(save1);
                                        return false;
                                        } catch(SQLException e){
                                            throw new RuntimeException("Failed to rollback changes " +
                                                "in TransactionInsertMySql with arguments:"+ Arrays.toString(input)+ "\n");
                                            }
                                    }
        finally {
                try {
                    for (int i = 0; i < input.length; i++) {
                        if (input[i] != null)
                                input[i].close();
                        }

                    if(con != null) {
                        con.setAutoCommit(true);
                        con.close();
                    }



                    } catch (SQLException e) {e.printStackTrace(); System.out.println("failed in finally TMSql");}
                }
        return true;
    }
}
