package fashion_shop.mailer;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mailer/")
@Service("mailer")
public class Mailer {
	@Autowired
	JavaMailSender mailer;
	@RequestMapping("send")
	public void send(String from, String to, String subject, String body) {
		try {
			MimeMessage mail= mailer.createMimeMessage();
			MimeMessageHelper heper= new MimeMessageHelper(mail,true,"utf-8");
			heper.setFrom(from, from);
			heper.setTo(to);
			heper.setReplyTo(from,from);
			heper.setSubject(subject);
			heper.setText(body,true);
			mailer.send(mail);
		}catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}