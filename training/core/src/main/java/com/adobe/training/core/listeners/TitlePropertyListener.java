package com.adobe.training.core.listeners;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TitlePropertyListener implements EventListener {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Reference
	private SlingRepository repository;

	private Session session;
	private ObservationManager observationManager;
	  
    @Reference
    private ResourceResolverFactory resolverFactory;
    
	protected void activate(ComponentContext context) throws Exception {
		logger.info("*************added JCR event listener");
			
	    Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "sysadmin");
        
        ResourceResolver resourceResolver=null;
		try {
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			logger.error(e1.toString());
		}   
		observationManager = session.getWorkspace().getObservationManager();

		observationManager.addEventListener(this, Event.NODE_ADDED | Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED, "/content/MySite/en", true, null,
				new String[]{"cq:PageContent","nt:unstructured"} , true);
		logger.info("*************added JCR event listener");
	}
	protected void deactivate(ComponentContext componentContext) {
		try {
			if (observationManager != null) {
				observationManager.removeEventListener(this);
				logger.info("*************removed JCR event listener");
			}
		}
		catch (RepositoryException re) {
			logger.error("*************error removing the JCR event listener ", re);
		}
		finally {
			if (session != null) {
				session.logout();
				session = null;
			}
		}
	}
	public void onEvent(EventIterator it) {
		logger.error("*************EventIterator");
		while (it.hasNext()) {
			Event event = it.nextEvent();
			logger.error("*************Property updated: {}", event.getType());
			try {
				Property changedProperty = session.getProperty(event.getPath());
				if (changedProperty.getName().equalsIgnoreCase("jcr:title")
						&& !changedProperty.getString().endsWith("!")) {
					changedProperty.setValue(changedProperty.getString() + "!");
					logger.error("*************Property updated: {}", event.getPath());
					session.save();
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
}