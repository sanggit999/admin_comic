package sanggit999.example.admin_comic.singleton;

import sanggit999.example.admin_comic.model.User;

public class UserDataSingleton {
    private  static  UserDataSingleton instance;
    private User user;

    public UserDataSingleton() {
    }

    public static UserDataSingleton getInstance() {
        if(instance ==null){
            instance  = new UserDataSingleton();
        }
        return instance;
    }

    public static void setInstance(UserDataSingleton instance) {
        UserDataSingleton.instance = instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
