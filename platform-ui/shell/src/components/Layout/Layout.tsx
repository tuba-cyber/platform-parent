import React, { Suspense } from "react";
import { Outlet, Navigate } from "react-router-dom";
import Sidebar from "../Sidebar/Sidebar";
import { useAuthStore } from "../../store/authStore";

const LoadingSpinner: React.FC = () => (
  <div className="flex items-center justify-center h-full min-h-64">
    <div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-600 border-t-transparent" />
  </div>
);

const Layout: React.FC = () => {
  const { isAuthenticated, token } = useAuthStore((s) => ({
    isAuthenticated: s.isAuthenticated,
    token: s.token,
  }));

  if (!isAuthenticated || !token) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 ml-64 overflow-y-auto">
        <div className="p-6">
          <Suspense fallback={<LoadingSpinner />}>
            <Outlet />
          </Suspense>
        </div>
      </main>
    </div>
  );
};

export default Layout;
