package cn.christian.server;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.common.base.Strings;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingFragment extends Fragment {


    private EditText sensorScopeEditText;
    private EditText sensorVelocityEditText;
    private EditText measureDistanceEditText;
    private EditText sensorZeroEditText;
    private EditText sensorVoltageScopeEditText;

    private EditText minScopeEditText;
    private EditText maxScopeEditText;


    private RadioGroup measureMode;
    private RadioButton scanMode;
    private RadioButton interuptMode;


    public static String sensorScope = "SENSOR_SCOPE";
    public static String sensorVelocity = "SENSOR_VELOCITY";
    public static String measureDistance = "MEASURE_DISTANCE";
    public static String sensorZero = "SENSOR_ZERO";
    public static String sensorVoltageScope = "SENSOR_VOLTAGE_SCOPE";

    public static String maxScope = "MAXSCOPE";
    public static String minScope = "MINSCOPE";

    public static String dataMode = "DATAMODE";


    float sensorScopeValue = 5;
    float sensorVelocityValue = (float) 0.5;
    float measureDistanceValue = (float) 1.0;
    float sensorZeroValue = 2;
    float sensorVoltageScopeValue = 12;
    float minScopeValue = 0;
    float maxScopeValue = 0;


    private Button save;

    private void showDialog(String title, String content) {

        new AlertDialog.Builder(getActivity()).setTitle(title).setMessage(content).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        }).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View settingLayout = inflater.inflate(R.layout.fragment_setting, container, false);

        sensorScopeEditText = (EditText) settingLayout.findViewById(R.id.sensor_scope);
        sensorVelocityEditText = (EditText) settingLayout.findViewById(R.id.sensor_velocity);
        measureDistanceEditText = (EditText) settingLayout.findViewById(R.id.measure_distance);
        sensorZeroEditText = (EditText) settingLayout.findViewById(R.id.sensor_zero);
        sensorVoltageScopeEditText = (EditText) settingLayout.findViewById(R.id.sensor_voltage_scope);

        minScopeEditText = (EditText) settingLayout.findViewById(R.id.min_scope);
        maxScopeEditText = (EditText) settingLayout.findViewById(R.id.max_scope);


        sensorScopeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sensorScopeEditText.getWindowToken(), 0);
                }
            }
        });
        sensorVelocityEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sensorVelocityEditText.getWindowToken(), 0);
                }
            }
        });
        measureDistanceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(measureDistanceEditText.getWindowToken(), 0);
                }
            }
        });
        sensorZeroEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sensorZeroEditText.getWindowToken(), 0);
                }
            }
        });
        sensorVoltageScopeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sensorVoltageScopeEditText.getWindowToken(), 0);
                }
            }
        });


        maxScopeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(maxScopeEditText.getWindowToken(), 0);
                }
            }
        });
        minScopeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(minScopeEditText.getWindowToken(), 0);
                }
            }
        });

        save = (Button) settingLayout.findViewById(R.id.setting_save);
        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor edit = setting.edit();

                                        Editable sensorScopeValueEdit = sensorScopeEditText.getText();
                                        Editable sensorVelocityValueEdit = sensorVelocityEditText.getText();
                                        Editable measureDistanceValueEdit = measureDistanceEditText.getText();
                                        Editable sensorZeroEdit = sensorZeroEditText.getText();
                                        Editable sensorVoltageScopeEdit = sensorVoltageScopeEditText.getText();

                                        Editable maxScopeEdit = maxScopeEditText.getText();
                                        Editable minScopeEdit = minScopeEditText.getText();

                                        String sensorScopeStr = "";
                                        String sensorVelocityStr = "";
                                        String measureDistanceStr = "";
                                        String sensorZeroStr = "";
                                        String sensorVoltageScopeStr = "";

                                        String minScopeStr = "";
                                        String maxScopeStr = "";

                                        if (sensorScopeValueEdit == null || Strings.isNullOrEmpty(sensorScopeValueEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            sensorScopeStr = sensorScopeValueEdit.toString();
                                        }
                                        if (sensorVelocityValueEdit == null || Strings.isNullOrEmpty(sensorVelocityValueEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            sensorVelocityStr = sensorVelocityValueEdit.toString();
                                        }


                                        if (sensorZeroEdit == null || Strings.isNullOrEmpty(sensorZeroEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            sensorZeroStr = sensorZeroEdit.toString();
                                        }
                                        if (sensorVoltageScopeEdit == null || Strings.isNullOrEmpty(sensorVoltageScopeEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            sensorVoltageScopeStr = sensorVoltageScopeEdit.toString();
                                        }
                                        if (measureDistanceValueEdit == null || Strings.isNullOrEmpty(measureDistanceValueEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            measureDistanceStr = measureDistanceValueEdit.toString();
                                        }


                                        if (minScopeEdit == null || Strings.isNullOrEmpty(minScopeEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            minScopeStr = minScopeEdit.toString();
                                        }

                                        if (maxScopeEdit == null || Strings.isNullOrEmpty(maxScopeEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            maxScopeStr = maxScopeEdit.toString();
                                        }


                                        minScopeValue = Float.parseFloat(minScopeStr);
                                        if (minScopeValue < 0) {
                                            showDialog("错误提示", "显示下限不能小于0");
                                            return;
                                        }
                                        maxScopeValue = Float.parseFloat(maxScopeStr);
                                        if (maxScopeValue < 0 || minScopeValue > maxScopeValue) {
                                            showDialog("错误提示", "显示上限小于0或者小于下限");
                                            return;
                                        }

                                        sensorVelocityValue = Float.parseFloat(sensorVelocityStr);
                                        if (sensorVelocityValue < 0) {
                                            showDialog("错误提示", "传感器移动速度不能小于0");
                                            return;
                                        }
                                        measureDistanceValue = Float.parseFloat(measureDistanceStr);
                                        if (measureDistanceValue < 0) {
                                            showDialog("错误提示", "测量长度不能小于0");
                                            return;
                                        }


                                        sensorScopeValue = Float.parseFloat(sensorScopeStr);
                                        if (sensorScopeValue <= 0) {
                                            showDialog("错误提示", "传感器量程不能小于等于0");
                                            return;
                                        }
                                        sensorZeroValue = Float.parseFloat(sensorZeroStr);
                                        if (sensorZeroValue <= 0) {
                                            showDialog("错误提示", "零点不能小于等于0");
                                            return;
                                        }


                                        edit.putFloat(maxScope, maxScopeValue);
                                        edit.putFloat(minScope, minScopeValue);

                                        edit.putFloat(sensorScope, sensorScopeValue);
                                        edit.putFloat(sensorVelocity, sensorVelocityValue);
                                        edit.putFloat(measureDistance, measureDistanceValue);
                                        edit.putFloat(sensorZero, sensorZeroValue);
                                        edit.putFloat(sensorVoltageScope, sensorVoltageScopeValue);

                                        if (scanMode.isChecked()) {
                                            edit.putBoolean(dataMode, true);
                                            sendBroadcastToService(ADService.SCAN_MODE);
                                        } else {
                                            edit.putBoolean(dataMode, false);
                                            sendBroadcastToService(ADService.INTERUPE_MODE);
                                        }

                                        edit.commit();

                                        sendSensorParameterToService();
                                        Toast.makeText(
                                                getActivity(),
                                                "保存成功", Toast.LENGTH_LONG).
                                                show();
                                    }
                                }

        );
        measureMode = (RadioGroup) settingLayout.findViewById(R.id.measure_mode);
        scanMode = (RadioButton) settingLayout.findViewById(R.id.measure_scan);
        interuptMode = (RadioButton) settingLayout.findViewById(R.id.measure_interupt);

        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        if (setting != null) {
            minScopeEditText.setText(String.valueOf(setting.getFloat(minScope, 0)));
            maxScopeEditText.setText(String.valueOf(setting.getFloat(maxScope, 0)));
            sensorScopeEditText.setText(String.valueOf(setting.getFloat(sensorScope, 0)));
            sensorVelocityEditText.setText(String.valueOf(setting.getFloat(sensorVelocity, 0)));
            measureDistanceEditText.setText(String.valueOf(setting.getFloat(measureDistance, 0)));
            sensorZeroEditText.setText(String.valueOf(setting.getFloat(sensorZero, 0)));
            sensorVoltageScopeEditText.setText(String.valueOf(setting.getFloat(sensorVoltageScope, 0)));


            boolean mode = setting.getBoolean(dataMode, true);
            if (mode) {
                scanMode.setChecked(true);
            } else {
                interuptMode.setChecked(true);
            }
        }


        return settingLayout;
    }

    protected void sendBroadcastToService(int state) {

        Intent intent = new Intent();
        intent.setAction(ADService.DEVICE_ACTION);
        intent.putExtra(ADService.MSG_TYPE, state);
        //向后台Service发送播放控制的广播
        getActivity().sendBroadcast(intent);

    }

    protected void sendSensorParameterToService() {

        Intent intent = new Intent();
        intent.setAction(ADService.SETTING_ACTION);
        intent.putExtra(ADService.MSG_TYPE, ADService.SETTING_MODE);

        intent.putExtra(ADService.SENSOR_MAX_SCOPE, sensorScopeValue);
        intent.putExtra(ADService.SENSOR_VELOCITY, sensorVelocityValue);
        intent.putExtra(ADService.SENSOR_ZERO_POINT, sensorZeroValue);
        intent.putExtra(ADService.SENSOR_VOLTAGE_SCOPE, sensorVoltageScopeValue);

        getActivity().sendBroadcast(intent);

    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        if (setting != null) {
            minScopeEditText.setText(String.valueOf(setting.getFloat(minScope, 0)));
            maxScopeEditText.setText(String.valueOf(setting.getFloat(maxScope, 0)));
            sensorScopeEditText.setText(String.valueOf(setting.getFloat(sensorScope, 0)));
            sensorVelocityEditText.setText(String.valueOf(setting.getFloat(sensorVelocity, 0)));
            measureDistanceEditText.setText(String.valueOf(setting.getFloat(measureDistance, 0)));
            sensorZeroEditText.setText(String.valueOf(setting.getFloat(sensorZero, 0)));
            sensorVoltageScopeEditText.setText(String.valueOf(setting.getFloat(sensorVoltageScope, 0)));
        }
    }
}