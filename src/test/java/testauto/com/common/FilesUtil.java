package testauto.com.common;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.extern.java.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
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

    public static String readFileTextContent(String filePath) throws IOException {
        StringBuilder builder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line).append(System.lineSeparator());
            }
        }catch (IOException e){
            LogUtil.logAndRethrow("Error reading '" + filePath + "'.", FilesUtil.class, e);
        }
        return builder.toString();
    }

    private static Workbook getWorkbook(FileInputStream inputStream, String filePath) throws IOException {
        String loweredFilePath = filePath.toLowerCase();
        if(loweredFilePath.endsWith(".xls")){
            return new HSSFWorkbook(inputStream);
        }else if(loweredFilePath.endsWith(".xlsx")){
            return new XSSFWorkbook(inputStream);
        }else{
            throw new IllegalArgumentException("'" + filePath + "' is not a supported file type. Supported types include '.xls' and '.xlsx'");
        }
    }

    private static int getColumnIndex(String columnName, Row columnHeadings){
        int columnIndex = -1;

        for(Cell cell: columnHeadings){
            String currentCellAsString = new DataFormatter().formatCellValue(cell);
            if(currentCellAsString.equalsIgnoreCase(columnName.strip())){
                return cell.getColumnIndex();
            }
        }

        if(columnIndex == -1){
            throw new NoSuchElementException("Column '" + columnName + "' not found.");
        }

        return columnIndex;
    }

    public static String getCellValue(String filePath,String sheetName, String columnName, int row) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(filePath)){
            Workbook workbook = getWorkbook(inputStream, filePath);
            Sheet sheet = Objects.requireNonNull(workbook.getSheet(sheetName), String.format("Sheet '%s' not found in '%s'.", sheetName, filePath));
            Row columnHeadings = sheet.getRow(0);
            Row targetRow = Objects.requireNonNull(sheet.getRow(row), "Row " + row + " does not exist.");
            Cell cell = targetRow.getCell(getColumnIndex(columnName, columnHeadings));
            if(cell == null){
                return "";
            }
            return new DataFormatter().formatCellValue(cell);
        }catch (IOException e){
            LogUtil.logAndRethrow("Error reading excel file '" + filePath + "'.", FilesUtil.class, e);
            return "";
        }
    }

    public static void writeCellValue(String filePath,String sheetName, String columnName, int row, String data) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(filePath)){
            Workbook workbook = getWorkbook(inputStream, filePath);
            Sheet sheet = Objects.requireNonNull(workbook.getSheet(sheetName),"'" + sheetName + "' not found.");
            Row headings = Objects.requireNonNull(sheet.getRow(0),"No column headings found the sheet '" + sheetName + "'.");
            int columnIndex = getColumnIndex(columnName, headings);

            Row targetRow = sheet.getRow(row);

            if(targetRow == null){
                targetRow = sheet.createRow(row);
            }

            Cell cell = targetRow.getCell(columnIndex);

            if(cell == null){
                cell = targetRow.createCell(columnIndex);
            }

            cell.setCellValue(data);

            try(FileOutputStream outputStream = new FileOutputStream(filePath)){
                workbook.write(outputStream);
            }
            workbook.close();
        }catch (IOException e){
            String errorMessage = "Error while updating row '" + row + "' from sheet '" + sheetName + "'" + " in workbook '" + filePath +"'.";
            LogUtil.logAndRethrow(errorMessage, FilesUtil.class, e);
        }
    }

}
