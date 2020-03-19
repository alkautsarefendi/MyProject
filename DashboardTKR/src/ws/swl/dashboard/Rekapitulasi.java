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
 * Servlet implementation class Rekapitulasi
 */
@WebServlet("/Rekapitulasi")
public class Rekapitulasi extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Rekapitulasi() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		String resp = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		
		Calendar start = getStartDay();
		Calendar end = getEndDay();
		
		Calendar end1 = Calendar.getInstance();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(start.getTime());
		cal.set(Calendar.MONTH, start.get(Calendar.MONTH));
		
		Integer periode = Integer.valueOf(new SimpleDateFormat("yyyyMM").format(cal.getTime()));
		
		Calendar endDate = getEndDay();
		
		Calendar startDateWeek = Calendar.getInstance();
		startDateWeek.setTime(start.getTime());
		if(startDateWeek.get(Calendar.DATE) - (start.get(Calendar.DAY_OF_WEEK) - 1)  < 0){
			startDateWeek.set(Calendar.DATE, 1);
		}else{
			startDateWeek.set(Calendar.DATE, startDateWeek.get(Calendar.DATE) - (start.get(Calendar.DAY_OF_WEEK) - 1));
		}
		
		Calendar startDateMonth = Calendar.getInstance();
		startDateMonth.setTime(start.getTime());
		startDateMonth.set(Calendar.DATE, 1);
		
		Calendar startDateYear = Calendar.getInstance();
		startDateYear.set(Calendar.DATE, 1);
		startDateYear.set(Calendar.MONTH, 0);
		
		String startVal = new SimpleDateFormat("yyyy-MM-dd").format(start.getTime());
		String endVal = new SimpleDateFormat("yyyy-MM-dd").format(end.getTime()) ;
		String stMinggu = new SimpleDateFormat("yyyy-MM-dd").format(startDateWeek.getTime());
		String stBulan = new SimpleDateFormat("yyyy-MM-dd").format(startDateMonth.getTime());
		String stTahun = new SimpleDateFormat("yyyy-MM-dd").format(startDateYear.getTime());
		
		/*System.out.println("Start Time : " + startVal 
				+ "End Time : " + endVal
				+ "Periode : " + periode 
				+ "Start Minggu : " + stMinggu
				+ "Start Bulan : " + stBulan
				+ "Start Tahun : " + stTahun);*/

		String s1 = request.getParameter("tgl");
		System.out.println(s1);

		try {
			String db_host = Setup.DB_HOST;
			int db_port = Setup.DB_PORT;
			String db_us = Setup.DB_US;
			String db_pas = Setup.DB_PA;
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@"+ db_host + ":"+ db_port + ":xe", db_us, db_pas);
			PreparedStatement ps = con.prepareStatement("select * from ( "
					+ "		  select count(*) as TOTAL_TRANSAKSI, " 
					+ "              nvl(sum(JML_BAYAR), 0) as NOMINAL_TRANSAKSI "
					+ "         from TPEMBAYARAN TP join TTAGIHAN TT on (TT.NO_TAGIHAN = TP.NO_TAGIHAN) "
					+ "			where KD_TAGIHAN = 0 and S_BAYAR <> 2 and TRUNC(TGL_BAYAR) >= TO_DATE('" + startVal + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal + "','YYYY-MM-DD') "
					+ " ) x, ("
					+ "		  select count(*) as TOTAL_TRANSAKSI1,"
					+ "       		 nvl(sum(A.JML_TAGIHAN), 0) as NOMINAL_TRANSAKSI1 ,   "
					+ "        		 sum(case when S_TAGIHAN = 1 then 1 else 0 end) as TOTAL_TRANSAKSI2,"
					+ "        		 nvl(sum(case when S_TAGIHAN = 1 then JML_TAGIHAN else 0 end), 0) as NOMINAL_TRANSAKSI2, "
					+ "        		 sum(case when S_TAGIHAN = 1 then 0 else 1 end) as TOTAL_TRANSAKSI3,"
					+ "        		 nvl(sum(case when S_TAGIHAN = 1 then 0 else JML_TAGIHAN end), 0) as NOMINAL_TRANSAKSI3 "
					+ "			from TTAGIHAN A where A.NO_THNBLN = " + periode + " and A.KD_TAGIHAN = 0 and A.S_TAGIHAN < 2 "
					+ " ) y, ( "
					+ " 	  select count(*) as JMLMINGGU, " 
					+ "			     nvl(sum(JML_BAYAR),0) as NMLMINGGU " 
					+ "         from TPEMBAYARAN TP join TTAGIHAN TT on (TT.NO_TAGIHAN = TP.NO_TAGIHAN) where KD_TAGIHAN = 0 and S_BAYAR <> 2 and TRUNC(TGL_BAYAR) >= TO_DATE('" + stMinggu + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal + "','YYYY-MM-DD') "
					+ " ) MINGGU, ("
					+ "       select count(*) as JMLBULAN, "
					+ "		         nvl(sum(JML_BAYAR),0) as NMLBULAN "
					+ "         from TPEMBAYARAN TP join TTAGIHAN TT on (TT.NO_TAGIHAN = TP.NO_TAGIHAN) where KD_TAGIHAN = 0 and S_BAYAR <> 2 and TRUNC(TGL_BAYAR) >= TO_DATE('" + stBulan + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal + "','YYYY-MM-DD') "
					+ " ) BULAN, ("
					+ " 	  select count(*) as JMLTAHUN, "
					+ "			     nvl(sum(JML_BAYAR),0) as NMLTAHUN "
					+ "         from TPEMBAYARAN TP join TTAGIHAN TT on (TT.NO_TAGIHAN = TP.NO_TAGIHAN) where KD_TAGIHAN = 0 and S_BAYAR <> 2 and TRUNC(TGL_BAYAR) >= TO_DATE('" + stTahun + "','YYYY-MM-DD') and TRUNC(TGL_BAYAR) <= TO_DATE('" + endVal + "','YYYY-MM-DD') "
					+ " ) TAHUN ");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String minggu = rs.getString("NMLMINGGU");
				String bulan = rs.getString("NMLBULAN");
				String tahun = rs.getString("NMLTAHUN");
				String ttlTrx = rs.getString("TOTAL_TRANSAKSI");
				String nmlTrx = rs.getString("NOMINAL_TRANSAKSI");
				System.out.println("Minggu ini : " + minggu + "|Bulan ini : " + bulan + "|Tahun ini : " + tahun +
						"|Total Transaksi : " + ttlTrx + "|Nominal Transaksi : " + nmlTrx);
				resp = "Minggu ini : " + minggu + "|Bulan ini : " + bulan + "|Tahun ini : " + tahun +
						"|Total Transaksi : " + ttlTrx + "|Nominal Transaksi : " + nmlTrx;
				response.setContentLength(resp.length());
			    //And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Minggu ini : 0|Bulan ini : 0|Tahun ini : 0|Total Transaksi : 0|Nominal Transaksi : 0";
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
