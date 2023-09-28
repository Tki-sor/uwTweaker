package com.tkisor.uwtweaker.util;

import com.tkisor.uwtweaker.UwTweaker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

public class PersistentData {
    private static final ResourceLocation rl = new ResourceLocation(UwTweaker.MOD_ID, "data");

    private MinecraftServer server;
    private CompoundTag tag;

    private PersistentData() {
    }

    public static PersistentData get(MinecraftServer server) {
        PersistentData data = new PersistentData();
        data.server = server;
        data.tag = server.getCommandStorage().get(rl);
        return data;
    }

    public CompoundTag getTag() {
        return tag;
    }

    /**
     * 自动检查类型并存入tag，如需获取数据请手动推断类型
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void put(String key, T value) {
        // 检查T的类型
        if (value instanceof String) {
            tag.putString(key, (String) value);
        } else if (value instanceof Integer) {
            tag.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            tag.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            tag.putFloat(key, (Float) value);
        } else if (value instanceof Double) {
            tag.putDouble(key, (Double) value);
        } else if (value instanceof Long) {
            tag.putLong(key, (Long) value);
        } else if (value instanceof Byte) {
            tag.putByte(key, (Byte) value);
        } else if (value instanceof Short) {
            tag.putShort(key, (Short) value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putString(String key, String value) {
        tag.putString(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putInt(String key, int value) {
        tag.putInt(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putBoolean(String key, boolean value) {
        tag.putBoolean(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putFloat(String key, float value) {
        tag.putFloat(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putDouble(String key, double value) {
        tag.putDouble(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putLong(String key, long value) {
        tag.putLong(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putByte(String key, byte value) {
        tag.putByte(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public void putShort(String key, short value) {
        tag.putShort(key, value);
        this.server.getCommandStorage().set(rl, tag);
    }

    public String getString(String key) {
        return tag.getString(key);
    }

    public int getInt(String key) {
        return tag.getInt(key);
    }

    public boolean getBoolean(String key) {
        return tag.getBoolean(key);
    }

    public float getFloat(String key) {
        return tag.getFloat(key);
    }

    public double getDouble(String key) {
        return tag.getDouble(key);
    }

    public long getLong(String key) {
        return tag.getLong(key);
    }

    public byte getByte(String key) {
        return tag.getByte(key);
    }

    public short getShort(String key) {
        return tag.getShort(key);
    }

}
