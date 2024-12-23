package com.alinesno.infra.base.gateway.manage.timer;

import com.alinesno.infra.base.gateway.core.entity.Monitor;
import com.alinesno.infra.base.gateway.core.entity.Route;
import com.alinesno.infra.base.gateway.core.service.MonitorService;
import com.alinesno.infra.base.gateway.core.service.RouteService;
import com.alinesno.infra.base.gateway.core.util.Constants;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @description 定时扫描监控配置表，获取发送告警的网关服务，并发送邮件通知
 * @author  JL
 * @date 2021/04/16
 * @version v1.0.0
 */
@Slf4j
@Service
public class TimerSendAlarmService {
    @Resource
    private MonitorService monitorService;
    @Resource
    private JavaMailSenderImpl javaMailSender;
    @Resource
    private RouteService routeService;

    @Value("${spring.mail.username:}")
    private String form;

    /**
     * 每分钟执行一次,获取告警网关路由，发送告警邮件
     */
    @Scheduled(cron = "5 * * * * ?")
    public void syncRequestTimeCache(){
        log.info("执行定时任务：发送网关路由告警邮件通知...");
        //读取所有监控配置，判断哪些已经超过30s，没有客户端请求，将其放入到监控队列
        List<Monitor> monitorList = monitorService.validRouteMonitorList();
        if (CollectionUtils.isEmpty(monitorList)){
            return ;
        }
        Date sendTime;
        long validTime;
        String sendDate = DateFormatUtils.format(new Date(), Constants.DATE_FORMAT_DAY);
        for (Monitor monitor : monitorList){
            //如果已是2告警状态,则发送告警邮件
            if (!Constants.ALARM.equals(monitor.getStatus())){
                continue;
            }
            //如果没有超出限定的最大发送频率值，则不发送告警邮件
            sendTime = monitor.getSendTime();
            if (sendTime != null){
                //距上次发送时间的时长
                validTime = System.currentTimeMillis() - sendTime.getTime();
                if (validTime <= getMaxAlarmTime(monitor.getFrequency())){
                    continue;
                }
            }
            //存在邮箱，则发送告警邮件
            if (StringUtils.isNotBlank(monitor.getEmails())){
                Route route = routeService.findById(monitor.getId());
                if (route != null){
                    String subject = String.format("网关告警邮件-【%s】-%s", route.getId(), sendDate);
                    String body = this.emailBody(route.getId(), route.getName(), route.getUri(),DateFormatUtils.format(monitor.getAlarmTime(), Constants.YYYY_MM_DD_HH_MM_SS), monitor.getTopic());
                    String [] emails = StringUtils.split(monitor.getEmails(), ",");
                    mailSend(subject, body, form, emails);
                }
            }
            //更新发送时间
            monitor.setSendTime(new Date());
            monitorService.update(monitor);
        }
    }

    /**
     * 发送邮件
     * @param subject
     * @param text
     * @param from
     * @param to
     */
    public boolean mailSend(String subject, String text, String from, String [] to) {
        //创建一个复杂的消息邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            //用MimeMessageHelper来包装MimeMessage
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(text, true);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            //mimeMessageHelper.addAttachment("topic.jpg",new File("D:\\topic.jpg"));
            javaMailSender.send(mimeMessage);
        } catch(Exception me){
            log.error("error", me);
            log.error("发送网关告警信息：{},到邮箱：{}失败",text, StringUtils.join(to,","));
            return false;
        }
        log.info("成功发送网关告警信息：{},到邮箱：{}",text,StringUtils.join(to,","));
        return true;
    }

    /**
     * 组建email中的内容，并进行简单格式化
     * @param id
     * @param name
     * @param uri
     * @param alarmTime
     * @param topic
     * @return
     */
    private String emailBody(String id, String name, String uri, String alarmTime, String topic){
        String body = "<html><body><div>" +
                "<p style=\"color: #ffffff; background-color: dodgerblue;\">&nbsp;&nbsp; 网关告警邮件-服务访问故障</p>\n" +
                "<ul style=\"line-height: 30px; font-size: 10.5pt;\">" +
                "<li>告警网关路由ID："+ id +"</li>" +
                "<li>告警网关由路服务："+ name +"</li>" +
                "<li>心跳检测地址："+ uri +"</li>" +
                "<li>告警时间："+ alarmTime +"</li>" +
                "<li>告警内容：网关服务对路由地址进行心跳检测失败！"+ topic +"</li>" +
                "<li>发送告警系统：FLYING-FISH-GATEWAY 网关服务</li></ul>" +
                "<p style=\"color: #ffffff; background-color: dodgerblue;\">&nbsp;&nbsp;  此网关路由服务访问告警，请及时处理！</p>" +
                "</div></body></html>";
        return body;
    }

    /**
     * 获取监控通知频率转换后的最大时间
     * @param frequency 频率
     * @return
     */
    private long getMaxAlarmTime(String frequency){
        long maxAlarmTime = 0L;
        switch (frequency) {
            case "30m" -> maxAlarmTime = 30 * 60 * 1000;
            case "1h" -> maxAlarmTime = 60 * 60 * 1000;
            case "5h" -> maxAlarmTime = 5 * 60 * 60 * 1000;
            case "12h" -> maxAlarmTime = 12 * 60 * 60 * 1000;
            case "24h" -> maxAlarmTime = 24 * 60 * 60 * 1000;
            default -> {
            }
        }
        return maxAlarmTime;
    }

}
