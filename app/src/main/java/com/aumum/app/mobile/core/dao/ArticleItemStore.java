package com.aumum.app.mobile.core.dao;

import com.aumum.app.mobile.core.model.ArticleItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Administrator on 18/03/2015.
 */
public class ArticleItemStore {

    public List<ArticleItem> getUpwardsList(String uri) throws Exception {
        ArrayList<ArticleItem> articleItems = new ArrayList<>();
        URL url = new URL(uri);
        URLConnection conn = url.openConnection();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(conn.getInputStream());
        NodeList nodes = doc.getElementsByTagName("entry");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            NodeList title = element.getElementsByTagName("title");
            Element titleElement = (Element) title.item(0);
            NodeList link = element.getElementsByTagName("link");
            Element linkElement = (Element) link.item(0);
            ArticleItem articleItem = new ArticleItem(
                    titleElement.getTextContent(),
                    linkElement.getAttribute("href"));
            articleItems.add(articleItem);
        }
        return articleItems;
    }
}
