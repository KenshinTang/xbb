package com.yunlinker.model;

import java.util.List;

public class MessageListModel {

    /**
     * code : 1
     * curPage : 1
     * data : [{"sendCustomerId":"1","itemId":"0","itemType":"0","addTime":"2019-09-05 09:17:05",
     * "messTitle":"提现失败","messId":"335","isread":"0","sendType":"0","customerId":"5",
     * "userType":"1","messType":"2","content":"您好！您的提现申请已通过申请，但由于其他原因提现失败"},{"sendCustomerId":"5
     * ","itemId":"96","itemType":"1","addTime":"2019-09-03 17:13:21","messTitle":"提醒发货",
     * "messId":"298","isread":"0","sendType":"1","customerId":"5","userType":"1","messType":"1",
     * "content":"买家您好：您的订单已发布，等待背包客接单。"},{"sendCustomerId":"1","itemId":"78","itemType":"5",
     * "addTime":"2019-09-02 14:50:50","messTitle":"背包客","messId":"232","isread":"0",
     * "sendType":"0","customerId":"5","userType":"2","messType":"2","content":" <p>背包客消息<\/p>"},
     * {"sendCustomerId":"5","itemId":"81","itemType":"1","addTime":"2019-09-02 14:32:18",
     * "messTitle":"提醒发货","messId":"228","isread":"0","sendType":"1","customerId":"5",
     * "userType":"1","messType":"1","content":"买家您好：您的订单已发布，等待背包客接单。"},{"sendCustomerId":"1",
     * "itemId":"65","itemType":"3","addTime":"2019-09-02 14:29:31","messTitle":"售后申请通知",
     * "messId":"222","isread":"0","sendType":"0","customerId":"5","userType":"1","messType":"2",
     * "content":"您好！您的售后申请由于\"其他\"的原因未通过审核！"},{"sendCustomerId":"5","itemId":"65",
     * "itemType":"1","addTime":"2019-08-29 15:57:35","messTitle":"售后消息","messId":"181",
     * "isread":"0","sendType":"1","customerId":"5","userType":"2","messType":"1",
     * "content":"买家您好：您的订单已经申请售后。"},{"sendCustomerId":"6","itemId":"65","itemType":"1",
     * "addTime":"2019-08-29 15:52:20","messTitle":"发货信息","messId":"179","isread":"0",
     * "sendType":"1","customerId":"5","userType":"1","messType":"1","content":"买家您好：您的订单已发货。"},{
     * "sendCustomerId":"6","itemId":"65","itemType":"1","addTime":"2019-08-29 15:48:35",
     * "messTitle":"采购消息","messId":"178","isread":"0","sendType":"1","customerId":"5",
     * "userType":"1","messType":"1","content":"买家您好：您订单的货物已被采购。"},{"sendCustomerId":"6",
     * "itemId":"65","itemType":"1","addTime":"2019-08-29 15:48:11","messTitle":"接单消息",
     * "messId":"177","isread":"0","sendType":"1","customerId":"5","userType":"1","messType":"1",
     * "content":"买家您好：您订单已经被接单。"},{"sendCustomerId":"1","itemId":"77","itemType":"5",
     * "addTime":"2019-08-29 15:45:05","messTitle":"普通会员","messId":"164","isread":"0",
     * "sendType":"0","customerId":"5","userType":"1","messType":"2","content":" <p>用户消息<\/p>"}]
     * pageSize : 10
     * totalCount : 55
     */

    private int code;
    private int curPage;
    private int pageSize;
    private int totalCount;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * sendCustomerId : 1
         * itemId : 0
         * itemType : 0
         * addTime : 2019-09-05 09:17:05
         * messTitle : 提现失败
         * messId : 335
         * isread : 0
         * sendType : 0
         * customerId : 5
         * userType : 1
         * messType : 2
         * content : 您好！您的提现申请已通过申请，但由于其他原因提现失败
         */

        private String sendCustomerId;
        private String itemId;
        private String itemType;
        private String addTime;
        private String messTitle;
        private String messId;
        private String isread;
        private String sendType;
        private String customerId;
        private String userType;
        private String messType;
        private String content;

        public String getSendCustomerId() {
            return sendCustomerId;
        }

        public void setSendCustomerId(String sendCustomerId) {
            this.sendCustomerId = sendCustomerId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public String getMessTitle() {
            return messTitle;
        }

        public void setMessTitle(String messTitle) {
            this.messTitle = messTitle;
        }

        public String getMessId() {
            return messId;
        }

        public void setMessId(String messId) {
            this.messId = messId;
        }

        public String getIsread() {
            return isread;
        }

        public void setIsread(String isread) {
            this.isread = isread;
        }

        public String getSendType() {
            return sendType;
        }

        public void setSendType(String sendType) {
            this.sendType = sendType;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getMessType() {
            return messType;
        }

        public void setMessType(String messType) {
            this.messType = messType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
