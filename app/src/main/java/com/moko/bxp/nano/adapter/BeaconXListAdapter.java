package com.moko.bxp.nano.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.elvishew.xlog.XLog;
import com.moko.bxp.nano.R;
import com.moko.bxp.nano.entity.BeaconXInfo;
import com.moko.bxp.nano.entity.BeaconXTLM;
import com.moko.bxp.nano.entity.BeaconXUID;
import com.moko.bxp.nano.entity.BeaconXiBeacon;
import com.moko.bxp.nano.entity.SensorInfo;
import com.moko.bxp.nano.utils.BeaconXParser;

import java.util.ArrayList;
import java.util.Collections;

public class BeaconXListAdapter extends BaseQuickAdapter<BeaconXInfo, BaseViewHolder> {
    public BeaconXListAdapter() {
        super(R.layout.list_item_device_nano);
    }

    @Override
    protected void convert(BaseViewHolder helper, BeaconXInfo item) {
        helper.setText(R.id.tv_mac, item.mac);
        helper.setText(R.id.tv_tag, TextUtils.isEmpty(item.tagId) ? "Tag ID:N/A" : String.format("Tag ID:0x%s", item.tagId));
        helper.setText(R.id.tv_rssi, String.format("%ddBm", item.rssi));
        helper.setText(R.id.tv_interval_time, item.intervalTime == 0 ? "<->N/A" : String.format("<->%dms", item.intervalTime));
        helper.setText(R.id.tv_battery, item.battery < 0 ? "N/A" : String.format("%dmV", item.battery));
        LinearLayout parent = helper.getView(R.id.ll_data);
        parent.removeAllViews();
        ArrayList<BeaconXInfo.ValidData> validDatas = new ArrayList<>(item.validDataHashMap.values());
        Collections.sort(validDatas, (lhs, rhs) -> {
            if (lhs.type > rhs.type) {
                return 1;
            } else if (lhs.type < rhs.type) {
                return -1;
            }
            return 0;
        });

        for (BeaconXInfo.ValidData validData : validDatas) {
            XLog.i(validData.toString());
            if (validData.type == BeaconXInfo.VALID_DATA_FRAME_TYPE_UID) {
                parent.addView(createUIDView(BeaconXParser.getUID(validData.data)));
            }
            if (validData.type == BeaconXInfo.VALID_DATA_FRAME_TYPE_TLM) {
                parent.addView(createTLMView(BeaconXParser.getTLM(validData.data)));
            }
            if (validData.type == BeaconXInfo.VALID_DATA_FRAME_TYPE_IBEACON_APPLE) {
                BeaconXiBeacon beaconXiBeacon = BeaconXParser.getiBeacon(item.rssi, validData.data);
                parent.addView(createiBeaconView(beaconXiBeacon));
            }
            if (validData.type == BeaconXInfo.VALID_DATA_FRAME_TYPE_SENSOR_INFO) {
                parent.addView(createTagTempView(BeaconXParser.getSensorInfo(validData.data)));
            }
        }
    }

    private View createUIDView(BeaconXUID uid) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.beaconx_uid, null);
        TextView tvRSSI0M = view.findViewById(R.id.tv_rssi_0m);
        TextView tvNameSpace = view.findViewById(R.id.tv_namespace);
        TextView tvInstanceId = view.findViewById(R.id.tv_instance_id);
        tvRSSI0M.setText(String.format("%sdBm", uid.rangingData));
        tvNameSpace.setText("0x" + uid.namespace.toUpperCase());
        tvInstanceId.setText("0x" + uid.instanceId.toUpperCase());
        return view;
    }

    private View createTLMView(BeaconXTLM tlm) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.beaconx_tlm, null);
        TextView tv_vbatt = view.findViewById(R.id.tv_vbatt);
        TextView tv_temp = view.findViewById(R.id.tv_temp);
        TextView tv_adv_cnt = view.findViewById(R.id.tv_adv_cnt);
        TextView tv_sec_cnt = view.findViewById(R.id.tv_sec_cnt);
        tv_vbatt.setText(String.format("%smV", tlm.vbatt));
        tv_temp.setText(tlm.temp);
        tv_adv_cnt.setText(tlm.adv_cnt);
        tv_sec_cnt.setText(tlm.sec_cnt);
        return view;
    }

    private View createiBeaconView(BeaconXiBeacon iBeacon) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.beaconx_ibeacon_nano, null);
        TextView tv_rssi_1m = view.findViewById(R.id.tv_rssi_1m);
        TextView tv_uuid = view.findViewById(R.id.tv_uuid);
        TextView tv_major = view.findViewById(R.id.tv_major);
        TextView tv_minor = view.findViewById(R.id.tv_minor);
        TextView tv_proximity_state = view.findViewById(R.id.tv_proximity_state);

        tv_rssi_1m.setText(String.format("%sdBm", iBeacon.rangingData));
        tv_proximity_state.setText(iBeacon.distanceDesc);
        tv_uuid.setText(iBeacon.uuid);
        tv_major.setText(iBeacon.major);
        tv_minor.setText(iBeacon.minor);
        return view;
    }

    private View createTagTempView(SensorInfo sensorInfo) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.beaconx_sensor, null);
        TextView tv_temp = view.findViewById(R.id.tv_temp);
        tv_temp.setText(sensorInfo.temperature);
        return view;
    }
}
