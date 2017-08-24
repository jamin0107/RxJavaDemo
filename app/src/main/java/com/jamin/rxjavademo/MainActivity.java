package com.jamin.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jamin.rx.RxJava2Helper;
import com.jamin.rx.FaceDetectRx;
import com.jamin.rx.RxJava1Helper;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.jamin.rxjavademo.R.layout.activity_main);

        findViewById(com.jamin.rxjavademo.R.id.rx1_back_pressure_strategy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxJava1Helper.backPressureTestStategy();
            }
        });


        findViewById(com.jamin.rxjavademo.R.id.rx1_back_pressure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxJava1Helper.backPressureTestObservable();
            }
        });

        findViewById(com.jamin.rxjavademo.R.id.rx2_back_pressure_strategy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxJava2Helper.backPressureTestStategy();
            }
        });


        findViewById(com.jamin.rxjavademo.R.id.rx2_back_pressure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxJava2Helper.backPressureTestObservable();
            }
        });

        findViewById(com.jamin.rxjavademo.R.id.rx_map_flatmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FaceDetectRx().faceDetect(getResources())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<FaceDetectRx.FaceDetectLandMark>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d("FaceDetectRx", "onSubscribe-->");
                            }

                            @Override
                            public void onNext(FaceDetectRx.FaceDetectLandMark faceMark) {
                                Log.d("FaceDetectRx", "onNext--> GET FaceDetectLandMark");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d("FaceDetectRx", "onError-->" + e.getMessage());
                                if (e instanceof CompositeException) {
                                    List<Throwable> exceptions = ((CompositeException) e).getExceptions();
                                    for (Throwable throwable : exceptions) {
                                        Log.d("FaceDetectRx", "onError-->" + throwable.getMessage());
                                    }

                                }
                            }

                            @Override
                            public void onComplete() {
                                Log.d("FaceDetectRx", "onComplete-->");
                            }
                        });
            }
        });


    }
}
