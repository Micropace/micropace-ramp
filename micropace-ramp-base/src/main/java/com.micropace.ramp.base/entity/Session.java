package com.micropace.ramp.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号用户会话
 *
 * @author Suffrajet
 */
@Getter
@Setter
@ToString
public class Session {
    /** 会话发起的用户openid */
    private String openid;
    /** 会话开始时间戳 */
    private Long timestemp;
    /**
     * 会话操作步骤总数
     * -1: 不限制步骤总数
     * >0: 限制步骤总数
     */
    private int stepCount;
    /**
     * 会话当前进行到的步骤
     * 限制步骤总数情况下, 则累加到总数
     * 如果不限制步骤总数，则一直累加
     */
    private int currentStep;
    /** 会话每个步骤的内容记录 */
    private Map<Long, Object> contents;

    public Session() {}

    public Session(String openid) {
        this.openid      = openid;
        this.timestemp   = System.currentTimeMillis();
        this.stepCount   = -1;
        this.currentStep = 1; // 创建会话则为第一步
        this.contents    = new HashMap<>();
        this.contents.put(1L, "Session Started");
    }

    public Session(String openid, int stepCount) {
        this(openid);
        this.stepCount   = stepCount;
    }

    /**
     * 如果存在有限步骤总数，则会话进行到下一步，直到最后一步
     * 如果不限制步骤总数，则忽略
     *
     * @param stepContent 最新一步的会话内容
     */
    public void next(Object stepContent) {
        this.currentStep += 1;
        if(this.stepCount > 0) {
            if(stepContent != null) {
                this.contents.put(Long.parseLong(String.valueOf(this.currentStep)), stepContent);
            }
            if(this.currentStep >= this.stepCount) {
                this.currentStep = this.stepCount;
            }
        } else {
            if(stepContent != null) {
                this.contents.put(Long.parseLong(String.valueOf(this.currentStep)), stepContent);
            }
        }
    }

    /**
     * 获取指定步骤下的会话内容
     * @param step 指定会话步骤
     * @return Object
     */
    public Object getStepContent(int step) {
        return this.contents.get(Long.parseLong(String.valueOf(step)));
    }


}
