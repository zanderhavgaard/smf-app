package com.mobile.Smf.database;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mobile.Smf.model.PicturePost;
import com.mobile.Smf.model.Post;
import com.mobile.Smf.model.TextPost;
import com.mobile.Smf.model.User;
import com.mobile.Smf.util.Timestamp;

import java.io.ByteArrayOutputStream;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.graphics.Point;
import android.util.Log;


public class MySql {
    //DB instance
    private static MySql singleton = null;
    //MySql connect values
    private static final String DB = "smf";
    private static final String user = "godtUserName";
    private static final String pass = "godtPassword";
    private static final String DB_URL = "jdbc:mysql://mydb.itu.dk/" + DB;

    private boolean deBug = true;
    private final String Tag = "MySql";

    //Thread related
    private static ExecutorService service;


    private MySql() {
        service = Executors.newCachedThreadPool();
    }

    public static MySql getMySql() {
        if (singleton == null) {
            singleton = new MySql();
        }
        return singleton;
    }


    //Interface

    public boolean checkIfValidLogin(String userName, String password) {

        boolean returnVal = false;
        try {
            Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass, String.format(Locale.getDefault(),
                "SELECT userID, userName, password FROM Users WHERE userName = '%s' AND password = '%s';",
                        userName, password)));

            ResultSet rs = f.get();

            rs.first();
            if (rs.getInt("userID") > 0 && rs.getString("password").equals(password) && rs.getString("userName").equals(userName))
                returnVal = true;

        } catch (SQLException exc) { exc.printStackTrace(); }
        catch (InterruptedException e) {e.printStackTrace(); }
        catch (ExecutionException ex) {ex.printStackTrace(); }

        return returnVal;
    }


    public boolean checkIfValidNewUser(String userName, String email) {

        Boolean returnVal = false;

        try {
            Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass,
                String.format("SELECT COUNT(*) FROM Users WHERE username = '%s' OR email = '%s';", userName, email)));

            ResultSet rs = f.get();

            rs.first();
            Log.d("ValidNewUser-result ",""+rs.getInt(1));
            rs.first();
            if (rs.getInt(1) == 0)
                returnVal = true;

        } catch (SQLException exc) { exc.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        catch (ExecutionException ex) { ex.printStackTrace(); }

        return returnVal;
    }

    public User getUser(String userName) {
        User returnVal = null;

        try {
            Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass, String.format(Locale.getDefault(),
                    "SELECT u.userID, u.userName, u.password, u.email, c.countryName, u.birthYear, u.countryID  FROM Users u " +
                            "LEFT JOIN Countries c ON u.countryID = c.countryID WHERE userName = '%s';", userName)));

            ResultSet rs = f.get();

            rs.first();
            returnVal = new User(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getInt(6),rs.getInt(7));

        } catch (InterruptedException e) { e.printStackTrace(); }
        catch (ExecutionException ex) { ex.printStackTrace(); }
        catch (SQLException exc) { exc.printStackTrace(); }

    return returnVal;
    }

    public User addNewUser(String userName, String password, String email, String country, int birthYear) {

        User returnVal = null;
        int id = 0;
        int countryID = 0;

        try {
                Future<Boolean> f = service.submit(new InsertMySql(DB_URL, user, pass, String.format(Locale.getDefault(),
                    "INSERT INTO Users (userName, password, email, countryID, birthYear) " +
                            "VALUES ('%s','%s','%s', " +
                            "(SELECT countryID FROM Countries WHERE countryName = '%s'), %d);",
                            userName, password, email, country, birthYear)));

                if ( f.get()) {
                //will refactor this to a sql function later on
                    Future<ResultSet> fx = service.submit(new queryMySql(DB_URL, user, pass,
                            String.format(Locale.getDefault(), "SELECT userID,countryID FROM Users WHERE email = '%s' AND userName = '%s';", email, userName)));
                    ResultSet rs = fx.get();

                    rs.first();
                    id = rs.getInt(1);
                    countryID = rs.getInt(2);

                }

            } catch (InterruptedException e) { e.printStackTrace(); }
              catch (ExecutionException ex) { ex.printStackTrace(); }
              catch (SQLException exc) { exc.printStackTrace(); }

            if (id > 0 && countryID > 0)
                returnVal = new User(id, userName, password, email, country, birthYear, countryID);

        return returnVal;
    }

    // DEPRECATED - DO NOT USE UNLESS QUERY AND LAST ARGUMENT IS FIXED
    public User getLoggedInUser(int userID) {

        User newUser = null;
        try {
            Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass, String.format(Locale.getDefault(),
                    "SELECT * FROM User WHERE userID = %d;", userID)));

            ResultSet rs = f.get();

            rs.first();
            if(rs.getInt(1) == userID)
                newUser = new User(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6),-1);

        } catch (InterruptedException e) {e.printStackTrace(); }
          catch (ExecutionException ex) {ex.printStackTrace();}
          catch (SQLException exc) {exc.printStackTrace(); }

        return newUser;
    }


    public boolean uploadTextPost(int userID, String postText, long timestamp, String localTime, String universalTime) {
        boolean successfulTransaction = false;
        try{

            String arg1 = String.format(Locale.getDefault(),"SELECT insertTextPost (%d,0,%d,'%s','%s','%s');"
                    ,userID, timestamp, postText,localTime,universalTime);


            Future<Boolean> fPosts = service.submit(new TransactionInsertMySql(null,DB_URL, user, pass,arg1));

            successfulTransaction = fPosts.get();

            if(deBug) {
                Log.d(Tag, "uploadTextPost sql argument = " + arg1);
                Log.d(Tag, "uploadTextPost: " + successfulTransaction);
            }

        }
        catch(RuntimeException e) {System.out.println(e.getMessage()); e.printStackTrace();}
        catch(ExecutionException ex) {ex.printStackTrace();}
        catch(InterruptedException exc) {exc.printStackTrace();}

        return successfulTransaction;
    }


    public boolean uploadPicturePost(int userID, Bitmap picture, long timestamp, String localTime, String universalTime) {

        boolean successfulTransaction = false;

        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            // if better picture quality is needed play with quality parameter
            picture.compress(Bitmap.CompressFormat.JPEG,50,stream);

            byte[] pic = stream.toByteArray();

            String arg1 = String.format(Locale.getDefault(),"SELECT insertPicturePost (%d,1,%d,?,'%s','%s');"
                    ,userID, timestamp,localTime,universalTime);


            Future<Boolean> fPosts = service.submit(new TransactionInsertMySql(pic,DB_URL, user, pass,arg1));

            successfulTransaction = fPosts.get();

            if(deBug) {
                Log.d(Tag,"pic size = "+pic.length+" insertPicturePost arg = "+arg1+" transaction success = "+successfulTransaction);
            }

        }
        catch(RuntimeException e) {System.out.println(e.getMessage()); e.printStackTrace();}
        catch(ExecutionException ex) {ex.printStackTrace();}
        catch(InterruptedException exc) {exc.printStackTrace();}

        return successfulTransaction;
    }




    public ArrayList<Post> getInitialPosts(int userID) {

        ArrayList<Post> returnList = new ArrayList<>();

        try {
            String arg = String.format(Locale.getDefault(),"SELECT p.postType, p.postID, u.userName, p.tStamp, " +
                    "p.universalTimeStamps, p.localTimeStamps, t.postText, pic.picture, l.likes, IF(p.postID IN " +
                    "(SELECT postID FROM LikeRelationship WHERE LikeRelationship.postID = p.postID AND " +
                    "LikeRelationship.userID = %d ), 1, 0) AS clicked FROM Posts p " +
                    "INNER JOIN Users u ON p.userID = u.userID LEFT JOIN TextPosts t ON p.postID = t.postID " +
                    "LEFT JOIN PicturePosts pic ON p.postID = pic.postID LEFT JOIN Likes l ON p.postID = l.postID " +
                    "ORDER BY p.tStamp DESC LIMIT 10;",userID);
            if (!deBug) {
                Log.d("getInitialPosts",arg);
            }

            Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass, arg));
            ResultSet rs = f.get();

            rs.first();
            if(!rs.next())
                return returnList;

            rs.first();

            while(!rs.isAfterLast()) {
                int eval = rs.getInt(1);
                if(eval == 0) {
                    System.out.println("Likes: "+rs.getInt(9));
                    returnList.add(new TextPost(rs.getInt(2), rs.getString(3),rs.getLong(4),rs.getString(7),
                            rs.getString(6),rs.getString(5),rs.getInt(9),rs.getInt(10) != 0 ));
                } else if (eval == 1) {
                    byte[] bytePic = rs.getBytes(8);
                    Bitmap pic = BitmapFactory.decodeByteArray(bytePic, 0, bytePic.length);
                    returnList.add(new PicturePost(rs.getInt(2), rs.getString(3),rs.getLong(4),pic, rs.getString(6),
                            rs.getString(5),rs.getInt(9),rs.getInt(10) != 0));
                }

                rs.next();
            }

        }catch(ExecutionException e) {e.printStackTrace();}
        catch(InterruptedException ex) {ex.printStackTrace();}
        catch (SQLException exc) {exc.printStackTrace(); }

        if(deBug) {
            System.out.println("POSTCHECK!!!!!!!");
            for (Post p : returnList) {
                if (p.getPostType() == 0) {
                    System.out.println("id: " + p.getPostID() + " name: " + p.getUserName() + " TimeStamp: " + p.getTimeStamp() + " text: " + ((TextPost) p).getText()
                            + " Likes: " + p.getlikes() + " isClicked: " + p.getClicked());
                } else {
                    System.out.println("id: " + p.getPostID() + " name: " + p.getUserName() + " TimeStamp: " + p.getTimeStamp() + " PicturePost "
                            + " Likes: " + p.getlikes() + " isClicked: " + p.getClicked());
                }
            }
        }
    return returnList;

    }

    public ArrayList<Post> getSpecificNumberOfNewerPosts(int numberOfPosts, long timestamp, int userID) {

        ArrayList<Post> returnList = new ArrayList<>();

        try {
            String arg =  String.format(Locale.getDefault(),"SELECT p.postType, p.postID, u.userName, p.tStamp, " +
                            "p.universalTimeStamps, p.localTimeStamps, t.postText, pic.picture, IF(p.postID IN " +
                            "(SELECT postID FROM LikeRelationship WHERE LikeRelationship.postID = p.postID AND " +
                            "LikeRelationship.userID = %d ), 1, 0) AS clicked FROM Posts p " +
                            "INNER JOIN Users u ON p.userID = u.userID LEFT JOIN TextPosts t ON p.postID = t.postID " +
                            "LEFT JOIN PicturePosts pic ON p.postID = pic.postID LEFT JOIN Likes l ON p.postID = l.postID " +
                            "WHERE p.tStamp > %d ORDER BY p.tStamp DESC LIMIT %d;",userID,timestamp,numberOfPosts);

            Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass, arg));
            ResultSet rs = f.get();

            if (rs.next() == false || rs == null)
                return returnList;

            rs.first();

            while(!rs.isAfterLast()) {
                int eval = rs.getInt(1);
                if(eval == 0) {
                    returnList.add(new TextPost(rs.getInt(2), rs.getString(3),rs.getLong(4),rs.getString(7), rs.getString(6),
                            rs.getString(5),rs.getInt(9),rs.getInt(10) != 0));
                } else if (eval == 1) {
                    byte[] bytePic = rs.getBytes(8);
                    Bitmap pic = BitmapFactory.decodeByteArray(bytePic, 0, bytePic.length);
                    returnList.add(new PicturePost(rs.getInt(2), rs.getString(3),rs.getLong(4),pic, rs.getString(6),
                            rs.getString(5),rs.getInt(9),rs.getInt(10) != 0));
                }

                rs.next();
            }

        }catch(ExecutionException e) {e.printStackTrace();}
        catch(InterruptedException ex) {ex.printStackTrace();}
        catch (SQLException exc) {exc.printStackTrace(); }

        return returnList;
    }

    public ArrayList<Post> getSpecificNumberOfLowerPosts(int numberOfPosts, int oldestPostId, int userID) {

        ArrayList<Post> returnList = new ArrayList<>();

        try {

            String arg = String.format(Locale.getDefault(),
            "SELECT p.postType, p.postID, u.userName, p.tStamp, p.universalTimeStamps, p.localTimeStamps, t.postText, " +
                    "pic.picture, l.likes, IF(p.postID IN (SELECT postID FROM LikeRelationship WHERE LikeRelationship.postID = %d " +
                    "AND LikeRelationship.userID = %d ) , 1, 0) AS clicked FROM Posts p INNER JOIN Users u ON p.userID = u.userID " +
                    "LEFT JOIN TextPosts t ON p.postID = t.postID LEFT JOIN PicturePosts pic ON p.postID = pic.postID " +
                    "LEFT JOIN Likes l ON p.postID = l.postID WHERE p.tStamp < (SELECT tStamp FROM Posts WHERE postID = %d) " +
                    "ORDER BY p.tStamp DESC LIMIT %d;",
                    oldestPostId,userID,oldestPostId,numberOfPosts);

            ResultSet rs = null;

            try {
                Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass, arg));
                rs = f.get();

                if (rs.isBeforeFirst()) {

                    rs.first();

                    while (!rs.isAfterLast()) {

                        int eval = rs.getInt(1);
                        if (eval == 0) {
                            returnList.add(new TextPost(rs.getInt(2), rs.getString(3), rs.getLong(4), rs.getString(7),
                                    rs.getString(6), rs.getString(5), rs.getInt(9), rs.getInt(10) != 0));
                        } else if (eval == 1) {
                            byte[] bytePic = rs.getBytes(8);
                            Bitmap pic = BitmapFactory.decodeByteArray(bytePic, 0, bytePic.length);
                            returnList.add(new PicturePost(rs.getInt(2), rs.getString(3), rs.getLong(4), pic, rs.getString(6),
                                    rs.getString(5), rs.getInt(9), rs.getInt(10) != 0));
                        }

                        rs.next();
                    }
                }
            } finally{
                if(rs != null)
                    rs.close();
            }

        }catch(ExecutionException e) {e.printStackTrace();}
        catch(InterruptedException ex) {ex.printStackTrace();}
        catch (SQLException exc) {exc.printStackTrace(); }

        return returnList;
    }

    /*
     * TODO, do this in sqlite to
     * */
    @SuppressLint("DefaultLocale")
    public boolean addLikes(List<Point> likedPosts, int userID) {
        boolean returnVal = false;

        System.out.println(likedPosts.toString());

        String[] args = new String[(likedPosts.size()*2)+3];

        args[0] = DB_URL;
        args[1] = user;
        args[2] = pass;

        int j = 3;
        for(int i = 0; i < likedPosts.size(); i++) {

            if(likedPosts.get(i).x > 0) {
            args[j++] = String.format("UPDATE Likes SET likes = (likes + %d) WHERE postID = %d AND postID NOT IN " +
                    "(SELECT postID FROM LikeRelationship WHERE postID = %d AND userID = %d);",
                    likedPosts.get(i).x,likedPosts.get(i).y,likedPosts.get(i).y, userID);


                args[j++] = String.format("INSERT IGNORE INTO LikeRelationship (postID, userID) VALUES (%d,%d);", likedPosts.get(i).y, userID);
                System.out.println("fell into positive LikeRelationship case");
            }
            else {
                args[j++] = String.format("UPDATE Likes SET likes = (likes + %d) WHERE postID = %d AND postID IN " +
                                "(SELECT postID FROM LikeRelationship WHERE postID = %d AND userID = %d);",
                        likedPosts.get(i).x,likedPosts.get(i).y,likedPosts.get(i).y, userID);

                args[j++] = String.format("DELETE FROM LikeRelationship WHERE postID = %d AND userID = %d;", likedPosts.get(i).y, userID);
            }
        }
        System.out.println("args: "+Arrays.toString(args));
        if(!deBug)
            System.out.println("ARGUMENTS FROM MYSQL TO TRANSACTIONS : "+Arrays.toString(args));

        try {
            Future<Boolean> f = service.submit(new TransactionInsertMySql(null, args));

            returnVal = f.get();

        } catch (ExecutionException e) {e.printStackTrace();}
        catch (InterruptedException ex) {ex.printStackTrace();}

        return returnVal;
    }

    /*
     * Remember to do this in sqlite to
     * */
    public List<Point> getLikes(List<Point> postIDsAtY) {

        if(!deBug) {
            Log.d("getLikes","LIST SIZE CHECK: "+postIDsAtY.isEmpty());
        }

        List<Point> returnList = new ArrayList<>();

        StringBuilder sb = new StringBuilder("(");
        for(int i = 0; i < postIDsAtY.size()-1; i++) {
            sb.append(postIDsAtY.get(i).y +",");
        }
        sb.append(postIDsAtY.get(postIDsAtY.size()-1).y +")");

        if(!deBug) {
            System.out.println("getLikes sb args -> "+sb.toString());
            Log.d("getLikes",sb.toString());
        }

        String arg = String.format( "SELECT postID,likes FROM Likes WHERE postID IN %s;",sb.toString());

        try {
            Future<ResultSet> f = service.submit(new queryMySql(DB_URL,user,pass,arg));
            ResultSet rs = f.get();

            rs.first();
            while (!rs.isAfterLast()) {
                returnList.add(new Point(rs.getInt(1),rs.getInt(2)));
                rs.next();
            }

        } catch (ExecutionException e) {e.printStackTrace();}
        catch (InterruptedException ex) {ex.printStackTrace();}
        catch (SQLException exc) {exc.printStackTrace();}

        return returnList;
    }



    /*
    * DEPRECATED METHODS
    * */
    public ArrayList<Post> getAllPosts() {
        ArrayList<Post> returnList = new ArrayList<>();

        try {         //THIS QUERY HAS NOT BEEN UPDATED WITH LIKED AND CLICKED BOOLEAN
            String arg = "SELECT p.postType, p.postID, u.userName, p.tStamp, p.universalTimeStamps, p.localTimeStamps, t.postText, pic.picture " +
                    "FROM Posts p INNER JOIN Users u ON p.userID = u.userID LEFT JOIN TextPosts t ON p.postID = t.postID " +
                    "LEFT JOIN PicturePosts pic ON p.postID = pic.postID ORDER BY p.tStamp DESC;";

            Future<ResultSet> f = service.submit(new queryMySql(DB_URL, user, pass,arg));
            ResultSet rs = f.get();

            rs.first();

            while(!rs.isAfterLast()) {
                int eval = rs.getInt(1);
                if(eval == 0) {
                    returnList.add(new TextPost(rs.getInt(2), rs.getString(3),rs.getLong(4),rs.getString(7),
                            rs.getString(6),rs.getString(5),rs.getInt(9),rs.getInt(10)!= 0));
                } else if (eval == 1) {
                    byte[] bytePic = rs.getBytes(8);
                    Bitmap pic = BitmapFactory.decodeByteArray(bytePic, 0, bytePic.length);
                    returnList.add(new PicturePost(rs.getInt(2), rs.getString(3),rs.getLong(4),pic, rs.getString(6),
                            rs.getString(5), rs.getInt(9),rs.getInt(10)!= 0));
                }

                rs.next();
            }

        } catch(ExecutionException e) {e.printStackTrace();}
        catch(InterruptedException ex) {ex.printStackTrace();}
        catch (SQLException exc) {exc.printStackTrace(); }

        return returnList;
    }

}


