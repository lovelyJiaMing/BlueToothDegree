package com.demo.bluetoothdegree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Edianzu on 2016/8/9.
 */

public class RecordAdapter extends BaseAdapter {

    private ArrayList<RecordItem> arrayList = new ArrayList<>();
    Context mContext;

    public RecordAdapter(Context context) {
        mContext = context;
    }

    public void setData(RecordItem item) {
        arrayList.add(0, item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.recordinfoitem, null);
        }
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView jianqing = (TextView) convertView.findViewById(R.id.recordjianqing);
        TextView xiahe = (TextView) convertView.findViewById(R.id.recordxiahe);
        TextView mianzhuan = (TextView) convertView.findViewById(R.id.recordmianzhuan);
        //
        time.setText(arrayList.get(position).time);
        jianqing.setText("肩倾角:" + arrayList.get(position).JianQing);
        xiahe.setText("下颌角:" + arrayList.get(position).XiaHe);
        mianzhuan.setText("面转角:" + arrayList.get(position).MianZhuan);
        return convertView;
    }

    public class RecordItem {
        public String time;
        public String JianQing;
        public String XiaHe;
        public String MianZhuan;
    }
}
