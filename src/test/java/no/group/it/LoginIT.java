package no.group.it;

import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginIT {


    private static final Logger log = LoggerFactory.getLogger(LoginIT.class);
    

    @Test
    public void testLoginToApp() throws Exception {

        CloseableHttpClient client = HttpClientBuilder.create()
            .setRedirectStrategy(new LaxRedirectStrategy()).build();
        
        Executor executor = Executor.newInstance(client);
        String response = executor.execute(Request.Get("http://localhost:8080/dalist")
                .connectTimeout(1000)
                .socketTimeout(1000)).returnContent().asString();

        Document doc = Jsoup.parse(response);
        Elements hiddens = doc.select("input[type='hidden']");

        String csrfName = "";
        String csrfValue = "";

        for (Element element : hiddens) {
            String lvalue = element.attr("value");

            if (lvalue.matches("^\\p{XDigit}{8}-(\\p{XDigit}{4}-){3}\\p{XDigit}{12}")) {
                csrfValue = element.attr("value");
                csrfName = element.attr("name");
                log.info("{} - {}", csrfName, csrfValue);
                break;
            }
            
        }

        String response2 = executor.execute(Request.Post("http://localhost:8080/dalist/login")
                .useExpectContinue()
                .bodyForm(Form.form()
                        .add("username", "user")
                        .add("password", "pass")
                        .add(csrfName, csrfValue).build()))
                .returnContent()
                .asString();

        doc = Jsoup.parse(response2);
        Element hidden = doc.select("input[name=_csrf]").first();
        csrfValue = hidden.attr("value");
        log.info("csrfValue: {}", csrfValue);

        log.info("response after login: {}", response2);

        String response3 = executor.execute(Request.Post("http://localhost:8080/dalist/logout")
                .useExpectContinue()
                .bodyForm(Form.form().add("_csrf", csrfValue).build()))
                .returnContent()
                .asString();

        log.info("response 3, {}", response3);
    }
    
}
