import React, {useContext, useMemo} from 'react';
import {Col} from "antd";
import RightColumnHeader from "./RightColumnHeader";
import MessageList from "../message/MessageList";
import RightColumnFooter from "./RightColumnFooter";
import {Context} from "../../index";
import {observer} from "mobx-react-lite";

const ChatRightColumn = observer(() => {
    const {chat} = useContext(Context);

    const selectedChat = useMemo(() => {
        return chat.chats.find((c) => c.id === chat.selectedChatId);
    }, [chat.selectedChatId, chat.chats])

    return (
        selectedChat ? (
            <Col
                span={18}
                className="messages-layout"
            >
                <RightColumnHeader/>
                <MessageList/>
                <RightColumnFooter/>
            </Col>
        ) : null
    );
});

export default ChatRightColumn;