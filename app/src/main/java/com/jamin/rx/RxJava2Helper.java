package com.jamin.rx;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by wangjieming on 2017/8/24.
 */

public class RxJava2Helper {


    public static void backPressureTestStategy() {
        //上游
//        Flowable<Long> flowable = Flowable.create(new FlowableOnSubscribe<Long>() {
//            @Override
//            public void subscribe(FlowableEmitter<Long> emitter) throws Exception {
//                long i = 0;
//                while (true) {
//                    emitter.onNext(i++);
////                    Thread.sleep(1);
//                }
//            }
//        }, BackpressureStrategy.BUFFER)
        Flowable<Long> flowable = Flowable.interval(1, TimeUnit.MICROSECONDS)
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread());


        //下游
        Subscriber<Long> subscriber = new Subscriber<Long>() {

            Subscription s;

            @Override
            public void onSubscribe(Subscription s) {
                Log.d("RxJavaHelper", "onSubscribe");
                this.s = s;
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Long aLong) {
                Log.d("RxJavaHelper", "Flowable Thread ID = " + Thread.currentThread().getId() + " , long = " + aLong);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                s.request(1);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        flowable.subscribe(subscriber);
    }


    public static void backPressureTestObservable() {
        Observable.interval(1, TimeUnit.MILLISECONDS)
//        Observable.create(new ObservableOnSubscribe<Long>() {
//            @Override
//            public void subscribe(ObservableEmitter<Long> e) throws Exception {
//                e.onNext(1l);
//            }
//        })
                .subscribeOn(Schedulers.newThread())
                //将观察者的工作放在新线程环境中
                .observeOn(Schedulers.newThread())
                //观察者处理每1000ms才处理一个事件
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onNext(Long aLong) {
                        Log.d("RxJavaHelper", "Observable Thread ID = " + Thread.currentThread().getId() + " , long = " + aLong);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


}
