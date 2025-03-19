package com.moko.bxp.nano.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.moko.bxp.nano.BaseApplication;
import com.moko.bxp.nano.BuildConfig;
import com.moko.bxp.nano.R;
import com.moko.bxp.nano.databinding.ActivityAboutNanoBinding;
import com.moko.bxp.nano.utils.ToastUtils;
import com.moko.bxp.nano.utils.Utils;

import java.io.File;
import java.util.Calendar;


public class AboutActivity extends BaseActivity<ActivityAboutNanoBinding> {


    @Override
    protected void onCreate() {
        if (!BuildConfig.IS_LIBRARY) {
            mBind.appVersion.setText(String.format("APP Version:V%s", Utils.getVersionInfo(this)));
            mBind.tvFeedbackLog.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected ActivityAboutNanoBinding getViewBinding() {
        return ActivityAboutNanoBinding.inflate(getLayoutInflater());
    }


    public void onBack(View view) {
        finish();
    }

    public void onCompanyWebsite(View view) {
        if (isWindowLocked())
            return;
        Uri uri = Uri.parse("https://" + getString(R.string.company_website));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onFeedback(View view) {
        if (isWindowLocked())
            return;
        File trackerLog = new File(NanoMainActivity.PATH_LOGCAT + File.separator + "NanoBeacon.txt");
        File trackerLogBak = new File(NanoMainActivity.PATH_LOGCAT + File.separator + "NanoBeacon.txt.bak");
        File trackerCrashLog = new File(NanoMainActivity.PATH_LOGCAT + File.separator + "crash_log.txt");
        if (!trackerLog.exists() || !trackerLog.canRead()) {
            ToastUtils.showToast(this, "File is not exists!");
            return;
        }
        String address = "Development@mokotechnology.com";
        StringBuilder mailContent = new StringBuilder("NanoBeacon_");
        Calendar calendar = Calendar.getInstance();
        String date = Utils.calendar2strDate(calendar, "yyyyMMdd");
        mailContent.append(date);
        String title = mailContent.toString();
        if ((!trackerLogBak.exists() || !trackerLogBak.canRead())
                && (!trackerCrashLog.exists() || !trackerCrashLog.canRead())) {
            Utils.sendEmail(this, address, "", title, "Choose Email Client", trackerLog);
        } else if (!trackerCrashLog.exists() || !trackerCrashLog.canRead()) {
            Utils.sendEmail(this, address, "", title, "Choose Email Client", trackerLog, trackerLogBak);
        } else if (!trackerLogBak.exists() || !trackerLogBak.canRead()) {
            Utils.sendEmail(this, address, "", title, "Choose Email Client", trackerLog, trackerCrashLog);
        } else {
            Utils.sendEmail(this, address, "", title, "Choose Email Client", trackerLog, trackerLogBak, trackerCrashLog);
        }
    }
}
