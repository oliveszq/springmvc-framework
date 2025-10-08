package com.example.framework.utils;

import com.example.framework.model.dto.EmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Component
public class EmailUtil {

    @Value("${spring.mail.username}")
    private String email;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    //邮箱验证码消费队列
    public void sendHtmlMail(EmailDTO emailDTO) {
        try {
            // 创建一个 MIME 消息
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            // 使用 MimeMessageHelper 来帮助构建 MIME 消息
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            // 创建 Thymeleaf 模板上下文
            Context context = new Context();
            // 将邮件对象中的评论 Map 添加到上下文中
            context.setVariables(emailDTO.getCommentMap());
            // 根据模板和上下文渲染邮件内容
            String process = templateEngine.process(emailDTO.getTemplate(), context);
            mimeMessageHelper.setFrom(email); // 设置发件人
            mimeMessageHelper.setTo(emailDTO.getEmail()); // 设置收件人
            mimeMessageHelper.setSubject(emailDTO.getSubject()); // 设置主题
            mimeMessageHelper.setText(process, true); // 设置邮件内容，第二个参数为 true 表示使用 HTML 格式发送邮件
            javaMailSender.send(mimeMessage); // 发送邮件
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
