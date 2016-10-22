package aloha.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by USER on 13/15/2015.
 */
public class FragmentFA extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    View rootView;
    ListView lvFa;
    ListViewAdapter mWordAdapter;
    WordHelper mWordHelper;
    int firstItem;
    int visibleItem;
    private final String queryGetWord = "SELECT KeyWord,State FROM ( SELECT KeyWord , 'EngVietDict' as State FROM EngVietDict WHERE Fav = '1' UNION SELECT KeyWord , 'VietEngDict' as State FROM VietEngDict WHERE Fav = '1') LIMIT 15";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        rootView = inflater.inflate(R.layout.layout_fa, container, false);//???
        final ListView lvFa = (ListView)rootView.findViewById(R.id.lvFa);

        mWordHelper = new WordHelper(getActivity());
        Static.arrayFa = mWordHelper.executeQuery(queryGetWord);

        mWordAdapter = new ListViewAdapter(getActivity(),R.layout.list_item, Static.arrayFa);

        lvFa.setAdapter(mWordAdapter);
        lvFa.setOnScrollListener(this);
        lvFa.setOnItemClickListener(this);
        lvFa.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = lvFa.getCheckedItemCount();
                mWordAdapter.toggleSelection(i,b); //i position , b check

                actionMode.setTitle(checkedCount +" Selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.menu_fav,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.delete:
                        SparseBooleanArray selected = mWordAdapter.getSparse();
                        for(int i = (selected.size() - 1);i>=0;i--){
                            {
                                Word selectedWord = mWordAdapter.getItem(selected.keyAt(i));
                                mWordHelper.setFav(selectedWord.getWord(), selectedWord.getDetail(), null);
                                selected.delete(selected.keyAt(i));
                                mWordAdapter.remove(selectedWord);
                            }
                        }
                        mWordAdapter.notifyDataSetChanged();
                        actionMode.finish(); //CLOSE CAB
                        return true;

                    case R.id.delete_all:
                        mWordHelper.delFav();
                        update();
                        actionMode.finish();
                        return true;

                    default:
                        return false; //?? true and false ?
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }

        });
        return rootView;
    }

    ////////////////////
    ///////LISTVIEW/////
    ///////////////////
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent localIntent = new Intent(getActivity(),WordDetail.class);
        Word localWord = Static.arrayFa.get(i);
        Word localContent = mWordHelper.getWordDetail(localWord.getWord(), localWord.getDetail());
        Bundle localBundle = new Bundle();
        localBundle.putString("KeyWord", localWord.getWord());
        localBundle.putString("WordState", localWord.getDetail()); //Detail have State
        localBundle.putString("WordContent", localContent.getWord()); //Word have Content , Detail have Fav
        localBundle.putString("isFav", localContent.getDetail());
        localBundle.putString("Title", getString(R.string.title_section4));
        localIntent.putExtras(localBundle);
        this.startActivity(localIntent);
    }

    /////////////////////
    ///SCROLL ACTION/////
    /////////////////////
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {//ScrollState i IDLE - 0 FREE FLY - 2 TOUCHING to DRAG - 1
        updateList(i);
    }
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) { //System.out.println("SrSt "+scrollState+" fiViIt: "+i+" viItCo "+i1+" toItCo "+i2+" fl "+SCROLL_STATE_FLING+" idl "+SCROLL_STATE_IDLE+" To "+SCROLL_STATE_TOUCH_SCROLL);
        visibleItem = i1;
        firstItem = i;
    }
    private void updateList(int state){
        if(visibleItem + firstItem >= Static.arrayFa.size() && state == 0){
            Static.arrayFa.addAll(mWordHelper.executeQuery("SELECT KeyWord,State FROM ( SELECT KeyWord , 'EngVietDict' as State FROM EngVietDict WHERE Fav = '1' UNION SELECT KeyWord , 'VietEngDict' as State FROM VietEngDict WHERE Fav = '1' ) LIMIT "+Static.arrayFa.size()+",15"));
            mWordAdapter.update(Static.arrayFa);
        }
    }

    /////////////////////
    ///PRIVATE METHOD///
    ////////////////////
    private void update(){
        mWordAdapter.destroySparse();
        mWordAdapter.clear();
    }


}
