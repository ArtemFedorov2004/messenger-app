import React from 'react';
import {Avatar, List} from "antd";
import userLogo from "../../../assets/user.png";

const SearchUserItem = ({user}) => {
    return (
        <List.Item>
            <List.Item.Meta
                avatar={<Avatar src={userLogo}/>}
                title={user.username}
                description={user.email}
            />
        </List.Item>
    );
};

export default SearchUserItem;