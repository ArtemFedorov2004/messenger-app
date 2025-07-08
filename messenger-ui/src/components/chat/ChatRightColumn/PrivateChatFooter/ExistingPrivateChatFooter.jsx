import {observer} from "mobx-react-lite";
import React, {useContext} from "react";
import {Context} from "../../../../index";
import MessageService from "../../../../service/MessageService";
import PrivateChatFooter from "./PrivateChatFooter";

const ExistingPrivateChatFooter = observer(() => {
    const {chat} = useContext(Context);

    const sendMessage = async () => {
        const {selectedChat} = chat;
        const {newMessage} = selectedChat;
        const messageContent = newMessage.content.trim();

        if (!messageContent) return;

        try {
            let response;

            if (newMessage.id) {
                response = await MessageService.updateMessage(newMessage);
                chat.editMessage(response.data);
            } else {
                response = await MessageService.createMessage(selectedChat.id, messageContent);
                chat.addMessage(response.data);
            }

            chat.updateNewMessage(chat.selectedChat.participantName, {content: ''});
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <PrivateChatFooter onSend={sendMessage}/>
    );
});

export default ExistingPrivateChatFooter;