<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>

    <script>
        let message = "${message}";
        if(message != null && message != ''){
            alert(message);
        }
        function checkInt() {
            var numberField = document.getElementById("paidFee");
            var number = numberField.value;
            console.log("입력 값: " + message);
            var pattern = /^\d+$/; // 0 이상의 정수 패턴
            var messageField = document.getElementById("message");
            var submitButton = document.getElementById("submitButton");

            if (pattern.test(number)) {
                messageField.textContent = "올바른 양식입니다.";
                messageField.style.color = "green";
                submitButton.disabled = false; // 버튼 활성화
            } else {
                messageField.textContent = "올바르지 않은 양식입니다.";
                messageField.style.color = "red";
                submitButton.disabled = true; // 버튼 비활성화
            }
        }
    </script>
    <script src="https://kit.fontawesome.com/78aaca3b36.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/exitPay.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbit&display=swap" rel="stylesheet">


</head>
<body>

<div>
    <nav class="jnu_nav">
        <div class="nav_logo">
            <img src="/myproject/logo/jnupslogo.jpeg" alt = "JNU Parking System Logo" style="width : 50px;">
            <a>JNU Parking System</a>
        </div>
        <ul class="nav_menu">
        </ul>
        <ul class="nav_icons">
            <li>
                <a>
                    <i class="fa-brands fa-github fa" style="font-size: 50px; color: #04ac6b;"></i>
                </a>
            </li>
        </ul>
    </nav>
</div>

<div class="container">
<form:form action="/myproject/parking/exit2" method="post" modelAttribute="car">
    <div class="title">주차비 ${car.fee}원 입니다</div>
    <input class="input-field" type="text" id="paidFee" name="paidFee" placeholder="0이상 정수로 기입하시오" oninput="checkInt()">
    <div id="message"></div>
    <button type="submit" id="submitButton" disabled>지불하기</button>
    <input type="hidden" id="carNumber" name="carNumber" value="${car.carNumber}">
    <input type="hidden" id="fee" name="fee" value="${car.fee}">
    <input type="hidden" id="enterDate" name="enterDate" value="${car.enterDate.getTime()}">
    <input type="hidden" id="haveRegularParkingTicket" name="haveRegularParkingTicket" value="${car.haveRegularParkingTicket}">
    <!-- 처음에는 버튼 비활성화 -->
</form:form>
</div>
</body>
</html>