package com.micropace.ramp.base.entity.thirdpart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 调用爱自己查询二维码结果
 *
 * @author Suffrajet
 */
public class QueryLoveselfQrcodeResp extends BaseRestfulResponse {
    @Getter
    @Setter
    @ToString
    public static class QrcodeDetail {
        private Long id;
        private String scene;
        private Integer type;
        private String name;
        private String path;
        private Integer expire;
        private String createdAt;
        private String updatedAt;
    }
}