import {$authApi} from "../http";

export default class ChatService {

    static async getChats() {
        return $authApi.get("/chats")
    }

    static async createMessage(chatId, content) {
        return $authApi.post(`/chats/${chatId}/messages`, {content});
    }

    static async editMessage(chatId, id, newContent) {
        return $authApi.patch(`/chats/${chatId}/messages/${id}`, {content: newContent});
    }

    static async deleteMessage(chatId, id) {
        return $authApi.delete(`/chats/${chatId}/messages/${id}`);
    }
}