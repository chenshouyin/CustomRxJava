package csy.com.customrxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.currentThread;

/**
 * 参照
 * 作者原文
 * https://github.com/ReactiveX/RxJava
 * https://github.com/ReactiveX/RxAndroid
 * 给 Android 开发者的 RxJava 详解  https://gank.io/post/560e15be2dca930e00da1083
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String tag = MainActivity.class.getSimpleName();
    private Button tvRxJava, tvRxAndroid, tvNewThread, tvRxJavaCreat;
    private Button tvLifeCircle1, tvLifeCircle2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvRxJava = (Button) findViewById(R.id.tvRxJava);
        tvRxAndroid = (Button) findViewById(R.id.tvRxAndroid);
        tvRxJava.setOnClickListener(this);
        tvRxAndroid.setOnClickListener(this);
        tvNewThread = (Button) findViewById(R.id.tvNewThread);
        tvNewThread.setOnClickListener(this);
        tvRxJavaCreat = (Button) findViewById(R.id.tvRxJavaCreat);
        tvRxJavaCreat.setOnClickListener(this);

        tvLifeCircle1 = (Button) findViewById(R.id.tvLifeCircle1);
        tvLifeCircle1.setOnClickListener(this);
        tvLifeCircle2 = (Button) findViewById(R.id.tvLifeCircle2);
        tvLifeCircle2.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v == tvRxJava) {
            //此处用的Observable.fromArray方式创建,当然也可参照下面的RxAndroid中的Observable.create和Observable.just
            //没有本质区别,RxJava和RxAndroid也没有本质区别,RxJava和RxAndroid
            //Rxjava和RxAndroid的区别
            //RxAndroid is part of the RxJava family the communication channels are similar
            //RxAndroid是RxJava的扩展,只使用RxAndroid的依赖也没问题
            //但是官方建议:
            //Because RxAndroid releases are few and far between, it is recommended you also
            //explicitly depend on RxJava's latest version for bug fixes and new features.
            String arr[] = new String[]{"CC", "CCSS", "CCSSYY"};
            Observable.fromArray(arr)
                    .subscribeOn(Schedulers.computation())//指定 subscribe() 发生在 新 线程
                    .observeOn(Schedulers.newThread())//不指定observeOn则默认哪个线程调用就哪个线程消费
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            System.out.println("=======onSubscribe===" + currentThread().getId());
                        }

                        @Override
                        public void onNext(String value) {
                            System.out.println("=======onNext===" + currentThread().getId() + "==value==" + value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println("=======onError===" + e);
                        }

                        @Override
                        public void onComplete() {
                            System.out.println("=======onComplete===" + currentThread().getId());
                        }
                    });
        } else if (v == tvRxAndroid) {
            //也可以使用Observable.fromArray()
            Observable.just("str1", "str2", "str3", "str4")
                    .doOnSubscribe(new Consumer<Disposable>() {
                        @Override
                        public void accept(Disposable disposable) throws Exception {//subscribeOn指定的线程运行
                            System.out.println("=======accept===" + currentThread().getId());
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            System.out.println("=======onSubscribe===" + currentThread().getId());

                        }

                        @Override
                        public void onNext(String value) {
                            System.out.println("=======onNext===" + currentThread().getId());

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else if (v == tvNewThread) {

            /**
             *  start（）:通过该方法启动线程的同时也创建了一个线程，真正实现了多线程，
             *  这是无需等待run（）方法中的代码执行完毕就可以直接执行下面的代码，通过
             *  start创建的线程处于可运行状态，当得到CPU时间片后就会执行其中的run方法，
             *  这里方法run（）称为线程体，它包含了要执行的这个线程的内容，Run方法运行结束，
             *  此线程随即终止。
             */
            new Thread(new Runnable() {
                @Override
                public void run() {//Thread-5878
                    System.out.println("====本线程是通过start方式开启的,会单独开启一个线程==1===" + currentThread().getName());
                }
            }).start();

            /**
             *  run（）：在当前线程开启,比如当前线程是主线程,那么运行在主线程,如果当前线程是子线程，那么在子线程运行
             */
            new Thread(new Runnable() {//Thread-main
                @Override
                public void run() {
                    System.out.println("====本线程是通过在主线程中run方式开启的,并没有创建一个线程==2===" + currentThread().getName());
                }
            }).run();//run开启是在当前线程


            new Thread(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {//run是在当前线程运行也就是外层的子线程   Thread-5880
                            System.out.println("====本线程是通过在子线程中通过run方式开启的,并没有创建一个线程==3===" + currentThread().getName());
                        }
                    }).run();//run开启是在当前线程
                }
            }).start();


        } else if (v == tvRxJavaCreat) {
            //也可用  Observable.just("one", "two", "three", "four", "five")
            Observable.create(new ObservableOnSubscribe<String>() {

                @Override
                public void subscribe(ObservableEmitter<String> subscribe) throws Exception {
                    //在subscribeOn指定的Thread做一些复杂操作
                    System.out.println("=====create==subscribe===" + currentThread().getName() + "优先级==" + currentThread().getPriority());
                    int i = 0;
                    while (i < 10000000) {
                        i++;
                        i++;
                        i--;
                    }

                    //String ss = null;//此处故意创造异常,那么会自动执行 onError 回调抛出异常,不会继续往1下面走
                    //ss.equals("");

                    subscribe.onNext("" + i);//触发回调
                    subscribe.onNext("" + i);//触发回调
                    subscribe.onNext("" + i);//触发回调
                    subscribe.onNext("" + i);//触发回调

                    subscribe.onComplete();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {//下面的回调都是在observeOn指定的Thread操作
                        @Override
                        public void onSubscribe(Disposable d) {
                            System.out.println("=======onSubscribe===" + currentThread().getName() + "优先级==" + currentThread().getPriority());
                        }

                        @Override
                        public void onNext(String value) {
                            System.out.println("=======onNext===" + currentThread().getName() + "====value==" + value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println("=======onError===" + currentThread().getName() + e);
                        }

                        @Override
                        public void onComplete() {
                            System.out.println("=======onComplete===" + currentThread().getName());
                        }
                    });
        } else if (v == tvLifeCircle1) {
            //创建被观察者
           observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> subscriber) throws Exception {
                    //调用观察者的回调

                    while (true) {
                        System.out.println("=======subscribe===" + currentThread().getName());
                        //Thread.sleep(1000*3);//休眠3s
//                        subscriber.onNext("我是");
//                        subscriber.onNext("RxJava");
//                        subscriber.onNext("简单示例");
                        //subscriber.onError(new Throwable("出错了")); onError和onComplete互斥,只能同时执行一个
                        //subscriber.onComplete();
                    }

                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());

            //创建观察者

            observer = new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                    System.out.println("=======onSubscribe===" + currentThread().getName());
                }

                @Override
                public void onNext(String value) {
                    Log.e(tag, "onCompleted");
                    System.out.println("=======onNext===" + currentThread().getName() + "==" + value);

                }

                @Override
                public void onError(Throwable e) {
                    Log.e(tag, "onCompleted");
                    System.out.println("=======onError===" + currentThread().getName() + e);

                }

                @Override
                public void onComplete() {
                    Log.e(tag, "onCompleted");
                    System.out.println("=======onComplete===" + currentThread().getName());

                }
            };

            //必须在observable创建的时候指定,不指定的话是默认的当前线程,多次指定只以第一次指定为准
            //observable.subscribeOn(Schedulers.newThread());//事件发布在子线程
            //observable.observeOn(AndroidSchedulers.mainThread());//时间回调在主线程


            //注册，是的观察者和被观察者关联，将会触发OnSubscribe.call方法
            observable.subscribe(observer);

        }


    }

    Observable<String> observable;
    Observer<String> observer;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("=======1onDestroy===");

    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("=======1没有订阅=onStop==");

    }
}
