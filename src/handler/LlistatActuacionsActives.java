package handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bean.Actuacio;
import core.ActuacioCore;
import core.CentreCore;
import utils.MyUtils;

/**
 * Servlet implementation class LlistatActuacionsActives
 */
@WebServlet("/LlistatActuacionsActives")
public class LlistatActuacionsActives extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LlistatActuacionsActives() {
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
        Gson gson = new Gson(); 
        JsonObject myObj = new JsonObject();
        Connection conn = MyUtils.getStoredConnection(request);
        List<Actuacio> llistatActuacions = new ArrayList<Actuacio>();
		try {
			llistatActuacions = ActuacioCore.searchActuacions(conn, idCentre, "-1", null, null).getLlistaActuacions();
			myObj.addProperty("success", true);
			JsonElement llistatObj = gson.toJsonTree(llistatActuacions);
			myObj.add("llistatActuacions", llistatObj);
			JsonElement nomCentreObj = gson.toJsonTree(CentreCore.nomCentre(conn, idCentre));
			myObj.add("nomCentre", nomCentreObj);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			myObj.addProperty("success", false);
		}              
        out.println(myObj.toString());
 
        out.close();
	}

}
