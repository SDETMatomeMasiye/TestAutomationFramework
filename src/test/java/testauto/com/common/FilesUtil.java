package testauto.com.common;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class FilesUtil {

    private static void checkIfNullOrEmpty(Map<String, String> fields){
        for(Map.Entry<String, String> entry: fields.entrySet()){
            String field = entry.getKey();
            String fieldValue = entry.getValue();
            if(fieldValue == null || fieldValue.isBlank()){
                throw new IllegalArgumentException("'" + field + "' cannot be null or empty / blank.");
            }
        }
    }

    public static String getPropertyValue(String propertiesFilePath, String targetProperty) throws IOException {
        Properties properties = new Properties();
        HashMap<String, String> fields = new HashMap<>();
        fields.put("propertiesFilePath",propertiesFilePath);
        fields.put("targetProperty", targetProperty);
        checkIfNullOrEmpty(fields);
        try(FileReader fileReader = new FileReader(propertiesFilePath)){
            properties.load(fileReader);
            if(!properties.containsKey(targetProperty))
                throw new NoSuchElementException("No property key '" + targetProperty +  "' exists in '" + propertiesFilePath + "'.");
            String propertyValue = properties.getProperty(targetProperty);
            LogUtil.debug("Retrieved value '" + targetProperty + " = " + propertyValue + "'.", FilesUtil.class);
            return propertyValue;
        }catch (IOException e){
            LogUtil.logAndRethrow("Error while reading properties file.", FilesUtil.class, e);
        }
        return null;
    }

    public static Document getXMLDocument(String filePath) throws Exception {
        Document document = null;
        HashMap<String, String> fields = new HashMap<>();
        fields.put("filePath", filePath);
        checkIfNullOrEmpty(fields);
        try{
            File file = new File(filePath);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            document = builder.parse(file);
            document.getDocumentElement().normalize();
        }catch (Exception e){
            LogUtil.logAndRethrow("Error while parsing XML document '" + filePath +"'.", FilesUtil.class, e);
        }
        return document;
    }

    public static List<List<String>> readCsv(String filePath) throws IOException, CsvValidationException {
        List<List<String>> csvRows = new ArrayList<>();
        try(CSVReader reader = new CSVReader(new FileReader(filePath))){
            String [] row;
            while((row = reader.readNext()) != null){
                csvRows.add(Arrays.asList(row));
            }
        }catch (CsvValidationException e){
            LogUtil.logAndRethrow("Error reading file '" + filePath + "'.", FilesUtil.class, e);
        }
        return csvRows;
    }

}
