package cn.christian.server.utils;

/**
 * Created by Administrator on 2016/9/9.
 */
public class Constants {

    public static final String sensorScope = "SENSOR_SCOPE";
    public static final String sensorVelocity = "SENSOR_VELOCITY";
    public static final String measureDistance = "MEASURE_DISTANCE";
    public static final String sensorBaseCount = "SENSOR_BASE_COUNT";
    public static final String sensorVoltageMin = "SENSOR_VOLTAGE_MIN";
    public static final String sensorVoltageMax = "SENSOR_VOLTAGE_MAX";
    public static final String sensorVoltageDistance = "SENSOR_VOLTAGE_DISTANCE";

    public static final String maxScope = "MAXSCOPE";
    public static final String minScope = "MINSCOPE";
    public static final String dataMode = "DATAMODE";

    public static final String SETTINGS = "SETTINGS";

    public static final String SENSOR_MAX_SCOPE = "SENSOR_MAX_SCOPE";
    public static final String SENSOR_VELOCITY = "SENSOR_VELOCITY";
    public static final String MEASURE_DISTANCE = "MEASURE_DISTANCE";
    public static final String SENSOR_VOLTAGE_MIN = "SENSOR_VOLTAGE_MIN";
    public static final String SENSOR_VOLTAGE_DISTANCE = "SENSOR_VOLTAGE_DISTANCE";
    public static final String SENSOR_BASE_COUNT = "SENSOR_BASE_COUNT";
    public static final String SENSOR_VOLTAGE_MAX = "SENSOR_VOLTAGE_MAX";
    public static final String SENSOR_DATA = "MEASURE_DISTANCE";
    public static final String SENSOR_POSITION_DATA = "MEASURE_DISTANCE";
    public static final String SENSOR_POSITION_CHECK = "SENSOR_POSITION_CHECK";
    //ACTION
    public static final String DEVICE_ACTION = "com.christian.server.DEVICE_ACTION";
    public static final String SETTING_ACTION = "com.christian.server.SETTING_ACTION";
    public static final String SENSOR_DATA_COMMING = "com.christian.server.SENSOR_DISTANCE";
    public static final String SENSOR_POSITION_CHECK_ACTION = "com.christian.server.SENSOR_POSITION_CHECK_ACTION";
    public static final String SENSOR_BASE_POSITION_NOTCORRUCET = "com.christian.server.SENSOR_BASE_POSITION_NOTCORRUCET";

    // DATA PARSER
    public static final String SENSOR_DATA_START_TAG = "+YAV";
    public static final String SENSOR_DATA_END_TAG = "EEFF";
    public static final String SENSOR_CMD_STOP = "STOP_2AD00001";

    public static final String BASE_POSITION_TOO_HIGH = "BASE_POSITION_TOO_HIGH";
    public static final String BASE_POSITION_TOO_LOW = "BASE_POSITION_TOO_LOW";

    //MSG TYPE
    public static final String MSG_TYPE = "MSG_TYPE";
    public static final int SCAN_MODE = 0;
    public static final int INTERUPE_MODE = 2;
    public static final int SETTING_MODE = 3;
    public static final int SENSOR_POSITION_CHECK_OPEN = 4;
    public static final int SENSOR_POSITION_CHECK_CLOSE = 5;

    //ACTIVITY
    public static final String SENSOR_HISTORY_DATA = "SENSOR_HISTORY_DATA";
    public static final String SENSOR_HISTORY_DETAIL_ACTION = "SENSOR_HISTORY_DETAIL_ACTION";

}
