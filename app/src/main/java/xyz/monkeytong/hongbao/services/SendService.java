package xyz.monkeytong.hongbao.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by shu.xinghu on 2017/1/9.
 */

public class SendService extends Service {


    private Handler mTypingHandler = new Handler();
    private String address;
    private static final int TYPING_TIMER_LENGTH = 2000;

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (mSocket!=null){
                mSocket.emit("wechat", "*****");
                Log.e("connected",address+"-->"+mSocket.connected());
                if (!mSocket.connected()){
                    Log.e("connected","重连");
                    init();
                }
            }
            mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
        }
    };

    private void init() {
        if (mSocket!=null){
            mSocket.disconnect();
            mSocket = null;
        }
        IO.Options opts = new IO.Options();
        opts.forceNew = true;
        opts.reconnection = false;

        try {
            mSocket = IO.socket("http://"+address,opts);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mTypingHandler.removeCallbacks(onTypingTimeout);
        mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
    }

    private Socket mSocket;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mSocket==null||!mSocket.connected()){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            address = preferences.getString("pref_comment_words","10.1.4.71:8002");
            Log.e("address",address+"---");
            init();
        }else {
            String msg = intent.getStringExtra("chat");
            if (msg!=null){
                mSocket.emit("wechat", msg);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mTypingHandler.removeCallbacks(onTypingTimeout);
        if (mSocket!=null&&mSocket.connected()){
            mSocket.disconnect();
        }
        super.onDestroy();
    }
}
