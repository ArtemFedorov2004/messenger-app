import React from 'react';
import {Col} from "antd";
import RightColumnHeader from "./RightColumnHeader";
import NoMessagesPlaceholder from "../../message/NoMessagesPlaceholder";
import NewPrivateChatFooter from "./PrivateChatFooter/NewPrivateChatFooter";

const NewPrivateChat = () => {
    return (
        <Col
            span={18}
            className="messages-layout"
        >
            <RightColumnHeader/>
            <NoMessagesPlaceholder/>
            <NewPrivateChatFooter/>
        </Col>
    );
};

export default NewPrivateChat;