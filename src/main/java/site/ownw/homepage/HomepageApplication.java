package site.ownw.homepage;

import java.lang.reflect.Field;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import site.ownw.homepage.common.util.AppUtil;

@SpringBootApplication
public class HomepageApplication implements ApplicationContextAware {

    public static void main(String[] args) {
        SpringApplication.run(HomepageApplication.class, args);
    }

    @Override
    @SneakyThrows
    public void setApplicationContext(@NotNull ApplicationContext applicationContext)
            throws BeansException {
        Field context = AppUtil.class.getDeclaredField("applicationContext");
        context.setAccessible(true);
        context.set(null, applicationContext);
    }
}
