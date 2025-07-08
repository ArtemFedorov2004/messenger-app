import React from 'react';
import {SearchOutlined} from "@ant-design/icons";
import {Input} from "antd";

const UsersSearchInput = ({searchQuery, setSearchQuery}) => {
    return (
        <Input
            size="large"
            placeholder="Введите имя пользователя или email..."
            prefix={<SearchOutlined/>}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            allowClear
        />
    );
};

export default UsersSearchInput;