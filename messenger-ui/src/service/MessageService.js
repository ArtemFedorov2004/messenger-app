import {$authApi} from "../http";

export default class MessageService {

    static async fetchMessages(chatId, page = 0, size = 10) {
        return $authApi.get(`/private-chats/${chatId}/messages?page=${page}&size=${size}`)
    }

    static async updateMessage(updatedMessage) {
        return $authApi.patch(`/private-chat-messages/${updatedMessage.id}`, updatedMessage)
    }

    static async createMessage(chatId, content) {
        return $authApi.post(`/private-chats/${chatId}/messages`, {content: content})
    }

    static async deleteMessage(messageId) {
        return $authApi.delete(`/private-chat-messages/${messageId}`)
    }
}