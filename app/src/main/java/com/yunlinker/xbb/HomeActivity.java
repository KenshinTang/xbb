package com.yunlinker.xbb;

/**
 * Created by lemon on 2017/10/8.
 */

public class HomeActivity extends MainActivity {

    @Override
    protected void addWebView() {
        super.addWebView();
        mwebView.setAlpha(0);
    }
}
