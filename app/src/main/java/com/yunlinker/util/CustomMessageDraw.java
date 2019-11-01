package com.yunlinker.util;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.ICustomMessageViewGroup;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.holder.IOnCustomMessageDrawListener;
import com.tencent.qcloud.tim.uikit.modules.message.MessageInfo;
import com.yunlinker.xbb.MainActivity;
import com.yunlinker.xbb.R;

import org.xutils.x;

public class CustomMessageDraw implements IOnCustomMessageDrawListener {
    private Context context;

    public CustomMessageDraw (Context context){
        this.context = context;
    }

    @Override
    public void onDraw(ICustomMessageViewGroup parent, MessageInfo messageInfo) {
        View view = null;
        // 获取到自定义消息的 JSON 数据
        TIMCustomElem elem = (TIMCustomElem) messageInfo.getTIMMessage().getElement(0);
        final CustomMessageData customMessageData = new Gson().fromJson(new String(elem.getData()), CustomMessageData.class);
        // 通过类型来创建不同的自定义消息展示 View
        view = LayoutInflater.from(context).inflate(R.layout.test_custom_message_layout, null, false);
        // 把自定义消息 View 添加到 TUIKit 内部的父容器里
        parent.addMessageContentView(view);

        ImageView img  = view.findViewById(R.id.im_img);
        TextView title = view.findViewById(R.id.im_title);
        TextView money = view.findViewById(R.id.im_money);
        //im_relative
        RelativeLayout im_relative = view.findViewById(R.id.im_relative);

        x.image().bind(img,customMessageData.skuImg);
        title.setText(customMessageData.skuName);
        money.setText(customMessageData.sellPrice);

        im_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("sendUrl","needdetail.html?orderDetailId="+customMessageData.orderDetailId+"&t=1");
                context.startActivity(intent);
            }
        });




    }

    /**
     * 自定义消息的 bean 实体，用来与 JSON 的相互转化
     */
    public static class CustomMessageData {
        String skuName;
        String skuImg;
        String sellPrice;
        String orderDetailId;

        public String getSkuName() {
            return skuName;
        }

        public void setSkuName(String skuName) {
            this.skuName = skuName;
        }

        public String getSkuImg() {
            return skuImg;
        }

        public void setSkuImg(String skuImg) {
            this.skuImg = skuImg;
        }

        public String getSellPrice() {
            return sellPrice;
        }

        public void setSellPrice(String sellPrice) {
            this.sellPrice = sellPrice;
        }

        public String getOrderDetailId() {
            return orderDetailId;
        }

        public void setOrderDetailId(String orderDetailId) {
            this.orderDetailId = orderDetailId;
        }

        @Override
        public String toString() {
            return "CustomMessageData{" +
                    "skuName='" + skuName + '\'' +
                    ", skuImg='" + skuImg + '\'' +
                    ", sellPrice='" + sellPrice + '\'' +
                    ", orderDetailId='" + orderDetailId + '\'' +
                    '}';
        }
    }

}

