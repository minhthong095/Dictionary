package aloha.dictionary;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

public class FragmentVE extends Fragment implements View.OnClickListener,View.OnTouchListener
        ,SearchView.OnQueryTextListener
        ,AdapterView.OnItemClickListener
        ,AbsListView.OnScrollListener {

    private View rootView;
    private WordAdapter mWordAdapter;
    private SearchView mSearchView;
    private WordHelper mWordHelper;
    private ListView lvEv;
    private Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    private Boolean flag = false;
    private int firstItem;
    private int visibleItem;
    private String lastWord;
    private Button btnSpeak;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_ev, container, false);//??
        rootView.setOnTouchListener(this);

        mSearchView = (SearchView) rootView.findViewById(R.id.svEv);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnClickListener(this);

        mWordHelper = new WordHelper(getActivity());
        Static.arrayVe = mWordHelper.executeQuery1("SELECT KeyWord FROM VietEngDict WHERE KeyWord LIKE '%'LIMIT 10");
        mWordAdapter = new WordAdapter(getActivity(), Static.arrayVe);


        lvEv = (ListView) rootView.findViewById(R.id.lvEv);
        lvEv.setAdapter(mWordAdapter);
        lvEv.setOnItemClickListener(this);
        lvEv.setOnScrollListener(this);

        btnSpeak = (Button)rootView.findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(this);

        return rootView;
    }

    /////////////////////
    //////PUBLIC METHOD//
    ////////////////////
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent localIntent = new Intent(getActivity(),WordDetail.class);
        Word localWord = Static.arrayVe.get(i);
        Word localContent = mWordHelper.getWordDetail(localWord.getWord(), Static.TABLE_VE);
        lastWord = localWord.getWord();
        Bundle localBundle = new Bundle();
        localBundle.putString("KeyWord", localWord.getWord());
        localBundle.putString("WordContent", localContent.getWord());
        localBundle.putString("isFav",localContent.getDetail());
        localBundle.putString("WordState", Static.TABLE_VE);
        localBundle.putString("Title", "    " + getString(R.string.title_section2));
        localIntent.putExtras(localBundle);
        this.startActivity(localIntent);
        flag = true;
        mWordHelper.setData(localWord.getWord(), Static.TABLE_VE, Static.TABLE_RE);
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
    @Override
    public boolean onQueryTextChange(String s) {
        if (flag == false) Static.arrayVe = mWordHelper.executeQuery1("SELECT KeyWord FROM " + Static.TABLE_VE + " WHERE KeyWord LIKE '" + s.replace("'", "''") + "%' OR KeyWordTemp LIKE '" + s.replace("'","''") + "' LIMIT 15");
        mWordAdapter.update(Static.arrayVe);
        return true;
    }
    @Override
    public void onClick(View view) {
        mSearchView.onActionViewExpanded();//expand the click area
        switch(view.getId())
        {
            case R.id.btnSpeak:
                Speech();
            return;
        }
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        if(!mWordAdapter.isEmpty()) this.onItemClick(this.lvEv,this.rootView,0,0); //default position 0 i , the row id of the item l
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case 1:
                if(data !=null)
                {
                    ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    {
                        mSearchView.setQuery(res.get(0).toLowerCase(),true);
                    }
                }
                return;
        }
    }

    ////////////////////
    ///////FRAGMENT/////
    ////////////////////
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mSearchView.clearFocus();
        return true;
    }

    /////////////////////
    ///ACTIVITY CIRCLE///
    ////////////////////
    @Override
    public void onStart(){
        super.onStart();
        mSearchView.setIconified(false);
        if( flag == true ) mSearchView.setQuery("", false); flag = false;
    }
    @Override
    public void onStop(){
        super.onStop();
        mSearchView.setIconified(true);
    }

    /////////////////////
    ///PRIVATE METHOD///
    ////////////////////
    private void Speech()
    {
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi_VN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something");
        try
        {
            startActivityForResult(intent,1);
        }catch(ActivityNotFoundException a)
        {
            Toast.makeText(getActivity(), "NOT SUPPORTED, PLEASE INSTALL 'GOOGLE'", Toast.LENGTH_LONG).show();
        }
    }

    private void updateList(int state){
        if(visibleItem + firstItem >= Static.arrayVe.size() && state == 0){
            if(flag == true)
                Static.arrayVe.addAll(mWordHelper.executeQuery1("SELECT KeyWord FROM " + Static.TABLE_VE + " WHERE KeyWord LIKE '" + lastWord.replace("'","''") + "%' LIMIT " + Static.arrayVe.size() + " , 15 "));
            else
                Static.arrayVe.addAll(mWordHelper.executeQuery1("SELECT KeyWord FROM " + Static.TABLE_VE + " WHERE KeyWord LIKE '" + mSearchView.getQuery().toString().replace("'","''") + "%' LIMIT " + Static.arrayVe.size() + " , 15 "));
            mWordAdapter.update(Static.arrayVe);
        }
    }

}
