package aloha.dictionary;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WordDetail extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {

    private String KeyWord;
    private String WordContent;
    private String WordState;
    private String pronounciation ="[  ]";
    private TextView txtItemWord;
    private TextView txtItemContent;
    private TextView txtPronoun;
    private ImageButton ibtnStar;
    private ImageButton ibtnAudio;
    private WordHelper mWordHelper;
    private TextToSpeech tts;
    private Bundle localBundle;
    private String isFav;
    private int result;
    private Intent installData;
    private Intent newData;
    private String ttsVN = "com.vnspeak.ttsengine.vitts";
    private String ttsUS = "com.google.android.tts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_content);

        localBundle = getIntent().getExtras();
        isFav = localBundle.getString("isFav");
        getSupportActionBar().setTitle(localBundle.getString("Title"));
        KeyWord = localBundle.getString("KeyWord");
        WordContent = localBundle.getString("WordContent");

        WordState = localBundle.getString("WordState");
        mWordHelper = new WordHelper(getBaseContext());//?? Not work , when combine with SQLiteDatabaseHelper

        txtItemContent = (TextView)findViewById(R.id.txtItemContent);
        txtItemContent.setText(Html.fromHtml(contentFitler(WordContent)));

        txtItemWord = (TextView)findViewById(R.id.txtItemWord);
        txtItemWord.setText(KeyWord);

        txtPronoun = (TextView)findViewById(R.id.txtPronoun);
        txtPronoun.setText(pronounciation);

        ibtnStar =(ImageButton)findViewById(R.id.ibtnStar);
        checkFavorite();
        ibtnStar.setOnClickListener(this);

        ibtnAudio=(ImageButton)findViewById(R.id.ibtnAudio);
        ibtnAudio.setOnClickListener(this);

        tts = new TextToSpeech(getBaseContext(),this);

    }


    @Override
    protected void onDestroy() {
        if(tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && data !=null)
        {
            tts.speak(KeyWord, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    //////////////////////
    /////IMAGE BUTTON/////
    //////////////////////
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtnStar:
            if(isFav != "1") {
                ibtnStar.setBackgroundResource(R.drawable.on);
                mWordHelper.setFav(KeyWord, WordState, "1");
                isFav = "1";
            }
            else {
                ibtnStar.setBackgroundResource(R.drawable.off);
                mWordHelper.setFav(KeyWord, WordState, null);
                isFav = "";
            }
                return;
            case R.id.ibtnAudio:
                Speak();
                return;
        }

    }

    //////////////////////
    /////PRIVATE METHOD///
    //////////////////////
    private void Speak()
    {
        if(WordState.equals(Static.TABLE_EV)) {
            tts.setLanguage(Locale.US);
            tts.setEngineByPackageName(ttsUS);
        }
            else {
            tts.setLanguage(new Locale("vi_VN"));
            tts.setEngineByPackageName(ttsVN);
        }
            tts.speak(KeyWord, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void checkFavorite(){
        if(isFav == null) {
            ibtnStar.setBackgroundResource(R.drawable.off); }
        else {
            ibtnStar.setBackgroundResource(R.drawable.on);}
    }

    private String langCode(String paramStr){
        if(paramStr.equals("EngVietDict")){
            return "en";
        }else{
            return "vi";
        }
    }

    private String contentFitler(String paramStr){
        String[] localStr;
        String localResult ="";
        paramStr = paramStr.replace("|+", "+");
        paramStr = paramStr.replace("#", "|");
        localStr = paramStr.split("\\|");

        for(int i=0 ; i < localStr.length ; i++){
            if( localStr[i].contains("[") ) {
                pronounciation = localStr[i].substring(localStr[i].indexOf("["));
            }

            if (localStr[i].contains("*")) {
                localResult += "<br><b><i><font color='#CC0000'><big>" + localStr[i] + "</font></i></big></b><br>";
            }
            if (localStr[i].contains("- ")) {
                localResult += "<font color='blue'>" + localStr[i] + "</font><br>";
            }
            if(localStr[i].contains("=") && localStr[i].contains("+") ){
                String part2 = localStr[i].substring(localStr[i].indexOf("+"));
                String part1 = localStr[i].substring(1, localStr[i].indexOf("+"));
                part1 = "<b><font color='black'>\t\t\t  " + part1 + "</font></b> : ";
                localResult += part1 + part2.replace("+", "").trim()+"<br>";
            }
        }
        return localResult;
    }


    @Override
    public void onInit(int i) {
        if(i == TextToSpeech.SUCCESS)
        {

        }
    }
}
