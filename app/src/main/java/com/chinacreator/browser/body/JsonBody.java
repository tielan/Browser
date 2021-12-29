package com.chinacreator.browser.body;

import androidx.annotation.Nullable;

import com.yanzhenjie.andserver.util.MediaType;

import org.json.JSONObject;

public class JsonBody extends StringBody {

    public JsonBody(String body) {
        super(body);
    }

    public JsonBody(JSONObject object) {
        super(object.toString());
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }
}