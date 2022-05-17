<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Riode -  Login</title>

    <link rel="stylesheet" href="./dist/css/reset.css">
    <link rel="stylesheet" href="./dist/css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css" integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g==" crossorigin="anonymous" referrerpolicy="no-referrer" />

    <link rel="stylesheet" href="./dist/css/owl.carousel.css">
    <link rel="stylesheet" href="./dist/css/owl.theme.default.css">
</head>
<body>
     
    <!-- Header -->
    <header class="header">
        <section class="header-top">
            <div class="container d-flex jc-space-between ai-center">
                <div class="header-left">
                    <p>Welcome to Riode store message or remove it!</p>
                </div>
    
                <div class="header-right">
                    <a class="hover-p-color" href="">
                        <i class="fa-solid fa-location-dot"></i>Contact Us
                    </a>
                    <a class="hover-p-color" href="">
                        <i class="fa-solid fa-circle-info"></i>Need help
                    </a>
                </div>
            </div>
        </section>

        <section class="header-middle">
            <div class="container d-flex jc-space-between ai-center">
                <div class="header-left  d-flex jc-space-between ai-center">
                    <div class="logo">
                        <a href=""><img src="https://d-themes.com/html/riode/images/demos/demo4/logo.png" alt=""></a>
                    </div>

                    <ul class="menu  d-flex jc-space-between ai-center">
                        <li><a href="">Home</a></li>
                        <li><a href="">Products</a></li>
                        <li><a href="">Sale</a></li>
                        <li><a href="">About</a></li>
                        <li class="search d-flex ai-center">
                            <form:input type="text" path="" placeholder="Search..."/>
                            <a href="">
                                <i class="fa-solid fa-magnifying-glass"></i>
                            </a>
                        </li>
                    </ul>
                </div>
    
                <ul class="header-right  d-flex jc-space-between ai-center">
                   
                    <li><a href=""><i class="fa-solid fa-user"></i></a></li>
                    <li><a href=""><i class="fa-solid fa-bag-shopping"></i></a></li>
                </ul>
            </div>
        </section>

        <div class="header-support"></div>

    </header>

    <main class="main login-container">
        <div class="container">
            
            <form:form class="login-form:form" action="">
                <h2>Login</h2>

                <form:input type="text" path="" placeholder="Username"/>
                <!-- <span class="errors">Have to type account</span> -->
                <form:input type="password" path="" placeholder="Password"/>
                <!-- <span class="errors">Wrong password</span> -->

                <div class="link">
                    <a href="">I don't have account</a>
                    <a href="">Lost your password?</a>
                </div>
                <!-- <br> -->
                <form:button>Login</form:button>
            </form:form>
        </div>
    </main>
</body>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js" referrerpolicy="no-referrer"></script>
    <script src="./dist/js/owl.carousel.js"></script>
    <script src="./dist/js/home.js"></script>
</html>