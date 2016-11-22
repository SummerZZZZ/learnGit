package com.cj.xso.s.report.provider;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cj.framework.common.dict.ListUtil;
import com.cj.framework.common.lang.LocaleUtil;
import com.cj.framework.common.pagination.Pager;
import com.cj.framework.common.pagination.SearchBean;
import com.cj.framework.service.BaseServiceImpl;
import com.cj.xso.s.order.model.Orders;
import com.cj.xso.s.report.dao.ReportDao;
import com.cj.xso.s.report.service.ReportService;

/**
 * 报表Service
 */
@Component(value="reportService")
@Transactional(readOnly = true)
public class ReportServiceImpl extends BaseServiceImpl<Orders, Long> implements ReportService{

	@Autowired
	private ReportDao reportDao;


	@Autowired(required = true)
	public ReportServiceImpl(@Qualifier(value = "reportDao")ReportDao dao) {
		super(dao);
		this.reportDao = dao;
	}
	/**
	 * awardsBasicDetail.jsp上的查询
	 */
	public Pager<Map<String,Object>> awardsBasicDetail(Map<String,String> paramFromJsp,int currentPage,int pageSize){
		return this.reportDao.awardsBasicDetail(paramFromJsp, currentPage, pageSize);
	}
	/**
	 * awardsbasicDetail.jsp上的导出
	 */
	public String awardsBasicDetailExport(Map<String,String> paramFromJsp){
		return this.reportDao.awardsBasicDetailExport(paramFromJsp);
	}
	/**
	 * sponsorExtraDetail.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> sponsorExtraDetail(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		return this.reportDao.sponsorDetail(paramsFromJsp, currentPage, pageSize);
	}
	
	/**
	 * sponsorExtraDetail.jsp 上的“导出”
	 */
	public String sponsorExtraDetailExport(Map<String, String> paramsFromJsp) {
		return this.reportDao.sponsorExtraDetailExport(paramsFromJsp);
	}
	/**
	 * sponsorDetail.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> sponsorDetail(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		return this.reportDao.sponsorDetail(paramsFromJsp, currentPage, pageSize);
	}
	
	/**
	 * sponsorDetail.jsp 上的“导出”
	 */
	public String sponsorDetailExport(Map<String, String> paramsFromJsp) {
		return this.reportDao.sponsorDetailExport(paramsFromJsp);
	}
	
	/**
	 * sharePointOverview.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> sharePointOverview(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		return this.reportDao.sharePointOverview(paramsFromJsp, currentPage, pageSize);
	}
	
	/**
	 * sharePointOverview.jsp 上的“导出”
	 */
	public String sharePointOverviewExport(Map<String, String> paramsFromJsp) {
		return this.reportDao.sharePointOverviewExport(paramsFromJsp);
	}
	
	/**
	 * personalPurchaseDetail.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> personalPurchaseDetail(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		return this.reportDao.personalPurchaseDetail(paramsFromJsp, currentPage, pageSize);
	}
	
	/**
	 personalPurchaseDetail.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
	 */
	public String personalPurchaseDetailExport(Map<String, String> paramsFromJsp) {
		return this.reportDao.personalPurchaseDetailExport(paramsFromJsp);
	}
	
	/**
	 * dailySalesCSV.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> dailySalesCSV(Map<String, String> paramsFromJsp, int currentPage, int pageSize) {
		return this.reportDao.dailySalesCSV(paramsFromJsp, currentPage, pageSize);
	}
	
	/**
	 dailySalesCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller 调用下载方法在浏览器端弹出下载框
	 */
	public String dailySalesExport(Map<String, String> paramsFromJsp) {
		return this.reportDao.dailySalesExport(paramsFromJsp);
	}
	
	/**
	detailProductSalesCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> detailProductSalesCSV(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, 
			String productType, int currentPage, int pageSize) {
		return this.reportDao.detailProductSalesCSV(companyCode, orderDateStartStr, orderDateEndStr, 
				paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType, currentPage, pageSize);
	}
	
	/**
	 detailProductSalesCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String getDetailProductFileName(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType) {
		return this.reportDao.getDetailProductFileName(companyCode, orderDateStartStr, orderDateEndStr, 
				paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType);
	}
	
	/**
	logisticsReportCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> logisticsReportCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		return this.reportDao.logisticsReportCSV(searchBeanList, currentPage, pageSize); 
	}
	
	/**
	 logisticsReportCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportLogisticsReport(List<SearchBean> searchBeanList) {
		return this.reportDao.exportLogisticsReport(searchBeanList);
	}
	
	/**
	SalesReportAtTheGlaceCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> SalesReportAtTheGlaceCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		return this.reportDao.SalesReportAtTheGlaceCSV(searchBeanList, currentPage, pageSize); 
	}
	
	/**
	 SalesReportAtTheGlaceCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportSalesReportAtTheGlace(List<SearchBean> searchBeanList) {
		return this.reportDao.exportSalesReportAtTheGlace(searchBeanList);
	}
	
	/**
	DistributorGroupAtTheGlaceCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> DistributorGroupAtTheGlaceCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		return this.reportDao.DistributorGroupAtTheGlaceCSV(searchBeanList, currentPage, pageSize); 
	}
	
	/**
	 DistributorGroupAtTheGlaceCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDistributorGroupAtTheGlace(List<SearchBean> searchBeanList) {
		return this.reportDao.exportDistributorGroupAtTheGlace(searchBeanList);
	}
	
	/**
	DistributorGroupTop.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> DistributorGroupTop(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		return this.reportDao.DistributorGroupTop(searchBeanList, currentPage, pageSize); 
	}
	
	/**
	 DistributorGroupTop.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDistributorGroupTop(List<SearchBean> searchBeanList) {
		return this.reportDao.exportDistributorGroupTop(searchBeanList);
	}
	
	/**
	distributorGroupSalesCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> distributorGroupSalesCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		return this.reportDao.distributorGroupSalesCSV(searchBeanList, currentPage, pageSize); 
	}
	
	/**
	 distributorGroupSalesCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDistributorGroupReport(List<SearchBean> searchBeanList) {
		return this.reportDao.exportDistributorGroupReport(searchBeanList);
	}
	/**
	detailSignalProductCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> detailSignalProductCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		return this.reportDao.detailSignalProductCSV(searchBeanList, currentPage, pageSize);  
	}
	
	/**
	 detailSignalProductCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportDetailSignalProduct(List<SearchBean> searchBeanList) {
		return this.reportDao.exportDetailSignalProduct(searchBeanList);
	}
	/**
	memberInfoCSV.jsp 页面上的“查询”
	 */
	public Pager<Map<String, Object>> memberInfoCSV(List<SearchBean> searchBeanList, int currentPage, int pageSize) {
		return this.reportDao.memberInfoCSV(searchBeanList, currentPage, pageSize);  
	}
	
	/**
	 memberInfoCSV.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportMemberInfo(List<SearchBean> searchBeanList) {
		return this.reportDao.exportMemberInfo(searchBeanList);
	}
	
	/**
	 bonusList.jsp 页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportBonusList(List<SearchBean> searchBeanList) {
		return this.reportDao.exportBonusList(searchBeanList);
	}
	
	/**
	 bonusList.jsp 页面上的“季度奖金导出”，返回 文件完整路径名给 controller
	 */
	public String exportQuarterBonusList(List<SearchBean> searchBeanList) {
		return this.reportDao.exportQuarterBonusList(searchBeanList);
	}
	
	/**
	 yearBonusList.jsp “年度奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportYearBonusList(Map<String, Object> searchParams) {
		return this.reportDao.exportYearBonusList(searchParams);
	}
	
	/**
	 quarterBonusList.jsp “季度奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportQuarterBonus(Map<String, Object> searchParams) {
		return this.reportDao.exportQuarterBonus(searchParams);
	}
	
	/**
	 monthBonusList.jsp “月度奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportMonthBonus(Map<String, Object> searchParams) {
		return this.reportDao.exportMonthBonus(searchParams);
	}
	
	/**
	 weekBonusList.jsp “周奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportWeekBonus(Map<String, Object> searchParams) {
		return this.reportDao.exportWeekBonus(searchParams);
	}
	
	/**
	 * memberUpDown.jsp 上的“查询”
	 */
	public Pager<Map<String, Object>> memberUpDown(Map<String, Object> searchParams, int currentPage, int pageSize) {
		return this.reportDao.memberUpDown(searchParams, currentPage, pageSize);
	}
	
	/**
	 memberUpDown.jsp “周奖金待发放”页面上的“导出”，返回 文件完整路径名给 controller
	 */
	public String exportMemberUpDown(Map<String, Object> searchParams) {
		return this.reportDao.exportMemberUpDown(searchParams);
	}
	
	/**
	 * 
	 * Sample-Product Sales Listing 报表查询条件
	 */
	public Pager<Map<String, Object>> sampleProductSalesListingCSV(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, 
			String productType,ListUtil listUtil,String localeCode,LocaleUtil localeUtil, int currentPage, int pageSize) {
		return this.reportDao.sampleProductSalesListingCSV(companyCode, orderDateStartStr, orderDateEndStr, 
				paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType, listUtil,localeCode,localeUtil,currentPage, pageSize);
	}
	
	/**
	 * Sample-Product Sales Listing导出
	 */
	public String getSampleProductSalesListingFileName(String companyCode, String orderDateStartStr, String orderDateEndStr, 
			String paymentDateStartStr, String paymentDateEndStr, String bonusDateStartStr, String bonusDateEndStr, String productType,ListUtil listUtil,String localeCode,LocaleUtil localeUtil) {
		return this.reportDao.getSampleProductSalesListingFileName(companyCode, orderDateStartStr, orderDateEndStr, 
				paymentDateStartStr, paymentDateEndStr, bonusDateStartStr, bonusDateEndStr, productType,listUtil,localeCode,localeUtil);
	}
}
