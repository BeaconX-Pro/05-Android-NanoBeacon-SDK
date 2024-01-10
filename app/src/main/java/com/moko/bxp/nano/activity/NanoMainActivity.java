package com.moko.bxp.nano.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.moko.ble.lib.event.ConnectStatusEvent;
import com.moko.ble.lib.event.OrderTaskResponseEvent;
import com.moko.bxp.nano.AppConstants;
import com.moko.bxp.nano.BuildConfig;
import com.moko.bxp.nano.R;
import com.moko.bxp.nano.adapter.BeaconXListAdapter;
import com.moko.bxp.nano.databinding.ActivityMainNanoBinding;
import com.moko.bxp.nano.dialog.AlertMessageDialog;
import com.moko.bxp.nano.dialog.LoadingDialog;
import com.moko.bxp.nano.dialog.ScanFilterDialog;
import com.moko.bxp.nano.entity.BeaconXInfo;
import com.moko.bxp.nano.utils.BeaconXInfoParseableImpl;
import com.moko.support.nano.MokoBleScanner;
import com.moko.support.nano.MokoSupport;
import com.moko.support.nano.callback.MokoScanDeviceCallback;
import com.moko.support.nano.entity.DeviceInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;


public class NanoMainActivity extends BaseActivity<ActivityMainNanoBinding> implements MokoScanDeviceCallback{


    private boolean mReceiverTag = false;
    private ConcurrentHashMap<String, BeaconXInfo> beaconXInfoHashMap;
    private ArrayList<BeaconXInfo> beaconXInfos;
    private BeaconXListAdapter adapter;
    private MokoBleScanner mokoBleScanner;
    private Handler mHandler;

    public static String PATH_LOGCAT;

    @Override
    protected void onCreate() {
        // 初始化Xlog
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 优先保存到SD卡中
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                PATH_LOGCAT = getExternalFilesDir(null).getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "mokoBeaconXPro" : "NanoBeacon");
            } else {
                PATH_LOGCAT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "mokoBeaconXPro" : "NanoBeacon");
            }
        } else {
            // 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = getFilesDir().getAbsolutePath() + File.separator + (BuildConfig.IS_LIBRARY ? "mokoBeaconXPro" : "NanoBeacon");
        }
        MokoSupport.getInstance().init(getApplicationContext());
        beaconXInfoHashMap = new ConcurrentHashMap<>();
        beaconXInfos = new ArrayList<>();
        adapter = new BeaconXListAdapter();
        adapter.replaceData(beaconXInfos);
        adapter.openLoadAnimation();
        mBind.rvDevices.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_recycleview_divider));
        mBind.rvDevices.addItemDecoration(itemDecoration);
        mBind.rvDevices.setAdapter(adapter);

        mHandler = new Handler(Looper.getMainLooper());
        mokoBleScanner = new MokoBleScanner(this);
        EventBus.getDefault().register(this);
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mReceiverTag = true;
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            MokoSupport.getInstance().enableBluetooth();
        } else {
            if (animation == null) {
                startScan();
            }
        }
    }

    @Override
    protected ActivityMainNanoBinding getViewBinding() {
        return ActivityMainNanoBinding.inflate(getLayoutInflater());
    }

    private String unLockResponse;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            if (animation != null) {
                                mHandler.removeMessages(0);
                                mokoBleScanner.stopScanDevice();
                                onStopScan();
                            }
                            break;
                        case BluetoothAdapter.STATE_ON:
                            if (animation == null) {
                                startScan();
                            }
                            break;

                    }
                }
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectStatusEvent(ConnectStatusEvent event) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderTaskResponseEvent(OrderTaskResponseEvent event) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CODE_DEVICE_INFO:
                    if (animation == null) {
                        startScan();
                    }
                    break;

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverTag) {
            mReceiverTag = false;
            // 注销广播
            unregisterReceiver(mReceiver);
        }
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onStartScan() {
        beaconXInfoHashMap.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (animation != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.replaceData(beaconXInfos);
                            mBind.tvDeviceNum.setText(String.format("DEVICE(%d)", beaconXInfos.size()));
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateDevices();
                }
            }
        }).start();
    }

    private BeaconXInfoParseableImpl beaconXInfoParseable;

    @Override
    public void onScanDevice(DeviceInfo deviceInfo) {
        BeaconXInfo beaconXInfo = beaconXInfoParseable.parseDeviceInfo(deviceInfo);
        if (beaconXInfo == null)
            return;
        beaconXInfoHashMap.put(beaconXInfo.mac, beaconXInfo);
    }

    @Override
    public void onStopScan() {
        findViewById(R.id.iv_refresh).clearAnimation();
        animation = null;
    }

    private void updateDevices() {
        beaconXInfos.clear();
        if (!TextUtils.isEmpty(filterCondition) || filterRssi != -100) {
            ArrayList<BeaconXInfo> beaconXInfosFilter = new ArrayList<>(beaconXInfoHashMap.values());
            Iterator<BeaconXInfo> iterator = beaconXInfosFilter.iterator();
            while (iterator.hasNext()) {
                BeaconXInfo beaconXInfo = iterator.next();
                if (beaconXInfo.rssi > filterRssi) {
                    if (TextUtils.isEmpty(filterCondition)) {
                        continue;
                    } else {
                        if (TextUtils.isEmpty(beaconXInfo.tagId) && TextUtils.isEmpty(beaconXInfo.mac)) {
                            iterator.remove();
                        } else if (TextUtils.isEmpty(beaconXInfo.tagId) && beaconXInfo.mac.toLowerCase().replaceAll(":", "").contains(filterCondition.toLowerCase())) {
                            continue;
                        } else if (TextUtils.isEmpty(beaconXInfo.mac) && beaconXInfo.tagId.toLowerCase().contains(filterCondition.toLowerCase())) {
                            continue;
                        } else if (!TextUtils.isEmpty(beaconXInfo.tagId) && !TextUtils.isEmpty(beaconXInfo.mac) && (beaconXInfo.tagId.toLowerCase().contains(filterCondition.toLowerCase()) || beaconXInfo.mac.toLowerCase().replaceAll(":", "").contains(filterCondition.toLowerCase()))) {
                            continue;
                        } else {
                            iterator.remove();
                        }
                    }
                } else {
                    iterator.remove();
                }
            }
            beaconXInfos.addAll(beaconXInfosFilter);
        } else {
            beaconXInfos.addAll(beaconXInfoHashMap.values());
        }
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        Collections.sort(beaconXInfos, (lhs, rhs) -> {
            if (lhs.rssi > rhs.rssi) {
                return -1;
            } else if (lhs.rssi < rhs.rssi) {
                return 1;
            }
            return 0;
        });
    }

    private Animation animation = null;
    public String filterCondition;
    public int filterRssi = -100;

    private void startScan() {
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            MokoSupport.getInstance().enableBluetooth();
            return;
        }
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        findViewById(R.id.iv_refresh).startAnimation(animation);
        beaconXInfoParseable = new BeaconXInfoParseableImpl();
        mokoBleScanner.startScanDevice(this);
    }


    private LoadingDialog mLoadingDialog;

    private void showLoadingProgressDialog() {
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getSupportFragmentManager());

    }

    private void dismissLoadingProgressDialog() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismissAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        back();
    }


    public void onBack(View view) {
        if (isWindowLocked())
            return;
        back();
    }

    public void onAbout(View view) {
        if (isWindowLocked())
            return;
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void onFilter(View view) {
        if (isWindowLocked())
            return;
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        ScanFilterDialog scanFilterDialog = new ScanFilterDialog();
        scanFilterDialog.setFilterCondition(filterCondition);
        scanFilterDialog.setFilterRssi(filterRssi);
        scanFilterDialog.setOnScanFilterListener((filterCondition, filterRssi) -> {
            NanoMainActivity.this.filterCondition = filterCondition;
            NanoMainActivity.this.filterRssi = filterRssi;
            if (!TextUtils.isEmpty(filterCondition) || filterRssi != -100) {
                mBind.rlFilter.setVisibility(View.VISIBLE);
                mBind.rlEditFilter.setVisibility(View.GONE);
                StringBuilder stringBuilder = new StringBuilder();
                if (!TextUtils.isEmpty(filterCondition)) {
                    stringBuilder.append(filterCondition);
                    stringBuilder.append(";");
                }
                if (filterRssi != -100) {
                    stringBuilder.append(String.format("%sdBm", filterRssi + ""));
                    stringBuilder.append(";");
                }
                mBind.tvFilter.setText(stringBuilder.toString());
            } else {
                mBind.rlFilter.setVisibility(View.GONE);
                mBind.rlEditFilter.setVisibility(View.VISIBLE);
            }
            if (isWindowLocked())
                return;
            if (animation == null) {
                startScan();
            }
        });
        scanFilterDialog.show(getSupportFragmentManager());
    }

    private void back() {
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        if (BuildConfig.IS_LIBRARY) {
            finish();
        } else {
            AlertMessageDialog dialog = new AlertMessageDialog();
            dialog.setMessage(R.string.main_exit_tips);
            dialog.setOnAlertConfirmListener(() -> NanoMainActivity.this.finish());
            dialog.show(getSupportFragmentManager());
        }
    }

    public void onRefresh(View view) {
        if (isWindowLocked())
            return;
        if (!MokoSupport.getInstance().isBluetoothOpen()) {
            // 蓝牙未打开，开启蓝牙
            MokoSupport.getInstance().enableBluetooth();
            return;
        }
        if (animation == null) {
            startScan();
        } else {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
    }

    public void onFilterDelete(View view) {
        if (animation != null) {
            mHandler.removeMessages(0);
            mokoBleScanner.stopScanDevice();
        }
        mBind.rlFilter.setVisibility(View.GONE);
        mBind.rlEditFilter.setVisibility(View.VISIBLE);
        filterCondition = "";
        filterRssi = -100;
        if (isWindowLocked())
            return;
        if (animation == null) {
            startScan();
        }
    }
}
