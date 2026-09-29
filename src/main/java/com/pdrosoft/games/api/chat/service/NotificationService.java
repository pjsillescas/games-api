package com.pdrosoft.games.api.chat.service;

import com.pdrosoft.games.api.chat.dto.NotificationDTO;

public interface NotificationService {
	void sendNotification(String roomId, NotificationDTO notification);
}
