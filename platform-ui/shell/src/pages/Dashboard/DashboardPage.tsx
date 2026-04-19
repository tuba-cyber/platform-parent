import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/authStore";
import axios from "axios";

// ─── Yardımcı ─────────────────────────────────────────────────────────────────

const getToken = () => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.token || null;
  } catch { return null; }
};

const getCompanyId = () => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.user?.companyId || "";
  } catch { return ""; }
};

const coApi = axios.create({ baseURL: "http://localhost:8084/api" });
const hrApi = axios.create({ baseURL: "http://localhost:8085/api" });

[coApi, hrApi].forEach((a) =>
  a.interceptors.request.use((cfg) => {
    const t = getToken();
    if (t) cfg.headers.Authorization = `Bearer ${t}`;
    return cfg;
  })
);

// ─── İstatistik Kartı ─────────────────────────────────────────────────────────

interface StatCardProps {
  label: string;
  value: number | string;
  color: string;
  icon: React.ReactNode;
  loading: boolean;
}

const StatCard: React.FC<StatCardProps> = ({ label, value, color, icon, loading }) => (
  <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 flex items-center gap-4">
    <div className={`${color} rounded-xl w-12 h-12 flex items-center justify-center text-white`}>
      {icon}
    </div>
    <div>
      <p className="text-sm text-gray-500">{label}</p>
      {loading ? (
        <div className="mt-1 h-7 w-12 bg-gray-100 animate-pulse rounded" />
      ) : (
        <p className="text-2xl font-bold text-gray-900">{value}</p>
      )}
    </div>
  </div>
);

// ─── Ana Bileşen ──────────────────────────────────────────────────────────────

const DashboardPage: React.FC = () => {
  const user = useAuthStore((s) => s.user);
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    companies: 0,
    employees: 0,
    departments: 0,
    positions: 0,
  });

  useEffect(() => {
    const companyId = getCompanyId();
    const promises: Promise<any>[] = [
      coApi.get("/companies").catch(() => ({ data: { data: [] } })),
    ];

    if (companyId) {
      promises.push(
        hrApi.get(`/employees/company/${companyId}`).catch(() => ({ data: { data: [] } })),
        hrApi.get(`/departments/company/${companyId}`).catch(() => ({ data: { data: [] } })),
        hrApi.get(`/positions/company/${companyId}`).catch(() => ({ data: { data: [] } })),
      );
    }

    Promise.all(promises)
      .then(([coRes, empRes, deptRes, posRes]) => {
        setStats({
          companies:   (coRes.data.data   || []).length,
          employees:   (empRes?.data.data   || []).length,
          departments: (deptRes?.data.data  || []).length,
          positions:   (posRes?.data.data   || []).length,
        });
      })
      .finally(() => setLoading(false));
  }, []);

  const statCards = [
    {
      label: "Şirketler",
      value: stats.companies,
      color: "bg-blue-500",
      icon: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
      ),
    },
    {
      label: "Çalışanlar",
      value: stats.employees,
      color: "bg-green-500",
      icon: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
      ),
    },
    {
      label: "Departmanlar",
      value: stats.departments,
      color: "bg-purple-500",
      icon: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
        </svg>
      ),
    },
    {
      label: "Pozisyonlar",
      value: stats.positions,
      color: "bg-orange-500",
      icon: (
        <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
            d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      ),
    },
  ];

  const quickLinks = [
    { label: "Şirket Ekle",      path: "/companies/new",       emoji: "🏢", color: "hover:border-blue-300 hover:bg-blue-50 hover:text-blue-700" },
    { label: "Çalışan Ekle",     path: "/hr/employees/new",    emoji: "👤", color: "hover:border-green-300 hover:bg-green-50 hover:text-green-700" },
    { label: "Departman Yönet",  path: "/hr/departments",      emoji: "🗂️", color: "hover:border-purple-300 hover:bg-purple-50 hover:text-purple-700" },
    { label: "Pozisyon Yönet",   path: "/hr/positions",        emoji: "📋", color: "hover:border-orange-300 hover:bg-orange-50 hover:text-orange-700" },
    { label: "Şirketleri Gör",   path: "/companies",           emoji: "🔍", color: "hover:border-gray-300 hover:bg-gray-50 hover:text-gray-700" },
    { label: "Çalışanları Gör",  path: "/hr/employees",        emoji: "👥", color: "hover:border-gray-300 hover:bg-gray-50 hover:text-gray-700" },
  ];

  return (
    <div>
      {/* Başlık */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">
          Hoş geldiniz, {user?.username} 👋
        </h1>
        <p className="text-gray-500 mt-1">Platform yönetim paneline hoş geldiniz.</p>
      </div>

      {/* İstatistik Kartları */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-5 mb-8">
        {statCards.map((card) => (
          <StatCard key={card.label} {...card} loading={loading} />
        ))}
      </div>

      {/* Hızlı Erişim */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Hızlı Erişim</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
          {quickLinks.map((item) => (
            <button
              key={item.path}
              onClick={() => navigate(item.path)}
              className={`flex items-center gap-2 px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg text-sm font-medium text-gray-700 transition ${item.color}`}
            >
              <span className="text-base">{item.emoji}</span>
              {item.label}
            </button>
          ))}
        </div>
      </div>

      {/* Sistem Durumu */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Sistem Durumu</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {[
            { name: "platform-gateway", port: 8080, desc: "API Gateway" },
            { name: "platform-co", port: 8084, desc: "Şirket Servisi" },
            { name: "platform-hr", port: 8085, desc: "İK Servisi" },
            { name: "platform-ui (shell)", port: 3000, desc: "Frontend Shell" },
          ].map((svc) => (
            <div key={svc.name} className="flex items-center gap-3 px-4 py-3 bg-gray-50 rounded-lg">
              <div className="w-2 h-2 rounded-full bg-green-400 animate-pulse" />
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-800 truncate">{svc.name}</p>
                <p className="text-xs text-gray-500">{svc.desc} · :{svc.port}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
