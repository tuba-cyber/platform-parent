import React from "react";
import { Routes, Route } from "react-router-dom";
import CompanyList from "./components/CompanyList/CompanyList";
import CompanyForm from "./components/CompanyForm/CompanyForm";
import "./index.css";

// Shell'e expose edilen giriş noktası
const CompanyRoutes: React.FC = () => {
  return (
    <Routes>
      <Route index element={<CompanyList />} />
      <Route path="new"  element={<CompanyForm />} />
      <Route path=":id"  element={<CompanyForm />} />
    </Routes>
  );
};

export default CompanyRoutes;
