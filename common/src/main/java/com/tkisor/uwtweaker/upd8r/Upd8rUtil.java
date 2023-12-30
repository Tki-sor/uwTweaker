package com.tkisor.uwtweaker.upd8r;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.tkisor.uwtweaker.config.UwTweakerConfig;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Upd8rUtil {
    @NotNull
    public static String getLatestVersion() {
        String text = null;
        for (int i = 0; i < UwTweakerConfig.getConfig().getUpd8rConfig().getLatestVersionUrls().size(); i++) {
            try {
                URLConnection urlConnection = new URL(
                        UwTweakerConfig.getConfig().getUpd8rConfig().getLatestVersionUrls().get(i)
                ).openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                try {
                    JsonElement jsonElement = JsonParser.parseString(stringBuilder.toString());
                    if (!jsonElement.isJsonObject()) {
                        continue;
                    }
                } catch (Exception e) {
                    continue;
                }

                text = stringBuilder.toString();

                reader.close();
                inputStream.close();
                break;
            } catch (IOException e) {
                LogUtils.getLogger().error("Failed to get latest version from " + UwTweakerConfig.getConfig().getUpd8rConfig().getLatestVersionUrls().get(i));
            }
        }
        if (text != null) {
            return text;
        }
        return "{}";
    }
}
