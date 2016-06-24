package com.jiaxy.ssf.regcenter.server;

import com.jiaxy.ssf.common.bo.SSFURL;
import com.jiaxy.ssf.common.bo.SubscribeURL;
import com.jiaxy.ssf.regcenter.common.*;
import io.atomix.copycat.Command;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.StateMachine;
import io.atomix.copycat.server.session.ServerSession;
import io.atomix.copycat.server.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
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

    private transient Map<ServerSession,Commit<RegisterCommand>> rcSessions = new HashMap<>();

    private transient Map<ServerSession,Commit<SubscribeCommand>> subscribeSessions = new HashMap<>();


    public SSFURL register(Commit<RegisterCommand> commit) {
        //bind session
        rcSessions.put(commit.session(),commit);
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
            subscribedSessions.parallelStream().forEach(session -> session.publish(Constants.PROVIDER_ADD_EVENT, ssfurl));
        }
        return ssfurl;
    }

    public boolean unRegister(Commit<UnRegisterCommand> commit) {
        try {
            SSFURL ssfurl = commit.operation().ssfurl();
            String key = buildKey(ssfurl);
            Commit<RegisterCommand> registeredCommand = findRegisteredCommand(key,
                    command -> ssfurl.equals(command.ssfurl()));
            if (registeredCommand == null){
                return false;
            }
            try {
                registeredCommand.close();
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
        //bind session
        subscribeSessions.put(commit.session(),commit);
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
            String key = buildKey(subscribeURL.getSourceURL());
            Commit<SubscribeCommand> subscribedCommand = findSubscribedCommand(key,
                    command -> subscribeURL.equals(command.subscribeURL()));
            removeSubscribeSession(subscribeURL,subscribedCommand.session());
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
        logger.debug("register session:{}", session.id());
    }

    @Override
    public void unregister(ServerSession session) {
        logger.debug("unregister session:{}", session.id());
    }

    @Override
    public void expire(ServerSession session) {
        logger.debug("expire session:{}", session.id());
    }

    @Override
    public void close(ServerSession session) {
        logger.debug("close session:{}", session.id());
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
        //close expired commit
        closeExpiredCommit(session ,
                s -> rcSessions.get(s),
                (Command c) -> findRegisteredCommand
                        (buildKey(((RegisterCommand) c).ssfurl()),
                                registerCommand -> registerCommand.ssfurl().equals(((RegisterCommand) c).ssfurl())));
        closeExpiredCommit(session ,s -> subscribeSessions.get(s),(Command c) -> findSubscribedCommand
                (buildKey(((SubscribeCommand) c).subscribeURL().getSourceURL()),
                        subscribeCommand -> subscribeCommand.subscribeURL().equals(((SubscribeCommand) c).subscribeURL())));
    }

    private String buildKey(SSFURL ssfurl){
        return ssfurl.getProtocol()+"#"+ssfurl.getServiceName()+"#"+ssfurl.getAlias();
    }

    private String buildSubscribeKey(SSFURL ssfurl){
        return ssfurl.getIp()+"#"+ssfurl.getPid();
    }

    private Commit<RegisterCommand> findRegisteredCommand(String key,Predicate<RegisterCommand> predicate){
        List<Commit<RegisterCommand>> providerList = rcMap.get(key);
        if (providerList == null) {
            return null;
        }
        Iterator<Commit<RegisterCommand>> iterator = providerList.iterator();
        while (iterator.hasNext()){
            Commit<RegisterCommand> registerCommandCommit = iterator.next();
            if (predicate.test(registerCommandCommit.operation())){
                iterator.remove();
                logger.info("remove register provider:{}",registerCommandCommit.operation().ssfurl());
                Set<ServerSession> subscribedSessions = subscribeSessionMap.get(key);
                if (subscribedSessions != null){
                    subscribedSessions.parallelStream().forEach(session -> session.publish(Constants.PROVIDER_REMOVE_EVENT, registerCommandCommit.operation().ssfurl()));
                }
                return registerCommandCommit;
            }
        }
        return null;
    }

    private Commit<SubscribeCommand> findSubscribedCommand(String key,Predicate<SubscribeCommand> predicate){
        List<Commit<SubscribeCommand>> subscribedList = subscribeMap.get(key);
        if (subscribedList == null) {
            return null;
        }
        Iterator<Commit<SubscribeCommand>> iterator = subscribedList.iterator();
        while (iterator.hasNext()){
            Commit<SubscribeCommand> subscribeCommandCommit = iterator.next();
            if (predicate.test(subscribeCommandCommit.operation())){
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


    private void closeExpiredCommit(ServerSession serverSession, Function<ServerSession,Commit> function,Consumer<Command> consumer){
        Optional<Commit> optional;
        optional = Optional.ofNullable(function.apply(serverSession));
        optional.ifPresent(c -> {
            consumer.accept((Command)c.operation());
            logger.info("close {} for closed session:{}",c,serverSession);
        });
    }
}
