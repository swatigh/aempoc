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
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.felix.scr.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
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
public class SimpleScheduledTask implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private String user = "";

	private Session session;
	private int cnt;

	@Reference
	private ResourceResolverFactory resolverFactory;

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
		CreatePage("mypage" + System.currentTimeMillis());
	}

	@SuppressWarnings("deprecation")
	public String CreatePage(String pageName) {
		logger.error("CreatePage >>");
		String pagePath = "/content/MySite/test";
		String templatePath = "/apps/mysite/templates/page-content";
		String pageTitle = "AEMHomePage";
		Page newPage;
		PageManager pageManager;

		ResourceResolver resolver = null;

		try {

			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "sysadmin");

			try {
				resolver = resolverFactory.getServiceResourceResolver(param);
				session = resolver.adaptTo(Session.class);
			} catch (Exception e1) {
				// TODO Auto-generated catch block

				logger.error(e1.toString());
			}

			// create a page manager instance
			pageManager = resolver.adaptTo(PageManager.class);

			// create a new page
			newPage = pageManager.create(pagePath, pageName, templatePath, pageTitle);
			if (newPage != null) {
				user = resolver.getUserID();
				Node newNode = newPage.adaptTo(Node.class);
				Node cont = newNode.getNode("jcr:content");
				if (cont != null) {
					cont.setProperty("cq:template", "/apps/mysite/templates/page-content");
					cont.setProperty("sling:resourceType", "mysite/components/page-content");
					Node rootNode = session.getRootNode();
					String path = rootNode.getPath();
					Node parNode = JcrUtils.getNodeIfExists(cont, "en");
					logger.error("CreatePage1 :" + parNode);
					Node imageNode = JcrUtils.getOrCreateByPath(parNode.getPath() + pageName,
							JcrConstants.NT_UNSTRUCTURED, session);
					Node textNode = JcrUtils.getNodeIfExists(parNode, "text");

					parNode.setProperty("text", "<p>This page is created using page manager</p>");
					session.save();
					logger.error("CreatePage save >>");
				}
			}

			return pageName;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("CreatePage failed >>" + e.getMessage());
			e.printStackTrace();
		}

		return "";

	}
}
