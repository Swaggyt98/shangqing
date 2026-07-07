package com.zosoftware.solid.api;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidubce.qianfan.Qianfan;
import com.baidubce.qianfan.model.chat.ChatResponse;
import com.zosoftware.solid.MyApplication;
import com.zosoftware.solid.utils.AppConfig;
import com.zosoftware.solid.utils.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {
    public static String ipaddr = "";
    public static String port = "";
    public static String baseaddr = "https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2024-06-20&endDate=2024-08-30&words=閹锋粎娅?categories=缁夋垶濡?page=1";
    public static String baseaddr_port = "http://" + ipaddr + ":" + port;
    public static String basehttpaddr = "http://" + ipaddr + ":" + port + "/";
    public static String loginurl = baseaddr + "/user/login";
    public static String addlog = baseaddr + "/api/addlog";
    public static String bleuuid = "";

    public static void seturl(){
        baseaddr = "http://" + ipaddr + ":" + port;
        baseaddr_port = "http://" + ipaddr + ":" + port;
        basehttpaddr = "http://" + ipaddr + ":" + port + "/";
        loginurl = baseaddr + "/user/login";
        addlog = baseaddr + "/api/addlog";
        Utils.loginfo(loginurl);
    }

    public static void Get(Context context, String url, Callback callback) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        Request request = new Request
                .Builder()
                .url(url)
                .addHeader("authorization", context.getSharedPreferences("app", Context.MODE_PRIVATE).getString("token", ""))
                .get().build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void Post(Context context, String url, JSONObject jsonObject, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toJSONString());
        Request request = new Request
                .Builder()
                .addHeader("authorization", context.getSharedPreferences("app", Context.MODE_PRIVATE).getString("token", ""))
                .post(requestBody)  // Post鐠囬攱鐪伴惃鍕棘閺侀绱堕柅?
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public static String callchatgpt(String question) {
        Context context = MyApplication.getCurrentContext();
        String accessKey = AppConfig.getString(context, "QIANFAN_ACCESS_KEY", "");
        String secretKey = AppConfig.getString(context, "QIANFAN_SECRET_KEY", "");
        if (accessKey.isEmpty() || secretKey.isEmpty()) {
            Utils.loginfo("Qianfan credentials are not configured");
            return "";
        }

        Qianfan qianfan = new Qianfan(accessKey, secretKey);
        ChatResponse response = qianfan.chatCompletion()
                .model("ERNIE-Bot-4") // 娴ｈ法鏁odel閹稿洤鐣炬０鍕枂濡€崇€?                // .endpoint("completions_pro") // 娑旂喎褰叉禒銉ゅ▏閻⑩暋ndpoint閹稿洤鐣炬禒缁樺壈濡€崇€?(娴滃矂鈧绔?
                .addMessage("user", question) // 濞ｈ濮為悽銊﹀煕濞戝牊浼?(濮濄倖鏌熷▔鏇炲讲娴犮儴鐨熼悽銊ヮ樋濞嗏槄绱濇禒銉ョ杽閻滄澘顦挎潪顔碱嚠鐠囨繄娈戝☉鍫熶紖娴肩娀鈧?
                .temperature(0.7) // 閼奉亜鐣炬稊澶庣Т閸欏倹鏆?                .execute(); // 閸欐垼鎹ｇ拠閿嬬湴
        return response.getResult();

    }
    // 娴ｈ法鏁ら崗宥堝瀭鏉烆剙褰?API 閻ㄥ嫬婀撮崸鈧崪灞界槕闁?
    // 閸ヨ棄鍞撮悽銊﹀煕瀵ら缚顔呮担璺ㄦ暏 Host1: https://api.chatanywhere.tech/v1/chat
    // 閸ヨ棄顦婚悽銊﹀煕鐠囧嘲鐨?URL 娣囶喗鏁兼稉? https://api.chatanywhere.org/v1/chat
    private static final String DEFAULT_CHAT_ANYWHERE_API_URL = "https://api.chatanywhere.tech/v1/chat/completions";
    public static void callChatAnywhereAPI(Context context, String question, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // 鐠佸墽鐤嗘潻鐐村复鐡掑懏妞傞弮鍫曟？
                .readTimeout(30, TimeUnit.SECONDS)    // 鐠佸墽鐤嗙拠璇插絿鐡掑懏妞傞弮鍫曟？
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // 閺嬪嫰鈧姾顕Ч鍌欑秼閿涘本鐗撮幑顔肩杽闂勫懘娓跺Ч鍌炩偓澶嬪濡€崇€烽敍宀冪箹闁插瞼銇氭笟瀣╁▏閻?deepseek 濡€崇€?
        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("model", "gpt-3.5-turbo"); // 婵″倿娓堕崗鏈电铂濡€崇€烽敍宀冾嚞閺嶈宓?API 閺傚洦銆傜拫鍐╂殻
        JSONArray message = new JSONArray() ;
        JSONObject obj = new JSONObject();
        obj.put("role","user");
        obj.put("content",question);
        message.add(obj);

        requestBodyJson.put("messages", message);

        RequestBody requestBody = RequestBody.create(JSON, requestBodyJson.toJSONString());

        Request request = new Request.Builder()
                .url(AppConfig.getString(context, "CHAT_ANYWHERE_API_URL", DEFAULT_CHAT_ANYWHERE_API_URL))
                .addHeader("Authorization", "Bearer " + AppConfig.getString(context, "CHAT_ANYWHERE_API_KEY", ""))
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 閹垫挸宓冪拠锔剧矎闁挎瑨顕ら弮銉ョ箶閿涘奔绌舵禍搴㈠笓閺屻儵妫舵０?
                e.printStackTrace();
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 閼惧嘲褰囬柨娆掝嚖鏉╂柨娲栭惃鍕嚊缂佸棔淇婇幁?
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    System.out.println("API鐠囬攱鐪版径杈Е, HTTP code: " + response.code() +
                            ", message: " + response.message() +
                            ", error: " + errorBody);
                }
                // 鐏忓棗鎼锋惔鏂炬唉閻㈣精鐨熼悽銊ㄢ偓鍛槱閻?
                callback.onResponse(call, response);
            }
        });
    }
}
