package com.test.ceramic_bot.bot;

import com.test.ceramic_bot.model.Role;
import com.test.ceramic_bot.model.dto.UserDto;
import com.test.ceramic_bot.service.ProductService;
import com.test.ceramic_bot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Component
public class CeramicBot extends TelegramLongPollingBot {
    @Autowired private UserService userService;
    @Autowired private ProductService productService;

    private static final String ADMIN_PASSWORD = "Java2023";  // 실제 사용할 비밀번호 설정


    @Value("${telegram.bot.useranme}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            Long telegramId = update.getMessage().getFrom().getId();
            String userName = update.getMessage().getFrom().getFirstName();
            Optional<UserDto> existingUser = userService.getUserByTelegramId(telegramId);

            if(messageText.equals("/start")) {
                if(existingUser.isPresent()) {
                    sendMessage(chatId, "다시 오신 것을 환영합니다, " + userName + "님! \uD83D\uDC4B");
                } else {
                    userService.addUser(telegramId, userName, Role.ROLE_USER);
                    sendMessage(chatId, "환영합니다, " + userName + "님! 초음 오셨군요. \uD83D\uDC4B");
                }
            } else if (messageText.startsWith("/add_admin")) {
                handleAddAdminCommand(chatId, messageText, userName);
            } else if (messageText.startsWith("/remove_admin")) {
                handleRemoveAdminCommand(chatId, messageText, userName);
            } else {
                sendMessage(chatId, "잘못된 명령어입니다. 다시 시도해 주세요!");
            }
        }
    }

    // add admin method
    private void handleAddAdminCommand(Long chatId, String messageText, String userName) {
        // /add_admin 며령어에서 비밀번호 분리
        String[] parts = messageText.split(" ");
        if(parts.length < 2) {
            sendMessage(chatId, "관리자로 추가되려면 /add_admin <비밀번호> 형식으로 입력해 주세요.");
            return;
        }

        String inputPassword = parts[1];

        if(!ADMIN_PASSWORD.equals(inputPassword)) {
            sendMessage(chatId, "잘못된 비밀번호 입니다. 관리자 권한을 얻을 수 없습니다.");
            return;
        }

        try {
            // 관리자를 추가하는 로직 호출
            userService.addAdmin(chatId, userName);
            sendMessage(chatId, "축하합니다! " + userName + "님! " + "관리로 승급하셨습니다 \uD83C\uDF89");
        } catch (IllegalArgumentException e) {
            sendMessage(chatId, e.getMessage());
        }
    }

    // remove admin method
    private void handleRemoveAdminCommand(Long chatId, String messageText, String userName) {
        // /remove_admin 며령어에서 비밀번호 분리
        String[] parts = messageText.split(" ");
        if(parts.length < 2) {
            sendMessage(chatId, "관리자로 제거하려면 /remove_admin <비밀번호> 형식으로 입력해 주세요.");
            return;
        }

        String inputPassword = parts[1];

        if(!ADMIN_PASSWORD.equals(inputPassword)) {
            sendMessage(chatId, "잘못된 비밀번호 입니다. 관리자 권한을 얻을 수 없습니다.");
            return;
        }

        try {
            // 관리자를 제거하는 로직 호출
            userService.removeAdmin(chatId);
            sendMessage(chatId, "관리자 권한이 성공적으로 제거되었습니다.");
        } catch (IllegalArgumentException e) {
            sendMessage(chatId, e.getMessage());
        }
    }

    // send message
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        executeTryCatch(message);
    }

    // execute message
    private void executeTryCatch(SendMessage message) {
        try {
            execute(message);
        }catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
