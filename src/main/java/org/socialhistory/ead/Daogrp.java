package org.socialhistory.ead;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Daogrp {

    private static final Logger logger = Logger.getLogger(Daogrp.class);
    private static CloseableHttpClient httpClient;

    public static String urlencode(String text) {
        return text.replaceAll("\\s", "%20").replaceAll("\\.", "%2E");
    }

    public static boolean availableOnline(String url) throws IOException {

        if (httpClient == null)
            httpClient = HttpClients.createDefault();

        final HttpHead httpHead = new HttpHead(url);
        final CloseableHttpResponse response = httpClient.execute(httpHead);
        if (response == null) return false;
        final int status = response.getStatusLine().getStatusCode();
        response.close();

        logger.info(status + " " + url);

        return status == HttpStatus.SC_OK;
    }
}