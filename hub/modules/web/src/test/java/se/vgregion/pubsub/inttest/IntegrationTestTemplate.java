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

package se.vgregion.pubsub.inttest;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.After;
import org.junit.Before;
import org.springframework.core.io.DefaultResourceLoader;

import se.vgregion.pubsub.Feed;
import se.vgregion.pubsub.push.SubscriptionMode;


public class IntegrationTestTemplate {

    private Server server;
    protected BlockingQueue<Boolean> verifications = new LinkedBlockingQueue<Boolean>();
    protected BlockingQueue<Feed> publishedFeeds = new LinkedBlockingQueue<Feed>();
    protected Publisher publisher;
    protected URI hubUrl;
    protected Subscriber subscriber;
    
    @Before
    public void setUpComponents() throws Exception {
        String testProperties = System.getProperty("testProperties");
        if(testProperties == null) {
            testProperties = "classpath:integrationtest.properties";
        }
        
        Properties testProps = new Properties();
        testProps.load(new DefaultResourceLoader().getResource(testProperties).getInputStream());
        
        
        String localServerHost = testProps.getProperty("test.localServerHost", "localhost");
        String hubUrlString = testProps.getProperty("test.hubUrl", "http://localhost");
        
        if(testProps.getProperty("test.createServer", "true").equals("true")) {
            // we should set up our own server
            System.setProperty("testproperties", testProperties);
            
            server = new Server(0);
            
            WebAppContext context = new WebAppContext();
            context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
            context.setResourceBase("src/main/webapp");
            context.setContextPath("/");
            
            server.setHandler(context);
            
            server.start();

            hubUrl = URI.create("http://localhost:" + server.getConnectors()[0].getLocalPort() + "/push/");
        } else {
            hubUrl = URI.create(hubUrlString);
        }

        publisher = new Publisher(localServerHost);
        
        subscriber = new Subscriber(localServerHost, createSubscriberResult());
        subscriber.addListener(new SubscriberListener() {
            @Override
            public void published(Feed feed) {
                publishedFeeds.add(feed);
            }

            @Override
            public void verified() {
                verifications.add(true);                
            }
        });
        
        subscriber.subscribe(SubscriptionMode.SUBSCRIBE, hubUrl, publisher.getUrl());
        
    }
    
    protected SubscriberResult createSubscriberResult() {
        return null;
    }
    
    @After
    public void stopServer() throws Exception {
        if(server != null) {
            server.stop();
        }
    }
}
