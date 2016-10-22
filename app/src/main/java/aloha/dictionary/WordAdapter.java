package aloha.dictionary;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WordAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Word> mData;
    private SparseBooleanArray sparseItem;

    public WordAdapter(Context paramContext ,ArrayList<Word> paramArray)
    {
        mContext = paramContext;
        mData=paramArray;
        sparseItem = new SparseBooleanArray();

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Word getItem(int i) {
        return (Word)mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Word localWord = getItem(i);
        WordHolder holder = new WordHolder();
        if(view == null){
            view = LayoutInflater.from(this.mContext).inflate(R.layout.list_item,viewGroup,false);
            holder.txtWord = (TextView) view.findViewById(R.id.txtWord);
            view.setTag(holder);
        }
        else{
            holder =(WordHolder)view.getTag();
        }
        holder.txtWord.setText(localWord.getWord());
        return view;
    }

    public void update(ArrayList<Word> paramArray){
        this.mData = paramArray;
        this.notifyDataSetChanged();
    }

    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }

    private class WordHolder{
        private TextView txtWord;
    }

}
