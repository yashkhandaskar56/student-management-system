<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>${title}</title>
  <style>
    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: #f4f7fa;
      margin: 0;
      padding: 0;
    }
    .email-container {
      max-width: 600px;
      margin: 40px auto;
      background: #ffffff;
      border-radius: 12px;
      box-shadow: 0 6px 20px rgba(0,0,0,0.1);
      overflow: hidden;
    }
    .header {
      background: linear-gradient(135deg, #118ab2, #06d6a0);
      color: white;
      padding: 20px;
      text-align: center;
      font-size: 22px;
      font-weight: 700;
    }
    .content {
      padding: 25px 35px;
      color: #333;
      line-height: 1.6;
      font-size: 16px;
    }
    .content h2 {
      color: #118ab2;
      margin-bottom: 15px;
    }
    .content p {
      margin: 8px 0;
    }
    .highlight {
      background: #06d6a025;
      border-left: 5px solid #06d6a0;
      padding: 10px 15px;
      border-radius: 5px;
      margin: 15px 0;
    }
    .footer {
      background: #f8f9fa;
      text-align: center;
      padding: 15px;
      font-size: 14px;
      color: #666;
      border-top: 1px solid #eee;
    }
    .footer a {
      color: #118ab2;
      text-decoration: none;
      font-weight: 600;
    }
  </style>
</head>
<body>
  <div class="email-container">
    <div class="header">
      ISEES TECHNOLOGIES
    </div>

    <div class="content">
      <h2>🎓 Admission Confirmation</h2>

      <p>Dear <strong>${name!"Student"}</strong>,</p>

      <p class="highlight">
        ${content!"We are delighted to inform you that your admission application has been received successfully."}
      </p>

      <p>Our admission team will review your application shortly and contact you for the next steps.</p>

      <p>If you have any questions, feel free to reach us at:
        <strong>info@iseestechnologies.com</strong>
      </p>

      <p>Best Regards,<br>
      <strong>ISEES TECHNOLOGIES Admission Team</strong></p>
    </div>

    <div class="footer">
      <p>&copy; 2025 ISEES TECHNOLOGIES | All rights reserved</p>
      <p>
        <a href="#">Website</a> | 
        <a href="#">Facebook</a> | 
        <a href="#">Instagram</a>
      </p>
    </div>
  </div>
</body>
</html>
