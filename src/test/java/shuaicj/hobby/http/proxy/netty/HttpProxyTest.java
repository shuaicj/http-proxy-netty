package shuaicj.hobby.http.proxy.netty;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Mock a http client and test.
 *
 * @author shuaicj 2017/09/21
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class HttpProxyTest {

    @Value("${proxy.port}")
    int port;

    @Test
    public void http() throws Exception {
        request("http://www.baidu.com");
    }

    @Test
    public void https() throws Exception {
        request("https://github.com");
    }

    private void request(String url) throws Exception {
        logger.info("check this: {}",
                Request.Get(url)
                        .viaProxy(new HttpHost("127.0.0.1", port))
                        .execute()
                        .returnContent());
    }
}
