package com.adobe.training.core.services;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.training.core.models.News;

@Component(metatype = true, label = "News Service")
@Service(NewsService.class)
public class NewsServiceImpl implements NewsService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static String url = "https://www.techmeme.com/feed.xml";

	private final static String xmlFile = "D://news.xml";


	protected void activate(Map<String, Object> properties) {
		configure(properties, "Activiated");
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		configure(properties, "Modified");
	}

	@Deactivate
	protected void deactivated(Map<String, Object> properties) {
		logger.info("#############Component (Modified) Good-bye");
	}

	protected void configure(Map<String, Object> properties, String status) {
		logger.info("#############Component (" );
	}

	public News getUpdatedNews() {
		News news = null;
		try {
			String newsStr = readNewsXml();
			writeNewsXml(newsStr);
			news = JAXB.unmarshal(new File(xmlFile), News.class);
			logger.error("no of record :" + news.getList().size());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return news;
	}

	@SuppressWarnings("finally")
	private String readNewsXml() {
		// Create an instance of HttpClient.
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet method = new HttpGet(url);
		String responseString = "";

		try {
			// Execute the method.
			HttpResponse response = client.execute(method);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + response.getStatusLine().getStatusCode());
			}
			// Read the response body.
			HttpEntity entity = response.getEntity();
			responseString = EntityUtils.toString(entity, "UTF-8");
			String s1 = "<?xml version=\"1.0\"?>";
			String s2 = "<rss version=\"2.0\">";
			responseString = responseString.replaceFirst(s1, "").replace(s2, "").replace("</rss>", "");
			System.out.println(responseString);

		} catch (Exception e) {
			logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
			return responseString;
		}
	}

	private void writeNewsXml(String responseString) {
		try {
			String s1 = "<?xml version=\"1.0\"?>";
			String s2 = "<rss version=\"2.0\">";
			responseString = responseString.replaceFirst(s1, "").replace(s2, "").replace("</rss>", "");
			logger.info(responseString);
			FileUtils.writeStringToFile(new File("D://news.xml"), responseString, Charset.defaultCharset());
		} catch (Exception e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} finally {

		}
	}
}
