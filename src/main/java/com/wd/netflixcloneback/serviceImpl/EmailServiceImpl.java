package com.wd.netflixcloneback.serviceImpl;

import com.wd.netflixcloneback.exception.EmailNotVerifiedException;
import com.wd.netflixcloneback.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private  static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        sendHtmlEmail(
                toEmail,
                "Netflix Clone Team - Verify Your Email",
                getVerificationEmailHtml(token)
        );
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        sendHtmlEmail(
                toEmail,
                "Netflix Clone Team - Password Reset",
                getResetPasswordEmailHtml(token)
        );
    }

    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // SECOND PARAM = HTML

            mailSender.send(message);

        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new EmailNotVerifiedException("Failed to send email");
        }
    }


    private String getVerificationEmailHtml(String token) {
        String link = frontendUrl + "/verify-email?token=" + token;

        return """
    <html>
    <body style="font-family: Arial; background-color: #f4f4f4; padding: 20px;">
        <div style="max-width: 600px; margin:auto; background:#fff; padding:20px; border-radius:8px;">
            <h2 style="text-align:center; color:#4CAF50;">Verify Your Email</h2>

            <p>Hello,</p>
            <p>Thank you for registering with <strong>Netflix Clone</strong>. Click the button below to verify your account:</p>

            <div style="text-align:center; margin:25px 0;">
                <a href="%s" style="background:#4CAF50; color:white; padding:12px 22px; text-decoration:none; border-radius:5px;">
                    Verify Email
                </a>
            </div>

            <p>If the button doesn't work, use this link:</p>
            <p style="color:#4CAF50;">%s</p>

            <p>This link will expire in <strong>24 hours</strong>.</p>

            <p>Best Regards,<br/>Netflix Clone Team</p>
        </div>
    </body>
    </html>
    """.formatted(link, link);
    }


    private String getResetPasswordEmailHtml(String token) {
        String link = frontendUrl + "/reset-password?token=" + token;

        return """
    <html>
    <body style="font-family: Arial; background-color: #f4f4f4; padding: 20px;">
        <div style="max-width: 600px; margin:auto; background:#fff; padding:20px; border-radius:8px;">
            <h2 style="text-align:center; color:#2196F3;">Reset Your Password</h2>

            <p>Hello,</p>
            <p>We received a request to reset your password. Click the button below to continue:</p>

            <div style="text-align:center; margin:25px 0;">
                <a href="%s" style="background:#2196F3; color:white; padding:12px 22px; text-decoration:none; border-radius:5px;">
                    Reset Password
                </a>
            </div>

            <p>If the button doesn't work, copy this link:</p>
            <p style="color:#2196F3;">%s</p>

            <p>This link will expire in <strong>1 hour</strong>.</p>

            <p>If you didn't request this, please ignore this email.</p>

            <p>Best Regards,<br/>Netflix Clone Team</p>
        </div>
    </body>
    </html>
    """.formatted(link, link);
    }

}
