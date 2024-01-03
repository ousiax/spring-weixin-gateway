package me.codefarm.gateway.weixin.mp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class Code2session {

    /**
     * 会话密钥
     */
    @JsonProperty("session_key")
    private String sessionKey;

    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台账号下会返回，详见 UnionID 机制说明。
     */
    @JsonProperty("unionid")
    private String unionId;

    /**
     * 用户唯一标识
     */
    @JsonProperty("openid")
    private String openId;

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

}
