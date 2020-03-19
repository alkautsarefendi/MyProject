package ws.swl.pbsummary;

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
 * Servlet implementation class PenerimaanPasangBaruSummarySecond
 */
@WebServlet("/PenerimaanPasangBaruSummarySecond")
public class PenerimaanPasangBaruSummarySecond extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PenerimaanPasangBaruSummarySecond() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String resp = "";

		Calendar date = Calendar.getInstance();
		String lblPeriode = new SimpleDateFormat("MMMMM").format(date.getTime());
		String lblTgl = new SimpleDateFormat("dd/MM/yyyy").format(date.getTime());
		int month = date.get(Calendar.MONTH) + 1;
		int year = date.get(Calendar.YEAR);
		
		Calendar calStart = Calendar.getInstance();
		int m = calStart.get(2) + 1;
	    int y = calStart.get(1);
		
		String s1 = request.getParameter("tgl");
		System.out.println(s1);

		try {
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);
			
			String sql = "select KD_WILAYAH, NM_WILAYAH, count(COUNT_PELANGGAN) as COUNT_PELANGGAN, COUNT(COUNT_SURVEY) as COUNT_SURVEY, "
					+ "nvl(COUNT(COUNT_SURVEY) / NULLIF(COUNT(COUNT_PELANGGAN),0) * 100,0) as PERSENTASE " 
					+ "from ( " 
					+ "SELECT b.kd_wilayah as KD_WILAYAH, b.nm_wilayah as NM_WILAYAH,a.kd_daftar as COUNT_PELANGGAN, "
					+ "c.kd_daftar as COUNT_SURVEY " 
					+ "from mwilayah b left join Tpendaftaran a on (a.kd_wilayah=b.kd_wilayah) AND a.j_daftar=1 " 
					+ "left join tsurvey c on (a.kd_daftar = c.kd_daftar) and  EXTRACT(month FROM c.tgl_survey) = 10 "
					+ "and EXTRACT(year FROM c.tgl_survey) = 2017 and c.sts_verifikasi = 1 " 
					+ "where b.kd_wilayah in(1,2,3,4,5,6,7,8,9,11) " 
					+ ")group by KD_WILAYAH, NM_WILAYAH";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String peri = lblPeriode;
				String survey = rs.getString("COUNT_SURVEY");
				
				System.out.println("Periode : " + peri + "|Total Survey : " + survey);
				resp = "Periode : " + peri + "|Total Survey : " + survey;
				response.setContentLength(resp.length());
				// And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
				
			} else {
				
				resp = "Periode : 0|Total Survey : 0";
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
