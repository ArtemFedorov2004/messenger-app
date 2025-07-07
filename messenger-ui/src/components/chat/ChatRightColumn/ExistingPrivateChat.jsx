import React from 'react';
import {Col} from "antd";
import RightColumnHeader from "./RightColumnHeader";
import ExistingPrivateChatFooter from "./PrivateChatFooter/ExistingPrivateChatFooter";
import MessageList from "../../message/MessageList";


const ExistingPrivateChat = () => {
    return (
        <Col
            span={18}
            className="messages-layout"
        >
            <RightColumnHeader/>
            <MessageList/>
            <ExistingPrivateChatFooter/>
        </Col>
    );
};

export default ExistingPrivateChat;