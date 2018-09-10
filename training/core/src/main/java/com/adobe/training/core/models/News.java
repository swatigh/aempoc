package com.adobe.training.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class News {
	@XmlElement(name = "item")
	private List<NewsItem> list = new ArrayList<NewsItem>();

	public List<NewsItem> getList() {
		return list;
	}

	public void setList(List<NewsItem> list) {
		this.list = list;
	}

}
