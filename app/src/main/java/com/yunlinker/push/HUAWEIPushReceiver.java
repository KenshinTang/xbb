package com.yunlinker.push;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.support.api.push.PushReceiver;

public class HUAWEIPushReceiver  extends PushReceiver {
    private static final String TAG = "HUAWEIPushReceiver";

    @Override
    public void onToken(Context context, String token, Bundle bundle) {
        Log.e(TAG, "onToken:" + token);
        ThirdPushTokenMgr.getInstance().setThirdPushToken(token);  // token 在此处传入，后续推送信息上报时需要使用
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
    }
}
