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
<title><jmoa:locale key='report.sponsorExtraDetaail' defaultValue="sponsorExtraDetail" /></title>
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
		formObj.action="${ctx}/sponsorExtraDetail";
		//formObj.submit();
	}
	function exportProduct(){
		var formObj = document.getElementById("searchForm");
		formObj.action="${ctx}/sponsorExtraDetailExport";
		formObj.submit();
	}
</script>
</head>
<body>
<div class="pwrap">
		<form:form id="searchForm" modelAttribute="product" action="${ctx}/sponsorDEtail" method="post" class="breadcrumb form-search"
			onsubmit="qryProduct()">
			<input id="pageNo" name="currentPage" type="hidden" value="${page.currentPage}" />
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}" />
			<input id="orderBy" name="orderBy" type="hidden" value="${orderBy }" />
			<input id="qryByHandId" name="qryByHand" type="hidden" value="yes" />
			<!--  <label><jmoa:locale key='report.country' defaultValue='Country' />：</label>
			<select name="sp_Country_EQ" id="companyCode_edit" styleClass="input-small">
				<c:forEach items="${listOffice}" var="office">
					<option value="${office.code }">${office.name }</option>
				</c:forEach>
			</select>-->
			<label><jmoa:locale key='member.memberNo'
					defaultValue="会员编号 " />:</label>
			<input name="sp_memberNo_LIKE" id="memberNo" maxlength="50" class="input-small"
				type="text"  value="${memberNo_LIKE}"  ${sessionScope['jmoa.user'].baseUserType==2?"disabled='disabled'":""} />
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
					<th sort="customerName"><jmoa:locale key='report.customerName' defaultValue='Customer Name' /></th>
					<th sort="BV"><jmoa:locale key='report.BV' defaultValue='BV' /></th>
					<th sort="cycle1Month"><jmoa:locale key='report.Cycle1Month' defaultValue='Cycle1 Month' /></th>
					<th sort="cycle2Month"><jmoa:locale key='report.Cycle2Month' defaultValue='Cycle2 Month' /></th>
					<th sort="cycle3Month"><jmoa:locale key='report.Cycle3Month' defaultValue='Cycle3 Month' /></th>
					<th sort="cycle1BuyBV"><jmoa:locale key='report.Cycle1BuyBV' defaultValue='Cycle1 Buy BV' /></th>
					<th sort="cycle1BV"><jmoa:locale key='report.Cycle1BV' defaultValue='Cycle1 BV'/></th>
					<th sort="cycle1ExtraBV"><jmoa:locale key='report.Cycle1ExtraBV' defaultValue='Cycle1 Extra BV' /></th>
					<th sort="cycle1ExtraSP"><jmoa:locale key='report.Cycle2ExtraSP' defaultValue='Cycle2 Extra SP' /></th>
					<th sort="cycle2BuyBV"><jmoa:locale key='report.Cycle1BuyBV' defaultValue='Cycle1 Buy BV' /></th>
					<th sort="cycle2BV"><jmoa:locale key='report.Cycle2BV' defaultValue='Cycle2 BV' /></th>
					<th sort="cycle2ExtraBV"><jmoa:locale key='report.Cycle2ExtraBV' defaultValue='Cycle2 Extra BV' /></th>
					<th sort="cycle2ExtraSP"><jmoa:locale key='report.Cycle2ExtraSP' defaultValue='Cycle2 Extra SP' /></th>
					<th sort="cycle3BuyBV"><jmoa:locale key='report.Cycle3BuyBV' defaultValue='Cycle3 Buy BV' /></th>
					<th sort="cycle3BV"><jmoa:locale key='report.Cycle3BV' defaultValue='Cycle3 BV' /></th>
					<th sort="cycle3ExtraBV"><jmoa:locale key='report.Cycle3ExtraBV' defaultValue='Cycle3 Extra BV' /></th>
					<th sort="cycle3ExtraSP"><jmoa:locale key='report.Cycle3ExtraSP' defaultValue='Cycle3 Extra SP' /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.pageList}" var="order">
					<tr>
						<td>${order.member_no}</td>
						<td>${order.member_name}</td>
						<td>${order.BV}</td>
						<td>${order.cycle1}</td>
						<td>${order.cycle2}</td>
						<td>${order.cycle3}</td>
						<td>${order.bv_self_buy1}</td>
						<td>${order.bv_1}</td>
						<td>${order.bv_extra_1}</td>
						<td>${order.sp_extra_1}</td>
						<td>${order.bv_self_buy2}</td>
						<td>${order.bv_2}</td>
						<td>${order.bv_extra_2}</td>
						<td>${order.sp_extra_2}</td>
						<td>${order.bv_self_buy3}</td>
						<td>${order.bv_3}</td>
						<td>${order.bv_extra_3}</td>
						<td>${order.sp_extra_3}</td>
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