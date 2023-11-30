package org.giftorg.common.tokenpool;

/**
 * 获取 api token 执行的任务
 * 外部应该实现该接口，实现自己的业务逻辑
 */
public interface APITask {

    /**
     * 程序启动入口
     */
    void run(String token) throws Exception;
}
