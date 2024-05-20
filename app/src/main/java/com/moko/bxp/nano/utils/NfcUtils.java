package com.moko.bxp.nano.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Parcelable;
import android.provider.Settings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.moko.ble.lib.utils.MokoUtils.bytesToHexString;

public class NfcUtils {

    //nfc
    public static NfcAdapter mNfcAdapter;
    public static IntentFilter[] mIntentFilter = null;
    public static PendingIntent mPendingIntent = null;
    public static String[][] mTechList = null;

    /**
     * 构造函数，用于初始化nfc
     */
    public NfcUtils(Activity activity) {
        mNfcAdapter = NfcCheck(activity);
        NfcInit(activity);
    }

    /**
     * 检查NFC是否打开
     */
    public static NfcAdapter NfcCheck(Activity activity) {
        NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            return null;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
                activity.startActivity(setNfc);
            }
        }
        return mNfcAdapter;
    }

    /**
     * 初始化nfc设置
     */
    public static void NfcInit(Activity activity) {
        mPendingIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    public static String readNFCFromTag(Intent intent) throws UnsupportedEncodingException {
        Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawArray != null) {
            NdefMessage mNdefMsg = (NdefMessage) rawArray[0];
            NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
            if (mNdefRecord != null) {
                String readResult = new String(mNdefRecord.getPayload(), "UTF-8");
                return readResult;
            }
        }
        return "";
    }


    public static String parseTag(Intent intent) {
        int sectorCount = 0;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareClassic mifageClassic = MifareClassic.get(tag);
        try {
            mifageClassic.connect();
            sectorCount = mifageClassic.getSectorCount();
            int byteIndex = 0;
            for (int i = 0; i < sectorCount; i++) {
                boolean auth = mifageClassic.authenticateSectorWithKeyA(i, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY);
                if (!auth) {
                    return null;
                }
//                int blockCount = mifageClassic.getBlockCountInSector(i);
//                int blockIndex = mifageClassic.sectorToBlock(i);
//                for (int j = 0; j < blockCount; j++) {
//                    if (j + 1 == blockCount) {
//                        continue;
//                    }
//                    byte buffer[] = new byte[16];
//                    buffer = mifageClassic.readBlock(blockIndex);
//                    if (blockIndex == 4) {
//                        System.arraycopy(buffer, 9, content, byteIndex, 6);
//                        byteIndex += 6;
//                    } else {
//                        System.arraycopy(buffer, 0, content, byteIndex, 16);
//                        byteIndex += 16;
//                    }
//                    blockIndex++;
//                    XLog.i(new String(content));
//                }
            }
//            String contentStr = new String(content);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取nfcID
     */
    public static String readNFCId(Intent intent) throws UnsupportedEncodingException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (null != tag)
            return ByteArrayToHexString(tag.getId());
        return null;
    }

    /**
     * 将字节数组转换为字符串
     */
    private static String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }
} 