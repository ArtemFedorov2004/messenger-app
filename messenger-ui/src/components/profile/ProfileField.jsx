import React from 'react';
import { Typography } from 'antd';

const { Text } = Typography;

const ProfileField = ({ icon, label, value, strong = true }) => {
    return (
        <div style={{ marginBottom: '16px' }}>
            <Text strong={strong} style={{ fontSize: '16px' }}>
                {React.cloneElement(icon, { style: { marginRight: '8px' } })}
                {label}:
            </Text>
            <Text style={{ marginLeft: '8px' }}>{value}</Text>
        </div>
    );
};

export default ProfileField;