package com.SCM.chartandFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Component
public class PdfUtils {
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Value("${pdf.directory}")
    private String pdfDirectory;
	
	final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());
	
	public byte[] generatePDF(String templateName, Map<String, Object> attributeMap) throws Exception {
		
		Assert.notNull(templateName,"The templateName can not be null..");
		Context ctx = new Context();
		ctx.setVariables(attributeMap);
		
		String processdHtml = templateEngine.process(templateName, ctx);
		ByteArrayOutputStream pdfDataStream = null;
		byte[] pdfData = null;
		
		try {
			
			pdfDataStream = new ByteArrayOutputStream();
			ITextRenderer renderer = new ITextRenderer();
			renderer.setDocumentFromString(processdHtml);
			renderer.layout();
			renderer.createPDF(pdfDataStream,false);
			renderer.finishPDF();
			pdfData = pdfDataStream.toByteArray();
			logger.debug("PDF created Successfully");
			
		}finally {
			if(pdfDataStream != null) {
				try {
					pdfDataStream.close();
				}catch(IOException e) {e.printStackTrace();}
			}
		}
		
		return pdfData;
	}
	


}
