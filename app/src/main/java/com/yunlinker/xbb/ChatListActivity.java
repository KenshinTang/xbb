package com.yunlinker.xbb;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListAdapter;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationProvider;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationIconView;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.yunlinker.model.MessageListModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.yunlinker.config.WebConfig.Messagelist;

public class ChatListActivity extends AppCompatActivity {
    private MessageListModel messageListModel;
    private  ImageView chat_back;
    private  TextView chat_setting,xitong_text,xitong_mess,messred;
    private  RelativeLayout relative;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //完成主界面更新,拿到数据
                    Log.e("1111", "handleMessage: "+msg.obj);
                    String messtype = null;
                    String contentmess = null;
                    String isread = null;
                    List<String> list = (List<String>)msg.obj;
                    if (list!=null){
                        for (int i = 0; i <list.size(); i++) {
                            messtype = list.get(0);
                            contentmess = list.get(1);
                            isread = list.get(2);
                        }
                        if (messtype.equals("1")){
                            xitong_text.setText("订单消息");
                        }else {
                            xitong_text.setText("系统消息");
                        }

                        if (isread.equals("0")){
                           messred.setBackgroundResource(R.drawable.text_circle_red);
                        }
                        String  conmess = stripHtml(contentmess);
                        xitong_mess.setText(conmess);
                    }
                    break;
                case 1:
                    relative.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // 从布局文件中获取会话列表面板
        ConversationLayout conversationLayout = findViewById(R.id.conversation_layout);
        // 会话列表面板的默认 UI 和交互初始化
        conversationLayout.initDefault();
        TitleBarLayout titleBarLayout = conversationLayout.findViewById(R.id.conversation_title);
        // 隐藏标题
        titleBarLayout.setVisibility(View.GONE);
        customizeConversation(conversationLayout);
        chat_back = (ImageView)findViewById(R.id.chat_list_back);
        chat_setting = (TextView)findViewById(R.id.chat_setting);
        relative = (RelativeLayout)findViewById(R.id.xitong);
        xitong_text = (TextView)findViewById(R.id.xitong_text);
        xitong_mess = (TextView)findViewById(R.id.xitong_mess);
        messred = (TextView)findViewById(R.id.messred);
        initview();
        messagelist();

       //TIMManager.getInstance().getConversationList();
    }

    private void messagelist() {
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Messagelist)
                .get()//默认就是GET请求，可以不写
                .addHeader("x-Agent",getIntent().getStringExtra("token"))
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("onFailure", "onFailure: "+"请求失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String responseData=response.body().string();
                    Gson gson=new Gson();
                    messageListModel=gson.fromJson(responseData, MessageListModel.class);
                    if (messageListModel.getData()!=null){
                        if (messageListModel.getData().size()!=0){
                            messageData(messageListModel);
                        }else {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //耗时操作，完成之后发送消息给Handler，完成UI更新；
                                    mHandler.sendEmptyMessage(1);
                                }
                            }).start();
                        }
                    }
                }
            }
        });
    }

    private void messageData( final MessageListModel messageListModel) {
        if (messageListModel!=null){
            for (int i = 0; i < messageListModel.getData().size(); i++) {

                final List<String> list = new ArrayList<>();
                list.add(messageListModel.getData().get(0).getMessType());
                list.add(messageListModel.getData().get(0).getContent());
                list.add(messageListModel.getData().get(0).getIsread());
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //耗时操作，完成之后发送消息给Handler，完成UI更新；
                        mHandler.sendEmptyMessage(0);
                        //需要数据传递，用下面方法；
                        Message msg =new Message();
                        msg.obj = list;//可以是基本类型，可以是对象，可以是List、map等；
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        }
    }

    private void initview() {

        //返回主页
        chat_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //去设置界面
        chat_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this,MainActivity.class);
                intent.putExtra("sendUrl","msgset.html");
                startActivity(intent);
            }
        });
        //系统消息
        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatListActivity.this,MainActivity.class);
                intent.putExtra("sendUrl","msgsys.html");
                startActivity(intent);
            }
        });

    }

    public  void customizeConversation(final ConversationLayout layout) {
        // 从CoversationLayout获取会话列表
        final ConversationListLayout listLayout = layout.getConversationList();
        listLayout.setItemTopTextSize(16); // 设置 item 中 top 文字大小
        listLayout.setItemBottomTextSize(12);// 设置 item 中 bottom 文字大小
        listLayout.setItemDateTextSize(10);// 设置 item 中 timeline 文字大小
        listLayout.enableItemRoundIcon(true);// 设置 item 头像是否显示圆角，默认是方形
        listLayout.disableItemUnreadDot(false);// 设置 item 是否不显示未读红点，默认显示
        listLayout.setPadding(35,0,0,0);

        // 去聊天
        listLayout.setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i, ConversationInfo conversationInfo) {
                Log.e("11111", "onItemClick: "+conversationInfo.getId());
                Intent intent = new Intent(ChatListActivity.this,ImActivity.class);
                intent.putExtra("cid",conversationInfo.getId());
                startActivity(intent);
            }
        });
        //删除会话
        listLayout.setOnItemLongClickListener(new ConversationListLayout.OnItemLongClickListener() {
            @Override
            public void OnItemLongClick(View view,final int i,final ConversationInfo conversation) {
                MessageDialog.show(ChatListActivity.this, "提示", "是否删除会话", "确定", "取消")
                        .setButtonOrientation(LinearLayout.HORIZONTAL)
                        .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                            @Override
                            public boolean onClick(BaseDialog baseDialog, View v) {
                                layout.deleteConversation(i,conversation);
                                return false;
                            }
                        })
                        .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                            @Override
                            public boolean onClick(BaseDialog baseDialog, View v) {
                                 baseDialog.doDismiss();
                                return false;
                            }
                        });
            }
        });


    }

    public String stripHtml(String content) {
        // <p>段落替换为换行
        content = content.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        content = content.replaceAll("<br\\s*/?>", "\r\n");
        // 去掉其它的<>之间的东西
        content = content.replaceAll("\\<.*?>", "");
        // 去掉空格
         content = content.replaceAll(" ", "");
        return content;
    }
}
