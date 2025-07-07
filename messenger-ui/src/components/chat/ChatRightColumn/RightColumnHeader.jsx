import {observer} from "mobx-react-lite";
import React, {useContext} from "react";
import {Context} from "../../../index";
import {Avatar} from "antd";
import userLogo from "../../../assets/user.png";
import './RightColumnHeader.css';

const RightColumnHeader = observer(() => {
    const {chat} = useContext(Context);

    return (
        <div className="right-column-header">
            <Avatar src={userLogo}/>
            <div className="info">
                <h3 className="username">{chat.selectedChat.participantName}</h3>
            </div>
        </div>
    );
});
export default RightColumnHeader;