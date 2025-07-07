import {observer} from "mobx-react-lite";
import React, {useContext} from "react";
import {Context} from "../../index";
import {Dropdown} from "antd";
import MessageService from "../../service/MessageService";
import MessageContent from "./MessageContent";

const Message = observer(({message}) => {
    const {chat, user} = useContext(Context);

    const menuItems = [
        {
            label: 'Удалить',
            key: '1',
            onClick: async () => {
                try {
                    await MessageService.deleteMessage(message.id);
                    chat.deleteMessageAndUpdateLastMessage(message.id);
                } catch (error) {
                    console.error(error);
                }
            },
        },
        {
            label: 'Редактировать',
            key: '2',
            onClick: () => {
                chat.updateNewMessage(chat.selectedChat.participantName, message);
            },
        }
    ];

    return message.senderName === user.user.username ? (
        <Dropdown
            menu={{items: menuItems}}
            trigger={['contextMenu']}
            className="message-context-menu"
        >
            <div>
                <MessageContent message={message}/>
            </div>
        </Dropdown>
    ) : (
        <MessageContent message={message}/>
    );
});

export default Message;