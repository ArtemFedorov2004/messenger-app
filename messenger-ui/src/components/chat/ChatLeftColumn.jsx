import React from 'react';
import {Col} from "antd";
import ChatList from "./ChatList";
import LeftColumnHeader from "./LeftColumnHeader";

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