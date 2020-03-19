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
 * Servlet implementation class PenerimaanPasangBaruSummaryFirst
 */
@WebServlet("/PenerimaanPasangBaruSummaryFirst")
public class PenerimaanPasangBaruSummaryFirst extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PenerimaanPasangBaruSummaryFirst() {
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
			
			String sql = "SELECT KD_WILAYAH, NM_WILAYAH, TGL_DAFTAR , count(DAFTAR) as COUNT_DAFTAR  FROM "
					+ "(select b.kd_wilayah as KD_WILAYAH, a.nm_wilayah as NM_WILAYAH, "
					+ "TO_NUMBER(TO_CHAR(b.tgl_daftar,'MM')) as TGL_DAFTAR,b.kd_daftar as DAFTAR "
					+ "from mwilayah a  left join tpendaftaran b on (b.kd_wilayah=a.kd_wilayah) "
					+ "and TO_CHAR(b.tgl_daftar,'YYYY')=" + year + " and TO_CHAR(b.tgl_daftar,'mm')=" + month + " "
							+ "where a.kd_wilayah in (1,2,3,4,5,6,7,8,9,11) and B.j_daftar=1 AND S_DAFTAR NOT in(7,8) ) a " + 
					"WHERE TGL_DAFTAR is not null " + "group by a.KD_WILAYAH, a.NM_WILAYAH, a.TGL_DAFTAR " + 
							"order by a.KD_WILAYAH, a.TGL_DAFTAR ";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String peri = lblPeriode;
				String daftar = rs.getString("COUNT_DAFTAR");
				
				System.out.println("Periode : " + peri + "|Total Daftar : " + daftar);
				resp = "Periode : " + peri + "|Total Daftar : " + daftar;
				response.setContentLength(resp.length());
				// And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Periode : 0|Total Daftar : 0";
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
