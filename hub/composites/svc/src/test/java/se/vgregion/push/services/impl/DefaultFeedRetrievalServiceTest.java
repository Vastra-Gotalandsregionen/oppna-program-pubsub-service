/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.push.services.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class DefaultFeedRetrievalServiceTest {

    private static final File TMP = new File("target/test-tmp");
    private static final File FEEDS = new File("target/test-feeds");
    
    private DefaultFeedRetrieverService service = new DefaultFeedRetrieverService(FEEDS);
    private LocalTestServer server = new LocalTestServer(null, null);
    private HttpEntity testEntity;
    
    @Before
    public void before() throws Exception {
        testEntity = new StringEntity("hello world");
        
        deleteDir(TMP);
        deleteDir(FEEDS);

        Assert.assertTrue("Failed to create " + TMP.getAbsolutePath(), TMP.mkdirs());
        Assert.assertTrue("Failed to create " + FEEDS.getAbsolutePath(), FEEDS.mkdirs());

        service.setFeedDirectory(FEEDS);
        service.setFeedDirectory(TMP);

        server.start();
    }
    
    private URI buildTestUrl(String path) throws URISyntaxException {
        return new URI("http://" + server.getServiceHostName() + ":" + server.getServicePort() + path);
    }
    
    @Test
    public void simpleRetrieval() throws Exception {
        server.register("/*", new HttpRequestHandler() {

            @Override
            public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException,
                    IOException {
                response.setEntity(testEntity);
            }});
        
        File downloaded = service.retrieve(buildTestUrl("/test"));
        
        Assert.assertTrue(downloaded.exists());
        Assert.assertEquals(testEntity.getContentLength(), downloaded.length());
    }

    @Test(expected=IOException.class)
    public void nonExistingFeed() throws Exception {
        server.register("/*", new HttpRequestHandler() {

            @Override
            public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException,
                    IOException {
                response.setStatusCode(404);
            }});
        
        
        service.retrieve(buildTestUrl("/test"));
    }

    
    @After
    public void after() {
        deleteDir(TMP);
        deleteDir(FEEDS);
    }
    
    private void deleteDir(File dir) {
        if(dir.exists()) {
            for(File file : dir.listFiles()) {
                Assert.assertTrue(file.delete());
            }
            Assert.assertTrue(dir.delete());
        }        
    }
}