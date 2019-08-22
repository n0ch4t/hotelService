<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
<title>Document</title>
	<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/style/bootstrap.css">
    <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
   	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.7.0/css/all.css">
   	
  <script>
        $(function(){
            $(".side-btn").click(function(){
                                console.log($(window).width())
                                $(".sidebar").toggleClass("active")
                                $(".content").toggleClass("active")
                                $(".menubtn").toggleClass("active") 
                                })
            
            $(window).resize(function(){
                if($(window).width() > 1224){
                    $(".sidebar").removeClass("active")
                    $(".content").removeClass("active")
                    $(".menubtn").removeClass("active") 
                }
            })
        })
      
    </script>
   <style>
   	@font-face { font-family: 'KOMACON'; src: url('https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts_seven@1.2/KOMACON.woff') format('woff'); font-weight: normal; font-style: normal; }
      *{
      		font-family:KOMACON;
           box-sizing: border-box;
       }
    body{
        margin: 0;
    }
    .menubtn{
    	position:fixed;
    	top : 0px;
		right:0px;
        height: 100px;
        z-index: 999;
        display: none;
        transition: left .3s ease-in;
    }
    .sidebar {
	    position: fixed;
	    width: 250px;
	    height: 100%;
	    min-height:1080px;
/* 	    background-color : #f1e3c4; */
		background-image : url("${pageContext.request.contextPath}/img/ocean2.jpg");
		background-repeat:no-repeat;
		background-size:auto 100%;
	   	box-shadow:2px 2px 5px #f5fff1;
	    z-index: 999;
        transition: margin .3s ease-in;
    }
   .sidebar.active{
       margin-left: -250px;
   }
   .content.active{
        margin-left: 0px;
   }
    .content {
    margin-left: 250px;
    min-height:1080px;
    min-width:360px;
    position: relative;
    background: white;
    overflow: auto;
        z-index: 1;
        transition: margin .3s ease-in;
    }
    .info {
    min-width: 355px;
    min-height:1080px;
    background-color:white;
    position: relative;
    z-index: 2;
    }
    .sidebar-list{
    	margin-top: 60px;
    }
    .sidebar-list{
    	text-align: center;
    }
    .sidebar-list p a{
    	font-size: 25px;
    	color: #81725f;
    }
    @media (max-width:1250px) {
       .sidebar {
         margin-left: -250px;
       }
       .content {
         margin-left: 0px;
       }
       .info{
       }
       .menubtn{
            display: block;
       }
       .menubtn.active{
       		right: 0px; 
       }
       .sidebar.active{
           margin-left: 0px;
       }
       .content.active{
            margin-left: 0px;
       }
     }
</style>
</head>
<body>
<div class="sidebar">
<div>
<div class="sidebar-list">
	<p><a href="${pageContext.request.contextPath}"><img alt="home" src="${pageContext.request.contextPath}/img/homebtn.png" width="100px" height="100px"></a></p>
	<br><br>
	<p><a href="${pageContext.request.contextPath}/notice/list">공지사항</a></p>
	<p>호텔 예약</p>
	<p>호텔 검색</p>
	<p>뭐넣지</p>
	<c:if test="${empty sessionScope.ok}">
	<p><a href="${pageContext.request.contextPath}/join">회원가입</a></p>
	</c:if>
	<c:choose>
		<c:when test="${empty sessionScope.ok}">
			<p><a href="${pageContext.request.contextPath}/member/login">로그인</a></p>
		</c:when>
		<c:otherwise>
			
			<p><a href="${pageContext.request.contextPath}/member/info">내 정보</a></p>
			<p><a href="${pageContext.request.contextPath}/member/logout">로그아웃</a></p>
		</c:otherwise>
	</c:choose>
</div>
</div>
</div>
<div class="menubtn">
<div style="width: 100%; background-color: none;">
	<button class="btn btn-default side-btn"><i style="color: #f1e3c4;" class="fa fa-bars fa-3x"></i></button>
</div>
</div>
<div class="content">
<div class="info">

