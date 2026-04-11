import React from "react";
import { useAuthStore } from "../../store/authStore";

const cards = [
  { label: "Şirketler",   value: "—", color: "bg-blue-500",   icon: "🏢" },
  { label: "Çalışanlar",  value: "—", color: "bg-green-500",  icon: "👥" },
  { label: "Departmanlar",value: "—", color: "bg-purple-500", icon: "🗂️" },
  { label: "Pozisyonlar", value: "—", color: "bg-orange-500", icon: "📋" },
];

const DashboardPage: React.FC = () => {
  const user = useAuthStore((s) => s.user);

  return (
    <div>
      {/* Başlık */}
      <div className="mb-8">
        <h1 className="text-2xl font-bold text-gray-900">
          Hoş geldiniz, {user?.username} 👋
        </h1>
        <p className="text-gray-500 mt-1">Platform yönetim paneline hoş geldiniz.</p>
      </div>

      {/* Özet Kartlar */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-5 mb-8">
        {cards.map((card) => (
          <div key={card.label} className="bg-white rounded-xl shadow-sm border border-gray-100 p-5 flex items-center gap-4">
            <div className={`${card.color} rounded-xl w-12 h-12 flex items-center justify-center text-2xl`}>
              {card.icon}
            </div>
            <div>
              <p className="text-sm text-gray-500">{card.label}</p>
              <p className="text-2xl font-bold text-gray-900">{card.value}</p>
            </div>
          </div>
        ))}
      </div>

      {/* Hızlı Erişim */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Hızlı Erişim</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
          {[
            { label: "Şirket Ekle",    path: "/companies/new",    emoji: "➕" },
            { label: "Çalışan Ekle",   path: "/hr/employees/new", emoji: "👤" },
            { label: "Departman Ekle", path: "/hr/departments",   emoji: "🗂️" },
          ].map((item) => (
            <a
              key={item.path}
              href={item.path}
              className="flex items-center gap-2 px-4 py-3 bg-gray-50 hover:bg-primary-50 border border-gray-200 hover:border-primary-300 rounded-lg text-sm font-medium text-gray-700 hover:text-primary-700 transition"
            >
              <span>{item.emoji}</span>
              {item.label}
            </a>
          ))}
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
