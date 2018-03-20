package com.jamin.rxjavademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.jamin.rx.FaceDetectRx;
import com.jamin.rx.RxBackPressureDrop;
import com.jamin.rx.RxJava1Helper;
import com.jamin.rx.RxJava2Helper;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

  RxBackPressureDrop rxQuickProducerAndSlowConsumer = new RxBackPressureDrop();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);

    findViewById(R.id.rx2_producer_consumer_start).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        rxQuickProducerAndSlowConsumer.start();
        rxQuickProducerAndSlowConsumer.emmit();
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    rxQuickProducerAndSlowConsumer.destroy();
    rxQuickProducerAndSlowConsumer = null;
  }
}
