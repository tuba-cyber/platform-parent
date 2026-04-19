import React from "react";
import { Routes, Route, NavLink } from "react-router-dom";
import EmployeeList from "./components/EmployeeList/EmployeeList";
import EmployeeForm from "./components/EmployeeForm/EmployeeForm";
import DepartmentList from "./components/DepartmentList/DepartmentList";
import PositionList from "./components/PositionList/PositionList";
import "./index.css";

// HR modülü sekme navigasyonu
const HrNav: React.FC = () => (
  <div className="flex gap-1 mb-6 border-b border-gray-200 pb-0">
    {[
      { to: "/hr/employees",   label: "👥 Çalışanlar" },
      { to: "/hr/departments", label: "🗂️ Departmanlar" },
      { to: "/hr/positions",   label: "📋 Pozisyonlar" },
    ].map((item) => (
      <NavLink
        key={item.to}
        to={item.to}
        className={({ isActive }) =>
          `px-4 py-2 text-sm font-medium rounded-t-lg border-b-2 transition ${
            isActive
              ? "border-primary-600 text-primary-700 bg-primary-50"
              : "border-transparent text-gray-500 hover:text-gray-700"
          }`
        }
      >
        {item.label}
      </NavLink>
    ))}
  </div>
);

// Shell'e expose edilen giriş noktası
const HrRoutes: React.FC = () => {
  return (
    <div>
      <HrNav />
      <Routes>
        <Route index element={<EmployeeList />} />
        <Route path="employees"      element={<EmployeeList />} />
        <Route path="employees/new"  element={<EmployeeForm />} />
        <Route path="employees/:id"  element={<EmployeeForm />} />
        <Route path="departments"    element={<DepartmentList />} />
        <Route path="positions"      element={<PositionList />} />
      </Routes>
    </div>
  );
};

export default HrRoutes;
