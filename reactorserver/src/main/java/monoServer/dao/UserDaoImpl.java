package monoServer.dao;

import org.springframework.stereotype.Component;

@Component("userDao111")
public class UserDaoImpl implements UserDao {
    @Override
    public String get() {
        return "moni db caozuo";
    }
}
