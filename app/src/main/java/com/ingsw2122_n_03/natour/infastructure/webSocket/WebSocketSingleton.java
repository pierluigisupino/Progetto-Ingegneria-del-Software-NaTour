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

import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.BuildConfig;
import com.ingsw2122_n_03.natour.model.Message;


public class WebSocketSingleton {

    private static WebSocketSingleton instance = null;
    private final WebSocket webSocket;
    private final String subClient;


    private WebSocketSingleton() {
        subClient = Amplify.Auth.getCurrentUser().getUserId();
        OkHttpClient mClient = new OkHttpClient();
        Request request = new Request.Builder().url(BuildConfig.URL_WEB_SOCKET).build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        webSocket = mClient.newWebSocket(request, listener);
        mClient.dispatcher().executorService().shutdown();
    }


    public static WebSocketSingleton getInstance() {
        if(instance == null)
            instance = new WebSocketSingleton();
        return  instance;
    }


    public void sendMessage(Message message) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sender", subClient);
            jsonObject.put("receiver", message.getReceiver().getUid());
            jsonObject.put("body", message.getBody());
            jsonObject.put("senddate", message.getSendDate());
            jsonObject.put("sendtime", message.getSendTime());
            webSocket.send(jsonObject.toString());
        } catch (JSONException ignored) {}

    }


    private void retrieveMessage(String message) {
        Log.i("MSG", message);
        //HANDLE IN UI
    }


        private final class EchoWebSocketListener extends WebSocketListener {

            private static final int CLOSE_STATUS = 1000;

            @Override
            public void onOpen(WebSocket webSocket, @NonNull Response response) {

                try {
                    JSONObject body = new JSONObject();
                    body.put("action", "$connect");
                    body.put("subclient", subClient);
                    webSocket.send(body.toString());
                } catch (JSONException ignored) {}

            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String message) {
                retrieveMessage(message);
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {}

            @Override
            public void onClosing(WebSocket webSocket, int code, @NonNull String reason) {

                try {
                    JSONObject body = new JSONObject();
                    body.put("action", "$disconnect");
                    body.put("subclient", subClient);
                    webSocket.send(body.toString());
                } catch (JSONException ignored) {}
                webSocket.close(CLOSE_STATUS, null);

            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable throwable, Response response) {}

        }

}
