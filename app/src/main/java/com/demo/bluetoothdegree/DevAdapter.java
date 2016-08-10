package com.demo.bluetoothdegree;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edianzu on 2016/8/7.
 */

public class DevAdapter extends BaseAdapter {

    ArrayList<BluetoothDevice> m_listInfo = new ArrayList<>();
    Context mContext;

    public DevAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<BluetoothDevice> devices) {
        m_listInfo.addAll(devices);
    }

    @Override
    public int getCount() {
        return m_listInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return m_listInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int nIndex = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.devinfoitem, null);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.devname1);
        TextView tvAdd = (TextView) convertView.findViewById(R.id.devaddress1);
        tvName.setText(m_listInfo.get(nIndex).getName());
        tvAdd.setText(m_listInfo.get(nIndex).getAddress());

        return convertView;
    }
}
