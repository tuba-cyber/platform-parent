import React from "react";
import { Routes, Route, NavLink } from "react-router-dom";
import EmployeeList from "./components/EmployeeList/EmployeeList";
import EmployeeForm from "./components/EmployeeForm/EmployeeForm";
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
        <Route path="departments"    element={<DepartmentPage />} />
        <Route path="positions"      element={<PositionPage />} />
      </Routes>
    </div>
  );
};

// Basit placeholder sayfalar (geliştirme aşamasında)
const DepartmentPage = () => (
  <div className="bg-white rounded-xl border border-gray-100 p-8 text-center text-gray-400 shadow-sm">
    <div className="text-4xl mb-3">🗂️</div>
    <p className="font-medium text-gray-600">Departman Yönetimi</p>
    <p className="text-sm mt-1">Yakında eklenecek</p>
  </div>
);

const PositionPage = () => (
  <div className="bg-white rounded-xl border border-gray-100 p-8 text-center text-gray-400 shadow-sm">
    <div className="text-4xl mb-3">📋</div>
    <p className="font-medium text-gray-600">Pozisyon Yönetimi</p>
    <p className="text-sm mt-1">Yakında eklenecek</p>
  </div>
);

export default HrRoutes;
