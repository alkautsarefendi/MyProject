package ws.swl.pasangbaru;

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
 * Servlet implementation class PenerimaanPasangBaruFirst
 */
@WebServlet("/PenerimaanPasangBaruFirst")
public class PenerimaanPasangBaruFirst extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PenerimaanPasangBaruFirst() {
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

		Calendar calStart = Calendar.getInstance();
		int m = calStart.get(2) + 1;
		int y = calStart.get(1);

		String s1 = request.getParameter("tgl");
		System.out.println(s1);

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);

			String sql = "select sum(kd_daftar) as KD_DAFTAR, " + 
					" sum( tgl_aktif )as tgl_aktif " + 
					" from (" + 
					" select 1 as kd_daftar, 0 as tgl_aktif " + 
					" " + 
					" from Tpendaftaran b " + 
					"   LEFT join mwilayah a on (b.kd_wilayah = a.kd_wilayah)" + 
					"   left join TSURVEY c on ( b.kd_daftar = c.kd_daftar and sts_verifikasi = 1 ) " + 
					"   left join ttagihan d on ( d.kd_pelanggan = b.kd_daftar )" + 
					"   left join TPEMBAYARAN e on ( d.no_tagihan = e.no_tagihan and s_bayar = 1 )" + 
					"   left join tpemasangan f on (b.kd_daftar = f.kd_daftar) " + 
					"   left join MPELANGGAN g on ( b.kd_daftar = g.kd_daftar) " + 
					"  where EXTRACT(month FROM b.tgl_daftar) = '" + month + "' " + 
					"  and EXTRACT(year FROM b.tgl_daftar) = '" + year + "' " + 
					"   AND S_DAFTAR NOT IN (7,8)" + 
					"   and b.j_daftar = 1 " + 
					"   union all" + 
					"   " + 
					"   select 0 as kd_daftar, 1 as tgl_aktif " + 
					" " + 
					" from Tpendaftaran b " + 
					"   LEFT join mwilayah a on (b.kd_wilayah = a.kd_wilayah)" + 
					"   left join TSURVEY c on ( b.kd_daftar = c.kd_daftar and sts_verifikasi = 1 ) " + 
					"   left join ttagihan d on ( d.kd_pelanggan = b.kd_daftar )" + 
					"   left join TPEMBAYARAN e on ( d.no_tagihan = e.no_tagihan and s_bayar = 1 )" + 
					"   left join tpemasangan f on (b.kd_daftar = f.kd_daftar) " + 
					"   left join MPELANGGAN g on ( b.kd_daftar = g.kd_daftar) " + 
					"  where EXTRACT(month FROM b.tgl_daftar) = '" + month + "'" + 
					"  and EXTRACT(year FROM b.tgl_daftar) = '" + year + "' " + 
					"   AND S_DAFTAR NOT IN (7,8)" + 
					"   and b.j_daftar=1" + 
					"   and f.tgl_pasang is not null)";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String peri = lblPeriode;
				String jmlPerm = rs.getString("KD_DAFTAR");
				String jmlTerp = rs.getString("tgl_aktif");

				System.out.println("Periode : " + peri + "|Jumlah Permintaan : " + jmlPerm + "|Jumlah Terpasang : " + jmlTerp);
				resp = "Periode : " + peri + "|Jumlah Permintaan : " + jmlPerm + "|Jumlah Terpasang : " + jmlTerp;
				response.setContentLength(resp.length());
				// And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {

				resp = "Periode : 0|Jumlah Permintaan : 0|Jumlah Terpasang : 0";
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

}
