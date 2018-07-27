package handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bean.Actuacio;
import bean.InformeActuacio;
import core.ActuacioCore;
import core.InformeCore;
import utils.MyUtils;

/**
 * Servlet implementation class LlistatInformes
 */
@WebServlet("/LlistatInformes")
public class LlistatInformes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LlistatInformes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
        response.setContentType("text/html");
 
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        String idCentre = request.getParameter("idCentre");
        String estat = request.getParameter("estat");
        String tipus = request.getParameter("tipus");
        String filterWithOutDate = request.getParameter("filterWithOutDate");
		String filterWithOutDateExec = request.getParameter("filterWithOutDateExec");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date dataInici = null; 
        Date dataFi = null;
        Date dataExecucioIni = null; 
        Date dataExecucioFi = null;
        Gson gson = new Gson(); 
        JsonObject myObj = new JsonObject();
        Connection conn = MyUtils.getStoredConnection(request);
        List<InformeActuacio> llistatInformes = new ArrayList<InformeActuacio>();
		try {
			/*dataInici = null;
			dataFi = null;
			if ("false".equals(filterWithOutDate)){
				dataInici = df.parse(request.getParameter("dataPeticioIni"));
				dataFi = df.parse(request.getParameter("dataPeticioFi"));	
			}
			dataExecucioIni = null;
			dataExecucioFi = null;
			if ("false".equals(filterWithOutDateExec)){
				dataExecucioIni = df.parse(request.getParameter("dataExecucioIni"));
				dataExecucioFi = df.parse(request.getParameter("dataExecucioFi"));	
			}					*/	
			llistatInformes = InformeCore.getInformesCentres(conn, idCentre);
			myObj.addProperty("success", true);
			JsonElement llistatObj = gson.toJsonTree(llistatInformes);
			myObj.add("llistatInformes", llistatObj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			myObj.addProperty("success", false);
		}              
        out.println(myObj.toString());
 
        out.close();
	}
}
