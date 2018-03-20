package com.jamin.rx;

import android.support.annotation.IntDef;
import android.util.Log;
import io.reactivex.BackpressureStrategy;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by wangjieming on 20/03/2018.
 */

public class RxBackPressureDrop {

  public static final String TAG = RxBackPressureDrop.class.getSimpleName();
  private final Subject<Integer> mBus;
  private Subscription subscription;
  private boolean isDestroy = false;
  private AtomicBoolean hasConsume = new AtomicBoolean(true);

  public RxBackPressureDrop() {
    mBus = PublishSubject.create();
    mBus.toSerialized();
  }

  public void start() {
    mBus.filter(new Predicate<Integer>() {
      @Override public boolean test(Integer integer) throws Exception {
        Log.d(RxBackPressureDrop.TAG,
            "filter = " + integer + "hasConsume.get() = " + hasConsume.get());
        return hasConsume.get();
      }
    })
        .toFlowable(BackpressureStrategy.DROP)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map(new Function<Integer, Integer>() {
          @Override public Integer apply(Integer integer) throws Exception {
            hasConsume.set(false);
            int cost = 200 + new Random().nextInt(50);
            Thread.sleep(cost);
            Log.d(RxBackPressureDrop.TAG, "Consumer = " + integer + ", cost = " + cost);
            hasConsume.set(true);
            return integer;
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
  }

  private Subscriber<Integer> subscriber = new Subscriber<Integer>() {

    @Override public void onSubscribe(Subscription s) {
      subscription = s;
      subscription.request(1);
      Log.d(RxBackPressureDrop.TAG, "onSubscribe = ");
      //s.request(Long.MAX_VALUE);
    }

    @Override public void onNext(Integer iInteger) {
      Log.d(RxBackPressureDrop.TAG, "onNext = " + iInteger);
      subscription.request(1);
    }

    @Override public void onError(Throwable t) {
      t.printStackTrace();
      Log.d(RxBackPressureDrop.TAG, "onError = ");
    }

    @Override public void onComplete() {
      Log.d(RxBackPressureDrop.TAG, "onComplete = ");
    }
  };

  public void emmit() {
    new Thread(new Runnable() {
      @Override public void run() {
        int i = 0;
        while (!isDestroy) {
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          if (mBus != null) {
            mBus.onNext(i);
            Log.d("Producer", "Producer = " + i);
          }
          i++;
        }
      }
    }).start();
  }

  public void destroy() {
    subscription.cancel();
    isDestroy = true;
  }
  //public void setMode(@SeekMode int seekMode) {
  //  isKeyFrameSeek = seekMode == MODE_KEY_FRAME;
  //}

  public static final int MODE_KEY_FRAME = 0;
  public static final int MODE_NORMAL = 1;

  @IntDef({ MODE_KEY_FRAME, MODE_NORMAL }) @Retention(RetentionPolicy.SOURCE)
  @Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD }) @interface SeekMode {

  }
}
