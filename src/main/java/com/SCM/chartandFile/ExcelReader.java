package com.SCM.chartandFile;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.SCM.utils.Utils;  

import jakarta.servlet.http.HttpServletResponse;

public class ExcelReader {
	
	final String CONTENT_TYPE = "application/vnd/openxmlformats-officedocument.spredsheetml.sheet";
	final String FILE_EXT = ".xlsx";
	
	public static final String DATE_FORMAT = "dd-MMM-yyyy";
	public final static String DATETIME_FORMAT = "dd-MM-yyyy HH:mm";
	public final static String DEC_NUMBER_FORMAT = "0.00";
	public final static String INT_NUMBER_FORMAT = "0";
	
	public static String DATE_FIELD_PREFIX = "format:date;";
	public static String DATETIME_FIELD_PREFIX = "format:datetime;";
	public static String DEC_NUMBER_FIELD_PREFIX = "format:decnumber;";
	public static String INT_NUMBER_FIELD_PREFIX = "format:intnumber;";
	


	public void generateExcel(String[] headers, List<String[]> data, OutputStream out) throws Exception {
	    Workbook workbook = new XSSFWorkbook();
	    Sheet workSheet = workbook.createSheet();
	    int index = 0;

	    CellStyle cellStyle = workbook.createCellStyle();
	    Font font = workbook.createFont();
	    font.setBold(true);
	    cellStyle.setFont(font);

	    // Set Header
	    Row hr = workSheet.createRow(index);
	    for (int i = 0; i < headers.length; i++) {
	        Cell cell = hr.createCell(i);
	        cell.setCellStyle(cellStyle);
	        cell.setCellValue(headers[i]);
	    }

	    // Data
	    for (String[] rowData : data) {
	        Row dr = workSheet.createRow(++index);
	        for (int i = 0; i < rowData.length; i++) {
	            Cell cell = dr.createCell(i);
	            setValueWithFormat(workbook, cell, rowData[i]);
	        }
	    }

	    workbook.write(out);
	    workbook.close();
	}

	
	 public void generateExcel(HttpServletResponse response, String fileName, String[] headers, List<String[]> data) throws Exception {
	        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");

	        try (OutputStream output = response.getOutputStream()) {
	            Workbook workbook = new XSSFWorkbook();
	            Sheet sheet = workbook.createSheet();

	            // Create header row
	            Row headerRow = sheet.createRow(0);
	            for (int i = 0; i < headers.length; i++) {
	                Cell cell = headerRow.createCell(i);
	                cell.setCellValue(headers[i]);
	            }

	            // Add data rows
	            int rowIndex = 1;
	            for (String[] rowData : data) {
	                Row dataRow = sheet.createRow(rowIndex);
	                for (int i = 0; i < rowData.length; i++) {
	                    Cell cell = dataRow.createCell(i);
	                    cell.setCellValue(rowData[i]);
	                }
	                rowIndex++;
	            }

	            workbook.write(output);
	            workbook.close();
	        }
	    }

	    public List<String[]> read(byte[] data, String fileName) throws Exception {
	        List<String[]> recordList = new ArrayList<>();
	        Workbook workbook = getWorkbook(data, fileName);
	        Sheet sheet = workbook.getSheetAt(0);

	        for (Row row : sheet) {
	            int countOfCells = row.getLastCellNum() + 1;
	            String[] rowData = new String[countOfCells];

	            for (int i = 0; i < countOfCells; i++) {
	                Cell cell = row.getCell(i);
	                if (cell != null) {
	                    rowData[i] = cell.toString();
	                }
	            }

	            recordList.add(rowData);
	        }

	        workbook.close();
	        return recordList;
	    }

	    private Workbook getWorkbook(byte[] data, String fileName) throws Exception {
	        Workbook workbook = null;
	        if (fileName.toLowerCase().endsWith("xlsx")) {
	            workbook = new XSSFWorkbook(new ByteArrayInputStream(data));
	        } else if (fileName.toLowerCase().endsWith("xls")) {
	            workbook = new HSSFWorkbook(new ByteArrayInputStream(data));
	        } else {
	            throw new RuntimeException("Unsupported File type: " + fileName);
	        }
	        return workbook;
	    }
	
	    
	    
	    
	    
	    
	    private void setValueWithFormat(Workbook workbook, Cell cell, String value) {
		
		CreationHelper createHelper = workbook.getCreationHelper();
		if(value == null) {
			
			cell.setCellValue(value);
			return;
			
		}
		
		if(value.startsWith(DATE_FIELD_PREFIX)) {
			
			value = value.replace(DATE_FIELD_PREFIX, "");
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(
					createHelper.createDataFormat().getFormat(DATE_FORMAT));
			
			cell.setCellStyle(cellStyle);
		}
		
		else if(value.startsWith(DATETIME_FIELD_PREFIX)) {
			
			value = value.replace(DATETIME_FIELD_PREFIX, "");
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(
					createHelper.createDataFormat().getFormat(DATE_FORMAT));
			
			cell.setCellStyle(cellStyle);
		}
		
		else if(value.startsWith(DEC_NUMBER_FIELD_PREFIX)) {
			
			value = value.replace(DEC_NUMBER_FIELD_PREFIX, "");
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(
					createHelper.createDataFormat().getFormat(DEC_NUMBER_FORMAT));
			cell.setCellStyle(cellStyle);
			if(value != null) {
				
				cell.setCellValue(new Double(value));
				return;
				
			}
			
		}
		
		else if(value.startsWith(INT_NUMBER_FIELD_PREFIX)) {
			
			value = value.replace(INT_NUMBER_FIELD_PREFIX, "");
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(
					createHelper.createDataFormat().getFormat(INT_NUMBER_FORMAT));
			cell.setCellStyle(cellStyle);
			if(value != null) {
				
				cell.setCellValue(new Integer(value));
				return;
				
			}
		
	}
	
		cell.setCellValue(value);
	
	}

	public static String wrapDateField(Date date) {
		if(date == null)
			return null;
		return DATE_FIELD_PREFIX + Utils.dateToString(date, DATE_FORMAT);
	}
	
	public static String wrapDateTimeField(Date date) {
		if(date == null)
			return null;
		return DATETIME_FIELD_PREFIX + Utils.dateToString(date, DATETIME_FORMAT);
	}
	
	public static String wrapDecimalTimeField(Float value) {
		if(value == null)
			return null;
		return DEC_NUMBER_FIELD_PREFIX + Utils.twoDecimalValue(value);
	}
	
	public static String wrapIntegerTimeField(Integer value) {
		if(value == null)
			return null;
		return INT_NUMBER_FIELD_PREFIX + value;
	}
	
	public static String wrapNumberField(String value) {
		if(value == null)
			return null;
		return INT_NUMBER_FIELD_PREFIX + value;
	}
}
