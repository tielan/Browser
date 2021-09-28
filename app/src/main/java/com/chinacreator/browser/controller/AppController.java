/*
 * Copyright 2018 Zhenjie Yan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chinacreator.browser.controller;

import com.chinacreator.browser.event.ExitAppEvent;
import com.chinacreator.browser.event.MessageEvent;
import com.chinacreator.browser.utils.ConfigUtils;
import com.chinacreator.browser.utils.FileUtils;
import com.chinacreator.browser.utils.ZipUtil;
import com.yanzhenjie.andserver.annotation.CookieValue;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.util.MediaType;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;

/**
 * Created by Zhenjie Yan on 2018/6/9.
 */
@RestController
@RequestMapping(path = "/app")
class AppController {

    @PostMapping(path = "/deployFile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String deployFile(@RequestParam(name = "file") MultipartFile file) throws Exception {
        String targetPath = FileUtils.getRandomPath().getAbsolutePath();
        ZipUtil.UnZipFolder(file.getStream(),targetPath );
        MessageEvent event = new MessageEvent();
        event.setUrl(targetPath+"/index.html");
        EventBus.getDefault().post(event);
        return "ok";
    }

    @PostMapping(path = "/saveConfig", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String saveConfig(@RequestBody MessageEvent event) {
        EventBus.getDefault().post(event);
        return "Save successful";
    }

    @GetMapping(path = "/getConfig", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MessageEvent getConfig() {
        MessageEvent event = ConfigUtils.getInstance().getConfig();
        return event;
    }

    @GetMapping(path = "/exitApp")
    String exitApp() {
        EventBus.getDefault().post(new ExitAppEvent(true,false));
        return "exitApp";
    }

    @GetMapping(path = "/restartApp")
    String restartApp() {
        EventBus.getDefault().post(new ExitAppEvent(false,true));
        return "restartApp";
    }
}