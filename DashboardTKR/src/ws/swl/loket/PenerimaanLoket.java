package ws.swl.loket;

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
 * Servlet implementation class PenerimaanLoket
 */
@WebServlet("/PenerimaanLoket")
public class PenerimaanLoket extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PenerimaanLoket() {
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
		int month = date.get(Calendar.MONTH)+1;
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
			PreparedStatement ps = con.prepareStatement("select count(T.JML_TAGIHAN) as JMLTX, "
					+ "          coalesce(sum (T.JML_TAGIHAN), 0) as TOTALTX " 
					+ "     from TTAGIHAN T  "
					+ "    where NO_THNBLN="+getPeriode(month, year)+" and s_tagihan < 2 and kd_tagihan = 0 ");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String peri = lblPeriode;
				String lmbRek = rs.getString("JMLTX");
				String nmlRek = rs.getString("TOTALTX");
				String tglHari = lblTgl;
				System.out.println("Periode Pembayaran : " + peri + "|Lembar Rekening : " + lmbRek + "|Nominal Rek : " + nmlRek +
						"|Tanggal Hari Ini : " + tglHari);
				resp = "Periode Pembayaran : " + peri + "|Lembar Rekening : " + lmbRek + "|Nominal Rek : " + nmlRek +
						"|Tanggal Hari Ini : " + tglHari;
				response.setContentLength(resp.length());
			    //And write the string to output.
				response.getOutputStream().write(resp.getBytes());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} else {
				
				resp = "Periode Pembayaran : 0|Lembar Rekening : 0|Nominal Rek : 0|Tanggal Hari Ini : 0";
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

	
	public static String getPeriode(Integer bulan, Integer tahun) {
	    String sBulan = String.valueOf(bulan);
	    if (sBulan.length() == 1) sBulan = "0" + sBulan;
	    return String.valueOf(tahun) + sBulan;
	  }
}
