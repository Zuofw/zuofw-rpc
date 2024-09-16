package com.zuofw.rpc.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum SerializerEnum {
    JDK(0, "jdk"),
    JSON(1, "json");

    private final int key;
    private final String value;

    SerializerEnum(int key, String value) {
        this.key = key;
        this.value = value;
    }
    public static List<String> getList() {
        return Arrays.stream(values())
                .map(item->item.value)
                .collect(Collectors.toList());
    }
    public static SerializerEnum getByKey(int key) {
        for (SerializerEnum anEnum : SerializerEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }

    public static SerializerEnum getByValue(String value) {
        for (SerializerEnum anEnum : SerializerEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
