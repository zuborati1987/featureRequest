<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="en">
<jsp:include page="../snippets/head.jsp">
    <jsp:param name="title" value="Profile"/>
</jsp:include>
<body>
<h1>Profile</h1>
<p>Email: <c:out value="${user.email}"/></p>
<p>Password: <c:out value="${user.password}"/></p>
<h2>Coupons</h2>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Percentage (%)</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="coupon" items="${coupons}">
        <tr>
            <td>${coupon.id}</td>
            <td><a href="coupon?id=<c:out value="${coupon.id}"/>">${coupon.name}</a></td>
            <td>${coupon.percentage}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<h2>Links</h2>
<ul>
    <li><a href="shops">Shops</a></li>
    <li><a href="couponsservlet">Coupons</a></li>
</ul>
<jsp:include page="../snippets/logout.jsp"/>
</body>
</html>
