import React from 'react';
import {rules} from "../../utils/rules";
import {Form, Input} from "antd";
import {MailOutlined} from "@ant-design/icons";

const EmailInput = ({email, setEmail}) => {
    return (
        <Form.Item
            name="email"
            rules={[rules.required("Пожалуйста введите почту")]}
        >
            <Input
                prefix={<MailOutlined/>}
                type="email"
                placeholder="Почта"
                value={email}
                onChange={e => setEmail(e.target.value)}
            />
        </Form.Item>
    );
};

export default EmailInput;