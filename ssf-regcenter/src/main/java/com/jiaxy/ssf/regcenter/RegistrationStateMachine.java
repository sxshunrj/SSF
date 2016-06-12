package com.jiaxy.ssf.regcenter;

import com.jiaxy.ssf.common.SSFURL;
import io.atomix.copycat.server.StateMachine;

/**
 * Title: <br>
 * <p>
 * Description: <br>
 * </p>
 *
 * @author <a href=mailto:taobaorun@gmail.com>wutao</a>
 *
 * @since 2016/06/12 17:16
 */
public class RegistrationStateMachine extends StateMachine {


    public SSFURL register(){
        SSFURL ssfurl = new SSFURL();
        return ssfurl;
    }
}
