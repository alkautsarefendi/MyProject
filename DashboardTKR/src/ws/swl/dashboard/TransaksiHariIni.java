package ws.swl.dashboard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ws.swl.setup.Setup;

/**
 * Servlet implementation class TransaksiHariIni
 */
@WebServlet("/TransaksiHariIni")
public class TransaksiHariIni extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TransaksiHariIni() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String resp = "";

		String s1 = request.getParameter("tgl");
		System.out.println(s1);
		
		Calendar start = getStartDay();
		Calendar end = getEndDay();
		
		String startVal = new SimpleDateFormat("yyyy-MM-dd").format(start.getTime());
		String endVal = new SimpleDateFormat("yyyy-MM-dd").format(end.getTime()) ;
		
		//System.out.println("KARAMBIA : " + startVal + "KARAMBIA 2 : " + endVal);

		try {
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);
			PreparedStatement ps = con.prepareStatement(" select count(*) as TOTAL_TRANSAKSI, nvl(sum(a.JML_TAGIHAN), 0) as NOMINAL_TRANSAKSI " +
					"   from TTAGIHAN a join TPEMBAYARAN b on (a.no_tagihan=b.no_tagihan) "+
					"  where a.S_TAGIHAN = 1 and a.KD_TAGIHAN = 0 and (b.TGL_BAYAR <= TO_DATE('" + startVal + "','YYYY-MM-DD') and b.TGL_BAYAR >= TO_DATE('" + endVal + "','YYYY-MM-DD') )  ");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String ttlTrx = rs.getString("TOTAL_TRANSAKSI");
				String nmTrx = rs.getString("NOMINAL_TRANSAKSI");
				System.out.println("Total Transaksi : " + ttlTrx + "Nominal Transaksi : " + nmTrx);
				resp = "Total Transaksi : " + ttlTrx + "|Nominal Transaksi : " + nmTrx;
				response.setContentLength(resp.length());
			    //And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Total Transaksi : 0|Nominal Transaksi : 0";
				response.setContentLength(resp.length());
			    //And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
				
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	
	public static Calendar getStartDay()
	  {
	    Calendar start = Calendar.getInstance();
	    start.set(9, 0);
	    start.set(10, 0);
	    start.set(12, 0);
	    start.set(13, 0);
	    start.set(14, 0);
	    return start;
	  }
	  
	  public static Calendar getEndDay() {
	    Calendar start = Calendar.getInstance();
	    start.set(9, 1);
	    start.set(10, 23);
	    start.set(12, 59);
	    start.set(13, 59);
	    return start;
	  }
}
