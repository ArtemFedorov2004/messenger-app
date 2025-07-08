import React from 'react';
import {rules} from "../../utils/rules";
import {Form, Input} from "antd";
import {MailOutlined} from "@ant-design/icons";

const EmailInput = ({email, setEmail, errors, setErrors}) => {
    return (
        <Form.Item
            name="email"
            validateStatus={errors ? 'error' : ''}
            help={errors}
            rules={[
                rules.required("Пожалуйста введите почту"),
                rules.email('Некорректный email')
            ]}
        >
            <Input
                prefix={<MailOutlined/>}
                type="email"
                placeholder="Почта"
                value={email}
                onChange={e => {
                    setEmail(e.target.value)
                    if (errors) {
                        setErrors(null);
                    }
                }}
            />
        </Form.Item>
    );
};

export default EmailInput;