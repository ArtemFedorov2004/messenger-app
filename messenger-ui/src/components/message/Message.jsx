import React, {useContext} from 'react';
import {Dropdown} from "antd";
import MessageContent from "./MessageContent";
import ChatService from "../../service/ChatService";
import {observer} from "mobx-react-lite";
import {Context} from "../../index";


const Message = observer(({message}) => {
    const {chat} = useContext(Context);

    const menuItems = [
        {
            label: 'Удалить',
            key: '1',
            onClick: () => {
                ChatService.deleteMessage(message.chatId, message.id).then(() => {
                    chat.deleteMessage(message)
                })
            },
        },
        {
            label: 'Редактировать',
            key: '2',
            onClick: () => {
                chat.updateNewMessage({...message})
            },
        }
    ];

    return (
        message.senderId === '1'
            ?
            <Dropdown
                menu={{items: menuItems}}
                trigger={['contextMenu']}
                className="message-context-menu"
            >
                <div>
                    <MessageContent message={message}/>
                </div>
            </Dropdown>
            :
            <MessageContent message={message}/>
    )
});

export default Message;