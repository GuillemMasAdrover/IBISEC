package handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import bean.Actuacio;
import bean.Empresa;
import bean.Expedient;
import bean.InformeActuacio;
import bean.Oferta;
import bean.User;
import core.ActuacioCore;
import core.CreditCore;
import core.EmpresaCore;
import core.InformeCore;
import core.UsuariCore;
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
    		if (idInforme.contains("-MOD-")) {
    			informe = InformeCore.getMoficacioInforme(conn, idInforme);
    		} else {
    			informe = InformeCore.getInformePrevi(conn, idInforme, false);
    		}    		
			actuacio = ActuacioCore.findActuacio(conn, idActuacio);	
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String filePath = "";
    	File downloadFile = null;
    	Context env;
    	String ruta = "";
		try {
			env = (Context)new InitialContext().lookup("java:comp/env");
			ruta = (String)env.lookup("ruta_base");
		} catch (NamingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	if (tipus.equals("autMen")) {	
			// Crear directoris si no existeixen
			File tmpFile = new File(ruta + "/documents/" + idIncidencia);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
    		
    		filePath = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/autoritzacio_informe_"+ idInforme +".pdf";
            try {
            	PdfReader reader = new PdfReader(ruta + "/base/MODELLICIMEN2v5.pdf"); // input PDF
    			PdfStamper stamper = new PdfStamper(reader,
    			  new FileOutputStream("/autoritzacio_informe_"+ idInforme +".pdf"));			
    			            
                AcroFields fields = stamper.getAcroFields();    	        
    	        fields.setField("tipusContracte",informe.getPropostaInformeSeleccionada().getTipusObraFormat());
    	        fields.setField("data",getData());
    	        fields.setField("adjudicatari","Adjudica l'execució d'aquesta actuació a " + informe.getOfertaSeleccionada().getNomEmpresa());
    	        fields.setField("pbase", informe.getOfertaSeleccionada().getPbaseFormat());
    	        fields.setField("iva", informe.getOfertaSeleccionada().getIvaFormat());
    	        fields.setField("plic", informe.getOfertaSeleccionada().getPlicFormat());
    	        fields.setField("termini", informe.getOfertaSeleccionada().getTermini());
    	        fields.setField("cifAdjudicatari", informe.getOfertaSeleccionada().getCifEmpresa());
    	        fields.setField("nomAdjudicatari", informe.getOfertaSeleccionada().getNomEmpresa());
    	        fields.setField("numActuacio", String.valueOf(idActuacio));
    	        fields.setField("nomTecnic", informe.getUsuari().getNomComplet());
    	        fields.setField("objecte", informe.getPropostaInformeSeleccionada().getObjecte());
    	        fields.setField("nomCentre", actuacio.getCentre().getNomComplet());
    	        stamper.close();
    	        reader.close();
                
    		} catch (DocumentException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} // output PDF
            downloadFile = new File("/autoritzacio_informe_"+ idInforme +".pdf");
       } else if ("PAObres".equals(tipus)) {
    		// Crear directoris si no existeixen
			File tmpFile = new File(ruta + "/documents/" + idIncidencia);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
    		
    		filePath = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/proposta_actuacio_"+ idInforme +".pdf";
            try {
            	
	        	PdfReader reader = new PdfReader(ruta + "/base/PA tècnic.pdf"); // input PDF
				PdfStamper stamper = new PdfStamper(reader,
				  new FileOutputStream("/proposta_actuacio_"+ idInforme +".pdf"));			
				            
	            AcroFields fields = stamper.getAcroFields();
	            fields.setField("informe",informe.getIdInf());
    	        fields.setField("tipusContracte",informe.getLlistaPropostes().get(0).getTipusObraFormat());
    	        fields.setField("nomCentre",actuacio.getCentre().getNomComplet());
    	        fields.setField("objecte",informe.getLlistaPropostes().get(0).getObjecte());
    	        fields.setField("pbase", informe.getLlistaPropostes().get(0).getPbaseFormat());
    	        fields.setField("iva", informe.getLlistaPropostes().get(0).getIvaFormat());
    	        fields.setField("plic", informe.getLlistaPropostes().get(0).getPlicFormat());
    	        fields.setField("termini", informe.getLlistaPropostes().get(0).getTermini());
    	        fields.setField("idActuacio", actuacio.getReferencia());
    	        fields.setField("tecnic", Usuari.getNomCompletReal());
    	        fields.setField("comentaris", informe.getLlistaPropostes().get(0).getComentari());
    	        String llicencia = "No";
    	        if (informe.getLlistaPropostes().get(0).isLlicencia()) llicencia = "Si";
    	        fields.setField("llicencia", llicencia);
    	        fields.setField("tipusLlicencia", informe.getLlistaPropostes().get(0).getTipusLlicencia());
    	        String contracte = "No";
    	        if (informe.getLlistaPropostes().get(0).isContracte()) contracte = "Si";
    	        fields.setField("contracte", contracte);
    	        stamper.close();
    	        reader.close();    		                
    		} catch (DocumentException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} // output PDF
            downloadFile = new File("/proposta_actuacio_"+ idInforme +".pdf");
       } else if ("VistiplauPAObres".equals(tipus)) {
   		// Crear directoris si no existeixen
			File tmpFile = new File(ruta + "/documents/" + idIncidencia);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
   		
			filePath = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/vistiplau_proposta_actuacio_"+ idInforme +".pdf";
			try {
	        	PdfReader reader = new PdfReader(ruta + "/base/PA cap de servei.pdf"); // input PDF
				PdfStamper stamper = new PdfStamper(reader,
				  new FileOutputStream("/vistiplau_proposta_actuacio_"+ idInforme +".pdf"));			
				            
	            AcroFields fields = stamper.getAcroFields();
	            fields.setField("informe",informe.getIdInf());
	        	fields.setField("capServei", Usuari.getNomCompletReal());
	   	       	fields.setField("observacions", informe.getComentariCap());	   	       
	   	       	stamper.close();
	   	       	reader.close();
   		                
	   		} catch (DocumentException e1) {
	   			// TODO Auto-generated catch block
	   			e1.printStackTrace();
	   		} // output PDF
			downloadFile = new File("/vistiplau_proposta_actuacio_"+ idInforme +".pdf");
       } else if ("financeraPAObres".equals(tipus)) {
      		// Crear directoris si no existeixen
   			File tmpFile = new File(ruta + "/documents/" + idIncidencia);
   			if (!tmpFile.exists()) {
   				tmpFile.mkdir();
   			}
   			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
   			if (!tmpFile.exists()) {
   				tmpFile.mkdir();
   			}
   			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
   			if (!tmpFile.exists()) {
   				tmpFile.mkdir();
   			}
      		
   			filePath = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/financera_proposta_actuacio_"+ idInforme +".pdf";
   			try {
   	        	PdfReader reader = new PdfReader(ruta + "/base/PA àrea econòmica.pdf"); // input PDF
   				PdfStamper stamper = new PdfStamper(reader,
   				  new FileOutputStream("/financera_proposta_actuacio_"+ idInforme +".pdf"));			
   				            
   	            AcroFields fields = stamper.getAcroFields();
   	            fields.setField("informe",informe.getIdInf());
   	        	fields.setField("capFinancera", Usuari.getNomCompletReal());
   	        	fields.setField("partida", informe.getAssignacioCredit().getPartida().getNom());	   	       
   	   	       	fields.setField("observacions", CreditCore.getComentariPartida(conn, idInforme));	
   	   	       	String resolucio = "El cap de l'àrea econòmica financera dóna el seu vistiplau a l'informe i procedeix a l'assignació de la partida pressupostària";
	   	        if (informe.getDataRebujada() != null) {
	   	        	resolucio = "El cap de l'àrea econòmica financera no dóna el seu vistiplau a l'informe";
	   	        	fields.setField("observacions", informe.getPartidaRebutjadaMotiu());	
   	   	       	}
   	   	       	fields.setField("resolucio", resolucio);
   	   	       	stamper.close();
   	   	       	reader.close();
      		                
   	   		} catch (DocumentException | SQLException e1) {
   	   			// TODO Auto-generated catch block
   	   			e1.printStackTrace();
   	   		} // output PDF
   			downloadFile = new File("/financera_proposta_actuacio_"+ idInforme +".pdf");
       } else if ("financeraMajor".equals(tipus)) {
    	   // Crear directoris si no existeixen
  			File tmpFile = new File(ruta + "/documents/" + idIncidencia);
  			if (!tmpFile.exists()) {
  				tmpFile.mkdir();
  			}
  			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
  			if (!tmpFile.exists()) {
  				tmpFile.mkdir();
  			}
  			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
  			if (!tmpFile.exists()) {
  				tmpFile.mkdir();
  			}
     		
  			filePath = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/certificat_existència_crèdit_"+ informe.getExpcontratacio().getExpContratacio().replace("/", "_") +".pdf";
  			try {
  				Expedient expedient = informe.getExpcontratacio();
  	        	PdfReader reader = new PdfReader(ruta + "/base/certCredit.pdf"); // input PDF
  				PdfStamper stamper = new PdfStamper(reader,
  				  new FileOutputStream("/certificat_existència_crèdit_"+ informe.getExpcontratacio().getExpContratacio().replace("/", "_") +".pdf"));			
  				            
  	            AcroFields fields = stamper.getAcroFields();
  	            fields.setField("expedient",expedient.getExpContratacio());
  	        	fields.setField("tipus", expedient.getTipus());
  	        	fields.setField("objecte", informe.getPropostaInformeSeleccionada().getObjecte());	   	       
  	   	       	fields.setField("partida", informe.getAssignacioCredit().getPartida().getCodi());	
  	   	       	fields.setField("PBase", informe.getPropostaInformeSeleccionada().getPbaseFormat());
  	   	       	fields.setField("IVA", informe.getPropostaInformeSeleccionada().getIvaFormat());
  	   	       	fields.setField("PLic", informe.getPropostaInformeSeleccionada().getPlicFormat());
  	   	       	stamper.close();
  	   	       	reader.close();
     		                
  	   		} catch (DocumentException e1) {
  	   			// TODO Auto-generated catch block
  	   			e1.printStackTrace();
  	   		} // output PDF
  			downloadFile = new File("/certificat_existència_crèdit_"+ informe.getExpcontratacio().getExpContratacio().replace("/", "_") +".pdf");
       } else if ("AutoritzacioPAObres".equals(tipus)) {
     		// Crear directoris si no existeixen
  			File tmpFile = new File(ruta + "/documents/" + idIncidencia);
  			if (!tmpFile.exists()) {
  				tmpFile.mkdir();
  			}
  			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
  			if (!tmpFile.exists()) {
  				tmpFile.mkdir();
  			}
  			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
  			if (!tmpFile.exists()) {
  				tmpFile.mkdir();
  			}
     		
  			filePath = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/autoritzacio_proposta_actuacio_"+ idInforme +".pdf";
  			try {
  	        	PdfReader reader = new PdfReader(ruta + "/base/PA gerència.pdf"); // input PDF
  				PdfStamper stamper = new PdfStamper(reader,
  				  new FileOutputStream("/autoritzacio_proposta_actuacio_"+ idInforme +".pdf"));			
  				            
  	            AcroFields fields = stamper.getAcroFields();
  	            fields.setField("informe",informe.getIdInf());
  	        	fields.setField("gerent", UsuariCore.findUsuarisByRol(conn, "GERENT").get(0).getNomCompletReal());
  	   	       	fields.setField("observacions", "");	  	   	      
  	   	       	stamper.close();
  	   	       	reader.close();
     		                
  	   		} catch (DocumentException | SQLException e1) {
  	   			// TODO Auto-generated catch block
  	   			e1.printStackTrace();
  	   		} // output PDF
  			downloadFile = new File("/autoritzacio_proposta_actuacio_"+ idInforme +".pdf");
    	} else if ("PTObres".equals(tipus)) {
    		// Crear directoris si no existeixen
			File tmpFile = new File(ruta + "/documents/" + idIncidencia);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio");
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
			tmpFile = new File(ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio);
			if (!tmpFile.exists()) {
				tmpFile.mkdir();
			}
    		
    		filePath = ruta + "/documents/" + idIncidencia + "/Actuacio/" + idActuacio +"/proposta_tecnica_"+ idInforme +".pdf";
            try {
	        	PdfReader reader = new PdfReader(ruta + "/base/MODPTOBRES.pdf"); // input PDF
	        	Rectangle pagesize = reader.getPageSize(1);
				PdfStamper stamper = new PdfStamper(reader,
				  new FileOutputStream("/proposta_tecnica_"+ idInforme +".pdf"));			
				            
	            AcroFields fields = stamper.getAcroFields();
    	        fields.setField("tipusContracte",informe.getLlistaPropostes().get(0).getTipusObraFormat());
    	        fields.setField("nomCentre",actuacio.getCentre().getNomComplet());
    	        fields.setField("objecte",informe.getLlistaPropostes().get(0).getObjecte());
    	        fields.setField("pbase", informe.getOfertaSeleccionada().getPbaseFormat());
    	        fields.setField("iva", informe.getOfertaSeleccionada().getIvaFormat());
    	        fields.setField("plic", informe.getOfertaSeleccionada().getPlicFormat());
    	        fields.setField("terminiInicial", informe.getOfertaSeleccionada().getTermini());
    	        fields.setField("idActuacio", actuacio.getReferencia());
    	        fields.setField("empresaSeleccionada", informe.getOfertaSeleccionada().getNomEmpresa() + " (" + informe.getOfertaSeleccionada().getCifEmpresa() + ")" );    	 
    	        fields.setField("comentaris", informe.getOfertaSeleccionada().getComentari());    	 
    	        fields.setField("terminiDefinitiu", informe.getOfertaSeleccionada().getTermini());
    	        fields.setField("data", getData());
    	        PdfPTable table = new PdfPTable(3);
    	        PdfPCell cell = new PdfPCell();   
    	        Paragraph text = new Paragraph();    	
    	        
    	       // table.setWidths(new int[]{1,3});
    	        List<Oferta> ofertesList = informe.getLlistaOfertes();
    	         
    	        String FONT = ruta + "/fonts/NotoSans-Regular.ttf";
    	        FontFactory.register(FONT,"Noto-Sans");
    	        Font f = FontFactory.getFont("Noto-Sans", "ISO-8859-1", true);
    	        f.setSize(11);   	    
    	        String FONTBOLD = ruta + "/fonts/NotoSans-Bold.ttf";
    	        FontFactory.register(FONTBOLD,"Noto-Sans-Bold");
    	        Font fb = FontFactory.getFont("Noto-Sans-Bold", "ISO-8859-1", true);
    	        fb.setSize(11); 
    	            
    	        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    	        cell.setPadding(5);
    	        text = new Paragraph("Import Oferta", fb);    	            
	        	cell.addElement(text);    	            
    	        table.addCell(cell);
    	        
    	        cell = new PdfPCell();   
    	        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    	        cell.setPadding(5);
    	        text = new Paragraph("Nom licitador", fb);    	            
	        	cell.addElement(text);    	            
    	        table.addCell(cell);
    	        
    	        cell = new PdfPCell();   
    	        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    	        cell.setPadding(5);
    	        text = new Paragraph("DNI/CIF licitador", fb);    	            
	        	cell.addElement(text);    	            
    	        table.addCell(cell);
    	        
    	        table.setHeaderRows(1);
    	        	                    
    	        for (Oferta ofertaActual : ofertesList) {
    	        	cell = new PdfPCell();
    	        	cell.setPadding(5);
    	            text = new Paragraph(ofertaActual.getPlicFormat(), f);    	            
    	        	cell.addElement(text);    	            
    	            table.addCell(cell);
    	            
    	            cell = new PdfPCell();
    	            cell.setPadding(5);
    	            text = new Paragraph(ofertaActual.getNomEmpresa(), f);    	            
    	        	cell.addElement(text);    	            
    	            table.addCell(cell);
    	            
    	            cell = new PdfPCell();
    	            cell.setPadding(5);
    	            text = new Paragraph(ofertaActual.getCifEmpresa(), f);    	            
    	        	cell.addElement(text);    	            
    	            table.addCell(cell);
    	        }
    	        
    	        ColumnText column = new ColumnText(stamper.getOverContent(2));
    	        Rectangle rectPage1 = new Rectangle(36, 36, 559, 650);
    	        column.setSimpleColumn(rectPage1);
    	        column.addElement(table);
    	        int pagecount = 1;
    	        Rectangle rectPage2 = new Rectangle(36, 36, 559, 806);
    	        int status = column.go();
    	        while (ColumnText.hasMoreText(status)) {
    	           status = triggerNewPage(stamper, pagesize, column, rectPage2, ++pagecount);
    	       	}             
    	    	        
    	        stamper.close();
    	        reader.close();
    		                
    		} catch (DocumentException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} // output PDF
            downloadFile = new File("/proposta_tecnica_"+ idInforme +".pdf");
    	} else if (tipus.equals("empresa")) {
    		String cif = request.getParameter("cif");
    		try {
    			Empresa empresa = EmpresaCore.findEmpresa(conn, cif);
 	        	PdfReader reader = new PdfReader(ruta + "/base/empresa.pdf"); // input PDF
 	        	Rectangle pagesize = reader.getPageSize(1);
 				PdfStamper stamper = new PdfStamper(reader,
 				  new FileOutputStream("/empresa_"+ cif +".pdf"));		
 				
 				PdfPTable table = new PdfPTable(2);
     	        PdfPCell cell = new PdfPCell();   
     	        Paragraph text = new Paragraph();    	
     	        
     	        String FONT = ruta + "/fonts/NotoSans-Regular.ttf";
     	        FontFactory.register(FONT,"Noto-Sans");
     	        Font f = FontFactory.getFont("Noto-Sans", "ISO-8859-1", true);
     	        f.setSize(11);   	    
     	        String FONTBOLD = ruta + "/fonts/NotoSans-Bold.ttf";
     	        FontFactory.register(FONTBOLD,"Noto-Sans-Bold");
     	        Font fb = FontFactory.getFont("Noto-Sans-Bold", "ISO-8859-1", true);
     	        fb.setSize(14); 
     	        
     	        //Dades bàsiques
     	        
     	        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
     	        cell.setPadding(5);
     	        cell.setBorder(0);
     	        text = new Paragraph("Informació bàsica", fb);    	            
 	        	cell.addElement(text);    	            
     	        table.addCell(cell);
     	        
     	        cell = new PdfPCell();   
     	        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
     	        cell.setPadding(5);
     	        cell.setBorder(0);   	            
     	        table.addCell(cell);    
     	        
     	        table.setHeaderRows(1);     	        	                    
     	        
     	            cell = new PdfPCell();
     	        	cell.setPadding(5);
     	            text = new Paragraph("CIF", f);    	            
     	        	cell.addElement(text); 
     	        	cell.setBorder(0);   	 
     	            table.addCell(cell);
     	            
     	            cell = new PdfPCell();
     	            cell.setPadding(5);
     	            text = new Paragraph(empresa.getCif(), f);    	            
     	        	cell.addElement(text);  
     	        	cell.setBorder(0);   	 
     	            table.addCell(cell);
     	            
     	            cell = new PdfPCell();
    	        	cell.setPadding(5);
    	            text = new Paragraph("Nom", f);    	            
    	        	cell.addElement(text);    	
    	        	cell.setBorder(0);   	 
    	            table.addCell(cell);
    	            
    	            cell = new PdfPCell();
    	            cell.setPadding(5);
    	            text = new Paragraph(empresa.getName(), f);    	            
    	        	cell.addElement(text);   
    	        	cell.setBorder(0);   	 
    	            table.addCell(cell);
     	       	
     	       		cell = new PdfPCell();
	   	        	cell.setPadding(5);
	   	            text = new Paragraph("Direcció", f);    	            
	   	        	cell.addElement(text);    	
	   	        	cell.setBorder(0);   	 
	   	            table.addCell(cell);
	   	            
	   	            cell = new PdfPCell();
	   	            cell.setPadding(5);
	   	            text = new Paragraph(empresa.getDireccio(), f);    	            
	   	        	cell.addElement(text);   
	   	        	cell.setBorder(0);   	 
	   	            table.addCell(cell);
	   	            
   	            cell = new PdfPCell();
  	        	cell.setPadding(5);
  	            text = new Paragraph("CP", f);    	            
  	        	cell.addElement(text);    	
  	        	cell.setBorder(0);   	 
  	            table.addCell(cell);
  	            
  	            cell = new PdfPCell();
  	            cell.setPadding(5);
  	            text = new Paragraph(empresa.getCP(), f);    	            
  	        	cell.addElement(text);   
  	        	cell.setBorder(0);   	 
  	            table.addCell(cell);     	      

     	      	cell = new PdfPCell();
   	        	cell.setPadding(5);
   	            text = new Paragraph("Provincia", f);    	            
   	        	cell.addElement(text);    	
   	        	cell.setBorder(0);   	 
   	            table.addCell(cell);
   	            
   	            cell = new PdfPCell();
   	            cell.setPadding(5);
   	            text = new Paragraph(empresa.getProvincia(), f);    	            
   	        	cell.addElement(text);   
   	        	cell.setBorder(0);   	 
   	            table.addCell(cell);
     	     
     	        cell = new PdfPCell();
  	        	cell.setPadding(5);
  	            text = new Paragraph("Localitat", f);    	            
  	        	cell.addElement(text);    	
  	        	cell.setBorder(0);   	 
  	            table.addCell(cell);
  	            
  	            cell = new PdfPCell();
  	            cell.setPadding(5);
  	            text = new Paragraph(empresa.getCiutat(), f);    	            
  	        	cell.addElement(text);   
  	        	cell.setBorder(0);   	 
  	            table.addCell(cell);
  	            
  	            cell = new PdfPCell();
 	        	cell.setPadding(5);
 	            text = new Paragraph("Teléfon", f);    	            
 	        	cell.addElement(text);    	
 	        	cell.setBorder(0);   	 
 	            table.addCell(cell);
 	            
 	            cell = new PdfPCell();
 	            cell.setPadding(5);
 	            text = new Paragraph(empresa.getTelefon(), f);    	            
 	        	cell.addElement(text);   
 	        	cell.setBorder(0);   	 
 	            table.addCell(cell);
     	    
 	            cell = new PdfPCell();
	        	cell.setPadding(5);
	            text = new Paragraph("Fax", f);    	            
	        	cell.addElement(text);    	
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
	            
	            cell = new PdfPCell();
	            cell.setPadding(5);
	            text = new Paragraph(empresa.getFax(), f);    	            
	        	cell.addElement(text);   
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
	            
	            cell = new PdfPCell();
	        	cell.setPadding(5);
	            text = new Paragraph("Email", f);    	            
	        	cell.addElement(text);    	
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
	            
	            cell = new PdfPCell();
	            cell.setPadding(5);
	            text = new Paragraph(empresa.getEmail(), f);    	            
	        	cell.addElement(text);   
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
	            
	           	cell = new PdfPCell();
	        	cell.setPadding(5);
	            text = new Paragraph("Data constituació", f);    	            
	        	cell.addElement(text);    	
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
	            
	            cell = new PdfPCell();
	            cell.setPadding(5);
	            text = new Paragraph(empresa.getDataConstitucioString(), f);    	            
	        	cell.addElement(text);   
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
     	    
	            cell = new PdfPCell();
	        	cell.setPadding(5);
	            text = new Paragraph("Pime", f);    	            
	        	cell.addElement(text);    	
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
	            
	            cell = new PdfPCell();
	            cell.setPadding(5);
	            text = new Paragraph(empresa.isPime() ? "Si" : "No", f);    	            
	        	cell.addElement(text);   
	        	cell.setBorder(0);   	 
	            table.addCell(cell);
	       	
	            int pagecount = 1;
     	        ColumnText column = new ColumnText(stamper.getOverContent(1));
     	        Rectangle rectPage1 = new Rectangle(36, 36, 559, 650);
     	        column.setSimpleColumn(rectPage1);
     	        column.addElement(table);
     	       
     	        Rectangle rectPage2 = new Rectangle(36, 36, 559, 806);
     	        int status = column.go();
     	        while (ColumnText.hasMoreText(status)) {
     	           status = triggerNewPage(stamper, pagesize, column, rectPage2, ++pagecount);
     	       	}        
     	        
     	        //Representació
 				
 		        stamper.close();
 		        reader.close();
    		} catch (DocumentException | SQLException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} // output PDF
 catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 downloadFile = new File("/empresa_"+ cif +".pdf");
    	} 				
		//Descarrega del document
	
        
        FileInputStream inStream = new FileInputStream(downloadFile);
                           
        // gets MIME type of the file
        String mimeType = null;
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
	
	public int triggerNewPage(PdfStamper stamper,
		    Rectangle pagesize, ColumnText column, Rectangle rect, int pagecount)
		        throws DocumentException {
		    stamper.insertPage(pagecount, pagesize);
		    PdfContentByte canvas = stamper.getOverContent(pagecount);
		    column.setCanvas(canvas);
		    column.setSimpleColumn(rect);
		    return column.go();
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
