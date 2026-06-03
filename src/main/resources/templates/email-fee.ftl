<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Payment Confirmation</title>
</head>

<body style="font-family:Segoe UI;background:#f6f9fc;margin:0;padding:0;">

<div style="max-width:600px;margin:30px auto;background:#ffffff;border-radius:10px;
box-shadow:0 4px 15px rgba(0,0,0,0.1);overflow:hidden;">

  <div style="background:linear-gradient(135deg,#118ab2,#06d6a0);
  color:white;text-align:center;padding:25px;">
    <h1>🎓 Payment Confirmation</h1>
  </div>

  <div style="padding:30px;line-height:1.6;">
    <h2>Hi ${student!"Student"}!</h2>

    <p>Your payment has been <strong>successfully received</strong>.</p>

    <div style="background:#f0f7f9;padding:15px 20px;border-radius:8px;margin:20px 0;">
      <p><strong>📘 Course:</strong> ${course!"N/A"}</p>
      <p><strong>💰 Amount Paid:</strong> ₹${paid!"0.00"}</p>
      <p><strong>🧾 Total Fee:</strong> ₹${total!"0.00"}</p>
      <p><strong>📅 Remaining Fee:</strong> ₹${remaining!"0.00"}</p>
      <p><strong>🟢 Status:</strong> <span style="color:#06d6a0;font-weight:bold;">Payment Successful</span></p>
    </div>

    <a href="http://localhost:8080/student-dashboard.html"
       style="display:inline-block;padding:10px 20px;
       background:#118ab2;color:white;text-decoration:none;
       border-radius:6px;font-weight:600;">
       Go to Dashboard
    </a>

    <p style="margin-top:25px;">
      Thank you for choosing <strong>ISEES Technologies</strong> 🚀
    </p>
  </div>

  <div style="text-align:center;background:#f6f9fc;padding:15px;
  font-size:14px;color:#777;">
    © 2025 ISEES Technologies | Do not reply
  </div>

</div>
</body>
</html>