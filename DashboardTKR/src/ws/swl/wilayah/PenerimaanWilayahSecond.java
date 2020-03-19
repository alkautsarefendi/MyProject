package ws.swl.wilayah;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ws.swl.setup.Setup;

/**
 * Servlet implementation class PenerimaanWilayahSecond
 */
@WebServlet("/PenerimaanWilayahSecond")
public class PenerimaanWilayahSecond extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PenerimaanWilayahSecond() {
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
			String sql = "		select 	zv.KD_WILAYAH, zv.NM_WILAYAH, "
					+ "				COUNT( zx.JML_TAGIHAN ) COUNT_TAGIHAN, "
					+ "				SUM( zx.JML_TAGIHAN ) TOTAL_TAGIHAN, "
					+ "				SUM((case when zx.S_TAGIHAN = 1 then 1 else 0 end )) as COUNT_PENERIMAAN, "
					+ "			   	SUM((case when zx.S_TAGIHAN = 1 then zx.JML_TAGIHAN else 0 end )) as TOTAL_PENERIMAAN, "
					+ "				SUM((case when zx.S_TAGIHAN = 0 then 1 else 0 end )) as COUNT_PIUTANG, "
					+ "			   	SUM((case when zx.S_TAGIHAN = 0 then zx.JML_TAGIHAN else 0 end )) as TOTAL_PIUTANG, "
					+ "			   	SUM((case when zx.S_TAGIHAN = 1 then zx.JML_TAGIHAN else 0 end ))/SUM( zx.JML_TAGIHAN ) * 100 as PRESENTASE_PENERIMAAN "
					+ "		  from 	TTagihan zx "
					+ "				join MWILAYAH zv on ( zv.KD_WILAYAH = zx.KD_WILAYAH ) "
					+ "		 where zx.no_thnbln=" + getPeriode(month, year) + " and zx.kd_tagihan = 0 and zx.s_tagihan < 2 "
					+ " GROUP BY zv.KD_WILAYAH, zv.NM_WILAYAH " + " ORDER BY zv.KD_WILAYAH ";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String nmWil = rs.getString("NM_WILAYAH");
				String cTag = rs.getString("COUNT_TAGIHAN");
				String totTag = rs.getString("TOTAL_TAGIHAN");
				String cPen = rs.getString("COUNT_PENERIMAAN");
				String totPen = rs.getString("TOTAL_PENERIMAAN");
				String cPiu = rs.getString("COUNT_PIUTANG");
				String totPiu = rs.getString("TOTAL_PIUTANG");
				String prePen = rs.getString("PRESENTASE_PENERIMAAN");
				
				int countPendapatan = Integer.parseInt(cTag);
				int countPenerimaan = Integer.parseInt(cPen);
				int jmlPenerimaan = Integer.parseInt(totPen);
				String persen = getPercentage(countPenerimaan, countPendapatan)+"%";
				
				System.out.println("Lembar Rek SD : " + countPenerimaan + "|Rek Air SD : " + jmlPenerimaan + "|Persentase : " + persen);
				resp = "Lembar Rek SD : " + countPenerimaan + "|Rek Air SD : " + jmlPenerimaan + "|Persentase : " + persen;
				response.setContentLength(resp.length());
				// And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Lembar Rek SD : 0|Rek Air SD : 0|Persentase : 0";
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
	
	
	private String getPercentage(Integer a, Integer b){
		double aa = new Double(a);
		double bb = new Double(b);
		
		return new BigDecimal(aa/bb*100).setScale(4, RoundingMode.CEILING).toEngineeringString();
	}

}
