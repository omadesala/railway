package cn.christian.server;

import android.app.Activity;
import android.app.Service;
import android.content.*;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import cn.christian.server.utils.Constants;
import cn.christian.server.utils.UDPHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/9/1.
 */
public class ADSocketService extends Service {

    private static WifiManager wifi;
    private boolean running = false;


    public static float sensorScopevalue = 0;
    public static float sensorVelocityvalue = 0;
    public static float measureDistance = 0;
    public static float sensorHZ = 100;
    public static int dataCount = 100;


    public static float micronVoltage = 17.0f / 5000.0f;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        setWifiApEnabled(true);

        SharedPreferences setting = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        if (setting != null) {
            sensorScopevalue = setting.getFloat(Constants.sensorScope, 5.0f);
            sensorVelocityvalue = setting.getFloat(Constants.sensorVelocity, 0.418879f);
            measureDistance = setting.getFloat(Constants.measureDistance, 1.0f);
            micronVoltage = setting.getFloat(Constants.sensorVoltageDistance, 3.4f) / 1000f;

            dataCount = (int) (sensorHZ / sensorVelocityvalue);
            Log.d("ADservice", "dataCount: " + dataCount);
        }


        ActionReceiver actionReceiver = new ActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DEVICE_ACTION);
        filter.addAction(Constants.SENSOR_POSITION_CHECK);
        registerReceiver(actionReceiver, filter);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);

        if (!running) {
            MySocketServer.startListen(this);
            running = true;
        }
        return START_STICKY;
    }


    // wifi热点开关
    public boolean setWifiApEnabled(boolean enabled) {
        if (enabled) { // disable WiFi in any case
            //wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
            wifi.setWifiEnabled(false);
        }
        try {
            //热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            //配置热点的名称(可以在名字后面加点随机数什么的)
            apConfig.SSID = "AndroidAP";
            //配置热点的密码
            apConfig.preSharedKey = "11121111";
            //通过反射调用设置热点
            Method method = wifi.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            //返回热点打开状态
            return (Boolean) method.invoke(wifi, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }

    private static class NetState {

        private String intToIp(int ip) {
            return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                    + ((ip >> 24) & 0xFF);
        }

        public boolean hasInternet() {
            if (wifi.getWifiState() == 3) {
                return true;
            } else {
                return false;
            }
        }

        public String GetIPAddress() {
            String ServerIP = intToIp(wifi.getConnectionInfo().getIpAddress());
            return ServerIP;
        }
    }


    static class MySocketServer implements Runnable {
        private static final String TAG = "MySocketServer";

        private static final int serverListenPort = 8888;
        private static Context mContext = null;

        private static boolean started = false;

        public static void startListen(Context ctx) {

            mContext = ctx;
            if (started) {
                return;
            }

            Log.e(TAG, "+startListen() ");
            Thread serverSocketListen = new Thread(new MySocketServer());
            serverSocketListen.start();
            Log.e(TAG, "-startListen() ");
            started = true;
        }

        @Override
        public void run() {
            // establish server socket
            int connIndex = 0;
            byte[] msg = new byte[1024];
            Thread connHandle = null;
//                DatagramSocket udpSocket = null;
//                DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
//                udpSocket = new DatagramSocket(serverListenPort);
//                ServerSocket serverSocket = new ServerSocket(serverListenPort);//, connectionMaxLength, InetAddress.getByName(serverIpString));
//                Log.e(TAG, "port:" + serverSocket.getLocalPort());

            new Thread(new UDPHelper(wifi)).start();

//                while (true) {
//
//                    try {
//                        udpSocket.receive(dPacket);
//                        Log.i("msg sever received", new String(dPacket.getData()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

//                    Socket incoming = serverSocket.accept();
//                    if (connHandle != null) {
//                        ConnectionHandle.exit();
//                        try {
//                            connHandle.join();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    Log.e(TAG, " Connected a client!connIndex:" + connIndex + " RemoteSocketAddress:" + String.valueOf(incoming.getRemoteSocketAddress()));
//                    connHandle = new Thread(new ConnectionHandle(mContext, incoming, connIndex));
//                    connHandle.start();
//                connIndex++;
//            }
        }
    }


    static class ConnectionHandle implements Runnable {
        public static final String TAG = "ConnectionHandle:";

        private Socket connectedSocket;
        private int connIndex;
        private static volatile boolean done = false;

        private static InputStream inStream;
        private static OutputStream outStream;
        static public DistanceParser dataParser;

        private static Context mContext;

        public ConnectionHandle(Context ctx, Socket incoming, int connIdx) {
            mContext = ctx;
            connectedSocket = incoming;
            connIndex = connIdx;
        }

        public static void exit() {

            done = true;
        }

        private static void sendStop() {

            if (outStream != null && dataParser != null) {

                dataParser.setBaseConfirm(false);
                try {
                    outStream.write(Constants.SENSOR_CMD_STOP.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }


        @Override
        public void run() {
            Log.e(TAG, "ConnectionHandle +run() +connIndex: " + connIndex);

            try {
                try {

                    inStream = connectedSocket.getInputStream();
                    outStream = connectedSocket.getOutputStream();
                    Scanner in = new Scanner(inStream, "UTF8");
//                    boolean done = false;

                    dataParser = new DistanceParser(dataCount);

                    StringBuffer sb = new StringBuffer();
                    while (!done && in.hasNext()) {

                        String token = in.next();

                        if (token.contains(Constants.SENSOR_DATA_START_TAG)) {
                            sb = new StringBuffer();
                            int start = token.indexOf(Constants.SENSOR_DATA_START_TAG);
                            if (start != 0) {
                                Log.e(TAG, " data exception");
                            }
                            sb.append(token.substring(start));
                        } else {
                            sb.append(token);
                        }


                        if (token.endsWith(Constants.SENSOR_DATA_END_TAG)) {

                            String record = sb.toString();
                            try {

                                float[] distance = dataParser.getValidateData(record);

                                if (distance != null) {

                                    Intent intent = new Intent();
                                    intent.setAction(Constants.SENSOR_DATA_COMMING);
                                    intent.putExtra(Constants.SENSOR_DATA, distance);
                                    mContext.sendBroadcast(intent);
                                    Log.e(TAG, "send stop command");
                                    sendStop();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

//                    connectedSocket.close();

                    Thread.sleep(10);
                } finally {
                    inStream.close();
                    outStream.close();
                    connectedSocket.close();

                    inStream = null;
                    outStream = null;
                    connectedSocket = null;
                }
            } catch (
                    IOException e
                    )

            {
                Log.e(TAG, "IOException:" + e.getMessage());
                e.printStackTrace();
            } catch (
                    InterruptedException e
                    )

            {
                Log.e(TAG, "InterruptedException:" + e.getMessage());
                e.printStackTrace();
            } catch (
                    Exception e
                    )

            {
                Log.e(TAG, "Exception:" + e.getClass().getName() + " msg:" + e.getMessage());
                e.printStackTrace();
            }

            Log.e(TAG, "-run()");
        }


    }

    class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SENSOR_POSITION_CHECK)) {
                boolean status = intent.getBooleanExtra(Constants.SENSOR_POSITION_CHECK, false);
                if (ConnectionHandle.dataParser != null) {
                    ConnectionHandle.dataParser.setBaseConfirm(status);
                    Log.d("ADService", "停止位置校准" + status);
                }
            } else {

                int type = intent.getIntExtra(Constants.MSG_TYPE, -1);
                switch (type) {

                    case Constants.SETTING_MODE:

                        sensorScopevalue = intent.getFloatExtra(Constants.SENSOR_MAX_SCOPE, 5.0f);
                        sensorVelocityvalue = intent.getFloatExtra(Constants.SENSOR_VELOCITY, 0.418879f);
                        measureDistance = intent.getFloatExtra(Constants.MEASURE_DISTANCE, 1.0f);
                        micronVoltage = intent.getFloatExtra(Constants.SENSOR_VOLTAGE_DISTANCE, 3.4f) / 1000.f;

                        break;

                    default:
                        Log.d("ADservice", " mode error");
                        break;
                }
            }
        }
    }
}
