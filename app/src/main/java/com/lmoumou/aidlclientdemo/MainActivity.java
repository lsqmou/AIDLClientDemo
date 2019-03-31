package com.lmoumou.aidlclientdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lmoumou.aidlservicedemo.Book;
import com.lmoumou.aidlservicedemo.BookController;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Client_MainActivity";

    private EditText bookNameEdt;
    private TextView contentTv;

    private BookController bookController;
    private boolean connected;
    private List<Book> bookList;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookController = BookController.Stub.asInterface(service);
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_getBookList).setOnClickListener(this);
        findViewById(R.id.btn_addBook_inOut).setOnClickListener(this);
        bookNameEdt = findViewById(R.id.book_name_edt);
        contentTv = findViewById(R.id.content_tv);
        bindService();
    }

    private void bindService() {
        Log.e(TAG,"bindService");
        Intent intent = new Intent();
        intent.setPackage("com.lmoumou.aidlservicedemo");
        intent.setAction("com.lmoumou.aidlservicedemo.action_book");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connected) {
            unbindService(serviceConnection);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getBookList:
                getData();
                break;
            case R.id.btn_addBook_inOut:
                Log.e(TAG,"connected->"+connected);
                if (connected) {
                    String bookName = bookNameEdt.getText().toString();
                    Log.e(TAG,""+bookName);
                    if (bookName.isEmpty()) {
                        Toast.makeText(MainActivity.this, "填写署书名", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Book book = new Book("这是一本新书 InOut");
                    try {
                        bookController.addBookInOut(book);
                        getData();
                        Log.e(TAG, "向服务器以InOut方式添加了一本新书");
                        Log.e(TAG, "新书名：" + book.getName());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Log.e(TAG, "异常2：->" + e.getMessage());
                    }
                }
                break;
        }
    }

    private void getData() {
        if (connected) {
            try {
                bookList = bookController.getBookList();
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.e(TAG, "异常1：->" + e.getMessage());
            }

            showMessage();
            for (Book item : bookList) {
                Log.e(TAG, "" + item.getName());
            }
        }
    }

    private void showMessage() {
        String contentStr = "";
        for (Book book : bookList) {
            contentStr += (book.getName() + "\n");
        }
        contentTv.setText(contentStr);
    }
}
