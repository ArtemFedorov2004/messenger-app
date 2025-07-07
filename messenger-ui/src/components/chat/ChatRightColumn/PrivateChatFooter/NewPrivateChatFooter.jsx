import {observer} from "mobx-react-lite";
import React, {useContext} from "react";
import {Context} from "../../../../index";
import MessageService from "../../../../service/MessageService";
import PrivateChatService from "../../../../service/PrivateChatService";
import PrivateChatFooter from "./PrivateChatFooter";

const NewPrivateChatFooter = observer(() => {
    const {chat} = useContext(Context);

    const sendMessage = async (chatId) => {
        const {content} = chat.selectedChat.newMessage;
        if (!content.trim()) return null;

        try {
            const response = await MessageService.createMessage(chatId, content);
            return response.data;
        } catch (error) {
            console.error(error);
            return null;
        }
    };

    const createChatAndSendMessage = async () => {
        const {selectedChat} = chat;
        const {newMessage, participantName} = selectedChat;
        const messageContent = newMessage.content.trim();

        if (!messageContent) return;

        try {
            const chatResponse = await PrivateChatService.createPrivateChat(participantName);
            const newChat = chatResponse.data;

            const message = await sendMessage(newChat.id);
            if (!message) return;

            chat.addCreatedChat({
                ...newChat,
                lastMessage: message
            });
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <PrivateChatFooter onSend={createChatAndSendMessage}/>
    );
});

export default NewPrivateChatFooter;