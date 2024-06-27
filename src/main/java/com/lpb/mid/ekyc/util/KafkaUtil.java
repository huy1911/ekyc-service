package com.lpb.mid.ekyc.util;

import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Log4j2
public class KafkaUtil {
    public static JWTDto getMessageKafka(SendKafkaDto sendKafkaDto, String topicSend, String topicReply, ReplyingKafkaTemplate<String, Object, Object> replyingTemplate, String key, String refNo) {
        JWTDto jwtDto;
        try {
            log.info("getMessageKafka : request send kafka -----> {} by refNo --->{}", sendKafkaDto,refNo);
            ProducerRecord<String, Object> record = new ProducerRecord<>(topicSend, key + sendKafkaDto.getUsername() + UUID.randomUUID(), sendKafkaDto);
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, topicReply.getBytes()));
            record.headers().add(new RecordHeader(KafkaHeaders.CORRELATION_ID, UUID.randomUUID().toString().getBytes()));
            RequestReplyFuture<String, Object, Object> replyFuture = replyingTemplate.sendAndReceive(record);
            ConsumerRecord<String, Object> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            log.info("getMessageKafka: read message consumer success by refNo ------> {}", refNo);
            String val = consumerRecord.value().toString();
            jwtDto = StringConvertUtils.readValueWithInsensitiveProperties(val, JWTDto.class);
            log.info("getMessageKafka : jwtDto getMessageKafka ----> {} by refNo --->{}", jwtDto,refNo);
            return jwtDto;
        }catch (TimeoutException e) {
            log.error("getMessageKafka: getMessageKafka timeout -----> {} by refNo --->{}", e.getMessage(),refNo);
            throw new ExceptionHandler(ErrorMessage.ERR_90);
        } catch (Exception e) {
            log.error("getMessageKafka: getMessageKafka error -----> {} by refNo --->{}", e.getMessage(),refNo);
            throw new ExceptionHandler(ErrorMessage.ERR_99);
        }

    }
}
