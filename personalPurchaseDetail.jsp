<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/views/include/head.jsp"%>
<%@ include file="/views/include/dialog.jsp"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.cj.framework.common.utils.SessionUtil" %>
<%
	
	String safedate = "2015-2-23"; 
	
	Date currentDate = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String strDate = sdf.format(currentDate);
	Date d = sdf.parse(safedate);
	Calendar c = Calendar.getInstance();  
	//c.setTime(d);//指定时间 safedate
	c.setTime(currentDate);//当前时间 currentDate
	
	strDate = sdf.format(c.getTime());//当前设置的时间
	
	c.add(Calendar.DAY_OF_YEAR, 1);//设置时间的后一天
	Date currentDateEnd = c.getTime();
	String strDateEnd = sdf.format(currentDateEnd);
	
	String companyCode = SessionUtil.getCompanyCode(request);
%>
<!DOCTYPE html>
<html>
<head>
<title><jmoa:locale key='report.personalPurchaseDetail' defaultValue="personalPurchaseDetail" /></title>
<script type="text/javascript">
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	window.onload = function() {
		var comCode = "<%=companyCode %>";
		var strDate = "<%=strDate %>";
		$("#companyCode_edit").select2("val", '${Country_EQ}');
	};

	//点击“查询”时，将 form 表单的 action 设为 action="${ctx}/dailySalesCSV"
	function qryProduct() {
		var formObj = document.getElementById("searchForm");
		formObj.action="${ctx}/personalPurchaseDetail";
		//formObj.submit();
	}
	function exportProduct(){
		var formObj = document.getElementById("searchForm");
		formObj.action="${ctx}/personalPurchaseDetailExport";
		formObj.submit();
	}
</script>
</head>
<body>
<div class="pwrap">
		<form:form id="searchForm" modelAttribute="product" action="${ctx}/personalPurchaseDetail" method="post" class="breadcrumb form-search"
			onsubmit="qryProduct()">
			<input id="pageNo" name="currentPage" type="hidden" value="${page.currentPage}" />
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
			<input id="orderBy" name="orderBy" type="hidden" value="${orderBy }" />
			<input id="qryByHandId" name="qryByHand" type="hidden" value="yes" />
			<!-- <label><jmoa:locale key='report.country' defaultValue='Country' />：</label>
			 <select name="sp_Country_EQ" id="companyCode_edit" styleClass="input-small">
				<c:forEach items="${listOffice}" var="office">
					<option value="${office.code }">${office.name }</option>
				</c:forEach>
			</select>-->
			<label><jmoa:locale key='member.memberNo'
					defaultValue="会员编号 " />:</label>
			<input name="sp_memberNo_LIKE" id="memberNo" maxlength="50" class="input-small"
				type="text"  value="${memberNo_LIKE}"  ${sessionScope['jmoa.user'].baseUserType==2?"disabled='disabled'":""} />
			<label><jmoa:locale key='report.createdate' defaultValue='Create Date' />：</label>
			<input name="sp_orderDate_GTE" maxlength="200" id="orderDateStart" class="Wdate input-small" type="text" value="${orderDate_GTE}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,isShowToday:false});"  style="width:120px;" />
			<label>-</label>
			<input name="sp_orderDate_LT" maxlength="200" id="orderDateEnd" class="Wdate input-small" type="text" value="${orderDate_LT}"
				onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true,isShowToday:false});"  style="width:120px;" />
		    &nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<jmoa:locale key='query' defaultValue='Query'/>" />
			&nbsp;
			<input id="btnExport" class="btn btn-primary" type="button" value="<jmoa:locale key='newly.exported' defaultValue='Export' />"
				onclick="exportProduct()" />
		</form:form>

		<tags:message content="${message}" />
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th sort="customerId"><jmoa:locale key='report.customerId' defaultValue='Customer ID' /></th>
					<th sort="distributorName"><jmoa:locale key='report.distributorName' defaultValue='Distributor Name' /></th>
					<th sort="orderID"><jmoa:locale key='report.orderID' defaultValue='Order ID' /></th>
					<th sort="bv"><jmoa:locale key='report.bv' defaultValue='BV' /></th>
					<!--  <th sort="totalAmount"><jmoa:locale key='report.totalAmount' defaultValue='totalAmount' /></th>-->
					<th sort="orderDate"><jmoa:locale key='report.createDate' defaultValue='Create Date' /></th>
					<th sort="sharePoint"><jmoa:locale key='report.sharePoint' defaultValue='Share Point' /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.pageList}" var="order">
					<tr>
						<td>${order.customerId}</td>
						<td>${order.distributorName}</td>
						<td>${order.orderId}</td>
						<td>${order.bv}</td>
						<!-- <td>${order.totalAmount}</td>-->
						<td>${order.orderDate}</td>
						<td>${order.sharedPoint}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<div class="pagination tr">
			<jmoa:page page="${page }" />
		</div>
	</div>		
</body>
</html>