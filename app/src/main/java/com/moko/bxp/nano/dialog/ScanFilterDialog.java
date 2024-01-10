package com.moko.bxp.nano.dialog;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.moko.bxp.nano.R;
import com.moko.bxp.nano.databinding.DialogScanFilterNanoBinding;


public class ScanFilterDialog extends MokoBaseDialog<DialogScanFilterNanoBinding> {
    public static final String TAG = ScanFilterDialog.class.getSimpleName();

    private int filterRssi;
    private String filterCondition;

    @Override
    protected DialogScanFilterNanoBinding getViewBind(LayoutInflater inflater, ViewGroup container) {
        return DialogScanFilterNanoBinding.inflate(inflater, container, false);
    }

    @Override
    protected void onCreateView() {
        mBind.tvRssi.setText(String.format("%sdBm", filterRssi + ""));
        mBind.sbRssi.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int rssi = (progress * -1);
                mBind.tvRssi.setText(String.format("%sdBm", rssi + ""));
                filterRssi = rssi;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBind.sbRssi.setProgress(Math.abs(filterRssi));
        if (!TextUtils.isEmpty(filterCondition)) {
            mBind.etFilterCondition.setText(filterCondition);
            mBind.etFilterCondition.setSelection(filterCondition.length());
        }
        mBind.ivFilterDelete.setOnClickListener(v -> mBind.etFilterCondition.setText(""));
        mBind.tvDone.setOnClickListener(v -> {
            listener.onDone(mBind.etFilterCondition.getText().toString(),
                    filterRssi);
            dismiss();
        });
    }

    @Override
    public int getDialogStyle() {
        return R.style.CenterDialog;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public float getDimAmount() {
        return 0.7f;
    }

    @Override
    public boolean getCancelOutside() {
        return true;
    }

    @Override
    public boolean getCancellable() {
        return true;
    }

    private OnScanFilterListener listener;

    public void setOnScanFilterListener(OnScanFilterListener listener) {
        this.listener = listener;
    }

    public void setFilterCondition(String filterCondition) {
        this.filterCondition = filterCondition;
    }


    public void setFilterRssi(int filterRssi) {
        this.filterRssi = filterRssi;
    }

    public interface OnScanFilterListener {
        void onDone(String filterCondition, int filterRssi);
    }
}
