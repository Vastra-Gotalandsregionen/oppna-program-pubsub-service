
<td id="wikicontent" class="psdescription">
  <p>
    This project aims to implement PubSubHubbub in a Java application, targeted for deployment on typical Servlet engines like Apache Tomcat. It also supports additional protocols, such as streaming APIs through a pluggable interface. 
  </p>
  <p>
    The core of the project is a publish-subscribe engine. In many ways, it is similar to other similar engine, e.g. JMS publish-subscribe brokers. It does have some differences, the primary one being that published events are collections and subscribers can opt to only use parts of this collection. For example, in the case of PubSubHubbub, a subscriber will only receive updated entries. Events are published on topics, described as an URI for the source data. In the case of PubSubHubbub, the URI is the location of the Atom or RSS2 feed. 
  </p>
  <p>
    In addition to the publish-subscribe engine, there are two other major types of components: 
  </p>
  <ul>
    <li>
      Publishers: implements the publishing end of a protocol, e.g. PubSubHubbub or the Twitter Streaming API and publishes events on the pubsub-engine.  
    </li>
    <li>
      Subscribers: implements a subscriber for a specific protocol, e.g. PubSubHubbub or Websockets. Will notify the publish-subscribe engine of topics it will subscribe to, and will then receive published events.  
    </li>
  </ul>
  <p>
  </p>
  <p>
    Components can listen for subscriber changes, e.g. when a subscriber subscribes to a topic. This can typically be used to enable dynamic publishers which only acts when a subscriber is active. 
  </p>
  <p>
    The following picture shows an a high level overview. 
  </p>
  <p>
    <img src="https://github.com/Vastra-Gotalandsregionen/oppna-program-pubsub-service/wiki/images/pubsub-overview.png"/>
  </p>
</td>
