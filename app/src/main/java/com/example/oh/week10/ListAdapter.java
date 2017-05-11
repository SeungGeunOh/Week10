package com.example.oh.week10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by OH on 2017-05-10.
 */

public class ListAdapter extends BaseAdapter{
    Context context;
    ArrayList<Bookmark> bookmarkArrayList;

    public ListAdapter (Context context, ArrayList<Bookmark> bookmarkArrayList){
        this.context = context;
        this.bookmarkArrayList = bookmarkArrayList;
    }
    @Override
    public int getCount() {
        return bookmarkArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookmarkArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.listview, null);
        TextView name = (TextView)convertView.findViewById(R.id.name);
        TextView url = (TextView)convertView.findViewById(R.id.url);

        name.setText("< " + bookmarkArrayList.get(position).name + " >");
        url.setText(bookmarkArrayList.get(position).url);

        return convertView;
    }
    public boolean findUrl(String str) {
        for (Bookmark bookmark : bookmarkArrayList){
            if (bookmark.getUrl().equals(str))
                return false;
        }
        return true;
    }
    public String getUrl(int position){
        return bookmarkArrayList.get(position).url;
    }
}
