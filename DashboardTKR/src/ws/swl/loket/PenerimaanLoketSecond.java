package ws.swl.loket;

import java.io.IOException;
import java.math.BigDecimal;
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
 * Servlet implementation class PenerimaanLoketSecond
 */
@WebServlet("/PenerimaanLoketSecond")
public class PenerimaanLoketSecond extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PenerimaanLoketSecond() {
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
		calStart.set(Calendar.AM_PM, Calendar.AM);
		calStart.set(Calendar.DATE, 1);
		calStart.set(Calendar.HOUR, 0);
		calStart.set(Calendar.MINUTE, 0);
		calStart.set(Calendar.SECOND, 0);

		Calendar start = getStartDay();
		Calendar end = getEndDay();
		Calendar startDate = calStart;
		Calendar endDate = getEndDay();

		BigDecimal nominalSDHariIni = BigDecimal.ZERO;
		BigDecimal nominalSDHariIniTung = BigDecimal.ZERO;

		int jmlSDHariIni = 0;
		int jmlSDHariIniTung = 0;

		int m = calStart.get(Calendar.MONTH) + 1;
		int y = calStart.get(Calendar.YEAR);
		String lblPeriode = getPeriode(m, y);

		String s1 = request.getParameter("tgl");
		System.out.println(s1);

		String startVal = new SimpleDateFormat("yyyy-MM-dd").format(start.getTime());
		String endVal = new SimpleDateFormat("yyyy-MM-dd").format(end.getTime());
		String startDateVal = new SimpleDateFormat("yyyy-MM-dd").format(startDate.getTime());
		String endDateVal = new SimpleDateFormat("yyyy-MM-dd").format(endDate.getTime());

		/*
		 * System.out.println("Start Time : " + startVal + "End Time : " + endVal +
		 * "Start Date : " + startDateVal + "End Date : " + endDateVal + "Periode : " +
		 * lblPeriode);
		 */

		try {
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);
			String sql = "select A.KD_KANTOR, A.KANTOR, COALESCE(SUM(A.JML_TAG_SD_KEMARIN),0) as JML_TAG_SD_KEMARIN, "
					+ " COALESCE(SUM(A.NML_TAG_SD_KEMARIN),0) as NML_TAG_SD_KEMARIN, "
					+ " COALESCE(SUM(A.JML_TAG_HARI_INI),0) as JML_TAG_HARI_INI, COALESCE(SUM(A.NML_TAG_HARI_INI),0) as NML_TAG_HARI_INI, "
					+ "COALESCE(SUM(A.JML_TAG_SD_KEMARIN_TUNG),0) as JML_TAG_SD_KEMARIN_TUNG, "
					+ " COALESCE(SUM(A.NML_TAG_SD_KEMARIN_TUNG),0) as NML_TAG_SD_KEMARIN_TUNG from " + " ( "
					+ "		select zv.KD_WILAYAH as KD_KANTOR, zv.NM_WILAYAH as KANTOR, "
					+ "			   (case when NO_THNBLN = " + lblPeriode
					+ " then 1 else 0 end ) as JML_TAG_SD_KEMARIN, " + "			   (case when NO_THNBLN = "
					+ lblPeriode + " then zx.JML_TAGIHAN else 0 end )  as NML_TAG_SD_KEMARIN, "
					+ "			   (case when NO_THNBLN < " + lblPeriode
					+ " then 1 else 0 end ) as JML_TAG_SD_KEMARIN_TUNG, "
					+ "			   (case when NO_THNBLN < " + lblPeriode
					+ " then zx.JML_TAGIHAN else 0  end ) as NML_TAG_SD_KEMARIN_TUNG, "
					+ "	     	   (case when NO_THNBLN = " + lblPeriode + " and TRUNC(TGL_BAYAR) >= TO_DATE('"
					+ startVal + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal
					+ "','YYYY-MM-DD') " + "then 1 else 0 end ) as JML_TAG_HARI_INI, "
					+ "			   (case when NO_THNBLN = " + lblPeriode + " and TRUNC(TGL_BAYAR) >= TO_DATE('"
					+ startVal + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal
					+ "','YYYY-MM-DD') " + "then zx.JML_TAGIHAN else 0 end ) as NML_TAG_HARI_INI "
					+ "		  from TTagihan zx join TPembayaran zy on (zx.no_tagihan= zy.no_tagihan) "
					+ "              	join PEA_PEOPLE_INFORMATION zz on (zy.CRT_USERID=zz.USER_ID) "
					+ "				join MKANTOR zq on (zq.KD_KANTOR = zz.KD_KANTOR) "
					+ "				join MWILAYAH zv on (zv.KD_WILAYAH=zq.KD_WILAYAH) "
					+ "		 where TRUNC(zy.TGL_BAYAR) >= TO_DATE('" + startDateVal
					+ "','YYYY-MM-DD') and TRUNC(zy.TGL_BAYAR) <= TO_DATE('" + endDateVal + "','YYYY-MM-DD') "
					+ "			   and  nvl(zy.s_bayar, 1) = 1 and zx.kd_tagihan = 0 and zx.s_tagihan < 2 "
					+ "		 union all select cast(zv.KD_BANK as INT) as KD_KANTOR, zv.NM_BANK as KANTOR, "
					+ "			   (case when NO_THNBLN = " + lblPeriode
					+ " then 1 else 0 end) as JML_TAG_SD_KEMARIN, " + "			   (case when NO_THNBLN = "
					+ lblPeriode + " then zx.JML_TAGIHAN else 0 end)  as NML_TAG_SD_KEMARIN, "
					+ "			   (case when NO_THNBLN < " + lblPeriode
					+ " then 1 else 0 end) as JML_TAG_SD_KEMARIN_TUNG, "
					+ "			   (case when NO_THNBLN < " + lblPeriode
					+ " then zx.JML_TAGIHAN else 0  end) as NML_TAG_SD_KEMARIN_TUNG, "
					+ "	     	   (case when NO_THNBLN = " + lblPeriode + " and TRUNC(TGL_BAYAR) >= TO_DATE('"
					+ startVal + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal
					+ "','YYYY-MM-DD') " + "then 1 else 0 end) as JML_TAG_HARI_INI, "
					+ "			   (case when NO_THNBLN = " + lblPeriode + " and TRUNC(TGL_BAYAR) >= TO_DATE('"
					+ startVal + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal
					+ "','YYYY-MM-DD') " + "then zx.JML_TAGIHAN else 0 end) as NML_TAG_HARI_INI "
					+ "		  from TTagihan zx join TPembayaran zy on ( zx.no_tagihan= zy.no_tagihan ) "
					+ "				join MBANK zv on ( zv.KD_BANK=zy.KODE_BANK ) "
					+ "		 where zy.TGL_BAYAR >= TO_DATE('" + startDateVal
					+ "','YYYY-MM-DD') and zy.TGL_BAYAR <= TO_DATE('" + endDateVal + "','YYYY-MM-DD') "
					+ "			   and nvl(zy.s_bayar, 1) = 1 and zx.kd_tagihan = 0 and zx.s_tagihan < 2 "
					+ " ) A " + "GROUP BY A.KD_KANTOR, A.KANTOR " + "ORDER BY A.KD_KANTOR";
			PreparedStatement ps = con.prepareStatement(sql);
			System.out.println(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				jmlSDHariIni = Integer.parseInt(rs.getString("JML_TAG_SD_KEMARIN"));
				jmlSDHariIniTung = Integer.parseInt(rs.getString("JML_TAG_SD_KEMARIN_TUNG"));
				int lmbRekSD = jmlSDHariIni + jmlSDHariIniTung;
				nominalSDHariIni = BigDecimal.valueOf(Double.valueOf(rs.getString("NML_TAG_SD_KEMARIN")));
				nominalSDHariIniTung = BigDecimal.valueOf(Double.valueOf(rs.getString("NML_TAG_SD_KEMARIN_TUNG")));
				BigDecimal rekAirSD = nominalSDHariIni.add(nominalSDHariIniTung);
				System.out.println("Lembar Rekening SD : " + lmbRekSD + "|Rek Air SD : " + rekAirSD);
				resp = "Lembar Rekening SD : " + lmbRekSD + "|Rek Air SD : " + rekAirSD;
				response.setContentLength(resp.length());
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Lembar Rekening SD : 0|Rek Air SD : 0";
				response.setContentLength(resp.length());
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

	public static Calendar getStartDay() {
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
