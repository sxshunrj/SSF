package com.jiaxy.ssf.thread.dreamwork;

/**
 * Title:<br>
 * Desc:<br>
 * <p>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2015/05/16 18:17
 */
public interface DreamTask extends Runnable{

    /**
     *
     * @return the type of the dream task
     */
    String dream();


    /**
     *
     * @return the first executed time of the dream task
     */
    long executedTime();


    /**
     *
     * @return the created time of the task
     */
    long createdTime();


    int timeout();

    //void updateFirstExecutedTime(long executedTime);


}
