import React from 'react';
import {rules} from "../../utils/rules";
import {Form, Input} from "antd";
import {UserOutlined} from "@ant-design/icons";

const UsernameInput = ({username, setUsername, errors, setErrors}) => {
    return (
        <Form.Item
            name="username"
            validateStatus={errors ? 'error' : ''}
            help={errors}
            rules={[rules.required("Пожалуйста введите имя пользователя")]}
        >
            <Input
                prefix={<UserOutlined/>}
                placeholder="Имя пользователя"
                value={username}
                onChange={e => {
                    setUsername(e.target.value)
                    if (errors) {
                        setErrors(null);
                    }
                }}
            />
        </Form.Item>
    );
};

export default UsernameInput;