import React from 'react';
import {Layout} from "antd";
import './App.css';
import Home from "./pages/Home";

const App = () => {

    return (
        <Layout className="h100">
            <Home/>
        </Layout>
    );
};

export default App;