package com.lx.xt.common.service;

import com.lx.xt.common.model.CallResult;

/**
 * @author Jarno
 */
public interface ServiceTemplate {


    /**
     * 执行数据库逻辑操作与事务操作
     * run in  datasource and execute Transaction
     * @param action
     * @param <T>
     * @return
     */
    <T> CallResult<T> execute(TemplateAction<T> action);

    /**
     * 不涉及到事务操作
     * run in  datasource and not execute Transaction
     * @param action
     * @param <T>
     * @return
     */
    <T> CallResult<T> executeQuery(TemplateAction<T> action);
}