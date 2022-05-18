<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Riode - Bill Report</title>
    <link rel="icon" type="image/png" href="https://d-themes.com/html/riode/images/icons/favicon.png">

    <link rel="stylesheet" href="./dist/css/reset.css">
    <link rel="stylesheet" href="./dist/css/adminAccount.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css" integrity="sha512-KfkfwYDsLkIlwQp6LFnl8zNdLGxu9YAA1QvwINks4PhcElQSvqcyVLLD9aMhXd13uQjoXtEKNosOWaZqXgel0g==" crossorigin="anonymous" referrerpolicy="no-referrer" />

</head>
<body>
    
    <aside class="aside">
        <a href="adminHome.html" class="admin">
            <div class="logo">
                <i class="fa-solid fa-a"></i>
            </div>
            Admin
        </a>

        <ul class="functions">
            <li class="function">
                <a href="adminAccount.html">
                    <div class="icon">
                        <i class="fa-solid fa-users"></i>
                    </div>
                    Account 
                </a>
            </li>

            <li class="function">
                <a href="adminBill.html"  class="active">
                    <div class="icon">
                        <i class="fa-solid fa-receipt"></i>

                    </div>
                    Bill 
                </a>
            </li>

            <li class="function">
                <a href="">
                    <div class="icon">
                        <i class="fa-solid fa-shirt"></i>
                    </div>
                    Products 
                </a>
            </li>

            
        </ul>

        <button class="btn-log-out">Log out</button>
    </aside>

    <!-- Main -->
    <main class="main">

        <!-- Map  -->
        <div class="main-header">
            <div class="header-left">
                <h2>Bill Details</h2>

                <div class="map">
                    <a href="home.html">Home</a>
                    /
                    <a href="adminHome.html">Admin</a>
                    /
                    <a href="adminBill.html">Bill </a>
                </div>
            </div>

            <div class="header-right">
                
            </div>
        </div>

        <div class="bill-data">
            <div class="data">
                <label for="">ID</label>
                <p class="value">123</p>
            </div>

            <div class="data">
                <label for="">Date</label>
                <p class="value">11-12-2022</p>
            </div>

            <div class="data">
                <label for="">Username</label>
                <p class="value">hoductrung</p>
            </div>

            <div class="data">
                <label for="">Total Price</label>
                <p class="value">$120.00</p>
            </div>
        </div>

        <!-- Main talbe data  -->
        <div class="main-content">
            <div class="table-container">
                <table>
                    <tr>
                        <th>Product ID</th>
                        <th>Product Name</th>
                        <th>Price</th>
                        <th>Quantity</th>
                    </tr>
    
                    <div class="scroll">
                        <tr>
                            <td>124</td>
                            <td>A foking good T-shirt</td>
                            <td>$30.00</td>
                            <td>1</td>
                        </tr>

                        <tr>
                            <td>124</td>
                            <td>A foking good T-shirt</td>
                            <td>$30.00</td>
                            <td>1</td>
                        </tr>

                        <tr>
                            <td>124</td>
                            <td>A foking good T-shirt</td>
                            <td>$30.00</td>
                            <td>1</td>
                        </tr>

                        <tr>
                            <td>124</td>
                            <td>A foking good T-shirt</td>
                            <td>$30.00</td>
                            <td>1</td>
                        </tr>
                    </div>
                   
                </table>
            </div>


        </div>
    </main>

  

</body>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.6.0/jquery.min.js" referrerpolicy="no-referrer"></script>
    <script src="./dist/js/owl.carousel.js"></script>
    <script src="./dist/js/home.js"></script>
</html>