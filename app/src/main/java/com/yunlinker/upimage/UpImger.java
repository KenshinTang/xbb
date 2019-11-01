package com.yunlinker.upimage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.yunlinker.baseclass.BaseTools;
import com.yunlinker.baseclass.BaseWebView;
import com.yunlinker.upimage.aliupload.OssService;
import com.yunlinker.upimage.aliupload.STSGetter;
import com.yunlinker.xbb.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

import static com.yunlinker.config.WebConfig.SELECTED_IMAGE_CODE;
import static com.yunlinker.config.WebConfig.bucket;
import static com.yunlinker.config.WebConfig.endpoint;

/**
 * Created by lemon on 2017/8/21.
 */

public class UpImger {




    private static UpImger instance = null;
    public static UpImger getInstance() {
        synchronized (UpImger.class) {
            if (instance == null)
                instance = new UpImger();
        }
        return instance;
    }

    //-----------------------------------上传图片-----------------------------------
    private Activity ma;
    private BaseWebView bw;
    private int remain;
    private OssService ossService;
    private ProgressDialog progressDialog;

    public interface UploadEvent{
        public void finished(ArrayList<String> list);
    }
    public UploadEvent uploadEvent;
    public void upimgs(JSONObject jb, Activity a, BaseWebView w) {
        ma = a;
        bw = w;
        try {
            remain = jb.getInt("remain");
            ossService = initOSS(endpoint, bucket, jb.getString("url"));
            if(remain > 0) {
                if(remain == 1) {
                    PictureSelector.create(a)
                            .openGallery(PictureMimeType.ofImage())
                            .theme(R.style.picture_Sina_style)
                            .imageSpanCount(3)
                            .selectionMode(PictureConfig.SINGLE)
                            .previewImage(false)
                            .previewVideo(false)
                            .enablePreviewAudio(false)
                            .isCamera(true)
                            .imageFormat(PictureMimeType.PNG)
                            .isZoomAnim(true)
                            .sizeMultiplier(0.5f)
                            .enableCrop(true)
                            .compress(false)
                            .isGif(true)
                            .circleDimmedLayer(false)
                            .showCropFrame(true)
                            .showCropGrid(true)
                            .previewEggs(true)
                            .rotateEnabled(true)
                            .scaleEnabled(true)
                            .isDragFrame(false)
                            .freeStyleCropEnabled(false)
                            .hideBottomControls(true)
                            .withAspectRatio(9,9)
                            .forResult(SELECTED_IMAGE_CODE);
                } else {
                    PictureSelector.create(a)
                            .openGallery(PictureMimeType.ofImage())
                            .theme(R.style.picture_QQ_style)
                            .maxSelectNum(remain)
                            .minSelectNum(1)
                            .imageSpanCount(3)
                            .selectionMode(PictureConfig.MULTIPLE)
                            .previewImage(true)
                            .previewVideo(false)
                            .isCamera(true)
                            .isZoomAnim(true)
                            .enableCrop(false)// 是否裁剪 true or false
                            .compress(false)// 是否压缩 true or false
                            .isGif(true)
                            .glideOverride(200,200)
                            .freeStyleCropEnabled(false)// 裁剪框是否可拖拽 true or false
                            .circleDimmedLayer(false)// 是否圆形裁剪 true or false
                            .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                            .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                            .openClickSound(false)// 是否开启点击声音 true or false
                            .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                            .rotateEnabled(false) // 裁剪是否可旋转图片 true or false
                            .scaleEnabled(false)// 裁剪是否可放大缩小图片 true or false
                            .isDragFrame(false)// 是否可拖动裁剪框(固定)
                            .forResult(SELECTED_IMAGE_CODE);//结果回调onActivityResult code
                }

            } else {
                Toast.makeText(ma, "当前图片已达最大值",Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private OssService initOSS( String endpoint, String bucket, String authUrl) {
        OSSCredentialProvider credentialProvider;
        credentialProvider = new STSGetter(authUrl);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(ma, endpoint, credentialProvider, conf);
        return new OssService(oss, bucket);
    }


    private ArrayList compressedList;
    private List<LocalMedia> compressItems;

    public void initUploadImageItems(List<LocalMedia> items) {
        compressItems = items;
        compressedList = new ArrayList();
        if(items.size()>0){
            progressDialog = new ProgressDialog(ma);
            progressDialog.setMessage("图片正在上传中...");
            progressDialog.show();
        }
        uploadEvent = new UploadEvent() {
            @Override
            public void finished(ArrayList<String> list) {
                progressDialog.dismiss();
                if(list.size()>0) {
                    String finalUrls = "";
                    for(String tmp:list){
                        finalUrls += (tmp + ",");
                    }
                    bw.setValue("upimg",finalUrls.substring(0,finalUrls.length()-1));
                    Toast.makeText(ma, "上传图片完成",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ma, "上传图片失败",Toast.LENGTH_SHORT).show();
                }

            }
        };

        compressImg();
    }


    public void compressImg() {
        if(compressItems.size()>0) {
            LocalMedia item = compressItems.get(0);
            compressItems.remove(0);
            if (item.isCut()){
                final File file = new File(item.getCutPath());
                Luban.get(ma).load(file).setMaxSize(500).setMaxHeight(1080).setMaxWidth(720)
                        .putGear(Luban.CUSTOM_GEAR)
                        .launch(new OnCompressListener() {
                            @Override
                            public void onStart() {}
                            @Override
                            public void onSuccess(File file) {
                                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                                String str = new SimpleDateFormat("yyyy_MM").format(curDate);
                                String random = BaseTools.createRandom(false,5);

                                Map<String, String> fileMap = new HashMap<>();
                                fileMap.put("name","wlj/"+str+"/a"+random+file.getName());
                                fileMap.put("path",file.getAbsolutePath());
                                compressedList.add(fileMap);

                                compressImg();
                            }
                            @Override
                            public void onError(Throwable e) {
                                compressImg();
                            }
                        });
            }else {
                final File file = new File(item.getPath());
                Luban.get(ma).load(file).setMaxSize(500).setMaxHeight(1080).setMaxWidth(720)
                        .putGear(Luban.CUSTOM_GEAR)
                        .launch(new OnCompressListener() {
                            @Override
                            public void onStart() {}
                            @Override
                            public void onSuccess(File file) {
                                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                                String str = new SimpleDateFormat("yyyy_MM").format(curDate);
                                String random = BaseTools.createRandom(false,5);

                                Map<String, String> fileMap = new HashMap<>();
                                fileMap.put("name","wlj/"+str+"/a"+random+file.getName());
                                fileMap.put("path",file.getAbsolutePath());
                                compressedList.add(fileMap);

                                compressImg();
                            }
                            @Override
                            public void onError(Throwable e) {
                                compressImg();
                            }
                        });
            }

        } else {
            startUploadImgs();
        }
    }

    private void startUploadImgs() {
        if(compressedList.size()>0) {
            ossService.configUploadList(compressedList);
            ossService.uploadImgs();
        } else {
            if(progressDialog!=null)
                progressDialog.dismiss();
        }
    }

}
