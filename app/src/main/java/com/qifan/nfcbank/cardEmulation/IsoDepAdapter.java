package com.qifan.nfcbank.cardEmulation;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Qifan on 28/11/2018.
 */
public class IsoDepAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<String> messages = new ArrayList<String>(100);
    private int messageCounter;

    public IsoDepAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

    public void addMessage(String message) {
        messageCounter++;
        messages.add("Message [" + messageCounter + "]: " + message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView view = (TextView)convertView.findViewById(android.R.id.text1);
        view.setText((CharSequence)getItem(position));
        return convertView;
    }
}
