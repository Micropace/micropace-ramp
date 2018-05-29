package com.micropace.ramp.base.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * HTTP回复Json信息格式
 * <pre>
 * {
 *     "status": "success",
 *     "message": "",
 *     "data": {}
 * }
 * </pre>
 */
@Getter
@Setter
public class ResponseMsg {
    /** 返回结果 */
    @JsonProperty("status")
    private String status;
    /** 结果信息 */
    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message = null;
    /** 返回数据 */
    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data = null;

    ResponseMsg(String status) {
        this.status = status;
    }

    ResponseMsg(String status, String message) {
        this.status = status;
        this.message = message;
    }

    ResponseMsg(String status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
