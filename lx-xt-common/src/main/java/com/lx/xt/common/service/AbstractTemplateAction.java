package com.lx.xt.common.service;

import com.lx.xt.common.model.CallResult;

/**
 * @author Jarno
 */
public abstract class AbstractTemplateAction<T> implements TemplateAction<T> {
    // 返回值一定是成功
    @Override
    public CallResult<T> checkParam() {
        return CallResult.success();
    }

    @Override
    public CallResult<T> checkBiz() {
        return CallResult.success();
    }

    @Override
    public void finishUp(CallResult<T> callResult) {

    }
}