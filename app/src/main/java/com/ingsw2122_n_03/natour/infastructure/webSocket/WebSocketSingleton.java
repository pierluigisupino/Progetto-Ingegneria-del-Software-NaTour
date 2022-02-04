package com.ingsw2122_n_03.natour.infastructure.webSocket;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.BuildConfig;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.model.Message;
import com.ingsw2122_n_03.natour.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class WebSocketSingleton {

    private static WebSocketSingleton instance = null;
    private final WebSocket webSocket;
    private final String subClient;

    private final int CLOSE_STATUS = 1000;


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

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "sendMessage");
            jsonObject.put("sender", subClient);
            jsonObject.put("receiver", message.getReceiver().getUid());
            jsonObject.put("text", message.getBody());
            jsonObject.put("senddate", message.getSendDate());
            jsonObject.put("sendtime", message.getSendTime());
            webSocket.send(jsonObject.toString());
        } catch (JSONException ignored) {}

    }



    public void closeConnection() {
        webSocket.close(CLOSE_STATUS, null);
        instance = null;
    }



        private final class EchoWebSocketListener extends WebSocketListener {

            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("action", "setConnectionId");
                    jsonObject.put("sub", subClient);
                    webSocket.send(jsonObject.toString());
                } catch (JSONException ignored) {}

            }

            @Override
            @SuppressLint("NewApi")
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String message) {

                try {

                    JSONObject jsonObject = new JSONObject(message);

                    if(jsonObject.has("statusCode")) {
                        if(jsonObject.getInt("statusCode") == 200)
                            MessageController.getInstance().onMessageSentSuccess();
                        else
                            MessageController.getInstance().onMessageSentError();
                        return;
                    }

                    String body = jsonObject.getString("text");

                    User sender = new User(jsonObject.getString("sender"));
                    sender.setName(jsonObject.getString("sendername"));

                    LocalDate sendDate = LocalDate.parse(jsonObject.getString("senddate"));
                    LocalTime sendTime = LocalTime.parse(jsonObject.getString("sendtime").substring(0,5), DateTimeFormatter.ofPattern("HH:mm"));

                    MessageController.getInstance().onMessageReceived(new Message(body, sendDate, sendTime, sender, new User(subClient)));

                } catch (JSONException ignored) {}
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {}

            @Override
            public void onClosing(WebSocket webSocket, int code, @NonNull String reason) {
                webSocket.close(CLOSE_STATUS, null);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable throwable, Response response) {}

        }

}
