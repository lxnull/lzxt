package com.lx.xt.common.service;

import com.lx.xt.common.model.BusinessCodeEnum;
import com.lx.xt.common.model.CallResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * @author Jarno
 */
@Component
@Slf4j
public class ServiceTemplateImpl implements ServiceTemplate {

    @Override
    public <T> CallResult<T> execute(TemplateAction<T> action) {
        try{
            CallResult<T> callResult = action.checkParam();
            if(callResult==null){
                log.warn("execute: Null result while checkParam");
                return CallResult.fail(BusinessCodeEnum.CHECK_PARAM_NO_RESULT.getCode(), BusinessCodeEnum.CHECK_PARAM_NO_RESULT.getMsg());
            }
            if(!callResult.isSuccess()){
                return callResult;
            }
            // 检查业务逻辑是否符合要求
            callResult = action.checkBiz();
            // 返回值为空
            if(callResult == null){
                // 回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                log.warn("execute: Null result while checkBiz");
                return CallResult.fail(BusinessCodeEnum.CHECK_BIZ_NO_RESULT.getCode(), BusinessCodeEnum.CHECK_BIZ_NO_RESULT.getMsg());
            }
            // 返回值为操作失败
            if(!callResult.isSuccess()){
                // 回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return callResult;
            }
            // 记录业务逻辑操作时长
            long start = System.currentTimeMillis();
            // 开始业务逻辑操作
            CallResult<T> cr= action.doAction();
            log.info("execute datasource method run time:{}ms", System.currentTimeMillis() - start);
            if (cr == null){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return CallResult.fail(BusinessCodeEnum.CHECK_ACTION_NO_RESULT.getCode(), BusinessCodeEnum.CHECK_ACTION_NO_RESULT.getMsg());
            }
            if (!cr.isSuccess()){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return cr;
            }
            if(cr.isSuccess()){
                action.finishUp(cr);
            }
            return cr;
        }catch(Exception e){
            // 出现异常回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            log.error("excute error", e);
            return CallResult.fail();
        }
    }

    // 不涉及事务，少了回滚操作
    @Override
    public <T> CallResult<T> executeQuery(TemplateAction<T> action) {
        try{
            CallResult<T> callResult = action.checkParam();
            if(callResult==null){
                log.warn("executeQuery: Null result while checkParam");
                return CallResult.fail(BusinessCodeEnum.CHECK_PARAM_NO_RESULT.getCode(), BusinessCodeEnum.CHECK_PARAM_NO_RESULT.getMsg());
            }
            if(!callResult.isSuccess()){
                return callResult;
            }
            callResult = action.checkBiz();
            if(callResult==null){
                log.warn("executeQuery: Null result while checkBiz");
                return CallResult.fail(BusinessCodeEnum.CHECK_BIZ_NO_RESULT.getCode(), BusinessCodeEnum.CHECK_BIZ_NO_RESULT.getMsg());
            }
            if(!callResult.isSuccess()){
                return callResult;
            }
            long start = System.currentTimeMillis();
            CallResult<T> cr= action.doAction();
            log.info("executeQuery datasource method run time:{}ms", System.currentTimeMillis() - start);
            if (cr == null){
                return CallResult.fail(BusinessCodeEnum.CHECK_ACTION_NO_RESULT.getCode(), BusinessCodeEnum.CHECK_ACTION_NO_RESULT.getMsg());
            }
            if (!cr.isSuccess()){
                return cr;
            }
            if(cr.isSuccess()){
                action.finishUp(cr);
            }
            return cr;
        }catch(Exception e){
            e.printStackTrace();
            log.error("executeQuery error", e);
            return CallResult.fail();
        }
    }
}