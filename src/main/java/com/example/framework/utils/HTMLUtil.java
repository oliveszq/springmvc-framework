package com.example.framework.utils;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;

public class HTMLUtil {

    // temporary unused
    private static final SensitiveWordBs sensitiveWordBs = SensitiveWordBs.newInstance()
            .ignoreCase(true)
            .ignoreWidth(true)
            .ignoreNumStyle(true)
            .ignoreChineseStyle(true)
            .ignoreEnglishStyle(true)
            .ignoreRepeat(true)
            .enableNumCheck(false)
            .enableEmailCheck(false)
            .enableUrlCheck(false)
            .init();

    /**
     * 过滤并清理HTML字符串
     * 该方法旨在清除HTML字符串中的标签，防止XSS攻击，并移除图像标签外的所有HTML标签
     *
     * @param source 待过滤的HTML字符串
     * @return 过滤后的字符串
     */
    public static String filter(String source) {
        // 移除除<img>标签外的所有HTML标签
        source = source.replaceAll("(?!<(img).*?>)<.*?>", "")
                // 移除onload事件处理程序，防止XSS攻击
                .replaceAll("(onload(.*?)=)", "")
                // 移除onerror事件处理程序，防止XSS攻击
                .replaceAll("(onerror(.*?)=)", "");
        // 进一步清理，删除任何残留的HTML标签
        return deleteHMTLTag(source);
    }

    /**
     * 删除字符串中的HTML标签
     * 该方法旨在清除字符串中的HTML实体、脚本标签和样式标签，以防止XSS攻击和提高文本处理的安全性
     *
     * @param source 待处理的字符串，可能包含HTML标签
     * @return 清除了HTML标签的字符串
     */
    public static String deleteHMTLTag(String source) {
        // 移除HTML实体，如&amp;, &lt;, &gt;等
        source = source.replaceAll("&.{2,6}?;", "");

        // 移除<script>标签及其内容，防止JavaScript注入
        source = source.replaceAll("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", "");

        // 移除<style>标签及其内容，防止CSS注入或样式篡改
        source = source.replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "");

        return source;
    }

}
