import React from 'react';
import {rules} from "../../utils/rules";
import {Form, Input} from "antd";
import {LockOutlined} from "@ant-design/icons";

const PasswordInput = ({password, setPassword}) => {
    return (
        <Form.Item
            name="password"
            rules={[rules.required("Пожалуйста введите пароль")]}
        >
            <Input
                prefix={<LockOutlined/>}
                type="password"
                placeholder="Пароль"
                value={password}
                onChange={e => setPassword(e.target.value)}
            />
        </Form.Item>
    );
};

export default PasswordInput;