package com.aumum.app.mobile.ui.view.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aumum.app.mobile.R;

import java.util.List;

/**
 * Created by Administrator on 1/01/2015.
 */
public class ListViewDialog extends PopupDialog {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public ListViewDialog(Context context,
                          String title,
                          List<String> items,
                          OnItemClickListener listener) {
        super(context, R.layout.dialog_list);
        this.listener = listener;
        setCanceledOnTouchOutside(true);
        initView(context, title, items);
    }

    private void initView(Context context, String title, List<String> items) {
        TextView titleText = (TextView) findViewById(R.id.text_title);
        if (title != null) {
            titleText.setText(title);
        } else {
            titleText.setVisibility(View.GONE);
        }
        ListView listView = (ListView) findViewById(android.R.id.list);
        DialogListAdapter adapter = new DialogListAdapter(context, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                dismiss();
                listener.onItemClick(position);
            }
        });
        adapter.notifyDataSetChanged();
    }

    class DialogListAdapter extends ArrayAdapter<String> {

        class Card {
            TextView valueText;
        }

        public DialogListAdapter(Context context, List<String> items) {
            super(context, 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Card card;

            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.dialog_listitem_inner, parent, false);
                card = new Card();
                card.valueText = (TextView) convertView.findViewById(R.id.text_value);
                convertView.setTag(card);
            } else {
                card = (Card) convertView.getTag();
            }

            String value = getItem(position);
            card.valueText.setText(value);

            return convertView;
        }
    }
}
