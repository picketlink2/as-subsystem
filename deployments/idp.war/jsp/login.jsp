<html><head><title>Login Page</title></head>
<body>
<font size='5' color='blue'>Please Login</font><hr>

<form id="loginForm" action='j_security_check' method='post'>
<table>
 <tr><td>Name:</td>
   <td><input type='text' id="usernameText" name='j_username'></td></tr>
 <tr><td>Password:</td> 
   <td><input type='password' id="passwordText" name='j_password' size='8'></td>
 </tr>
</table>
<br>
  <input type='submit' id="loginButton" value='login'> 
</form></body>
 </html>
