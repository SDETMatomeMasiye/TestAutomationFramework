package testauto.com.common;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

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

}
