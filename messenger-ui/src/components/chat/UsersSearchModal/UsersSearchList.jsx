import React from 'react';
import SearchUserItem from "./SearchUserItem";
import {List} from "antd";

const UsersSearchList = ({loading, users, pagination}) => {
    return (
        <List
            itemLayout="horizontal"
            loading={loading}
            dataSource={users}
            renderItem={(user) => <SearchUserItem userItem={user}/>}
            locale={{
                emptyText: 'Пользователи не найдены'
            }}
            pagination={pagination}
        />
    );
};

export default UsersSearchList;