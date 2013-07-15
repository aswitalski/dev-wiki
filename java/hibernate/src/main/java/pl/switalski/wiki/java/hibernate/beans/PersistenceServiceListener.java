package pl.switalski.wiki.java.hibernate.beans;

import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.springframework.stereotype.Component;

@Component
public class PersistenceServiceListener implements PostUpdateEventListener, PostInsertEventListener, PostDeleteEventListener {

    private static final long serialVersionUID = 6649142176084671706L;

    @Override
    public void onPostDelete(PostDeleteEvent event) {
		System.out.println("--> post delete");
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
		System.out.println("--> post insert");
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
		System.out.println("--> post update");
    }

}
