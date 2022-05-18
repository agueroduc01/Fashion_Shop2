<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Riode - Register</title>
<base href="${pageContext.servletContext.contextPath}/">

<link href="<c:url value='/resources/home/dist/css/reset.css' />"
	rel="stylesheet">
<link href="<c:url value='/resources/home/dist/css/login.css' />"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css"
	integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />

<link href="<c:url value='/resources/home/dist/css/owl.carousel.css' />"
	rel="stylesheet">
<link
	href="<c:url value='/resources/home/dist/css/owl.theme.default.css' />"
	rel="stylesheet">
</head>
<body>

	<!-- HEADER -->
	<%@include file="/WEB-INF/views/header.jsp"%>
	
	
	
	<!-- MAIN -->
	<main class="main login-container">
		<div class="container">

			<form class="register-form" action="">
				<h2>Register</h2>

				<div class="row">
					<div class="col-4 ">
						<input type="text" placeholder="Full name"> <input
							type="text" placeholder="Gmail"> <input type="number"
							placeholder="Phone number">

						<div class="input-group">
							<label for="">Gender</label> <select name="gender" id="gender">
								<option value="male">Male</option>
								<option value="female">Female</option>
								<option value="other">Other</option>
							</select>
						</div>
					</div>

					<div class="col-4 ">
						<input type="text" placeholder="Username"> <input
							type="password" placeholder="Password"> <input
							type="password" placeholder="Password again">

						<p>
							<a href="">You already have an account?</a>
						</p>
					</div>

					<div class="col-4 ">
						<div class="input-group">
							<label for="">Image file upload</label> <input type="file">

						</div>

						<div class="input-group">
							<label for="">Date of birth </label> <input type="date">

						</div>

						<input type="text" placeholder="Address">

						<button>REGISTER</button>
					</div>
				</div>


			</form>
		</div>
	</main>
</body>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js" referrerpolicy="no-referrer"></script>
<script src="<c:url value='/resources/home/dist/js/owl.carousel.js' />"></script>
<script src="<c:url value='/resources/home/dist/js/home.js' />"></script>
</html>