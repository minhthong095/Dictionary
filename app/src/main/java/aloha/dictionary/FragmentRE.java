package aloha.dictionary;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by USER on 13/15/2015.
 */
public class FragmentRE extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener{ //Hành vi
    private View rootView;
    private ListView lvRe;
    private WordAdapter mWordAdapter;
    private WordHelper mWordHelper;
    private int firstItem;
    private int visibleItem;

    private final String queryGetWord = "SELECT KeyWord,WordState FROM RecentWord ORDER BY rowid DESC LIMIT 15";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      //  ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_section3);

        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.layout_re, container, false);//???
        lvRe = (ListView)rootView.findViewById(R.id.lvRe);
        lvRe.setOnItemClickListener(this);
        lvRe.setOnScrollListener(this);
        mWordHelper = new WordHelper(getActivity());
        Static.arrayRe = mWordHelper.executeQuery(queryGetWord);

        mWordAdapter = new WordAdapter(getActivity(), Static.arrayRe);
        lvRe.setAdapter(mWordAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_re,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //////////////////////////
    ////////LISTVIEW/////////
    //////////////////////////
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent localIntent = new Intent(getActivity(),WordDetail.class);
        Word localWord = Static.arrayRe.get(i);
        Word localContent = mWordHelper.getWordDetail(localWord.getWord(), localWord.getDetail());
        Bundle localBundle = new Bundle();
        localBundle.putString("KeyWord", localWord.getWord());
        localBundle.putString("WordState", localWord.getDetail()); //Detail have State
        localBundle.putString("WordContent", localContent.getWord()); //Word have Content , Detail have Fav
        localBundle.putString("isFav",localContent.getDetail());
        localBundle.putString("Title", getString(R.string.title_section4));
        localIntent.putExtras(localBundle);
        this.startActivity(localIntent);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.delete_all_re:
                mWordHelper.deleteAllRe();
                mWordAdapter.update(mWordHelper.executeQuery(queryGetWord));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateList(int state){
        if(visibleItem + firstItem >= Static.arrayRe.size() && state == 0){
            Static.arrayRe.addAll(mWordHelper.executeQuery("SELECT KeyWord,WordState FROM RecentWord ORDER BY rowid DESC LIMIT "+Static.arrayRe.size()+",15"));
            mWordAdapter.update(Static.arrayRe);
        }
    }
}
