<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Exam Result</title>
    <style>
        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background-color: #f9f9f9;
            padding: 30px;
            color: #333;
        }

        .container {
            max-width: 600px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            padding: 25px;
            margin: auto;
        }

        h2 {
            color: #007bff;
            text-align: center;
            margin-bottom: 10px;
        }

        p {
            font-size: 15px;
            margin: 8px 0;
        }

        .score-box {
            background: #eaf6ff;
            border: 1px solid #007bff;
            border-radius: 8px;
            padding: 15px;
            text-align: center;
            margin: 20px 0;
        }

        .footer {
            text-align: center;
            font-size: 13px;
            color: #777;
            margin-top: 20px;
        }

        .grade {
            font-size: 18px;
            font-weight: bold;
            color: #0d6efd;
        }

        .percentage {
            font-size: 16px;
            font-weight: 600;
            color: #28a745;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Exam Result Notification</h2>

    <p>Hi <strong>${name!"Student"}</strong>,</p>

    <p>Thank you for completing your exam. Below are your results:</p>

    <div class="score-box">
        <p><strong>Email:</strong> ${email!"Not Available"}</p>
        <p><strong>Your Score:</strong> ${totalScore!0} / ${totalMarks!0}</p>

        <p class="percentage">
            <strong>Percentage:</strong> ${percentage!0}%
        </p>

        <p class="grade">
            <strong>Grade:</strong> ${grade!"N/A"}
        </p>
    </div>

    <p>
        Keep learning and improving! You can log in to your dashboard
        to view detailed analysis of your answers.
    </p>

    <div class="footer">
        <p>
            Best regards,<br>
            <strong>ISEES TECHNOLOGIES</strong><br>
            support@iseestech.com
        </p>
    </div>
</div>
</body>
</html>