package com.jamin.rx;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;


import com.jamin.rxjavademo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangjieming on 2017/8/24.
 */

public class FaceDetectRx {


    //次数
    private int costTimes = 0;
    //人脸识别开始时间
    private long faceDetectStartTime;
    //人脸识别原始图片尺寸
    private int mImageWidth;
    private int mImageHeight;


    public Observable<FaceDetectLandMark> faceDetect(final Resources res) {
        return Observable
                .create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        e.onNext(R.mipmap.ic_launcher);
                        Log.d("FaceDetectRx", "Emit data at Thread-->" + Thread.currentThread().getId());
                    }
                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                //ResId ---> Bitmap
                .map(new Function<Integer, Bitmap>() {
                    @Override
                    public Bitmap apply(Integer integer) throws Exception {
                        Bitmap bitmap = BitmapFactory.decodeResource(res, integer);
                        mImageWidth = bitmap.getWidth();
                        mImageHeight = bitmap.getHeight();
                        Log.d("FaceDetectRx", "Decode resId to bitmap" + Thread.currentThread().getId());
                        return bitmap;
                    }
                })
                //Bitmap转灰阶图
                .map(new Function<Bitmap, byte[]>() {
                    @Override
                    public byte[] apply(Bitmap bitmap) throws Exception {
                        byte[] grayScaleImageByteArray = getNV12FromBitmap(mImageWidth, mImageHeight, bitmap);
                        Log.d("FaceDetectRx", "Decode bitmap to Gray Scale Image. ARGB --> YUV at Thread-->" + Thread.currentThread().getId());
                        return grayScaleImageByteArray;
                    }
                })
                //记录起始时间
                .doOnNext(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) throws Exception {
                        faceDetectStartTime = System.currentTimeMillis();
                    }
                })
                //模拟Bitmap转换为FaceDetectLandMark的过程,需要多次尝试.
                .flatMap(new Function<byte[], ObservableSource<FaceDetectLandMark>>() {
                    @Override
                    public ObservableSource<FaceDetectLandMark> apply(byte[] grayScaleImageByteArray) throws Exception {
                        return getFaceMark(grayScaleImageByteArray);
                    }
                })
                //如果flatMap抛出Error,chain下面的代码不会执行.所以需要onErrorResumeNext来保证能统计到flatMap中任务的耗时
                .doOnNext(new Consumer<FaceDetectLandMark>() {
                    @Override
                    public void accept(FaceDetectLandMark bitmap) throws Exception {
                        long endTime = System.currentTimeMillis();
                        Log.d("FaceDetectRx", " costTime = " + (endTime - faceDetectStartTime) + "ms");
                        Log.d("FaceDetectRx", " times = " + costTimes + " times ");
                    }
                })
                //即使错误也要继续!!!
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends FaceDetectLandMark>>() {

                    @Override
                    public ObservableSource<? extends FaceDetectLandMark> apply(Throwable throwable) throws Exception {
                        long endTime = System.currentTimeMillis();
                        Log.d("FaceDetectRx", "onErrorResumeNext-->" + throwable.getMessage());
                        Log.d("FaceDetectRx", "No Face Detect" + " costTime = " + (endTime - faceDetectStartTime) + "ms" + ",times = " + costTimes + " times");
                        throw Exceptions.propagate(new Throwable("No Face Detect"));
//                        throw Exceptions.propagate(throwable);
                    }
                });
    }


    /**
     * chain 2
     * 使用人脸识别框架将byte array 转换成 人脸坐标系
     *
     * @param grayScaleImageByteArray
     * @return
     */
    private Observable<FaceDetectLandMark> getFaceMark(byte[] grayScaleImageByteArray) {
        return Observable
                .just(grayScaleImageByteArray)
                .map(new Function<byte[], FaceDetectLandMark>() {

                    @Override
                    public FaceDetectLandMark apply(byte[] grayScaleImageByteArray) throws Exception {
                        Log.d("FaceDetectRx", "Face Detect at Thread-->" + Thread.currentThread().getId() + " times:" + costTimes);
                        costTimes++;
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (costTimes < 50) {
                            throw Exceptions.propagate(new Throwable("retry"));
                        }
                        return new FaceDetectLandMark();
                    }
                })
                .retry(40);
    }


    public class FaceDetectLandMark {


    }


    /**
     * 转灰度
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    private byte[] getNV12FromBitmap(int inputWidth, int inputHeight, Bitmap scaled) {
        // Reference (Variation) : https://gist.github.com/wobbals/5725412

        int[] argb = new int[inputWidth * inputHeight];

        //Log.i(TAG, "scaled : " + scaled);
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

//        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        byte[] yuv = new byte[inputWidth * inputHeight];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        //scaled.recycle();

        return yuv;
    }


    /**
     * @param yuv420sp
     * @param argb
     * @param width
     * @param height
     */
    private void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

//                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
//                V = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128; // Previously U
//                U = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128; // Previously V

                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
//                if (j % 2 == 0 && index % 2 == 0) {
//                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
//                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
//                }

                index++;
            }
        }
    }

}
