package me.codefarm.gateway.weixin.mp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public final class UserPhoneNumber {
    /**
     * 用户手机号信息
     */
    @JsonProperty("phone_info")
    private PhoneInfo phoneInfo;

    /**
     * 错误码
     */
    @JsonProperty("errcode")
    private int errCode;

    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errMsg;

    /**
     * 用户手机号信息
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public final static class PhoneInfo {
        /**
         * 用户绑定的手机号（国外手机号会有区号）
         */
        private String phoneNumber;

        /**
         * 没有区号的手机号
         */
        private String purePhoneNumber;

        /**
         * 区号
         */
        private String countryCode;

        /**
         * 数据水印
         */
        private Watermark watermark;
    }
}
