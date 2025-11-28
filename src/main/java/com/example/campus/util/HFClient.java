package com.example.campus.util;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class HFClient {

    // ==== 1. HuggingFace API Endpoint ====
    private static final String API_URL =
            "https://api-inference.huggingface.co/models/facebook/bart-large-mnli";

    // ==== 2. 显式写入你的 Token（你必须填上自己的 Token）====
    private static final String HF_API_KEY = "YOUR_HF_API_KEY";

    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 调用 HF Zero-shot 分类
     */
    public static JSONObject analyze(String text) {

        try {
            JSONObject data = new JSONObject()
                    .put("inputs", text)
                    .put("parameters", new JSONObject()
                            .put("candidate_labels",
                                    new JSONArray()
                                            .put("quiet")
                                            .put("noisy")
                                            .put("tidy")
                                            .put("messy")
                                            .put("study")
                                            .put("gaming")
                            )
                    );

            RequestBody body = RequestBody.create(
                    data.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + TOKEN)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();

            return new JSONObject(json);

        } catch (Exception e) {
            e.printStackTrace();

            // 出错时返回空结构，避免 null 问题
            return new JSONObject()
                    .put("labels", new JSONArray())
                    .put("scores", new JSONArray());
        }
    }
}
