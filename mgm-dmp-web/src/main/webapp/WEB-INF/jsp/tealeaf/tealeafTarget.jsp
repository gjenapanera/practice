<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/plain" %>
<% String TLT_TARGET_VERSION = "1.2"; %>
<%!
    public static String toMD5(String s)
    {
        if (s == null || s.length() <= 0) {
            return null;
        }
        try {
          java.security.MessageDigest alg = java.security.MessageDigest.getInstance("MD5");
          alg.reset();
          alg.update(s.getBytes());
          byte[] digest = alg.digest();
        
          StringBuffer hashedVal = new StringBuffer();
          for (int i=0; i < digest.length; i++) {
	          String hx = Integer.toHexString(0xFF & digest[i]);
	          if (hx.length() < 2) {
	            hx = "0" + hx;
	          }
          	hashedVal.append(hx);
          }
          return hashedVal.toString();
        }
        catch (Exception e) {
          return e.toString();
        }
    }
%>
<html>
	<body>
	Response
    <%
        long ts1 = System.currentTimeMillis();
        long totalLength = 0L;
        java.io.BufferedReader rdr = request.getReader();
        String str1 = null, str2 = null;

        try {
            totalLength = rdr.skip(12*1024);
        } catch (Exception e) {
            out.print("<br>Exception when reading request data!" + e.toString());
        }

        long ts2 = System.currentTimeMillis();
    %>
	<hr>
	Read <% out.print(totalLength); %> bytes in <% out.print(ts2-ts1); %>ms.
    <%
        boolean versionFlag = (request.getParameter("version") != null);
        boolean serverFlag = (request.getParameter("server") != null);
        
        if (versionFlag || serverFlag) {
            out.print("<hr>");
        }
        if (versionFlag) {
            out.print("<br> Version " + TLT_TARGET_VERSION);
        }
                
        if (serverFlag) {
            String serverIP = java.net.InetAddress.getLocalHost().getHostAddress();
            String serverName = java.net.InetAddress.getLocalHost().getHostName();
            out.print("<br>" + toMD5(serverName) + ", " + toMD5(serverIP));
            serverName = serverIP = "";
        }
    %>
	</body>
</html>
