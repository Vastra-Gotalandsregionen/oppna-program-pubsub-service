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

package se.vgregion.push.repository.jpa;

import java.net.URI;
import java.util.List;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import se.vgregion.portal.core.infrastructure.persistence.jpa.JpaRepository;
import se.vgregion.push.repository.SubscriptionRepository;
import se.vgregion.push.types.Subscription;
    
@Repository
public class JpaSubscriptionRepository extends JpaRepository<Subscription, Long> implements SubscriptionRepository {
    
    public JpaSubscriptionRepository() {
        super(Subscription.class);
    }

    @SuppressWarnings("unchecked")
    public List<Subscription> findByTopic(URI topic) {
        try {
            return entityManager.createQuery("select l from Subscription l where l.topic = :topic")
                .setParameter("topic", topic.toString()).getResultList();
        } catch(NoResultException e) {
            return null;
        }
    }   
}