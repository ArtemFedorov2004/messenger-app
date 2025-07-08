import {$authApi} from "../http";

export default class PrivateChatService {

    static async getPrivateChats() {
        return $authApi.get("/private-chats")
    }

    static async createPrivateChat(participantName) {
        return $authApi.post(`/private-chats`, {participantName: participantName})
    }
}