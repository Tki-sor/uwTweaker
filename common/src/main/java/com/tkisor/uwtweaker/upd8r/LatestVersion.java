package com.tkisor.uwtweaker.upd8r;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.tkisor.uwtweaker.config.UwTweakerConfig;
import org.jetbrains.annotations.NotNull;

public class LatestVersion {
    String versionName;
    int versionCode;
    String versionFormat;

    private LatestVersion(String versionName, int versionCode, String versionFormat) {
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.versionFormat = versionFormat;
    }

    @NotNull
    public static LatestVersion deserializer(String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if (!jsonElement.isJsonObject()) {
                LogUtils.getLogger().error("Invalid JSON object format: " + json);
                return new LatestVersion("", -1, UwTweakerConfig.getConfig().getUpd8rConfig().getCurrentVersionFormat());
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String versionName = "null";
            int versionCode = -1;
            String versionFormat = UwTweakerConfig.getConfig().getUpd8rConfig().getCurrentVersionFormat();

            JsonElement versionNameElement = jsonObject.get("versionName");
            if (versionNameElement != null && versionNameElement.isJsonPrimitive() && versionNameElement.getAsJsonPrimitive().isString()) {
                versionName = versionNameElement.getAsString();
            } else {
                LogUtils.getLogger().warn("Invalid or missing 'versionName' field in JSON: " + json);
            }

            JsonElement versionCodeElement = jsonObject.get("versionCode");
            if (versionCodeElement != null && versionCodeElement.isJsonPrimitive() && versionCodeElement.getAsJsonPrimitive().isNumber()) {
                versionCode = versionCodeElement.getAsInt();
            } else {
                LogUtils.getLogger().warn("Invalid or missing 'versionCode' field in JSON: " + json);
            }

            return new LatestVersion(versionName, versionCode, versionFormat);
        } catch (JsonSyntaxException e) {
            LogUtils.getLogger().error("Failed to parse JSON: " + json, e);
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error("Invalid argument: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.getLogger().error("An unexpected error occurred while parsing JSON: " + json, e);
        }

        return new LatestVersion("", -1, UwTweakerConfig.getConfig().getUpd8rConfig().getCurrentVersionFormat());
    }
}
