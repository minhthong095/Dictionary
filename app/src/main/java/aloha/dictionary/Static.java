package aloha.dictionary;

import android.os.Environment;

import java.util.ArrayList;

/**
 * Created by USER on 15/11/2015.
 */
public class Static {

    public static ArrayList<Word> arrayEv;
    public static ArrayList<Word> arrayVe;
    public static ArrayList<Word> arrayFa;
    public static ArrayList<Word> arrayRe;

    public static final String TABLE_EV = "EngVietDict";
    public static final String TABLE_VE = "VietEngDict";
    public static final String TABLE_RE= "RecentWord";

    public static Boolean favRelease;

    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/OCR/";
    public static final String lang = "eng";
    private static final String TAG = "OCR.java";



}
