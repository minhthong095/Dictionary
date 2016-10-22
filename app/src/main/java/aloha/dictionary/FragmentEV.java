package aloha.dictionary;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentEV extends Fragment implements View.OnClickListener,View.OnTouchListener
        ,SearchView.OnQueryTextListener
        ,AdapterView.OnItemClickListener
        ,AbsListView.OnScrollListener {
    
    private View rootView;
    private WordAdapter mWordAdapter;
    private SearchView mSearchView;
    private WordHelper mWordHelper;
    private ListView lvEv;
    private Button btnSpeak;
    private Button btnCapture;
    private Boolean flag = false;
    private int firstItem;
    private int visibleItem;
    private String lastWord;
    private Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    final int EXTRACT = 3;
    final int PIC_DROP = 2;
    final int SPEECH = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_ev, container, false);//??
        rootView.setOnTouchListener(this);

        mSearchView = (SearchView) rootView.findViewById(R.id.svEv);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnClickListener(this);

        mWordHelper = new WordHelper(getActivity());
        Static.arrayEv = mWordHelper.executeQuery1("SELECT KeyWord FROM EngVietDict WHERE KeyWord LIKE '%'LIMIT 10");
        mWordAdapter = new WordAdapter(getActivity(), Static.arrayEv);


        lvEv = (ListView) rootView.findViewById(R.id.lvEv);
        lvEv.setAdapter(mWordAdapter);
        lvEv.setOnItemClickListener(this);
        lvEv.setOnScrollListener(this);

        btnSpeak = (Button)rootView.findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(this);

        btnCapture = (Button)rootView.findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(this);

        return rootView;
    }

    /////////////////////
    //////LISTVIEW//////
    ////////////////////
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent localIntent = new Intent(getActivity(),WordDetail.class);
        Word localWord = Static.arrayEv.get(i);
        Word localContent = mWordHelper.getWordDetail(localWord.getWord(), Static.TABLE_EV);
        lastWord = localWord.getWord();
        Bundle localBundle = new Bundle();
        localBundle.putString("KeyWord", localWord.getWord());
        localBundle.putString("WordContent", localContent.getWord());
        localBundle.putString("isFav",localContent.getDetail());
        localBundle.putString("WordState", Static.TABLE_EV);
        localBundle.putString("Title", "    " + getString(R.string.title_section1));
        localIntent.putExtras(localBundle);
        this.startActivity(localIntent);
        flag = true;
        mWordHelper.setData(localWord.getWord(), Static.TABLE_EV, Static.TABLE_RE);
    }
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {//ScrollState i IDLE - 0 FREE FLY - 2 TOUCHING to DRAG - 1
        updateList(i);
    }
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) { //System.out.println("SrSt "+scrollState+" fiViIt: "+i+" viItCo "+i1+" toItCo "+i2+" fl "+SCROLL_STATE_FLING+" idl "+SCROLL_STATE_IDLE+" To "+SCROLL_STATE_TOUCH_SCROLL);
        visibleItem = i1;
        firstItem = i;
    }

    /////////////////////
    /////PUBLIC METHOD///
    ////////////////////
    @Override
    public boolean onQueryTextChange(String s) {
        if (flag == false) Static.arrayEv = mWordHelper.executeQuery1("SELECT KeyWord FROM " + Static.TABLE_EV + " WHERE KeyWord LIKE '" + s.replace("'", "''") + "%' LIMIT 15");
        mWordAdapter.update(Static.arrayEv);
        return true;
    }

    @Override
    public void onClick(View view) {
        mSearchView.onActionViewExpanded();//expand the click area
        switch(view.getId())
        {
            case R.id.btnSpeak:
                Speech();
                break;
            case R.id.btnCapture:
                mWordHelper.CopyTrainToSD();
                Start();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        if(!mWordAdapter.isEmpty()) this.onItemClick(this.lvEv,this.rootView,0,0); //default position 0 i , the row id of the item l
        return false;//??
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {

            if (requestCode == SPEECH) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mSearchView.setQuery(result.get(0).toLowerCase(), false);

            }
        }

            if (requestCode == PIC_DROP) {
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "img.jpg");
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(Uri.fromFile(file), "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 0);
                cropIntent.putExtra("aspectY", 0);
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, EXTRACT);
            }
            if (requestCode == EXTRACT) {
                Bundle extras = data.getExtras();
                Bitmap imgBitmap = extras.getParcelable("data");

                TessBaseAPI tess = new TessBaseAPI();
                tess.setDebug(true);
                tess.init(Static.DATA_PATH, Static.lang);
                tess.setImage(imgBitmap);
                mSearchView.setQuery(tess.getUTF8Text().toLowerCase(), false);

                tess.end();
            }
    }

    /////////////////////
    ///////FRAGMENT/////
    ////////////////////
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mSearchView.clearFocus();
        return true;//??
    }

    /////////////////////
    ///ACTIVITY CIRCLE///
    ////////////////////
    @Override
    public void onStart(){
        super.onStart();
       // mSearchView.setIconified(false);
        if( flag == true) mSearchView.setQuery("", false); flag = false;
    }
    @Override
    public void onStop(){
        super.onStop();
       // mSearchView.setIconified(true);
    }


    /////////////////////
    ///PRIVATE METHOD///
    ////////////////////
    private void Speech()
    {
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        try
        {
            startActivityForResult(intent,SPEECH);
        }catch(ActivityNotFoundException a)
        {
            Toast.makeText(getActivity(),"NOT SUPPORTED, PLEASE INSTALL 'GOOGLE'",Toast.LENGTH_LONG).show();
        }
    }
    private void updateList(int state){
        if(visibleItem + firstItem >= Static.arrayEv.size() && state == 0){
            if(flag == true)
                Static.arrayEv.addAll(mWordHelper.executeQuery1("SELECT KeyWord FROM " + Static.TABLE_EV + " WHERE KeyWord LIKE '" + lastWord.replace("'","''") + "%' LIMIT " + Static.arrayEv.size() + " , 15 "));
            else
                Static.arrayEv.addAll(mWordHelper.executeQuery1("SELECT KeyWord FROM " + Static.TABLE_EV + " WHERE KeyWord LIKE '" + mSearchView.getQuery().toString().replace("'","''") + "%' LIMIT " + Static.arrayEv.size() + " , 15 "));
            mWordAdapter.update(Static.arrayEv);
        }
    }

    private void Start()
    {
        Intent cropIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "img.jpg");
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(cropIntent, PIC_DROP); //??
    }

}
