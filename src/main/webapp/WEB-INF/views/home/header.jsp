<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<body>
	<!-- Header -->
	<header class="header">
		<section class="header-top">
			<div class="container d-flex jc-space-between ai-center">
				<div class="header-left">
					<p>Welcome to Riode store message or remove it!</p>
				</div>

				<div class="header-right">
					<a class="hover-p-color" href=""> <i
						class="fa-solid fa-location-dot"></i>Contact Us
					</a> <a class="hover-p-color" href=""> <i
						class="fa-solid fa-circle-info"></i>Need help
					</a>
				</div>
			</div>
		</section>

		<section class="header-middle">
			<div class="container d-flex jc-space-between ai-center">
				<div class="header-left  d-flex jc-space-between ai-center">
					<div class="logo">
						<a href="home/index.htm"><img
							src="https://d-themes.com/html/riode/images/demos/demo4/logo.png"
							alt=""></a>
					</div>

					<ul class="menu  d-flex jc-space-between ai-center">
						<li><a href="home/index.htm">Home</a></li>
						<li><a href="home/products.htm">Categories</a></li>
						<li><a href="">Sale</a></li>
						<li><a href="">About</a></li>
						<li class="search d-flex ai-center"><input type="text"
							placeholder="Search..."> <a href=""> <i
								class="fa-solid fa-magnifying-glass"></i>
						</a></li>
					</ul>
				</div>

				<ul class="header-right  d-flex jc-space-between ai-center">
		<!-- getSession -->
					<c:set var="salary" scope="session" value="${2000*2}" />
					<c:if test="${salary > 2000}">
						<li><a href="userHome.html " class="user-area"> <span
								class="user-name">Ho Duc Trung lz</span>
								<div class="user-thumbnail">
									<img src="" alt="">
								</div>
						</a></li>
					</c:if>

					<c:if test="${salary <= 2000}">
						<p>
							My salary is:
							<c:out value="${salary}" />
						<p>
						<li><a href="user/login.htm"><i class="fa-solid fa-user"></i></a></li>
					</c:if>
					<li><a href=""><i class="fa-solid fa-bag-shopping"></i></a></li>
				</ul>
			</div>
		</section>

		<div class="header-support"></div>

	</header>
</body>