package ws.swl.harian;

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
 * Servlet implementation class PenerimaanHarianFirst
 */
@WebServlet("/PenerimaanHarianFirst")
public class PenerimaanHarianFirst extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PenerimaanHarianFirst() {
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
		
		Calendar calStart = Calendar.getInstance();
		int m = calStart.get(Calendar.MONTH)+1;

		Calendar date = Calendar.getInstance();
		date.set(Calendar.MONTH, m-1);
		String lblPeriode = new SimpleDateFormat("MMMMM").format(date.getTime());
		String lblTgl = new SimpleDateFormat("dd/MM/yyyy").format(date.getTime());
		int month = date.get(Calendar.MONTH) + 1;
		int year = date.get(Calendar.YEAR);

		String s1 = request.getParameter("tgl");
		System.out.println(s1);

		try {
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);
			PreparedStatement ps = con.prepareStatement(" select lembar,biaya_air,denda,materai from " + 
					"(select COUNT(TP.NO_TAGIHAN) AS lembar,sum(tt.jml_rk+tt.jml_bpma+tt.JML_admin) as biaya_air, sum(TP.JML_DENDA) as denda, " + 
					"sum(TP.JML_MATERAI) as materai " + 
					"FROM TPEMBAYARAN TP " + 
					"JOIN TTAGIHAN TT ON TT.NO_TAGIHAN=TP.NO_TAGIHAN WHERE  TO_CHAR(TP.tgl_BAYAR,'YYYYMM')=" + getPeriode(month, year) + " " + 
					" AND TT.KD_TAGIHAN=0 AND TP.S_BAYAR=1) ");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String peri = lblPeriode;
				String lmbr = rs.getString("lembar");
				String bAir = rs.getString("biaya_air");
				String dend = rs.getString("denda");
				String mate = rs.getString("materai");

				System.out.println(
						"Lembar : " + lmbr + "|Biaya Air : " + bAir + "|Denda : " + dend + "|Materai : " + mate + "|Periode : " + peri);
				resp = "Lembar : " + lmbr + "|Biaya Air : " + bAir + "|Denda : " + dend + "|Materai : " + mate + "|Periode : " + peri;
				response.setContentLength(resp.length());
				// And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Lembar : 0|Biaya Air : 0|Denda : 0|Materai : 0|Periode : 0";
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

}
