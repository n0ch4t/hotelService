<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<style>
	.goodbye-wrap{
		padding: 50px;
	}

</style>



<div align="center" class="goodbye-wrap">
	<h2>그동안 이용해 주셔서 감사합니다</h2>
	<h4><font color="blue"></font></h4>
	<h3><a class="btn btn-danger" href="<%=request.getContextPath()%>">메인으로</a></h3>
</div>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>