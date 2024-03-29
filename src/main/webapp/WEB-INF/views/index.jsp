<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JNU Parking System</title>

    <script>

        let message = "${message}";
        if(message != null && message != ''){
            alert(message);
        }
    </script>

    <script src="https://kit.fontawesome.com/78aaca3b36.js" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="<c:url value='/css/index.css'/>">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Orbit&display=swap" rel="stylesheet">
    <link href="https://fonts.cdnfonts.com/css/ds-digital" rel="stylesheet">




</head>
<body>
    <jsp:include page="navBar.jsp"/>

<div style="text-align:center">
    <h3>주차공간 <span class="digital-font" id = "count">${count}</span></h3>
    <button class="enterBtn" type="button" onclick="location.href='<c:url value='/parking/enter'/>' ">Enter</button>
    <button class="exitBtn" type="button" onclick="location.href='<c:url value='/parking/exit'/>' ">Exit</button>

</div>

    <script>
        var countElement = document.getElementById("count");
        var count = parseInt(countElement.innerText);

        if (count === 0) {
            countElement.style.color = "red";
        } else if (count >= 1) {
            countElement.style.color = "green";
        }
    </script>
</body>
</html>