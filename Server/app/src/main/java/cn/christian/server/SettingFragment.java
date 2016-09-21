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

import cn.christian.server.utils.Constants;

/**
 * Created by Administrator on 2016/9/1.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingFragment extends Fragment {


    private EditText sensorScopeEditText;
    private EditText sensorVelocityEditText;
    private EditText measureDistanceEditText;
    private EditText sensorVoltageDistanceEditText;

    private EditText minScopeEditText;
    private EditText maxScopeEditText;


    float sensorScopeValue = 5.0f;
    float sensorVelocityValue = 0.418979f;
    float measureDistanceValue = 1.0f;
    float sensorVoltageDistanceValue = 3.4f;
    float minScopeValue = 0.0f;
    float maxScopeValue = 0.0f;


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
        sensorVoltageDistanceEditText = (EditText) settingLayout.findViewById(R.id.sensor_voltage_distance);

        measureDistanceEditText = (EditText) settingLayout.findViewById(R.id.measure_distance);

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

        sensorVoltageDistanceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(sensorVoltageDistanceEditText.getWindowToken(), 0);
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
                                        Editable sensorVoltateDistanceValueEdit = sensorVoltageDistanceEditText.getText();
                                        Editable sensorVelocityValueEdit = sensorVelocityEditText.getText();
                                        Editable measureDistanceValueEdit = measureDistanceEditText.getText();

                                        Editable maxScopeEdit = maxScopeEditText.getText();
                                        Editable minScopeEdit = minScopeEditText.getText();

                                        String sensorScopeStr = "";
                                        String sensorVoltageDistanceScopeStr = "";
                                        String sensorVelocityStr = "";
                                        String measureDistanceStr = "";

                                        String minScopeStr = "";
                                        String maxScopeStr = "";

                                        if (sensorScopeValueEdit == null || Strings.isNullOrEmpty(sensorScopeValueEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            sensorScopeStr = sensorScopeValueEdit.toString();
                                        }


                                        if (sensorVoltateDistanceValueEdit == null || Strings.isNullOrEmpty(sensorVoltateDistanceValueEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            sensorVoltageDistanceScopeStr = sensorVoltateDistanceValueEdit.toString();
                                        }


                                        if (sensorVelocityValueEdit == null || Strings.isNullOrEmpty(sensorVelocityValueEdit.toString())) {
                                            showDialog("参数提示", "未输入任何参数");
                                            return;
                                        } else {
                                            sensorVelocityStr = sensorVelocityValueEdit.toString();
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


                                        sensorScopeValue = Float.parseFloat(sensorScopeStr);
                                        if (sensorScopeValue <= 0) {
                                            showDialog("错误提示", "传感器量程不能小于等于0");
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


                                        sensorVoltageDistanceValue = Float.parseFloat(sensorVoltageDistanceScopeStr);
                                        if (sensorVoltageDistanceValue < 0) {
                                            showDialog("错误提示", "输入不能小于0");
                                            return;
                                        }


                                        minScopeValue = Float.parseFloat(minScopeStr);
                                        maxScopeValue = Float.parseFloat(maxScopeStr);
                                        if (minScopeValue > maxScopeValue) {
                                            showDialog("错误提示", "显示上限小于下限");
                                            return;
                                        }

                                        edit.putFloat(Constants.sensorScope, sensorScopeValue);
                                        edit.putFloat(Constants.sensorVelocity, sensorVelocityValue);
                                        edit.putFloat(Constants.measureDistance, measureDistanceValue);
                                        edit.putFloat(Constants.sensorVoltageDistance, sensorVoltageDistanceValue);

                                        edit.putFloat(Constants.maxScope, maxScopeValue);
                                        edit.putFloat(Constants.minScope, minScopeValue);

                                        edit.commit();

                                        sendSensorParameterToService();
                                        Toast.makeText(
                                                getActivity(),
                                                "保存成功", Toast.LENGTH_LONG).
                                                show();
                                    }
                                }

        );

        SharedPreferences setting = getActivity().getSharedPreferences(Constants.SETTINGS, Activity.MODE_PRIVATE);
        if (setting != null) {

            sensorScopeEditText.setText(String.valueOf(setting.getFloat(Constants.sensorScope, 5.0f)));
            sensorVelocityEditText.setText(String.valueOf(setting.getFloat(Constants.sensorVelocity, 0.418879f)));
            sensorVoltageDistanceEditText.setText(String.valueOf(setting.getFloat(Constants.sensorVoltageDistance, 3.4f)));
            measureDistanceEditText.setText(String.valueOf(setting.getFloat(Constants.measureDistance, 5.0f)));

            minScopeEditText.setText(String.valueOf(setting.getFloat(Constants.minScope, -1.5f)));
            maxScopeEditText.setText(String.valueOf(setting.getFloat(Constants.maxScope, 1.5f)));
        }


        return settingLayout;
    }

    protected void sendBroadcastToService(int state) {

        Intent intent = new Intent();
        intent.setAction(Constants.DEVICE_ACTION);
        intent.putExtra(Constants.MSG_TYPE, state);
        //向后台Service发送播放控制的广播
        getActivity().sendBroadcast(intent);

    }

    protected void sendSensorParameterToService() {

        Intent intent = new Intent();
        intent.setAction(Constants.SETTING_ACTION);
        intent.putExtra(Constants.MSG_TYPE, Constants.SETTING_MODE);

        intent.putExtra(Constants.SENSOR_MAX_SCOPE, sensorScopeValue);
        intent.putExtra(Constants.SENSOR_VELOCITY, sensorVelocityValue);
        intent.putExtra(Constants.MEASURE_DISTANCE, measureDistanceValue);
        intent.putExtra(Constants.SENSOR_VOLTAGE_DISTANCE, sensorVoltageDistanceValue);

        getActivity().sendBroadcast(intent);

    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences setting = getActivity().getSharedPreferences("setting", Activity.MODE_PRIVATE);
        if (setting != null) {

            sensorScopeEditText.setText(String.valueOf(setting.getFloat(Constants.sensorScope, 5.0f)));
            sensorVelocityEditText.setText(String.valueOf(setting.getFloat(Constants.sensorVelocity, 0.418879f)));
            measureDistanceEditText.setText(String.valueOf(setting.getFloat(Constants.measureDistance, 5.0f)));
            sensorVoltageDistanceEditText.setText(String.valueOf(setting.getFloat(Constants.sensorVoltageDistance, 3.4f)));

            minScopeEditText.setText(String.valueOf(setting.getFloat(Constants.minScope, -1.5f)));
            maxScopeEditText.setText(String.valueOf(setting.getFloat(Constants.maxScope, 1.5f)));
        }
    }
}