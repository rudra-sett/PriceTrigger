package com.rs.pricetrigger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageButton;

import java.util.ArrayList;
public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;

    public CustomAdapter(ArrayList<String> list, Context context) {
    this.list = list;
    this.context = context;
    }

@Override
public int getCount() {
    return list.size();
}

@Override
public Object getItem(int pos) {
    return list.get(pos);
}

@Override
public long getItemId(int pos) {
      //  list.get(pos).
    //return list.get(pos).getId();
    return pos;
    //just return 0 if your list items do not have an Id variable.
}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.triggerinfo, null);
        }

        //Handle TextView and display string from your list
        TextView product= (TextView)view.findViewById(R.id.productedittext);
        product.setText(list.get(position).split(",")[0]);
        TextView price= (TextView)view.findViewById(R.id.priceedittext);
        price.setText(list.get(position).split(",")[1]);

        //Handle buttons and add onClickListeners
        final AppCompatImageButton deletebtn = (AppCompatImageButton)view.findViewById(R.id.deletebutton);
        //Button deletebtn= (Button)view.findViewById(R.id.deletebutton);

        deletebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        /*addBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                notifyDataSetChanged();
            }
        });*/

        return view;
    }
}
