package me.codefarm.gateway.weixin.mp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 数据水印
 * 
 * @see https://developers.weixin.qq.com/minigame/dev/guide/open-ability/signature.html
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Watermark {

    /**
     * 敏感数据获取的时间戳, 开发者可以用于数据时效性校验
     */
    private int timestamp;

    /**
     * 敏感数据归属 appId，开发者可校验此参数与自身 appId 是否一致
     */
    @JsonProperty("appid")
    private String appId;
}