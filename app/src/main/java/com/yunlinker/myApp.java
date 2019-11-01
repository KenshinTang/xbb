package com.yunlinker;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.security.rp.RPSDK;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.TextInfo;
import com.luck.picture.lib.PictureExternalPreviewActivity;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.CustomDialog;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToastManage;
import com.qiyukf.unicorn.api.ImageLoaderListener;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.StatusBarNotificationConfig;
import com.qiyukf.unicorn.api.UICustomization;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.UnicornImageLoader;
import com.qiyukf.unicorn.api.UnreadCountChangeListener;
import com.qiyukf.unicorn.api.YSFOptions;
import com.tencent.imsdk.TIMBackgroundParam;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMLogListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMOfflinePushSettings;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.session.SessionWrapper;
import com.tencent.imsdk.utils.IMFunc;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.config.CustomFaceConfig;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.config.TUIKitConfigs;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.yunlinker.baseclass.BaseActivity;
//import com.yunlinker.image.GlideApp;
import com.yunlinker.manager.ActivityManager;
import com.yunlinker.xbb.BuildConfig;
import com.yunlinker.xbb.ChatListActivity;
import com.yunlinker.xbb.MainActivity;
import com.yunlinker.xbb.R;


import org.xutils.x;

import java.io.IOException;
import java.util.List;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;

import static com.yunlinker.config.WebConfig.QQID;
import static com.yunlinker.config.WebConfig.QQSECRET;
import static com.yunlinker.config.WebConfig.QYSECRET;
import static com.yunlinker.config.WebConfig.UMKEY;
import static com.yunlinker.config.WebConfig.WXID;
import static com.yunlinker.config.WebConfig.WXSECRET;
import static com.yunlinker.config.WebConfig.saveKey;

/**
 * Created by lemon on 2017/8/21.
 */

public class myApp extends Application {
 private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "017cea90c1d5dcab11c17439c9b7fc1e");
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(false);
        initUmeng();
        UMShareAPI.get(this);
        BGAQRCodeUtil.setDebug(true);
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
        DialogSettings.DEBUGMODE = true;
        DialogSettings.isUseBlur = true;
        DialogSettings.theme = DialogSettings.THEME.LIGHT;
        DialogSettings.buttonTextInfo =
                new TextInfo().setFontColor(getResources().getColor(R.color.color_quxiao));
        DialogSettings.buttonPositiveTextInfo =
                new TextInfo().setFontColor(getResources().getColor(R.color.color_quedin));
        mContext = getApplicationContext();

        RPSDK.initialize(this);


        if (SessionWrapper.isMainProcess(getApplicationContext())) {
            TUIKitConfigs configs = TUIKit.getConfigs();
            configs.setSdkConfig(new TIMSdkConfig(1400249630));
            configs.setCustomFaceConfig(new CustomFaceConfig());
            configs.setGeneralConfig(new GeneralConfig());
            TUIKit.init(this, 1400249630, configs);


            TIMSdkConfig config = new TIMSdkConfig(1400249630)
                    .enableLogPrint(true)
                    .setLogLevel(TIMLogLevel.DEBUG)
                    .setLogPath(Environment.getExternalStorageDirectory().getPath() + "/justfortest/");

            //初始化 SDK
            TIMManager.getInstance().init(getApplicationContext(), config);

            registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback());


            TIMOfflinePushSettings settings = new TIMOfflinePushSettings();
            //开启离线推送
            settings.setEnabled(true);
            //设置收到 C2C 离线消息时的提示声音，以把声音文件放在 res/raw 文件夹下为例
            settings.setC2cMsgRemindSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.umqqqq));

            TIMManager.getInstance().setOfflinePushSettings(settings);

            TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
                @Override
                public boolean onNewMessages(List<TIMMessage> list) {
                    Log.e("1111111", "onNewMessages: " + "收到消息");
                    return false;
                }
            });


        }else if (IMFunc.isBrandHuawei()){
            //华为推送
            //HMSAgent.init(this);
        }

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        initIM();



        SharedPreferences sp = getSharedPreferences(saveKey, Context.MODE_PRIVATE);
        String uid = sp.getString("uid","");
        String sing = sp.getString("sing","");
        if (uid!=null&&!uid.equals("")&&sing!=null&&!sing.equals("")){
            TIMManager.getInstance().login(uid, sing, new TIMCallBack() {
                @Override
                public void onError(int code, String desc) {
                    //错误码 code 和错误描述 desc，可用于定位请求失败原因
                    //错误码 code 列表请参见错误码表
                }

                @Override
                public void onSuccess() {
                    // Toast.makeText(mContext, "11111111", Toast.LENGTH_SHORT).show();
                    Log.e("1111111111", "onSuccess: "+"走走走走走" );
                }
            });
        }







    }

    private void initIM() {

        //基本用户配置
        TIMUserConfig userConfig = new TIMUserConfig()
                //设置用户状态变更事件监听器
                .setUserStatusListener(new TIMUserStatusListener() {
                    @Override
                    public void onForceOffline() {
                        //被其他终端踢下线
                        // 问题1 我们使用一个账号去多个手机上登录 这时候 另一个手机的IM 就应该掉线
                        // 需要安卓做掉线处理，处理方式就是 安卓弹出提示 去登录界面
                        // 登录界面 open.html
                        Log.e("kenshin", "onForceOffline, your are kicked off, please login." );
                        showForceOffLineDialog();
//                        TIMManager.getInstance().logout(new TIMCallBack() {
//                            @Override
//                            public void onError(int code, String desc) {
//                                Log.e("kenshin", "logout failed code = " + code + ", desc = " + desc);
//                            }
//
//                            @Override
//                            public void onSuccess() {
//                                Log.i("kenshin", "logout success");
//
//                            }
//                        });
                    }

                    @Override
                    public void onUserSigExpired() {
                        //用户签名过期了，需要刷新 userSig 重新登录 IM SDK
                        // 问题2 没有登录其他手机 出现的IM登录过期 两种处理方式
                        // 1，提示用户去跳转登录，2：自动登录（自动登录需要调用js的一个方法 IM_Refsing）
                        Log.e("kenshin", "onUserSigExpired, your account is expired, auto login." );
                    }
                })
                //设置连接状态事件监听器
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                       // Log.i(tag, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                       // Log.i(tag, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                       // Log.i(tag, "onWifiNeedAuth");
                    }
                })
                //设置群组事件监听器
                .setGroupEventListener(new TIMGroupEventListener() {
                    @Override
                    public void onGroupTipsEvent(TIMGroupTipsElem elem) {
                       // Log.i(tag, "onGroupTipsEvent, type: " + elem.getTipsType());
                    }
                })
                //设置会话刷新监听器
                .setRefreshListener(new TIMRefreshListener() {
                    @Override
                    public void onRefresh() {
                        //Log.i(tag, "onRefresh");
                    }

                    @Override
                    public void onRefreshConversation(List<TIMConversation> conversations) {
                        //Log.i(tag, "onRefreshConversation, conversation size: " + conversations.size());
                    }
                });


         //开启消息已读回执
        userConfig.enableReadReceipt(true);

        //将用户配置与通讯管理器进行绑定
        TIMManager.getInstance().setUserConfig(userConfig);
    }

    private void showForceOffLineDialog() {
        final CustomDialog dialog = new CustomDialog(ActivityManager.getInstance().getFirst(),
                ScreenUtils.getScreenWidth(mContext) * 4 / 5,
                ScreenUtils.getScreenHeight(mContext) / 5,
                com.luck.picture.lib.R.layout.dialog_force_offline, com.luck.picture.lib.R.style.Theme_dialog);
        Button btn_commit = dialog.findViewById(com.luck.picture.lib.R.id.btn_ok);
        TextView tv_title = dialog.findViewById(com.luck.picture.lib.R.id.tv_title);
        TextView tv_content = dialog.findViewById(com.luck.picture.lib.R.id.tv_content);
        tv_title.setText(getString(com.luck.picture.lib.R.string.picture_prompt));
        tv_content.setText(getString(com.luck.picture.lib.R.string.tip_force_offline));
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myApp.this, MainActivity.class);
                intent.putExtra("sendUrl", "open.html");
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void initUmeng() {

        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(getApplicationContext(),UMKEY,"OnlineApp"));

        final PushAgent mPushAgent = PushAgent.getInstance(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //注册推送服务，每次调用register方法都会回调该接口
                mPushAgent.register(new IUmengRegisterCallback() {

                    @Override
                    public void onSuccess(String deviceToken) {
                        //注册成功会返回device token
                        Log.e("deviceToken", deviceToken);
                        if(deviceToken!=null) {
                            SharedPreferences sp = getApplicationContext().getSharedPreferences(saveKey, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("deviceToken", deviceToken);
                            editor.apply();
                        }
                    }

                    @Override
                    public void onFailure(String s, String s1) {

                    }
                });
            }
        }).start();
    }

    {
        //设置Log开关
        UMConfigure.setLogEnabled(true);
        PlatformConfig.setWeixin(WXID, WXSECRET);
        PlatformConfig.setQQZone(QQID, QQSECRET);
    }

    static public boolean checkUpdate = false;

    private void initQiyu() {
        //----qiyu---
        Unicorn.init(this, QYSECRET, options(), new UnicornImageLoader(){
            @Nullable
            @Override
            public Bitmap loadImageSync(String uri, int width, int height) {
                return null;
            }
            @Override
            public void loadImage(String uri, int width, int height, final ImageLoaderListener listener) {
                if(!TextUtils.isEmpty(uri)){}
                    Glide.with(getApplicationContext()).asBitmap().load(uri).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            listener.onLoadComplete(resource);
                        }
                    });
            }
        });

        Unicorn.addUnreadCountChangeListener(new UnreadCountChangeListener() {
            @Override
            public void onUnreadCountChange(int count) {
                BaseActivity a = ActivityManager.getInstance().getFirst();
                if(a!=null) {
                    a.getWebView().loadUrl("javascript:qiyuMsgCount&&qiyuMsgCount("+count+")");
                }
            }
        }, true);
    }

    //----qiyu---如果返回值为null，则全部使用默认参数。
    public static YSFOptions options() {
        YSFOptions options = new YSFOptions();
        options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        options.statusBarNotificationConfig.notificationEntrance = MainActivity.class;
        options.savePowerConfig = new SavePowerConfig();
        options.uiCustomization = new UICustomization();
        options.uiCustomization.leftAvatar = "";
        return options;
    }


    class StatisticActivityLifecycleCallback implements ActivityLifecycleCallbacks{
        private int foregroundActivities = 0;
        private boolean isChangingConfiguration;
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            if (bundle!= null) { // 若bundle不为空则程序异常结束
                // 重启整个程序
                Intent intent = new Intent(activity, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {
            foregroundActivities++;
            if (foregroundActivities == 1 && !isChangingConfiguration) {
                // 应用切到前台
                TIMManager.getInstance().doForeground(new TIMCallBack() {
                    @Override
                    public void onError(int code, String desc) {
                        //DemoLog.e(TAG, "doForeground err = " + code + ", desc = " + desc);
                    }

                    @Override
                    public void onSuccess() {
                        //DemoLog.i(TAG, "doForeground success");
                    }
                });
            }
            isChangingConfiguration = false;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            foregroundActivities--;
            if (foregroundActivities == 0) {
                // 应用切到后台
                //DemoLog.i(TAG, "application enter background");
                int unReadCount = 0;
                List<TIMConversation> conversationList = TIMManager.getInstance().getConversationList();
                for (TIMConversation timConversation : conversationList) {
                    unReadCount += timConversation.getUnreadMessageNum();
                }
                TIMBackgroundParam param = new TIMBackgroundParam();
                param.setC2cUnread(unReadCount);
                TIMManager.getInstance().doBackground(param, new TIMCallBack() {
                    @Override
                    public void onError(int code, String desc) {
                       // DemoLog.e(TAG, "doBackground err = " + code + ", desc = " + desc);
                    }

                    @Override
                    public void onSuccess() {
                        //DemoLog.i(TAG, "doBackground success");
                    }
                });
            }
            isChangingConfiguration = activity.isChangingConfigurations();

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }




}
