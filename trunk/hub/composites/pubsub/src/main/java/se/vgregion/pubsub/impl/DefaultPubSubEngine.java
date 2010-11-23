package se.vgregion.pubsub.impl;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.transaction.annotation.Transactional;

import se.vgregion.pubsub.Feed;
import se.vgregion.pubsub.PubSubEngine;
import se.vgregion.pubsub.SubscriberTimeoutNotifier;
import se.vgregion.pubsub.Topic;
import se.vgregion.pubsub.repository.FeedRepository;
import se.vgregion.pubsub.repository.TopicRepository;

public class DefaultPubSubEngine implements PubSubEngine {

    private TopicRepository topicRepository;
    private FeedRepository feedRepository;
    private SubscriberTimeoutNotifier subscriberTimeoutNotifier = new DefaultSubscriberTimeoutNotifier();
    
    private Map<URI, Topic> topics = new ConcurrentHashMap<URI, Topic>();
    
    public DefaultPubSubEngine(TopicRepository topicRepository, FeedRepository feedRepository) {
        this.topicRepository = topicRepository;
        this.feedRepository = feedRepository;
        
        Collection<Topic> storedTopics = topicRepository.findAll();
        
        for(Topic storedTopic : storedTopics) {
            DefaultTopic defaultTopic = (DefaultTopic) storedTopic;
            
            defaultTopic.setFeedRepository(feedRepository);
            defaultTopic.setSubscriberTimeoutNotifier(subscriberTimeoutNotifier);
            topics.put(defaultTopic.getUrl(), defaultTopic);
        }
    }

    @Transactional
    public Topic getTopic(URI url) {
        return topics.get(url);
    }

    @Override
    @Transactional
    public Topic createTopic(URI url) {
        Topic topic = new DefaultTopic(url, feedRepository, subscriberTimeoutNotifier);
        topics.put(url, topic);
        
        topicRepository.persist(topic);
        return topic;
    }

    @Override
    @Transactional
    public Topic getOrCreateTopic(URI url) {
        Topic topic = getTopic(url);
        if(topic == null) {
            topic = createTopic(url);
        }
        return topic;
    }

    @Override
    @Transactional
    public void publish(URI url, Feed feed) {
        Topic topic = getOrCreateTopic(url);
        topic.publish(feed);
    }
}