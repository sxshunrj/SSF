package com.jiaxy.ssf.regcenter.server;

import com.jiaxy.ssf.common.bo.SSFURL;
import com.jiaxy.ssf.common.bo.SubscribeURL;
import com.jiaxy.ssf.regcenter.common.RegisterCommand;
import com.jiaxy.ssf.regcenter.common.SubscribeCommand;
import com.jiaxy.ssf.regcenter.common.UnRegisterCommand;
import com.jiaxy.ssf.regcenter.common.UnSubscribeCommand;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.session.ServerSession;
import io.atomix.copycat.server.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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
public class RegistrationStateMachine extends StateMachine implements SessionListener {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationStateMachine.class);

    private Map<String,List<Commit<RegisterCommand>>> rcMap = new HashMap<>();

    private Map<String,List<Commit<SubscribeCommand>>> subscribeMap = new HashMap<>();

    private Map<String,Set<ServerSession>> subscribeSessionMap = new HashMap<>();


    public SSFURL register(Commit<RegisterCommand> commit) {
        SSFURL ssfurl = commit.operation().ssfurl();
        ssfurl.setStartTime(System.currentTimeMillis());
        String key = buildKey(ssfurl);
        List<Commit<RegisterCommand>> providerList = rcMap.get(key);
        if (providerList == null) {
            providerList = new ArrayList<>();
        }
        providerList.add(commit);
        rcMap.put(key, providerList);
        Set<ServerSession> subscribedSessions = subscribeSessionMap.get(buildKey(ssfurl));
        if (subscribedSessions != null){
            subscribedSessions.parallelStream().forEach(session -> session.publish("provider.add", ssfurl));
        }
        return ssfurl;
    }

    public boolean unRegister(Commit<UnRegisterCommand> commit) {
        try {
            SSFURL ssfurl = commit.operation().ssfurl();
            Commit<RegisterCommand> registeredCommand = findRegisteredCommand(ssfurl);
            if (registeredCommand == null){
                return false;
            }
            try {
                registeredCommand.close();
                Set<ServerSession> subscribedSessions = subscribeSessionMap.get(buildKey(ssfurl));
                if (subscribedSessions != null){
                    subscribedSessions.parallelStream().forEach(session -> session.publish("provider.remove", ssfurl));
                }
            } catch (Exception e) {
                logger.error("close RegisterCommand error", e);
                return false;
            }
        } catch (Throwable e){
            e.printStackTrace();
            return false;
        } finally {
            commit.close();
        }
        return true;
    }


    public SubscribeURL subscribe(Commit<SubscribeCommand> commit){
        SubscribeURL subscribeURL = commit.operation().subscribeURL();
        String key = buildKey(subscribeURL.getSourceURL());
        List<Commit<SubscribeCommand>> subscribeList = subscribeMap.get(key);
        Set<ServerSession> subscribedSessions = subscribeSessionMap.get(key);
        if (subscribeList == null) {
            subscribeList = new ArrayList<>();
            subscribeMap.put(key, subscribeList);
        }
        subscribeList.add(commit);
        if (subscribedSessions == null){
            subscribedSessions = new HashSet<>();
            subscribeSessionMap.put(key, subscribedSessions);
        }
        subscribedSessions.add(commit.session());
        subscribeURL.setProviderList(getProviderList(subscribeURL.getSourceURL()));
        return subscribeURL;
    }

    public boolean unSubscribe(Commit<UnSubscribeCommand> commit) {
        try {
            SubscribeURL subscribeURL = commit.operation().subscribeURL();
            Commit<SubscribeCommand> subscribedCommand = findSubscribedCommand(subscribeURL);
            removeSubscribeSession(subscribeURL,commit.session());
            if (subscribedCommand == null) {
                return false;
            }
            subscribedCommand.close();
        } catch (Throwable e){
            logger.error("close SubscribeCommand error", e);
            return false;
        } finally {
            commit.close();
        }
        return true;
    }

    @Override
    public void register(ServerSession session) {
        logger.info("register session:{}",session.id());
    }

    @Override
    public void unregister(ServerSession session) {
        logger.info("unregister session:{}",session.id());
    }

    @Override
    public void expire(ServerSession session) {
        logger.info("expire session:{}",session.id());
    }

    @Override
    public void close(ServerSession session) {
        logger.info("close session:{}",session.id());
        subscribeSessionMap.forEach( (key,set) -> {
            Iterator<ServerSession> iterator = set.iterator();
            while (iterator.hasNext()){
                ServerSession subscribeSession = iterator.next();
                if (session.equals(subscribeSession)){
                    logger.info("remove session:{}",subscribeSession.id());
                    iterator.remove();
                }
            }
        });
    }

    private String buildKey(SSFURL ssfurl){
        return ssfurl.getProtocol()+"#"+ssfurl.getServiceName()+"#"+ssfurl.getAlias();
    }

    private Commit<RegisterCommand> findRegisteredCommand(SSFURL ssfurl){
        String key = buildKey(ssfurl);
        List<Commit<RegisterCommand>> providerList = rcMap.get(key);
        if (providerList == null) {
            return null;
        }
        Iterator<Commit<RegisterCommand>> iterator = providerList.iterator();
        while (iterator.hasNext()){
            Commit<RegisterCommand> registerCommandCommit = iterator.next();
            if (ssfurl.equals(registerCommandCommit.operation().ssfurl())){
                iterator.remove();
                return registerCommandCommit;
            }
        }
        return null;
    }

    private Commit<SubscribeCommand> findSubscribedCommand(SubscribeURL subscribeURL){
        String key = buildKey(subscribeURL.getSourceURL());
        List<Commit<SubscribeCommand>> subscribedList = subscribeMap.get(key);
        if (subscribedList == null) {
            return null;
        }
        Iterator<Commit<SubscribeCommand>> iterator = subscribedList.iterator();
        while (iterator.hasNext()){
            Commit<SubscribeCommand> subscribeCommandCommit = iterator.next();
            if (subscribeURL.equals(subscribeCommandCommit.operation().subscribeURL())){
                iterator.remove();
                return subscribeCommandCommit;
            }
        }
        return null;
    }


    private List<SSFURL> getProviderList(SSFURL ssfurl){
        String key = buildKey(ssfurl);
        List<Commit<RegisterCommand>> providerList = rcMap.get(key);
        if (providerList == null){
            return null;
        }
        return providerList.stream().filter( registerCommit -> registerCommit.operation() != null &&
                registerCommit.operation().ssfurl() != null)
                .map( registerCommit -> registerCommit.operation().ssfurl())
        .collect(Collectors.toList());
    }

    private void removeSubscribeSession(SubscribeURL subscribeURL,ServerSession serverSession){
        String key = buildKey(subscribeURL.getSourceURL());
        Set<ServerSession> subscribeSessions = subscribeSessionMap.get(key);
        if (subscribeSessions != null){
            Iterator<ServerSession> iterator = subscribeSessions.iterator();
            while (iterator.hasNext()){
                ServerSession subscribedSession = iterator.next();
                if (serverSession.equals(subscribedSession)){
                    iterator.remove();
                    logger.info("remove subscribe session:{}",subscribedSession.id());
                }
            }
        }
    }
}
