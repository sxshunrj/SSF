package com.jiaxy.ssf.message;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/03/21 17:14
 */
public class ResponseMessage<T> extends AbstractMessage{

    /**
     * the result of the service's method returned;
     */
    private T response;

    /**
     * the exception of the service declared
     */
    private Throwable exception;


    public ResponseMessage(boolean initMessageHead) {
        super(initMessageHead);
        if ( getHead() != null ){
            getHead().setMessageType(RESPONSE_MSG);
        }
    }

    public ResponseMessage(){
        super(true);
        getHead().setMessageType(RESPONSE_MSG);
    }

    public boolean isError(){
        return exception != null;
    }


    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public void setHead(MessageHead head) {
        super.setHead(head);
        if ( this.getHead() != null ){
            this.getHead().setMessageType(RESPONSE_MSG);
        }
    }
}
