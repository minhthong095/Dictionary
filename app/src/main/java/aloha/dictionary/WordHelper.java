package aloha.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class WordHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "EVDict2.db";
    private static final int DB_VERSION =1;
    private static final String DB_PATH_SUFFIX = "/databases/";

    static Context ctx;
    Cursor cursor;

    public WordHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctx = context;
    }
	
	public SQLiteDatabase openDB() throws SQLiteException {
        File dbFile = ctx.getDatabasePath(DB_NAME);//??

                if(!dbFile.exists()){
            try {
                CopyDataBaseFromAsset();
            }catch (IOException e){
                throw new RuntimeException("Error creating source database");
            }
        }

        return SQLiteDatabase.openDatabase(dbFile.getPath(),null,SQLiteDatabase.CONFLICT_ABORT | SQLiteDatabase.CREATE_IF_NECESSARY);
    }
	
	private void CopyDataBaseFromAsset() throws IOException{
        InputStream myInput = ctx.getAssets().open(DB_NAME);
        File f = new File(ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX);//??
        if(!f.exists())
            f.mkdir();

        OutputStream myOutput = new FileOutputStream(getDatabasePath());

        byte[] buffer = new byte[1024];
        int length;
        while((length=myInput.read(buffer))>0){
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
	}		


    public ArrayList<Word> executeQuery(String query){
        this.openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList localArray = new ArrayList();
        cursor = db.rawQuery(query,null);
        try {
            if (cursor.moveToFirst() && cursor != null) {
                do {
                    Word cont = new Word(cursor.getString(0), cursor.getString(1));
                    localArray.add(cont);
                } while (cursor.moveToNext());
            }
        }finally{
            cursor.close();
            db.close();
        }
        return localArray;
    }

    public ArrayList<Word> executeQuery1(String query){
        this.openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList localArray = new ArrayList();
        cursor = db.rawQuery(query,null);
        try {
            if (cursor.moveToFirst() && cursor != null) {
                do {
                    Word cont = new Word(cursor.getString(0));
                    localArray.add(cont);
                } while (cursor.moveToNext());
            }
        }finally{
            cursor.close();
            db.close();
        }
        return localArray;
    }

	// 1,2 : Column name 3: Table name
    public void setData(String paramStr,String paramStr2,String paramStr3){
        this.openDB();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("KeyWord",paramStr);
        values.put("WordState", paramStr2);
		//null can replace with column name which is null of all rows
        db.insert(paramStr3, null, values);
        db.close();
    }

    public void setFav(String paramStr,String paramStr2,String paramStr3){
        this.openDB();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Fav", paramStr3);
        db.update(paramStr2, values, "KeyWord = '" + paramStr.replace("'", "''") + "'", null);
        db.close();

    }

    public void delFav(){
        this.openDB();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String empty = null;
        values.put("Fav",empty);
        db.update(Static.TABLE_EV, values, "Fav = 1", null);
        db.update(Static.TABLE_VE,values,"Fav = 1",null);
        db.close();
    }


    public Word getWordDetail(String paramStr,String paramStr2) {
        this.openDB();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT WordContent,Fav FROM " + paramStr2 + " WHERE KeyWord = '" + paramStr.replace("'", "''") + "'", null);
        try {
            if (cursor != null && cursor.moveToFirst()) { //?? Why it must have cursor.moveToFirst()
                Word word = new Word(cursor.getString(0), cursor.getString(1));
                cursor.close();
                db.close();
                return word;
            }
        }
            finally
            {
                cursor.close();
                db.close();
            }
        return null;
    }

    public void deleteAllRe(){
        this.openDB();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM RecentWord");
        db.close();
    }

    public void CopyTrainToSD()
    {
        if (!(new File(Static.DATA_PATH + "tessdata/" + Static.lang + ".traineddata")).exists()) {
            try {

                AssetManager assetManager = ctx.getAssets();
                InputStream in = assetManager.open("tessdata/" + Static.lang + ".traineddata");
                OutputStream out = new FileOutputStream(Static.DATA_PATH
                        + "tessdata/" + Static.lang + ".traineddata");

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {

            }
        }
    }


    private static String getDatabasePath()
    {
        return ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
