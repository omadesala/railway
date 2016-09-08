package cn.christian.server;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/9/1.
 */
public class ADService extends Service {

    private static WifiManager wifi;
    private boolean running = false;

    public static final int SCAN_MODE = 0;
    public static final int INTERUPE_MODE = 2;
    public static final int SETTING_MODE = 3;
    public static final int MSG_DISTANCE = 4;
    public static final String MSG_TYPE = "MSG_TYPE";

    public static final String SENSOR_MAX_SCOPE = "SENSOR_MAX_SCOPE";
    public static final String SENSOR_VELOCITY = "SENSOR_VELOCITY";
    public static final String SENSOR_ZERO_POINT = "SENSOR_ZERO_POINT";
    public static final String SENSOR_VOLTAGE_SCOPE = "SENSOR_VOLTAGE_SCOPE";
    public static final String SENSOR_DATA = "MEASURE_DISTANCE";

    public static final String DEVICE_ACTION = "com.christian.server.DEVICE_ACTION";
    public static final String SETTING_ACTION = "com.christian.server.SETTING_ACTION";
    public static final String SENSOR_DATA_COMMING = "com.christian.server.SENSOR_DISTANCE";

    private int curMode = SCAN_MODE;

    public static float sensorScopevalue = 0;
    public static float sensorVelocityvalue = 0;
    public static float sensorZerovalue = 0;
    public static float sensorVoltateScopevalue = 0;

    public static float micronVoltage = (float) (17.0 / 5000);// 每微米电压值

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
            sensorScopevalue = setting.getFloat(SettingFragment.sensorScope, 0);
            sensorVelocityvalue = setting.getFloat(SettingFragment.sensorVelocity, 0);
            sensorZerovalue = setting.getFloat(SettingFragment.sensorZero, 0);
            sensorVoltateScopevalue = setting.getFloat(SettingFragment.sensorVoltageScope, 0);


            if (sensorVoltateScopevalue > 0 && sensorScopevalue > 0) {
                micronVoltage = sensorVoltateScopevalue / (sensorScopevalue * 1000);
            }
        }


        ActionReceiver actionReceiver = new ActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DEVICE_ACTION);
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
            apConfig.preSharedKey = "11111111";
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
        private static boolean started = false;
        private static Context mContext = null;

        public static void startListen(Context ctx) {
            if (started) {
                return;
            }

            Log.e(TAG, "+startListen()");
            mContext = ctx;
            Thread serverSocketListen = new Thread(new MySocketServer());
            serverSocketListen.start();
            Log.e(TAG, "-startListen()");
            started = true;
        }

        @Override
        public void run() {
            try {
                // establish server socket
                int connIndex = 0;
                ServerSocket serverSocket = new ServerSocket(serverListenPort);//, connectionMaxLength, InetAddress.getByName(serverIpString));
                Log.e(TAG, "port:" + serverSocket.getLocalPort());

                while (true) {
                    Socket incoming = serverSocket.accept();
                    Log.e(TAG, "Connected a client!connIndex:" + connIndex + " RemoteSocketAddress:" + String.valueOf(incoming.getRemoteSocketAddress()));
                    Thread connHandle = new Thread(new ConnectionHandle(mContext, incoming, connIndex));
                    connHandle.start();
                    connIndex++;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class SendActionHandle implements Runnable {

        private final String INTERUPT_MODE = "MODE=2_11_0_2AD00001";
        private final String VOLTAGE_MODE = "MODE=3_3A_100";
        private final String SCAN_MODE = "MODE=0_2AD00001";
//
//        private final String INTERUPT_MODE = "MODE=2_11_1_2AD00001";
//        private final String SCAN_MODE = "MODE=0_2AD00001";


        private static boolean sendStatus = false;
        private static int sendCommand = 0;
        private static int scanMode = 0;
        private static int interuptMode = 2;

        static private OutputStream outputStream;

        public SendActionHandle(OutputStream os) {
            outputStream = os;
        }

        @Override
        public void run() {

            try {
                while (true) {

                    if (sendStatus) {
                        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF8");
                        if (sendCommand == scanMode) {
//                            writer.write(SCAN_MODE);
                            outputStream.write(SCAN_MODE.getBytes());
                            Log.d("SERVICE", "send scan mode to device ---- " + SCAN_MODE);
                        } else if (sendCommand == interuptMode) {
                            //outputStream.write(INTERUPT_MODE.getBytes());
                            outputStream.write(VOLTAGE_MODE.getBytes());
//                            writer.write(INTERUPT_MODE);
                            Log.d("SERVICE", "send interupt mode to device ---" + INTERUPT_MODE);
                        }
                        sendStatus = false;
                    }
                    Thread.sleep(50);
                }

            } catch (Exception e) {
                Log.d("SERVICE", e.getMessage());
            }
        }

        static public void sendCmd(int mode) {
            sendStatus = true;
            if (mode == scanMode) {
                sendCommand = scanMode;
            } else if (mode == interuptMode) {
                sendCommand = interuptMode;
            }
        }
    }


    static class ConnectionHandle implements Runnable {
        public static final String TAG = "ConnectionHandle:";

        private static Context mContext;
        private Socket connectedSocket;
        private int connIndex;


        public ConnectionHandle(Context ctx, Socket incoming, int connIdx) {
            mContext = ctx;
            connectedSocket = incoming;
            connIndex = connIdx;
        }


        @Override
        public void run() {
            Log.e(TAG, "+run()");
            try {
                try {
                    InputStream inStream = connectedSocket.getInputStream();
                    OutputStream outStream = connectedSocket.getOutputStream();

                    new Thread(new SendActionHandle(outStream)).start();
                    Scanner in = new Scanner(inStream, "UTF8");
                    //PrintStream out = new PrintStream(outStream, true, "UTF8");
                    //PrintWriter out = new PrintWriter(outStream, true);
                    //InputStreamReader  reader  = new InputStreamReader(inStream, "UTF8");
                    //String test = TAG + "abc陈123";
                    //writer.write(test);
                    //writer.flush();
                    //out.println("客户端连接成功！");
                    //out.flush();
                    boolean done = false;

                    StringBuffer sb = new StringBuffer();
                    DataParser dataParser = new DataParser();

                    while (!done && in.hasNext()) {

                        String token = in.next();
//                        Log.e(TAG, token);

                        if (token.startsWith("+YAV")) {
                            sb = new StringBuffer();
                        }
                        sb.append(token);

                        if (token.endsWith("EEFF")) {
                            String record = sb.toString();


//                            float voltage = dataParser.chanel0Voltage(record);
                            try {
                                float[] distance = dataParser.getValidateData(record);
                                if (distance != null) {
                                    Log.d("ADService", "data length is: " + distance.length);
                                    Log.d("ADService", "data is: " + distance.toString());
                                    Intent intent = new Intent();
                                    intent.setAction(ADService.SENSOR_DATA_COMMING);
                                    intent.putExtra(ADService.SENSOR_DATA, distance);
                                    mContext.sendBroadcast(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ADService", e.getMessage());
                            }
                        }
                    }

                    connectedSocket.close();

                    Thread.sleep(10);
                } finally {
                    //incoming.close();
                    //outStream.close();
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
            int type = intent.getIntExtra(MSG_TYPE, -1);
            switch (type) {
                case SCAN_MODE:
                    if (curMode != SCAN_MODE) {
                        curMode = SCAN_MODE;
                        // SEND SCAN_MODE CMD TO DEVICE
                        SendActionHandle.sendCmd(SCAN_MODE);
                    }
                    break;
                case INTERUPE_MODE:
                    if (curMode != INTERUPE_MODE) {
                        curMode = INTERUPE_MODE;
                        // SEND INTERUPE_MODE CMD TO DEVICE
                        // SendActionHandle.sendCmd(INTERUPE_MODE);
                    }

                    break;

                case SETTING_MODE:

                    sensorScopevalue = intent.getFloatExtra(SENSOR_MAX_SCOPE, 0);
                    sensorZerovalue = intent.getFloatExtra(SENSOR_ZERO_POINT, 0);
                    sensorVoltateScopevalue = intent.getFloatExtra(SENSOR_VOLTAGE_SCOPE, 0);

                    if (sensorVoltateScopevalue > 0 && sensorScopevalue > 0) {
                        micronVoltage = sensorVoltateScopevalue / (sensorScopevalue * 1000);
                    }
                    break;

                default:
                    Log.d("ADservice", "mode error");
                    break;
            }
        }
    }
}
