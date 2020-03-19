package ws.swl.bacameter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ws.swl.setup.Setup;

/**
 * Servlet implementation class BacaMeterFirst
 */
@WebServlet("/BacaMeterFirst")
public class BacaMeterFirst extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BacaMeterFirst() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		String resp = "";

		Calendar date = Calendar.getInstance();
		String lblPeriode = new SimpleDateFormat("MMMMM").format(date.getTime());
		String lblTgl = new SimpleDateFormat("dd/MM/yyyy").format(date.getTime());
		int month = date.get(Calendar.MONTH) + 1;
		int year = date.get(Calendar.YEAR);
		
		String s1 = request.getParameter("tgl");
		System.out.println(s1);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		Date now = new Date();
	    String strDate = sdf.format(now);

		try {
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);
			PreparedStatement ps = con.prepareStatement("select KD_WILAYAH, NM_WILAYAH, SUM(COUNT_PELANGGAN) as COUNT_PELANGGAN, SUM(COUNT_BACA) as COUNT_BACA,  " +
					"SUM(COUNT_BACA) / SUM(COUNT_PELANGGAN) * 100 as PERSENTASE " + 
					"from (Select a.kd_wilayah as KD_WILAYAH, b.nm_wilayah as NM_WILAYAH " + 
					", 1 as COUNT_PELANGGAN, 0 as COUNT_BACA   from mpelanggan a join mwilayah b on (a.kd_wilayah=b.kd_wilayah) " + 
					"where a.s_pelanggan = 1 or a.s_pelanggan = 3 " + 
					"union all " + 
					"Select a.kd_wilayah as KD_WILAYAH, b.nm_wilayah as NM_WILAYAH, 0 as COUNT_PELANGGAN, 1 as COUNT_BACA " + 
					"from mpelanggan a " + 
					"join mwilayah b on a.kd_wilayah=b.kd_wilayah " + 
					"join tbacameter c on a.kd_pelanggan = c.kd_pelanggan " + 
					"and c.no_thnbln=" + strDate +  "and c.sts_verifikasi IN (0,1,2) " + 
					"where a.s_pelanggan = 1 or a.s_pelanggan = 3) " + 
					"group by KD_WILAYAH, NM_WILAYAH " + 
					"order by KD_WILAYAH");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String peri = lblPeriode;
				String totPel = rs.getString("COUNT_PELANGGAN");
				String jmlBaca = rs.getString("COUNT_BACA");
				String persentase = rs.getString("PERSENTASE");
				
				int jmlPendapatan = Integer.parseInt(jmlBaca);
				int countPendapatan = Integer.parseInt(totPel);
				String persen = getPercentage(jmlPendapatan, countPendapatan)+"%";
				

				System.out.println("Periode : " + peri + "|Total Pelanggan : " + totPel + "|Jumlah Pembacaan : " + jmlBaca 
						+ "|Persentase : " + persen);
				resp = "Periode : " + peri + "|Total Pelanggan : " + totPel + "|Jumlah Pembacaan : " + jmlBaca 
						+ "|Persentase : " + persen;
				response.setContentLength(resp.length());
				// And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Periode : 0|Total Pelanggan : 0|Jumlah Pembacaan : 0|Persentase : 0%";
				response.setContentLength(resp.length());
				// And write the string to output.
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	
	public static String getPeriode(Integer bulan, Integer tahun) {
		String sBulan = String.valueOf(bulan);
		if (sBulan.length() == 1)
			sBulan = "0" + sBulan;
		return String.valueOf(tahun) + sBulan;
	}
	
	
	private String getPercentage(Integer a, Integer b)
	  {
	    double aa = new Double(a.intValue()).doubleValue();
	    double bb = new Double(b.intValue()).doubleValue();
	    
	    return new BigDecimal(aa / bb * 100.0D).setScale(4, java.math.RoundingMode.CEILING).toEngineeringString();
	  }
}
