package aloha.dictionary;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by USER on 06/12/2015.
 */
public class ListViewAdapter extends ArrayAdapter<Word> {

    private Context mContext;
    private ArrayList<Word> mData;
    private SparseBooleanArray sparseItem; //?? Meaning ?


    public ListViewAdapter(Context context,int resource,ArrayList<Word> array) {
        super(context, resource,array);
        sparseItem = new SparseBooleanArray();
            mContext = context;
            mData = array;
    }

    public SparseBooleanArray getSparse() {
        return sparseItem;
    }

    private class Holder {
        TextView txtWord;
    }

    @Override
    public View getView(int position,View rootView,ViewGroup parent){ //?? it's not override , where it get those paremeters
        Holder holder = new Holder();
        if(rootView == null){
            rootView = LayoutInflater.from(mContext).inflate(R.layout.list_item,null);

            holder.txtWord = (TextView)rootView.findViewById(R.id.txtWord);

            rootView.setTag(holder);
        }else{
            holder = (Holder)rootView.getTag();
        }

        holder.txtWord.setText(mData.get(position).getWord());

        return rootView;
    }

    public void update(ArrayList<Word> paramArray){
        mData = paramArray;
        notifyDataSetChanged();
    }

    /////////////////////
    ///MULTIPLE CHOICE///
    /////////////////////
    public void toggleSelection(int position,boolean check){
        if(check){
            sparseItem.put(position, !sparseItem.get(position));
        }else{
            sparseItem.delete(position);
        }
        notifyDataSetChanged();
    }

    public void destroySparse(){
        sparseItem.clear();
    }
}
