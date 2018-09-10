/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.training.core.schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.training.core.models.News;
import com.adobe.training.core.models.NewsItem;
import com.adobe.training.core.services.NewsService;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.JcrTagManagerFactory;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * A simple demo for cron-job like tasks that get executed regularly. It also
 * demonstrates how property values can be set. Users can set the property
 * values in /system/console/configMgr
 */
@Component(metatype = true, label = "A scheduled task", description = "Simple demo for cron-job like task with properties")
@Service(value = Runnable.class)
@Properties({ @Property(name = "scheduler.expression", value = "0/15 * * * * ?", description = "Cron-job expression"),
	@Property(name = "scheduler.concurrent", boolValue = false, description = "Whether or not to schedule this task concurrently") })
public class NewsUpdaterScheduler implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Session session;

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
	private NewsService newsService;

	@Reference
	JcrTagManagerFactory jcrTagManagerFactory;

	@Override
	public void run() {
		logger.debug("SimpleScheduledTask is now running, myParameter='{}'", myParameter);
	}

	@Property(label = "A parameter", description = "Can be configured in /system/console/configMgr")
	public static final String MY_PARAMETER = "myParameter";
	private String myParameter;

	@Activate
	protected void activate(final Map<String, Object> config) {
		configure(config);

	}

	private void configure(final Map<String, Object> config) {
		myParameter = PropertiesUtil.toString(config.get(MY_PARAMETER), null);
		logger.error("configure: myParameter='{}''", myParameter);
		createNewPages();
	}

	public void createNewPages() {
		logger.info("CreatePage >>");
		String pagePath = "/content/MySite/test11";
		String templatePath = "/apps/mysite/templates/news";

		Page newPage;
		PageManager pageManager;
		ResourceResolver resolver = null;

		try {

			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "sysadmin");

			try {
				resolver = resolverFactory.getServiceResourceResolver(param);
				session = resolver.adaptTo(Session.class);
				logger.info("session:" + session);
			} catch (Exception e1) {
				logger.error(e1.toString());
			}
			News news =newsService.getUpdatedNews();
			// create a page manager instance
			pageManager = resolver.adaptTo(PageManager.class);
			List<NewsItem> list = news.getList();
			logger.info("No of news:" + list.size());
			for (NewsItem newsItem : list) {
				// create a new page
				String filename = "News_" + +System.currentTimeMillis();
				logger.info("CreatePage >>" + filename);
				newPage = pageManager.create(pagePath, filename, templatePath, filename);
				if (newPage != null) {
					Node newNode = newPage.adaptTo(Node.class);
					Node jcrContentNode = newNode.getNode("jcr:content");
					if (jcrContentNode != null) {
						jcrContentNode.setProperty("cq:template", "/apps/mysite/templates/news");
						jcrContentNode.setProperty("sling:resourceType", "mysite/components/page/news");
						Node newcompNode = JcrUtils.getOrCreateByPath(jcrContentNode.getPath() + "/internalNewComp",
								JcrConstants.NT_UNSTRUCTURED, session);
						newcompNode.setProperty("issueDesc", newsItem.getDescription());
						logger.error("issueTitle propsave >>" + newsItem.getTitle());
						if (newsItem.getTitle() != null)
							newcompNode.setProperty("issueTitle", newsItem.getTitle().substring(0, 25) + "...");
						newcompNode.setProperty("path", newsItem.getLink());
						newcompNode.setProperty("guidUrl", newsItem.getGuid());
						newcompNode.setProperty("pubDate", newsItem.getPubDate());
						session.save();
						logger.error("CreatePage save >>");
					}
				}
			}

		} catch (Exception e) {
			logger.error("CreatePage failed >>" + e.getMessage());
			e.printStackTrace();
		}

	}

	
}
