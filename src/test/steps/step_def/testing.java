package step_def;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by mitchell on 11/04/17.
 */
public class testing {
    Response resp;
    String jsonAsString = "";
    String jsonData = "";
    JSONObject fileName;
    WireMockServer wireMockRun;
    String balanceCheckUrlRegex = "/pttg/financialstatus/v1/accounts/\\d{6}/\\d{8}/dailybalancestatus*";
    String consentCheckUrkRegex = "/pttg/financialstatus/v1.*";
    WireMockServer wm = new WireMockServer(8082);
    Logger LOGGER = LoggerFactory.getLogger(testing.class);

    public void setup(String jsonData) {
        try {
          this.jsonData =  loadData(jsonData).toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        wireMockRun = wm;
        wireMockRun.start();
        WireMock.configureFor("localhost", 8082);
        LOGGER.debug("Started Wiremock Server");
        if (wireMockRun.isRunning() == true) {
            System.out.println("Wiremock is running");
        }
    }


    public void endWireMock() {
        wireMockRun.stop();
    }

    public JSONObject loadData(String file_Name) throws Exception {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("/home/mitchell/Documents/HMRC/wiremock_testStub_II/src/test/resources/account_data/" + file_Name + ".json"));
        JSONObject js = (JSONObject) obj;
        fileName = js;
        return fileName;

    }

    public void exactUrlOnly(String url) throws Exception {
        url = balanceCheckUrlRegex;

        stubFor(get(urlPathMatching(url))
                .willReturn(aResponse()
                        .withBody(jsonData)
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));
        System.out.println("Printing " + jsonData);
//        assertEquals(resp.getStatusCode(), 200);

    }

    public void consenttUrlOnly(String file, String url) throws Exception {

        loadData(file);

        stubFor(WireMock.get(urlPathMatching(consentCheckUrkRegex))
                .willReturn(aResponse()
                        .withBody(String.valueOf(loadData(file)))
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));
        System.out.println("Printing " + String.valueOf(loadData(file)));


    }


}
