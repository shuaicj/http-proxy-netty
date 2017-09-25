package shuaicj.hobby.http.proxy.netty;

import static org.assertj.core.api.Assertions.assertThat;

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
public class HttpProxyTest {

    @Value("${proxy.port}")
    int port;

    @Test
    public void http() throws Exception {
        assertThat(statusCodeOfRequest("http://www.baidu.com")).isEqualTo(200);
    }

    @Test
    public void https() throws Exception {
        assertThat(statusCodeOfRequest("https://github.com")).isEqualTo(200);
    }

    private int statusCodeOfRequest(String url) throws Exception {
        return Request.Get(url)
                .viaProxy(new HttpHost("127.0.0.1", port))
                .execute()
                .returnResponse()
                .getStatusLine()
                .getStatusCode();
    }
}

