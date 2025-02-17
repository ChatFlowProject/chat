package com.example.chatrepo.service;

import com.example.chatrepo.common.*;
import com.example.chatrepo.config.member_server.MemberServiceClient;
import com.example.chatrepo.config.noti_server.MentionNotificationRequest;
import com.example.chatrepo.config.noti_server.NotificationClient;
import com.example.chatrepo.dto.ChatRoomDetail;
import com.example.chatrepo.dto.Sender;
import com.example.chatrepo.dto.req.ChatMessageReq;
import com.example.chatrepo.dto.req.CreateChatRoomReq;
import com.example.chatrepo.dto.req.GetMessageReq;
import com.example.chatrepo.dto.res.ChatMessageRes;
import com.example.chatrepo.dto.res.CreateChatRoomRes;
import com.example.chatrepo.dto.res.GetChatRoomRes;
import com.example.chatrepo.dto.Participant;
import com.example.chatrepo.dto.res.SearchChatRoomRes;
import com.example.chatrepo.entity.Chat;
import com.example.chatrepo.entity.ChatRoom;
import com.example.chatrepo.exception.custom.InvalidChatException;
import com.example.chatrepo.file.CloudFileUploadService;
import com.example.chatrepo.repository.ChatRepository;
import com.example.chatrepo.repository.ChatRoomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberServiceClient memberServiceClient;
    private final CloudFileUploadService cloudFileUploadService;
    private final NotificationClient notificationClient;

    @Autowired
    private EntityManager entityManager;

    // 모든 멤버 조회 메서드
    public List<MemberResponse> getAllMembers() {
        ApiResponse<List<MemberResponse>> response = memberServiceClient.getAllMembers();

        if (response.status() != ApiStatus.SUCCESS || response.data() == null) {
            throw new RuntimeException("Failed to fetch members: " + response.message());
        }

        return response.data();
    }

    // 특정 멤버 정보를 ID로 조회하는 메서드
    private MemberResponse findMemberById(UUID memberId) {
        List<MemberResponse> members = getAllMembers();

        return members.stream()
                .filter(member -> member.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Member not found for ID: " + memberId));
    }

    // 1. 새로운 채팅방 생성 (1:1 또는 그룹)
    @Transactional
    public CreateChatRoomRes createChatRoom(CreateChatRoomReq createChatRoomReq) {

        // 상대방 사용자 정보 가져오기
        List<UUID> recipientIds = createChatRoomReq.getRecipientIds();

        // 상대방 사용자 정보 조회
        List<MemberResponse> recipients = recipientIds.stream()
                .map(this::findMemberById)
                .collect(Collectors.toList());
        // 참여자 목록 생성
        List<UUID> participants = new ArrayList<>(recipientIds);
        participants.add(createChatRoomReq.getUserId());

        // 상대방 이름 리스트 생성
        List<String> recipientNames = recipients.stream()
                .map(MemberResponse::getName)
                .collect(Collectors.toList());
        // 채팅방 이름 설정
        String roomName = String.join(",", recipientNames);
        // 기존 채팅방 존재 여부 확인
        List<ChatRoom> existingRooms = chatRoomRepository.findByParticipants(participants, participants.size());
        ChatRoom chatRoom;
        if (existingRooms.isEmpty()) {
            // 새 채팅방 생성
            chatRoom = ChatRoom.builder()
                    .roomName(roomName)
                    .participants(participants)
                    .build();
            chatRoomRepository.save(chatRoom);
        } else {
            // 기존 채팅방 반환
            chatRoom = existingRooms.get(0);
        }
        return CreateChatRoomRes.builder()
                .chatRoomId(chatRoom.getId())
                .chatRoomName(chatRoom.getRoomName())
                .participants(participants)
                .recipientName(recipientNames)
                .build();
    }
    // 2. 채팅방 목록 조회
    public List<GetChatRoomRes> getMyChatRoomList(UUID userId) {
        List<ChatRoom> myChatRooms = chatRoomRepository.findAllByParticipantsContaining(userId);

       return myChatRooms.stream().map(chatRoom -> {
           List<Participant> participants = chatRoom.getParticipants().stream()
                   .map(participantId -> {
                       MemberResponse member = findMemberById(participantId);
                       return Participant.builder()
                               .userId(participantId)
                               .nickname(member.getNickname())
                               .build();
                   })
                   .collect(Collectors.toList());
           // 메시지 및 전송 시간 조회
           String lastMessage = "";
           LocalDateTime lastMessageDay = null;
           List<Chat> chatList = chatRoom.getChatList();
           if(!chatList.isEmpty()){
               Chat lastChat = chatList.stream()
                       .max(Comparator.comparing(Chat::getSendTime))
                       .orElse(null);
               if(lastChat != null){
                   lastMessage = lastChat.getMessage();
                   lastMessageDay = lastChat.getSendTime();
               }
           }
           return GetChatRoomRes.builder()
                   .chatRoomId(chatRoom.getId())
                   .chatRoomName(chatRoom.getRoomName())
                   .participants(participants)
                   .participantsLength(participants.size())
                   .build();
       }).collect(Collectors.toList());
    }

    // 채팅 메시지 저장
    @Transactional
    public void saveChat(GetMessageReq getMessageReq, MultipartFile imgFile) {
        ChatRoom chatRoom = chatRoomRepository.findById(getMessageReq.getChatRoomId())
                .orElseThrow(() -> new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));

        UUID senderId = getMessageReq.getSenderId();
        if (!chatRoom.getParticipants().contains(senderId)) {
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_USER_ID);
        }

        String imageUrl = null;
        if (imgFile != null && !imgFile.isEmpty()) {
            imageUrl = cloudFileUploadService.uploadImg(imgFile);
        }

        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .message(getMessageReq.getMessage())
                .sendTime(LocalDateTime.parse(getMessageReq.getSendTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .senderId(senderId)
                .imageUrl(imageUrl)
                .build();
        chatRepository.save(chat);
    }
    // 채팅방 검색
    public SearchChatRoomRes searchChatRooms(String query){
        List<ChatRoom> matchingRooms = chatRoomRepository.findByRoomNameContainingIgnoreCase(query);
        List<ChatRoomDetail> chatRooms = matchingRooms.stream().map(chatRoom -> {
            List<Participant> participants = chatRoom.getParticipants().stream().map(participantId -> {
                MemberResponse member = findMemberById(participantId);
                return Participant.builder()
                        .userId(participantId)
                        .nickname(member.getNickname())
                        .build();
            }).collect(Collectors.toList());
            return ChatRoomDetail.builder()
                    .chatRoomId(chatRoom.getId())
                    .name(chatRoom.getRoomName())
                    .participants(participants)
                    .build();
        }).collect(Collectors.toList());
        return SearchChatRoomRes.builder()
                .query(query)
                .chatRooms(chatRooms)
                .build();
    }
    public ChatMessageRes processMessage(Long chatroomId, ChatMessageReq chatMessageReq){
        // 채팅방 확인
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId).orElseThrow(()->new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));
        MemberResponse sender = findMemberById(chatMessageReq.getUserId());
        // 메시지 저장
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .senderId(chatMessageReq.getUserId())
                .message(chatMessageReq.getMessage())
                .attachments(chatMessageReq.getAttachments())
                .sendTime(LocalDateTime.now())
                .build();
        chatRepository.save(chat);

        // 멘션된 사용자 추출 및 알림 전송
        List<MemberResponse> allMembers = getAllMembers();
        List<UUID> mentionedUserIds = extractMentionedUserIds(chat.getMessage(), allMembers);
        if(!mentionedUserIds.isEmpty()){
            MentionNotificationRequest notificationRequest = MentionNotificationRequest.builder()
                    .receiverIds(mentionedUserIds)
                    .senderId(chatMessageReq.getUserId())
                    .message(chat.getMessage())
                    .chatRoomId(chatroomId)
                    .build();
            notificationClient.sendMentionNotification(notificationRequest);
        }

        // 참여자 정보 생성
        List<Participant> participants = chatRoom.getParticipants().stream()
                .map(participantId -> {
                    MemberResponse member = findMemberById(participantId);
                    return Participant.builder()
                            .userId(participantId)
                            .nickname(member.getNickname())
                            .build();
                }).collect(Collectors.toList());
        return ChatMessageRes.builder()
                .messageId(chat.getId())
                .chatRoomId(chatRoom.getId())
                .sender(Sender.builder()
                        .userId(sender.getId())
                        .username(sender.getNickname())
                        .build())
                .message(chat.getMessage())
                .attachments(chat.getAttachments())
                .timestamp(chat.getTimestamp())
                .status("sent")
                .build();
    }
    public List<UUID> extractMentionedUserIds(String message, List<MemberResponse> allMembers){
        Pattern mentionPattern = Pattern.compile("@(\\\\w+)");
        Matcher matcher = mentionPattern.matcher(message);
        List<UUID> mentionedUserIds = new ArrayList<>();
        while(matcher.find()){
            String username = matcher.group(1); // @ 두의 닉네임 추출
            allMembers.stream()
                    .filter(member-> member.getNickname().equals(username))
                    .findFirst()
                    .ifPresent(member->mentionedUserIds.add(member.getId()));
        }
        return mentionedUserIds;
    }

    @Transactional
    public void leaveChatRoom(Long chatRoomId, UUID userId) {
        // Hibernate의 플러시 모드를 ALWAYS로 설정
        entityManager.unwrap(Session.class).setHibernateFlushMode(FlushMode.ALWAYS);

        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));

        if (chatRoom.getParticipants().size() == 2 && chatRoom.getParticipants().contains(userId)) {
            throw new InvalidChatException(BaseResponseStatus.CHAT_CANNOT_LEAVE_ONE_TO_ONE);
        }

        // 참여자 목록 수정
        List<UUID> updatedParticipants = new ArrayList<>(chatRoom.getParticipants());
        boolean removed = updatedParticipants.remove(userId);
        if (!removed) {
            throw new InvalidChatException(BaseResponseStatus.CHAT_ROOM_USER_NOT);
        }
        chatRoom.setParticipants(updatedParticipants);

        // 저장 및 강제 동기화
        chatRoomRepository.save(chatRoom);
    }
}

