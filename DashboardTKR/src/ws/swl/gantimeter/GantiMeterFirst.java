package ws.swl.gantimeter;

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
 * Servlet implementation class GantiMeterFirst
 */
@WebServlet("/GantiMeterFirst")
public class GantiMeterFirst extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GantiMeterFirst() {
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

		try {
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);
			PreparedStatement ps = con.prepareStatement("SELECT count(L.PEMILIK) AS PEMILIK "
					+ "FROM  TPEMASANGAN PS  LEFT JOIN TGANTIMETER G ON PS.NO_BAP = G.NO_BAPP "
					+ "LEFT JOIN MPELANGGAN P ON PS.KD_daftar=P.KD_PELANGGAN "
					+ "LEFT JOIN MPEMILIK L ON L.KD_PEMILIK=P.KD_PEMILIK "
					+ "JOIN MWILAYAH W ON W.KD_WILAYAH=P.KD_WILAYAH   WHERE  EXTRACT(month FROM ps.CRT_TGL) = '" + month
					+ "' " + "and EXTRACT(year FROM ps.CRT_TGL) = '" + year + "'");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String peri = lblPeriode;
				String totGantiWM = rs.getString("PEMILIK");

				System.out.println("Periode : " + peri + "|Total Ganti WM : " + totGantiWM);
				resp = "Periode : " + peri + "|Total Ganti WM : " + totGantiWM;
				response.setContentLength(resp.length());
				// And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {

				resp = "Periode : 0|Total Ganti WM : 0";
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
