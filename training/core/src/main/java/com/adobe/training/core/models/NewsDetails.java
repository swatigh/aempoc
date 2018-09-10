package com.adobe.training.core.models;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public class NewsDetails {
	@Inject
	private Node test;

	public String getTitle() throws ValueFormatException, PathNotFoundException, RepositoryException {
		return test.getProperty("title").getString();
	}

	public String getLink() throws ValueFormatException, PathNotFoundException, RepositoryException {
		return test.getProperty("link").getString();
	}

	public String getDescription() throws ValueFormatException, PathNotFoundException, RepositoryException {
		return test.getProperty("description").getString();
	}

	public String getPubDate() throws ValueFormatException, PathNotFoundException, RepositoryException {
		return test.getProperty("pubDate").getString();
	}

	public String getGuid() throws ValueFormatException, PathNotFoundException, RepositoryException {
		return test.getProperty("guid").getString();
	}

}