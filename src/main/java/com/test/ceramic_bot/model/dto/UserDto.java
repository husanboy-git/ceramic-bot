package com.test.ceramic_bot.model.dto;

import com.test.ceramic_bot.model.Role;
import com.test.ceramic_bot.model.entity.UserEntity;

public record UserDto(
        Long telegramId, String name, Role role
) {
   public static UserDto from(UserEntity userEntity) {
       return new UserDto(
               userEntity.getTelegramId(),
               userEntity.getName(),
               userEntity.getRole()
       );
   }
}
