package com.mongo.lostfound.controller;

import com.mongo.lostfound.Response.Response;
import com.mongo.lostfound.dto.MessageDTO;
import com.mongo.lostfound.service.MessageService;
import com.mongo.lostfound.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public MessageVO sendMessage(@RequestBody MessageDTO messageDTO) {
        return messageService.sendMessage(messageDTO);
    }

    @GetMapping("/conversation")
    public List<MessageVO> getConversation(@RequestParam String userId1,
                                           @RequestParam String userId2) {
        return messageService.getConversation(userId1, userId2);
    }

    @PostMapping("/markRead")
    public ResponseEntity<Response<Object>> markMessagesRead(@RequestParam String fromUserId, @RequestParam String toUserId) {
        messageService.markMessagesRead(fromUserId, toUserId);
        return ResponseEntity.ok(new Response<>(true, null, "设置成功"));
    }
}
