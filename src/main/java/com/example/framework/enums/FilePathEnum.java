package com.example.framework.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilePathEnum {

    AVATAR("avatar/", "头像路径");

    private final String path;

    private final String desc;

}
