package handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.lowagie.text.*;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.headerfooter.*;
import com.lowagie.text.rtf.field.*;
import com.lowagie.text.rtf.table.*;
import com.lowagie.text.rtf.style.RtfFont;

import bean.Actuacio;
import bean.InformeActuacio;
import bean.Oferta;
import bean.User;
import core.ActuacioCore;
import core.CreditCore;
import core.InformeCore;
import core.LoggerCore;
import core.OfertaCore;
import utils.MyUtils;

/**
 * Servlet implementation class DoCrearDocument
 */
@WebServlet("/CrearDocument")
public class DoCrearDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DoCrearDocument() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String idActuacio = request.getParameter("idActuacio");
		String idIncidencia = request.getParameter("idIncidencia");
		String idInforme = request.getParameter("idInforme");
		User Usuari = MyUtils.getLoginedUser(request.getSession());	   
    	String tipus = request.getParameter("tipus");
    	Connection conn = MyUtils.getStoredConnection(request);		
    	InformeActuacio informe = new InformeActuacio();
    	Actuacio actuacio = new Actuacio();
    	Oferta oferta = new Oferta();
    	try {
    		informe = InformeCore.getInformePrevi(conn, idInforme);
			actuacio = ActuacioCore.findActuacio(conn, idActuacio);			
			ActuacioCore.aprovar(conn, idActuacio, Usuari.getIdUsuari());
			ActuacioCore.actualitzarActuacio(conn, idActuacio, "Autorització generada");
			oferta = OfertaCore.findOfertaSeleccionada(conn, idInforme);
			OfertaCore.aprovarOferta(conn, idInforme, Usuari.getIdUsuari());
   			CreditCore.assignar(conn, idInforme, OfertaCore.findOfertaSeleccionada(conn, idInforme).getPlic());
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	String filePath = "";
    	if (tipus.equals("autMen")) {		
    		
			// Crear directoris si no existeixen
			File tmpFile = new File(utils.Fitxers.RUTA_BASE + "/documents/" + idIncidencia);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(utils.Fitxers.RUTA_BASE + "/documents/" + idIncidencia + "/Actuacio");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(utils.Fitxers.RUTA_BASE + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
    		
    		filePath = utils.Fitxers.RUTA_BASE + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/autoritzacio_informe_"+ idInforme +".pdf";
            try {
            	PdfReader reader = new PdfReader(utils.Fitxers.RUTA_BASE + "/base/MODELLICIMEN2v4.pdf"); // input PDF
    			PdfStamper stamper = new PdfStamper(reader,
    			  new FileOutputStream(filePath));			
    			            
                AcroFields fields = stamper.getAcroFields();
    	        PRAcroForm form = reader.getAcroForm();
    	        if (form != null) {
    		        for (Iterator<?> it = form.getFields().iterator(); it.hasNext(); ) {
    					PRAcroForm.FieldInformation field = (PRAcroForm.FieldInformation) it.next();
    					String fieldValue = fields.getField(field.getName());
    					LoggerCore.addLog("Field: " + field.getName() + ", Value: " + fieldValue, "");
    					if (fieldValue != null && !fieldValue.equals("")) {
    						
    					} else {
    						
    					}
    				}
    	        }
    	        fields.setField("tipusContracte",informe.getTipusObraFormat());
    	        fields.setField("data",getData());
    	        fields.setField("adjudicatari",oferta.getNomEmpresa());
    	        fields.setField("vec", oferta.getVecFormat());
    	        fields.setField("iva", oferta.getIvaFormat());
    	        fields.setField("plic", oferta.getPlicFormat());
    	        fields.setField("termini", oferta.getTermini());
    	        fields.setField("cifAdjudicatari", oferta.getCifEmpresa());
    	        fields.setField("nomAdjudicatari", oferta.getNomEmpresa());
    	        fields.setField("numActuacio", String.valueOf(idActuacio));
    	        fields.setField("nomTecnic", informe.getUsuari().getNomComplet());
    	        fields.setField("objecte", informe.getObjecte());
    	        fields.setField("nomCentre", actuacio.getNomCentre());
    	        stamper.close();
    	        reader.close();
                
    		} catch (DocumentException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} // output PDF
        	
                
    		Map<String, Object> variablesMap = new HashMap<String, Object>();
    		String tipusObra = informe.getTipusObraFormat(); 
    		String nomCentre = actuacio.getNomCentre();
    		String objecte = informe.getObjecte();
    		String tecnic = informe.getUsuari().getNomComplet();
    		String nomAdjudicatari = oferta.getNomEmpresa();
    		String cifAdjudicatari = oferta.getCifEmpresa();
    		String vec = oferta.getVecFormat();
    		String iva = oferta.getIvaFormat();
    		String plic = oferta.getPlicFormat();
    		String terminiExecucio = oferta.getTermini();
    		String reqContracte = "Si";
    		if (!informe.isContracte()) reqContracte = "No";
    		String data = getData();
    		variablesMap.put("contracte", tipusObra);
    		variablesMap.put("nomcentre", nomCentre); 
    		variablesMap.put("objecte", objecte);		 
    		variablesMap.put("formalitzacio", reqContracte); 
    		variablesMap.put("tecnic", tecnic);
    		variablesMap.put("numactuacio", idActuacio);
    		variablesMap.put("nomadjudicatari", nomAdjudicatari);
    		variablesMap.put("cifadjudicatari", cifAdjudicatari);
    		variablesMap.put("vec", vec);
    		variablesMap.put("iva", iva);
    		variablesMap.put("plic", plic);
    		variablesMap.put("terminiexecucio", terminiExecucio);
    		variablesMap.put("data",data);
    		
    	
    	} else {
    		try {
    		 Document doc = new Document();
    		          RtfWriter2 writer2 = RtfWriter2.getInstance(doc,
    		              new FileOutputStream("V:/PERSONAL/MAS GUILLEM/testNew.rtf"));
    		          RtfWriter2.getInstance(doc, new FileOutputStream("V:/PERSONAL/MAS GUILLEM/testOld.rtf"));
    		  
    		          filePath = "V:/PERSONAL/MAS GUILLEM/testNew.rtf";
    		         writer2.setAutogenerateTOCEntries(true);
    		 
    		          Table headerTable = new Table(3);
    		         headerTable.addCell("Test Cell 1");
    		         headerTable.addCell("Test Cell 2");
    		          headerTable.addCell("Test Cell 3");
    		          HeaderFooter header = new RtfHeaderFooter(headerTable);
    		          RtfHeaderFooterGroup footer = new RtfHeaderFooterGroup();
    		          footer
    		              .setHeaderFooter(
    		              new RtfHeaderFooter(new Phrase(
    		              "This is the footer on the title page")),
    		              com.lowagie.text.rtf.headerfooter.RtfHeaderFooter.DISPLAY_FIRST_PAGE);
    		          footer
    		              .setHeaderFooter(
    		              new RtfHeaderFooter(new Phrase(
    		              "This is a left side page")),
    		              com.lowagie.text.rtf.headerfooter.RtfHeaderFooter.DISPLAY_LEFT_PAGES);
    		          footer
    		              .setHeaderFooter(
    		              new RtfHeaderFooter(new Phrase(
    		              "This is a right side page")),
    		              com.lowagie.text.rtf.headerfooter.RtfHeaderFooter.DISPLAY_RIGHT_PAGES);
    		  
    		          doc.setHeader(header);
    		          doc.setFooter(footer);
    		  
    		          doc.open();
    		          Paragraph p = new Paragraph();
    		          p.add(new RtfTableOfContents("UPDATE ME!"));
    		          doc.add(p);
    		  
    		          p = new Paragraph("", new RtfFont("Staccato222 BT"));
    		          p.add(new Chunk("Hello! "));
    		          p.add(new Chunk("How do you do?"));
    		          doc.add(p);
    		          p.setAlignment(Element.ALIGN_RIGHT);
    		          doc.add(p);
    		  
    		          Anchor a = new Anchor("http://www.uni-klu.ac.at");
    		          a.setReference("http://www.uni-klu.ac.at");
    		          doc.add(a);
    		  
    		         // Image img = Image.getInstance("pngnow.png");
    		         // doc.add(new Chunk(img, 0, 0));
    		         // doc.add(new Annotation("Mark", "This works!"));
    		 
    		          Chunk c = new Chunk("");
    		         c.setNewPage();
    		         doc.add(c);
    		 
    		         List subList = new List(true, 40);
    		         subList.add(new ListItem("Sub list 1"));
    		         subList.add(new ListItem("Sub list 2"));
    		 
    		         List list = new List(true, 20);
    		         list.add(new ListItem("Test line 1"));
    		         list
    		             .add(new ListItem(
    		             "Test line 2 - This is a really long test line to test that linebreaks are working the way they are supposed to work so a really really long line of drivel is required"));
    		         list.add(subList);
    		         list.add(new ListItem("Test line 3 - \t\u20ac\t 60,-"));
    		         doc.add(list);
    		 
    		         list = new List(false, 20);
    		         list.add(new ListItem("Bullet"));
    		         list.add(new ListItem("Another one"));
    		         doc.add(list);
    		 
    		         doc.newPage();
    		 
    		         Chapter chapter = new Chapter(new Paragraph("This is a Chapter"), 1);
    		         chapter.add(new Paragraph(
    		             "This is some text that belongs to this chapter."));
    		         chapter.add(new Paragraph("A second paragraph. What a surprise."));
    		 
    		         Section section = chapter.addSection(new Paragraph(
    		             "This is a subsection"));
    		         section.add(new Paragraph("Text in the subsection."));
    		 
    		         doc.add(chapter);
    		 
    		         com.lowagie.text.rtf.field.RtfTOCEntry rtfTOC = new com.lowagie.text.rtf.field.RtfTOCEntry(
    		             "Table Test");
    		         doc.add(rtfTOC);
    		 
    		         Table table = new Table(3);
    		         table.setSpacing(2);
    		         table.setAlignment(Element.ALIGN_LEFT);
    		         table.setSpacing(2);
    		 
    		         Cell emptyCell = new Cell("");
    		 
    		         Cell cellLeft = new Cell("Left Alignment");
    		         cellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);
    		         Cell cellCenter = new Cell("Center Alignment");
    		         cellCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
    		         Cell cellRight = new Cell("Right Alignment");
    		         cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
    		 
    		         table.addCell(cellLeft);
    		         table.addCell(cellCenter);
    		         table.addCell(cellRight);
    		 
    		         Cell cellSpanHoriz = new Cell("This Cell spans two columns");
    		         cellSpanHoriz.setColspan(2);
    		         table.addCell(cellSpanHoriz);
    		         table.addCell(emptyCell);
    		 
    		         Cell cellSpanVert = new Cell("This Cell spans two rows");
    		         cellSpanVert.setRowspan(2);
    		         table.addCell(emptyCell);
    		         table.addCell(cellSpanVert);
    		         table.addCell(emptyCell);
    		         table.addCell(emptyCell);
    		         table.addCell(emptyCell);
    		 
    		         Cell cellSpanHorizVert = new Cell(
    		             "This Cell spans both two columns and two rows");
    		         cellSpanHorizVert.setColspan(2);
    		         cellSpanHorizVert.setRowspan(2);
    		         table.addCell(emptyCell);
    		         table.addCell(cellSpanHorizVert);
    		         table.addCell(emptyCell);
    		 
    		         RtfCell cellDotted = new RtfCell("Dotted border");
    		         cellDotted.setBorders(new RtfBorderGroup());
    		         RtfCell cellEmbossed = new RtfCell("Embossed border");
    		         RtfCell cellNoBorder = new RtfCell("No border");
    		         cellNoBorder.setBorders(new RtfBorderGroup());
    		 
    		         table.addCell(cellDotted);
    		         table.addCell(cellEmbossed);
    		         table.addCell(cellNoBorder);
    		 
    		         try {
						doc.add(table);
						for (int i = 0; i < 300; i++) {
	    		             doc.add(new Paragraph(
	    		                 "Dummy line to get multi-page document. Line "
	    		                 + (i + 1)));
	    		         }
					} catch (com.lowagie.text.DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    		 
    		 	          
    		 
    		         doc.close();
    		 	}   	
    		 catch (com.lowagie.text.DocumentException e) {
    			
    		}
    	}
				
		//Descarrega del document
	
        File downloadFile = new File(filePath);
        FileInputStream inStream = new FileInputStream(downloadFile);
                  
        // obtains ServletContext
        ServletContext context = getServletContext();
         
        // gets MIME type of the file
        String mimeType = context.getMimeType(filePath);
        if (mimeType == null) {        
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
         
        // forces download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("inline; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
         
        // obtains response's output stream
        OutputStream outStream = response.getOutputStream();
         
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
         
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
         
        inStream.close();
        outStream.close();    
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private static String getData(){
		String dataStr = "";
		Calendar today = Calendar.getInstance();
		dataStr += String.format("%02d", today.get(Calendar.DAY_OF_MONTH));
		dataStr += " de " + getMonth(today.get(Calendar.MONTH));
		dataStr += " de " + (today.get(Calendar.YEAR));
		return dataStr;
	}
	
	private static String getMonth(int month){
		switch (month) {
			case 0: 
				return "Gener";
			case 1:
				return "Febrer";
			case 2: 
				return "Març";
			case 3:
				return "Abril";
			case 4: 
				return "Maig";
			case 5:
				return "Juny";
			case 6: 
				return "Juliol";
			case 7:
				return "Agost";
			case 8: 
				return "Setembre";
			case 9:
				return "Octubre";
			case 10: 
				return "Novembre";
			case 11:
				return "Desembre";
			default:
				return "";
		}
	}

}
