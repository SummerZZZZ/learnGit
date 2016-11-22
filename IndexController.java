package com.cj.xso.w.report;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.druid.sql.PagerUtils;
import com.cj.framework.common.exception.AppException;
import com.cj.framework.common.pagination.Pager;
import com.cj.framework.common.pagination.SearchBean;
import com.cj.framework.common.utils.Constants;
import com.cj.framework.common.utils.RequestUtils;
import com.cj.framework.common.utils.SessionUtil;
import com.cj.framework.mvc.web.BaseController;
import com.cj.xso.s.ca.model.Office;
import com.cj.xso.s.ca.provider.OfficeServiceImpl;
import com.cj.xso.s.ca.service.OfficeService;
import com.cj.xso.s.report.provider.ReportInfoServiceImpl;
import com.cj.xso.s.report.provider.ReportServiceImpl;
import com.cj.xso.s.report.service.ReportInfoService;
import com.cj.xso.s.report.service.ReportService;

@Controller
public class IndexController extends BaseController{
	private Log log = LogFactory.getLog(IndexController.class);
	@Autowired
    private OfficeService officeService;
	@Autowired
	private ReportServiceImpl reportService;
	@Autowired
	private ReportInfoService reportInfoService;
	
	/*
	@Autowired
	private YbonusDetailService ybonusDetailService;
	
	@Autowired
	private QbonusDetailService qbonusDetailService;
	
	@Autowired
	private BonusIssueService bonusIssueService;
	
	@Autowired
	private WbonusDetailService wbonusDetailService;
	
	@Autowired
	private MbonusDetailService mbonusDetailService;*/
	
    public void setOfficeService(OfficeService officeService) {
        this.officeService = officeService;
    }
	/***
	 * 进入首页
	 * @param request
	 * @return
	 */
    @RequestMapping(value = {"","/index"})
    public String index(HttpServletRequest request,Model model) {
        return "modules/report/index";
    }
    
    private void setCompanyName(HttpServletRequest request, Model model) {
    	String officeCode = SessionUtil.getCompanyCode(request);
    	String companyName = "";
    	if (officeCode != null && !officeCode.trim().equals("AA")) {
    		Office office = officeService.findOfficeByCode(officeCode);
    		companyName = office.getName();
    	}
    	model.addAttribute("companyName", companyName);
    }
    
    /***
     * 进入printOrder.jsp
     * @param request
     * @return
     */
    @RequestMapping(value = {"","/printOrder"})
    public String printOrder(HttpServletRequest request, Model model) {
    	this.setCompanyName(request, model);
    	return "modules/report/printOrder";
    }
    
    /***
     * 进入printAutoOrder.jsp
     * @param request
     * @return
     */
    @RequestMapping(value = {"","/printAutoOrder"})
    public String printAutoOrder(HttpServletRequest request, Model model) {
    	this.setCompanyName(request, model);
    	return "modules/report/printAutoOrder";
    }
    
    
    /***
     * 进入dailySalesByCountry.jsp
     * @param request
     * @return
     */
    @RequestMapping(value = {"","/dailySalesByCountry"})
    public String dailySalesByCountry(HttpServletRequest request, Model model) {
    	this.setCompanyName(request, model);
    	return "modules/report/dailySalesByCountry";
    }
    
    /***
     * 进入distributorGroupSalesReport.jsp
     * @param request
     * @return
     */
    @RequestMapping(value = {"","/distributorGroupSalesReport"})
    public String distributorGroupSalesReport(HttpServletRequest request, Model model) {
    	this.setCompanyName(request, model);
    	return "modules/report/distributorGroupSalesReport";
    }
    
    /***
     * 进入productSalesListing.jsp
     * @param request
     * @return
     */
    @RequestMapping(value = {"","/productSalesListing"})
    public String productSalesListing(HttpServletRequest request, Model model) {
    	this.setCompanyName(request, model);
    	return "modules/report/productSalesListing";
    }
    
    /***
     * 进入detaileProductSalesListing.jsp
     * @param request
     * @return
     */
    @RequestMapping(value = {"","/detaileProductSalesListing"})
    public String detaileProductSalesListing(HttpServletRequest request, Model model) {
    	this.setCompanyName(request, model);
    	return "modules/report/detaileProductSalesListing";
    }
    
    /***
     * 进入weeklyFullBonusSummary.jsp
     * @param request
     * @return
     */
    @RequestMapping(value = {"","/weeklyFullBonusSummary"})
    public String weeklyFullBonusSummary(HttpServletRequest request, Model model) {
    	this.setCompanyName(request, model);
    	return "modules/report/weeklyFullBonusSummary";
    }
    
    /**
     * awardsBasicDetail.jsp上的查询
     */  
    @RequestMapping(value ={"","/awardsBasicDetail"})
    public String awardsBasicDetail(HttpServletRequest request,HttpServletResponse response,Model model){
    	Pager<Map<String,Object>> pager = RequestUtils.getPager(request);
    	String memberNoStr = request.getParameter("sp_MemberNo_LIKE");
    	String userLoginName = SessionUtil.getLoginName(request);
    	boolean isNormal = SessionUtil.isNormal(userLoginName);
    	if(isNormal){
    		model.addAttribute("MemberNo_Like", memberNoStr);
    	}
    	
    	Map<String,String> paramsFromJsp = new HashMap<String,String>();
    	paramsFromJsp.put("memberNoStr", memberNoStr);
    	pager = this.reportService.awardsBasicDetail(paramsFromJsp, pager.getCurrentPage(), pager.getPageSize());
    	Map<String, Object> searchParams = new HashMap<String, Object>();
		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	model.addAttribute("page", pager);
    	model.addAllAttributes(searchParams);
    	return "modules/report/awardsBasicDetail";
    }
    /**
     * awardsBasicDetail.jsp上的导出
     */
    @RequestMapping(value = {"","/awardsBasicDetailExport"})
    public String awardsBasicDetailExport(HttpServletRequest request,HttpServletResponse response,Model model){
    	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
    	
		//将前台jsp传过来的查询条件封装到 map 中，传给 service 层
		Map<String, String> paramsFromJsp = new HashMap<String, String>();
		paramsFromJsp.put("memberNo", memberNoStr);
		
    	String fullFileName = this.reportService.awardsBasicDetailExport(paramsFromJsp);
    	log.debug("awardsBasicDetail--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    /**
     * sponsorExtraDetail.jsp上的查询
     */
    @RequestMapping(value = {"","/sponsorExtraDetail"})
    public String sponsorExtraDetail(HttpServletRequest request,HttpServletResponse response,Model model){
    	Pager<Map<String,Object>> pager = RequestUtils.getPager(request);	
    	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
    	
    	String userLoginName = SessionUtil.getLoginName(request);
    	boolean isNormal = SessionUtil.isNormal(request);

    	if(isNormal){
    		model.addAttribute("memberNo_LIKE",userLoginName);
    	}

    	Map<String, String> paramsFromJsp = new HashMap<String, String>();
		paramsFromJsp.put("memberNoStr", memberNoStr);
    	pager = this.reportService.sponsorDetail(paramsFromJsp, pager.getCurrentPage(), pager.getPageSize());
    	Map<String, Object> searchParams = new HashMap<String, Object>();
		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	model.addAttribute("page", pager);
    	model.addAllAttributes(searchParams);
    	return "modules/report/sponsorExtraDetail";
    }
    @RequestMapping(value = {"","/sponsorExtraDetailExport"})
    public String sponsorExtraDetailExport(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
       	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
    	
		//将前台jsp传过来的查询条件封装到 map 中，传给 service 层
		Map<String, String> paramsFromJsp = new HashMap<String, String>();
		paramsFromJsp.put("memberNo", memberNoStr);
		
    	String fullFileName = this.reportService.sponsorExtraDetailExport(paramsFromJsp);
    	log.debug("sharePointOverviewExport--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
		
    }
    /**
     * sponsorDetail.jsp上的查询
     */
    @RequestMapping(value = {"","/sponsorDetail"})
    public String sponsorDetail(HttpServletRequest request,HttpServletResponse response,Model model){
    		Pager<Map<String,Object>> pager = RequestUtils.getPager(request);
    		List<Office> listOffice = officeService.getDeptList();
    		listOffice.add(0,null);
    		String officeCode = SessionUtil.getCompanyCode(request);
    		Office office = officeService.findOfficeByCode(officeCode);
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			listOffice = new ArrayList<Office>();
    			listOffice.add(office);
    		}
    		Date currentDate = new Date();
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	String strDate = sdf.format(currentDate);
        	String qryByHand = request.getParameter("qryByHand"); 
//        	String companyCode = request.getParameter("sp_Country_EQ"); 
//        	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
//        	String orderDateEndStr = request.getParameter("sp_orderDate_LT");
        	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
        	
        	String userLoginName = SessionUtil.getLoginName(request);
        	boolean isNormal = SessionUtil.isNormal(request);
        	if (qryByHand == null) {
//        		orderDateStartStr = strDate;
//        		orderDateEndStr = strDate;
//        		model.addAttribute("orderDate_GTE", strDate);
//        		model.addAttribute("orderDate_LT", strDate);
//        		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
//        			companyCode = officeCode;
//        		}
        		if(isNormal){
        			model.addAttribute("memberNo_LIKE",userLoginName);
        		}
        	}
        	Map<String, String> paramsFromJsp = new HashMap<String, String>();
    		//paramsFromJsp.put("companyCode", companyCode);
//    		paramsFromJsp.put("orderDateStartStr", orderDateStartStr);
//    		paramsFromJsp.put("orderDateEndStr", orderDateEndStr);
    		paramsFromJsp.put("memberNoStr", memberNoStr);
    		this.setCompanyName(request, model);
    		model.addAttribute("listOffice", listOffice);
        	pager = this.reportService.sponsorDetail(paramsFromJsp, pager.getCurrentPage(), pager.getPageSize());
        	System.out.println("D");
        	Map<String, Object> searchParams = new HashMap<String, Object>();
    		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
        	model.addAttribute("page", pager);
        	model.addAllAttributes(searchParams);
    		return "modules/report/sponsorDetail";
    	
    }
    
    @RequestMapping(value = {"","/sponsorDetailExport"})
    public String sponsorDetailExport(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
    	
		//将前台jsp传过来的查询条件封装到 map 中，传给 service 层
		Map<String, String> paramsFromJsp = new HashMap<String, String>();
		paramsFromJsp.put("memberNo", memberNoStr);
		
    	String fullFileName = this.reportService.sponsorDetailExport(paramsFromJsp);
    	log.debug("sharePointOverviewExport--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
		
    }
    
    /***
     * sharePointOverview.jsp上的查询
     */
    @RequestMapping(value = {"","/sharePointOverview"})
    public String sharePointOverview(HttpServletRequest request,HttpServletResponse response,Model model){
    		Pager<Map<String,Object>> pager = RequestUtils.getPager(request);
    		List<Office> listOffice = officeService.getDeptList();
    		listOffice.add(0,null);
    		String officeCode = SessionUtil.getCompanyCode(request);
    		Office office = officeService.findOfficeByCode(officeCode);
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			listOffice = new ArrayList<Office>();
    			listOffice.add(office);
    		}
//    		Date currentDate = new Date();
//        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        	String strDate = sdf.format(currentDate);
        	String qryByHand = request.getParameter("qryByHand"); 
//        	//String companyCode = request.getParameter("sp_Country_EQ"); 
//        	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
//        	String orderDateEndStr = request.getParameter("sp_orderDate_LT");
        	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
        	
        	String userLoginName = SessionUtil.getLoginName(request);
        	boolean isNormal = SessionUtil.isNormal(request);
        	if (qryByHand == null) {
//        		orderDateStartStr = strDate;
//        		orderDateEndStr = strDate;
//        		model.addAttribute("orderDate_GTE", strDate);
//        		model.addAttribute("orderDate_LT", strDate);
//        		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
//        			companyCode = officeCode;
//        		}
        		if(isNormal){
        			model.addAttribute("memberNo_LIKE",userLoginName);
        		}
        	}
        	Map<String, String> paramsFromJsp = new HashMap<String, String>();
    		//paramsFromJsp.put("companyCode", companyCode);
//    		paramsFromJsp.put("orderDateStartStr", orderDateStartStr);
//    		paramsFromJsp.put("orderDateEndStr", orderDateEndStr);
    		paramsFromJsp.put("memberNoStr", memberNoStr);
    		this.setCompanyName(request, model);
    		model.addAttribute("listOffice", listOffice);
        	pager = this.reportService.sharePointOverview(paramsFromJsp, pager.getCurrentPage(), pager.getPageSize());
        	System.out.println("D");
        	Map<String, Object> searchParams = new HashMap<String, Object>();
    		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
        	model.addAttribute("page", pager);
        	model.addAllAttributes(searchParams);
    		return "modules/report/sharePointOverview";
    	
    }
    /***
     * sharePointOverview.jsp 上的“导出”
     */
    @RequestMapping(value = {"","/sharePointOverviewExport"})
    public String sharePointOverviewExport(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
    	
		//将前台jsp传过来的查询条件封装到 map 中，传给 service 层
		Map<String, String> paramsFromJsp = new HashMap<String, String>();
		paramsFromJsp.put("memberNo", memberNoStr);
		
    	String fullFileName = this.reportService.sharePointOverviewExport(paramsFromJsp);
    	log.debug("sharePointOverviewExport--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
		
    }
    /***
     * personalPurchaseDetail.jsp上的查询
     */
    @RequestMapping(value = {"","/personalPurchaseDetail"})
    public String personalPurchaseDetail(HttpServletRequest request, HttpServletResponse response, Model model){
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	List<Office> listOffice = officeService.getDeptList();
    	listOffice.add(0, null);
		String officeCode = SessionUtil.getCompanyCode(request);
		Office office = officeService.findOfficeByCode(officeCode);
		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
			listOffice = new ArrayList<Office>();
			listOffice.add(office);
		}
		Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	String qryByHand = request.getParameter("qryByHand"); 
    	//String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT");
    	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
    	
    	String userLoginName = SessionUtil.getLoginName(request);
    	boolean isNormal = SessionUtil.isNormal(request);
    	if (qryByHand == null) {
    		orderDateStartStr = strDate;
    		orderDateEndStr = strDate;
    		model.addAttribute("orderDate_GTE", strDate);
    		model.addAttribute("orderDate_LT", strDate);
//    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
//    			companyCode = officeCode;
//    		}
    		if(isNormal){
    			model.addAttribute("memberNo_LIKE",userLoginName);
    		}
    	}
    	Map<String, String> paramsFromJsp = new HashMap<String, String>();
		//paramsFromJsp.put("companyCode", companyCode);
		paramsFromJsp.put("orderDateStartStr", orderDateStartStr);
		paramsFromJsp.put("orderDateEndStr", orderDateEndStr);
		paramsFromJsp.put("memberNoStr", memberNoStr);
		this.setCompanyName(request, model);
		model.addAttribute("listOffice", listOffice);
    	pager = this.reportService.personalPurchaseDetail(paramsFromJsp, pager.getCurrentPage(), pager.getPageSize());
    	Map<String, Object> searchParams = new HashMap<String, Object>();
		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	model.addAttribute("page", pager);
    	model.addAllAttributes(searchParams);
    	return "modules/report/personalPurchaseDetail";  	
    }
    
    /***
     * personalPurchaseDetail.jsp 上的“导出”
     */
    @RequestMapping(value = {"","/personalPurchaseDetailExport"})
    public String personalPurchaseDetailExport(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	//String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT"); 
    	String memberNoStr = request.getParameter("sp_memberNo_LIKE");
    	
		//将前台jsp传过来的查询条件封装到 map 中，传给 service 层
		Map<String, String> paramsFromJsp = new HashMap<String, String>();
		//paramsFromJsp.put("companyCode", companyCode);
		paramsFromJsp.put("orderDateStartStr", orderDateStartStr);
		paramsFromJsp.put("orderDateEndStr", orderDateEndStr);
		paramsFromJsp.put("memberNo", memberNoStr);
		
    	String fullFileName = this.reportService.personalPurchaseDetailExport(paramsFromJsp);
    	log.debug("personalPurchaseDetailExport--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
		
    }
    
    /***
     * dailySalesCSV.jsp 上的“查询”
     */
    @RequestMapping(value = {"","/dailySalesCSV"})
    public String dailySalesCSV(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	List<Office> listOffice = officeService.getDeptList();
		listOffice.add(0, null);
		String officeCode = SessionUtil.getCompanyCode(request);
		Office office = officeService.findOfficeByCode(officeCode);
		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
			listOffice = new ArrayList<Office>();
			listOffice.add(office);
		}
		
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	String qryByHand = request.getParameter("qryByHand"); 
    	String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	if (qryByHand == null) {
    		orderDateStartStr = strDate;
    		model.addAttribute("orderDate_GTE", strDate);
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			companyCode = officeCode;
    		}
    	}
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT"); 
    	if (qryByHand == null) {
    		orderDateEndStr = strDate;
    		model.addAttribute("orderDate_LT", strDate);
    	}
		String paymentDateStartStr = request.getParameter("sp_paymentDate_GTE"); 
		String paymentDateEndStr = request.getParameter("sp_paymentDate_LT"); 
		String bonusDateStartStr = request.getParameter("sp_bonusDate_GTE"); 
		String bonusDateEndStr = request.getParameter("sp_bonusDate_LT"); 
		String payStatusStr = request.getParameter("sp_payStatus_EQ"); 
		String payDateStartStr = request.getParameter("sp_payDate_GTE"); 
		String payDateEndStr = request.getParameter("sp_payDate_LTE"); 
		
		//将前台jsp传过来的查询条件封装到 map 中，传给 service 层
		Map<String, String> paramsFromJsp = new HashMap<String, String>();
		paramsFromJsp.put("companyCode", companyCode);
		paramsFromJsp.put("orderDateStartStr", orderDateStartStr);
		paramsFromJsp.put("orderDateEndStr", orderDateEndStr);
		paramsFromJsp.put("paymentDateStartStr", paymentDateStartStr);
		paramsFromJsp.put("paymentDateEndStr", paymentDateEndStr);
		paramsFromJsp.put("bonusDateStartStr", bonusDateStartStr);
		paramsFromJsp.put("bonusDateEndStr", bonusDateEndStr);
		paramsFromJsp.put("payStatusStr", payStatusStr);
		paramsFromJsp.put("payDateStartStr", payDateStartStr);
		paramsFromJsp.put("payDateEndStr", payDateEndStr);
		
    	this.setCompanyName(request, model);
    	
		model.addAttribute("listOffice", listOffice);
		
    	pager = this.reportService.dailySalesCSV(paramsFromJsp, pager.getCurrentPage(), pager.getPageSize());
    	
		Map<String, Object> searchParams = new HashMap<String, Object>();
		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	model.addAttribute("page", pager);
    	model.addAllAttributes(searchParams);
		
    	return "modules/report/dailySalesCSV";
    	
    }
    
    
    /***
     * dailySalesCSV.jsp 上的“导出”
     */
    @RequestMapping(value = {"","/dailySalesExport"})
    public String dailySalesExport(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT"); 
		String paymentDateStartStr = request.getParameter("sp_paymentDate_GTE"); 
		String paymentDateEndStr = request.getParameter("sp_paymentDate_LT"); 
		String bonusDateStartStr = request.getParameter("sp_bonusDate_GTE"); 
		String bonusDateEndStr = request.getParameter("sp_bonusDate_LT"); 
		String payStatusStr = request.getParameter("sp_payStatus_EQ"); 
		String payDateStartStr = request.getParameter("sp_payDate_GTE"); 
		String payDateEndStr = request.getParameter("sp_payDate_LTE"); 
		
		//将前台jsp传过来的查询条件封装到 map 中，传给 service 层
		Map<String, String> paramsFromJsp = new HashMap<String, String>();
		paramsFromJsp.put("companyCode", companyCode);
		paramsFromJsp.put("orderDateStartStr", orderDateStartStr);
		paramsFromJsp.put("orderDateEndStr", orderDateEndStr);
		paramsFromJsp.put("paymentDateStartStr", paymentDateStartStr);
		paramsFromJsp.put("paymentDateEndStr", paymentDateEndStr);
		paramsFromJsp.put("bonusDateStartStr", bonusDateStartStr);
		paramsFromJsp.put("bonusDateEndStr", bonusDateEndStr);
		paramsFromJsp.put("payStatusStr", payStatusStr);
		paramsFromJsp.put("payDateStartStr", payDateStartStr);
		paramsFromJsp.put("payDateEndStr", payDateEndStr);
		
    	String fullFileName = this.reportService.dailySalesExport(paramsFromJsp);
    	log.debug("dailySalesExport--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
		
    }
    
    /***
     * detailProductSalesCSV.jsp 页面上的“查询”
     */
    @RequestMapping(value = {"","/detailProductSalesCSV"})
    public String detailProductSalesCSV(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	List<Office> listOffice = officeService.getDeptList();
		listOffice.add(0, null);
		String officeCode = SessionUtil.getCompanyCode(request);
		Office office = officeService.findOfficeByCode(officeCode);
		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
			listOffice = new ArrayList<Office>();
			listOffice.add(office);
		}
		
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	if (qryByHand == null) {
    		orderDateStartStr = strDate;
    		model.addAttribute("orderDate_GTE", strDate);
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			companyCode = officeCode;
    		}
    	}
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT"); 
    	if (qryByHand == null) {
    		orderDateEndStr = strDate;
    		model.addAttribute("orderDate_LT", strDate);
    	}
		String paymentDateStartStr = request.getParameter("sp_paymentDate_GTE"); 
		String paymentDateEndStr = request.getParameter("sp_paymentDate_LT"); 
		String bonusDateStartStr = request.getParameter("sp_bonusDate_GTE"); 
		String bonusDateEndStr = request.getParameter("sp_bonusDate_LT"); 
		String productType = request.getParameter("sp_ProductType_EQ");
    	this.setCompanyName(request, model);
		
		model.addAttribute("listOffice", listOffice);
    			 
    	pager = this.reportService.detailProductSalesCSV(companyCode, orderDateStartStr, 
    			orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType, 
    			pager.getCurrentPage(), pager.getPageSize());
    	
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/detailProductSalesCSV";
    }
    
    
    
    /***
     * detailProductSalesCSV.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/getDetailProductFileName"})
    public String getDetailProductFileName(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT"); 
		String paymentDateStartStr = request.getParameter("sp_paymentDate_GTE"); 
		String paymentDateEndStr = request.getParameter("sp_paymentDate_LT"); 
		String bonusDateStartStr = request.getParameter("sp_bonusDate_GTE"); 
		String bonusDateEndStr = request.getParameter("sp_bonusDate_LT"); 
		String productType = request.getParameter("sp_ProductType_EQ");
		
    	String fullFileName = this.reportService.getDetailProductFileName(companyCode, orderDateStartStr,
    			orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
    	log.debug("getDetailProductFileName--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /***
     * logisticsReportCSV.jsp 页面上的“查询”
     */
    @RequestMapping(value = {"","/logisticsReportCSV"})
    public String logisticsReportCSV(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	List<Office> listOffice = officeService.getDeptList();
    	listOffice.add(0, null);
    	String officeCode = SessionUtil.getCompanyCode(request);
    	Office office = officeService.findOfficeByCode(officeCode);
    	if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    		listOffice = new ArrayList<Office>();
    		listOffice.add(office);
    	}
    	
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	if (qryByHand == null) {
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			SearchBean sbCountry = new SearchBean("Country", officeCode, SearchBean.Operator.EQ);
    			searchBeanList.add(sbCountry);
    		}
    		SearchBean sbOrderDateStart = new SearchBean("orderDateStart", strDate, SearchBean.Operator.GTE);
    		searchBeanList.add(sbOrderDateStart);
    		SearchBean sbOrderDateEnd = new SearchBean("orderDateEnd", strDate, SearchBean.Operator.LT);
    		searchBeanList.add(sbOrderDateEnd);
    		
    		model.addAttribute("orderDateStart_GTE", strDate);
    		model.addAttribute("orderDateEnd_LT", strDate);
    	}
    	this.setCompanyName(request, model);
    	
    	model.addAttribute("listOffice", listOffice);
    	
    	pager = this.reportService.logisticsReportCSV(searchBeanList, pager.getCurrentPage(), pager.getPageSize());
    	
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/logisticsReportCSV";
    }
    
    /***
     * logisticsReportCSV.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/exportLogisticsReport"})
    public String exportLogisticsReport(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportLogisticsReport(searchBeanList);
    	log.debug("exportLogisticsReport--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /***
     * SalesReportAtTheGlace.jsp 页面上的“查询”
     */
    @RequestMapping(value = {"","/SalesReportAtTheGlaceCSV"})
    public String SalesReportAtTheGlace(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	List<Office> listOffice = officeService.getDeptList();
    	listOffice.add(0, null);
    	String officeCode = SessionUtil.getCompanyCode(request);
    	Office office = officeService.findOfficeByCode(officeCode);
    	if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    		listOffice = new ArrayList<Office>();
    		listOffice.add(office);
    	}
    	
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	if (qryByHand == null) {
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			SearchBean sbCountry = new SearchBean("Country", officeCode, SearchBean.Operator.EQ);
    			searchBeanList.add(sbCountry);
    		}
    		SearchBean sbOrderDateStart = new SearchBean("orderDateStart", strDate, SearchBean.Operator.GTE);
    		searchBeanList.add(sbOrderDateStart);
    		SearchBean sbOrderDateEnd = new SearchBean("orderDateEnd", strDate, SearchBean.Operator.LT);
    		searchBeanList.add(sbOrderDateEnd);
    		
    		model.addAttribute("orderDateStart_GTE", strDate);
    		model.addAttribute("orderDateEnd_LT", strDate);
    	}
    	this.setCompanyName(request, model);
    	
    	model.addAttribute("listOffice", listOffice);
    	
    	pager = this.reportService.SalesReportAtTheGlaceCSV(searchBeanList, pager.getCurrentPage(), pager.getPageSize());
    	
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/SalesReportAtTheGlace";
    }
    
    /***
     * SalesReportAtTheGlace.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/exportSalesReportAtTheGlace"})
    public String exportSalesReportAtTheGlace(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportSalesReportAtTheGlace(searchBeanList);
    	log.debug("exportSalesReportAtTheGlace--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /***
     * DistributorGroupAtTheGlace.jsp 页面上的“查询”
     */
    @RequestMapping(value = {"","/DistributorGroupAtTheGlaceCSV"})
    public String DistributorGroupAtTheGlace(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	List<Office> listOffice = officeService.getDeptList();
    	listOffice.add(0, null);
    	String officeCode = SessionUtil.getCompanyCode(request);
    	Office office = officeService.findOfficeByCode(officeCode);
    	if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    		listOffice = new ArrayList<Office>();
    		listOffice.add(office);
    	}
    	
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	if (qryByHand == null) {
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			SearchBean sbCountry = new SearchBean("Country", officeCode, SearchBean.Operator.EQ);
    			searchBeanList.add(sbCountry);
    		}
    		SearchBean sbOrderDateStart = new SearchBean("orderDateStart", strDate, SearchBean.Operator.GTE);
    		searchBeanList.add(sbOrderDateStart);
    		SearchBean sbOrderDateEnd = new SearchBean("orderDateEnd", strDate, SearchBean.Operator.LT);
    		searchBeanList.add(sbOrderDateEnd);
    		
    		model.addAttribute("orderDateStart_GTE", strDate);
    		model.addAttribute("orderDateEnd_LT", strDate);
    	}
    	this.setCompanyName(request, model);
    	
    	model.addAttribute("listOffice", listOffice);
    	
    	pager = this.reportService.DistributorGroupAtTheGlaceCSV(searchBeanList, pager.getCurrentPage(), pager.getPageSize());
    	
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/DistributorGroupAtTheGlace";
    }
    
    /***
     * DistributorGroupAtTheGlace.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/exportDistributorGroupAtTheGlace"})
    public String exportDistributorGroupAtTheGlace(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportDistributorGroupAtTheGlace(searchBeanList);
    	log.debug("exportDistributorGroupAtTheGlace--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /***
     * distributorGroupSalesCSV.jsp 页面上的“查询”
     */
    @RequestMapping(value = {"","/distributorGroupSalesCSV"})
    public String distributorGroupSalesCSV(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	List<Office> listOffice = officeService.getDeptList();
    	listOffice.add(0, null);
    	String officeCode = SessionUtil.getCompanyCode(request);
    	Office office = officeService.findOfficeByCode(officeCode);
    	if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    		listOffice = new ArrayList<Office>();
    		listOffice.add(office);
    	}
    	
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	if (qryByHand == null) {
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			SearchBean sbCountry = new SearchBean("Country", officeCode, SearchBean.Operator.EQ);
    			searchBeanList.add(sbCountry);
    		}
    		SearchBean sbOrderDateStart = new SearchBean("orderDateStart", strDate, SearchBean.Operator.GTE);
    		searchBeanList.add(sbOrderDateStart);
    		SearchBean sbOrderDateEnd = new SearchBean("orderDateEnd", strDate, SearchBean.Operator.LT);
    		searchBeanList.add(sbOrderDateEnd);
    		
    		model.addAttribute("orderDateStart_GTE", strDate);
    		model.addAttribute("orderDateEnd_LT", strDate);
    	}
    	this.setCompanyName(request, model);
    	
    	model.addAttribute("listOffice", listOffice);
    	
    	pager = this.reportService.distributorGroupSalesCSV(searchBeanList, pager.getCurrentPage(), pager.getPageSize());
    	
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/distributorGroupSalesCSV";
    }
    
    /***
     * distributorGroupSalesCSV.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/exportDistributorGroupReport"})
    public String exportDistributorGroupReport(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportDistributorGroupReport(searchBeanList);
    	log.debug("exportDistributorGroupReport--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    /***
     * detailSignalProductCSV.jsp 页面上的“查询”
     */
    @RequestMapping(value = {"","/detailSignalProductCSV"})
    public String detailSignalProductCSV(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	List<Office> listOffice = officeService.getDeptList();
    	listOffice.add(0, null);
    	String officeCode = SessionUtil.getCompanyCode(request);
    	Office office = officeService.findOfficeByCode(officeCode);
    	if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    		listOffice = new ArrayList<Office>();
    		listOffice.add(office);
    	}
    	
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	if (qryByHand == null) {
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			SearchBean sbCountry = new SearchBean("Country", officeCode, SearchBean.Operator.EQ);
    			searchBeanList.add(sbCountry);
    		}
    		SearchBean sbOrderDateStart = new SearchBean("orderDateStart", strDate, SearchBean.Operator.GTE);
    		searchBeanList.add(sbOrderDateStart);
    		SearchBean sbOrderDateEnd = new SearchBean("orderDateEnd", strDate, SearchBean.Operator.LT);
    		searchBeanList.add(sbOrderDateEnd);
    		
    		model.addAttribute("orderDateStart_GTE", strDate);
    		model.addAttribute("orderDateEnd_LT", strDate);
    	}
    	this.setCompanyName(request, model);
    	
    	model.addAttribute("listOffice", listOffice);
    	
    	pager = this.reportService.detailSignalProductCSV(searchBeanList, pager.getCurrentPage(), pager.getPageSize()); 
    	
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/detailSignalProductCSV";
    }
    
    /***
     * detailSignalProductCSV.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/exportDetailSignalProduct"})
    public String exportDetailSignalProduct(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportDetailSignalProduct(searchBeanList);
    	log.debug("exportDetailSignalProduct--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    /***
     * memberInfoCSV.jsp 页面上的“查询”
     */
    @RequestMapping(value = {"","/memberInfoCSV"})
    public String memberInfoCSV(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	List<Office> listOffice = officeService.getDeptList();
    	listOffice.add(0, null);
    	String officeCode = SessionUtil.getCompanyCode(request);
    	Office office = officeService.findOfficeByCode(officeCode);
    	if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    		listOffice = new ArrayList<Office>();
    		listOffice.add(office);
    	}
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	if (qryByHand == null) {
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			SearchBean sbCountry = new SearchBean("Country", officeCode, SearchBean.Operator.EQ);
    			searchBeanList.add(sbCountry);
    		}
    	}
    	this.setCompanyName(request, model);
    	
    	model.addAttribute("listOffice", listOffice);
    	
    	pager = this.reportService.memberInfoCSV(searchBeanList, pager.getCurrentPage(), pager.getPageSize()); 
    	
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/memberInfoCSV";
    }
    
    /***
     * memberInfoCSV.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/exportMemberInfo"})
    public String exportMemberInfo(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportMemberInfo(searchBeanList);
    	log.debug("exportMemberInfo--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /**
     * bonusList.jsp 页面上的“查询”
     
    @RequestMapping(value = {"","/bonusList"})
    public String bonusList(HttpServletRequest request, HttpServletResponse response, Model model) {
    	String wworkingStage = request.getParameter("wworkingStage");
    	String mworkingStage = request.getParameter("mworkingStage");
    	String quarter = request.getParameter("quarter");
    	if (wworkingStage != null && !wworkingStage.trim().equals("0")) {
    		WbonusDetail wbonusDetail = new WbonusDetail();
    		model.addAttribute("wworkingStage", wworkingStage);
    		return this.weekBonusList(wbonusDetail, request, response, model);
    	} else {
    		if (mworkingStage != null && !mworkingStage.trim().equals("0")) {
    			MbonusDetail mbonusDetail = new MbonusDetail();
    			model.addAttribute("mworkingStage", mworkingStage);
    			return this.monthBonusList(mbonusDetail, request, response, model);
    		} else {
    			if (quarter != null && !quarter.trim().equals("0")) {
    				model.addAttribute("quarter", quarter);
    	    		return this.quarterBonusList(request, response, model);
    	    	} else {
    	    		return this.yearBonusList(request, response, model);
    	    	}
    		}
    	}
    }*/
    
    /**
     * yearBonusList.jsp “年度奖金待发放”的查询
     * @param request
     * @param response
     * @param model
     * @return
     
    @RequestMapping(value = {"","/yearBonusList"})
    public String yearBonusList(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> searchParams = new HashMap<String, Object>();
//		searchParams.put("status_EQ", 0);
		getCompanyFilterSearchParams(searchParams, "companyCode_EQ", request);
		String year = request.getParameter("year");
		if (year == null || year.trim().length() == 0) {
			year = DateUtils.getYear();
		}
		String startYear = year + "01";
		String endYear = year + "12";
		searchParams.put("monthlyWorking_GTE", startYear);
		searchParams.put("monthlyWorking_LTE", endYear);
		
		
		Pager<YbonusDetail> pager = RequestUtils.getPager(request);
		// 获取排序数据
		Map<String, String> orderParams = new HashMap<String, String>();
		List<OrderBy> orderByList = RequestUtils.getOrderBy(orderParams, request);
		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, YbonusDetail.class);
		
		String exportYearBonus = request.getParameter("exportYearBonus");
		if (StringUtils.isNotBlank(exportYearBonus)) {
			if (exportYearBonus.equals("exportYearBonusList")) {
				return this.exportYearBonusList(request, response, searchParams);
			}
		}
		
		pager = this.ybonusDetailService.getPager(searchBeanList, orderByList, pager.getCurrentPage(), pager.getPageSize());
		
		model.addAttribute("page", pager);
		model.addAllAttributes(searchParams);
		model.addAllAttributes(orderParams);
		model.addAttribute("year", year);
		model.addAttribute("pagersize", pager.getPageList().size());
		
		boolean flag = false;
		if (pager.getPageList().size() > 0
				&& pager.getPageList().get(0).getTotalBonus().compareTo(BigDecimal.ZERO) > 0) {
			flag = true;
		}
		model.addAttribute("showSureBtn", flag);
		
		return "modules/report/yearBonusList";
    }*/
    
    /**
     * yearBonusList.jsp “年度奖金待发放”页面上的“导出”
     */
    public String exportYearBonusList(HttpServletRequest request, HttpServletResponse response, Map<String, Object> searchParams) {
    	String fullFileName = this.reportService.exportYearBonusList(searchParams);
    	log.debug("exportYearBonusList--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /**
     * quarterBonusList.jsp “季度奖金待发放”页面上的“查询”
     
    @RequestMapping(value = {"","/quarterBonusList"})
    public String quarterBonusList(HttpServletRequest request, HttpServletResponse response, Model model) {
	    Map<String, Object> searchParams = new HashMap<String, Object>();
//		searchParams.put("status_EQ", 0);
		getCompanyFilterSearchParams(searchParams, "companyCode_EQ", request);
		String quarter = request.getParameter("quarter");
		String year = request.getParameter("year");
		if (year == null || year.trim().length() == 0) {
			year = DateUtils.getYear();
		}
		if (quarter == null || quarter.trim().length() == 0) {
			String m = DateUtils.getMonth();
			if (Integer.parseInt(m) >= 10) {
				quarter = "4";
			} else if (Integer.parseInt(m) >= 7) {
				quarter = "3";
			} else if (Integer.parseInt(m) >= 4) {
				quarter = "2";
			} else {
				quarter = "1";
			}
		}
		String inparam = "";
		if ("1".equals(quarter)) {
			inparam = String.format("%s01,%s02,%s03", year, year, year);
		} else if ("2".equals(quarter)) {
			inparam = String.format("%s04,%s05,%s06", year, year, year);
		} else if ("3".equals(quarter)) {
			inparam = String.format("%s07,%s08,%s09", year, year, year);
		} else if ("4".equals(quarter)) {
			inparam = String.format("%s10,%s11,%s12", year, year, year);
		}
		searchParams.put("monthlyWorking_IN", inparam);
		Pager<QbonusDetail> pager = RequestUtils.getPager(request);
		// 获取排序数据
		Map<String, String> orderParams = new HashMap<String, String>();
		List<OrderBy> orderByList = RequestUtils.getOrderBy(orderParams, request);
		List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, QbonusDetail.class);
		
		String exportQuarterBonus = request.getParameter("exportQuarterBonus");
		if (StringUtils.isNotBlank(exportQuarterBonus)) {
			if (exportQuarterBonus.equals("exportQuarterBonus")) {
				return this.exportQuarterBonus(request, response, searchParams);
			}
		}
		
		pager = this.qbonusDetailService.getPager(searchBeanList, orderByList, pager.getCurrentPage(), pager.getPageSize());
	
		model.addAttribute("page", pager);
		model.addAllAttributes(searchParams);
		model.addAllAttributes(orderParams);
		model.addAttribute("quarter", quarter);
		model.addAttribute("year", year);
		model.addAttribute("pagersize", pager.getPageList().size());
		
		boolean flag = false;
		if (pager.getPageList().size() > 0
				&& pager.getPageList().get(0).getTotalBonus().compareTo(BigDecimal.ZERO) > 0) {
			flag = true;
		}
		model.addAttribute("showSureBtn", flag);
		return "modules/report/quarterBonusList";
    }*/
    
    /**
     * quarterBonusList.jsp “季度奖金待发放”页面上的“导出”
     */
    public String exportQuarterBonus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> searchParams) {
    	String fullFileName = this.reportService.exportQuarterBonus(searchParams);
    	log.debug("exportQuarterBonus--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /**
     * monthBonusList.jsp “月度奖金待发放”页面上的“查询”
    
    @RequestMapping(value = {"","/monthBonusList"})
    public String monthBonusList(MbonusDetail mbonusDetail, HttpServletRequest request,
			HttpServletResponse response, Model model) {
    	String company = currentCompanyCode(request) == null ? "AA" : currentCompanyCode(request);
//		boolean isNotHQ = isNotHQ(request); 
		boolean isNotHQ = !"AA".equals(company) ? true : false; 
		
		//sp_status_EQ
		String bonusStatusTemp = request.getParameter("sp_status_EQ");
		String bonusStatus = "";
		if (StringUtils.isBlank(bonusStatusTemp)) {
			bonusStatusTemp = "0, 1";
			bonusStatus = "";
		} else {
			bonusStatus = bonusStatusTemp;
		}
		model.addAttribute("bonusStatus", bonusStatus);
				
		List<Map<String,Object>> bonusIssueList = bonusIssueService.getAllowBonusIssue(company, "2", bonusStatus,isNotHQ);
//		List<Map<String,Object>> bonusIssueList = bonusIssueService.getAllowBonusIssue(company, "2", "0",isNotHQ);
		String workingStage = "";
		String versionNo = "";
		String memberCode = null;
		
		Pager<MbonusDetail> pager = RequestUtils.getPager(request);
		Map<String, Object> searchParams = new HashMap<String, Object>();
		Map<String, String> orderParams = new HashMap<String, String>();
		List<OrderBy> orderByList = RequestUtils.getOrderBy(orderParams, request);
		
		
		
		if (null != bonusIssueList && bonusIssueList.size() > 0) {
			
			//按 WORKINGSTAGE desc 排序
			Collections.sort(bonusIssueList, new Comparator<Map<String, Object>>() {
				public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
					return arg1.get("WORKINGSTAGE").toString().compareTo(arg0.get("WORKINGSTAGE").toString());
				}
			});
			model.addAttribute("allowBonus", bonusIssueList);
			
			if (null != mbonusDetail && null != mbonusDetail.getWorkingStage()) {
				memberCode = mbonusDetail.getMemberCode();
				for(Map<String,Object> bi : bonusIssueList){
					if(bi.get("workingStage").equals(mbonusDetail.getWorkingStage())){
						workingStage = bi.get("workingStage")+"";
						versionNo = bi.get("versionNo")+""; 
					}
				}
				searchParams.put("memberCode_EQ", memberCode);
			} else {
				Map<String,Object> bi = bonusIssueList.get(0);
				workingStage = bi.get("workingStage")+"";
				versionNo = bi.get("versionNo")+""; 
			}
			if( StringUtils.isNotBlank( workingStage )){
				searchParams.put("workingStage_EQ", workingStage);
			}
			if( StringUtils.isNotBlank( versionNo )){
				searchParams.put("versionNo_EQ", versionNo);
			}
			if(isNotHQ){
				searchParams.put("companyCode_EQ", company);
			}
			searchParams.put("bonusIssue.status_IN", bonusStatusTemp);
			// 获取排序数据
			List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, MbonusDetail.class);
			
			String exportMonthBonus = request.getParameter("exportMonthBonus");
			if (StringUtils.isNotBlank(exportMonthBonus)) {
				if (exportMonthBonus.equals("exportMonthBonus")) {
					searchParams.put("bonusIssue.status_EQ", bonusStatus);
					return this.exportMonthBonus(request, response, searchParams);
				}
			}
			
			if(!"".equals(workingStage)){
				pager = this.mbonusDetailService.getPager(searchBeanList, orderByList, pager.getCurrentPage(), pager.getPageSize());
				model.addAttribute("workingStageFlag", workingStage);
				model.addAttribute("page", pager);
				model.addAllAttributes(searchParams);
				model.addAllAttributes(orderParams);
				model.addAttribute("pagersize", pager.getPageList().size());
			}
		}
		
		model.addAttribute("page", pager);
		model.addAllAttributes(searchParams);
		model.addAllAttributes(orderParams);
		model.addAttribute("workingStageFlag", workingStage);
		
		return "modules/report/monthBonusList";
	} */
    
    /**
     * monthBonusList.jsp “月度奖金待发放”页面上的“导出”
     */
    public String exportMonthBonus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> searchParams) {
    	String fullFileName = this.reportService.exportMonthBonus(searchParams);
    	log.debug("exportMonthBonus--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /*@ResponseBody
	@RequestMapping(value = "getWorkingStageByStatus")
	public String getWorkingStageByStatus(HttpServletRequest request) {
    	String company = currentCompanyCode(request) == null ? "AA" : currentCompanyCode(request);
//		boolean isNotHQ = isNotHQ(request); 
		boolean isNotHQ = !"AA".equals(company) ? true : false; 
		
		//sp_status_EQ
		String bonusStatus = request.getParameter("sp_status_EQ");
		//bonus_type, varchar2(1), optional, 奖金类型1 周奖金 2 月奖金
		String bonusType = request.getParameter("sp_bonusType_EQ");
				
		List<Map<String, Object>> bonusIssueList = bonusIssueService.getAllowBonusIssue(company, bonusType, bonusStatus,isNotHQ);
		if (bonusIssueList != null && bonusIssueList.size() > 0) {
			//按 WORKINGSTAGE desc 排序
			Collections.sort(bonusIssueList, new Comparator<Map<String, Object>>() {
				public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
					return arg1.get("WORKINGSTAGE").toString().compareTo(arg0.get("WORKINGSTAGE").toString());
				}
			});
		}
		
		// 组装JSON数据
		String prefix = "{\"success\":true,\"isException\":false,\"exception\":false,\"successesultValue\":null,\"workingStages\":";
		String jsonText = JSON.toJSONString(bonusIssueList, true);
		String suffix = "}";
//		log.error("getWorkingStageByStatus------:" + prefix + jsonText + suffix);
		return prefix + jsonText + suffix;
	}*/
    
    /**
     * weekBonusList.jsp “周奖金待发放”页面上的“查询”
     
    @RequestMapping(value = {"","/weekBonusList"})
    public String weekBonusList(WbonusDetail wbonusDetail, HttpServletRequest request,
			HttpServletResponse response, Model model) {
    	String company = currentCompanyCode(request) == null ? "AA" : currentCompanyCode(request);
//		boolean isNotHQ = isNotHQ(request); 
		boolean isNotHQ = !"AA".equals(company) ? true : false; 
		
		//sp_status_EQ
		String bonusStatusTemp = request.getParameter("sp_status_EQ");
		String bonusStatus = "";
		if (StringUtils.isBlank(bonusStatusTemp)) {
			bonusStatusTemp = "0, 1";
			bonusStatus = "";
		} else {
			bonusStatus = bonusStatusTemp;
		}
		model.addAttribute("bonusStatus", bonusStatus);
				
//		List<Map<String,Object>> bonusIssueList = bonusIssueService.getAllowBonusIssue(company, "1", "0",isNotHQ);
		List<Map<String,Object>> bonusIssueList = bonusIssueService.getAllowBonusIssue(company, "1", bonusStatus,isNotHQ);
		String workingStage = "";
		String versionNo = "";
		String memberCode = null;
		
		Pager<WbonusDetail> pager = RequestUtils.getPager(request);
		Map<String, String> orderParams = new HashMap<String, String>();
		List<OrderBy> orderByList = RequestUtils.getOrderBy(orderParams, request);
		Map<String, Object> searchParams = new HashMap<String, Object>();
		
		
		if (null != bonusIssueList && bonusIssueList.size() > 0) {
			
			//按 WORKINGSTAGE desc 排序
			Collections.sort(bonusIssueList, new Comparator<Map<String, Object>>() {
				public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
					return arg1.get("WORKINGSTAGE").toString().compareTo(arg0.get("WORKINGSTAGE").toString());
				}
			});
			model.addAttribute("allowBonus", bonusIssueList);
			
			if (null != wbonusDetail && null != wbonusDetail.getWorkingStage()) {
				memberCode = wbonusDetail.getMemberCode();
				for(Map<String,Object> bi : bonusIssueList){
					if(bi.get("workingStage").equals(wbonusDetail.getWorkingStage())){
						workingStage = bi.get("workingStage")+"";
						versionNo = bi.get("versionNo")+""; 
					}
				}
				searchParams.put("memberCode_EQ", memberCode);
			} else {
				Map<String,Object> bi = bonusIssueList.get(0);
				workingStage = bi.get("workingStage")+"";
				versionNo = bi.get("versionNo")+""; 
			}
			
			searchParams.put("workingStage_EQ", workingStage);
			searchParams.put("versionNoe_EQ", versionNo);
			if(isNotHQ){
				searchParams.put("companyCode_EQ", company);
			}
			searchParams.put("bonusIssue.status_IN", bonusStatusTemp);
			
			List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, WbonusDetail.class);
			
			String exportWeekBonus = request.getParameter("exportWeekBonus");
			if (StringUtils.isNotBlank(exportWeekBonus)) {
				if (exportWeekBonus.equals("exportWeekBonus")) {
					searchParams.put("bonusIssue.status_EQ", bonusStatus);
					return this.exportWeekBonus(request, response, searchParams);
				}
			}
			
			if(!"".equals(workingStage)){
				pager = this.wbonusDetailService.getPager(searchBeanList, orderByList, pager.getCurrentPage(), pager.getPageSize());
				model.addAttribute("workingStageFlag", workingStage);
				model.addAttribute("page", pager);
				model.addAllAttributes(searchParams);
				model.addAllAttributes(orderParams);
				model.addAttribute("pagersize", pager.getPageList().size());
			}
		}
		
		model.addAttribute("workingStageFlag", workingStage);
		model.addAttribute("page", pager);
		model.addAllAttributes(searchParams);
		model.addAllAttributes(orderParams);
		
		return "modules/report/weekBonusList";
	}
    */
    
    /**
     * weekBonusList.jsp “周奖金待发放”页面上的“导出”
     */
    public String exportWeekBonus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> searchParams) {
    	String fullFileName = this.reportService.exportWeekBonus(searchParams);
    	log.debug("exportWeekBonus--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /**
     * DistributorGroupTop.jsp 页面上的“查询”
     
    @RequestMapping(value = {"","/DistributorGroupTop"})
    public String DistributorGroupTop(WbonusDetail wbonusDetail, HttpServletRequest request,
    		HttpServletResponse response, Model model) {
    	String company = currentCompanyCode(request) == null ? "AA" : currentCompanyCode(request);
//		boolean isNotHQ = isNotHQ(request); 
    	boolean isNotHQ = !"AA".equals(company) ? true : false; 
    	
    	
    	
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String, String> orderParams = new HashMap<String, String>();
    	Map<String, Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	List<Map<String,Object>> bonusIssueList = new ArrayList<Map<String,Object>>();
    	String qryByHand = request.getParameter("qryByHand"); 
		if (qryByHand == null) {
			//默认查询周对应的奖金期别
	    	 bonusIssueList = bonusIssueService.getAllowBonusIssue(company, "1", "1", isNotHQ);
		} else {
			//bonus_type, varchar2(1), optional, 奖金类型1 周奖金 2 月奖金
			String bonusType = request.getParameter("sp_bonusType_EQ");
			//不是默认的情况下，根据bonusType来查询对应的周，月奖金期别
			bonusIssueList = bonusIssueService.getAllowBonusIssue(company, bonusType, "1", isNotHQ);
		}
	    	if (null != bonusIssueList && bonusIssueList.size() > 0) {
	    		//按 WORKINGSTAGE desc 排序
	    		Collections.sort(bonusIssueList, new Comparator<Map<String, Object>>() {
	    			public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
	    				return arg1.get("WORKINGSTAGE").toString().compareTo(arg0.get("WORKINGSTAGE").toString());
	    			}
	    		});
	    		model.addAttribute("allowBonus", bonusIssueList);
    		}
    	
    	
    	String exportDistributorGroupTop = request.getParameter("exportDistributorGroupTop");
    	if (StringUtils.isNotBlank(exportDistributorGroupTop)) {
    		if (exportDistributorGroupTop.equals("exportDistributorGroupTop")) {
    			//sp_bonusType_EQ
//    				searchParams.put("bonusIssue.status_EQ", bonusStatus);
//    				searchParams.put("bonusIssue.bonusType_EQ", bonusStatus);
    			return this.exportDistributorGroupTop(request, response, model);
    		}
    	}
    	
    	pager = this.reportService.DistributorGroupTop(searchBeanList, pager.getCurrentPage(), pager.getPageSize()); 
		model.addAttribute("page", pager);
		model.addAllAttributes(searchParams);
		model.addAllAttributes(orderParams);
		model.addAttribute("pagersize", pager.getPageList().size());
    	
    	return "modules/report/DistributorGroupTop";
    }*/
    
    /**
     * DistributorGroupTop.jsp 页面上的“导出”
     */
    public String exportDistributorGroupTop(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	String fullFileName = this.reportService.exportDistributorGroupTop(searchBeanList);
    	log.debug("exportDistributorGroupTop--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /**
     * memberUpDown.jsp “会员安置推荐网络上下线”页面上的“查询”
     
    @RequestMapping(value = {"","/memberUpDown"})
    public String memberUpDown(WbonusDetail wbonusDetail, HttpServletRequest request,
    		HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	Map<String, Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	String memberUpDownName = request.getParameter("memberUpDownName");
    	String sp_network_EQ = request.getParameter("sp_network_EQ");
    	String sp_memberNo_EQ = request.getParameter("sp_memberNo_EQ");
    	//searchParams.put("sp_network_EQ", sp_network_EQ);
    	//searchParams.put("sp_memberNo_EQ", sp_memberNo_EQ);
    	
    	if (StringUtils.isNotBlank(memberUpDownName) && memberUpDownName.equals("exportMemberUpDown")) {
			if (StringUtils.isNotBlank(sp_memberNo_EQ)) {
				return this.exportMemberUpDown(request, response, searchParams);
    		}
    	}
    	
    	if (StringUtils.isNotBlank(sp_memberNo_EQ)) {
    		pager = this.reportService.memberUpDown(searchParams, pager.getCurrentPage(), pager.getPageSize());
		}
    	
    	model.addAttribute("page", pager);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/memberUpDown";
    }*/
    
    /**
     * memberUpDown.jsp “会员安置推荐网络上下线”页面上的“导出”
     */
    public String exportMemberUpDown(HttpServletRequest request, HttpServletResponse response, Map<String, Object> searchParams) {
    	String fullFileName = this.reportService.exportMemberUpDown(searchParams);
    	log.debug("exportMemberUpDown--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /**
     * bonusList.jsp 页面上的“导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    @RequestMapping(value = {"","/exportBonusList"})
    public String exportBonusList(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	String quarter = request.getParameter("quarter");
    	if (quarter != null && quarter.trim().length() != 0) {
    		this.exportQuarterBonusList(request, response, model);
    	}
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportBonusList(searchBeanList);
    	log.debug("exportBonusList--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    /**
     * bonusList.jsp 页面上的“季度奖金导出”
     * @param request
     * @return
     * @throws SQLException 
     */
    public String exportQuarterBonusList(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	
    	String fullFileName = this.reportService.exportQuarterBonusList(searchBeanList);
    	log.debug("exportBonusList--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
    
    private void download(String path, HttpServletResponse response) {  
        try {  
            // path是指欲下载的文件的路径。  
            File file = new File(path);  
            // 取得文件名。  
            String filename = file.getName();  
            // 以流的形式下载文件。  
            InputStream fis = new BufferedInputStream(new FileInputStream(path));  
            byte[] buffer = new byte[fis.available()];  
            fis.read(buffer);  
            fis.close();  
            // 清空response  
            response.reset();  
            // 设置response的Header  
            response.addHeader("Content-Disposition", "attachment;filename="  
                    + new String(filename.getBytes()));  
            response.addHeader("Content-Length", "" + file.length());  
            OutputStream toClient = new BufferedOutputStream(  
                    response.getOutputStream());  
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");  
            toClient.write(buffer);  
            toClient.flush();  
            toClient.close();  
        } catch (IOException ex) {  
            ex.printStackTrace();  
        }  
    }  
    /**
     * 
     * 导出excel的方法，页面调用
     * 只需要写sql语句即可，sql语句的格式例子如下：
     * SELECT T.MEMBER_NO, T.NAME, (SELECT T3.CHARACTER_VALUE FROM SYS_CHARACTER_VALUE T3 LEFT JOIN SYS_CHARACTER_KEY T4 ON T3.KEY_ID = T4.ID WHERE T4.CHARACTER_KEY = 'member.enrollmentGrade.option' || T.ENROLLMENT_GRADE AND T3.CHARACTER_CODE = :CHARACTER_CODE) ENROLLMENT_GRADE, T.CREATE_BY, T.CREATE_DATE, SPONSORMEMBER.MEMBER_NO, PLACEMENTMEMBER.MEMBER_NO, (SELECT T3.CHARACTER_VALUE FROM SYS_CHARACTER_VALUE T3 LEFT JOIN SYS_CHARACTER_KEY T4 ON T3.KEY_ID = T4.ID WHERE T4.CHARACTER_KEY = 'member.subtype.option' || T.SUBTYPE AND T3.CHARACTER_CODE = :CHARACTER_CODE) SUBTYPE FROM MM_MEMBER T LEFT JOIN MM_MEMBER SPONSORMEMBER ON T.SPONSOR_ID = SPONSORMEMBER.ID LEFT JOIN MM_MEMBER PLACEMENTMEMBER ON T.PLACEMENT_ID = PLACEMENTMEMBER.ID
     * 
     * @param code:对应rp_report_info表里的code
     * @param request
     * @param response
     * @param model
     * @throws Exception
     */
    @RequestMapping(value = "excelRecordRS/{code}")
    public void exportRS(@PathVariable("code")String code,HttpServletRequest request, HttpServletResponse response,
            Model model) throws Exception {
        String fileNameCode="export.excel."+code;
        String dfFileName = "report"+DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        String fileName = getLocalText(fileNameCode, dfFileName);
        if(!StringUtils.endsWith(fileName, ".xlsx")){
            fileName+=".xlsx";
        }
        // 获取查询过滤参数
        String characterCoding =SessionUtil.getLocaleString();
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try { 
            boolean ret = reportInfoService.excelExport(request, code, characterCoding, os);
            if(!ret){
                throw new AppException("Sorry,create excel error!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AppException("Sorry,create excel error!",e);
        }
        byte[] content = os.toByteArray();
        InputStream is = new ByteArrayInputStream(content);
        // 设置response参数，可以打开下载页面
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename="
                + new String((fileName).getBytes(), "iso-8859-1"));
        ServletOutputStream out = response.getOutputStream();
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            // Simple read/write loop.
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (final IOException e) {
            throw e;
        } finally {
            if (bis != null){
                bis.close();
            }
            if (bos != null){
                bos.close();
            }
            if(out!=null){
              out.close();
            }
            if(is!=null){
              is.close();
            }
            if(os!=null){
              os.close();
            }
        }
    
    }
   
    /**
     * Sample-Product Sales Listing 报表
     * @param request
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = {"","/sampleProductSalesListingCSV"})
    public String sampleProductSalesListingCSV(HttpServletRequest request, HttpServletResponse response, Model model) {
    	Pager<Map<String, Object>> pager = RequestUtils.getPager(request);
    	List<Office> listOffice = officeService.getDeptList();
		listOffice.add(0, null);
		String officeCode = SessionUtil.getCompanyCode(request);
		Office office = officeService.findOfficeByCode(officeCode);
		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
			listOffice = new ArrayList<Office>();
			listOffice.add(office);
		}
		
    	Date currentDate = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String strDate = sdf.format(currentDate);
    	
    	String qryByHand = request.getParameter("qryByHand"); 
    	String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	if (qryByHand == null) {
    		orderDateStartStr = strDate;
    		model.addAttribute("orderDate_GTE", strDate);
    		if (officeCode != null && !officeCode.trim().equals("") && !officeCode.trim().equals("AA")) {
    			companyCode = officeCode;
    		}
    	}
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT"); 
    	if (qryByHand == null) {
    		orderDateEndStr = strDate;
    		model.addAttribute("orderDate_LT", strDate);
    	}
		String paymentDateStartStr = request.getParameter("sp_paymentDate_GTE"); 
		String paymentDateEndStr = request.getParameter("sp_paymentDate_LT"); 
		String bonusDateStartStr = request.getParameter("sp_bonusDate_GTE"); 
		String bonusDateEndStr = request.getParameter("sp_bonusDate_LT"); 
		String productType = request.getParameter("sp_ProductType_EQ");
    	this.setCompanyName(request, model);
		
		model.addAttribute("listOffice", listOffice);
		//ListUtil listUtil = (ListUtil) ContextUtil.getSpringBeanByName(request.getServletContext(), "listUtil");
		String localeCode = StringUtils.defaultIfBlank(SessionUtil.getLocaleString(request), Constants.DEFAULT_LOCALE);
		pager = this.reportService.sampleProductSalesListingCSV(companyCode, orderDateStartStr, 
    			orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType, listUtil,localeCode,
    			localeUtil,pager.getCurrentPage(), pager.getPageSize());
    	
    	Map<String,Object> searchParams = new HashMap<String, Object>();
    	List<SearchBean> searchBeanList = RequestUtils.populateSBs(searchParams, request, "", "");
    	model.addAttribute("page", pager);
    	model.addAttribute("searchBeanList", searchBeanList);
    	model.addAllAttributes(searchParams);
    	
    	return "modules/report/sampleProductSalesListingCSV";
    }
    
    
    /**
     * Sample-Product Sales Listing 报表导出
     */
    @RequestMapping(value = {"","/getSampleProductSalesListingFileName"})
    public String getSampleProductSalesListingFileName(HttpServletRequest request, HttpServletResponse response, Model model) throws SQLException {
    	String companyCode = request.getParameter("sp_Country_EQ"); 
    	String orderDateStartStr = request.getParameter("sp_orderDate_GTE");
    	String orderDateEndStr = request.getParameter("sp_orderDate_LT"); 
		String paymentDateStartStr = request.getParameter("sp_paymentDate_GTE"); 
		String paymentDateEndStr = request.getParameter("sp_paymentDate_LT"); 
		String bonusDateStartStr = request.getParameter("sp_bonusDate_GTE"); 
		String bonusDateEndStr = request.getParameter("sp_bonusDate_LT"); 
		String productType = request.getParameter("sp_ProductType_EQ");
		String localeCode = StringUtils.defaultIfBlank(SessionUtil.getLocaleString(request), Constants.DEFAULT_LOCALE);
    	String fullFileName = this.reportService.getSampleProductSalesListingFileName(companyCode, orderDateStartStr,
    			orderDateEndStr, paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType,listUtil,localeCode,localeUtil);
    	log.debug("getSampleProductSalesListingFileName--fullFileName------:" + fullFileName);
    	this.download(fullFileName, response);
    	return null;
    }
}
