package com.demo.bluetoothdegree;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    TextView m_tvDiscover;
    ListView m_listviewDevice;
    BluetoothAdapter mBluetoothAdapter;
    private static final int ENABLE_BLUETOOTH = 0X91;
    DevAdapter mAdapter;
    TextView m_tvDis2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_tvDiscover = (TextView) findViewById(R.id.discover1);
        m_listviewDevice = (ListView) findViewById(R.id.listview1);
        m_tvDis2 = (TextView) findViewById(R.id.discover2);
        m_tvDis2.setVisibility(View.GONE);

        m_tvDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_tvDis2.setVisibility(View.VISIBLE);
                mBluetoothAdapter.startDiscovery();
            }
        });
        checkEnableBluetooth();
        //重要广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        //
        mAdapter = new DevAdapter(this);
        m_listviewDevice.setAdapter(mAdapter);
        //
        m_listviewDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点击设备: " + m_listDevices.get(position).getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, OperateDevActivity.class);
                intent.putExtra("device", m_listDevices.get(position));
                startActivity(intent);
            }
        });
    }

    ArrayList<BluetoothDevice> m_listDevices = new ArrayList<>();
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                m_listDevices.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                m_tvDis2.setVisibility(View.GONE);
                mAdapter.setData(m_listDevices);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private void checkEnableBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
        } else
            mBluetoothAdapter.enable();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BLUETOOTH && resultCode == RESULT_CANCELED) {
            finish();
        } else
            mBluetoothAdapter.enable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
