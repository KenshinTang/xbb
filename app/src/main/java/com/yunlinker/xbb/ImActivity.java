package com.yunlinker.xbb;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.conversation.Msg;
import com.tencent.openqq.protocol.imsdk.msg;
import com.tencent.openqq.protocol.imsdk.msg_common;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.ITitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.NoticeLayout;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.component.gatherimage.UserIconView;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.inputmore.InputMoreActionUnit;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayoutUI;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;
import com.yunlinker.auth.WebPermissionManager;
import com.yunlinker.baseclass.BaseActivity;
import com.yunlinker.myApp;
import com.yunlinker.upimage.UpImger;
import com.yunlinker.util.CustomMessageDraw;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.luck.picture.lib.config.PictureConfig.CHOOSE_REQUEST;
import static com.yunlinker.auth.WebPermissionManager.UpImgPermissions;

public class ImActivity extends AppCompatActivity implements Handler.Callback{

    public static final int SELECTED_IMAGE = 111;
    public static final int openNewDetali=100;
    public static final int REQUEST_CODE=1001;
    private Handler handler;
    private ChatLayout chatLayout;
    private String nickname;

    private String path = Environment.getExternalStorageDirectory() +
            File.separator + Environment.DIRECTORY_DCIM + File.separator;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_im);

        //推送消息点击跳转的handler
        handler = new Handler(this);
        JSInspect.setMainhandler(handler);

        final Intent intent = getIntent();
        // 从布局文件中获取聊天面板
        chatLayout = findViewById(R.id.chat_layout);
        // 单聊面板的默认 UI 和交互初始化
        chatLayout.initDefault();
        // 传入 ChatInfo 的实例，这个实例必须包含必要的聊天信息，一般从调用方传入

        List<String> users = new ArrayList<String>();
        users.add(intent.getStringExtra("cid"));
        //获取用户资料
        TIMFriendshipManager.getInstance().getUsersProfile(users, true, new TIMValueCallBack<List<TIMUserProfile>>(){
            @Override
            public void onError(int code, String desc){
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.e("111", "getUsersProfile failed: " + code + " desc");
            }

            @Override
            public void onSuccess(List<TIMUserProfile> result){
                Log.e("111111", "getUsersProfile succ");
                for(TIMUserProfile res : result){
                    Log.e("111", "identifier: " + res.getIdentifier() + " nickName: " + res.getNickName());
                  if (res.getNickName()!=null&&!res.getNickName().equals("")){
                      ChatInfo chatInfo = new ChatInfo();
                      chatInfo.setId(intent.getStringExtra("cid"));
                      chatInfo.setChatName(res.getNickName());
                      chatInfo.setType(TIMConversationType.C2C);
                      chatInfo.isTopChat();
                      chatLayout.setChatInfo(chatInfo);
                  }else {
                      ChatInfo chatInfo = new ChatInfo();
                      chatInfo.setId(intent.getStringExtra("cid"));
                      chatInfo.setChatName(intent.getStringExtra("cid"));
                      chatInfo.setType(TIMConversationType.C2C);
                      chatInfo.isTopChat();
                      chatLayout.setChatInfo(chatInfo);
                  }
                }
            }
        });

        //获取服务器保存的自己的资料
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>(){
            @Override
            public void onError(int code, String desc){
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
                Log.e("获取服务器保存的自己的资料", "getSelfProfile failed: " + code + " desc");
            }

            @Override
            public void onSuccess(TIMUserProfile result){
                Log.e("获取服务器保存的自己的资料", "getSelfProfile succ");
                Log.e("获取服务器保存的自己的资料", "identifier: " + result.getIdentifier() + " nickName: " + result.getNickName()
                        + " allow: " + result.getAllowType());
            }
        });



        TitleBarLayout titleBarLayout = chatLayout.findViewById(R.id.chat_title_bar);
        titleBarLayout.getRightGroup().setVisibility(View.GONE);
        titleBarLayout.setLeftIcon(R.mipmap.back_im_it);
        //titleBarLayout.

        // 从ChatLayout 里获取 MessageLayout

         MessageLayout messageLayout = chatLayout.getMessageLayout();
        ////// 设置聊天背景 //////
        //messageLayout.setBackground(new ColorDrawable(0xB0E2FF00));
        //messageLayout.setRightBubble(this.getResources().getDrawable(R.mipmap.qipaoqq));
        // 设置朋友聊天气泡的背景
        //messageLayout.setLeftBubble(context.getResources().getDrawable(R.drawable.chat_self_bg));
        messageLayout.setOnCustomMessageDrawListener(new CustomMessageDraw(this));
        // 设置聊天内容字体大小，朋友和自己用一种字体大小
        messageLayout.setChatContextFontSize(15);
        // 设置自己聊天内容字体颜色
        messageLayout.setRightChatContentFontColor(0xA0ffffff);
        // 设置朋友聊天内容字体颜色
        messageLayout.setLeftChatContentFontColor(0xA0000000);
        //设置头像不可点击
        messageLayout.setOnItemClickListener(new MessageLayout.OnItemClickListener() {
            @Override
            public void onMessageLongClick(View view, int i, MessageInfo messageInfo) {
                view.setEnabled(false);
            }

            @Override
            public void onUserIconClick(View view, int i, MessageInfo messageInfo) {
                 view.setEnabled(false);
            }
        });
        ////// 设置头像 //////
        // 设置默认头像，默认与朋友与自己的头像相同
        messageLayout.setAvatar(R.mipmap.ic_launcher);
        // 设置头像圆角，不设置则默认不做圆角处理
        messageLayout.setAvatarRadius(50);
        // 设置头像大小
        messageLayout.setAvatarSize(new int[]{48, 48});

        // 从 ChatLayout 里获取 InputLayout
        InputLayout inputLayout = chatLayout.getInputLayout();
        // 隐藏拍照并发送
        inputLayout.disableCaptureAction(true);
         // 隐藏发送文件
        inputLayout.disableSendFileAction(true);
        // 隐藏发送图片
        inputLayout.disableSendPhotoAction(true);
        // 隐藏摄像并发送
        inputLayout.disableVideoRecordAction(true);
        //语音
        ImageView imageView = inputLayout.findViewById(R.id.voice_input_switch);
        imageView.setVisibility(View.GONE);
//        //表情
//        ImageView face = inputLayout.findViewById(R.id.face_btn);
//        face.setImageResource(R.drawable.biaoqing);
//        //加号
//        ImageView more = inputLayout.findViewById(R.id.more_btn);
//        more.setImageResource(R.drawable.jiahao);


        InputMoreActionUnit unit1 = new InputMoreActionUnit();
        unit1.setIconResId(R.drawable.xiangce); // 设置单元的图标
        unit1.setTitleId(R.string.tu_imgs); // 设置单元的文字标题
        unit1.setOnClickListener(new View.OnClickListener() { // 定义点击事件
            @Override
            public void onClick(View v) {
                PictureSelector.create(ImActivity.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .theme(R.style.picture_Sina_style)//主题样式(不设置为默认样式) 也可参考demo values/styles下 例如：R.style.picture.white.style
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(3)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .previewVideo(false)// 是否可预览视频 true or false
                        .enablePreviewAudio(false) // 是否可播放音频 true or false
                        .isCamera(false)// 是否显示拍照按钮 true or false
                        .isGif(false)
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .enableCrop(false)// 是否裁剪 true or false
                        .compress(false)// 是否压缩 true or false
                        .isGif(false)// 是否显示gif图片 true or false
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .minimumCompressSize(100)// 小于100kb的图片不压缩
                        .synOrAsy(true)//同步true或异步false 压缩 默认同步
                        .forResult(CHOOSE_REQUEST);//结果回调onActivityResult code

            }
        });
        // 把定义好的单元增加到更多面板
        inputLayout.addAction(unit1);

        InputMoreActionUnit unit2 = new InputMoreActionUnit();
        unit2.setIconResId(R.drawable.paizhao); // 设置单元的图标
        unit2.setTitleId(R.string.photo); // 设置单元的文字标题
        unit2.setOnClickListener(new View.OnClickListener() { // 定义点击事件
            @Override
            public void onClick(View v) {
                XXPermissions.with(ImActivity.this)
                        .permission(Permission.CAMERA)
                        .permission(Permission.Group.STORAGE)
                        .request(new OnPermission() {

                            @Override
                            public void hasPermission(List<String> granted, boolean isAll) {
                                if (isAll) {
                                    String state = Environment.getExternalStorageState();
                                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                                        File file = new File(path);
                                        if (!file.exists()) {
                                            file.mkdir();
                                        }
                                        String fileName = getPhotoFileName() + ".jpg";
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        photoUri = Uri.fromFile(new File(path + fileName));
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                        startActivityForResult(intent, REQUEST_CODE);
                                    }
                                    //Toast.makeText(MainActivity.this, "获取权限成功", Toast.LENGTH_SHORT).show();
                                }else {
                                    //Toast.makeText(MainActivity.this, "获取权限成功，部分权限未正常授予", Toast.LENGTH_SHORT).show();
                                    String state = Environment.getExternalStorageState();
                                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                                        File file = new File(path);
                                        if (!file.exists()) {
                                            file.mkdir();
                                        }
                                        String fileName = getPhotoFileName() + ".jpg";
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        photoUri = Uri.fromFile(new File(path + fileName));
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                        startActivityForResult(intent, REQUEST_CODE);
                                    }
                                }
                            }
                            @Override
                            public void noPermission(List<String> denied, boolean quick) {
                                if(quick) {
                                    Toast.makeText(ImActivity.this, "被永久拒绝授权，请手动授予权限", Toast.LENGTH_SHORT).show();
                                    //如果是被永久拒绝就跳转到应用权限系统设置页面
                                    XXPermissions.gotoPermissionSettings(ImActivity.this);
                                }else {
                                    Toast.makeText(ImActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        // 把定义好的单元增加到更多面板
        inputLayout.addAction(unit2);

        InputMoreActionUnit unit3 = new InputMoreActionUnit();
        unit3.setIconResId(R.drawable.xuqiu); // 设置单元的图标
        unit3.setTitleId(R.string.profile); // 设置单元的文字标题
        unit3.setOnClickListener(new View.OnClickListener() { // 定义点击事件
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ImActivity.this,MainActivity.class);
                intent1.putExtra("sendUrl","myxq.html");
                startActivity(intent1);

            }
        });
        // 把定义好的单元增加到更多面板
        inputLayout.addAction(unit3);

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case openNewDetali:
                Log.e("handleMessage", "handleMessage: "+msg.obj.toString());
                try {
                    JSONObject jb = new JSONObject(msg.obj.toString());
                    String skuImg = jb.getString("skuImg");
                    String skuName = jb.getString("skuName");
                    String sellPrice = jb.getString("sellPrice");
                    String orderDetailId = jb.getString("orderDetailId");

                    MessageLayout messageLayout = chatLayout.getMessageLayout();
                    messageLayout.setOnCustomMessageDrawListener(new CustomMessageDraw(this));

                    MessageInfo info = MessageInfoUtil.buildCustomMessage("{\"skuName\": \""+skuName+"\",\"skuImg\": \""+skuImg+"\",\"sellPrice\": \""+sellPrice+"\",\"orderDetailId\": \""+orderDetailId+"\"}");

                    chatLayout.sendMessage(info,false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_REQUEST:
                    List<LocalMedia> images = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia localMedia : images) {
                        Log.e("aaaa", "resultBack: "+localMedia.getPath());
                        Uri uri = Uri.fromFile(new File(localMedia.getPath()));
                        MessageInfo info = MessageInfoUtil.buildImageMessage(uri, false);
                        chatLayout.sendMessage(info,false);
                    }
                    break;
                case REQUEST_CODE:
                    Uri uri = null;
                    if (data != null && data.getData() != null) {
                        uri = data.getData();
                        Log.e("aaaa", "resultBack: "+ data.getData());
                    }
                    if (uri == null) {
                        if (photoUri != null) {
                            uri = photoUri;
                            Log.e("aaaa", "resultBack: "+ uri);
                        }
                    }
                    MessageInfo info = MessageInfoUtil.buildImageMessage(uri, false);
                    chatLayout.sendMessage(info,false);
                    break;
            }
        }
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }

}
