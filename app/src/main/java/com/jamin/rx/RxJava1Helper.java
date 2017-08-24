package com.jamin.rx;



/**
 * Created by wangjieming on 2017/8/24.
 * 队列中最多保存15个任务.如果是快速生产者,慢速消费者.
 * 一定要考虑背压问题.否则会抛出MissingBackpressureException
 */

public class RxJava1Helper {

    private static boolean emit = true;

    /**
     * Drop策略举例
     */
    public static void backPressureTestStategy() {
//        Observable.interval(1, TimeUnit.MILLISECONDS)
////        Observable
////                .create(new Observable.OnSubscribe<Long>() {
////                    @Override
////                    public void call(Subscriber<? super Long> subscriber) {
////                        long i = 0;
////                        while (emit) {
////                            subscriber.onNext(i++);
//////                            try {
//////                                Thread.sleep(10);
//////                            } catch (InterruptedException e) {
//////                                e.printStackTrace();
//////                            }
////                            Log.d("RxJavaHelper", ">>>>>>Observable Emit Thread ID = " + Thread.currentThread().getId() + " , long = " + i);
////                        }
////                    }
////                })
////                .onBackpressureDrop()
////                .throttleFirst(200, TimeUnit.MILLISECONDS)
//                .onBackpressureBuffer(Long.MAX_VALUE)
//                .subscribeOn(Schedulers.io())
//                //将观察者的工作放在新线程环境中
//                .observeOn(Schedulers.io())
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        Log.d("RxJavaHelper", "Observable onNext Thread ID = " + Thread.currentThread().getId() + " , long = " + aLong);
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });


    }


    public static void backPressureTestObservable() {
//        Log.d("RxJavaHelper", "backPressureTestObservable Thread ID = " + Thread.currentThread().getId());
////        Observable.interval(1, TimeUnit.SECONDS)
//        Observable
//                .create(new Observable.OnSubscribe<Long>() {
//                    @Override
//                    public void call(Subscriber<? super Long> subscriber) {
//                        long i = 0;
//                        while (emit) {
//                            subscriber.onNext(i++);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            Log.d("RxJavaHelper", ">>>>>>Observable Emit Thread ID = " + Thread.currentThread().getId() + " , long = " + i);
//                        }
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                //将观察者的工作放在新线程环境中
//                .observeOn(Schedulers.io())
//                //观察者处理每1000ms才处理一个事件
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //MissingBackpressureException
//                        e.printStackTrace();
//                        emit = false;
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//
//                        Log.d("RxJavaHelper", "Observable Thread ID = " + Thread.currentThread().getId() + " , long = " + aLong);
//
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

    }


}
