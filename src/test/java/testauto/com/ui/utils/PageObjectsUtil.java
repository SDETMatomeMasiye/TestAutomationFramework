package testauto.com.ui.utils;

import org.openqa.selenium.By;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import testauto.com.common.FilesUtil;
import testauto.com.common.LogUtil;

import java.util.Objects;

public class PageObjectsUtil {

    private final String filePath;
    private Document cachedDocument;

    public PageObjectsUtil(String filePath){
        this.filePath = filePath;
    }

    private Document getCachedDocument() throws Exception {
        if(cachedDocument == null){
            cachedDocument = FilesUtil.getXMLDocument(filePath);
            return cachedDocument;
        }
        return cachedDocument;
    }

    private String [] getElementXMLValues(String elementName) throws Exception {
        if(elementName == null || elementName.isBlank()) throw new IllegalArgumentException("element name cannot be blank or empty / blank.");
        Document document = getCachedDocument();
        NodeList elements = document.getElementsByTagName("element");
        String [] elementXMLValues = null;

        for(int i = 0; i < elements.getLength(); i++){
            Element element = (Element) elements.item(i);
            String name = element.getAttribute("name");
            if(name.equalsIgnoreCase(elementName)){
                elementXMLValues= new String[2];
                elementXMLValues[0] = element.getAttribute("by");
                elementXMLValues[1] = element.getTextContent();
            }
        }
        return Objects.requireNonNull(elementXMLValues, "Target element '" + elementName + "' not found in the page repository (XML).");
    }

    public By getElementBy(String element) throws Exception {
        String [] elementValues = getElementXMLValues(element);
        String locatorStrategy = elementValues[0];
        if (locatorStrategy == null || locatorStrategy.isBlank()) {
            throw new IllegalArgumentException("Locator strategy ('by') is missing for element: " + element);
        }
        String locator = elementValues[1];
        By by = switch (locatorStrategy.toLowerCase()){
            case "xpath" -> By.xpath(locator);
            case "id" -> By.id(locator);
            case "classname" -> By.className(locator);
            case "css" -> By.cssSelector(locator);
            case "name" -> By.name(locator);
            case "linktext" -> By.linkText(locator);
            case "partiallinktext" -> By.partialLinkText(locator);
            case "tagname" -> By.tagName(locator);
            default -> throw new UnsupportedOperationException("'" + locatorStrategy + "' is not a valid or supported locator strategy.");
        };
        LogUtil.info("Retrieved locator (By) for element '" + element+ "' = '" + by + "'.", PageObjectsUtil.class);
        return by;
    }
}
