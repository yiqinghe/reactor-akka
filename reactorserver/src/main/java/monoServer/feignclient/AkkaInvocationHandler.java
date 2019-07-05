package monoServer.feignclient;

import monoServer.dao.UserDao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AkkaInvocationHandler implements InvocationHandler{


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("111");
        return null;
    }


    public static void main(String[] args) {
        UserDao userDao = (UserDao) Proxy.newProxyInstance(UserDao.class.getClassLoader(), new Class[]{UserDao.class}
                , new AkkaInvocationHandler());
        userDao.get();
    }
}
