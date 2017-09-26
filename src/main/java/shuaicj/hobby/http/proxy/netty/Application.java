package shuaicj.hobby.http.proxy.netty;

import java.util.concurrent.atomic.AtomicLong;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;

/**
 * Spring boot app.
 *
 * @author shuaicj 2017/09/21
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Scope("prototype")
    @Profile("default")
    public LoggingHandler loggingHandlerDefault() {
        return new LoggingHandler(LogLevel.INFO);
    }

    @Bean
    @Scope("prototype")
    @Profile("prod")
    public LoggingHandler loggingHandlerProd() {
        return new LoggingHandler(LogLevel.WARN);
    }

    @Bean
    public AtomicLong taskCounter() {
        return new AtomicLong();
    }
}
