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

package se.vgregion.pubsub.content;

import nu.xom.Document;
import nu.xom.Element;

import org.junit.Assert;
import org.junit.Test;

import se.vgregion.pubsub.ContentType;
import se.vgregion.pubsub.Entry;
import se.vgregion.pubsub.UnitTestConstants;
import se.vgregion.pubsub.impl.DefaultEntry.EntryBuilder;
import se.vgregion.pubsub.impl.DefaultFeed.FeedBuilder;



public class Rss2SerializerTest {

    
    @Test
    public void print() throws Exception {
        FeedBuilder builder = new FeedBuilder(ContentType.RSS);
        builder.id("f1").updated(UnitTestConstants.UPDATED1)
            .field("link", "f1")
            .field("pubDate", UnitTestConstants.UPDATED1_STR)
            .field("title", "foobar")
            .entry(new EntryBuilder().id("e1").updated(UnitTestConstants.UPDATED1)
                    .field("guid", "e1")
                    .field("pubDate", UnitTestConstants.UPDATED1_STR)
                    .field("title", "foobar").build()
                    )
            .entry(new EntryBuilder().id("e2").updated(UnitTestConstants.UPDATED2)
                    .field("guid", "e2")
                    .field("pubDate", UnitTestConstants.UPDATED2_STR)
                    .field("title", "foobar").build());
        
        Rss2Serializer serializer = new Rss2Serializer();
        Document doc = serializer.print(builder.build());
        
        Assert.assertEquals("", doc.getRootElement().getNamespaceURI());
        Assert.assertEquals("rss", doc.getRootElement().getLocalName());
        
        Element channel = doc.getRootElement().getFirstChildElement("channel");
        
        Assert.assertNotNull(channel);
        
        Assert.assertEquals("f1", channel.getFirstChildElement("link").getValue());
        Assert.assertEquals("foobar", channel.getFirstChildElement("title").getValue());
        Assert.assertEquals("2010-03-01T00:00:00.000Z", channel.getFirstChildElement("pubDate").getValue());
        
        Assert.assertEquals(2, channel.getChildElements("item").size());
        
        Element entry = channel.getChildElements("item").get(0);
        Assert.assertEquals("e1", entry.getFirstChildElement("guid").getValue());
        Assert.assertEquals("foobar", entry.getFirstChildElement("title").getValue());
        Assert.assertEquals("2010-03-01T00:00:00.000Z", entry.getFirstChildElement("pubDate").getValue());

    }

    @Test
    public void printWithFilter() throws Exception {
        FeedBuilder builder = new FeedBuilder(ContentType.RSS);
        builder.id("f1").updated(UnitTestConstants.UPDATED1)
            .field("link", "f1")
            .field("pubDate", UnitTestConstants.UPDATED1_STR)
            .field("title", "foobar")
            .entry(new EntryBuilder().id("e1").updated(UnitTestConstants.UPDATED1)
                    .field("guid", "e1")
                    .field("pubDate", UnitTestConstants.UPDATED1_STR)
                    .field("title", "foobar").build()
                    )
            .entry(new EntryBuilder().id("e2").updated(UnitTestConstants.UPDATED2)
                    .field("guid", "e2")
                    .field("pubDate", UnitTestConstants.UPDATED2_STR)
                    .field("title", "foobar").build());
        
        Rss2Serializer serializer = new Rss2Serializer();
        Document doc = serializer.print(builder.build(), new EntryFilter() {
            @Override
            public boolean include(Entry entry) {
                return entry.getEntryId().equals("e1");
            }
        });
        
        Element channel = doc.getRootElement().getFirstChildElement("channel");

        Assert.assertEquals(1, channel.getChildElements("item").size());
        
        Element entry = channel.getChildElements("item").get(0);
        Assert.assertEquals("e1", entry.getFirstChildElement("guid").getValue());
    }

}
