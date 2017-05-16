package com.a11.rxjava;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {

    private static EditText input;
    private static EditText output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button task1 = (Button) findViewById(R.id.btn_task_1);
        Button task2 = (Button) findViewById(R.id.btn_task_2);
        Button task3 = (Button) findViewById(R.id.btn_task_3);
        Button task4 = (Button) findViewById(R.id.btn_task_4);
        Button task5 = (Button) findViewById(R.id.btn_task_5);
        Button task6 = (Button) findViewById(R.id.btn_task_6);

        input = (EditText)findViewById(R.id.input);
        output = (EditText) findViewById(R.id.output);

        task1.setOnClickListener(pView -> {
            input.setText("");
            output.setText("");
            List<String> ll = new LinkedList<>();
            ll.add("Vasya");
            ll.add("Roma");
            ll.add("Artur");
            ll.add("Petya");
            ll.add("Dima");
            task1(ll).subscribe(
                    pI -> {
                        Log.e("TASK1", "" + pI);
                        processOutput(output, "" + pI);
                    }
            );
        });

        task2.setOnClickListener(pView -> {
            input.setText("");
            output.setText("");
            List<String> ll = new LinkedList<>();
            ll.add("Vasya");
            ll.add("Roma");
            ll.add("Artur");
            ll.add("Petya");
            ll.add("Artur");
            ll.add("Petya");
            ll.add("Dima");
            ll.add("END");
            ll.add("AfterEnd");
            task2(Observable.fromIterable(ll)).subscribe(
                    pI -> {
                        Log.e("TASK2", "" + pI);
                        processOutput(output, "" + pI);
                    }
            );
        });

        task3.setOnClickListener(pView -> {
            input.setText("");
            output.setText("");
            Observable<Integer> arrayObservable = Observable.fromArray(new Integer[] {1, 2, 3, 4});
            sum(arrayObservable).subscribe(
                    pI ->
                    {
                        Log.e("TASK3", "" + pI);
                        processOutput(output, "" + pI);
                    }
            );
        });

        task4.setOnClickListener(pView -> {
            input.setText("");
            output.setText("");
            Observable<Boolean> flag = Observable.fromArray(new Boolean[] {false});
            Observable<Integer> arrayObservable = Observable.fromArray(new Integer[] {1, 2, 3, 4});
            Observable<Integer> arrayObservable2 = Observable.fromArray(new Integer[] {12, 222, 3, 4});
            task4(flag, arrayObservable, arrayObservable2).subscribe(
                    pI -> {
                        Log.e("TASK4", "" + pI);
                        processOutput(output, "" + pI);
                    }
                    ,  pThrowable -> {
                        Log.e("TASK4", "" + pThrowable.getMessage());
                        processOutput(output, pThrowable.getMessage());
                    });
        });

        task5.setOnClickListener(pView -> {
            input.setText("");
            output.setText("");
            Observable<Integer> arrayObservable = Observable.fromArray(new Integer[] {100, 17, 63});
            Observable<Integer> arrayObservable2 = Observable.fromArray(new Integer[] {15, 89, 27});
            gcdsObservable(arrayObservable, arrayObservable2)
                    .subscribe( pI -> {
                        Log.e("TASK5", "" + pI);
                        processOutput(output, "" + pI);
                    });
        });

        final Observable<BigInteger> task6Observable = task6Observable();
        task6.setOnClickListener(pView -> {
            input.setText("");
            output.setText("");
            task6Observable
                    .subscribe(pBigInteger ->  {
                        Log.e("TASK6", "Subscriber 1 result - " + pBigInteger);
                        processOutput(output, "" + pBigInteger);
                    });
            task6Observable
                    .subscribe(pBigInteger ->  Log.e("TASK6", "Subscriber 2 result - " + pBigInteger));

        });
    }

    @NonNull
    public static Observable<Integer> task1(@NonNull List<String> list) {
        return Observable.fromIterable(list)
                .doOnNext(pS -> processOutput(input, pS))
                .filter(pS -> pS.toUpperCase().contains("R"))
                .map(String::length);
    }

    @NonNull
    public static Observable<String> task2(@NonNull Observable<String> observable) {
        return observable
                .doOnNext(pS -> processOutput(input, pS))
                .takeWhile(pS -> !pS.equals("END"))
                .distinct();
    }

    @NonNull
    public static Observable<Integer> sum(@NonNull Observable<Integer> observable) {
        return observable
                .doOnNext(pS -> processOutput(input, "" + pS))
                .reduce(0, (pI, pI2) -> pI + pI2)
                .toObservable();
    }

    @NonNull
    public static Observable<Integer> task4(@NonNull Observable<Boolean> flagObservable,
                                            @NonNull Observable<Integer> first, @NonNull Observable<Integer> second) {
        return flagObservable
                .doOnNext(pS -> processOutput(input, "flag: " + pS))
                .flatMap(pBoolean -> pBoolean ? first : second)
                .doOnNext(pS -> processOutput(input, "" + pS))
                .map(pI -> {
                    if (pI > 99) {
                        throw new IllegalArgumentException("Number is greater than 99");
                    }
                    return pI;
                });
    }

    @NonNull
    public static Observable<Integer> gcdsObservable(@NonNull Observable<Integer> first,
                                                     @NonNull Observable<Integer> second) {
        return Observable.zip(first, second, (pInteger, pInteger2) -> {
            processOutput(input, "" + pInteger + "/" + pInteger2);
            return BigInteger.valueOf(pInteger).gcd(BigInteger.valueOf(pInteger2)).intValue();
        });
    }

    @NonNull
    public static Observable<BigInteger> task6Observable() {
        return Observable.range(1, 100000)
                .map(pInteger -> pInteger * 2)
                .skip(40000)
                .skipLast(40000)
                .filter(pInteger -> pInteger % 3 == 0)
                .reduce(1, (pR, pInteger) -> pR * pInteger)
                .toObservable()
                .map(pInteger -> Log.e("TASK6", "recalculation"))
                .map(BigInteger::valueOf)
                .cache();
    }

    private static void processOutput(EditText view, String msg) {
        if (view.getText().toString().isEmpty()) {
            view.setText(msg);
        } else {
            view.setText(view.getText() + ", " + msg);
        }
    }
}
