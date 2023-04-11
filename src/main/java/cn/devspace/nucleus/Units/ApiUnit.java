/*
 *
 *  _   _            _
 * | \ | |          | |
 * |  \| |_   _  ___| | ___ _   _ ___
 * | . ` | | | |/ __| |/ _ \ | | / __|
 * | |\  | |_| | (__| |  __/ |_| \__ \
 * \_| \_/\__,_|\___|_|\___|\__,_|___/
 * Author: Pama Lee
 * CreateTime: 2023/1/22 下午11:44
 */

package cn.devspace.nucleus.Units;

import cn.devspace.nucleus.Message.Log;
import cn.devspace.nucleus.Server.Server;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

public class ApiUnit {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * 生成POST请求
     * @param URL 请求地址
     * @param form 请求参数
     * @return 返回请求结果
     */
    public static HttpResponse<String> createPOST(String URL, Map<String, String> form){
        HttpRequest request = HttpRequest.newBuilder()
                .POST(ofFormData(form))
                .uri(URI.create(URL))
                .setHeader("User-Agent", Server.getName()+Server.getServerVersion())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Log.sendWarn(e.getMessage());
            return null;
        }
    }

    /**
     * 生成GET请求
     * @param URL 请求地址
     * @return 返回请求结果
     */
    public static HttpResponse<String> createGET(String URL){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL))
                .setHeader("User-Agent", Server.getName()+Server.getServerVersion())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Log.sendWarn(e.getMessage());
            return null;
        }
    }

    /**
     * 生成GET请求
     * @param URL 请求地址
     * @param form 请求参数
     * @return 返回请求结果
     */
    public static HttpResponse<String> createGET(String URL, Map<String, String> form){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(URL+"?"+ofFormData(form)))
                .setHeader("User-Agent", Server.getName()+Server.getServerVersion())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Log.sendWarn(e.getMessage());
            return null;
        }
    }

    /**
     * 生成POST请求的Body
     * @param data
     * @return
     */
    public static HttpRequest.BodyPublisher ofFormData(Map<String, String> data) {
        var builder = new StringBuilder();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    /**
     * 将json字符串转换为Map
     * @param json json字符串
     * @return 返回Map
     */
    public static Map<String, Object> jsonToMap(String json) {
        Gson gson = new GsonBuilder().create();
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        Map<String, Object> map = gson.fromJson(reader, new TypeToken<Map<String, Object>>() {}.getType());
        // 默认将code字段的double类型转换为int类型
        if(map.containsKey("code") && map.get("code") instanceof Double){
            map.replace("code", (int)Double.parseDouble(map.get("code").toString()));
        }
        return map;
    }



}
