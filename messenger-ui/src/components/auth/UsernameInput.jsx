import React from 'react';
import {rules} from "../../utils/rules";
import {Form, Input} from "antd";
import {UserOutlined} from "@ant-design/icons";

const UsernameInput = ({username, setUsername}) => {
    return (
        <Form.Item
            name="username"
            rules={[rules.required("Пожалуйста введите имя пользователя")]}
        >
            <Input
                prefix={<UserOutlined/>}
                placeholder="Имя пользователя"
                value={username}
                onChange={e => setUsername(e.target.value)}
            />
        </Form.Item>
    );
};

export default UsernameInput;