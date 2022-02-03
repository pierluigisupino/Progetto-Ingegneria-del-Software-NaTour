package com.ingsw2122_n_03.natour.infastructure.webSocket;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketSingleton {


        private static WebSocketSingleton instance = null;
        private final WebSocket webSocket;


        private WebSocketSingleton() {
            OkHttpClient mClient = new OkHttpClient();
            Request request = new Request.Builder().url("YOUR SOCKET ENDPOINT").build();
            EchoWebSocketListener listener = new EchoWebSocketListener();
            webSocket = mClient.newWebSocket(request, listener);
            mClient.dispatcher().executorService().shutdown();
        }


        public static WebSocketSingleton getInstance() {
            if(instance == null)
                instance = new WebSocketSingleton();
            return  instance;
        }


        private static final class EchoWebSocketListener extends WebSocketListener {

            private static final int CLOSE_STATUS = 1000;

            @Override
            public void onOpen(WebSocket webSocket, @NonNull Response response) {
                Log.i("CONNECT", response.toString());
                JSONObject a = new JSONObject();
                try {
                    a.put("action", "sendMessage");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                webSocket.send(a.toString());
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String message) {
                Log.i("MSG", message);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
                Log.i("MSG", String.valueOf(bytes));
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(CLOSE_STATUS, null);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable throwable, Response response) {
                Log.i("ERR", response.toString());
            }

        }

}
