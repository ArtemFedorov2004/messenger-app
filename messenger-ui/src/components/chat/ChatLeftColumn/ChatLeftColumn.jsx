import React from 'react';
import {Col} from "antd";
import LeftColumnHeader from "./LeftColumnHeader";
import ChatList from "./ChatList/ChatList";

const ChatLeftColumn = () => {
    return (
        <Col
            span={6}
            className="messages-layout"
        >
            <LeftColumnHeader/>
            <ChatList/>
        </Col>
    );
};

export default ChatLeftColumn;