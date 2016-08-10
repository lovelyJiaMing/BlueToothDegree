package com.demo.bluetoothdegree;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

/**
 * Created by Edianzu on 2016/8/7.
 */

public class OperateDevActivity extends Activity {

    BluetoothDevice mDev;//被选中的设备
    private BluetoothSocket mSocket;
    TextView m_tvName2;
    TextView m_tvBoundDev;
    TextView m_tvConnectDev;
    TextView m_tvUnBoundDev;
    TextView m_tvDisConnectDev;
    TextView m_tvLocationZero;
    TextView m_tvRecordData;
    //
    String sJianQ;
    String sXiaH;
    String sMianZ;
    String sRecordJianQ = "";
    String sRecordXiaH = "";
    String sRecordMianZ = "";
    //
    TextView m_tvJianQing1;
    TextView m_tvJianQing2;
    TextView m_tvXiaHe1;
    TextView m_tvXiaHe2;
    TextView m_tvMianZhuan1;
    TextView m_tvMianZhuan2;
    ListView mRecordListView;
    RecordAdapter adapter;

    boolean m_bThreadRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        mDev = getIntent().getParcelableExtra("device");
        //
        m_tvName2 = (TextView) findViewById(R.id.devname1);
        //
        m_tvBoundDev = (TextView) findViewById(R.id.bounddev);
        m_tvConnectDev = (TextView) findViewById(R.id.connectdev);
        m_tvUnBoundDev = (TextView) findViewById(R.id.unbounddev);
        m_tvDisConnectDev = (TextView) findViewById(R.id.disconnectdev);
        m_tvLocationZero = (TextView) findViewById(R.id.locationzero);
        m_tvRecordData = (TextView) findViewById(R.id.recorddata);
        //
        m_tvJianQing1 = (TextView) findViewById(R.id.jianqingjiao1);
        m_tvJianQing2 = (TextView) findViewById(R.id.jianqingjiao2);
        m_tvXiaHe1 = (TextView) findViewById(R.id.xiahejiao1);
        m_tvXiaHe2 = (TextView) findViewById(R.id.xiahejiao2);
        m_tvMianZhuan1 = (TextView) findViewById(R.id.mianzhuanjiao1);
        m_tvMianZhuan2 = (TextView) findViewById(R.id.mianzhuanjiao2);
        //
        m_tvName2.setText(mDev.getName());

        mRecordListView = (ListView) findViewById(R.id.recordlist);
        adapter = new RecordAdapter(this);
        mRecordListView.setAdapter(adapter);

        setClickListener();
        setBroadCastReceiver();
    }

    private void setBroadCastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                int nStatus = mDev.getBondState();
                if (nStatus == BOND_NONE) {
                    Toast.makeText(OperateDevActivity.this, "解除绑定成功", Toast.LENGTH_SHORT).show();
                } else if (nStatus == BOND_BONDED) {
                    Toast.makeText(OperateDevActivity.this, "绑定此设备成功", Toast.LENGTH_SHORT).show();
                }
            }
            if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
            }
        }
    };

    private void setClickListener() {

        m_tvBoundDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createBound(mDev.getClass(), mDev);
                } catch (Exception e) {
                }
            }
        });

        m_tvConnectDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
                UUID uuid = UUID.fromString(SPP_UUID);
                try {
                    mSocket = mDev.createRfcommSocketToServiceRecord(uuid);
                    mSocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(OperateDevActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                //开启接收线程
                try {
                    m_bThreadRun = true;
                    startReceiveThread();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        m_tvUnBoundDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    removeBound(mDev.getClass(), mDev);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        m_tvDisConnectDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    m_bThreadRun = false;
                    mSocket.close();
                    Toast.makeText(OperateDevActivity.this, "已断开连接", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        m_tvLocationZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRecordXiaH = sXiaH;
                sRecordMianZ = sMianZ;
                sRecordJianQ = sJianQ;

                m_tvJianQing1.setText(sRecordXiaH + "°");
                m_tvXiaHe1.setText(sRecordJianQ + "°");
                m_tvMianZhuan1.setText(sRecordMianZ + "°");
            }
        });

        m_tvRecordData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordAdapter.RecordItem item = adapter.new RecordItem();
                SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                item.time = myFmt2.format(new Date());
                item.JianQing = s1;
                item.XiaHe = s2;
                item.MianZhuan = s3;
                adapter.setData(item);
            }
        });
    }

    InputStream myInStream;

    private void startReceiveThread() throws IOException {
        myInStream = mSocket.getInputStream();

        //开启接收线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (m_bThreadRun) {
                    try {
                        if (myInStream == null)
                            continue;
                        //从通道的输入流InputStream中读取数据到buffer数组中
                        int count;
                        while (true) {
                            count = myInStream.available();
                            if (count != 11) {
                                continue;
                            } else
                                break;
                        }
                        byte[] bytes = new byte[count];
                        int readCount = 0; // 已经成功读取的字节的个数
                        while (readCount < count) {
                            readCount += myInStream.read(bytes, readCount, count - readCount);
                        }
                        //
                        String sHex = bytesToHexString(bytes);
                        if (sHex.startsWith("5553", 0)) {
                            Message msg = new Message();
                            msg.obj = sHex;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public boolean createBound(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    public boolean removeBound(Class btClass, BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    Handler handler = new android.os.Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            analysisStrDisplay(msg.obj.toString());
            return true;
        }
    });

    private void analysisStrDisplay(String sHex) {
        int nIndex = sHex.indexOf("53");
        int nRoll = Integer.parseInt(sHex.substring(nIndex + 4, nIndex + 6) + sHex.substring(nIndex + 2, nIndex + 4), 16);
        int nPitch = Integer.parseInt(sHex.substring(nIndex + 8, nIndex + 10) + sHex.substring(nIndex + 6, nIndex + 8), 16);
        int nYaw = Integer.parseInt(sHex.substring(nIndex + 12, nIndex + 14) + sHex.substring(nIndex + 10, nIndex + 12), 16);

        double dbJianQingJiao = ((double) nRoll / (double) 32768) * (double) 180;//jianqingjiao
        if (dbJianQingJiao > 180)
            dbJianQingJiao -= 360;
        double dbXiaHeJiao = ((double) nPitch / (double) 32768) * (double) 180;//xiahejiao
        if (dbXiaHeJiao > 180)
            dbXiaHeJiao -= 360;
        double dbMianZhuanJiao = ((double) nYaw / (double) 32768) * (double) 180;//mianzhuan
        if (dbMianZhuanJiao > 180)
            dbMianZhuanJiao -= 360;

        sJianQ = String.format("%.1f", dbJianQingJiao);
        sXiaH = String.format("%.1f", dbXiaHeJiao);
        sMianZ = String.format("%.1f", dbMianZhuanJiao);

        //如果从来没有按下过位置校零
        if (sRecordJianQ.isEmpty() && sRecordMianZ.isEmpty() && sRecordXiaH.isEmpty()) {
            m_tvJianQing1.setText(sXiaH + "°");
            m_tvXiaHe1.setText(sJianQ + "°");
            m_tvMianZhuan1.setText(sMianZ + "°");
        } else {
            //按下过后，减法，然后显示在2上
            String sminus1;
            double minus1 = (Double.parseDouble(sXiaH) - Double.parseDouble(sRecordXiaH));
            if (minus1 > 0)
                sminus1 = "下";
            else
                sminus1 = "上";

            String sminus2;
            double minus2 = (Double.parseDouble(sJianQ) - Double.parseDouble(sRecordJianQ));
            if (minus2 > 0)
                sminus2 = "左";
            else
                sminus2 = "右";

            String sminus3;
            double minus3 = (Double.parseDouble(sMianZ) - Double.parseDouble(sRecordMianZ));
            if (minus3 > 0)
                sminus3 = "左";
            else
                sminus3 = "右";
            //
            s1 = String.format("%.1f", Math.abs(minus1));
            s2 = String.format("%.1f", Math.abs(minus2));
            s3 = String.format("%.1f", Math.abs(minus3));
            //
            m_tvJianQing2.setText(s1 + "°" + sminus1);
            m_tvXiaHe2.setText(s2 + "°" + sminus2);
            m_tvMianZhuan2.setText(s3 + "°" + sminus3);
        }
    }

    String s1;
    String s2;
    String s3;

    @Override
    protected void onStop() {
        super.onStop();
        m_bThreadRun = false;
        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}