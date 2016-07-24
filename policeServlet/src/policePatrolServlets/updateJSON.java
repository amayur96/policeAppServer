package policePatrolServlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class updateJSON
 */
@WebServlet("/updateJSON")
public class updateJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int criteria = 8;
	public HashMap<String,Object> jsonData;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public updateJSON() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File f = new File(getServletContext().getRealPath("/data1.txt"));
		Scanner scan = new Scanner(f);
		String[]data = new String[criteria];
		ArrayList<HashMap<String,String>> policeCars = new ArrayList<HashMap<String,String>>();
		String time = scan.next();
		String empty = scan.nextLine();
		while (scan.hasNextLine()) 
		{
			String line = scan.nextLine();
			Scanner console = new Scanner(line);
			int j=0;
			while(console.hasNext())
			{
				data[j]=console.next();
				j++;	
			}
			HashMap<String,String> singleCar = new HashMap<String,String>();
			singleCar.put("VehicleID",data[0]);
			singleCar.put("CallSign", data[1]);
			singleCar.put("UnitStatus", data[2]);
			singleCar.put("Latitude", data[3]);
			singleCar.put("Longitude", data[4]);
			singleCar.put("Speed", data[5]);
			singleCar.put("Heading", data[6]);
			singleCar.put("Altitude", data[7]);
			policeCars.add(singleCar);
		}
		response.getWriter().write("{"+"\"coordinates\""+":[");
		for(int i=0; i<policeCars.size();i++)
		{
		HashMap<String,String> map= policeCars.get(i);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new Gson().toJson(map));
		if(i<policeCars.size()-1)
		{
			response.getWriter().write(",");
		}
		}
		response.getWriter().write("]}");
}

	
/*
	private void write(HttpServletResponse response, HashMap<String, String> map) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(new Gson().toJson(map));
		
	}
	*/

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		jsonData = new HashMap<String,Object>();
		doGet(request, response);
		try {
			JSONObject obj = new JSONObject(request.getParameter("policeData"));/*the data sent by the POST 
			must have policeData as the tag*/
			Iterator it = obj.keys();
			while(it.hasNext())
			{
			    String key = (String) it.next(); // key
			    Object o = obj.get(key); // value
			    jsonData.put(key, o);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
