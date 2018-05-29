package com.micropace.ramp.wechat.core.thirdpart.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
class BaseRestfulResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private QueryLoveselfQrcodeResp.QrcodeDetail data;
    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
}
