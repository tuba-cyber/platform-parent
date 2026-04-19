import React, { lazy, Suspense } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Layout from "./components/Layout/Layout";
import LoginPage from "./pages/Login/LoginPage";
import DashboardPage from "./pages/Dashboard/DashboardPage";
import ModuleAdminPage from "./pages/Admin/ModuleAdminPage";
import ScreenBuilderPage from "./pages/Admin/ScreenBuilderPage";

// Module Federation — remote modüller lazy load edilir
const CompanyRoutes = lazy(() => import("platformCo/CompanyRoutes"));
const HrRoutes      = lazy(() => import("platformHr/HrRoutes"));

const Fallback = () => (
  <div className="flex items-center justify-center h-64">
    <div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-600 border-t-transparent" />
  </div>
);

const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route path="/login" element={<LoginPage />} />

        {/* Screen Builder — tam ekran, layout dışında */}
        <Route path="/admin/builder/:moduleId/:screenId" element={<ScreenBuilderPage />} />

        {/* Protected */}
        <Route element={<Layout />}>
          <Route index element={<DashboardPage />} />

          {/* Remote: platform-co-ui */}
          <Route
            path="companies/*"
            element={
              <Suspense fallback={<Fallback />}>
                <CompanyRoutes />
              </Suspense>
            }
          />

          {/* Remote: platform-hr-ui */}
          <Route
            path="hr/*"
            element={
              <Suspense fallback={<Fallback />}>
                <HrRoutes />
              </Suspense>
            }
          />

          {/* Admin: Modül Yönetimi */}
          <Route path="admin/modules" element={<ModuleAdminPage />} />
        </Route>

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
