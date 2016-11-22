package com.cj.xso.s.report.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Repository;

import com.cj.framework.common.dict.ListUtil;
import com.cj.framework.common.lang.LocaleUtil;
import com.cj.framework.common.pagination.Pager;
import com.cj.framework.common.pagination.SearchBean;
import com.cj.framework.common.utils.StringUtil;
import com.cj.framework.common.utils.StringUtils;
import com.cj.framework.persistence.BaseHibernateDAO;
import com.cj.xso.s.order.model.Orders;


/**
 * 报表DAO接口
 */
   @Repository
   public class ReportDao extends BaseHibernateDAO<Orders, Long> {
	  public ReportDao() {
		 super(Orders.class);
	}
	  
	/**
	 * awardsBasicDetail.jsp
	 */
  public Pager<Map<String,Object>> awardsBasicDetail(Map<String,String> paramFromJsp,int currentPage,int pageSize){
	  Class clazz = ReportDao.class;
	  String sql ="select member_no customerId,NAME customerName,"
		  		+ "MONTH,awards,sp,create_time createTime"
		  		+ "from t_sp_detail_awards_basic  where 1=1 ";
	  String memberNo = paramFromJsp.get("memberNoStr");
	  if (memberNo != null && !memberNo.trim().equals("")) {
			sql += " and  member_no LIKE '%" + memberNo + "%'";
	  }
	  log.debug("awardsBasicDetail"+sql);
	  Pager<Map<String,Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
	  return pager;
  }
  /**
   * 
   */  
	/**
	 * sponsorExtraDetail.jsp上的查询
	 */
   public Pager<Map<String,Object>> sponsorExtraDetail(Map<String,String> paramFromJsp,int currentPage,int pageSize){
	   Class clazz = ReportDao.class;
	   String sql ="select * from T_SP_Detail_SPONSOR_EXTRA WHERE 1=1 ";
	   sql = sql + this.createSponsorExtraDetail(paramFromJsp);
	  
	   log.debug("sponsorExtraDetail"+sql);
	   Pager<Map<String,Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
	   return pager;	   
   }
   /**
    * awardsBasicDetail.jsp 页面上的"导出"，范湖文件完整路径名给controller 调用下载方法在浏览器端弹出下载框
    */
   public String awardsBasicDetailExport(Map<String,String> paramsFromJsp){
	   DataSource ds = this.getJdbcTemplate().getDataSource();
	   Connection oracleConn = null;
	   Statement oracleStmt = null;
	   ResultSet oracleRs = null;
	   String csvFile = "awardsBasicDetail.csv";
	   String path = "";
	   
	   try{
		   path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
		   log.debug("awardsBasicDetail-----path----------"+path);
		   
		   String sql = "select member_no customerId,NAME customerName,"
			  		+ "MONTH,awards,sp,create_time createTime"
			  		+ "from t_sp_detail_awards_basic  where 1=1 ";
		   //页面传过来的条件参数
		   String memberNo = paramsFromJsp.get("memberNoStr");
		   if (memberNo != null && !memberNo.trim().equals("")) {
			   sql += " and  member_no LIKE '%" + memberNo + "%'";
	       }
		   log.debug("awardsBasicDetail---sql-------:" + sql);
			
			oracleConn = ds.getConnection();
			oracleStmt = oracleConn.createStatement();
			oracleRs = oracleStmt.executeQuery(sql);

			FileWriter fileWriter;
	    	CSVPrinter csvPrinter = null;
	    	try {
	    		fileWriter = new FileWriter(path + csvFile);
	    		
	    		// 新建csv文件
	    		File file = new File(path + csvFile);
	    		FileOutputStream fos = new FileOutputStream(file);
	    		// 写BOM
	    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
	    		// 创建字节流输出对象
	    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	    		// Apache Commons CSV打印对象
	    		
	    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
	    		csvPrinter.printRecords(oracleRs);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		try {
	    			if (csvPrinter != null) {
	    				csvPrinter.flush();
	    				csvPrinter.close();
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e1) {
			e1.printStackTrace();
		}  
		finally {
			try {
				if (oracleRs != null) {
					oracleRs.close();
				}
				if (oracleStmt != null) {
					oracleStmt.close();
				}
				if (oracleConn != null) {
					oracleConn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		
		return path + csvFile;
	}
   /**
	 * sponsorDetail.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
    */
	public String sponsorExtraDetailExport(Map<String, String> paramsFromJsp) {
			DataSource ds = this.getJdbcTemplate().getDataSource();
			Connection oracleConn = null;  
			Statement oracleStmt = null;  
			ResultSet oracleRs = null;
			//String sqlFile = "sharePointOverview.sql";
			String csvFile = "sponsorExtraDetail.csv";
			String path = "";
			
			try {
				path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
				log.debug("sponsorExtraDetail-----path----------:" + path);
				
				String sql ="select * from T_SP_Detail_SPONSOR_EXTRA WHERE 1=1 "+ this.createSponsorExtraDetail(paramsFromJsp);
				log.debug("sponsorExtraDetail---sql-------:" + sql);
				
				oracleConn = ds.getConnection();
				oracleStmt = oracleConn.createStatement();
				oracleRs = oracleStmt.executeQuery(sql);

				FileWriter fileWriter;
		    	CSVPrinter csvPrinter = null;
		    	try {
		    		fileWriter = new FileWriter(path + csvFile);
		    		
		    		// 新建csv文件
		    		File file = new File(path + csvFile);
		    		FileOutputStream fos = new FileOutputStream(file);
		    		// 写BOM
		    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
		    		// 创建字节流输出对象
		    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		    		// Apache Commons CSV打印对象
		    		
		    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
		    		csvPrinter.printRecords(oracleRs);
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	} finally {
		    		try {
		    			if (csvPrinter != null) {
		    				csvPrinter.flush();
		    				csvPrinter.close();
		    			}
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    	}
		    	
			} catch (SQLException e) {
				e.printStackTrace();
			}  catch (URISyntaxException e1) {
				e1.printStackTrace();
			}  
			finally {
				try {
					if (oracleRs != null) {
						oracleRs.close();
					}
					if (oracleStmt != null) {
						oracleStmt.close();
					}
					if (oracleConn != null) {
						oracleConn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} 
			
			return path + csvFile;
		}
	  /**
	   * sponsorExtraDetail 构造查询条件
	   */
	  public String createSponsorExtraDetail(Map<String, String> paramsFromJsp) {
			String memberNo = paramsFromJsp.get("memberNoStr");
			String sql = "";
			if (memberNo != null && !memberNo.trim().equals("")) {
				sql += " and  member_no LIKE '%" + memberNo + "%'";
			}
			return sql;
		}
	
	  /**
	   * sponsorDetail.jsp上的查询
	   */
	public Pager<Map<String, Object>> sponsorDetail(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		   Class clazz = ReportDao.class;
		   String sql = "select * from T_SP_Detail_SPONSOR where 1=1 ";
		   sql = sql + this.createSponsorDetail(paramsFromJsp);
		   log.debug("sponsorDetail--sql----------:" + sql);
		   Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		   return pager;
	}
	/**
	 * sponsorDetail.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
     */
	  public String sponsorDetailExport(Map<String, String> paramsFromJsp) {
			DataSource ds = this.getJdbcTemplate().getDataSource();
			Connection oracleConn = null;  
			Statement oracleStmt = null;  
			ResultSet oracleRs = null;
			//String sqlFile = "sharePointOverview.sql";
			String csvFile = "sponsorDetail.csv";
			String path = "";
			
			try {
				path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
				log.debug("sponsorDetail-----path----------:" + path);
				
				String sql ="select * from T_SP_Detail_SPONSOR where1=1 "+ this.createSponsorDetail(paramsFromJsp);
				log.debug("sponsorDetail---sql-------:" + sql);
				
				oracleConn = ds.getConnection();
				oracleStmt = oracleConn.createStatement();
				oracleRs = oracleStmt.executeQuery(sql);

				FileWriter fileWriter;
		    	CSVPrinter csvPrinter = null;
		    	try {
		    		fileWriter = new FileWriter(path + csvFile);
		    		
		    		// 新建csv文件
		    		File file = new File(path + csvFile);
		    		FileOutputStream fos = new FileOutputStream(file);
		    		// 写BOM
		    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
		    		// 创建字节流输出对象
		    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		    		// Apache Commons CSV打印对象
		    		
		    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
		    		csvPrinter.printRecords(oracleRs);
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	} finally {
		    		try {
		    			if (csvPrinter != null) {
		    				csvPrinter.flush();
		    				csvPrinter.close();
		    			}
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    	}
		    	
			} catch (SQLException e) {
				e.printStackTrace();
			}  catch (URISyntaxException e1) {
				e1.printStackTrace();
			}  
			finally {
				try {
					if (oracleRs != null) {
						oracleRs.close();
					}
					if (oracleStmt != null) {
						oracleStmt.close();
					}
					if (oracleConn != null) {
						oracleConn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} 
			
			return path + csvFile;
		}
	  /**
	   * sponsorDetail 构造查询条件
	   */
	  public String createSponsorDetail(Map<String, String> paramsFromJsp) {
			String memberNo = paramsFromJsp.get("memberNoStr");
			String sql = "";
			if (memberNo != null && !memberNo.trim().equals("")) {
				sql += " and member_no LIKE '%" + memberNo + "%'";
			}
			return sql;
		}
	/**
	 * sharePointOverview.jsp上的查询
	 */
	  public Pager<Map<String, Object>> sharePointOverview(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
			Class clazz = ReportDao.class;
			String sqlFile = "sharePointOverview.sql";
			String sql = this.getSqlFromFile(clazz, sqlFile)  + this.createSharePointOverview(paramsFromJsp);
			System.out.println(sql);
			log.debug("sharePointOverview--sql----------:" + sql);
			Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
			return pager;
		}
	 
	/**
	 * sharePointOverview.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
     */
	  public String sharePointOverviewExport(Map<String, String> paramsFromJsp) {
			DataSource ds = this.getJdbcTemplate().getDataSource();
			Connection oracleConn = null;  
			Statement oracleStmt = null;  
			ResultSet oracleRs = null;
			String sqlFile = "sharePointOverview.sql";
			String csvFile = "sharePointOverview.csv";
			String path = "";
			
			try {
				path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
				log.debug("sharePointOverview-----path----------:" + path);
				
				String sql =this.getSqlFromFile(ReportDao.class,sqlFile)+ this.createSharePointOverview(paramsFromJsp);
				log.debug("sharePointOverview---sql-------:" + sql);
				
				oracleConn = ds.getConnection();
				oracleStmt = oracleConn.createStatement();
				oracleRs = oracleStmt.executeQuery(sql);

				FileWriter fileWriter;
		    	CSVPrinter csvPrinter = null;
		    	try {
		    		fileWriter = new FileWriter(path + csvFile);
		    		
		    		// 新建csv文件
		    		File file = new File(path + csvFile);
		    		FileOutputStream fos = new FileOutputStream(file);
		    		// 写BOM
		    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
		    		// 创建字节流输出对象
		    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		    		// Apache Commons CSV打印对象
		    		
		    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
		    		csvPrinter.printRecords(oracleRs);
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	} finally {
		    		try {
		    			if (csvPrinter != null) {
		    				csvPrinter.flush();
		    				csvPrinter.close();
		    			}
		    		} catch (IOException e) {
		    			e.printStackTrace();
		    		}
		    	}
		    	
			} catch (SQLException e) {
				e.printStackTrace();
			}  catch (URISyntaxException e1) {
				e1.printStackTrace();
			}  
			finally {
				try {
					if (oracleRs != null) {
						oracleRs.close();
					}
					if (oracleStmt != null) {
						oracleStmt.close();
					}
					if (oracleConn != null) {
						oracleConn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} 
			
			return path + csvFile;
		}
	    /**
		 * 为SharePointOverview 构造查询条件
		 */
	  public String createSharePointOverview(Map<String, String> paramsFromJsp) {
			//String companyCode = paramsFromJsp.get("companyCode");
			//String orderDateStartStr = paramsFromJsp.get("orderDateStartStr");
			//String orderDateEndStr = paramsFromJsp.get("orderDateEndStr");
			String memberNo = paramsFromJsp.get("memberNoStr");
			String sql = "";
//			if (companyCode != null && !companyCode.trim().equals("")) {
//				sql += " where tmo.company_code = '" + companyCode + "'";
//			}
//			if (orderDateStartStr != null && !orderDateStartStr.trim().equals("")) {
//				sql += " and tmo.order_date >= to_date('" + orderDateStartStr + "', 'yyyy-mm-dd')";
//			}
//			if (orderDateEndStr != null && !orderDateEndStr.trim().equals("")) {
//				sql += " and tmo.order_date < to_date('" + orderDateEndStr + "', 'yyyy-mm-dd') + 1";
//			}
			if (memberNo != null && !memberNo.trim().equals("")) {
				sql += " and member_no LIKE '%" + memberNo + "%'";
			}
			return sql;
		}
	/**
	 * personalPurchaseDetail.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> personalPurchaseDetail(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		Class clazz = ReportDao.class;
		String sqlFile = "personalPurchaseDetail.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile) + this.createPersonPurchaseDetail(paramsFromJsp);
		log.debug("personalPurchaseDetail--sql----------:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 personalPurchaseDetail.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
	 */
	public String personalPurchaseDetailExport(Map<String, String> paramsFromJsp) {
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection oracleConn = null;  
		Statement oracleStmt = null;  
		ResultSet oracleRs = null;
		String sqlFile = "personalPurchaseDetail.sql";
		String csvFile = "personalPurchaseDetail.csv";
		String path = "";
		
		try {
			path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
			log.debug("personalPurchaseDetailExport-----path----------:" + path);
			
			String sql = this.getSqlFromFile(ReportDao.class, sqlFile) + this.createPersonPurchaseDetail(paramsFromJsp);
			log.debug("personalPurchaseDetailExport---sql-------:" + sql);
			
			oracleConn = ds.getConnection();
			oracleStmt = oracleConn.createStatement();
			oracleRs = oracleStmt.executeQuery(sql);

			FileWriter fileWriter;
	    	CSVPrinter csvPrinter = null;
	    	try {
	    		fileWriter = new FileWriter(path + csvFile);
	    		
	    		// 新建csv文件
	    		File file = new File(path + csvFile);
	    		FileOutputStream fos = new FileOutputStream(file);
	    		// 写BOM
	    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
	    		// 创建字节流输出对象
	    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	    		// Apache Commons CSV打印对象
	    		
	    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
	    		csvPrinter.printRecords(oracleRs);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		try {
	    			if (csvPrinter != null) {
	    				csvPrinter.flush();
	    				csvPrinter.close();
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e1) {
			e1.printStackTrace();
		}  
		finally {
			try {
				if (oracleRs != null) {
					oracleRs.close();
				}
				if (oracleStmt != null) {
					oracleStmt.close();
				}
				if (oracleConn != null) {
					oracleConn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		
		return path + csvFile;
	}
	
	/**
	 * 为personalPurchaseDetail.sql 构造查询条件
	 */
	public String createPersonPurchaseDetail(Map<String, String> paramsFromJsp) {
		String orderDateStartStr = paramsFromJsp.get("orderDateStartStr");
		String orderDateEndStr = paramsFromJsp.get("orderDateEndStr");
		String memberNo = paramsFromJsp.get("memberNoStr");
		String sql = "";
		if (orderDateStartStr != null && !orderDateStartStr.trim().equals("")) {
			sql += " and tmo.order_date >= to_date('" + orderDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (orderDateEndStr != null && !orderDateEndStr.trim().equals("")) {
			sql += " and tmo.order_date < to_date('" + orderDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		if (memberNo != null && !memberNo.trim().equals("")) {
			sql += " and tmo.member_no LIKE '%" + memberNo + "%'";
		}
		sql += " order by tmo.order_date desc ";
		return sql;
	}
	
	/**
	 * dailySalesCSV.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> dailySalesCSV(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		Class clazz = ReportDao.class;
		String sqlFile = "dailySales.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile) + this.createSqlByCondition(paramsFromJsp);
		log.debug("dailySalesCSV--sql----------:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 dailySalesCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
	 */
	public String dailySalesExport(Map<String, String> paramsFromJsp) {
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection oracleConn = null;  
		Statement oracleStmt = null;  
		ResultSet oracleRs = null;
		String sqlFile = "dailySales.sql";
		String csvFile = "dailySales.csv";
		String path = "";
		
		try {
			path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
			log.debug("dailySalesExport-----path----------:" + path);
			
			String sql = this.getSqlFromFile(ReportDao.class, sqlFile) + this.createSqlByCondition(paramsFromJsp);
			log.debug("dailySalesExport---sql-------:" + sql);
			
			oracleConn = ds.getConnection();
			oracleStmt = oracleConn.createStatement();
			oracleRs = oracleStmt.executeQuery(sql);

			FileWriter fileWriter;
	    	CSVPrinter csvPrinter = null;
	    	try {
	    		fileWriter = new FileWriter(path + csvFile);
	    		
	    		// 新建csv文件
	    		File file = new File(path + csvFile);
	    		FileOutputStream fos = new FileOutputStream(file);
	    		// 写BOM
	    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
	    		// 创建字节流输出对象
	    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	    		// Apache Commons CSV打印对象
	    		
	    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
	    		csvPrinter.printRecords(oracleRs);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		try {
	    			if (csvPrinter != null) {
	    				csvPrinter.flush();
	    				csvPrinter.close();
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e1) {
			e1.printStackTrace();
		}  
		finally {
			try {
				if (oracleRs != null) {
					oracleRs.close();
				}
				if (oracleStmt != null) {
					oracleStmt.close();
				}
				if (oracleConn != null) {
					oracleConn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		
		return path + csvFile;
	}
	
	
	/**
	 * 为dailySalesCSV.jsp 构造查询条件
	 */
	public String createSqlByCondition(Map<String, String> paramsFromJsp) {
		String companyCode = paramsFromJsp.get("companyCode");
		String orderDateStartStr = paramsFromJsp.get("orderDateStartStr");
		String orderDateEndStr = paramsFromJsp.get("orderDateEndStr");
		String paymentDateStartStr = paramsFromJsp.get("paymentDateStartStr");
		String paymentDateEndStr = paramsFromJsp.get("paymentDateEndStr");
		String bonusDateStartStr = paramsFromJsp.get("bonusDateStartStr");
		String bonusDateEndStr = paramsFromJsp.get("bonusDateEndStr");
		String sql = "";
		if (companyCode != null && !companyCode.trim().equals("")) {
			sql += " and tod.company_code = '" + companyCode + "'";
		}
		if (orderDateStartStr != null && !orderDateStartStr.trim().equals("")) {
			sql += " and tod.order_date >= to_date('" + orderDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (orderDateEndStr != null && !orderDateEndStr.trim().equals("")) {
			sql += " and tod.order_date < to_date('" + orderDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (paymentDateStartStr != null && !paymentDateStartStr.trim().equals("")) {
			sql += " and tod.payment_date >= to_date('" + paymentDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (paymentDateEndStr != null && !paymentDateEndStr.trim().equals("")) {
			sql += " and tod.payment_date < to_date('" + paymentDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (bonusDateStartStr != null && !bonusDateStartStr.trim().equals("")) {
			sql += " and tod.bonus_date >= to_date('" + bonusDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (bonusDateEndStr != null && !bonusDateEndStr.trim().equals("")) {
			sql += " and tod.bonus_date < to_date('" + bonusDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		sql += " order by tod.order_date desc ";
		return sql;
	}
	
	/**
	 detailProductSalesCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> detailProductSalesCSV(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType, int currentPage, int pageSize) {
		Class clazz = ReportDao.class;
		String sqlFile = "detailProductSales.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile);
		String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStartStr, 
				orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
		String returnOrderInnerQueryCondition = this.getDetailReturnInerQryCon(companyCode, orderDateStartStr, 
				orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
		sql = sql.replaceAll("innerQueryCondition", innerQueryCondition);
		sql = sql.replaceAll("returnOrderInnerQueryCondition", returnOrderInnerQueryCondition);
		if (productType != null && !productType.trim().equals("")) {
			sql += " and tpp.product_type = " + productType;
		}
		//sql += " order by tod.doc_no desc ";
				
		log.debug("detailProductSalesCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
//		public Pager<Map<String, Object>> queryForPager(String sql, int currentPage, int pageSize,List<Object> paramList)
		return pager;
	}
	
	/**
	 detailProductSalesCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
	 */
	public String getDetailProductFileName(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType) {
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection oracleConn = null;  
		Statement oracleStmt = null;  
		ResultSet oracleRs = null;
		String sqlFile = "detailProductSales.sql";
		String csvFile = "detailProductSales.csv";
		String path = "";
		
		try {
			path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
			log.debug("getDetailProductResultSet-----path----------:" + path);
			
			/*String sql = this.getSqlFromFile(ReportDao.class, sqlFile) 
					+ this.createDetailProductSql(companyCode, orderDateStartStr, orderDateEndStr, paymentDateStartStr, 
					paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);*/
			String sql = this.getSqlFromFile(ReportDao.class, sqlFile);
			String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStartStr, 
					orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
			String returnOrderInnerQueryCondition = this.getDetailReturnInerQryCon(companyCode, orderDateStartStr, 
					orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
			sql = sql.replaceAll("innerQueryCondition", innerQueryCondition);
			sql = sql.replaceAll("returnOrderInnerQueryCondition", returnOrderInnerQueryCondition);
			if (productType != null && !productType.trim().equals("")) {
				sql += " and tpp.product_type = " + productType;
			}
//			sql += " order by tod.doc_no desc ";
			
			log.debug("getDetailProductResultSet---sql-------:" + sql);
			
			oracleConn = ds.getConnection();
			oracleStmt = oracleConn.createStatement();
			oracleRs = oracleStmt.executeQuery(sql);

			FileWriter fileWriter;
	    	CSVPrinter csvPrinter = null;
	    	try {
	    		fileWriter = new FileWriter(path + csvFile);
	    		
	    		// 新建csv文件
	    		File file = new File(path + csvFile);
	    		FileOutputStream fos = new FileOutputStream(file);
	    		// 写BOM
	    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
	    		// 创建字节流输出对象
	    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	    		// Apache Commons CSV打印对象
	    		
	    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
	    		csvPrinter.printRecords(oracleRs);
	    		
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		try {
	    			if (csvPrinter != null) {
	    				csvPrinter.flush();
	    				csvPrinter.close();
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e1) {
			e1.printStackTrace();
		}  
		finally {
			try {
				if (oracleRs != null) {
					oracleRs.close();
				}
				if (oracleStmt != null) {
					oracleStmt.close();
				}
				if (oracleConn != null) {
					oracleConn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		
		return path + csvFile;
	}
	
	/**
	 * 根据jsp页面的条件构造 DetailProduct Sql
	 */
	public String createDetailProductSql(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType) {
		String sql = "";
		if (companyCode != null && !companyCode.trim().equals("")) {
			sql += " and tod.company_code = '" + companyCode + "'";
		}
		if (orderDateStartStr != null && !orderDateStartStr.trim().equals("")) {
			sql += " and tod.order_date >= to_date('" + orderDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (orderDateEndStr != null && !orderDateEndStr.trim().equals("")) {
			sql += " and tod.order_date < to_date('" + orderDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (paymentDateStartStr != null && !paymentDateStartStr.trim().equals("")) {
			sql += " and tod.payment_date >= to_date('" + paymentDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (paymentDateEndStr != null && !paymentDateEndStr.trim().equals("")) {
			sql += " and tod.payment_date < to_date('" + paymentDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (bonusDateStartStr != null && !bonusDateStartStr.trim().equals("")) {
			sql += " and tod.bonus_date >= to_date('" + bonusDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (bonusDateEndStr != null && !bonusDateEndStr.trim().equals("")) {
			sql += " and tod.bonus_date < to_date('" + bonusDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		if (productType != null && !productType.trim().equals("")) {
			sql += " and tpp.product_type = " + productType;
		}
		
		sql += " order by tod.doc_no desc ";
		log.debug("createDetailProductSql-----sql-----:" + sql);
		return sql;
	}
	
	/**
	 * 	 为 detailProductSales.sql 文件里的 innerQueryCondition 构造查询条件
	 * @param clazz
	 * @param fileName
	 * @return
	 */
	public String getDetailInerQryCon(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType) {
		String sql = "";
		if (companyCode != null && !companyCode.trim().equals("")) {
			sql += " and toa.company_code = '" + companyCode + "'";
		}
		if (orderDateStartStr != null && !orderDateStartStr.trim().equals("")) {
			sql += " and toa.order_date >= to_date('" + orderDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (orderDateEndStr != null && !orderDateEndStr.trim().equals("")) {
			sql += " and toa.order_date < to_date('" + orderDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (paymentDateStartStr != null && !paymentDateStartStr.trim().equals("")) {
			sql += " and toa.payment_date >= to_date('" + paymentDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (paymentDateEndStr != null && !paymentDateEndStr.trim().equals("")) {
			sql += " and toa.payment_date < to_date('" + paymentDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (bonusDateStartStr != null && !bonusDateStartStr.trim().equals("")) {
			sql += " and toa.bonus_date >= to_date('" + bonusDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (bonusDateEndStr != null && !bonusDateEndStr.trim().equals("")) {
			sql += " and toa.bonus_date < to_date('" + bonusDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		log.debug("getDetailInerQryCon-----sql-----:" + sql);
		return sql;
	}
	
	/**
	 * 	 为 detailProductSales.sql 文件里的 returnOrderInnerQueryCondition 构造查询条件
	 * @param clazz
	 * @param fileName
	 * @return
	 */
	public String getDetailReturnInerQryCon(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType) {
		String sql = "";
			        
		if (companyCode != null && !companyCode.trim().equals("")) {
			sql += " and tob.company_code = '" + companyCode + "'";
		}
		if (orderDateStartStr != null && !orderDateStartStr.trim().equals("")) {
			sql += " and tr.CREATE_DATE >= to_date('" + orderDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (orderDateEndStr != null && !orderDateEndStr.trim().equals("")) {
			sql += " and tr.CREATE_DATE < to_date('" + orderDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (paymentDateStartStr != null && !paymentDateStartStr.trim().equals("")) {
			sql += " and tob.payment_date >= to_date('" + paymentDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (paymentDateEndStr != null && !paymentDateEndStr.trim().equals("")) {
			sql += " and tob.payment_date < to_date('" + paymentDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		
		if (bonusDateStartStr != null && !bonusDateStartStr.trim().equals("")) {
			sql += " and tob.bonus_date >= to_date('" + bonusDateStartStr + "', 'yyyy-mm-dd')";
		}
		if (bonusDateEndStr != null && !bonusDateEndStr.trim().equals("")) {
			sql += " and tob.bonus_date < to_date('" + bonusDateEndStr + "', 'yyyy-mm-dd') + 1";
		}
		log.debug("getDetailReturnInerQryCon-----sql-----:" + sql);
		return sql;
	}
	
	/**
	logisticsReportCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> logisticsReportCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		String companyCode = ""; 
    	String orderDateStart = "";
    	String orderDateEnd = ""; 
    	String paymentDateStart = ""; 
    	String paymentDateEnd = ""; 
    	String bonusDateStart = ""; 
    	String bonusDateEnd = ""; 
    	String orderStatus = ""; 
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("Country")) {
						companyCode = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateStart")) {
						orderDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateEnd")) {
						orderDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateStart")) {
						paymentDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateEnd")) {
						paymentDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateStart")) {
						bonusDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateEnd")) {
						bonusDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderStatus")) {
						orderStatus = sb.value.toString();
					}
				}
			}
		}
		String orderStatusSql = "";
		if (orderStatus != null && !orderStatus.trim().equals("")) {
			orderStatusSql += " and toa.order_Status = '" + orderStatus + "'";
		}
		String returnOrderStatusSql = "";
		if (orderStatus != null && !orderStatus.trim().equals("")) {
			returnOrderStatusSql += " and tob.order_Status = '" + orderStatus + "'";
		}
		Class clazz = ReportDao.class;
		String sqlFile = "LogisticsReport.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile);
		String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStart, 
				orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, "");
		String returnOrderInnerQueryCondition = this.getDetailReturnInerQryCon(companyCode, orderDateStart, 
				orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, "");
		sql = sql.replaceAll("innerQueryCondition", innerQueryCondition + orderStatusSql);
		sql = sql.replaceAll("returnOrderInnerQueryCondition", returnOrderInnerQueryCondition + returnOrderStatusSql);
		//sql += " order by tod.doc_no desc ";
				
		log.debug("logisticsReportCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 logisticsReportCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportLogisticsReport(List<SearchBean> searchBeanList) {
		String companyCode = ""; 
    	String orderDateStart = "";
    	String orderDateEnd = ""; 
    	String paymentDateStart = ""; 
    	String paymentDateEnd = ""; 
    	String bonusDateStart = ""; 
    	String bonusDateEnd = ""; 
    	String orderStatus = ""; 
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("Country")) {
						companyCode = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateStart")) {
						orderDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateEnd")) {
						orderDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateStart")) {
						paymentDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateEnd")) {
						paymentDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateStart")) {
						bonusDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateEnd")) {
						bonusDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderStatus")) {
						orderStatus = sb.value.toString();
					}
				}
			}
		}
		String orderStatusSql = "";
		if (orderStatus != null && !orderStatus.trim().equals("")) {
			orderStatusSql += " and toa.order_Status = '" + orderStatus + "'";
		}
		String returnOrderStatusSql = "";
		if (orderStatus != null && !orderStatus.trim().equals("")) {
			returnOrderStatusSql += " and tob.order_Status = '" + orderStatus + "'";
		}
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection oracleConn = null;  
		Statement oracleStmt = null;  
		ResultSet oracleRs = null;
		String sqlFile = "LogisticsReport.sql";
		String csvFile = "LogisticsReport.csv";
		String path = "";
		
		try {
			path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
			log.debug("getDetailProductResultSet-----path----------:" + path);
			
			String sql = this.getSqlFromFile(ReportDao.class, sqlFile);
			String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStart, 
					orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, "");
			String returnOrderInnerQueryCondition = this.getDetailReturnInerQryCon(companyCode, orderDateStart, 
					orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, "");
			sql = sql.replaceAll("innerQueryCondition", innerQueryCondition + orderStatusSql);
			sql = sql.replaceAll("returnOrderInnerQueryCondition", returnOrderInnerQueryCondition + returnOrderStatusSql);
			sql += " order by tod.doc_no desc ";
			
			log.debug("exportLogisticsReport---sql-------:" + sql);
			
			oracleConn = ds.getConnection();
			oracleStmt = oracleConn.createStatement();
			oracleRs = oracleStmt.executeQuery(sql);

			FileWriter fileWriter;
	    	CSVPrinter csvPrinter = null;
	    	try {
	    		fileWriter = new FileWriter(path + csvFile);
	    		
	    		// 新建csv文件
	    		File file = new File(path + csvFile);
	    		FileOutputStream fos = new FileOutputStream(file);
	    		// 写BOM
	    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
	    		// 创建字节流输出对象
	    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	    		// Apache Commons CSV打印对象
	    		
	    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
	    		csvPrinter.printRecords(oracleRs);
	    		
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		try {
	    			if (csvPrinter != null) {
	    				csvPrinter.flush();
	    				csvPrinter.close();
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e1) {
			e1.printStackTrace();
		}  
		finally {
			try {
				if (oracleRs != null) {
					oracleRs.close();
				}
				if (oracleStmt != null) {
					oracleStmt.close();
				}
				if (oracleConn != null) {
					oracleConn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		
		return path + csvFile;
	}
	
	public String[] getParamsOfSales(List<SearchBean> searchBeanList) {
		String companyCode = ""; 
		String orderDateStart = "";
		String orderDateEnd = ""; 
		String paymentDateStart = ""; 
		String paymentDateEnd = ""; 
		String bonusDateStart = ""; 
		String bonusDateEnd = ""; 
		String orderType = ""; 
		String bvStart = ""; 
		String bvEnd = ""; 
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("Country")) {
						companyCode = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateStart")) {
						orderDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateEnd")) {
						orderDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateStart")) {
						paymentDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateEnd")) {
						paymentDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateStart")) {
						bonusDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateEnd")) {
						bonusDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderType")) {
						orderType = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bvStart")) {
						bvStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bvEnd")) {
						bvEnd = sb.value.toString();
					}
				}
			}
		}
		/*
		 * order_date_s, order_date_e, payment_date_s, payment_date_e, 
		 * bonus_date_s, bonus_date_e, company_code, order_type, total_bv_s,  total_bv_e
		 */
		String[] params = 
			{orderDateStart, orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, companyCode, orderType, bvStart, bvEnd};
		return params;
	}
	
	public String[] getParamsOfDistributor(List<SearchBean> searchBeanList) {
		String companyCode = ""; 
		String orderDateStart = "";
		String orderDateEnd = ""; 
		String paymentDateStart = ""; 
		String paymentDateEnd = ""; 
		String bonusDateStart = ""; 
		String bonusDateEnd = ""; 
		String orderType = ""; 
		String bvStart = ""; 
		String bvEnd = ""; 
		String networkType = ""; 
		String networkValue = ""; 
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("Country")) {
						companyCode = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateStart")) {
						orderDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateEnd")) {
						orderDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateStart")) {
						paymentDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateEnd")) {
						paymentDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateStart")) {
						bonusDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateEnd")) {
						bonusDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderType")) {
						orderType = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bvStart")) {
						bvStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bvEnd")) {
						bvEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("networkType")) {
						networkType = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("networkValue")) {
						networkValue = sb.value.toString();
					}
				}
			}
		}
		/*
		 * order_date_s, order_date_e, payment_date_s, payment_date_e, 
		 * bonus_date_s, bonus_date_e, company_code, order_type, total_bv_s, total_bv_e, net_work_type, net_work_value
		 */
		String[] params = 
			{orderDateStart, orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, 
			companyCode, orderType, bvStart, bvEnd, networkType, networkValue};
		return params;
	}
	
	/**
	SalesReportAtTheGlaceCSV.jsp 页面上的“查询”
	 * @throws Exception 
	 */
	public Pager<Map<String, Object>> SalesReportAtTheGlaceCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize)  {
		String[] params = this.getParamsOfSales(searchBeanList);
		try {
			this.invokeSalesReportAtTheGlace(params);
		} catch (Exception e) {
			log.error("SalesReportAtTheGlaceCSV--ex--:", e);
		}
		String sql = "select t.id, t.tname, t.qty, t.bv, t.localcurrency, t.usd from t_SalesReportAtTheGlace t order by t.id";
		log.debug("SalesReportAtTheGlaceCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 SalesReportAtTheGlaceCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportSalesReportAtTheGlace(List<SearchBean> searchBeanList) {
		String[] params = this.getParamsOfSales(searchBeanList);
		try {
			this.invokeSalesReportAtTheGlace(params);
		} catch (Exception e) {
			log.error("SalesReportAtTheGlaceCSV--ex--:", e);
		}
		String sql = "select t.tname, t.qty, t.bv, t.localcurrency, t.usd from t_SalesReportAtTheGlace t order by t.id";
		log.debug("exportSalesReportAtTheGlace---sql-------:" + sql);
		String csvFile = "SalesReportAtTheGlace.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportSalesReportAtTheGlace---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	DistributorGroupAtTheGlaceCSV.jsp 页面上的“查询”
	 * @throws Exception 
	 */
	public Pager<Map<String, Object>> DistributorGroupAtTheGlaceCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize)  {
		String[] params = this.getParamsOfDistributor(searchBeanList);
		try {
			this.invokeDistributorGroupGlace(params);
		} catch (Exception e) {
			log.error("DistributorGroupAtTheGlaceCSV--ex--:", e);
		}
		String sql = "select t.id, t.tname, t.qty, t.bv, t.localcurrency, t.usd from t_DistributorGroupAtTheGlace t order by t.id";
		log.debug("DistributorGroupAtTheGlaceCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 DistributorGroupAtTheGlaceCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDistributorGroupAtTheGlace(List<SearchBean> searchBeanList) {
		String[] params = this.getParamsOfDistributor(searchBeanList);
		try {
			this.invokeDistributorGroupGlace(params);
		} catch (Exception e) {
			log.error("DistributorGroupAtTheGlaceCSV--ex--:", e);
		}
		String sql = "select t.tname, t.qty, t.bv, t.localcurrency, t.usd from t_DistributorGroupAtTheGlace t order by t.id";
		log.debug("exportDistributorGroupAtTheGlace---sql-------:" + sql);
		String csvFile = "DistributorGroupAtTheGlace.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportDistributorGroupAtTheGlace---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	public String[] getParamsOfDistributorGroupTop(List<SearchBean> searchBeanList) {
		String bonusType = ""; 
		String workingStageS = "";
		String workingStageE = ""; 
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("bonusType")) {
						bonusType = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("workingStageS")) {
						workingStageS = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("workingStageE")) {
						workingStageE = sb.value.toString();
					}
				}
			}
		}
		//sp_bonusType_EQ  sp_workingStageS_GE  sp_workingStageE_LE
		String[] params = {bonusType, workingStageS, workingStageE};
		return params;
	}
	
	public String createSqlForDistributorGroupTop(String[] params) {
		String sql = "select t.member_code, t.start_bonus_bv as start_bonus,                                             "
				+ "t.organizational_bonus_bv as organizational_bonus, t.leader_bonus_bv as leader_bonus,                   "
 + "'' as pew_bonus, '' as superpin_bonus, '' as crown_bonus,                                                              "
 + "t.deduction_reissue_bv, t.total_bonus_bv, round(nvl(t.total_bonus_bv, 0) * nvl(t.exchange_rate, 0), 2) as local_total  "
 + " from bs_wbonus_detail t, bs_bonus_issue tb                                                                            "
// + " where t.operation_id = tb.id                                                                     "
 + " where t.operation_id = tb.id and tb.status = 1                                                                        "
 + " and tb.bonus_type = '1' ";
 String sqlCon = "";
 String sqlOderBy = " order by t.total_bonus_bv desc";
 String sqlFinal = "";
		 if (params[0].equals("2")) {                                                                                            
			 sql = "  select t.member_code, '' as start_bonus, '' as organizational_bonus, '' as leader_bonus,                             "
				  + " t.pew_bonus_bv as pew_bonus, t.superpin_bonus_bv as superpin_bonus, t.crown_bonus_bv as crown_bonus,                  "
				  + " t.deduction_reissue_bv, t.total_bonus_bv, round(nvl(t.total_bonus_bv, 0) * nvl(t.exchange_rate, 0), 2) as local_total " 
				  + "  from bs_mbonus_detail t, bs_bonus_issue tb                                                                           "
//				  + "  where t.operation_id = tb.id                                                                        "
				  + "  where t.operation_id = tb.id and tb.status = 1                                                                       "
				  + "  and tb.bonus_type = '2'                                                                                              ";
		 }
 		if (StringUtils.isNotBlank(params[1])) {
 			sqlCon += " and t.working_stage >= '" + params[1] + "'";
 		}
 		if (StringUtils.isNotBlank(params[2])) {
 			sqlCon += " and t.working_stage <= '" + params[2] + "'";
 		}
 		
		sqlFinal = sql + sqlCon + sqlOderBy;
		log.debug("DistributorGroupTop-----sqlFinal-----:" + sqlFinal);
		return sqlFinal;
	}
	
	/**
	DistributorGroupTop.jsp 页面上的“查询”
	 * @throws Exception 
	 */
	public Pager<Map<String, Object>> DistributorGroupTop(List<SearchBean> searchBeanList, int currentPage, int pageSize)  {
		String[] params = this.getParamsOfDistributorGroupTop(searchBeanList);
		String sqlFinal = this.createSqlForDistributorGroupTop(params);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sqlFinal, currentPage, pageSize);
		return pager;
	}
	
	/**
	 DistributorGroupTop.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDistributorGroupTop(List<SearchBean> searchBeanList) {
		String[] params = this.getParamsOfDistributorGroupTop(searchBeanList);
		String sqlFinal = this.createSqlForDistributorGroupTop(params);
		log.debug("exportDistributorGroupTop---sqlFinal-------:" + sqlFinal);
		String csvFile = "DistributorGroupTop.csv";
		String csvFullPath = this.getCsvFullPath(sqlFinal, csvFile);
		log.debug("exportDistributorGroupTop---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	distributorGroupSalesCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> distributorGroupSalesCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		String sql = this.getDistributorGroupSalesSql(searchBeanList);
		log.debug("distributorGroupSalesCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	public String getDistributorGroupSalesSql(List<SearchBean> searchBeanList) {
		String companyCode = ""; 
		String orderDateStart = "";
		String orderDateEnd = ""; 
		String paymentDateStart = ""; 
		String paymentDateEnd = ""; 
		String bonusDateStart = ""; 
		String bonusDateEnd = ""; 
		String sponsorNetwork = ""; 
		String placementNetwork = ""; 
		
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("Country")) {
						companyCode = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateStart")) {
						orderDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateEnd")) {
						orderDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateStart")) {
						paymentDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateEnd")) {
						paymentDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateStart")) {
						bonusDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateEnd")) {
						bonusDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("sponsorNetwork")) {
						sponsorNetwork = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("placementNetwork")) {
						placementNetwork = sb.value.toString();
					}
				}
			}
		}
		String sponsorNetworkSql = "";
		if (sponsorNetwork != null && !sponsorNetwork.trim().equals("")) {
			//and tod.member_id in (select tmm.id from mm_member tmm start with tmm.member_no = 'MY702597' connect by tmm.sponsor_id = prior tmm.id) 
			sponsorNetworkSql += " and tod.member_id in (select tmm.id from mm_member tmm where tmm.subtype = 40 start with tmm.member_no = '" + sponsorNetwork + "' connect by tmm.sponsor_id = prior tmm.id)  " ;
		}
		String placementNetworkSql = "";
		if (placementNetwork != null && !placementNetwork.trim().equals("")) {
			//and tod.member_id in (select tmm.id from mm_member tmm start with tmm.member_no = 'MY702597' connect by tmm.placement_id = prior tmm.id)   
			placementNetworkSql += " and tod.member_id in (select tmm.id from mm_member tmm where tmm.subtype = 40 start with tmm.member_no = '" + placementNetwork + "' connect by tmm.placement_id = prior tmm.id)  " ;
		}
		Class clazz = ReportDao.class;
		String sqlFile = "distributorGroupSalesCSV.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile);
		String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStart, 
				orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, "");
		log.debug("getDistributorGroupSalesSql--innerQueryCondition------:" + innerQueryCondition);
		String innerQueryConditionTemp = innerQueryCondition.replaceAll("toa", "tod");
		log.debug("getDistributorGroupSalesSql--innerQueryConditionTemp------:" + innerQueryConditionTemp);
		sql = sql.replaceAll("innerQueryCondition", innerQueryCondition);
		sql = sql.replaceAll("innerQueryLastCondition", innerQueryConditionTemp + sponsorNetworkSql + placementNetworkSql);
		sql += " order by tod.doc_no desc ";
		
		return sql;
	}
	
	/**
	 * 调用存储过程
	 * @throws Exception
	 */
	public void invokeSalesReportAtTheGlace(String[] params) throws Exception {
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection conn = null;
		CallableStatement callStmt = null;
		try {
			conn = ds.getConnection();
			callStmt = conn.prepareCall("{call Proc_SalesReportAtTheGlace(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			/*
			 * order_date_s, order_date_e, payment_date_s, payment_date_e, 
			 * bonus_date_s, bonus_date_e, company_code, order_type, total_bv_s,  total_bv_e
			 */
			// 参数index从1开始，依次 1,2,3...
			callStmt.setString(1, params[0]);
			callStmt.setString(2, params[1]);
			callStmt.setString(3, params[2]);
			callStmt.setString(4, params[3]);
			callStmt.setString(5, params[4]);
			callStmt.setString(6, params[5]);
			callStmt.setString(7, params[6]);
			callStmt.setString(8, params[7]);
			callStmt.setString(9, params[8]);
			callStmt.setString(10, params[9]);
			callStmt.execute();
		} catch (Exception e) {
			log.error("invokeSalesReportAtTheGlace--error---:", e);
		} finally {
			if (null != callStmt) {
				callStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	/**
	 * 调用存储过程
	 * @throws Exception
	 */
	public void invokeDistributorGroupGlace(String[] params) throws Exception {
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection conn = null;
		CallableStatement callStmt = null;
		try {
			conn = ds.getConnection();
			callStmt = conn.prepareCall("{call Proc_DistributorGroupGlace(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			/*
			 * order_date_s, order_date_e, payment_date_s, payment_date_e, 
			 * bonus_date_s, bonus_date_e, company_code, order_type, total_bv_s,  total_bv_e
			 */
			// 参数index从1开始，依次 1,2,3...
			callStmt.setString(1, params[0]);
			callStmt.setString(2, params[1]);
			callStmt.setString(3, params[2]);
			callStmt.setString(4, params[3]);
			callStmt.setString(5, params[4]);
			callStmt.setString(6, params[5]);
			callStmt.setString(7, params[6]);
			callStmt.setString(8, params[7]);
			callStmt.setString(9, params[8]);
			callStmt.setString(10, params[9]);
			callStmt.setString(11, params[10]);
			callStmt.setString(12, params[11]);
			callStmt.execute();
		} catch (Exception e) {
			log.error("invokeDistributorGroupGlace--error---:", e);
		} finally {
			if (null != callStmt) {
				callStmt.close();
			}
			if (null != conn) {
				conn.close();
			}
		}
	}
	
	
	/**
	 公共的调用jbdc获取ResultSet，然后用apache commons csv 写到文件中，最后返回一个完整的路径名给 controller 下载
	 */
	public String getCsvFullPath(String sql, String csvFile) {
		log.debug(csvFile + "---sql-------:" + sql);
		log.debug(csvFile + "---csv-------:" + csvFile);
		
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection oracleConn = null;  
		Statement oracleStmt = null;  
		ResultSet oracleRs = null;
		String path = "";
		
		try {
			path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
			log.debug(csvFile + "-----path----------:" + path);
			
			oracleConn = ds.getConnection();
			oracleStmt = oracleConn.createStatement();
			oracleRs = oracleStmt.executeQuery(sql);
			CSVPrinter csvPrinter = null;
			try {
				// 新建csv文件
				File file = new File(path + csvFile);
				FileOutputStream fos = new FileOutputStream(file);
				// 写BOM
				fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
				// 创建字节流输出对象
				OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
				// Apache Commons CSV打印对象
				
				csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
				csvPrinter.printRecords(oracleRs);
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (csvPrinter != null) {
						csvPrinter.flush();
						csvPrinter.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e1) {
			e1.printStackTrace();
		}  
		finally {
			try {
				if (oracleRs != null) {
					oracleRs.close();
				}
				if (oracleStmt != null) {
					oracleStmt.close();
				}
				if (oracleConn != null) {
					oracleConn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		
		return path + csvFile;
	}
	/**
	 distributorGroupSalesCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDistributorGroupReport(List<SearchBean> searchBeanList) {
		String sql = this.getDistributorGroupSalesSql(searchBeanList);
		log.debug("exportDistributorGroupReport---sql-------:" + sql);
		String csvFile = "distributorGroupSales.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportDistributorGroupReport---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	detailSignalProductCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> detailSignalProductCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		String sql = this.getDetailSignalProductSql(searchBeanList);
		log.debug("detailSignalProductCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 detailSignalProductCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDetailSignalProduct(List<SearchBean> searchBeanList) {
		String sql = this.getDetailSignalProductSql(searchBeanList);
		log.debug("exportDetailSignalProduct---sql-------:" + sql);
		String csvFile = "detailSignalProduct.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportDetailSignalProduct---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	/**
	memberInfoCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> memberInfoCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		String sql = this.getMemberInfoSql(searchBeanList);
		log.debug("memberInfoCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 memberInfoCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportMemberInfo(List<SearchBean> searchBeanList) {
		String sql = this.getMemberInfoSql(searchBeanList);
		log.debug("exportMemberInfo---sql-------:" + sql);
		String csvFile = "memberInfoCSV.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportMemberInfo---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	 bonusList.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportBonusList(List<SearchBean> searchBeanList) {
		String sql = "select t.* from bs_year_detail t";
		log.debug("exportBonusList---sql-------:" + sql);
		String csvFile = "bonusList.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportBonusList---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	 bonusList.jsp 页面上的“季度奖金导出”，返回 文件完整路径名给 controller
	 */
	public String exportQuarterBonusList(List<SearchBean> searchBeanList) {
		String sql = "select t.* from bs_quarter_detail t";
		log.debug("exportQuarterBonusList---sql-------:" + sql);
		String csvFile = "quarterBonusList.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportQuarterBonusList---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	 yearBonusList.jsp “年度奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportYearBonusList(Map<String, Object> searchParams) {
		String status_EQ = searchParams.get("status_EQ") != null ? searchParams.get("status_EQ").toString() : "";
		String companyCode_EQ = searchParams.get("companyCode_EQ") != null ? searchParams.get("companyCode_EQ").toString() : "";
		String monthlyWorking_GTE = searchParams.get("monthlyWorking_GTE") != null ? searchParams.get("monthlyWorking_GTE").toString() : "";
		String monthlyWorking_LTE = searchParams.get("monthlyWorking_LTE") != null ? searchParams.get("monthlyWorking_LTE").toString() : "";
		String workingStage_LIKE = searchParams.get("workingStage_LIKE") != null ? searchParams.get("workingStage_LIKE").toString() : "";
		String memberCode_LIKE = searchParams.get("memberCode_LIKE") != null ? searchParams.get("memberCode_LIKE").toString() : "";
		/*
		 年度奖金发放sql：
		 select t.working_stage, t.version_no, t.monthly_working, t.member_code, t.member_name, t.crown_bonus_bv,
		 t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_time from bs_year_detail t 
		 where t.status = 0 and t.company_code = 'TW' and t.monthly_working >= '201501' and t.monthly_working <= '201512';
		 to_char(t.create_time, 'yyyy-mm-dd hh24:mi:ss') as create_time
		*/
		String sql = "select t.working_stage, t.version_no, t.monthly_working, t.member_code, t.member_name, t.crown_bonus_bv,"
		+ " t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_User, to_char(t.create_time, 'yyyy-mm-dd hh24:mi:ss') as create_time from bs_year_detail t where 1 = 1 ";
		if (StringUtils.isNotBlank(status_EQ)) {
			sql += " and t.status = " + status_EQ;
		}
		if (StringUtils.isNotBlank(companyCode_EQ)) {
			sql += " and t.company_code = '" + companyCode_EQ + "'";
		}
		if (StringUtils.isNotBlank(monthlyWorking_GTE)) {
			sql += " and t.monthly_working >= '" + monthlyWorking_GTE + "'";
		}
		if (StringUtils.isNotBlank(monthlyWorking_LTE)) {
			sql += " and t.monthly_working <= '" + monthlyWorking_LTE + "'";
		}
		if (StringUtils.isNotBlank(workingStage_LIKE)) {
			sql += " and t.working_stage like '%" + workingStage_LIKE + "%'";
		}
		if (StringUtils.isNotBlank(memberCode_LIKE)) {
			sql += " and t.member_code like '%" + memberCode_LIKE + "%'";
		}
		
		log.debug("exportYearBonusList---sql-------:" + sql);
		String csvFile = "yearBonusList.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportYearBonusList---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	 quarterBonusList.jsp “季度奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportQuarterBonus(Map<String, Object> searchParams) {
		String status_EQ = searchParams.get("status_EQ") != null ? searchParams.get("status_EQ").toString() : "";
		String companyCode_EQ = searchParams.get("companyCode_EQ") != null ? searchParams.get("companyCode_EQ").toString() : "";
		String monthlyWorking_IN = searchParams.get("monthlyWorking_IN") != null ? searchParams.get("monthlyWorking_IN").toString() : "";
		String workingStage_LIKE = searchParams.get("workingStage_LIKE") != null ? searchParams.get("workingStage_LIKE").toString() : "";
		String memberCode_LIKE = searchParams.get("memberCode_LIKE") != null ? searchParams.get("memberCode_LIKE").toString() : "";
		/*
		 季度奖金发放sql：
  select t.working_stage, t.version_no, t.monthly_working, t.member_code, t.member_name, t.samsung_bonus_bv,
  t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_User, t.create_time from bs_quarter_detail t 
  where t.status = 0 and t.company_code = 'TW' and t.monthly_working in (201501, 201502, 201503);
		*/
		String sql = "select t.working_stage, t.version_no, t.monthly_working, t.member_code, t.member_name, t.samsung_bonus_bv,"
  + " t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_User, to_char(t.create_time, 'yyyy-mm-dd hh24:mi:ss') as create_time from bs_quarter_detail t  where 1 = 1 ";
		if (StringUtils.isNotBlank(status_EQ)) {
			sql += " and t.status = " + status_EQ;
		}
		if (StringUtils.isNotBlank(companyCode_EQ)) {
			sql += " and t.company_code = '" + companyCode_EQ + "'";
		}
		if (StringUtils.isNotBlank(monthlyWorking_IN)) {
			sql += " and t.monthly_working in (" + monthlyWorking_IN + ")";
		}
		
		if (StringUtils.isNotBlank(workingStage_LIKE)) {
			sql += " and t.working_stage like '%" + workingStage_LIKE + "%'";
		}
		if (StringUtils.isNotBlank(memberCode_LIKE)) {
			sql += " and t.member_code like '%" + memberCode_LIKE + "%'";
		}
		
		log.debug("exportQuarterBonus---sql-------:" + sql);
		String csvFile = "exportQuarterBonus.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportQuarterBonus---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	 monthBonusList.jsp “月度奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportMonthBonus(Map<String, Object> searchParams) {
		String status_EQ = searchParams.get("bonusIssue.status_EQ") != null ? searchParams.get("bonusIssue.status_EQ").toString() : "";
		String companyCode_EQ = searchParams.get("companyCode_EQ") != null ? searchParams.get("companyCode_EQ").toString() : "";
		String versionNo_EQ = searchParams.get("versionNo_EQ") != null ? searchParams.get("versionNo_EQ").toString() : "";
		String workingStage_EQ = searchParams.get("workingStage_EQ") != null ? searchParams.get("workingStage_EQ").toString() : "";
		String memberCode_EQ = searchParams.get("memberCode_EQ") != null ? searchParams.get("memberCode_EQ").toString() : "";
	
		/*
		 月度奖金发放sql：
  select t.working_stage, t.version_no, t.monthly_working, t.member_code, t.member_name, t.pew_bonus_bv,
          t.superpin_bonus_bv, t.total_bonus_bv, t.deduction_reissue_bv, 
  t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_User, t.create_time 
  from bs_mbonus_detail t, bs_bonus_issue tb where t.operation_id = tb.id and t.working_stage = ''
  and t.version_no = '' and t.company_code = '' and t.member_code = '' and tb.status = 0;
		*/
		String sql = "select t.working_stage, t.version_no, t.monthly_working, t.member_code, t.member_name, t.pew_bonus_bv,"
          + " t.superpin_bonus_bv, t.total_bonus_bv, t.deduction_reissue_bv, "
          + " t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_User, to_char(t.create_time, 'yyyy-mm-dd hh24:mi:ss') as create_time "
          + " from bs_mbonus_detail t, bs_bonus_issue tb where t.operation_id = tb.id ";
		if (StringUtils.isNotBlank(status_EQ)) {
			sql += " and tb.status = " + status_EQ;
		}
		if (StringUtils.isNotBlank(companyCode_EQ)) {
			sql += " and t.company_code = '" + companyCode_EQ + "'";
		}
		if (StringUtils.isNotBlank(versionNo_EQ)) {
			sql += " and t.version_no = " + versionNo_EQ;
		}
		
		if (StringUtils.isNotBlank(workingStage_EQ)) {
			sql += " and t.working_stage like '%" + workingStage_EQ + "%'";
		}
		if (StringUtils.isNotBlank(memberCode_EQ)) {
			sql += " and t.member_code like '%" + memberCode_EQ + "%'";
		}
		
		log.debug("exportMonthBonus---sql-------:" + sql);
		String csvFile = "exportMonthBonus.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportMonthBonus---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	 weekBonusList.jsp “周奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportWeekBonus(Map<String, Object> searchParams) {
		String status_EQ = searchParams.get("bonusIssue.status_EQ") != null ? searchParams.get("bonusIssue.status_EQ").toString() : "";
		String companyCode_EQ = searchParams.get("companyCode_EQ") != null ? searchParams.get("companyCode_EQ").toString() : "";
		String versionNo_EQ = searchParams.get("versionNo_EQ") != null ? searchParams.get("versionNo_EQ").toString() : "";
		String workingStage_EQ = searchParams.get("workingStage_EQ") != null ? searchParams.get("workingStage_EQ").toString() : "";
		String memberCode_EQ = searchParams.get("memberCode_EQ") != null ? searchParams.get("memberCode_EQ").toString() : "";
	
		/*
		 周奖金待发送列表查询sql：
select t.working_stage, t.version_no, t.monthly_working, t.weekly_working, t.member_code, t.member_name, 
          t.retail_profit_bv, t.start_bonus_bv, t.organizational_bonus_bv, t.leader_bonus_bv, t.total_bonus_bv,
          t.deduction_reissue_bv, t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_User, t.create_time 
  from bs_wbonus_detail t, bs_bonus_issue tb where t.operation_id = tb.id and t.working_stage = ''
  and t.version_no = '' and t.company_code = '' and t.member_code = '' and tb.status = 0;
		*/
		String sql = "select t.working_stage, t.version_no, t.monthly_working, t.weekly_working, t.member_code, t.member_name,     "
 + "         t.retail_profit_bv, t.start_bonus_bv, t.organizational_bonus_bv, t.leader_bonus_bv, t.total_bonus_bv,                 "
 + "         t.deduction_reissue_bv, t.total_bonus, t.tax, t.insurance, t.net_bonus, t.exchange_rate, t.create_User, to_char(t.create_time, 'yyyy-mm-dd hh24:mi:ss') as create_time "
 + " from bs_wbonus_detail t, bs_bonus_issue tb where t.operation_id = tb.id ";
		if (StringUtils.isNotBlank(status_EQ)) {
			sql += " and tb.status = " + status_EQ;
		}
		if (StringUtils.isNotBlank(companyCode_EQ)) {
			sql += " and t.company_code = '" + companyCode_EQ + "'";
		}
		if (StringUtils.isNotBlank(versionNo_EQ)) {
			sql += " and t.version_no = " + versionNo_EQ;
		}
		
		if (StringUtils.isNotBlank(workingStage_EQ)) {
			sql += " and t.working_stage like '%" + workingStage_EQ + "%'";
		}
		if (StringUtils.isNotBlank(memberCode_EQ)) {
			sql += " and t.member_code like '%" + memberCode_EQ + "%'";
		}
		
		log.debug("exportWeekBonus---sql-------:" + sql);
		String csvFile = "exportWeekBonus.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportWeekBonus---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	/**
	 * memberUpDown.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> memberUpDown(Map<String, Object> searchParams, int currentPage, int pageSize) {
		String sql = this.getMemberUpDownSql(searchParams);
		log.debug("memberUpDown--sql----------:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
		return pager;
	}
	
	/**
	 memberUpDown.jsp “周奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportMemberUpDown(Map<String, Object> searchParams) {
		String sql = this.getMemberUpDownSql(searchParams);
		log.debug("exportMemberUpDown---sql-------:" + sql);
		String csvFile = "exportMemberUpDown.csv";
		String csvFullPath = this.getCsvFullPath(sql, csvFile);
		log.debug("exportMemberUpDown---csvFullPath-------:" + csvFullPath);
		return csvFullPath;
	}
	
	public String getMemberUpDownSql(Map<String, Object> searchParams) {
		String sql = "";
		String sqlA = "select tma.member_no, tma.company_code, tma.name, 							"
				 + " (case                                                                          "
				 + "      when tma.enrollment_grade = 0 then 'Regular Member'                       "
				 + "      when tma.enrollment_grade = 10 then 'Silver'                              "
				 + "      when tma.enrollment_grade = 20 then 'Gold'                                "
				 + "      when tma.enrollment_grade = 30 then 'Platinum'                            "
				 + "      when tma.enrollment_grade = 40 then 'Diamond'                             "
				 + "      else '0'                                                                  "
				 + "  end) as enrollmentgrade,                                                      "
				 + "  tma.telephone, tma.mobile_phone, tma.email, to_char(toa.bonus_date, 'yyyy-mm-dd hh24:mi:ss') as bonus_date from               "
				 + " (                                                                              "
				 + " select level as le, t.id, t.member_no, t.company_code, t.name,                              "
				 + "  t.enrollment_grade, t.telephone, t.mobile_phone,  t.email                     "
				 + " from mm_member t                                                               ";
				 //" start with t.member_no = 'TW03000052' connect by prior t.id = t.placement_id "
		String sqlB = " ) tma                                                                       "
				 + " left join                                                                      "
				 + " (                                                                              "
				 + " select ta.member_id, ta.bonus_date from om_orders ta where ta.order_type = 10  "
				 + " ) toa on tma.id = toa.member_id order by tma.le                                ";
		String sp_network_EQ = searchParams.get("network_EQ") != null ? searchParams.get("network_EQ").toString() : "";
    	String sp_memberNo_EQ = searchParams.get("memberNo_EQ") != null ? searchParams.get("memberNo_EQ").toString() : "";
    	if (StringUtils.isNotBlank(sp_memberNo_EQ)) {
    		if (StringUtils.isNotBlank(sp_network_EQ)) {
    			String networkSql = "";
    			switch(sp_network_EQ) {
    				case "1":
    					networkSql = " start with t.member_no = '" + sp_memberNo_EQ + "' connect by t.id = prior t.placement_id ";
    					break;
    				case "2":
    					networkSql = " start with t.member_no = '" + sp_memberNo_EQ + "' connect by prior t.id = t.placement_id ";
    					break;
    				case "3":
    					networkSql = " start with t.member_no = '" + sp_memberNo_EQ + "' connect by t.id = prior t.sponsor_id ";
    					break;
    				case "4":
    					networkSql = " start with t.member_no = '" + sp_memberNo_EQ + "' connect by prior t.id = t.sponsor_id ";
    					break;
    			}
    			sql = sqlA + networkSql + sqlB;
    		}
    	}
		return sql;
	}
	
	public String getMemberInfoSql(List<SearchBean> searchBeanList) {
		String companyCode = ""; 
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("Country")) {
						companyCode = sb.value.toString();
					}
				}
			}
		}
		Class clazz = ReportDao.class;
		String sqlFile = "memberInfo.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile);
		if (companyCode != null && !companyCode.trim().equals("")) {
			sql += " and tmm.company_code = '" + companyCode + "'";
		}
		log.debug("getMemberInfoSql--sql------:" + sql);
		
		return sql;
	}
	public String getDetailSignalProductSql(List<SearchBean> searchBeanList) {
		String companyCode = ""; 
		String orderDateStart = "";
		String orderDateEnd = ""; 
		String paymentDateStart = ""; 
		String paymentDateEnd = ""; 
		String bonusDateStart = ""; 
		String bonusDateEnd = ""; 
		
		if (searchBeanList != null) {
			for (SearchBean sb : searchBeanList) {
				if (sb != null) {
					if (sb.fieldName.trim().equals("Country")) {
						companyCode = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateStart")) {
						orderDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("orderDateEnd")) {
						orderDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateStart")) {
						paymentDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("paymentDateEnd")) {
						paymentDateEnd = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateStart")) {
						bonusDateStart = sb.value.toString();
					}
					if (sb.fieldName.trim().equals("bonusDateEnd")) {
						bonusDateEnd = sb.value.toString();
					}
				}
			}
		}
		Class clazz = ReportDao.class;
		String sqlFile = "detailSignalProduct.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile);
		String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStart, 
				orderDateEnd, paymentDateStart, paymentDateEnd, bonusDateStart, bonusDateEnd, "");
		log.debug("getDistributorGroupSalesSql--innerQueryCondition------:" + innerQueryCondition);
		String innerQueryConditionTemp = innerQueryCondition.replaceAll("toa", "tod");
		log.debug("getDistributorGroupSalesSql--innerQueryConditionTemp------:" + innerQueryConditionTemp);
		sql = sql.replaceAll("innerQueryCondition", innerQueryCondition);
		sql = sql.replaceAll("innerQueryLastCondition", innerQueryConditionTemp);
		sql += " order by tod.doc_no desc ";
		
		return sql;
	}
	
	public String getSqlFromFile(Class clazz, String fileName) {
		String sql = "";
		String path = "";
		// 获取当前类加载的根目录，如：/C:/Program Files/Apache/Tomcat 6.0/webapps/fee/WEB-INF/classes/  
		try {
			path = clazz.getClassLoader().getResource("").toURI().getPath();
			path = path.replace("classes", "resources");
			log.debug("sqlpath---:" + path);
		} catch (URISyntaxException e1) {
			log.error("getSqlFromFile---:", e1);
		}
		sql = this.getSql(path + fileName);
		log.debug("sqlpathfileName---:" + path + fileName);
		log.debug("getSqlFromFile--sql:" + sql);
		return sql;
	}
	
	/**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
	public String getSql(String filePath) {
		String lineTxt = "";
		String sql = "";
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				
				while ((lineTxt = bufferedReader.readLine()) != null) {
					//System.out.println(lineTxt);
					sql += lineTxt;
				}
				read.close();
			} else {
				log.error("getSql找不到指定的文件:" + filePath);
			}
		} catch (Exception e) {
			log.error("getSql读取文件内容出错:" + filePath, e);
		}
		log.debug("getSql lineTxt-------:" + sql);
		return sql;
	}
	
	/**
	 *	Sample-Product Sales Listing 报表查询条件
	 */
	public Pager<Map<String, Object>> sampleProductSalesListingCSV(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType,
			ListUtil listUtil,String localeCode,LocaleUtil localeUtil, int currentPage, int pageSize) {
		Class clazz = ReportDao.class;
		String sqlFile = "sampleProductSalesListing.sql";
		String sql = this.getSqlFromFile(clazz, sqlFile);
		String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStartStr, 
				orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
		if(!StringUtil.isEmpty(sql)){
			sql = sql.replaceAll("productType1", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "1"));
			sql = sql.replaceAll("productType2", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "2"));
			sql = sql.replaceAll("productType3", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "3"));
			sql = sql.replaceAll("productType4", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "4"));
			sql = sql.replaceAll("productType0", localeUtil.getLocalText(localeCode, "report.productType.singal","singal"));
		}
		sql = sql.replaceAll("innerQueryCondition", innerQueryCondition);
		if (productType != null && !productType.trim().equals("")) {
			sql += " and tpp.product_type = " + productType;
		}
		sql += " group by tso.name,toa.order_type,tpp.product_no,tpcp.type,tpp.product_name,tber.rate ";
				
		log.debug("sampleProductSalesListingCSV-----sql-----:" + sql);
		Pager<Map<String, Object>> pager = this.getJdbcTemplate().queryForPager(sql, currentPage, pageSize);
//		public Pager<Map<String, Object>> queryForPager(String sql, int currentPage, int pageSize,List<Object> paramList)
		return pager;
	}
	
	public String getSampleProductSalesListingFileName(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType,ListUtil listUtil,String localeCode,LocaleUtil localeUtil) {
		DataSource ds = this.getJdbcTemplate().getDataSource();
		Connection oracleConn = null;  
		Statement oracleStmt = null;  
		ResultSet oracleRs = null;
		String sqlFile = "sampleProductSalesListing.sql";
		String csvFile = "sampleProductSalesListing.csv";
		String path = "";
		
		try {
			path = ReportDao.class.getClassLoader().getResource("").toURI().getPath();
			log.debug("getSampleProductSalesListingResultSet-----path----------:" + path);
			
			/*String sql = this.getSqlFromFile(ReportDao.class, sqlFile) 
					+ this.createDetailProductSql(companyCode, orderDateStartStr, orderDateEndStr, paymentDateStartStr, 
					paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);*/
			String sql = this.getSqlFromFile(ReportDao.class, sqlFile);
			String innerQueryCondition = this.getDetailInerQryCon(companyCode, orderDateStartStr, 
					orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
			if(!StringUtil.isEmpty(sql)){
				sql = sql.replaceAll("productType1", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "1"));
				sql = sql.replaceAll("productType2", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "2"));
				sql = sql.replaceAll("productType3", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "3"));
				sql = sql.replaceAll("productType4", listUtil.getListTitle(companyCode, localeCode, "compositeProduct.type", "4"));
				sql = sql.replaceAll("productType0", localeUtil.getLocalText(localeCode, "report.productType.singal","singal"));
			}
			sql = sql.replaceAll("innerQueryCondition", innerQueryCondition);
			if (productType != null && !productType.trim().equals("")) {
				sql += " and tpp.product_type = " + productType;
			}
//			sql += " order by tod.doc_no desc ";
			sql += " group by tso.name,toa.order_type,tpp.product_no,tpcp.type,tpp.product_name,tber.rate ";
			log.debug("getDetailProductResultSet---sql-------:" + sql);
			
			oracleConn = ds.getConnection();
			oracleStmt = oracleConn.createStatement();
			oracleRs = oracleStmt.executeQuery(sql);

			FileWriter fileWriter;
	    	CSVPrinter csvPrinter = null;
	    	try {
	    		fileWriter = new FileWriter(path + csvFile);
	    		
	    		// 新建csv文件
	    		File file = new File(path + csvFile);
	    		FileOutputStream fos = new FileOutputStream(file);
	    		// 写BOM
	    		fos.write(new byte[] { (byte)0xEF, (byte)0xBB, (byte)0xBF });
	    		// 创建字节流输出对象
	    		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
	    		// Apache Commons CSV打印对象
	    		
	    		csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT.withHeader(oracleRs));
	    		csvPrinter.printRecords(oracleRs);
	    		
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	} finally {
	    		try {
	    			if (csvPrinter != null) {
	    				csvPrinter.flush();
	    				csvPrinter.close();
	    			}
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
		} catch (SQLException e) {
			e.printStackTrace();
		}  catch (URISyntaxException e1) {
			e1.printStackTrace();
		}  
		finally {
			try {
				if (oracleRs != null) {
					oracleRs.close();
				}
				if (oracleStmt != null) {
					oracleStmt.close();
				}
				if (oracleConn != null) {
					oracleConn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} 
		
		return path + csvFile;
	}
}
