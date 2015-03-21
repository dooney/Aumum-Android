package com.aumum.app.mobile.utils;

import com.aumum.app.mobile.core.model.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpHost;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.auth.AuthScope;
import ch.boye.httpclientandroidlib.auth.UsernamePasswordCredentials;
import ch.boye.httpclientandroidlib.client.AuthCache;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.protocol.HttpClientContext;
import ch.boye.httpclientandroidlib.impl.auth.BasicScheme;
import ch.boye.httpclientandroidlib.impl.client.BasicAuthCache;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.util.EntityUtils;

/**
 * Created by Administrator on 21/03/2015.
 */
public class EventFinderUtils {

    private static Gson gson = new Gson();

    class EventJson {
        private List<Event> events;
    }

    public static List<Event> getList(int category) throws Exception {
        HttpHost targetHost = new HttpHost("api.eventfinda.com.au", 80, "http");
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                new UsernamePasswordCredentials("", "9yk7bj7fccbj")
        );
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);

        String url = String.format("/v2/events.json?fields=event:(url,name,description,datetime_start,address,point)&category=%d&rows=20&free=1", category);
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = httpClient.execute(targetHost, httpget, localContext);
        HttpEntity entity = response.getEntity();

        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        EntityUtils.consume(entity);
        EventJson eventJson = gson.fromJson(sb.toString(), new TypeToken<EventJson>(){}.getType());
        return eventJson.events;
    }
}
