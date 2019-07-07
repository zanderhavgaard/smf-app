package com.mobile.Smf.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mobile.Smf.model.PicturePost;
import com.mobile.Smf.model.Post;
import com.mobile.Smf.model.TextPost;
import com.mobile.Smf.model.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static android.database.Cursor.FIELD_TYPE_BLOB;
import static android.database.Cursor.FIELD_TYPE_FLOAT;
import static android.database.Cursor.FIELD_TYPE_INTEGER;
import static android.database.Cursor.FIELD_TYPE_NULL;
import static android.database.Cursor.FIELD_TYPE_STRING;

public class SqLite extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SmfSqLite";


    private static SQLiteDatabase mydatabase;

    private static SqLite sqLite;

    private SqLite(Context context) {
        super(context, DATABASE_NAME, null, 1);
        mydatabase = context.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        onCreate(mydatabase);
    }

    public static  SqLite getSqLite(Context context) {
        if(sqLite == null)
            sqLite = new SqLite(context);
        return sqLite;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        setUpSchemas();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        Cursor res = null;
        try {
        res = db.rawQuery("select * from Profile_info ", null);

            //and other tables if
            //db.execSQL(String.format("DROP TABLE IF EXISTS %s", "Profile_info"));
            dropAllTables();
        } catch(SQLException e) {e.printStackTrace();}
        onCreate(db);
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",res.getInt(0));
        contentValues.put("name",res.getString(1));
        contentValues.put("password",res.getString(2));
        contentValues.put("email",res.getString(3));
        contentValues.put("country",res.getString(4));
        contentValues.put("birthYear",res.getInt(5));
        long eval = db.insert("Profile_info", null, contentValues);
        if(eval == -1)
            throw new RuntimeException("Exception while onUpgrade SqLite");

    }

//Use this to set up all
    public void setUpSchemas() {
        try {
            //mydatabase.execSQL("DROP TABLE IF EXISTS Profile_info;");
            //mydatabase.execSQL("DROP TABLE IF EXISTS PostsSync;");
            //mydatabase.execSQL("DROP TABLE IF EXISTS TextPostsSync;");
            //mydatabase.execSQL("DROP TABLE IF EXISTS PicturePostsSync;");

            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Profile_info (id int, name VARCHAR(100) NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, email VARCHAR(100) NOT NULL, " +
                    "country VARCHAR(100), birthYear int, countryID int, UNIQUE (id));");

            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS PostsSync (postID int NOT NULL, postType int NOT NULL," +
                    "userName VARCHAR(100), tStamp INTEGER NOT NULL, uniTime VARCHAR(14), locTime VARCHAR(14), UNIQUE (postID));");

            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS TextPostsSync (postID int NOT NULL, postText VARCHAR(145) NOT NULL, UNIQUE(postID));");

            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS PicturePostsSync (postID int NOT NULL, picture BLOB NOT NULL, UNIQUE (postID));");

            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS LikesSync (postID int NOT NULL, likes int NOT NULL, clicked int NOT NULL, UNIQUE(postID));");

        } catch(SQLException e) {e.printStackTrace();}
    }


    //Interface

    public boolean syncProfileInfoFromMySql(User user) {

        setUpSchemas();
        boolean returnVal = false;

        System.out.println("Sync profile  -->  userID "+user.getId()+" "+user.getUserName());

            ContentValues contentValues = new ContentValues();
            contentValues.put("id", user.getId());
            contentValues.put("name", user.getUserName());
            contentValues.put("password", user.getPassword());
            contentValues.put("email", user.getEmail());
            contentValues.put("country", user.getCountry());
            contentValues.put("birthYear", user.getBirthYear());
            contentValues.put("countryID", user.getCountryID());

            long eval = mydatabase.insert("Profile_info", null, contentValues);
                if(eval != -1) {
                    returnVal = true;
                }

        return returnVal;
    }

    public User getLoggedInUser() {
        User returnVal = null;

        if(checkTable("Profile_info")) {
            //printAllTables();

            try {
                Cursor res = mydatabase.rawQuery("SELECT * FROM Profile_info;", null);

                if (((res == null) || (res.getCount() == 0))) {
                    return returnVal;
                }

                res.moveToFirst();
                returnVal = new User(res.getInt(0), res.getString(1), res.getString(2),
                        res.getString(3), res.getString(4), res.getInt(5), res.getInt(6));

            } catch (SQLException e) { e.printStackTrace();}
        }

        return returnVal;
    }



    public String getUserName() {

        String userName = null;
        try {
            Cursor res = mydatabase.rawQuery("SELECT name FROM Profile_info ;", null);
            userName = res.getString(0);

        } catch (SQLException e) {e.printStackTrace();}
        return userName;
    }

    public int getUserID() {

        int userID = -1;
        try {
            Cursor res = mydatabase.rawQuery("SELECT userID FROM Profile_info ;", null);
            userID = res.getInt(0);

        } catch (SQLException e) {e.printStackTrace();}
        return userID;
    }



    public boolean dropAllTables() {
        //printAllTables();
        Cursor cursor = mydatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        try {
            List<String> tables = new ArrayList<>(cursor.getCount());

            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0));
            }

            for (String table : tables) {
                if (table.startsWith("sqlite_") || table.startsWith("android_metadata")) {
                    continue;
                }
                mydatabase.execSQL("DROP TABLE IF EXISTS " + table);
            }
            return true;

        } finally {
            cursor.close();
        }
    }


    public boolean addToPosts(List<Post> list) {

        boolean returnVal = false;

        String argPost = "INSERT OR IGNORE INTO PostsSync (postID,postType,userName,tStamp,uniTime,locTime) VALUES (?,?,?,?,?,?);";
        String argTextPost = "INSERT OR IGNORE INTO TextPostsSync (postID,postText) VALUES (?,?);";
        String argPicturePost = "INSERT OR IGNORE INTO PicturePostsSync (postID, picture) VALUES (?,?);";
        String argLikes = "INSERT OR IGNORE INTO LikesSync (postID, likes, clicked) VALUES (?,?,?);";

        try {
            mydatabase.beginTransaction();
            SQLiteStatement stmtPost = mydatabase.compileStatement(argPost);
            SQLiteStatement stmtTextPost = mydatabase.compileStatement(argTextPost);
            SQLiteStatement stmtPicturePost = mydatabase.compileStatement(argPicturePost);
            SQLiteStatement stmtLikes = mydatabase.compileStatement(argLikes);

            for(Post p : list) {
                stmtPost.bindLong(1,p.getPostID());
                stmtPost.bindLong(2,p.getPostType());
                stmtPost.bindString(3,p.getUserName());
                stmtPost.bindLong(4,p.getTimeStamp());
                stmtPost.bindString(5,p.getUniversalTimeStamp());
                stmtPost.bindString(6,p.getLocalTimeStamp());
                stmtPost.execute();
                stmtPost.clearBindings();

                stmtLikes.bindLong(1,p.getPostID());
                stmtLikes.bindLong(2,p.getlikes());
                stmtLikes.bindLong(3,p.getClicked() ? 1 : 0);
                stmtLikes.execute();
                stmtLikes.clearBindings();

                if(p.getPostType() == 0) {
                    stmtTextPost.bindLong(1,p.getPostID());
                    stmtTextPost.bindString(2,((TextPost) p).getText());
                    stmtTextPost.execute();
                    stmtTextPost.clearBindings();

                } else if(p.getPostType() == 1) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    // if better picture quality is needed play with quality parameter
                    ((PicturePost)p).getPicture().compress(Bitmap.CompressFormat.JPEG,50,stream);

                    byte[] pic = stream.toByteArray();
                    stmtPicturePost.bindLong(1,p.getPostID());
                    stmtPicturePost.bindBlob(2,pic);
                    stmtPicturePost.execute();
                    stmtPicturePost.clearBindings();
                }
            }

            mydatabase.setTransactionSuccessful();

        } catch(IllegalStateException e) {e.printStackTrace();}
        catch (SQLException ex) {ex.printStackTrace();}

        finally {
            mydatabase.endTransaction();
            returnVal = true;
        }

        return returnVal;
    }


    public List<Post> getOlderPosts(long timeStamp) {

        List<Post> returnList = new ArrayList<>();

        try {
            Cursor c = mydatabase.rawQuery(String.format(Locale.getDefault(),
                    "SELECT p.postType, p.postID, p.userName, p.tStamp, p.uniTime, p.locTime, t.postText," +
                            " pic.picture, l.likes, l.clicked" +
                    " FROM PostsSync p LEFT JOIN TextPostsSync t ON p.postID = t.postID" +
                    " LEFT JOIN PicturePostsSync pic ON p.postID = pic.postID LEFT JOIN LikeSync l ON" +
                    " p.postID = l.postID " +
                    " WHERE p.tStamp < %d ORDER BY p.tStamp DESC;",timeStamp),null);

            c.moveToFirst();
            while(!c.isAfterLast()) {
                if(c.getInt(0) == 0) {
                        returnList.add(new TextPost(c.getInt(1),c.getString(2),c.getLong(3),c.getString(6),c.getString(4),
                                c.getString(5),c.getInt(8),c.getInt(9) != 0 ));
                } else if(c.getInt(0) == 1){
                    byte[] bytePic = c.getBlob(7);
                    Bitmap pic = BitmapFactory.decodeByteArray(bytePic, 0, bytePic.length);
                    returnList.add(new PicturePost(c.getInt(1),c.getString(2),c.getLong(3), pic, c.getString(4),
                            c.getString(5),c.getInt(8),c.getInt(9) != 0 ));
                }
                c.moveToNext();
            }
            c.close();

        } catch (Exception e) {e.printStackTrace();}

        return returnList;
    }

    public List<Post> getNewerPosts(long timeStamp) {
        List<Post> returnList = new ArrayList<>();

        try {
            Cursor c = mydatabase.rawQuery(String.format(Locale.getDefault(),
                    "SELECT p.postType, p.postID, p.userName, p.tStamp, p.uniTime, p.locTime, t.postText," +
                    " pic.picture, l.likes, l.clicked" +
                    " FROM PostsSync p LEFT JOIN TextPostsSync t ON p.postID = t.postID" +
                    " LEFT JOIN PicturePostsSync pic ON p.postID = pic.postID LEFT JOIN LikeSync l ON" +
                    " p.postID = l.postID " +
                    " WHERE p.tStamp > %d ORDER BY p.tStamp DESC;",timeStamp),null);

            c.moveToFirst();
            while(!c.isAfterLast()) {
                if(c.getInt(0) == 0) {
                    returnList.add(new TextPost(c.getInt(1),c.getString(2),c.getLong(3),c.getString(6),c.getString(4),
                            c.getString(5),c.getInt(8),c.getInt(9) != 0));
                } else if(c.getInt(0) == 1){
                    byte[] bytePic = c.getBlob(6);
                    Bitmap pic = BitmapFactory.decodeByteArray(bytePic, 0, bytePic.length);
                    returnList.add(new PicturePost(c.getInt(1),c.getString(2),c.getLong(3), pic, c.getString(4),
                            c.getString(5),c.getInt(8),c.getInt(9) != 0));
                }
                c.moveToNext();
            }
            c.close();

        } catch (Exception e) {e.printStackTrace();}

        return returnList;
    }

    // Deprecated - delete when sure it is not going to be used
    public boolean checkIfValidLogin(String userName, String password) {

        Cursor cursor = mydatabase.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='Profile_info';", null);

        if(cursor == null)
            return false;
        else if(cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            cursor.close();
            Cursor cur = mydatabase.rawQuery("SELECT * FROM Profile_info;", null);
            cur.moveToFirst();
            if ((cur.getString(1).equals(userName) && cur.getString(2).equals(password))) {
                cur.close();
                return true;
            }
            cur.close();
        }
        return false;
    }

    // Helper methods

    private void print(Cursor c) {
        System.out.println("PRINTCHECK");
        if(((c != null) && (c.getCount() > 0))) {
            c.moveToFirst();

            int rowNumb = 0;
            while(!c.isAfterLast()) {
                int collumCount = c.getColumnCount();
                System.out.println("Row numb : "+rowNumb++);
                for(int i = 0; i < collumCount; i++) {
                    switch(c.getType(i)){
                        case FIELD_TYPE_INTEGER:
                            System.out.println("Integer at index "+i+" : "+c.getInt(i));
                            break;
                        case FIELD_TYPE_NULL:
                            System.out.println("Null at index "+i+" : Null");
                            break;
                        case FIELD_TYPE_FLOAT:
                            System.out.println("Float at index "+i+" : "+c.getFloat(i));
                            break;
                        case FIELD_TYPE_BLOB:
                            System.out.println("Blob at index "+i+" : "+c.getBlob(i));
                            break;
                        case FIELD_TYPE_STRING:
                            System.out.println("String at index "+i+" : "+c.getString(i));
                            break;
                    }
                }
                c.moveToNext();
            }
        } else {
            System.out.println("Printcheck: c="+ c +" c count ="+c.getCount());
        }
    }



    public boolean checkTable(String table) {
            Cursor c = mydatabase.rawQuery(
                    String.format("SELECT name FROM sqlite_master WHERE type = 'table' AND name = '%s';",table),null);
            return c.getCount() != 0;
    }

    private void printAllTables() {
        if(checkTable("Profile_info")) {

            Cursor c1 = mydatabase.rawQuery(
                    "SELECT * FROM Profile_info;", null);
            System.out.println("Profile_info:");
            print(c1);
            Cursor c2 = mydatabase.rawQuery(
                    "SELECT * FROM PostsSync;", null);
            System.out.println("PostsSync:");
            print(c2);
            Cursor c3 = mydatabase.rawQuery(
                    "SELECT * FROM TextPostsSync;", null);
            System.out.println("TextPostsSync:");
            print(c3);
            Cursor c4 = mydatabase.rawQuery(
                    "SELECT * FROM PicturePostsSync;", null);
            System.out.println("PicturePostsSync:");
            print(c4);
            Cursor c5 = mydatabase.rawQuery(
                    "SELECT * FROM LikesSync;", null);
            System.out.println("LikesSync:");
            print(c5);
        } else
            System.out.println("No tables to print");
    }

}


