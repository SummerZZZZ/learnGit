<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/views/include/head.jsp"%>
<%@ include file="/views/include/dialog.jsp"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.cj.framework.common.utils.SessionUtil" %>

<!DOCTYPE html>
<html>
<head>
<title><jmoa:locale key='report.awardsBasicDetail' defaultValue="awardsBasicDetail" /></title>
<script type="text/javascript">
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
	//点击“查询”时，将 form 表单的 action 设为 action="${ctx}/dailySalesCSV"
	function qryProduct() {
		var formObj = document.getElementById("searchForm");
		formObj.action="${ctx}/awardsBasicDetail";
		//formObj.submit();
	}
	function exportProduct(){
		var formObj = document.getElementById("searchForm");
		formObj.action="${ctx}/awardsBasicDetailExport";
		formObj.submit();
	}
</script>
</head>
<body>
<div class="pwrap">
		<form:form id="searchForm" modelAttribute="product" action="${ctx}/awardsBasicDetail" method="post" class="breadcrumb form-search"
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
					<th sort="month"><jmoa:locale key='report.month' defaultValue='Month' /></th>
					<th sort="awards"><jmoa:locale key='report.awards' defaultValue='Awards' /></th>
					<th sort="sharePoints"><jmoa:locale key='report.sharePoints' defaultValue='SharePoints' /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${page.pageList}" var="order">
					<tr>
						<td>${order.customerId}</td>
						<td>${order.customerName}</td>
						<td>${order.Month}</td>
						<td>${order.awards}</td>
						<td>${order.sp}</td>
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