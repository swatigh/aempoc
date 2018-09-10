package com.adobe.training.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUse;

public class NewsComponent extends WCMUse {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Reference
	private ResourceResolverFactory resolverFactory;
	private Session session;

	// firstName and lastName are available via Bindings
	public String getFullname() {
		return get("firstName", String.class) + " " + get("lastName", String.class);
	}

	public List<NewsItem> getNewsList() {
		List<NewsItem> news = getNewsLinkList();
		return news;
	}

	@Override
	public void activate() throws Exception {

	}

	public List<NewsItem> getNewsLinkList() {
		List<NewsItem> news = new ArrayList<NewsItem>();
		logger.info("getNewPages");
		String pagePath = "/content/MySite/test11";

		try {

			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "sysadmin");

			try {
				session = this.getResource().getResourceResolver().adaptTo(Session.class);
				logger.error("session:" + session);
			} catch (Exception e1) {
				logger.error(e1.toString());
			}

			Node landingPageNode = JcrUtils.getNodeIfExists(pagePath, session);
			logger.info("landingPageNode :" + landingPageNode);
			Iterable<Node> nodes = JcrUtils.getChildNodes(landingPageNode);

			Iterator<Node> it = nodes.iterator();
			while (it.hasNext()) {
				Node newNode = it.next();
				logger.info("getNewsLinkList :" + newNode.getName());
				String link = "http://localhost:4502/content/MySite/test11/" + newNode.getName() + ".html";
				logger.info("Link  :" + link);
				NewsItem n = new NewsItem();
				n.setLink(link);
				Node compNode = JcrUtils
						.getNodeIfExists(pagePath + "/" + newNode.getName() + "/jcr:content/internalNewComp", session);
				logger.info("getPage3 compNode :" + compNode);

				if (compNode != null && compNode.getProperty("issueTitle") != null) {
					logger.info("compNode" + compNode.getProperty("issueTitle").getValue().getString());
					n.setTitle(compNode.getProperty("issueTitle").getValue().getString());
				}
				news.add(n);
			}
		} catch (Exception e) {
			logger.error("getNewsLinkList failed >>" + e.getMessage());
			e.printStackTrace();
		}
		return news;
	}
}