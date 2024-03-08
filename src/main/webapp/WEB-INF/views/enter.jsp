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
        function checkCarNumber() {

            var carNumberField = document.getElementById("carNumber");
            var carNumber = carNumberField.value;
            console.log("차량 번호: " + carNumber);
            var pattern1 = /^\d{2}[가-힣]\d{4}$/; // 차량 번호 패턴 (00가0000 형식)
            var pattern2 = /^\d{3}[가-힣]\d{4}$/; // 차량 번호 패턴 (000가0000 형식)
            var messageField = document.getElementById("message");
            var submitButton = document.getElementById("submitButton");

            if ((pattern1.test(carNumber) && carNumber.length === 7) || (pattern2.test(carNumber) && carNumber.length === 8)) {
                messageField.textContent = "올바른 차량번호입니다.";
                messageField.style.color = "green";
                submitButton.disabled = false; // 버튼 활성화
            } else {
                messageField.textContent = "올바르지 않은 차량번호입니다.";
                messageField.style.color = "red";
                submitButton.disabled = true; // 버튼 비활성화
            }
        }
    </script>

    <script src="https://kit.fontawesome.com/78aaca3b36.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/enter.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbit&display=swap" rel="stylesheet">

</head>
<body>
    <jsp:include page="navBar.jsp"/>
    <div class="container">
        <form:form action="/myproject/parking/enter" method="post" modelAttribute="car">
        <div class="title">입차하실 차량 번호를 입력하세요</div>
        <input class="input-field" type="text" id="carNumber" name="carNumber" placeholder="00가0000 또는 000가0000" oninput="checkCarNumber()">
        <div id="message"></div>
        <button type="submit" id="submitButton" disabled>입차하기</button> <!-- 처음에는 버튼 비활성화 -->
        </form:form>
    </div>
</body>
</html>